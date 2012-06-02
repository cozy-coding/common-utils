package com.suiveg.utils.image;

import com.suiveg.utils.abs.AbstractUtil;
import com.suiveg.utils.exceptions.NotImplementedException;
import com.suiveg.utils.image.model.ImageElement;
import com.suiveg.utils.image.model.MediaType;
import org.apache.log4j.Logger;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.ImageOutputStream;
import java.awt.*;
import java.awt.color.ColorSpace;
import java.awt.geom.AffineTransform;
import java.awt.image.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.suiveg.utils.file.FileUtils.getFileName;
import static com.suiveg.utils.image.model.ImageElement.determineSizeOfBufferedImage;
import static com.suiveg.utils.image.model.ImageElement.determineTransparencyType;

/**
 * Simple class that handles image-handling. This code is compiled from:
 * - Open Source frameworks
 * - Self-made crap
 * - Java native
 *
 *
 * Will be used to scale, refactor, format, filter and more!
 *
 * @author <a href="mailto:vegaasen@gmail.com">Vegard Aasen</a>
 * @author <a href="mailto:marius.kristensen@gmail.com">Marius Kristensen</a>
 * @version see system.properties
 * @since 0.1
 */
public class ImageUtils extends AbstractUtil {

    private static final Logger LOGGER = Logger.getLogger(ImageUtils.class);
    private static final String M_TARGET_TYPE_OF = "Target is <%s>. Writing to this.";
    private static final int O_Y = 0, O_X = 0;
    public static final float[] SHARPEN_KERNEL_FILTER_VEAA =
            {0.f, -1.f, 0.f, -1.f, 5.f, -1.f, 0.f, -1.f, 0.f};
    public static final float[] SHARPEN_KERNEL_FILTER_STD =
            {-1, -1, -1, -1, 9, -1, -1, -1, -1};
    public static final float[] BLUR_KERNEL_FILTER_STD =
            {1f/9f, 1f/9f, 1f/9f, 1f/9f, 1f/9f, 1f/9f, 1f/9f, 1f/9f, 1f/9f};
    public static final float[] BLUR_KERNEL_FILTER_3x3_TEST =
            {0.1f, 0.1f, 0.1f, 0.1f, 0.2f, 0.1f, 0.1f, 0.1f, 0.1f};

    private int height;
    private int width;
    private Object source;
    private boolean autoScale = true;

    private ImageType imageType = ImageUtils.ImageType.JPG;
    private Map<RenderingHints.Key, Object> renderingHints;
    private ImageProcessing imageProcessing = ImageUtils.ImageProcessing.NONE;

    public ImageUtils() {
        super();
    }

    /**
     * Simple ImageProperty fetcher based on URL / ImageType
     *
     * @param url       _
     * @param imageType _
     * @return _
     * @throws NullPointerException _
     * @throws IOException          _
     */
    public static ImageElement getImageProperties(final URL url, final ImageType imageType)
            throws NullPointerException, IOException {
        if (verifyNotNull(url, imageType)) {
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            final long lastModified = connection.getLastModified();
            final BufferedImage bufferedImage = ImageIO.read(connection.getInputStream());
            final String mimeType = connection.getHeaderField("Content-Type");
            if (!verifyNotNull(bufferedImage)) {
                throw new IOException("Unable to load image from resource (URL)");
            }
            connection.disconnect();
            return fetchImageProperties(bufferedImage,
                    imageType,
                    getFileName(url.getFile(), true),
                    new Date(lastModified),
                    mimeType);
        }
        throw new NullPointerException("URL or ImageType was null");
    }

    /**
     * Returns ImageElement based on a BufferedImage
     * (will not return modified, nor created date!)
     *
     * @param bufferedImage _
     * @param imageType     _
     * @return _
     * @throws NullPointerException _
     * @throws IOException          _
     */
    public static ImageElement getImageProperties(final BufferedImage bufferedImage, final ImageType imageType)
            throws NullPointerException, IOException {
        if (verifyNotNull(bufferedImage, imageType)) {
            fetchImageProperties(bufferedImage, imageType, null, null, null);
        }
        throw new NullPointerException(E_OBJECT_WAS_NULL);
    }

