package net.sayaya.document.modeler.model;

import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import java.time.LocalDateTime;

@Table("model")
@Data
@Accessors(fluent = true)
public class Model {
	@PrimaryKey
	@Column
	String name;
	@Column("create_time")
	LocalDateTime createTime = LocalDateTime.now();
	@Column
	Double cohesion;
}
