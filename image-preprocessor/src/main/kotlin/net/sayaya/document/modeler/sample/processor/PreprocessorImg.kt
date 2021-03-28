package net.sayaya.document.modeler.sample.processor

import net.sayaya.document.modeler.sample.Sample
import org.apache.pdfbox.tools.imageio.ImageIOUtil
import org.bouncycastle.util.encoders.Base64Encoder
import org.springframework.stereotype.Component
import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.nio.file.Path
import javax.imageio.ImageIO
import kotlin.math.roundToInt

@Component
class PreprocessorImg : Preprocessor {
    companion object {
        private val ENCODER = Base64Encoder()
        private const val THUMBNAIL_WIDTH = 150.0
    }
    override fun chk(sample: Sample, file: Path): Boolean {
        return if (sample.name.toLowerCase().endsWith("png") ||
            sample.name.toLowerCase().endsWith("jpg") ||
            sample.name.toLowerCase().endsWith("jpeg") ||
            sample.name.toLowerCase().endsWith("gif") ||
            sample.name.toLowerCase().endsWith("bmp")
        ) try {
            ImageIO.read(file.toFile()) != null
        } catch (e: IOException) {
            e.printStackTrace()
            false
        } else false
    }

    override fun process(sample: Sample, file: Path): Sample {
        sample.page = 1
        sample.size = file.toFile().length()
        val original = ImageIO.read(file.toFile())
        val scaled = original.getScaledInstance(THUMBNAIL_WIDTH.roundToInt(), (THUMBNAIL_WIDTH * original.height / original.width).roundToInt(), BufferedImage.SCALE_SMOOTH)
        val thumbnail = BufferedImage(scaled.getWidth(null), scaled.getHeight(null), BufferedImage.TYPE_INT_ARGB)
        thumbnail.graphics.drawImage(scaled, 0, 0, null)
        thumbnail.graphics.dispose()
        var baos = ByteArrayOutputStream()
        ImageIOUtil.writeImage(thumbnail, "png", baos)
        val img = baos.toByteArray()
        baos.close()
        baos = ByteArrayOutputStream()
        ENCODER.encode(img, 0, img.size, baos)
        sample.thumbnail = baos.toString()
        baos.close()
        return sample
    }
}