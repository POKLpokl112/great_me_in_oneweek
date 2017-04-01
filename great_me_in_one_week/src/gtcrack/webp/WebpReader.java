package gtcrack.webp;

import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;

import javax.imageio.IIOException;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.FileImageInputStream;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.MemoryCacheImageInputStream;

import gtcrack.GTUtil;
import net.sf.javavp8decoder.imageio.WebPImageReaderSpi;

public class WebpReader {

	private static final WebPImageReaderSpi wp = new WebPImageReaderSpi();

	public static BufferedImage readByte(final byte[] bytes) throws Exception {

		final InputStream input = new BufferedInputStream(new ByteArrayInputStream(bytes));

		final BufferedImage bfi = ImageIO.read(input);

		if (bfi != null) {
			return bfi;
		}

		return readInputStream(new MemoryCacheImageInputStream(input));

	}

	public static BufferedImage readFile(final File file) throws Exception {

		final BufferedImage bfi = ImageIO.read(file);

		if (bfi != null) {
			return bfi;
		}

		return readInputStream(new FileImageInputStream(file));

	}

	public static BufferedImage readInputStream(final ImageInputStream InputStream) throws Exception {

		final ImageReader imageReader = wp.createReaderInstance();
		imageReader.setInput(InputStream);

		BufferedImage bufferedImage = null;
		int i = 0;

		try {
			while (bufferedImage == null) {
				try {
					bufferedImage = imageReader.read(i);
				} catch (final IIOException e) {

					i++;
					if (i == 5) {
						throw e;
					}
				}
			}
		} catch (final IIOException e) {

			bufferedImage = ImageIO.read(InputStream);
			if (bufferedImage == null) {
				throw e;
			}
		}

		return bufferedImage;

	}

	public static void main(final String[] args) throws Exception {
		final BufferedImage b = WebpReader
				.readByte(GTUtil.loadUrlContent("http://static.geetest.com/pictures/gt/a87ff679a/bg/cab8c4563.webp"));
		System.out.println(b);
	}

}
