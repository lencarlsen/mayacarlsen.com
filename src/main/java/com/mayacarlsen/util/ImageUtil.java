package com.mayacarlsen.util;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.PixelGrabber;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

public class ImageUtil {

    public static byte[] scaleImage(byte[] imageBytes, double width, boolean smooth) {
	try {
	    InputStream is = new ByteArrayInputStream(imageBytes);
	    BufferedImage bi = ImageIO.read(is);

	    double ratioInt = width / bi.getWidth();

	    byte[] scaledImageBytes = scaleImageAsBytes(bi, ratioInt, smooth);

	    return scaledImageBytes;
	} catch (IOException e) {
	    throw new RuntimeException(e);
	}
    };

    /**
     * Scales an image by <code>ratio</code>.
     * 
     * @param image Image to scale
     * @param ratio Ratio to scale image by
     * @return Byte array
     * @throws IOException Any image IO errors
     */
    public static byte[] scaleImageAsBytes(BufferedImage originalImage, double ratio, boolean smooth)
	    throws IOException {
	ImageType imageType = ImageType.UNKNOWN_IMAGE;
	BufferedImage scaledImage = scaleImage(originalImage, ratio, imageType, smooth);

	ByteArrayOutputStream baos = new ByteArrayOutputStream();
	ImageIO.write(scaledImage, "jpg", baos);

	return baos.toByteArray();
    }

    private static BufferedImage scaleImage(BufferedImage source, double ratio, ImageType imageType, boolean smooth) {
	int w = (int) (source.getWidth() * ratio);
	int h = (int) (source.getHeight() * ratio);

	BufferedImage bi;
	if (smooth) {
	    bi = createHeadlessSmoothBufferedImage(source, ImageType.UNKNOWN_IMAGE, w, h);
	} else {
	    bi = createHeadlessBufferedImage(source, ImageType.UNKNOWN_IMAGE, w, h);
	}

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

    public static BufferedImage createHeadlessSmoothBufferedImage(BufferedImage source, ImageType imageType, int width,
	    int height) {
	int type = BufferedImage.TYPE_INT_RGB;
	if (imageType == ImageType.PNG && hasAlpha(source)) {
	    type = BufferedImage.TYPE_INT_ARGB;
	}

	BufferedImage dest = new BufferedImage(width, height, type);

	int sourcex;
	int sourcey;

	double scalex = (double) width / source.getWidth();
	double scaley = (double) height / source.getHeight();

	int x1;
	int y1;

	double xdiff;
	double ydiff;

	int rgb;
	int rgb1;
	int rgb2;

	for (int y = 0; y < height; y++) {
	    sourcey = y * source.getHeight() / dest.getHeight();
	    ydiff = scale(y, scaley) - sourcey;

	    for (int x = 0; x < width; x++) {
		sourcex = x * source.getWidth() / dest.getWidth();
		xdiff = scale(x, scalex) - sourcex;

		x1 = Math.min(source.getWidth() - 1, sourcex + 1);
		y1 = Math.min(source.getHeight() - 1, sourcey + 1);

		rgb1 = getRGBInterpolation(source.getRGB(sourcex, sourcey), source.getRGB(x1, sourcey), xdiff);
		rgb2 = getRGBInterpolation(source.getRGB(sourcex, y1), source.getRGB(x1, y1), xdiff);

		rgb = getRGBInterpolation(rgb1, rgb2, ydiff);

		dest.setRGB(x, y, rgb);
	    }
	}

	return dest;
    }

    private static double scale(int point, double scale) {
	return point / scale;
    }

    private static int getRGBInterpolation(int value1, int value2, double distance) {
	int alpha1 = (value1 & 0xFF000000) >>> 24;
	int red1 = (value1 & 0x00FF0000) >> 16;
	int green1 = (value1 & 0x0000FF00) >> 8;
	int blue1 = (value1 & 0x000000FF);

	int alpha2 = (value2 & 0xFF000000) >>> 24;
	int red2 = (value2 & 0x00FF0000) >> 16;
	int green2 = (value2 & 0x0000FF00) >> 8;
	int blue2 = (value2 & 0x000000FF);

	int rgb = ((int) (alpha1 * (1.0 - distance) + alpha2 * distance) << 24) | ((int) (red1 * (1.0 - distance) + red2
		* distance) << 16) | ((int) (green1 * (1.0 - distance) + green2 * distance) << 8) | (int) (blue1 * (1.0
			- distance) + blue2 * distance);

	return rgb;
    }

}
