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
		return Flux.fromStream(repo.findAll().stream()).map(ModelToDTO::map);
	}
	public Mono<Model> create(String name) {
		net.sayaya.document.modeler.model.Model model = new net.sayaya.document.modeler.model.Model().name(name);
		return Mono.just(repo.save(model)).map(ModelToDTO::map);
	}
	public Mono<Void> remove(String name) {
		repo.deleteById(name);
		return Mono.empty();
	}
}
