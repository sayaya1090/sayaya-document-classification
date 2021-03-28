package net.sayaya.document.modeler.model

import lombok.Data
import lombok.experimental.Accessors
import org.springframework.data.cassandra.core.mapping.Column
import org.springframework.data.cassandra.core.mapping.PrimaryKey
import org.springframework.data.cassandra.core.mapping.Table
import java.time.LocalDateTime


@Table("model")
@Data
@Accessors(fluent = true)
class Model {
    @PrimaryKey
    @Column
    var name: String? = null

    @Column("create_time")
    var createTime = LocalDateTime.now()

    @Column
    var cohesion: Double? = null
}