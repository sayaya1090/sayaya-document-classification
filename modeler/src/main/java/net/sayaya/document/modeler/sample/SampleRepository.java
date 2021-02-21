package net.sayaya.document.modeler.sample;

import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SampleRepository extends CassandraRepository<Sample, String> {
}
