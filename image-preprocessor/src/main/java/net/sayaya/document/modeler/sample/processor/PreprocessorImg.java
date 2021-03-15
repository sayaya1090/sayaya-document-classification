package net.sayaya.document.modeler.sample.processor;

import net.sayaya.document.modeler.sample.Sample;
import org.apache.pdfbox.tools.imageio.ImageIOUtil;
import org.bouncycastle.util.encoders.Base64Encoder;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Path;

@Component
public class PreprocessorImg implements Preprocessor {
	private final static Base64Encoder ENCODER = new Base64Encoder();
	private final static double THUMBNAIL_WIDTH = 150;

	@Override
	public boolean chk(Sample sample, Path file) {
		if(sample.name().toLowerCase().endsWith("png") ||
		   sample.name().toLowerCase().endsWith("jpg") ||
		   sample.name().toLowerCase().endsWith("jpeg") ||
		   sample.name().toLowerCase().endsWith("gif") ||
		   sample.name().toLowerCase().endsWith("bmp")) try {
			return ImageIO.read(file.toFile())!=null;
		} catch(IOException e) {
			e.printStackTrace();
			return false;
		} else return false;
	}

	@Override
	public Sample process(Sample sample, Path file) {
		try {
			sample.page(1).size(file.toFile().length());
			var original = ImageIO.read(file.toFile());
			var scaled = original.getScaledInstance((int)Math.round(THUMBNAIL_WIDTH), (int)Math.round(THUMBNAIL_WIDTH*original.getHeight()/original.getWidth()), BufferedImage.SCALE_SMOOTH);
			var thumbnail = new BufferedImage(scaled.getWidth(null), scaled.getHeight(null), BufferedImage.TYPE_INT_ARGB);
			thumbnail.getGraphics().drawImage(scaled, 0, 0, null);
			thumbnail.getGraphics().dispose();
			var baos = new ByteArrayOutputStream();
			ImageIOUtil.writeImage(thumbnail, "png", baos);
			var img = baos.toByteArray();
			baos.close();
			baos = new ByteArrayOutputStream();
			ENCODER.encode(img, 0, img.length, baos);
			sample.thumbnail(baos.toString());
			baos.close();
			return sample;
		} catch(IOException e) {
			e.printStackTrace();
			return null;
		}
	}
}
