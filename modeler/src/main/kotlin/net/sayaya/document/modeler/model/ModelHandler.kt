package net.sayaya.document.modeler.model

import com.fasterxml.jackson.databind.ObjectMapper
import net.sayaya.document.data.MessageModel
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.core.publisher.Sinks
import java.util.function.Consumer
import java.util.function.Supplier

@Service
class ModelHandler(private val repo: ModelRepository, private val om: ObjectMapper) {
    private val publisher = Sinks.many().unicast().onBackpressureBuffer<MessageModel>()
    private val subscriber = Sinks.many().multicast().directBestEffort<MessageModel>()
    fun list(): Flux<net.sayaya.document.data.Model> {
        return repo.findAll().map(ModelToDTO::map)
    }
    fun create(name: String): Mono<MessageModel> {
        val model = Model().apply { this.name = name }
        return repo.save(model).map(ModelToDTO::map)
            .map { data -> MessageModel(MessageModel.MessageType.CREATE, data) }
            .doOnSuccess { t: MessageModel -> publisher.tryEmitNext(t) }
    }
    fun remove(name: String): Mono<MessageModel> {
        return repo.deleteById(name)
            .then(Mono.just(net.sayaya.document.data.Model().name(name).cntDocuments(0)))
            .map { data -> MessageModel(MessageModel.MessageType.DELETE, data) }
            .doOnSuccess { t: MessageModel -> publisher.tryEmitNext(t) }
    }
    fun subscribe(): Flux<MessageModel> {
        return subscriber.asFlux()
    }
    private fun map(dto: MessageModel): String {
        return om.writeValueAsString(dto)
    }
    private fun map(json: String): MessageModel {
        return om.readValue(json, MessageModel::class.java)
    }
    @Bean("publish-model")
    fun publishModel(): Supplier<Flux<String>> {
        return Supplier { publisher.asFlux().map { dto: MessageModel -> this.map(dto) } }
    }
    @Bean("broadcast-model")
    fun broadcastModel(): Consumer<String> {
        return Consumer { c: String -> subscriber.tryEmitNext(map(c)) }
    }
}