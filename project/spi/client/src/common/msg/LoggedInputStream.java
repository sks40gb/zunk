/* $Header: /home/common/cvsarea/ibase/dia/src/common/msg/LoggedInputStream.java,v 1.19 2005/06/13 19:53:08 weaston Exp $ */

package common.msg;

import com.lexpar.util.Log;

import java.io.FilterInputStream;
import java.io.InputStream;
import java.io.IOException;

/**
 * A filter class to allow saving a copy of an input stream
 * in the log.  The copy is written when the stream reaches eof or is closed.
 * This is used primarily for testing purposes, so that
 * an XML message can be saved for visual examination.
 * <p>
 * For normal use, the private variable LOGGING is set to false,
 * and no logging is done.
 */
public class LoggedInputStream extends FilterInputStream {

    final private static boolean LOGGING = true; //false;
    final private static int BUFFER_LIMIT = 500;  // max characters to log for a message

    private StringBuffer buffer = new StringBuffer();

    /**
     * Create a LoggedInputStream backed by the given InputStream.
     * Messages on the logged stream are written to the log, EXCEPT
     * that ping messages (containing "<ping/>") are suppressed.
     * @param stream The given input stream.
     */
    private LoggedInputStream (InputStream stream) {
        super(stream);
    }

    /**
     * Create a LoggedInputStream backed by the given InputStream.
     * Messages on the logged stream are written to the log, EXCEPT
     * that ping and ok messages (messages containing "<ping" or "<ok")
     * are suppressed.  ALso, the ?xml and !DOCTYPE lines are
     * suppressed, as are the outer &lt;message&gt; and &lt;/message&gt;
     * <p>
     * If LOGGING is false, the given stream is returned.
     * @param stream The given input stream.
     */
    public static InputStream makeInstance (InputStream stream) {
        if (LOGGING) {
            return new LoggedInputStream(stream);
        } else {
            return stream;
        }
    }


    public int read() throws IOException {
        byte[] temp = new byte[1];
        int count = read(temp);
        return (count > 0 ? temp[0] : -1);
    }

    
    public int read(byte[] A, int B, int C) throws IOException {
        int result = in.read(A,B,C);
        if (result > 0) {
            //System.out.println("&>"+new String(A,B,result,"UTF-8"));
            if (buffer.length() < BUFFER_LIMIT) {
                buffer.append(new String(A,B,result,"UTF-8"));
            }
        } else {
            //System.out.println("&=EOF");
            // Note.  Apparently, DocumentBuilder calls read after EOF.
            //System.out.println("&=read returns no characters");
            if (buffer != null ) {
                writeToLog();
            }
        }
        return result;
    }

    // write the message to the log
    // ignore ping and ok messages, to avoid the wallpaper effect
    // also, suppress printing of plaintext passwords
    private void writeToLog() {
        String text = buffer.toString();
        if (text.length() > 0
        && text.indexOf("<ping") < 0
        && text.indexOf("<ok") < 0) {

            // suppress printing of cleartext password
            int endPos = buffer.indexOf("</hello>");
            if (endPos >= 0) {
                int startPos = 1 + buffer.substring(0, endPos).lastIndexOf('>');
                buffer.replace(startPos, endPos, "########");
            }

            //Log.info("==========");
            Log.write("Message read:");
            String[] data = buffer.toString().split("\n");
            for (int i = 0; i < data.length; i++) {
                if (! data[i].startsWith("<")) {
                    // suppress boilerplate
                    Log.info(data[i]);
                }
            }
            //Log.info("==========");
        }
        buffer = null;
    }

    public void close() throws IOException {
        in.close();
    }
}
