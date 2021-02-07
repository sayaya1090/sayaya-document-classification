package net.sayaya.document;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

@Configuration
public class JsonConfig {
	@Bean
	public ObjectMapper objectMapper() {
		return Jackson2ObjectMapperBuilder.json()
				.visibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY)
				.featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
				.featuresToEnable(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS)
				.featuresToEnable(JsonGenerator.Feature.WRITE_BIGDECIMAL_AS_PLAIN)
				.modules(new JavaTimeModule())
				.propertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE)
				.build();
	}
}