    /**
     * Convert a source to a BufferedImage.
     * Source supported:
     * File,BufferedImage,InputStream,URL,ImageInputStream, byte[]
     *
     * @param imageType the ImageType to use
     * @param source    source to generate BufferedImage from
     * @return Enhanced BufferedImage
     * @throws NullPointerException          _
     * @throws IOException                   _
     * @throws UnsupportedOperationException throws this is the source is of unsupported type
     */
    public static <T> BufferedImage convertImageType(final ImageType imageType, final T source)
            throws NullPointerException, IOException, UnsupportedOperationException {
        if (verifyNotNull(imageType, source)) {
            BufferedImage target = null;
            if (source instanceof File) {
                target = convert(ImageIO.read((File) source), imageType);
            } else if (source instanceof BufferedImage) {
                target = convert((BufferedImage) source, imageType);
            } else if (source instanceof InputStream) {
                target = convert(ImageIO.read((InputStream) source), imageType);
            } else if (source instanceof URL) {
                target = convert(ImageIO.read((URL) source), imageType);
            } else if (source instanceof ImageInputStream) {
                target = convert(ImageIO.read((ImageInputStream) source), imageType);
            } else if (source instanceof byte[]) {
                final InputStream streamOfInput = new ByteArrayInputStream((byte[]) source);
                target = convert(ImageIO.read(streamOfInput), imageType);
            } else {
                throw new UnsupportedOperationException("%s is not supported. Read JavaDoc.");
            }
            if (verifyNotNull(target)) {
                LOGGER.info(
                        String.format("Returning requested converted object<%s> as target", target.getClass().getName())
                );
                return target;
            }
            throw new NullPointerException("Return value was null.");
        }
        throw new NullPointerException("Nilled param detected. Please verify your params!");
    }

    /**
     * Scale a provided image. Please note, not everything is supported as input, even it is passed as generic..
     * Soon: byte[] ++
     *
     * @param target    target to write to
     * @param scaleType the scaleUtility that should be used
     * @return the type of target that has been specified
     * @throws NullPointerException _
     * @throws IOException          _
     */
    public <T> T scaleImage(T target, final ImageScaleType scaleType)
            throws NullPointerException,
            IOException {
        if (verifyNotNull(source)) {
            BufferedImage loadedImage = convertToBufferedImage(target);
            if(verifyNotNull(loadedImage)) {
                scale(scaleType, loadedImage, target);
            }else{
                throw new NullPointerException("Image was null");
            }
            if (verifyNotNull(target)) {
                LOGGER.info("Returning object target");
                return target;
            }
            LOGGER.info("Target was null");
            throw new NullPointerException("Unable to load image object from source");
        }
        LOGGER.info("Missing substantial parameter <source>.");
        throw new NullPointerException(E_OBJECT_WAS_NULL);
    }

