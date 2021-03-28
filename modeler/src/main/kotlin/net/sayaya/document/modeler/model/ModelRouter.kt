package net.sayaya.document.modeler.model

import net.sayaya.document.data.MessageModel
import net.sayaya.document.data.Model
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.codec.ServerSentEvent
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.server.*
import reactor.core.publisher.Mono

@Configuration
class ModelRouter(private val handler: ModelHandler) {
    @Bean
    fun modelRouterInstance(): RouterFunction<ServerResponse?> {
        return RouterFunctions
            .route(RequestPredicates.GET("/models"), this::findModels)
            .andRoute(RequestPredicates.PUT("/models/{id}"), this::createModels)
            .andRoute(RequestPredicates.DELETE("/models/{id}"), this::removeModels)
            .andRoute(RequestPredicates.GET("/models/changes"), this::subscribeModel)
    }
    private fun findModels(request: ServerRequest): Mono<ServerResponse> {
        return handler.list().collectList()
            .flatMap(ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)::bodyValue)
            .switchIfEmpty(ServerResponse.noContent().build())
            .onErrorResume (ServerResponse.badRequest()::bodyValue)
    }
    private fun createModels(request: ServerRequest): Mono<ServerResponse> {
        return handler.create(request.pathVariable("id"))
            .flatMap { ServerResponse.ok().build() }
            .switchIfEmpty(ServerResponse.status(HttpStatus.CONFLICT).build())
            .onErrorResume (ServerResponse.badRequest()::bodyValue)
    }
    private fun removeModels(request: ServerRequest): Mono<ServerResponse> {
        return handler.remove(request.pathVariable("id"))
            .flatMap { ServerResponse.ok().build() }
            .onErrorResume { e ->
                e.printStackTrace()
                ServerResponse.badRequest().bodyValue(e.message!!)
            }
    }
    private fun subscribeModel(request: ServerRequest): Mono<ServerResponse> {
        return ServerResponse.ok().contentType(MediaType.TEXT_EVENT_STREAM)
            .body(BodyInserters.fromServerSentEvents(handler.subscribe().map {
                    msg -> ServerSentEvent.builder<Model>(msg.data).event(msg.type.name).id(msg.data.name()).build()
            }))
    }
}