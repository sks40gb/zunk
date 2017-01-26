/* $Header: /home/common/cvsarea/ibase/dia/src/client/ClientTask.java,v 1.13.8.1 2006/03/09 12:09:16 nancy Exp $ */
package client;

import common.Log;
import common.msg.MessageConstants;
import common.msg.MessageWriter;
import ui.SplitPaneViewer;
import java.awt.Component;
import java.io.IOException;
import javax.swing.JFrame;

/**
 * Parent class of all client tasks.                              f
 * Subclasses should implement:
 * <pre>
 *   private TaskSub(Cls param1, ...) {
 *       this.param1 = param1; ...
 *   }
 * </pre>
 * ClientThread will run an enqueued task, then call the callback set by setCallback.
 * @see ClientThread
 */
public abstract class ClientTask implements MessageConstants {

    private Runnable callback = null;
    private Object result = null;

    // These are filled in and sent to server
    // for a viewer in the admin app
    private int volumeId = 0;
    private int batchId = 0;

    // The component which gets error pop-ups (filled in on enqueue)
    private Component parent;

    /**
     * Create a new ClientTask.
     */
    protected ClientTask() {
    }

    /**
     * Run the task.  Must be overridden in subclasses.
     */
    public abstract void run() throws IOException;

    /**
     * Set the callback instance.
     */
    public synchronized void setCallback(Runnable callback) {
        this.callback = callback;
    }

    /**
     * Get the callback instance.
     */
    public synchronized Runnable getCallback() {
        return callback;
    }

    /**
     * Set the result instance.  Called from subclasses
     * to return data to the ED thread.  
     */
    protected synchronized void setResult(Object result) {

        this.result = result;
    }

    /**
     * Get the result instance.  The result is an object to
     * be passed to the ED thread with any results of this
     * task.
     */
    public synchronized Object getResult() {
        return result;
    }

    /**
     * Get parent component.  The parent component is used
     * for positioning error pop-ups.  (parent only referred
     * to on GUI thread, so synchronized is not needed)
     */
    public Component getParent() {
        return parent;
    }

    /**
     * Enqueue this task for execution by the client thread.
     * This method is used when there is no obvious window
     * for error pop-ups.
     * @return true, if the task was enqueued; otherwise false.
     */
    public boolean enqueue() {
        return enqueue(Global.mainWindow);
    }

    /**
     * Enqueue this task for execution by the client thread.
     * While doing so, if this is an admin viewer, save volume and batch
     * @param parent The parent component (for positioning error dialogs)
     * @return true, if the task was enqueued; otherwise false.
     */
    public boolean enqueue(Component parent) {
        this.parent = parent;
        if (parent == null) {
            Log.print("enqueue: parent is null");
        } else {
            //Log.print("enqueue: parent is "+parent.getClass().getName());
        }
        if (!(Global.mainWindow instanceof SplitPaneViewer)) {
            Component frameComponent = parent;
            while (frameComponent != null && !(frameComponent instanceof JFrame)) {
                frameComponent = frameComponent.getParent();
            }
            if (frameComponent == null) {
                Log.print("enqueue: parentWindow is null");
            } else {
                //Log.print("enqueue: parentWindow is "+frameComponent.getClass().getName());
            }
            if (frameComponent instanceof SplitPaneViewer) {
                // it's subordinate to a viewer in the admin app
                SplitPaneViewer parentViewer = (SplitPaneViewer) frameComponent;
                synchronized (this) {
                    volumeId = parentViewer.getVolumeId();
                    batchId = parentViewer.getBatchId();
                }
            //Log.print("enqueue sets v="+volumeId+" b="+batchId);
            }
        }
        return ClientThread.enqueue(this);
    }

    /**
     * Add the volume_id and batch_id in use by the current viewer
     * to the current message, if they exist.
     * @param writer the <code>MessageWriter</code> in use by the current
     * task
     */
    public void addStandardAttributes(MessageWriter writer) throws IOException {
        //Log.print("addStandardAttributes: v="+volumeId+" b="+batchId);
        if ((volumeId | batchId) != 0) {
            synchronized (this) {
                if (volumeId != 0) {
                    writer.writeAttribute(A_VOLUME_ID, volumeId);
                }
                if (batchId != 0) {
                    writer.writeAttribute(A_BATCH_ID, batchId);
                }
            }
        }
    }
}
