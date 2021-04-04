package net.sayaya.document.modeler.model

import com.fasterxml.jackson.databind.ObjectMapper
import net.sayaya.document.data.MessageModel
import org.mapstruct.factory.Mappers
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.core.publisher.Sinks
import java.util.function.Consumer
import java.util.function.Supplier

@Service
class ModelHandler(
    private val repo: ModelRepository,
    private val om: ObjectMapper,
    private val mapper: ModelToDTO
) {
    private val publisher = Sinks.many().unicast().onBackpressureBuffer<MessageModel>()
    private val subscriber = Sinks.many().multicast().directBestEffort<MessageModel>()
    fun list(): Flux<net.sayaya.document.data.Model> {
        return repo.findAll().map(mapper::toModel)
    }
    fun create(name: String): Mono<MessageModel> {
        val model = Model(name)
        return repo.save(model).map(mapper::toModel)
            .map { data -> MessageModel(MessageModel.MessageType.CREATE, data) }
            .doOnSuccess(publisher::tryEmitNext)
    }
    fun remove(name: String): Mono<MessageModel> {
        return repo.deleteById(name)
            .then(Mono.just(net.sayaya.document.data.Model().name(name).cntDocuments(0)))
            .map { data -> MessageModel(MessageModel.MessageType.DELETE, data) }
            .doOnSuccess(publisher::tryEmitNext)
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
        return Supplier { publisher.asFlux().map(this::map) }
    }
    @Bean("broadcast-model")
    fun broadcastModel(): Consumer<String> {
        return Consumer { json -> subscriber.tryEmitNext(map(json)) }
    }
}