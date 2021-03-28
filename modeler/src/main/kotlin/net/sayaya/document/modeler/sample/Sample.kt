package net.sayaya.document.modeler.sample

import org.springframework.data.cassandra.core.cql.PrimaryKeyType
import org.springframework.data.cassandra.core.mapping.Column
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn
import org.springframework.data.cassandra.core.mapping.Table
import java.time.LocalDateTime
import java.util.*

@Table("sample")
data class Sample (
    @PrimaryKeyColumn(type = PrimaryKeyType.PARTITIONED) val model: String,
    @PrimaryKeyColumn(type = PrimaryKeyType.CLUSTERED) val id: UUID,
    @Column("create_time") val createTime: LocalDateTime = LocalDateTime.now()
) {
    @Column var name: String? = null
    @Column var size: Long = 0
    @Column var page = 0
    @Column var thumbnail: String? = null
    @Column var conformity: Boolean? = null
}