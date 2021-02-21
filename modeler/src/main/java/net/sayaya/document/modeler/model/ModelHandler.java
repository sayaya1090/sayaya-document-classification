package net.sayaya.document.modeler.model;

import net.sayaya.document.data.Model;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class ModelHandler {
	private final ModelRepository repo;
	public ModelHandler(ModelRepository repo) {this.repo = repo;}

	public Flux<Model> list() {
		return repo.findAll().map(ModelToDTO::map);
	}
	public Mono<Model> create(String name) {
		net.sayaya.document.modeler.model.Model model = new net.sayaya.document.modeler.model.Model().name(name);
		return repo.save(model).map(ModelToDTO::map);
	}
	public Mono<Void> remove(String name) {
		return repo.deleteById(name);
	}
}
