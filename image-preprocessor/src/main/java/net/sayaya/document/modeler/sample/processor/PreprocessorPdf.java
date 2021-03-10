package net.sayaya.document.modeler.sample.processor;

import net.sayaya.document.modeler.sample.Sample;
import org.springframework.stereotype.Service;

import java.nio.file.Path;

@Service
public class PreprocessorPdf implements Preprocessor {
	@Override
	public boolean chk(Sample sample, Path file) {
		if(file.getFileName().toString().toLowerCase().endsWith("pdf")) return true;
		return false;
	}

	@Override
	public Sample process(Sample sample, Path file) {
		return sample;
	}
}
