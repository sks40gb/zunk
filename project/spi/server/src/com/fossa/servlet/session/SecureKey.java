/* $Header: /home/common/cvsarea/ibase/dia/src/server/SecureKey.java,v 1.3 2003/12/14 23:36:05 weaston Exp $ */
package com.fossa.servlet.session;

import com.fossa.servlet.thirdparty.mindprod.base64.Base64;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import org.apache.log4j.Logger;

/**
 * Generation of secure keys.  Keys are generated randomly from a
 * "cryptographically good" random-number generator.  A key may
 * be used by a client as a "one-time password" for reconnecting
 * after a lost connection.  Although, in principle, two identical
 * keys could be generated, the likelihood of this happening is
 * negligible, as is the possibility of someone guessing the key.
 */
public class SecureKey {

    // we'll generate 144 bits of key (rounded up to multiple of 24 from 128)
    // this will result in a 24 byte key after base-64 encoding
    private static int KEY_BYTES = 18;

    // A cryptographically good random-number generator
    private static SecureRandom random;

    // Base 64 encoding class instance
    private static Base64 b64 = new Base64();

    private static Logger logger = Logger.getLogger("com.fossa.servlet.session");
    /**
     * Generate a key.  The resulting key is 24 characters long, containing
     * 144 bits (18 bytes) of randomness.
     */
    public static synchronized String generate() {
        byte[] keyBytes = new byte[KEY_BYTES];
        random.nextBytes(keyBytes);
        return b64.encode(keyBytes);
    }
    
    static {
        try {
            random = SecureRandom.getInstance("SHA1PRNG");
        } catch (NoSuchAlgorithmException e) {
            //TODO: Cyrus - Find mechanism for logging
            //Log.quit(e);
           logger.error("Exception while generation of secure keys." + e);
           StringWriter sw = new StringWriter();
           e.printStackTrace(new PrintWriter(sw));
           logger.error(sw.toString());
           
        }

        // generate the first one on startup
        // this causes the random-number generator to be seeded
        // (so that first login doesn't have to wait for it)
        generate();
    }
}
