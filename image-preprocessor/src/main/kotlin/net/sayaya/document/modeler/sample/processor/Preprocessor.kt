package net.sayaya.document.modeler.sample.processor

import net.sayaya.document.modeler.sample.Sample
import java.nio.file.Path

interface Preprocessor {
    fun chk(sample: Sample, file: Path): Boolean
    fun process(sample: Sample, file: Path): Sample
}