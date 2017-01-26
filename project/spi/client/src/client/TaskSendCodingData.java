/* $Header: /home/common/cvsarea/ibase/dia/src/client/TaskSendCodingData.java,v 1.14.8.1 2006/03/21 16:42:41 nancy Exp $ */
package client;

import common.Log;
import common.msg.MessageWriter;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import org.w3c.dom.Element;

/**
 * ClientTask called from SplitPaneViewer to send coded data from
 * the client user to the server for storage.  This class calls
 * <code>MessageMap</code> to compare the original value Map with 
 * the given Map and send an XML message with any updated values.
 * @see MessageMap
 * @see server.Command_page_values
 */
public class TaskSendCodingData extends ClientTask {

    private int pageId;
    private String boundaryFlag;
    private Map valueMap;
    private Map oldValueMap;
    private Map errorMap;
    private Map errortypeMap;
    private String status;
    final private ServerConnection scon = Global.theServerConnection;

    /**
     * Create an instance of this class and remember the parameters.
     * @param pageId the page.page_id of the page to be updated with the
     * coded data
     * @param boundaryFlag the new boundary value for this page; can be
     * B_CHILD, B_NONE, B_RANGE
     * @param valueMap a Map containing the client's coded values for this page
     * @param oldValueMap a Map containing the values as they are currently
     * stored on the server
     * @param errorMap a Map containing changes made by a QCer or QAer
     */
    public TaskSendCodingData(int pageId, String boundaryFlag,
            Map valueMap, Map oldValueMap, Map errorMap, Map errortypeMap, String status) {
        
        this.pageId = pageId;
        this.valueMap = valueMap;
        this.oldValueMap = oldValueMap;
        this.boundaryFlag = boundaryFlag;
        this.errorMap = errorMap;
        this.errortypeMap = errortypeMap;
        this.status = status;
    }

     /**
     * Write the message with attributes and set the result.
     * Message should be in XML format. 
     */
    @Override
    public void run() throws IOException {
        MessageWriter writer = scon.startMessage(T_PAGE_VALUES);

        writer.writeAttribute(A_PAGE_ID, pageId);
        writer.writeAttribute(A_STATUS, status);
        addStandardAttributes(writer);
        if (boundaryFlag != null) {
            // unitizing update
            writer.writeAttribute(A_BOUNDARY_FLAG, boundaryFlag);
        }

        if (valueMap != null || oldValueMap != null) {
            MessageMap.encode(writer, valueMap, oldValueMap);
        }

        if (errorMap != null && errorMap.size() != 0) {
            writer.startElement(T_ERROR_FLAG_LIST);
            Iterator it = errorMap.entrySet().iterator();
            while (it.hasNext()) {
                writer.startElement(T_VALUE);
                Map.Entry entry = (Map.Entry) it.next();
                writer.writeAttribute(A_NAME, (String) entry.getKey());
                writer.writeContent(
                        (Boolean.TRUE.equals(entry.getValue()) ? "Yes" : "No"));
                writer.endElement();
            }
            writer.endElement();

            writer.startElement(T_ERROR_TYPE_LIST);
            Iterator itr = errortypeMap.entrySet().iterator();
            while (itr.hasNext()) {
                writer.startElement(T_VALUE);
                Map.Entry entry = (Map.Entry) itr.next();
                writer.writeAttribute(A_NAME, (String) entry.getKey());
                writer.writeContent((String) errortypeMap.get(entry.getKey()));
                writer.endElement();
            }
            writer.endElement();
        }
        writer.endElement();
        writer.close();

        Element reply = scon.receiveMessage();
        String ok = reply.getNodeName();
        //Log.print("T_PAGE_VALUES reply " + ok);
        setResult((Object) ok);

        if (!T_OK.equals(ok) && !T_FAIL.equals(ok)) {
            Log.quit("SendCodingData unexpected message type: " + ok);
        }
    }
}