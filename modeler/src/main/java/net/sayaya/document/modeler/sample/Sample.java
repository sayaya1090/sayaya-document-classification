package net.sayaya.document.modeler.sample;

import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import java.util.UUID;

@Table("sample")
@Data
@Accessors(fluent = true)
public class Sample {
	@PrimaryKey
	@Column
	UUID id;
	@Column
	String name;
	@Column
	String size;
	@Column
	String page;
}
