/*$Header: /home/common/cvsarea/ibase/dia/src/common/msg/MD5.java,v 1.5 2004/03/27 20:27:01 weaston Exp $*/
package common.msg;

import common.Log;

//import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class MD5 {

    private static Charset charsetUTF8 = null;

    /**
     * Compute an MD5 digest of a string.
     * Intended for use on server, where we get password as string
     */
    public static String computeDigest(String text) {
        char[] chars = text.toCharArray();
        String result = computeDigest(chars);
        Arrays.fill(chars, '\u0000');
        return result;
    }

    /**
     * Compute an MD5 digest of a character array.
     */
    public static String computeDigest(char[] chars) {
        if (charsetUTF8 == null) {
            charsetUTF8 = Charset.forName("UTF-8");
        }
        ByteBuffer buffer = charsetUTF8.encode(CharBuffer.wrap(chars));
        // Note. we crash if the returned buffer is not array-backed
        byte[] bytes = buffer.array();
        String result = computeDigest(bytes, buffer.arrayOffset(), buffer.remaining());
        // Write over temporary
        Arrays.fill(bytes, (byte) 0);
        return result;
    }

    /**
     * Compute an MD5 digest of a byte array.
     */
    public static String computeDigest(byte[] bytes) {
        return computeDigest(bytes, 0, bytes.length);
    }

    /**
     * Compute an MD5 digest of a subarray of a byte array.
     */
    public static String computeDigest(byte[] bytes, int offset, int length) {
        try {
            MessageDigest theDigest = MessageDigest.getInstance("MD5");
            theDigest.update(bytes, offset, length);
            byte[] digestBytes = theDigest.digest();

            // Convert to hex characters
            StringBuffer buffer = new StringBuffer(digestBytes.length * 2);
            for (int i = 0; i < digestBytes.length; i++) {
                int theByte = digestBytes[i] & 255;
                if (theByte <= 15) {
                    buffer.append('0');
                }
                buffer.append(Integer.toHexString(theByte));
            }
            return buffer.toString();
        } catch (NoSuchAlgorithmException e) {
            Log.quit(e);
            return null; // keep compiler happy
        }
    }
}
