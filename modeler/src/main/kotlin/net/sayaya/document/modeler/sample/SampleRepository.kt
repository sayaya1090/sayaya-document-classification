package net.sayaya.document.modeler.sample

import org.springframework.data.cassandra.repository.ReactiveCassandraRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.*

@Repository
interface SampleRepository : ReactiveCassandraRepository<Sample, String> {
    fun findByModel(model: String): Flux<Sample>
    fun deleteByModelAndId(model: String, id: UUID): Mono<Void>
}