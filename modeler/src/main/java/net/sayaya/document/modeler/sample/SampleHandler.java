package net.sayaya.document.modeler.sample;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.sayaya.document.data.Sample;
import net.sayaya.document.data.SampleMessage;
import org.springframework.context.annotation.Bean;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Supplier;

@Service
public class SampleHandler {
	private final SampleRepository repo;
	private final ObjectMapper OM;
	private final Sinks.Many<SampleMessage> publisher = Sinks.many().multicast().directAllOrNothing();
	private final Sinks.Many<SampleMessage> subscriber = Sinks.many().multicast().directBestEffort();
	public SampleHandler(SampleRepository repo, ObjectMapper om) {this.repo = repo;
		OM = om;
	}

	public Flux<Sample> list(String model) {
		return repo.findByModel(model).map(SampleToDTO::map);
	}
	public Flux<SampleMessage> upload(String model, Flux<FilePart> files) {
		return files.flatMap(part->this.toEntity(model, part))
				.flatMap(repo::save)
				.map(SampleToDTO::map)
				.map(data-> SampleMessage.builder().type(SampleMessage.MessageType.CREATE).data(data).build())
				.doOnNext(publisher::tryEmitNext);
	}
	private Mono<net.sayaya.document.modeler.sample.Sample> toEntity(String model, FilePart part) {
		UUID id = UUID.randomUUID();
		Path dir = Path.of(model);
		try {
			if(!dir.toFile().exists()) Files.createDirectories(dir);
		} catch(IOException e) {
			throw new RuntimeException("Can't create directory:" + model);
		}
		Path tmp = dir.resolve(id.toString());
		return part.transferTo(tmp).then(Mono.just(new net.sayaya.document.modeler.sample.Sample().id(id).model(model).name(part.filename())));
	}
	public Mono<SampleMessage> remove(String model, String id) {
		return repo.deleteByModelAndId(model, UUID.fromString(id))
				.then(Mono.just(new Sample().model(model).id(id)))
				.map(data->SampleMessage.builder().type(SampleMessage.MessageType.DELETE).data(data).build())
				.doOnSuccess(publisher::tryEmitNext);
	}
	public Flux<SampleMessage> subscribe(String model) {
		return subscriber.asFlux().filter(s->model.equals(s.data().model()));
	}
	private String map(SampleMessage dto) {
		try {
			return OM.writeValueAsString(dto);
		} catch(JsonProcessingException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}
	private SampleMessage map(String json) {
		try {
			return OM.readValue(json, SampleMessage.class);
		} catch(JsonProcessingException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}
	@Bean("publish-sample")
	public Supplier<Flux<String>> publishSample() {
		return ()->publisher.asFlux().map(this::map);
	}
	@Bean("broadcast-sample")
	public Consumer<String> broadcastSample() {
		return c->subscriber.tryEmitNext(map(c));
	}
	@Bean("analyze-sample")
	public Consumer<String> analyzeSample() {
		return c->{
			SampleMessage msg = map(c);
			if(msg.type() != SampleMessage.MessageType.CREATE) return;
			Sample info = msg.data();
			publisher.tryEmitNext(SampleMessage.builder().type(SampleMessage.MessageType.PROCESSING).data(info).build());
			Path tmp = Path.of(info.model()).resolve(info.id());
			repo.findByModelAndId(info.model(), UUID.fromString(info.id()))
					.map(e->process(e, tmp))
					.flatMap(repo::save)
					.map(SampleToDTO::map)
					.map(data->SampleMessage.builder().type(SampleMessage.MessageType.ANALYZED).data(data).build())
					.doOnSuccess(publisher::tryEmitNext)
					.subscribe();
		};
	}
	private static net.sayaya.document.modeler.sample.Sample process(net.sayaya.document.modeler.sample.Sample entity, Path file) {
		try {
			Thread.sleep(5000);
		} catch(InterruptedException e) {
			e.printStackTrace();
		}
		return entity.size(file.toFile().length()).page(10).thumbnail("RRWEFWEF");
	}
}
