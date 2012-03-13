package com.suiveg.utils.image.model;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Date;

/**
 * Simple model that contains Image properties
 *
 * @since see system.properties
 */
public class ImageElement {

    private String id;
    private String name;
    private MediaType mediaType;
    private long sizeInBytes;
    private int width;
    private int height;
    private int bitDepth;
    private int horizontalResolution;
    private int verticalResolution;
    private Date created;
    private Date modified;
    private TransparencyType transparent;
    private Font fontType;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public MediaType getMediaType() {
        return mediaType;
    }

    public void setMediaType(MediaType mediaType) {
        this.mediaType = mediaType;
    }

    public long getSizeInBytes() {
        return sizeInBytes;
    }

    public void setSizeInBytes(long sizeInBytes) {
        this.sizeInBytes = sizeInBytes;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getBitDepth() {
        return bitDepth;
    }

    public void setBitDepth(int bitDepth) {
        this.bitDepth = bitDepth;
    }

    public int getHorizontalResolution() {
        return horizontalResolution;
    }

    public void setHorizontalResolution(int horizontalResolution) {
        this.horizontalResolution = horizontalResolution;
    }

    public int getVerticalResolution() {
        return verticalResolution;
    }

    public void setVerticalResolution(int verticalResolution) {
        this.verticalResolution = verticalResolution;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Date getModified() {
        return modified;
    }

    public void setModified(Date modified) {
        this.modified = modified;
    }

    public TransparencyType getTransparent() {
        return transparent;
    }

    public void setTransparent(TransparencyType transparent) {
        this.transparent = transparent;
    }

    public Font getFontType() {
        return fontType;
    }

    public void setFontType(Font fontType) {
        this.fontType = fontType;
    }

    public enum TransparencyType {
        OPAQUE,BIT_MASK,TRANSLUCENT, UNDEFINED
    }

    public static long determineSizeOfBufferedImage(final BufferedImage source, final String imageType) {
        try{
            ByteArrayOutputStream tmpStream = new ByteArrayOutputStream();
            ImageIO.write(source, imageType, tmpStream);
            tmpStream.close();
            return (tmpStream.size()>0L)?tmpStream.size():0L;
        }catch (IOException e) {
            e.printStackTrace(); 
        }
        return 0L;
    }

    public static TransparencyType determineTransparencyType(final int type){
        switch (type) {
            case 1:
                return ImageElement.TransparencyType.OPAQUE;
            case 2:
                return ImageElement.TransparencyType.BIT_MASK;
            case 3:
                return ImageElement.TransparencyType.TRANSLUCENT;
            default:
                return ImageElement.TransparencyType.UNDEFINED;
        }
    }

}
