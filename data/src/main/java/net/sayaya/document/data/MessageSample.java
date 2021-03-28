package net.sayaya.document.data;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Builder;
import lombok.Value;
import lombok.experimental.Accessors;

@JsonDeserialize(builder = MessageSample.SampleMessageBuilder.class)
@Builder(builderClassName = "SampleMessageBuilder")
@Value
@Accessors(fluent = true)
public class MessageSample {
	MessageType type;
	Sample data;

	public enum MessageType {
		CREATE, PROCESSING, ANALYZED, DELETE
	}
}
