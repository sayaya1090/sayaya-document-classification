package net.sayaya.document.modeler.sample;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.sayaya.document.data.Sample;
import net.sayaya.document.data.SampleMessage;
import net.sayaya.document.modeler.sample.processor.Preprocessor;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Supplier;

@Service
public class SampleHandler {
	private final SampleRepository repo;
	private final ObjectMapper OM;
	private final List<Preprocessor> processors;
	private final Sinks.Many<SampleMessage> publisher = Sinks.many().multicast().directBestEffort();
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
			SampleMessage msg = map(c);
			if(msg.type() != SampleMessage.MessageType.CREATE) return;
			Sample info = msg.data();
			Path tmp = Path.of(info.model()).resolve(info.id());
			repo.findByModelAndId(info.model(), UUID.fromString(info.id()))
				.map(e-> processors.stream().filter(p->p.chk(e, tmp))
								   .peek(p->publisher.tryEmitNext(SampleMessage.builder().type(SampleMessage.MessageType.PROCESSING).data(info).build()))
								   .findFirst()
								   .map(p->p.process(e, tmp)).orElse(null)).filter(Objects::nonNull)
				.flatMap(repo::save)
				.map(SampleToDTO::map)
				.map(data->SampleMessage.builder().type(SampleMessage.MessageType.ANALYZED).data(data).build())
				.doOnSuccess(publisher::tryEmitNext)
				.subscribe();
		};
	}
	private String map(SampleMessage dto) {
		try {
			return OM.writeValueAsString(dto);
		} catch(JsonProcessingException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}
	private SampleMessage map(String json) {
		try {
			return OM.readValue(json, SampleMessage.class);
		} catch(JsonProcessingException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}
}
