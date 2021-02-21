package net.sayaya.document.modeler.sample;

import net.sayaya.document.data.Sample;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import org.springframework.util.FastByteArrayOutputStream;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.ByteBuffer;

@Service
public class SampleHandler {
	private final SampleRepository repo;

	public SampleHandler(SampleRepository repo) {this.repo = repo;}

	public Flux<Sample> upload(String model, Flux<FilePart> files) {
		return files.flatMap(part->this.toEntity(model, part))
				.flatMap(repo::save)
				.map(SampleToDTO::map);
	}
	private Mono<net.sayaya.document.modeler.sample.Sample> toEntity(String model, FilePart part) {
		return DataBufferUtils.join(part.content()).map(d->d.asByteBuffer().array())
				.map(content-> new net.sayaya.document.modeler.sample.Sample().model(model)
						.name(part.filename())
						.data(ByteBuffer.wrap(content))
						.size(content.length));
	}
}
