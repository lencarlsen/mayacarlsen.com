package com.mayacarlsen.util;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.PixelGrabber;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

public class ImageUtil {

    /**
     * Scales an image by <code>ratio</code>.
     * 
     * @param image Image to scale
     * @param ratio Ratio to scale image by
     * @return Byte array
     * @throws IOException Any image IO errors
     */
    public static byte[] scaleImageAsBytes(BufferedImage originalImage, double ratio) throws IOException {
	ImageType imageType = ImageType.UNKNOWN_IMAGE;
	BufferedImage scaledImage = scaleImage(originalImage, ratio, imageType);

	ByteArrayOutputStream baos = new ByteArrayOutputStream();
	ImageIO.write(scaledImage, "jpg", baos);

	return baos.toByteArray();
    }

    private static BufferedImage scaleImage(BufferedImage source, double ratio, ImageType imageType) {
	int w = (int) (source.getWidth() * ratio);
	int h = (int) (source.getHeight() * ratio);
	BufferedImage bi = createHeadlessBufferedImage(source, ImageType.UNKNOWN_IMAGE, w, h);
	Graphics2D g2d = bi.createGraphics();
	double xScale = (double) w / source.getWidth();
	double yScale = (double) h / source.getHeight();
	AffineTransform at = AffineTransform.getScaleInstance(xScale, yScale);
	g2d.drawRenderedImage(source, at);
	g2d.dispose();
	return bi;
    }

    public static String getFileType(Object obj) {
	try {
	    ImageInputStream iis = ImageIO.createImageInputStream(obj);
	    Iterator<ImageReader> readers = ImageIO.getImageReaders(iis);

	    ImageReader read = readers.next();
	    return read.getFormatName();
	} catch (IOException e) {
	    throw new RuntimeException(e);
	}
    }

    public enum ImageType {
	UNKNOWN_IMAGE, JPEG, PNG, GIF;

	public static ImageType getEnum(String value) {
	    for (ImageType type : values()) {
		if (type.name().equalsIgnoreCase(value.trim().toUpperCase())) {
		    return type;
		}
	    }
	    return ImageType.UNKNOWN_IMAGE;
	}
    };

    public static BufferedImage createHeadlessBufferedImage(final BufferedImage image, final ImageType imageType,
	    final int width, final int height) {
	int type = BufferedImage.TYPE_INT_RGB;
	if (imageType == ImageType.PNG && hasAlpha(image)) {
	    type = BufferedImage.TYPE_INT_ARGB;
	}

	BufferedImage bi = new BufferedImage(width, height, type);

	for (int y = 0; y < height; y++) {
	    for (int x = 0; x < width; x++) {
		bi.setRGB(x, y, image.getRGB(x * image.getWidth() / width, y * image.getHeight() / height));
	    }
	}

	return bi;
    }

    /**
     * Determines if the image has transparent pixels.
     * 
     * @param image The image to check for transparent pixel.s
     * @return <code>true</code> of <code>false</code>, according to the result
     */
    public static boolean hasAlpha(Image image) {
	try {
	    PixelGrabber pg = new PixelGrabber(image, 0, 0, 1, 1, false);
	    pg.grabPixels();

	    return pg.getColorModel().hasAlpha();
	} catch (InterruptedException e) {
	    return false;
	}
    }
}
