package net.sayaya.document;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.http.codec.json.Jackson2JsonEncoder;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.reactive.config.CorsRegistry;
import org.springframework.web.reactive.config.EnableWebFlux;
import org.springframework.web.reactive.config.WebFluxConfigurer;
import org.springframework.web.server.WebFilter;

import java.beans.BeanProperty;

@Configuration
@EnableAsync
@EnableWebFlux
public class WebFluxConfig implements WebFluxConfigurer {
	private final ObjectMapper objectMapper;
	public WebFluxConfig(ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
	}
	@Bean
	public WebFilter addViewControllers() {
		return (exchange, chain) -> {
			if (exchange.getRequest().getURI().getPath().equals("/")) return chain.filter(exchange.mutate().request(exchange.getRequest().mutate().path("/index.html").build()).build());
			else return chain.filter(exchange);
		};
	}
	@Override
	public void configureHttpMessageCodecs(ServerCodecConfigurer configurer) {
		configurer.defaultCodecs().jackson2JsonEncoder(new Jackson2JsonEncoder(objectMapper));
		configurer.defaultCodecs().jackson2JsonDecoder(new Jackson2JsonDecoder(objectMapper));
	}
	@Override
	public void addCorsMappings(CorsRegistry corsRegistry) {
		corsRegistry.addMapping("/**")
				.allowedOrigins("*")
				.allowedMethods("OPTION", "GET", "PUT", "POST", "PATCH", "DELETE")
				.maxAge(3600);
	}
}
