package net.sayaya.document.data;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Builder;
import lombok.Value;
import lombok.experimental.Accessors;

@JsonDeserialize(builder = MessageModel.ModelMessageBuilder.class)
@Builder(builderClassName = "ModelMessageBuilder")
@Value
@Accessors(fluent = true)
public class MessageModel {
	MessageType type;
	Model data;

	public enum MessageType {
		CREATE, LEARNING, LEARNED, DELETE
	}
}
