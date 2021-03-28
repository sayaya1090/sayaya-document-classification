package net.sayaya.document.modeler.sample

import com.fasterxml.jackson.databind.ObjectMapper
import net.sayaya.document.data.MessageSample
import net.sayaya.document.modeler.sample.processor.Preprocessor
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.core.publisher.Sinks
import reactor.util.retry.Retry
import java.nio.file.Path
import java.time.Duration
import java.util.*
import java.util.function.Consumer
import java.util.function.Supplier

@Service
class SampleHandler(
    private val repo: SampleRepository,
    private val om: ObjectMapper,
    private val processors: List<Preprocessor>,
    @Value("\${server.temp-directory}") private val tmp: Path) {
    private val publisher: Sinks.Many<MessageSample> = Sinks.many().unicast().onBackpressureBuffer()
    @Bean("publish-sample")
    fun publishSample(): Supplier<Flux<String>> {
        return Supplier { publisher.asFlux().map(this::map) }
    }

    @Bean("analyze-sample")
    fun analyzeSample(): Consumer<String> {
        return Consumer { json ->
            val (type, info) = map(json)
            if(type !== MessageSample.MessageType.CREATE) return@Consumer
            val path = tmp.resolve(info.model()).resolve(info.id())
            repo.findByModelAndId(info.model(), UUID.fromString(info.id()))
                .retryWhen(Retry.fixedDelay(5, Duration.ofSeconds(1)))
                .map { s -> processors.stream()
                        .filter { p -> p.chk(s, path) }
                        .findFirst()
                        .apply { publisher.tryEmitNext(MessageSample(MessageSample.MessageType.PROCESSING, info)) }
                        .map { p -> p.process(s, path) }
                        .orElseThrow { NullPointerException()  }
                }.flatMap(repo::save)
                .map(SampleToDTO::map)
                .map { data -> MessageSample(MessageSample.MessageType.ANALYZED, data) }
                .doOnSuccess(publisher::tryEmitNext)
                .onErrorStop()
                .subscribe()
        }
    }

    private fun map(dto: MessageSample): String {
        return om.writeValueAsString(dto)
    }

    private fun map(json: String): MessageSample {
        return om.readValue(json, MessageSample::class.java)
    }
}