package net.sayaya.document.modeler.sample

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.http.codec.ServerSentEvent
import org.springframework.http.codec.multipart.FilePart
import org.springframework.http.codec.multipart.Part
import org.springframework.util.MultiValueMap
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.server.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Configuration
class SampleRouter(private val handler: SampleHandler) {
    @Bean
    fun documentRouterInstance(): RouterFunction<ServerResponse?> {
        return RouterFunctions
            .route(RequestPredicates.GET("/models/{model}/samples"), this::findSamples)
            .andRoute(RequestPredicates.POST("/models/{model}/samples"), this::uploadSamples)
            .andRoute(RequestPredicates.DELETE("/models/{model}/samples/{id}"), this::removeSamples)
            .andRoute(RequestPredicates.GET("/models/{model}/samples/changes"), this::subscribeSample)
    }

    private fun findSamples(request: ServerRequest): Mono<ServerResponse?> {
        val model = request.pathVariable("model")
        return handler.list(model).collectList()
            .flatMap(ServerResponse.ok().contentType(APPLICATION_JSON)::bodyValue)
            .switchIfEmpty(ServerResponse.noContent().build())
            .onErrorResume (ServerResponse.badRequest()::bodyValue)
    }

    private fun uploadSamples(request: ServerRequest): Mono<ServerResponse?> {
        return handler.upload(request.pathVariable("model"),
            request.multipartData()
                .map { i -> i["files"] }
                .flatMapMany { Flux.fromIterable(it) }
                .cast(FilePart::class.java))
            .collectList()
            .flatMap { ServerResponse.ok().build() }
            .switchIfEmpty(ServerResponse.status(HttpStatus.CONFLICT).build())
            .onErrorResume(ServerResponse.badRequest()::bodyValue)
    }

    private fun removeSamples(request: ServerRequest): Mono<ServerResponse?> {
        val model = request.pathVariable("model")
        val id = request.pathVariable("id")
        return handler.remove(model, id)
            .flatMap { ServerResponse.ok().build() }
            .onErrorResume(ServerResponse.badRequest()::bodyValue)
    }

    private fun subscribeSample(request: ServerRequest): Mono<ServerResponse?> {
        val model = request.pathVariable("model")
        return ServerResponse.ok().contentType(MediaType.TEXT_EVENT_STREAM)
            .body(BodyInserters.fromServerSentEvents(handler.subscribe(model)
                .map { msg -> ServerSentEvent.builder(msg.data).event(msg.type.name).id(msg.data.name()).build() }))
    }
}