package net.sayaya.document.data;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Builder;
import lombok.Value;
import lombok.experimental.Accessors;

import java.io.Serializable;

@JsonDeserialize(builder = SampleMessage.SampleMessageBuilder.class)
@Builder(builderClassName = "SampleMessageBuilder")
@Value
@Accessors(fluent = true)
public class SampleMessage implements Serializable {
	SampleMessage.MessageType type;
	Sample data;

	public enum MessageType {
		CREATE, PROCESSING, ANALYZED, DELETE
	}
}
