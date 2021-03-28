package net.sayaya.document.modeler.sample

import com.fasterxml.jackson.databind.ObjectMapper
import net.sayaya.document.data.MessageSample
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.http.codec.multipart.FilePart
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.core.publisher.Sinks
import java.nio.charset.StandardCharsets
import java.nio.file.Files.createDirectories
import java.nio.file.Path
import java.util.*
import java.util.function.Consumer
import java.util.function.Supplier

@Service
class SampleHandler(private val repo: SampleRepository, private val om: ObjectMapper, @Value("\${server.temp-directory}") private val tmp: Path) {
    private val publisher = Sinks.many().unicast().onBackpressureBuffer<MessageSample>()
    private val subscriber = Sinks.many().multicast().directAllOrNothing<MessageSample>()
    fun list(model: String): Flux<net.sayaya.document.data.Sample> {
        return repo.findByModel(model).map(SampleToDTO::map)
    }
    fun upload(model: String, files: Flux<FilePart>): Flux<MessageSample> {
        return files.flatMap { part -> toEntity(model, part)}
            .flatMap(repo::save)
            .map(SampleToDTO::map)
            .map { data -> MessageSample(MessageSample.MessageType.CREATE, data) }
            .doOnNext(publisher::tryEmitNext)
    }
    private fun toEntity(model: String, part: FilePart): Mono<Sample> {
        val id = UUID.randomUUID()
        val dir = tmp.resolve(model)
        if (!dir.toFile().exists()) createDirectories(dir)
        val tmp = dir.resolve(id.toString())
        var fileName = String(part.filename().toByteArray(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8)
        if (fileName.contains("/")) fileName = fileName.substring(fileName.indexOf("/" + 1))
        if (fileName.contains("\\")) fileName = fileName.substring(fileName.indexOf("\\" + 1))
        return part.transferTo(tmp).then(Mono.just(Sample(model, id).apply { name=fileName }))
    }
    fun remove(model: String, id: String): Mono<MessageSample> {
        return repo.deleteByModelAndId(model, UUID.fromString(id))
            .then(Mono.just(net.sayaya.document.data.Sample().model(model).id(id)))
            .map { data -> MessageSample(MessageSample.MessageType.DELETE, data) }
            .doOnSuccess(publisher::tryEmitNext)
    }
    fun subscribe(model: String): Flux<MessageSample> {
        return subscriber.asFlux().filter { s -> model == s.data.model() }
    }
    private fun map(dto: MessageSample): String {
        return  om.writeValueAsString(dto)
    }
    private fun map(json: String): MessageSample {
        return om.readValue(json, MessageSample::class.java)
    }
    @Bean("publish-sample")
    fun publishSample(): Supplier<Flux<String>> {
        return Supplier { publisher.asFlux().map(this::map) }
    }
    @Bean("broadcast-sample")
    fun broadcastSample(): Consumer<String> {
        return Consumer { c: String -> subscriber.tryEmitNext(map(c)) }
    }
}