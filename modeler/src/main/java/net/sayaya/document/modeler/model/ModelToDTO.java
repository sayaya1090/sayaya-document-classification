package net.sayaya.document.modeler.model;

import net.sayaya.document.data.Model;
import lombok.experimental.UtilityClass;

@UtilityClass
public class ModelToDTO {
	public Model map(net.sayaya.document.modeler.model.Model entity) {
		return new Model().name(entity.name()).cohesion(entity.cohesion());
	}
}
