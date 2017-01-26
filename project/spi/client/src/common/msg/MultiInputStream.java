/* $Header: /home/common/cvsarea/ibase/dia/src/common/msg/MultiInputStream.java,v 1.11 2004/04/28 19:48:43 weaston Exp $ */

package common.msg;

import com.lexpar.util.Log;

import java.io.InputStream;
import java.io.IOException;
import java.io.EOFException;
import java.net.SocketTimeoutException;

/**
 * Allows multiple InputStream objects to be extracted safely
 * from a single InputStream.  This is used to allow multiple messages to
 * be sent over a single socket connection.  The java implementation of
 * GZIP and of ObjectOutputStream seems to assume there is only one 
 * compressed file in the stream.
 * <p> 
 * Each substream is contains a series of zero or more chunks of the form
 * &lt;prefix&gt;&lt;data-length&gt;&lt;data&gt;.  The end of the substream
 * is indicated by &lt;prefix&gt;&lt;-1&gt;.  All numbers are two bytes, in
 * big-endian format.
 * <p>
 * The implementation is not thread safe.  It is assumed that all calls
 * to methods of an instance of this class are from the same thread.
 * @see MultiOutputStream
 */

public class MultiInputStream {

    // The size of the prefix (2-byte magic + 2-byte length)
    final private static int    PREFIX_SIZE    = 4;

    // Magic number to identify MultiOutputStream data chunks
    // 0xBE03, in big-endian order
    final private static int MOS_MAGIC      = 0xBE03;
 
    // The underlying input stream.
    private InputStream inStream;

    // The currently-open substream
    private InputStream subStream = null; 

    /**
     * Construct a new MultiInputStream backed by an existing InputStream.
     * @param in The existing stream.
     */
    public MultiInputStream (java.io.InputStream in) throws IOException {
        this.inStream = in;
    }

    /**
     * Return an InputStream to be read.  When this InputStream is closed,
     * the underlying stream is not closed, but a new input stream may be
     * requested by calling newStream again.
     */
    public InputStream newStream () throws IOException {       
        if (subStream != null) {
            IOException e = new IOException("An input substream is already open");
            e.printStackTrace();
            throw e;
        }

        subStream = new InputStream() {

                int count = 0;

                public int read() throws IOException {
                    if (count == 0) {
                        count = getCount();
                    }
                    if (count < 0) {
                        //System.out.println(">>>read() at eof");
                        return -1;
                    }

                    // read a byte from socket
                    // SO_TIMEOUT is set, so we'll get an interrupt every so often
                    // just keep trying.  this is to allow detection of lost connection
                    int result;
                    for (;;) {
                        try {
                            result = inStream.read();
                            break;
                        } catch (SocketTimeoutException e) {
                            Log.print("Timeout waiting for byte: "+e);
                            throw e;
                        }
                    }
                    if (result < 0) {
                        throw new EOFException("Unexpected EOF while reading subfile byte");
                    }
                    //System.out.println(">>>read() returns "+Integer.toHexString(result & 0xFF));
                    count--;
                    return result;
                }

                public void close() throws IOException {
                    if (subStream != null) {
                        // discard any remaining data in the substream
                        subStream.skip(Long.MAX_VALUE);
                        subStream = null;
                    }
                    
                }

                public int read(byte[] b, int off, int len) throws IOException {
                    //System.out.println("read "+off+" "+len);
                    if (off < 0 || len < 0 || off + len > b.length) {
                        throw new IndexOutOfBoundsException();
                    }
                    if (len == 0) {
                        return 0;
                    }
                    if (count < 0) {
                        //System.out.println(">>>...already at EOF");
                        return -1;
                    }
                    if (count == 0) {
                        count = getCount();
                        if (count < 0) {
                            //System.out.println(">>>...EOF");
                            close();
                            return -1;
                        }
                    }
                    // read bytes from socket
                    // SO_TIMEOUT is set, so we'll get an interrupt every so often
                    // just keep trying.  this is to allow detection of lost connection
                    int bytesRead;
                    for (;;) {
                        try {
                            bytesRead = inStream.read(b, off, Math.min(len, count));
                            break;
                        } catch (SocketTimeoutException e) {
                            Log.print("Timeout waiting for multiple bytes: "+e);
                            throw e;
                        }
                    }
                    //System.out.println(">>>len="+len+" count="+count+" bytesRead="+bytesRead);
                    if (bytesRead <= 0) {
                        count = -1;
                        throw new IOException("Unexpected EOF in MultiInputStream");
                    }
                    count -= bytesRead;
                    return bytesRead;
                }

                public long skip(long len) throws IOException {

                    if (len <= 0 || count < 0) {
                        return 0;
                    }

                    long skipCount = 0;
                    while (skipCount < len) {
                        if (count == 0) {
                            count = getCount();
                            if (count < 0) {
                                //System.out.println(">>>...EOF");
                                close();
                                return 0;
                            }
                        }
                        long bytesSkipped = inStream.skip(Math.min(len, count));
                        if (bytesSkipped <= 0) {
                            count = -1;
                            throw new IOException("Unexpected EOF on skip in MultiInputStream");
                        }
                        count -= bytesSkipped;
                        skipCount += bytesSkipped;
                    }
                    return skipCount;
                }

                public int available() throws IOException {
                    return (count < 0 ? 0 : count);
                }

                // Read prefix and extract count.  Return -1 for eof; otherwise
                // return data length between 1 and Integer.MAX_VALUE.
                private int getCount() throws IOException {
                    //System.err.println("in getCount");
                    int magic = readShort();
                    if (magic < 0) {
                        return -1;
                    }
                    if (magic != MOS_MAGIC) {
                        Log.quit("Invalid magic reading MultiInputStream: 0x"
                                 +Integer.toHexString(magic));
                    }
                    int result = readShort();
                    if (result < 0) {
                        throw new EOFException("EOF while reading subfile prefix");
                    }
                    if (result == 0) {
                        throw new EOFException("Zero data length while reading subfile prefix");
                    }
                    // return the value read.  The (short) cast causes 0xFFFF to be returned as -1;
                    result = (short) result;
                    //System.out.println("... getCount returns "+ result);
                    return result;
                }

                // Read an unsigned short integer into an int.  Bytes of
                // the integer are in big-endian order.
                private int readShort() throws IOException {
                    // read a byte from socket
                    // SO_TIMEOUT is set, so we'll get an interrupt every so often
                    // just keep trying.  this is to allow detection of lost connection
                    int byte1;
                    for (;;) {
                        try {
                            byte1 = inStream.read();
                            break;
                        } catch (SocketTimeoutException e) {
                            Log.print("Timeout waiting for byte1: "+e);
                            throw e;
                        }
                    }
                    //System.out.println("byte1="+byte1);
                    if (byte1 < 0) {
                        return -1;
                    }
                    // read a byte from socket
                    // SO_TIMEOUT is set, so we'll get an interrupt every so often
                    // just keep trying.  this is to allow detection of lost connection
                    int byte2;
                    for (;;) {
                        try {
                            byte2 = inStream.read();
                            break;
                        } catch (SocketTimeoutException e) {
                            Log.print("Timeout waiting for byte2: "+e);
                            throw e;
                        }
                    }
                    if (byte2 < 0) {
                        throw new EOFException("EOF while reading subfile prefix");
                    }
                    return (byte1 & 0xFF) << 8 | (byte2 & 0xFF);
                }
            };

        return subStream;
    }

    /**
     * Close the underlying stream.  Discards open substream.
     */
    public void close () throws IOException {
        if (inStream != null) {
            subStream = null;
            inStream.close();
            inStream = null;
        }
    }
}
