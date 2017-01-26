/*
 * MD5.java
 *
 * Created on 13 November, 2007, 1:15 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package com.fossa.servlet.common.msg;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import org.apache.log4j.Logger;

/**
 *
 * @author prakash
 */

public class MD5 {

    /** Creates a new instance of MD5 */
    public MD5() {
    }
    
    private static Charset charsetUTF8 = null;
    private static Logger logger = Logger.getLogger("com.fossa.servlet.common.msg");
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
            //Log.quit(e);
            logger.error("Exception while computing the MD5 digest." + e);
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            logger.error(sw.toString());
            return null; // keep compiler happy
        }
    }
}
