package net.sayaya.document.modeler.sample;

import org.springframework.http.codec.multipart.Part;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class SampleHandler {
	public Mono<Void> upload(String model, Flux<Part> files) {
		return Mono.empty();
	}
}
