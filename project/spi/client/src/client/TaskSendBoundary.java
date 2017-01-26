/* $Header: /home/common/cvsarea/ibase/dia/src/client/TaskSendBoundary.java,v 1.3.8.1 2006/03/21 16:42:41 nancy Exp $ */
package client;

import common.Log;
import common.msg.MessageWriter;

import java.io.IOException;
import org.w3c.dom.Element;

/**
 * ClientTask called from SplitPaneViewer to change the boundary of a
 * given page.
 * @see server.Handler_page_boundary
 */
public class TaskSendBoundary extends ClientTask {

    /** Page Id */
    private int pageId;
    /** Boundary flag */
    private String boundaryFlag;
    /** Server connection */
    final private ServerConnection scon = Global.theServerConnection;

    /**
     * Create an instance of this class and remember the parameters.
     * @param pageId the page.page_id of the page to receive the new boundary
     * @param boundaryFlag the new boundary, can be one of B_CHILD, B_NONE, B_RANGE
     */
    public TaskSendBoundary(int pageId, String boundaryFlag) {
        this.pageId = pageId;
        this.boundaryFlag = boundaryFlag;
    }

    /**
     * Write the message with attributes and set the result.
     * Message should be in XML format. 
     */
    @Override
    public void run() throws IOException {

        MessageWriter writer = scon.startMessage(T_PAGE_BOUNDARY);
        writer.writeAttribute(A_PAGE_ID, pageId);
        addStandardAttributes(writer);
        assert boundaryFlag != null;
        writer.writeAttribute(A_BOUNDARY_FLAG, boundaryFlag);
        writer.endElement();
        writer.close();

        Element reply = scon.receiveMessage();
        String ok = reply.getNodeName();
        setResult((Object) ok);

        if (!T_OK.equals(ok) && !T_FAIL.equals(ok)) {
            Log.quit("SendBoundary unexpected message type: " + ok);
        }
    }
}
