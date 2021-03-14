package net.sayaya.document.modeler.sample.processor;

import net.sayaya.document.modeler.sample.Sample;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.tools.imageio.ImageIOUtil;
import org.bouncycastle.util.encoders.Base64Encoder;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;

@Service
public class PreprocessorPdf implements Preprocessor {
	private final static byte[] PDF_HEADER = new byte[]{0x25,0x50,0x44,0x46,0x2D};
	private final static Base64Encoder encoder = new Base64Encoder();

	@Override
	public boolean chk(Sample sample, Path file) {
		if(sample.name().toLowerCase().endsWith("pdf")) try {
			var channel = FileChannel.open(file, StandardOpenOption.READ);
			var buffer = ByteBuffer.allocate(5);
			channel.read(buffer);
			channel.close();
			return Arrays.equals(buffer.array(), PDF_HEADER);
		} catch(IOException e) {
			e.printStackTrace();
			return false;
		} else return false;
	}

	@Override
	public Sample process(Sample sample, Path file) {
		try {
			PDDocument doc = PDDocument.load(file.toFile());
			sample.page(doc.getPages().getCount()).size(file.toFile().length());
			if(doc.getPages().getCount() > 0) {
				var baos = new ByteArrayOutputStream();
				ImageIOUtil.writeImage(new PDFRenderer(doc).renderImageWithDPI(0, 100, ImageType.RGB), "png", baos);
				var img = baos.toByteArray();
				baos.close();
				baos = new ByteArrayOutputStream();
				encoder.encode(img, 0, img.length, baos);
				sample.thumbnail(baos.toString());
				baos.close();
			}
			return sample;
		} catch(IOException e) {
			e.printStackTrace();
			return null;
		}
	}
}
