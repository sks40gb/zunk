/* $Header: /home/common/cvsarea/ibase/dia/src/client/ClipboardWatcher.java,v 1.2.8.1 2006/03/09 12:09:16 nancy Exp $/ */
package client;

import common.Log;
import tools.LocalProperties;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;

/**
 * A thread which watches the system clipboard and clears it when it
 * contains an image that could have been obtained by PrtSc.  This
 * makes it harder for the user to save images.  A determined user
 * can probably still do so.
 */
final public class ClipboardWatcher extends Thread {

    final private static int DELAY = 1000; // one second


    /**
     * Called from <code>DiaClient</code> to start the
     * clipboard watcher for the duration of the client session.
     */
    final public static void startClipboardWatcher() {

        // allow clipboard, with the magic word
        if ("lexpartest".equals(LocalProperties.getProperty("printok"))) {
            return;
        }

        ClipboardWatcher w = new ClipboardWatcher();
        w.setDaemon(true);
        w.start();
    }

    /**
     * Override of <code>Thread.run</code> to continuously get
     * the contents of the system clipboard and clear it if it
     * seems to be an image.
     */
    public void run() {

        // allow clipboard, with the magic word
        if ("lexpartest".equals(LocalProperties.getProperty("printok"))) {
            this.setDaemon(false);
            return;
        }

        Clipboard cb = Toolkit.getDefaultToolkit().getSystemClipboard();
        for (;;) {
            try {
                Transferable data = cb.getContents(null);
                if (data.isDataFlavorSupported(DataFlavor.imageFlavor)) {
                    cb.setContents(new StringSelection(""), null);
                }
            } catch (IllegalStateException e) {
                // O.K., clipboard not available 
            } catch (Exception e) {
                Log.print("Exception clearing clipboard: " + e);
            }
            try {
                Thread.sleep(DELAY);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
