package net.sayaya.document.modeler.sample;

import org.springframework.data.cassandra.repository.ReactiveCassandraRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface SampleRepository extends ReactiveCassandraRepository<Sample, String> {
	<T> Flux<T> findByModel(String model, Class<T> clazz);
}
