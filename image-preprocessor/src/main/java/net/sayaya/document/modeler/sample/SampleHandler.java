package net.sayaya.document.modeler.sample;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.sayaya.document.data.MessageSample;
import net.sayaya.document.modeler.sample.processor.Preprocessor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;
import reactor.util.retry.Retry;

import java.nio.file.Path;
import java.time.Duration;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Supplier;

@Service
public class SampleHandler {
	private final SampleRepository repo;
	private final ObjectMapper OM;
	private final List<Preprocessor> processors;
	private final Sinks.Many<MessageSample> publisher = Sinks.many().unicast().onBackpressureBuffer();
	@Value("${server.temp-directory}")
	private Path tmp;
	public SampleHandler(SampleRepository repo, ObjectMapper om, List<Preprocessor> processors) {
		this.repo = repo;
		OM = om;
		this.processors = processors;
	}

	@Bean("publish-sample")
	public Supplier<Flux<String>> publishSample() {
		return ()->publisher.asFlux().map(this::map);
	}
	@Bean("analyze-sample")
	public Consumer<String> analyzeSample() {
		return c->{
			var msg = map(c);
			if(msg.type() != MessageSample.MessageType.CREATE) return;
			var info = msg.data();
			var path = tmp.resolve(info.model()).resolve(info.id());
			repo.findByModelAndId(info.model(), UUID.fromString(info.id()))
				.retryWhen(Retry.fixedDelay(5, Duration.ofSeconds(1)))
				.map(e->processors.stream().filter(p->p.chk(e, path))
								  .peek(p->publisher.tryEmitNext(MessageSample.builder().type(MessageSample.MessageType.PROCESSING).data(info).build()))
								  .findFirst()
								  .map(p->p.process(e, path))
								  .orElseThrow(NullPointerException::new))
				.doOnNext(repo::save)
				.map(SampleToDTO::map)
				.map(data-> MessageSample.builder().type(MessageSample.MessageType.ANALYZED).data(data).build())
				.doOnSuccess(publisher::tryEmitNext)
				.onErrorStop()
				.subscribe();
		};
	}

	private String map(MessageSample dto) {
		try {
			return OM.writeValueAsString(dto);
		} catch(JsonProcessingException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}
	private MessageSample map(String json) {
		try {
			return OM.readValue(json, MessageSample.class);
		} catch(JsonProcessingException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}
}
