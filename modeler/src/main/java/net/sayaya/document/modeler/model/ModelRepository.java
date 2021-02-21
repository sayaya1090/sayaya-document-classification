package net.sayaya.document.modeler.model;

import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ModelRepository extends CassandraRepository<Model, String> {
}
