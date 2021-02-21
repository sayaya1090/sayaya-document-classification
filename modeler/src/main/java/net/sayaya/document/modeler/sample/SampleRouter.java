package net.sayaya.document.modeler.sample;

import net.sayaya.document.data.Sample;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.reactive.function.BodyExtractor;
import org.springframework.web.reactive.function.BodyExtractors;
import org.springframework.web.reactive.function.BodyInserter;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Objects;

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
		// Flux<Sample> result = handler.upload(model, request.multipartData().flatMapMany().cast(FilePart.class));
		Flux<Sample> result = handler.upload(model, request.multipartData().map(i->i.get("files")).flatMapMany(Flux::fromIterable).cast(FilePart.class));
		return ServerResponse.ok().contentType(MediaType.APPLICATION_NDJSON).body(result, Sample.class)
				.switchIfEmpty(ServerResponse.status(HttpStatus.CONFLICT).build())
				.onErrorResume(e->ServerResponse.badRequest().bodyValue(e.getMessage()));
	}
}
