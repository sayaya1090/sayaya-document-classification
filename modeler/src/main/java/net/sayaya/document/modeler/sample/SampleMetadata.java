package net.sayaya.document.modeler.sample;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class SampleMetadata {
	String model;
	UUID id;
	LocalDateTime createTime;
	String name;
	long size;
	int page;
}
