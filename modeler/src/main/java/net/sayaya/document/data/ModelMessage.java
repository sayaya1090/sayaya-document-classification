package net.sayaya.document.data;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Builder;
import lombok.Value;
import lombok.experimental.Accessors;

import java.io.Serializable;

@JsonDeserialize(builder = ModelMessage.ModelMessageBuilder.class)
@Builder(builderClassName = "ModelMessageBuilder")
@Value
@Accessors(fluent = true)
public class ModelMessage implements Serializable {
	MessageType type;
	Model data;

	public enum MessageType {
		CREATE, UPDATE, DELETE
	}
}
