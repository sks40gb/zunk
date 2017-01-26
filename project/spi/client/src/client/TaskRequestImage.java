/* $Header: /home/common/cvsarea/ibase/dia/src/client/TaskRequestImage.java,v 1.13.6.2 2006/03/21 16:42:41 nancy Exp $ */
package client;

import common.ImageData;
import common.Log;
import common.msg.MessageReader;
import common.msg.MessageWriter;

import java.io.IOException;
import org.w3c.dom.Element;

/**
 * ClientTask to retrieve the data necessary to locate an image on the server.
 * @see common.ImageData
 * @see common.msg.MessageReader
 * @see server.Handler_request_page_by_bates
 * @see server.Handler_request_page
 */
public class TaskRequestImage extends ClientTask {

    private String batesNumber = null;
    private int pageId = 0;
    private int delta;
    private int boundary;
    private String whichStatus;
    private boolean findLast;
    final private ServerConnection scon = Global.theServerConnection;

    /**
     * Create an instance of this class and remember the parameters.
     * @param whichStatus the status of the calling user; for use in
     * determining whether error flag data should be collected.
     */
    public TaskRequestImage(String batesNumber, String whichStatus) {
        this.whichStatus = whichStatus;
        this.batesNumber = batesNumber;
    }

    /**
     * @param pageId may be 0; the page.page_id of the current page
     * @param delta the relative position of the return page to the
     * given pageId or the beginning or end of the current batch
     * @param boundary may be common.msg.MessageConstants.B_NONE,
     * common.msg.MessageConstants.B_CHILD,
     * common.msg.MessageConstants.B_RANGE
     * @param whichStatus the status of the calling user; for use in
     * determining whether error flag data should be collected.
     * @param findLast true to find the last child in the range; false for the first
     */
    public TaskRequestImage(int pageId, int delta, int boundary,
            String whichStatus, boolean findLast) {
        this.pageId = pageId;
        this.delta = delta;
        this.boundary = boundary;
        this.whichStatus = whichStatus;
        this.findLast = findLast;
    }

    /**
     * Write the message with attributes and set the result with the
     * data decoded into <code>common.ImageData</code>.
     */
    public void run() throws IOException {
        MessageWriter writer;
        if (batesNumber != null) {
            writer = scon.startMessage(T_REQUEST_PAGE_BY_BATES);
            writer.writeAttribute(A_BATES_NUMBER, batesNumber);
        } else { // since batesNumber == null

            writer = scon.startMessage(T_REQUEST_PAGE);
            writer.writeAttribute(A_PAGE_ID, pageId);
            writer.writeAttribute(A_DELTA, delta);
            writer.writeAttribute(A_BOUNDARY, boundary);
            if (findLast) {
                writer.writeAttribute(A_FIND_LAST, "YES");
            }
        }
        if ("Binder".equals(whichStatus)) {
            writer.writeAttribute(A_STATUS, "Binder");
        }
        addStandardAttributes(writer);
        writer.endElement();
        writer.close();

        Element reply = scon.receiveMessage();

        if (T_IMAGE_DATA.equals(reply.getNodeName())) {
            ImageData data = new ImageData();
            MessageReader.decode(reply, data);
            //Log.print("pageId="+data.pageId);
            // store the result so the callback can get it
            setResult(data);
        } else {
            Log.quit("Unexpected message type: " + reply.getNodeName());
        }
    }
}
