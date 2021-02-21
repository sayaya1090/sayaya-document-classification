package net.sayaya.document.modeler.model;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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
		return route(GET(""), this::findModels)
				.andRoute(PUT("/{id}"), this::createModels)
				.andRoute(DELETE("/{id}"), this::removeModels);
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
				.flatMap(obj->ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).bodyValue(obj))
				.switchIfEmpty(ServerResponse.status(HttpStatus.CONFLICT).build())
				.onErrorResume(e->ServerResponse.badRequest().bodyValue(e.getMessage()));
	}
	private Mono<ServerResponse> removeModels(ServerRequest request) {
		String id = request.pathVariable("id");
		return handler.remove(id)
				.flatMap(obj->ServerResponse.ok().build())
				.onErrorResume(e->ServerResponse.badRequest().bodyValue(e.getMessage()));
	}
}
