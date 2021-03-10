package net.sayaya.document.modeler.sample.processor;


import net.sayaya.document.modeler.sample.Sample;

import java.nio.file.Path;

public interface Preprocessor {
	boolean chk(Sample sample, Path file);
	Sample process(Sample sample, Path file);
}
