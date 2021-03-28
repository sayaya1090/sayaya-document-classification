package net.sayaya.document.modeler.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.sayaya.document.data.Model;
import net.sayaya.document.data.MessageModel;
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
	private final Sinks.Many<MessageModel> publisher = Sinks.many().unicast().onBackpressureBuffer();
	private final Sinks.Many<MessageModel> subscriber = Sinks.many().multicast().directBestEffort();
	public ModelHandler(ModelRepository repo, ObjectMapper om) {
		this.repo = repo;
		OM = om;
	}
	public Flux<Model> list() {
		return repo.findAll().map(ModelToDTO::map);
	}
	public Mono<MessageModel> create(String name) {
		net.sayaya.document.modeler.model.Model model = new net.sayaya.document.modeler.model.Model();
		model.setName(name);
		return repo.save(model)
				.map(ModelToDTO::map)
				.map(data->new MessageModel(MessageModel.MessageType.CREATE, data))
				.doOnSuccess(publisher::tryEmitNext);
	}
	public Mono<MessageModel> remove(String name) {
		return repo.deleteById(name)
				.then(Mono.just(new Model().name(name).cntDocuments(0)))
				.map(data->new MessageModel(MessageModel.MessageType.DELETE, data))
				.doOnSuccess(publisher::tryEmitNext);
	}
	public Flux<MessageModel> subscribe() {
		return subscriber.asFlux();
	}
	private String map(MessageModel dto) {
		try {
			return OM.writeValueAsString(dto);
		} catch(JsonProcessingException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}
	private MessageModel map(String json) {
		try {
			return OM.readValue(json, MessageModel.class);
		} catch(JsonProcessingException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}
	@Bean("publish-model")
	public Supplier<Flux<String>> publishModel() {
		return ()-> publisher.asFlux().map(this::map);
	}
	@Bean("broadcast-model")
	public Consumer<String> broadcastModel() {
		return c->subscriber.tryEmitNext(map(c));
	}
}
