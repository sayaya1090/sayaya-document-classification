package net.sayaya.document.modeler.sample

import org.springframework.data.cassandra.repository.ReactiveCassandraRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono
import java.util.*

@Repository
interface SampleRepository : ReactiveCassandraRepository<Sample, String> {
    fun findByModelAndId(model: String, id: UUID): Mono<Sample>
}