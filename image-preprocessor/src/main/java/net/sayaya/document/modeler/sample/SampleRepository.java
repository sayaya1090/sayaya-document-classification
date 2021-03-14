package net.sayaya.document.modeler.sample;

import org.springframework.data.cassandra.repository.ReactiveCassandraRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
public interface SampleRepository extends ReactiveCassandraRepository<Sample, String> {
	Mono<Sample> findByModelAndId(String model, UUID id);
}
