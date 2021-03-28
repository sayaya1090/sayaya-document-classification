package net.sayaya.document.modeler.model

import org.springframework.data.cassandra.core.mapping.Column
import org.springframework.data.cassandra.core.mapping.PrimaryKey
import org.springframework.data.cassandra.core.mapping.Table
import java.time.LocalDateTime


@Table("model")
data class Model (
    @PrimaryKey
    @Column val name: String,
    @Column("create_time") val createTime: LocalDateTime = LocalDateTime.now()
) {
    @Column var cohesion: Double? = null
}