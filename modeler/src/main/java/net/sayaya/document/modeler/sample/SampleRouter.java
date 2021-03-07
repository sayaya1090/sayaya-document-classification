package net.sayaya.document.modeler.sample;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.reactive.function.BodyInserters;
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
		return route(GET("/models/{model}/samples"), this::findSamples)
				.andRoute(POST("/models/{model}/samples"), this::uploadSamples)
				.andRoute(DELETE("/models/{model}/samples/{id}"), this::removeSamples)
				.andRoute(GET("/models/{model}/samples/changes"), this::subscribeSample);
	}
	private Mono<ServerResponse> findSamples(ServerRequest request) {
		String model = request.pathVariable("model");
		return handler.list(model).collectList()
				.flatMap(list->ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).bodyValue(list))
				.switchIfEmpty(ServerResponse.noContent().build())
				.onErrorResume(e->ServerResponse.badRequest().bodyValue(e.getMessage()));
	}
	private Mono<ServerResponse> uploadSamples(ServerRequest request) {
		String model = request.pathVariable("model");
		return handler.upload(model,  request.multipartData().map(i->i.get("files")).flatMapMany(Flux::fromIterable).cast(FilePart.class))
				.collectList()
				.flatMap(obj->ServerResponse.ok().build())
				.switchIfEmpty(ServerResponse.status(HttpStatus.CONFLICT).build())
				.onErrorResume(e->ServerResponse.badRequest().bodyValue(e.getMessage()));
	}
	private Mono<ServerResponse> removeSamples(ServerRequest request) {
		String model = request.pathVariable("model");
		String id = request.pathVariable("id");
		return handler.remove(model, id)
				.flatMap(obj->ServerResponse.ok().build())
				.onErrorResume(e->{
					e.printStackTrace();
					return ServerResponse.badRequest().bodyValue(e.getMessage());
				});
	}
	private Mono<ServerResponse> subscribeSample(ServerRequest request) {
		String model = request.pathVariable("model");
		return ServerResponse.ok().contentType(MediaType.TEXT_EVENT_STREAM)
				.body(BodyInserters.fromServerSentEvents(handler.subscribe(model)
						.map(msg-> ServerSentEvent.builder(msg.data())
								.event(msg.type().name())
								.id(msg.data().name())
								.build())));
	}
}
