/* $Header: /home/common/cvsarea/ibase/dia/src/client/TaskRequestCoding.java,v 1.17.6.2 2006/03/21 16:42:41 nancy Exp $ */
package client;

import common.CodingData;
import common.Log;
import common.msg.MessageReader;
import common.msg.MessageWriter;
import java.io.IOException;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * ClientTask to request project data for one document from the server.
 * @see common.CodingData
 * @see server.Handler_request_coding
 * @see server.MarshallPage
 */
public class TaskRequestCoding extends ClientTask {

    private int pageId = 0;
    private int delta;
    private int boundary;
    private String whichStatus;
    private String bateno = "";
    private int volumeId = 0;
    private boolean flag = false;
    final private ServerConnection scon = Global.theServerConnection;

    /**
     * Create an instance of this class and remember the parameters.
     * @param pageId may be 0; the page.page_id of the current page
     * @param delta the relative position of the return page to the
     * given pageId or the beginning or end of the current batch
     * @param boundary may be common.msg.MessageConstants.B_NONE,
     * common.msg.MessageConstants.B_CHILD,
     * common.msg.MessageConstants.B_RANGE
     * @param whichStatus the status of the calling user; for use in
     * determining whether error flag data should be collected.
     */
    public TaskRequestCoding(int pageId, int delta, int boundary, String whichStatus) {
        this.pageId = pageId;
        this.delta = delta;
        this.boundary = boundary;
        this.whichStatus = whichStatus;
    }

    public TaskRequestCoding(int pageId, int delta, int boundary, String whichStatus, String bateno, int volumeId, boolean flag) {
        this.pageId = pageId;
        this.delta = delta;
        this.boundary = boundary;
        this.whichStatus = whichStatus;
        this.bateno = bateno;
        this.volumeId = volumeId;
        this.flag = flag;

    }

    /**
     * Write the message with attributes and set the result with
     * the data decoded into <code>common.CodingData</code>.
     */
    public void run() throws IOException {
        MessageWriter writer;
        writer = scon.startMessage(T_REQUEST_CODING);
        writer.writeAttribute(A_PAGE_ID, pageId);
        writer.writeAttribute(A_DELTA, delta);
        writer.writeAttribute(A_BOUNDARY, boundary);
        writer.writeAttribute(A_BATE, bateno);
        if (flag) {
            writer.writeAttribute(A_VOLUME_ID, volumeId);
        }
        addStandardAttributes(writer);
        if (whichStatus != null) {
            writer.writeAttribute(A_STATUS, whichStatus);
        }
        writer.endElement();
        writer.close();

        Element reply = scon.receiveMessage();
        if (T_CODING_DATA.equals(reply.getNodeName())) {
            CodingData data = new CodingData();
            // fill in the int and String fields of the CodingData
            MessageReader.decode(reply, data);
            //Log.print("pageId="+data.pageId/*+" nextChildImageInRegion="+data.nextChildImageInRegion*/);

            // if there were values, fill in the HashMap
            NodeList valueList = reply.getElementsByTagName(T_VALUE_LIST);
            if (valueList.getLength() > 0) { // Note: length will be 0 or 1

                data.valueMap = MessageMap.decode((Element) (valueList.item(0)));
            }
            NodeList errorFlagList = reply.getElementsByTagName(T_ERROR_FLAG_LIST);
            if (errorFlagList.getLength() > 0) { // Note: length will be 0 or 1

                data.errorFlagMap = MessageMap.decode((Element) (errorFlagList.item(0)));
            }
            NodeList errorTypeList = reply.getElementsByTagName(T_ERROR_TYPE_LIST);
            if (errorTypeList.getLength() > 0) { // Note: length will be 0 or 1

                data.errorTypeMap = MessageMap.decode((Element) (errorTypeList.item(0)));
            }
            // store the result so the callback can get it
            setResult(data);
        } else {
            Log.quit("Coding unexpected message type: " + reply.getNodeName());
        }
    }
}