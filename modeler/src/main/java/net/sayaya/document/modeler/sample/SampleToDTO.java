package net.sayaya.document.modeler.sample;

import lombok.experimental.UtilityClass;
import net.sayaya.document.data.Sample;

@UtilityClass
public class SampleToDTO {
	public Sample map(net.sayaya.document.modeler.sample.Sample entity) {
		return new Sample().id(entity.id().toString())
				.model(entity.model())
				.name(entity.name())
				.size(entity.size())
				.page(entity.page());
	}
}
