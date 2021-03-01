package net.sayaya.document.modeler.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.sayaya.document.data.Model;
import net.sayaya.document.data.ModelMessage;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

import java.util.function.Consumer;
import java.util.function.Supplier;

@Service
public class ModelHandler {
	private final ModelRepository repo;
	private final ObjectMapper OM;
	private final Sinks.Many<ModelMessage> publisher = Sinks.many().multicast().directAllOrNothing();
	private final Sinks.Many<ModelMessage> subscriber = Sinks.many().multicast().directBestEffort();
	public ModelHandler(ModelRepository repo, ObjectMapper om) {
		this.repo = repo;
		OM = om;
	}
	public Flux<Model> list() {
		return repo.findAll().map(ModelToDTO::map);
	}
	public Mono<ModelMessage> create(String name) {
		net.sayaya.document.modeler.model.Model model = new net.sayaya.document.modeler.model.Model().name(name);
		return repo.save(model)
				.map(ModelToDTO::map)
				.map(data->ModelMessage.builder().type(ModelMessage.MessageType.CREATE).data(data).build())
				.doOnSuccess(publisher::tryEmitNext);
	}
	public Mono<ModelMessage> remove(String name) {
		return repo.deleteById(name)
				.then(Mono.just(new Model().name(name).cntDocuments(0)))
				.map(data->ModelMessage.builder().type(ModelMessage.MessageType.DELETE).data(data).build())
				.doOnSuccess(publisher::tryEmitNext);
	}
	public Flux<ModelMessage> subscribe() {
		return subscriber.asFlux();
	}
	private String map(ModelMessage dto) {
		try {
			return OM.writeValueAsString(dto);
		} catch(JsonProcessingException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}
	private ModelMessage map(String json) {
		try {
			return OM.readValue(json, ModelMessage.class);
		} catch(JsonProcessingException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}
	@Bean
	public Supplier<Flux<String>> publish() {
		return ()-> publisher.asFlux().map(this::map);
	}
	@Bean
	public Consumer<String> broadcast() {
		return c->subscriber.tryEmitNext(map(c));
	}
}
