/* $Header: /home/common/cvsarea/ibase/dia/src/client/TaskBinderUpdate.java,v 1.2.6.1 2006/03/14 15:08:46 nancy Exp $ */
package client;

import common.Log;
import common.msg.MessageWriter;

import java.io.IOException;
import org.w3c.dom.Element;

/**
 * ClientTask for updating the binder, adding or removing pages.
 * @see server.Handler_binder_update
 */
public class TaskBinderUpdate extends ClientTask {

    /** Page Id */
    private int pageId;
    /** remove or add the page */
    private boolean isToRemovePage = false;
    /** The server connection */
    final private ServerConnection scon = Global.theServerConnection;

    /**
     * Create an instance of this class and remember the given parameters.
     * @param pageId the page.page_id to be added or removed
     * @param isToRemovePage true to remove the page; false to add the page
     */
    public TaskBinderUpdate(int pageId, final boolean isToRemovePage) {
        this.pageId = pageId;
        this.isToRemovePage = isToRemovePage;
    }

    /**
     * Write the message with attributes and set the result.
     */
    @Override
    public void run() throws IOException {
        MessageWriter writer = scon.startMessage(T_BINDER_UPDATE);
        writer.writeAttribute(A_PAGE_ID, pageId);
        if (isToRemovePage) {
            writer.writeAttribute(A_REMOVE, "YES");
        }
        addStandardAttributes(writer);
        writer.endElement();
        writer.close();
        Log.print("(TaskBinderUpdate.run) page=" + pageId + " isToRemovePage="+isToRemovePage);

        Element reply = scon.receiveMessage();

        String ok = reply.getNodeName();

        if (! T_OK.equals(ok)
            && ! T_FAIL.equals(ok)) {
            Log.quit("BatchBoundary unexpected message type: "+ok);
        }
    }
}
