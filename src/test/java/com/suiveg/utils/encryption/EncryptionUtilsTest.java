package com.suiveg.utils.encryption;

import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.Before;
import org.powermock.api.easymock.mockpolicies.Log4jMockPolicy;
import org.powermock.core.classloader.annotations.MockPolicy;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.testng.annotations.Test;

import java.security.MessageDigest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@MockPolicy(Log4jMockPolicy.class)
@PrepareForTest({IOUtils.class, MessageDigest.class, StringBuffer.class})
public class EncryptionUtilsTest {

    private static final String
            CONVERTED_STRING_THROUGH_MD5 = "ConvertedStringThroughMD5";
    private static final String
            SIMPLE_STRING = CONVERTED_STRING_THROUGH_MD5,
            EXPECTED_RESULT_OF_CONVERTED_SIMPLE_STRING = "436f6e766572746564537472696e675468726f7567684d4435";

    @Before
    public void setUp() {
        //unset atm.. not using powermock or any of that "fancy stuff" as everything is statically called!
    }

    @Test(enabled = true, alwaysRun = true, testName = "")
    public void convertPasswordWithCrypt_allowedSalt() {
        
    }

    @Test(enabled = true, alwaysRun = true, testName = "MD5 Test")
    public void convertToDigest_provideMD5() {
        byte[] result = EncryptionUtils.convertToDigest(CONVERTED_STRING_THROUGH_MD5.getBytes(), EncryptionUtils.EncryptionType.MD_5);
        assertNotNull(result);
        assertTrue(result.length>0);
    }

    @Test(enabled = true, alwaysRun = true, testName = "MD5 Test")
    public void convertToDigest_provideMD2() {
        byte[] result = EncryptionUtils.convertToDigest(CONVERTED_STRING_THROUGH_MD5.getBytes(), EncryptionUtils.EncryptionType.MD_2);
        assertNotNull(result);
        assertTrue(result.length>0);
    }

    @Test(enabled = true, alwaysRun = true, testName = "MD5 Test")
    public void convertToDigest_provideSHA_1() {
        byte[] result = EncryptionUtils.convertToDigest(CONVERTED_STRING_THROUGH_MD5.getBytes(), EncryptionUtils.EncryptionType.SHA_1);
        assertNotNull(result);
        assertTrue(result.length>0);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void convertToDigest_provideNullObject() {
        EncryptionUtils.convertToDigest(null, EncryptionUtils.EncryptionType.MD_5);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void convertToDigest_provideNullEncryptionType() {
        EncryptionUtils.convertToDigest(CONVERTED_STRING_THROUGH_MD5.getBytes(), null);
    }

    @Test
    public void convertToHex_simpleString() {
        byte[] result = EncryptionUtils.convertToHex(SIMPLE_STRING.getBytes());
        assertNotNull(result);
        assertTrue(result.length>0);
        assertEquals(new String(result), EXPECTED_RESULT_OF_CONVERTED_SIMPLE_STRING);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void convertToHex_nullArgument() {
        EncryptionUtils.convertToHex(null);
    }

    @Test
    public void convertToHex_emptyString() {
        EncryptionUtils.convertToHex("".getBytes());
    }

    @Test
    public void convertFromHex_simpleString() {
        byte[] result = EncryptionUtils.convertFromHex(EXPECTED_RESULT_OF_CONVERTED_SIMPLE_STRING);
        assertNotNull(result);
        assertTrue(result.length>0);
        assertEquals(SIMPLE_STRING, new String(result));
    }

    @After
    public void tearDown() {

    }

}
