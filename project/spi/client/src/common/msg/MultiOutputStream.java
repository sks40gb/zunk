/* $Header: /home/common/cvsarea/ibase/dia/src/common/msg/MultiOutputStream.java,v 1.7 2004/04/28 19:48:43 weaston Exp $ */

package common.msg;

  // temporary
import java.io.OutputStream;
import java.io.IOException;

/**
 * Allows multiple OutputStream objects to be concatenated safely
 * into a single OutputStream.  This is used to allow multiple messages to
 * be sent over a single socket connection.  The java implementation of
 * GZIP and of ObjectOutputStream seems to assume there is only one 
 * compressed file in the stream.
 * <p>
 * Each substream is written as a series of zero or more chunks of the form
 * &lt;prefix&gt;&lt;data-length&gt;&lt;data&gt;.  The end of the substream
 * is indicated by &lt;prefix&gt;&lt;-1&gt;.  All numbers are two bytes, in
 * big-endian format.
 * <p>
 * The implementation is not thread safe.  It is assumed that all calls
 * to methods of an instance of this class are from the same thread.
 * @see MultiInputStream
 */
public class MultiOutputStream {

    // The size of the prefix (2-byte magic + 2-byte length)
    final private static int    PREFIX_SIZE    = 4;

    // The size of the internal buffer.
    final private static int    BUFFER_SIZE    =1024;

    // The largest chunk to be written.  The output stream is
    // broken into such chunks in order to (1) avoid overflow
    // of the 2-byte size field and (2) to allow for [future] 
    // flow-control and progress reporting.
    final private static int    MAX_CHUNK_SIZE = 16384;

    // Magic number to identify MultiOutputStream data chunks
    final private static int MOS_MAGIC      = 0xBE03;

    // The internal buffer used for collecting small pieces for output
    final byte[] buffer = new byte[BUFFER_SIZE];

    // The underlying output stream.
    private OutputStream outStream;

    // The currently-open substream
    private OutputStream subStream = null; 

    /**
     * Construct a new MultiOutputStream backed by an existing OutputStream.
     * @param out The existing stream.
     */
    public MultiOutputStream (OutputStream out) {
        outStream = out;
        //outStream = new FilterOutputStream(out) {
        //        boolean writingArray = false;
        //        public void write(int A) throws IOException {
        //            if (! writingArray) {
        //                System.out.println("|"+hex(A));
        //            }
        //            super.write(A);
        //        }
        //        public void write(byte[] A) throws IOException {
        //            write(A,0,A.length);
        //        }
        //        public void write(byte[] A, int B, int C) throws IOException {
        //            System.out.print("|"+C+"|");
        //            for (int i=0; i < C && i < 40; i++) {
        //                System.out.print(" "+hex(A[i]));
        //            }
        //            System.out.println();
        //            writingArray = true;
        //            super.write(A,B,C);
        //            writingArray = false;
        //        }
        //        private String hex(int x) {
        //            x = x & 0xFF;
        //            return (x < 16 ? "0" + Integer.toHexString(x) : Integer.toHexString(x));
        //        }
        //    };
    }   

    /**
     * Return an OutputStream to be written.  When this OutputStream is closed,
     * the underlying stream is not closed, but a new output stream may be
     * requested by calling newStream again.
     */
    public OutputStream newStream () throws IOException {        
        if (subStream != null) {
            IOException e = new IOException("An output substream is already open");
            e.printStackTrace();
            throw e;
        }

        subStream = new OutputStream() {

                int count = PREFIX_SIZE;

                public void write(int A) throws IOException {
                    if (count >= BUFFER_SIZE) {
                        flushBuffer(0);
                    }
                    buffer[count] = (byte) A;
                    count++;
                }

                public void write(byte[] A, int B, int C) throws IOException {
                    
                    // send in chunks if too big
                    while (C > MAX_CHUNK_SIZE) {
                        write (A, B, MAX_CHUNK_SIZE);
                        B += MAX_CHUNK_SIZE;
                        C -= MAX_CHUNK_SIZE;
                    }

                    if ((count + C) > BUFFER_SIZE) {
                        flushBuffer(C);
                        //dumpBuffer(A,B,C);
                        outStream.write(A,B,C);
                    } else {
                        System.arraycopy(A,B,buffer,count,C);
                        count += C;
                    }
                }

                public void close() throws IOException {
                    if (count > (BUFFER_SIZE - PREFIX_SIZE)) {
                        flushBuffer(0);
                    };
                    if (count == PREFIX_SIZE) {
                        count = 0;
                    } else {
                        buildPrefix(0);
                    }
                    storeInt(MOS_MAGIC, count);
                    storeInt(-1, count+2);
                    count += 4;
                    //dumpBuffer(buffer, 0, count);
                    outStream.write(buffer, 0, count);
                    outStream.flush();
                    subStream = null;
                    //System.err.println("output closed");
                }

                // write content of buffer, with block length
                // = number of bytes in buffer + given extra count
                // empty the buffer
                private void flushBuffer(int extra) throws IOException {
                    if ((count + extra) > PREFIX_SIZE) {
                        buildPrefix(extra);
                        //dumpBuffer(buffer, 0, count);
                        outStream.write(buffer, 0, count);
                    }
                    count = PREFIX_SIZE;
                }

                // build a prefix in the buffer with block length
                // = number of bytes in buffer + given extra count
                private void buildPrefix(int extra) throws IOException {
                    storeInt(MOS_MAGIC, 0);
                    storeInt(count - 4 + extra, 2);
                }

                // store 16-bit big-endian integer in 2 bytes in buffer
                private void storeInt(int value, int offset) {
                    buffer[offset]   = (byte) (value >> 8);
                    buffer[offset+1] = (byte) (value & 0xFF);
                }
            };

        return subStream;
    }

    //private void dumpBuffer(byte[] b, int off, int count) {
    //    StringBuffer buffer = new StringBuffer();
    //    for (int i = off; i < off + Math.min(count,32); i++) {
    //        buffer.append(" ");
    //        if ((b[i] & 0xFF) < 16) {
    //            buffer.append("0");
    //        }
    //        buffer.append(Integer.toHexString(b[i] & 0xFF));
    //   }
    //   System.out.println("..."+off+" "+count+" '"+buffer+"'");
    //}
    
    /**
     * Close the underlying stream.  Closes substream first, if it is open.
     */
    public void close () throws IOException {
        if (outStream != null) {
            if (subStream != null) {
                subStream.close();
            }
            outStream.close();
            outStream = null;
        }
    }
}
