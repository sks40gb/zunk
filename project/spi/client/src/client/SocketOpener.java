/* $Header: /home/common/cvsarea/ibase/dia/src/client/SocketOpener.java,v 1.13.8.1 2006/03/09 12:09:16 nancy Exp $ */
package client;


//Note.  in Java 1.4 SSLContext is implemented in both javax.net.ssl and com.sun.net.ssl,
//with the latter being deprecated.  However, it is not in javax.net.ssl in Java 1.3,
import com.lexpar.util.Log;
import common.FileProperties;
import tools.LocalProperties;
import java.util.StringTokenizer;
import java.io.IOException;
import java.net.Socket;
import java.net.ConnectException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import javax.net.SocketFactory;
import javax.net.ssl.SSLContext;

/**
 * This class is used by ImageConnection and <code>ServerConnection</code> to
 * open a socket on the given server IP and server port, using a given timeout.
 */
public class SocketOpener implements Runnable {

    private String host;
    private int port;
    private Socket socket = null;
    private IOException failure = null;

    // A seed to use in case there's no local.properties
    final private static String DEFAULT_SEED = "97,90,e8,0c,bf,29,62,7b,13,fa,06,c9,19,04,26,a9,ad,1b,05,13";
    // just to avoid creating new seed twice
    private static boolean seedWasStored = false;

    /**
     * Open a socket, with specified timeout.
     * @param  aHost    the host IP
     * @param  aPort    the port on the host
     * @param  timeout  timeout, in milliseconds
     * @return the socket
     * @throws IOException if unable to connect or connect times out.
     */
    public static Socket openSocket(String aHost, int aPort, int timeout)
            throws IOException {
        SocketOpener opener = new SocketOpener(aHost, aPort);
        Thread t = new Thread(opener);
        t.start();
        try {
            t.join(timeout);
        } catch (InterruptedException exception) {
        }

        if (opener.getSocket() != null) {
            // before returning, set socket to timeout when blocking
            //opener.getSocket().setSoTimeout(timeout);
            return opener.getSocket();
        } else if (opener.getFailure() != null) {
            throw opener.getFailure();
        } else {
            throw new ConnectException("Timed out connecting to " + aHost + ":" + aPort);
        }
    }

    /**
     * Called from <code>client.SocketOpener.openSocket</code> to store
     * the given values.
     * @param  aHost    the host IP
     * @param  aPort    the port on the host
     */
    private SocketOpener(String aHost, int aPort) {
        socket = null;
        host = aHost;
        port = aPort;
    }

    /**
     * Using an SSLSocketFactory initialized with a new seed, create 
     * and synchronize a new <code>Socket</code>.
     */
    public void run() {
        try {
            SocketFactory factory;
            if (true) {
                // create an SSLSocketFactory

                System.setProperty("javax.net.ssl.trustStore",
                        FileProperties.getBaseDirectory() + "/truststore");
                System.setProperty("javax.net.ssl.trustStorePassword", "trustword");

                // Get random seed and turn it into an array of bytes
                String seedString = LocalProperties.getProperty("random_seed", DEFAULT_SEED);
                StringTokenizer tokenizer = new StringTokenizer(seedString, ",");
                byte[] seedBytes = new byte[tokenizer.countTokens()];
                for (int i = 0; i < seedBytes.length; i++) {
                    String seedOneByte = (String) tokenizer.nextElement();
                    seedBytes[i] = (byte) Integer.parseInt(seedOneByte, 16);
                }

                // Initialize random-number generator with saved seed
                //SecureRandom sr0 = new SecureRandom();
                SecureRandom sr0 = SecureRandom.getInstance("SHA1PRNG");
                sr0.setSeed(seedBytes);

                // Save a new seed in the properties file
                // Note.  properties file written after successful login
                if (!seedWasStored) {
                    seedWasStored = true;

                    // stir in a few bits of entropy (so key isn't always the same)
                    sr0.setSeed(System.currentTimeMillis());

                    long startTime = System.currentTimeMillis();
                    byte[] newSeedBytes = sr0.generateSeed(seedBytes.length);
                    Log.write("time to generate seed: " + (System.currentTimeMillis() - startTime));
                    StringBuffer newSeed = new StringBuffer();
                    for (int i = 0; i < newSeedBytes.length; i++) {
                        newSeed.append(',');
                        int newByte = newSeedBytes[i] & 0xFF;
                        if (newByte < 0x10) {
                            newSeed.append('0');
                        }
                        newSeed.append(Integer.toHexString(newByte));
                    }
                    LocalProperties.setProperty("random_seed", newSeed.substring(1));
                }

                // create an SSLSocketFactory initialized with the new seed
                SSLContext sslc = SSLContext.getInstance("TLS");
                sslc.init(null, null, sr0);
                factory = sslc.getSocketFactory();

            } else {
                // create a regular socket factory (not SSL)
                // THIS CODE IS NEVER REACHED
                factory = SocketFactory.getDefault();
            }
            // Note: Changed from passing the host and port to factory.createSocket
            // Java 1.3 gave errors on connect fail; Java 1.4.2 does not, in that case
            // Now we get errors when the host isn't found, but still don't see errors
            // if host is found and port is open, but port isn't listening, until
            // we attempt to write to the socket.  TBD: investigate further
            Log.write("factory.createSocket(" + host + ", " + port + ")");
            // Note.  Java sometimes gives a failure in the VM on
            // InetSocketAddress(host, port) with numeric port.
            // TBD: can we do something else??
            //Socket newSocket = factory.createSocket();
            //newSocket.connect(new InetSocketAddress(host, port));
            Socket newSocket = factory.createSocket(host, port);
            synchronized (this) {
                socket = newSocket;
            }
        } catch (NoSuchAlgorithmException exception) {
            synchronized (this) {
                failure = new IOException(exception.toString());
            }
        } catch (KeyManagementException exception) {
            synchronized (this) {
                failure = new IOException(exception.toString());
            }
        } catch (IOException exception) {
            synchronized (this) {
                failure = exception;
            }
        }
    }

    private synchronized Socket getSocket() {
        return socket;
    }

    private synchronized IOException getFailure() {
        return failure;
    }
}