    /**
     * Simple image flipper. Can flip image either vertically, horizontally or both directions
     *
     * @param source _
     * @param flipType _
     * @return BufferedImage
     * @throws NullPointerException _
     * @throws IllegalArgumentException _
     */
    public static <T> BufferedImage flipImage(final T source, final FlipType flipType)
            throws NullPointerException, IllegalArgumentException, IOException {
        if (verifyNotNull(source, flipType)) {
            BufferedImage sourceImage = convertToBufferedImage(source);
            BufferedImage target = new BufferedImage(
                    sourceImage.getWidth(),
                    sourceImage.getHeight(),
                    sourceImage.getType());
            AffineTransform affineTransform;
            AffineTransformOp affineTransformOp;
            if (flipType.equals(FlipType.HORIZONTAL) ||
                    flipType.equals(FlipType.BOTH)) {
                affineTransform = AffineTransform.getScaleInstance(1, -1);
                affineTransform.translate(-sourceImage.getWidth(), 0);
                affineTransformOp = new AffineTransformOp(affineTransform, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
                target = affineTransformOp.filter(sourceImage, target);
            }
            if (flipType.equals(FlipType.VERTICAL) ||
                    flipType.equals(FlipType.BOTH)) {
                affineTransform = AffineTransform.getScaleInstance(1, -1);
                affineTransformOp = new AffineTransformOp(affineTransform, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
                affineTransform.translate(0, -sourceImage.getHeight());
                target = affineTransformOp.filter(sourceImage, target);
            }
            return target;
        }
        throw new NullPointerException(E_OBJECT_WAS_NULL);
    }

    /**
     * Simple method to use if one wants to change the current color scheme to e.g grayscale, black/white and so on
     *
     * @param source source image object
     * @param colorSpaceType color space type. see java.awt.color.ColorSpace.<TYPES>
     * @param <T> type
     * @return BufferedImage
     * @throws IOException _
     * @throws NullPointerException _
     */
    public static <T> BufferedImage convertColorScheme(final T source, final Integer colorSpaceType)
            throws IOException, NullPointerException {
        if (verifyNotNull(source, colorSpaceType)) {
            BufferedImage sourceImage;
            sourceImage = convertToBufferedImage(source);
            BufferedImage target = new BufferedImage(
                    sourceImage.getWidth(),
                    sourceImage.getHeight(),
                    sourceImage.getType());
            if (verifyNotNull(sourceImage)) {
                ColorSpace colorSpace = ColorSpace.getInstance(colorSpaceType);
                ColorConvertOp converter = new ColorConvertOp(colorSpace, null);
                target = converter.filter(sourceImage, target);
                if (verifyNotNull(target)) {
                    return target;
                }
                throw new NullPointerException("Unable to create target image. Result was null");
            }
        }
        throw new NullPointerException(E_OBJECT_WAS_NULL);
    }

    /**
     * Simple converter from the types:
     * -File
     * -InputStream
     * -ImageInputStream
     * -URL
     *
     * @param convertibleObject object to be converted
     * @return BufferedImage
     * @throws IOException _
     * @throws IllegalArgumentException If an unsupported type is being parametrised, throws an error
     */
    public static BufferedImage convertToBufferedImage(final Object convertibleObject)
            throws IOException, IllegalArgumentException {
        if (verifyNotNull(convertibleObject)) {
            if (convertibleObject instanceof File) return ImageIO.read((File) convertibleObject);
            else if (convertibleObject instanceof InputStream) return ImageIO.read((InputStream) convertibleObject);
            else if (convertibleObject instanceof ImageInputStream) return ImageIO.read((ImageInputStream) convertibleObject);
            else if (convertibleObject instanceof URL) return ImageIO.read((URL) convertibleObject);
            else if (convertibleObject instanceof BufferedImage) return (BufferedImage) convertibleObject;
            else
                throw new IllegalArgumentException(String.format("Unsupported object. Please provide one of the following: \n" +
                        "File, InputStream, ImageInputStream, URL. You provided %s.", convertibleObject.getClass().getName()));
        }
        throw new NullPointerException(E_OBJECT_WAS_NULL);
    }

    /**
     * Use this method to easily blur your image. If the customBlurFilter property has not been provided,
     * we will use the filter: BLUR_KERNEL_FILTER_STD and a 3x3 mask. This can easily be overridden by the following
     * Kernel = new Kernel(N, N, MASK); We have defined two masks for you to play around with. These are:
     *  BLUR_KERNEL_FILTER_STD
     *  BLUR_KERNEL_FILTER_3x3_TEST
     *
     * If you find these somewhat crappy/not working - please send us a feedback, and we'll try your recommendation
     * OR: Comment inline in the google.code.com-code.
     *
     * @param source can be what not
     * @param customBlurFilter - you can either include your own Kernel-filter, or provide null. Null means that
     * the best filter will be chosen for your output file
     * @return BufferedImage Image that has been modified
     * @throws IOException _
     * @throws NullPointerException _
     */
    public static <T> BufferedImage blurImage(final T source, Kernel customBlurFilter)
            throws IOException, NullPointerException {
        if(verifyNotNull(source)) {
            if(!verifyNotNull(customBlurFilter)) {
                customBlurFilter = new Kernel(3, 3, BLUR_KERNEL_FILTER_STD);
            }
            return applyKernelFilter(convertToBufferedImage(source), customBlurFilter);
        }
        throw new NullPointerException(E_OBJECT_WAS_NULL);
    }

    /**
     * Use this method to easily sharpen your image. If the customSharpenFilter property has not been provided,
     * we will use the filter: SHARPEN_KERNEL_FILTER_VEAA and a 3x3 mask. This can easily be overridden by the following
     * Kernel = new Kernel(N, N, MASK); We have defined two masks for you to play around with. These are:
     *  SHARPEN_KERNEL_FILTER_VEAA
     *  SHARPEN_KERNEL_FILTER_STD
     *
     * If you find these somewhat crappy/not working - please send us a feedback, and we'll try your recommendation
     * OR: Comment inline in the google.code.com-code.
     *
     * @param source can be what not
     * @param customSharpenFilter - you can either include your own Kernel-filter, or provide null. Null means that
     * the best filter will be chosen for your output file
     * @return BufferedImage Image that has been modified
     * @throws IOException _
     * @throws NullPointerException _
     */
    public static <T> BufferedImage sharpenImage(final T source, Kernel customSharpenFilter)
            throws IOException, NullPointerException {
        if(verifyNotNull(source)) {
            if(!verifyNotNull(customSharpenFilter)) {
                customSharpenFilter = new Kernel(3, 3, SHARPEN_KERNEL_FILTER_VEAA);
            }
            return applyKernelFilter(convertToBufferedImage(source), customSharpenFilter);
        }
        throw new NullPointerException(E_OBJECT_WAS_NULL);
    }

    private static BufferedImage applyKernelFilter (final BufferedImage source, Kernel customFilter)
            throws IOException, NullPointerException {
        if(verifyNotNull(source)) {
            RenderingHints requiredRenderingHintsForQuality = new RenderingHints(
                    generateRenderingHintsBasedOnProperties(ImageProcessing.HIGH_BEST_SLOW)
            );
            ConvolveOp bufferedImageOperations =
                    new ConvolveOp(customFilter, ConvolveOp.EDGE_NO_OP, requiredRenderingHintsForQuality);
            BufferedImage target = new BufferedImage(source.getWidth(), source.getHeight(), source.getType());
            target = bufferedImageOperations.filter(source, target);
            if(verifyNotNull(target)) {
                return target;
            }
            throw new NullPointerException("Internal: Target was null");
        }
        throw new NullPointerException(E_OBJECT_WAS_NULL);
    }

    private <T> T scale(final ImageScaleType scaleType, BufferedImage sourceImage, T target) {
        if (verifyNotNull(scaleType)) {
            switch (scaleType) {
                case AffineTransformation:
                    LOGGER.info("Using AffineTransformation");
                    target = scaleImageUsingAffineTransformation(sourceImage, target);
                    break;
                case Graphics2D:
                    LOGGER.info("Using Graphics2D.draw()");
                    target = scaleImageUsingGraphics2D(sourceImage, target);
                    break;
                default:
                    LOGGER.info("Using 'Default', which is AffineTransformation");
                    target = scaleImageUsingAffineTransformation(sourceImage, target);
                    break;
            }
            return target;
        }
        return null;
    }

    private <T> T scaleImageUsingAffineTransformation(final BufferedImage bufferedImage, T target) {
        BufferedImage destinationImage = generateDestinationImage();
        Graphics2D graphics2D = destinationImage.createGraphics();
        AffineTransform transformation = AffineTransform.getScaleInstance(
                ((double) getQualifiedWidth() / bufferedImage.getWidth()),
                ((double) getQualifiedHeight() / bufferedImage.getHeight()));
        graphics2D.drawRenderedImage(bufferedImage, transformation);
        graphics2D.addRenderingHints(retrieveRenderingHints());
        try {
            if (target instanceof File) {
                LOGGER.info(String.format(M_TARGET_TYPE_OF, "File"));
                ImageIO.write(destinationImage, imageType.toString(), (File) target);
            } else if (target instanceof ImageOutputStream) {
                LOGGER.info(String.format(M_TARGET_TYPE_OF, "ImageOutputStream"));
                ImageIO.write(destinationImage, imageType.toString(), (ImageOutputStream) target);
            } else if (target instanceof OutputStream) {
                LOGGER.info(String.format(M_TARGET_TYPE_OF, "OutputStream"));
                ImageIO.write(destinationImage, imageType.toString(), (OutputStream) target);
            } else {
                target = null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return target;
    }

    @SuppressWarnings("unchecked")
    private <T> T scaleImageUsingGraphics2D(final BufferedImage bufferedImage, T target) {
        BufferedImage destinationImage = generateDestinationImage();
        Graphics2D graphics2D = destinationImage.createGraphics();
        graphics2D.addRenderingHints(retrieveRenderingHints());
        graphics2D.drawImage(bufferedImage, O_X, O_Y, getQualifiedWidth(), getQualifiedHeight(), null);
        graphics2D.dispose();
        target = (T) destinationImage;
        return target;
    }

    private <T> T scaleImageUsingRescaleOp(final BufferedImage bufferedImage, T target) throws NotImplementedException {
        throw new NotImplementedException(NotImplementedException.METHOD_NOT_IMPLEMENTED_MESSAGE);
    }

    private Map<RenderingHints.Key, Object> retrieveRenderingHints() {
        if (!renderingHints.isEmpty() && (imageProcessing.equals(ImageProcessing.NONE) || verifyNull(imageProcessing))) {
            return renderingHints;
        } else if (verifyNotNull(imageProcessing) &&
                !imageProcessing.equals(ImageUtils.ImageProcessing.NONE)) {
            return generateRenderingHintsBasedOnProperties(this.imageProcessing);
        }
        return Collections.emptyMap();
    }

    private static Map<RenderingHints.Key, Object> generateRenderingHintsBasedOnProperties(ImageProcessing imageProcessing) {
        Map<RenderingHints.Key, Object> setOfRenderingHints = new HashMap<RenderingHints.Key, Object>();
        switch (imageProcessing) {
            case HIGH_BEST_SLOW:
                setOfRenderingHints.put(RenderingHints.KEY_TEXT_ANTIALIASING,
                        RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HBGR);
                setOfRenderingHints.put(RenderingHints.KEY_DITHERING,
                        RenderingHints.VALUE_DITHER_ENABLE);
                setOfRenderingHints.put(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
                setOfRenderingHints.put(RenderingHints.KEY_RENDERING,
                        RenderingHints.VALUE_RENDER_QUALITY);
                setOfRenderingHints.put(RenderingHints.KEY_ALPHA_INTERPOLATION,
                        RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
                setOfRenderingHints.put(RenderingHints.KEY_INTERPOLATION,
                        RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
                break;
            case HIGH_MEDIUM:
                setOfRenderingHints.put(RenderingHints.KEY_TEXT_ANTIALIASING,
                        RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                setOfRenderingHints.put(RenderingHints.KEY_DITHERING,
                        RenderingHints.VALUE_DITHER_ENABLE);
                setOfRenderingHints.put(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
                setOfRenderingHints.put(RenderingHints.KEY_ALPHA_INTERPOLATION,
                        RenderingHints.VALUE_ALPHA_INTERPOLATION_DEFAULT);
                setOfRenderingHints.put(RenderingHints.KEY_INTERPOLATION,
                        RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                break;
            case MEDIUM_MEDIUM:
                setOfRenderingHints.put(RenderingHints.KEY_TEXT_ANTIALIASING,
                        RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
                setOfRenderingHints.put(RenderingHints.KEY_DITHERING,
                        RenderingHints.VALUE_DITHER_ENABLE);
                setOfRenderingHints.put(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
                setOfRenderingHints.put(RenderingHints.KEY_INTERPOLATION,
                        RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                break;
            case LOW_MEDIUM:
                break;
            case LOW_LOW_FAST:
                break;
            case BAD_BEST_FAST:
                break;
            case BAD_NORMAL_FASTEST:
                setOfRenderingHints.put(RenderingHints.KEY_TEXT_ANTIALIASING,
                        RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
                setOfRenderingHints.put(RenderingHints.KEY_DITHERING,
                        RenderingHints.VALUE_DITHER_DISABLE);
                setOfRenderingHints.put(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_OFF);
                setOfRenderingHints.put(RenderingHints.KEY_INTERPOLATION,
                        RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                setOfRenderingHints.put(RenderingHints.KEY_RENDERING,
                        RenderingHints.VALUE_RENDER_SPEED);
                setOfRenderingHints.put(RenderingHints.KEY_ALPHA_INTERPOLATION,
                        RenderingHints.VALUE_ALPHA_INTERPOLATION_SPEED);
                break;
            default:
                break;
        }
        return setOfRenderingHints;
    }

    private BufferedImage generateDestinationImage() {
        return new BufferedImage(
                getQualifiedWidth(),
                getQualifiedHeight(),
                getQualifiedType());
    }

    private int getQualifiedHeight() {
        if (height > 0) {
            return height;
        } else {
            if (source instanceof BufferedImage) {
                height = ((BufferedImage) source).getHeight();
            }
            if (autoScale) {
                if (source instanceof BufferedImage) {
                    int originalWidth = ((BufferedImage) source).getWidth();
                    if (originalWidth > width) {
                        height = (height * Math.abs((width / originalWidth)));
                    }
                    return height;
                }
            } else {
                return height;
            }
        }
        return 0;
    }

    private int getQualifiedWidth() {
        if (width > 0) {
            return width;
        } else {
            if (source instanceof BufferedImage) {
                height = ((BufferedImage) source).getWidth();
            }
            if (autoScale) {
                if (source instanceof BufferedImage) {
                    int originalHeight = ((BufferedImage) source).getHeight();
                    if (originalHeight > height) {
                        width = (width * Math.abs((height / originalHeight)));
                    }
                    return width;
                }
            } else {
                return width;
            }
        }
        return 0;
    }

    private int getQualifiedType() {
        if (verifyNotNull(source)) {
            if (source instanceof BufferedImage) {
                return ((BufferedImage) source).getType();
            }
        }
        return BufferedImage.TYPE_INT_ARGB;
    }

    private static BufferedImage convert(final BufferedImage source, final ImageType imageType) throws NullPointerException, IOException {
        if (verifyNotNull(source)) {
            File targetedFile = null;
            ImageIO.write(source, imageType.toString(), targetedFile);
            if (verifyNotNull(targetedFile)) {
                return ImageIO.read(targetedFile);
            }
        }
        throw new NullPointerException("Something bad happened. You should never try that again. Ever.");
    }

    private static ImageElement fetchImageProperties(
            final BufferedImage source,
            final ImageType imageType,
            final String id,
            final Date modifiedDate,
            final String mimeType)
            throws NullPointerException, IOException {
        if (verifyNotNull(source)) {
            ImageElement image = new ImageElement();
            image.setBitDepth(verifyNotNull(source.getColorModel().getPixelSize()) ?
                    source.getColorModel().getPixelSize() :
                    0);
            image.setHeight(verifyNotNull(source.getHeight()) ? source.getHeight() : 0);
            image.setWidth(verifyNotNull(source.getWidth()) ? source.getWidth() : 0);
            image.setSizeInBytes(determineSizeOfBufferedImage(source, imageType.toString()));
            image.setCreated(new Date(System.currentTimeMillis()));
            image.setTransparent(determineTransparencyType(source.getGraphics().getColor().getTransparency()));
            if (verifyNotNull(modifiedDate)) image.setModified(modifiedDate);
            image.setId((verifyNotNull(id) ? id : ""));
            image.setFontType(
                    verifyNotNull(source.getGraphics().getFont()) ?
                            source.getGraphics().getFont() :
                            new Font("Default", 0, 0));
            image.setMediaType(MediaType.Missing);
            return image;
        }
        throw new NullPointerException(E_OBJECT_WAS_NULL);
    }

    public void setHeight(final int height) {
        this.height = height;
    }

    public void setWidth(final int width) {
        this.width = width;
    }

    public void setSource(final Object source) {
        this.source = source;
    }

    public void setAutoScale(final boolean autoScale) {
        this.autoScale = autoScale;
    }

    public void setImageType(final ImageType imageType) {
        this.imageType = imageType;
    }

    private enum ReturnFormatType {
        File, ImageOutputStream, OutputStream
    }

    public void setRenderingHints(final Map<RenderingHints.Key, Object> renderingHints) {
        if (verifyRenderingHints(renderingHints)) {
            this.renderingHints = renderingHints;
        }
    }

    public void setImageProcessing(ImageProcessing imageProcessing) {
        this.imageProcessing = imageProcessing;
    }

    private boolean verifyRenderingHints(final Map<?, ?> renderingHints) {
        return true;
    }

    public enum ImageScaleType {
        AffineTransformation,
        Graphics2D
    }

    public enum ImageProcessing {
        HIGH_BEST_SLOW, HIGH_MEDIUM, MEDIUM_MEDIUM, LOW_MEDIUM, LOW_LOW_FAST, BAD_BEST_FAST, BAD_NORMAL_FASTEST, NONE
    }

    public enum ImageType {
        JPG("JPG"),
        PNG("PNG"),
        BMP("BMP");

        private String imageType;

        ImageType(String imageType) {
            this.imageType = imageType;
        }

        @Override
        public String toString() {
            return imageType;
        }
    }

    public enum FlipType {
        HORIZONTAL,
        VERTICAL,
        BOTH
    }

}
