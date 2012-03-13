package com.suiveg.utils.image;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.mockpolicies.Log4jMockPolicy;
import org.powermock.core.classloader.annotations.MockPolicy;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import static org.easymock.EasyMock.createNiceMock;
import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.powermock.api.easymock.PowerMock.*;

/**
 * Test for
 * @see com.suiveg.utils.image.ImageUtils
 *
 * @author <a href="mailto:vegaasen@gmail.com">Vegard Aasen</a>
 * @author <a href="mailto:marius.kristensen@gmail.com">Marius Kristensen</a>
 *
 * @since 0.1
 * @version see system.properties
 */
@RunWith(PowerMockRunner.class)
@MockPolicy(Log4jMockPolicy.class)
@PrepareForTest({ImageIO.class, AffineTransform.class, ImageUtils.class})
public class ImageUtilsTest {

    private ImageUtils mockImageUtil;
    private AffineTransform mockAffineTransform;

    @Before
    public void setUp() {
        mockStatic(ImageIO.class);
        mockStatic(AffineTransform.class);
    }

    @Test
    public void scaleImage_UseFileAsTarget_JPG() throws Exception {
        mockImageUtil = createPartialMock(ImageUtils.class, "scaleImageUsingAffineTransformation", BufferedImage.class, File.class);

        BufferedImage mockBuffSource = createNiceMock(BufferedImage.class);
        File mockFile = createMock(File.class);

        Whitebox.setInternalState(mockImageUtil, "autoScale", true);
        Whitebox.setInternalState(mockImageUtil, "width", 1000);
        Whitebox.setInternalState(mockImageUtil, "imageType", ImageUtils.ImageType.JPG);
        Whitebox.setInternalState(mockImageUtil, "source", mockBuffSource);

        expect(ImageIO.read(mockFile)).andReturn(mockBuffSource);

        expectPrivate(mockImageUtil, "scaleImageUsingAffineTransformation", mockBuffSource, mockFile)
                .andReturn(mockBuffSource);

        expect(mockBuffSource.getWidth()).andReturn(1000).anyTimes();
        expect(mockBuffSource.getHeight()).andReturn(500).anyTimes();
        expect(mockBuffSource.getType()).andReturn(BufferedImage.TYPE_INT_RGB);

        replayAll();

        File f = mockImageUtil.scaleImage(mockFile, ImageUtils.ImageScaleType.AffineTransformation);
        assertNotNull(f);
        
        verifyAll();
    }

    @Test
    public void scaleImage_UseFileAsTarget_PNG() throws Exception {
        mockImageUtil = createPartialMock(ImageUtils.class, "scaleImageUsingAffineTransformation", BufferedImage.class, File.class);

        BufferedImage mockBuffSource = createNiceMock(BufferedImage.class);
        File mockFile = createMock(File.class);

        Whitebox.setInternalState(mockImageUtil, "autoScale", true);
        Whitebox.setInternalState(mockImageUtil, "width", 1000);
        Whitebox.setInternalState(mockImageUtil, "imageType", ImageUtils.ImageType.PNG);
        Whitebox.setInternalState(mockImageUtil, "source", mockBuffSource);

        expect(ImageIO.read(mockFile)).andReturn(mockBuffSource);

        expectPrivate(mockImageUtil, "scaleImageUsingAffineTransformation", mockBuffSource, mockFile)
                .andReturn(mockBuffSource);

        expect(mockBuffSource.getWidth()).andReturn(1000).anyTimes();
        expect(mockBuffSource.getHeight()).andReturn(500).anyTimes();
        expect(mockBuffSource.getType()).andReturn(BufferedImage.TYPE_INT_RGB);

        replayAll();

        File f = mockImageUtil.scaleImage(mockFile, ImageUtils.ImageScaleType.AffineTransformation);
        assertNotNull(f);

        verifyAll();
    }

    @Test(expected = NullPointerException.class)
    public void scaleImage_UseTargetThatIsNull() throws Exception {
        mockImageUtil = createPartialMock(ImageUtils.class, "scaleImageUsingAffineTransformation", BufferedImage.class, File.class);

        BufferedImage mockBuffSource = null;
        File mockFile = null;

        Whitebox.setInternalState(mockImageUtil, "autoScale", true);
        Whitebox.setInternalState(mockImageUtil, "width", 1000);
        Whitebox.setInternalState(mockImageUtil, "imageType", ImageUtils.ImageType.PNG);
        Whitebox.setInternalState(mockImageUtil, "source", mockBuffSource);

        replayAll();

        mockImageUtil.scaleImage(mockFile, ImageUtils.ImageScaleType.AffineTransformation);
    }

    @Test(expected = NullPointerException.class)
    public void scaleImage_ReturnThatWasNull() throws Exception {
        mockImageUtil = createPartialMock(ImageUtils.class, "scale", ImageUtils.ImageScaleType.class, BufferedImage.class, File.class);

        BufferedImage mockBuffSource = createNiceMock(BufferedImage.class);
        File mockFile = null;

        Whitebox.setInternalState(mockImageUtil, "autoScale", true);
        Whitebox.setInternalState(mockImageUtil, "width", 1000);
        Whitebox.setInternalState(mockImageUtil, "imageType", ImageUtils.ImageType.PNG);
        Whitebox.setInternalState(mockImageUtil, "source", mockBuffSource);

        expectPrivate(mockImageUtil, "scale", ImageUtils.ImageScaleType.AffineTransformation, mockBuffSource, mockFile)
                .andReturn(null);

        replayAll();

        mockImageUtil.scaleImage(mockFile, ImageUtils.ImageScaleType.AffineTransformation);
    }

    @After
    public void tearDown() {
        mockImageUtil = null;
    }
}
