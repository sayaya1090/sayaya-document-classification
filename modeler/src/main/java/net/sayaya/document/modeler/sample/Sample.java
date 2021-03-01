package net.sayaya.document.modeler.sample;

import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.core.mapping.Table;

import java.time.LocalDateTime;
import java.util.UUID;

@Table("sample")
@Data
@Accessors(fluent = true)
public class Sample {
	@PrimaryKeyColumn(name = "model", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
	String model;
	@PrimaryKeyColumn(name = "id", ordinal = 1, type = PrimaryKeyType.CLUSTERED)
	UUID id = UUID.randomUUID();
	@Column("create_time")
	LocalDateTime createTime = LocalDateTime.now();
	@Column
	String name;
	@Column
	long size;
	@Column
	int page;
	@Column
	Boolean conformity;
}