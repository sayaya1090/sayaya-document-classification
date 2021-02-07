package net.sayaya.document.publisher;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@CrossOrigin
@RestController
public class PublishController {
	@GetMapping(value="/upload")
	@ResponseStatus(HttpStatus.OK)
	public Mono<Void> upload(ServerWebExchange exchange) {
		System.out.println(exchange.getRequest().getHeaders());
		return Mono.empty();
	}
}
