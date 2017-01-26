/* $Header: /home/common/cvsarea/ibase/dia/src/model/WeakTableModelListener.java,v 1.2 2004/04/08 15:46:26 weaston Exp $ */
package model;

import common.Log;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

/**
 * A WeakReference wrapper for a TableModelListener.  Used to allow
 * a listener to be garbage collected, even though the model
 * it has been added to is still live.
 */
final public class WeakTableModelListener
extends WeakReference implements TableModelListener {

    private static ReferenceQueue queue = new ReferenceQueue();
    private TableModel model;

    /**
     * Construct a new WeakTableModelListener wrapping a given listener.
     */
    public WeakTableModelListener(TableModelListener listener, TableModel model) {
        super(listener, queue);
        this.model = model;
    }

    public void tableChanged(TableModelEvent evt) {
        // get the referent of this WeakReference
        TableModelListener listener = (TableModelListener) get();
        if (listener != null) {
            // pass this event on to the wrapped listener.
            listener.tableChanged(evt);
        }
    }

    /**
     * Remove any listeners that are no longer accessable.
     */
    public static void pollQueue() {
        WeakTableModelListener item;
        while (null != (item = (WeakTableModelListener) queue.poll())) {
            Log.print ("REMOVING UNREACHABLE LISTENER FROM "+item.model);
            item.model.removeTableModelListener(item);
        }
    }
}
