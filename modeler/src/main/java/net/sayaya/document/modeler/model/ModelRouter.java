package net.sayaya.document.modeler.model;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import static org.springframework.web.reactive.function.server.RequestPredicates.DELETE;
import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.PUT;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class ModelRouter {
	private final ModelHandler handler;
	public ModelRouter(ModelHandler handler) {
		this.handler = handler;
	}
	@Bean
	public RouterFunction<ServerResponse> modelRouterInstance() {
		return route(GET("/models"), this::findModels)
				.andRoute(PUT("/models/{id}"), this::createModels)
				.andRoute(DELETE("/models/{id}"), this::removeModels)
				.andRoute(GET("/models/changes"), this::subscribeModel);
	}
	private Mono<ServerResponse> findModels(ServerRequest request) {
		return handler.list().collectList()
				.flatMap(list->ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).bodyValue(list))
				.switchIfEmpty(ServerResponse.noContent().build())
				.onErrorResume(e->ServerResponse.badRequest().bodyValue(e.getMessage()));
	}
	private Mono<ServerResponse> createModels(ServerRequest request) {
		String id = request.pathVariable("id");
		return handler.create(id)
				.flatMap(obj->ServerResponse.ok().build())
				.switchIfEmpty(ServerResponse.status(HttpStatus.CONFLICT).build())
				.onErrorResume(e->ServerResponse.badRequest().bodyValue(e.getMessage()));
	}
	private Mono<ServerResponse> removeModels(ServerRequest request) {
		String id = request.pathVariable("id");
		return handler.remove(id)
				.flatMap(obj->ServerResponse.ok().build())
				.onErrorResume(e->{
					e.printStackTrace();
					return ServerResponse.badRequest().bodyValue(e.getMessage());
				});
	}
	private Mono<ServerResponse> subscribeModel(ServerRequest request) {
		return ServerResponse.ok().contentType(MediaType.TEXT_EVENT_STREAM)
				.body(BodyInserters.fromServerSentEvents(handler.subscribe()
						.map(msg->ServerSentEvent.builder(msg.data())
								.event(msg.type().name())
								.id(msg.data().name())
								.build())));
	}
}
