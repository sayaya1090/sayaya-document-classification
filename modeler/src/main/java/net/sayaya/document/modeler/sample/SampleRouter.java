package net.sayaya.document.modeler.sample;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class SampleRouter {
	private final SampleHandler handler;
	public SampleRouter(SampleHandler handler) {this.handler = handler;}
	@Bean
	public RouterFunction<ServerResponse> documentRouterInstance() {
		return route(POST("/{model}/samples"), this::uploadDocuments);
	}
	private Mono<ServerResponse> uploadDocuments(ServerRequest request) {
		String model = request.pathVariable("model");
		return handler.upload(model, request.multipartData().flatMapMany(files-> Flux.fromIterable(files.get("files"))))
				.flatMap(obj->ServerResponse.ok().build())
				.switchIfEmpty(ServerResponse.status(HttpStatus.CONFLICT).build())
				.onErrorResume(e->ServerResponse.badRequest().bodyValue(e.getMessage()));
	}
}
