/* $Header: /home/common/cvsarea/ibase/dia/src/client/TaskRequestCodingValues.java,v 1.3.8.2 2006/03/21 16:42:41 nancy Exp $ */
package client;

import common.Log;
import common.msg.MessageWriter;
import java.io.IOException;
import java.util.Map;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * Get the coded values for a child.  This is used by the copy all and copy field
 * functions.
 * @see server.Handler_request_coding_values
 */
public class TaskRequestCodingValues extends ClientTask {

    private int pageId = 0;
    private int delta;
    private int boundary;
    final private ServerConnection scon = Global.theServerConnection;

    /**
     * Create an instance of this class and remember the parameters.
     * @param pageId may be 0; the page.page_id of the current page
     * @param delta the relative position of the return page to the
     * given pageId or the beginning or end of the current batch
     * @param boundary may be common.msg.MessageConstants.B_NONE,
     * common.msg.MessageConstants.B_CHILD,
     * common.msg.MessageConstants.B_RANGE
     */
    public TaskRequestCodingValues(int pageId, int delta, int boundary) {
        this.pageId = pageId;
        this.delta = delta;
        this.boundary = boundary;
    }

    /**
     * Write the message with attributes and set the result with the
     * data decoded into a <code>Map</code>.
     */
    public void run() throws IOException {
        MessageWriter writer;
        writer = scon.startMessage(T_REQUEST_CODING_VALUES);
        writer.writeAttribute(A_PAGE_ID, pageId);
        writer.writeAttribute(A_DELTA, delta);
        writer.writeAttribute(A_BOUNDARY, boundary);
        addStandardAttributes(writer);
        writer.endElement();
        writer.close();

        Element reply = scon.receiveMessage();

        if (T_PAGE_VALUES.equals(reply.getNodeName())) {
            // if there were values, fill in the HashMap
            Map valueMap = null;
            NodeList valueList = reply.getElementsByTagName(T_VALUE_LIST);
            if (valueList.getLength() > 0) {
                Log.print("(TaskRequestCodingValues) is value_list " + valueList.getLength());
                valueMap = MessageMap.decode((Element) (valueList.item(0)));
            }
            // store the result so the callback can get it
            setResult(valueMap);
        } else {
            Log.quit("Coding unexpected message type: " + reply.getNodeName());
        }
    }
}