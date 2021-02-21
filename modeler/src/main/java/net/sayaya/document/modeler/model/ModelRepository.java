package net.sayaya.document.modeler.model;

import org.springframework.data.cassandra.repository.ReactiveCassandraRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ModelRepository extends ReactiveCassandraRepository<Model, String> {
}
