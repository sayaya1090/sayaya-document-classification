package net.sayaya.document.modeler.sample

import org.springframework.data.cassandra.core.cql.PrimaryKeyType
import org.springframework.data.cassandra.core.mapping.Column
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn
import org.springframework.data.cassandra.core.mapping.Table
import java.time.LocalDateTime
import java.util.*

@Table("sample")
class Sample(
    @PrimaryKeyColumn(name = "model", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
    var model: String,
    @PrimaryKeyColumn(name = "id", ordinal = 1, type = PrimaryKeyType.CLUSTERED)
    var id: UUID
) {
    @Column("create_time")
    var createTime = LocalDateTime.now()
    @Column
    var name: String = ""
    @Column
    var size: Long = 0
    @Column
    var page = 0
    @Column
    var thumbnail: String? = null
    @Column
    var conformity: Boolean? = null
}