package com.suiveg.utils.properties;

import com.suiveg.utils.exceptions.NoSuchFileException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.powermock.api.easymock.mockpolicies.Log4jMockPolicy;
import org.powermock.core.classloader.annotations.MockPolicy;
import org.powermock.core.classloader.annotations.PrepareForTest;

import java.util.Properties;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Test for
 *
 * @author <a href="mailto:vegaasen@gmail.com">Vegard Aasen</a>
 * @author <a href="mailto:marius.kristensen@gmail.com">Marius Kristensen</a>
 * @see com.suiveg.utils.image.ImageUtils
 * @since 0.2-SNAPSHOT
 */
@MockPolicy(Log4jMockPolicy.class)
@PrepareForTest({PropUtils.class, ClassLoader.class})
public class PropUtilsTest {

    private static final String DEFAULT_VALUE_FROM_PROPERTIES_SET = "is that ok?";

    @Before
    public void setUp() {
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_missingPropertiesFileInput_failing() {
        PropUtils.loadProperties("");
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_missingPropertiesFileInputAndNoResourceBundle_failing() {
        PropUtils.loadProperties("", false);
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_missingPropertiesFileInputAndResourceBundle_failing() {
        PropUtils.loadProperties("", false);
    }

    @Test(expected = NoSuchFileException.class)
    public void test_missingPropertiesFileInputAndNoResourceBundleAndNullClassLOader_failing() throws NoSuchFileException {
        PropUtils.loadProperties("", null, false);
    }

    @Test()
    public void test_passingPropertiesFileNotExists_failing() {
        Properties p =PropUtils.loadProperties("system.propertiess");
        assertTrue(p==null);
    }

    @Test()
    public void test_passingPropertiesFileExists_failing() {
        Properties p =PropUtils.loadProperties("system.properties");
        assertTrue(p.size()>0);
    }

    @Test()
    public void test_passingPropertiesFileExistsVerifyContent_failing() {
        Properties p =PropUtils.loadProperties("system.properties");
        assertTrue(p.size()>0);

        String s = p.get("this.is.a.test").toString();
        assertFalse(s == null);

        assertEquals("Loaded texts should be the same: " + s, s, DEFAULT_VALUE_FROM_PROPERTIES_SET);
    }

    @After
    public void tearDown() {

    }

}
