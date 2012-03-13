package com.suiveg.utils.encryption;

import com.suiveg.utils.abs.AbstractUtil;
import org.apache.commons.lang.StringUtils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Tools to encrypt / decrypt elements using both well known and not-so-well known routines (also known as self-made..)
 *
 * @author <a href="mailto:vegaasen@gmail.com">Vegard Aasen</a>
 * @author <a href="mailto:marius.kristensen@gmail.com">Marius Kristensen</a>
 * @version see system.properties
 * @since 0.1
 */
public class EncryptionUtils extends AbstractUtil {

    public EncryptionUtils() {
        //empty
    }

    /**
     * Simple digester. Takes two arguments ; sentence and encryptionType
     *
     * @param sentence _
     * @param encryptionType _
     * @return _
     * @throws NullPointerException _
     */
    public static byte[] convertToDigest(final byte[] sentence, final EncryptionType encryptionType)
            throws NullPointerException {
        if (verifyNotNull(sentence, encryptionType)) {
            if(sentence.length>0) {
                try {
                    MessageDigest messageDigest = MessageDigest.getInstance(encryptionType.toString());
                    messageDigest.update(sentence);
                    return messageDigest.digest();
                } catch (NoSuchAlgorithmException e) {
                    throw new UnsupportedOperationException("Could not process item.");
                }
            }
            return sentence;
        }
        throw new NullPointerException(E_OBJECT_WAS_NULL);
    }

    /**
     * Simple byte array to hex byte array converter
     *
     * @param sentence _
     * @return _
     * @throws NullPointerException _
     */
    public static byte[] convertToHex(final byte[] sentence)
            throws NullPointerException {
        if (verifyNotNull(sentence)) {
            if(sentence.length>0) {
                StringBuffer sb = new StringBuffer();
                for (byte b : sentence) {
                    sb.append(
                            Integer.toString(
                                    (b & 0xff) + 0x100,
                                    16
                            ).substring(1)
                    );
                }
                return sb.toString().getBytes();
            }
            return sentence;
        }
        throw new NullPointerException(E_OBJECT_WAS_NULL);
    }

    /**
     * Simple String to hex converter
     *
     * @param hexSentence _
     * @return _
     * @throws NullPointerException _
     */
    public static byte[] convertFromHex(final String hexSentence)
            throws NullPointerException {
        if (verifyNotNull(hexSentence)) {
            if(StringUtils.isNotBlank(hexSentence)) {
                StringBuffer sb = new StringBuffer();
                for (int i = 0; i < hexSentence.length() - 1; i += 2) {
                    String output = hexSentence.substring(i, (i + 2));
                    int decimal = Integer.parseInt(output, 16);
                    //Note: Dah! Ofc is char important! Without it, you will get its ascii reference!
                    sb.append((char) decimal);
                }
                return sb.toString().getBytes();
            }
            return hexSentence.getBytes();
        }
        throw new NullPointerException(E_OBJECT_WAS_NULL);
    }

    public enum EncryptionType {
        MD_5("MD5"),
        SHA_1("SHA-1"),
        MD_2("MD2");

        private String encryptionType;

        EncryptionType(String encryptionType) {
            this.encryptionType = encryptionType;
        }

        @Override
        public String toString() {
            return encryptionType;
        }
    }

}
