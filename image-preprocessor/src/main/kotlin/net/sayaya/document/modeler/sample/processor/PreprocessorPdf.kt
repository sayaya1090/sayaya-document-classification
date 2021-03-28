package net.sayaya.document.modeler.sample.processor

import net.sayaya.document.modeler.sample.Sample
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.rendering.ImageType
import org.apache.pdfbox.rendering.PDFRenderer
import org.apache.pdfbox.tools.imageio.ImageIOUtil
import org.bouncycastle.util.encoders.Base64Encoder
import org.springframework.stereotype.Component
import java.io.ByteArrayOutputStream
import java.nio.ByteBuffer
import java.nio.channels.FileChannel
import java.nio.file.Path
import java.nio.file.StandardOpenOption
import java.util.*

@Component
class PreprocessorPdf : Preprocessor {
    companion object {
        private val PDF_HEADER = byteArrayOf(0x25, 0x50, 0x44, 0x46, 0x2D)
        private val ENCODER = Base64Encoder()
    }
    override fun chk(sample: Sample, file: Path): Boolean {
        return if (sample.name.toLowerCase().endsWith("pdf")) {
            val channel = FileChannel.open(file, StandardOpenOption.READ)
            val buffer = ByteBuffer.allocate(5)
            channel.read(buffer)
            channel.close()
            Arrays.equals(buffer.array(), PDF_HEADER)
        } else false
    }
    override fun process(sample: Sample, file: Path): Sample {
        val doc = PDDocument.load(file.toFile())
        sample.page = doc.pages.count;
        sample.size = file.toFile().length()
        if (doc.pages.count > 0) {
            var baos = ByteArrayOutputStream()
            ImageIOUtil.writeImage(PDFRenderer(doc).renderImageWithDPI(0, 100f, ImageType.RGB), "png", baos)
            val img = baos.toByteArray()
            baos.close()
            baos = ByteArrayOutputStream()
            ENCODER.encode(img, 0, img.size, baos)
            sample.thumbnail = baos.toString()
            baos.close()
        }
        doc.close()
        return sample
    }
}