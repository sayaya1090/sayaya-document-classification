package net.sayaya.document.modeler.sample;

import net.sayaya.document.data.Model;
import net.sayaya.document.data.Sample;
import net.sayaya.document.modeler.model.ModelToDTO;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

@Service
public class SampleHandler {
	private final SampleRepository repo;

	public SampleHandler(SampleRepository repo) {this.repo = repo;}

	public Flux<Sample> list(String model) {
		return repo.findByModel(model).map(SampleToDTO::map);
	}
	public Flux<Sample> upload(String model, Flux<FilePart> files) {
		return files.flatMap(part->this.toEntity(model, part))
				.flatMap(repo::save)
				.map(SampleToDTO::map);
	}
	private Mono<net.sayaya.document.modeler.sample.Sample> toEntity(String model, FilePart part) {
		UUID id = UUID.randomUUID();
		Path dir = Path.of(model);
		try {
			Files.createDirectories(dir);
		} catch(IOException e) {
			throw new RuntimeException("Can't create directory:" + model);
		}
		Path tmp = dir.resolve(id.toString());
		return part.transferTo(tmp).then(Mono.just(new net.sayaya.document.modeler.sample.Sample().id(id).model(model).name(part.filename())));
	}
}
