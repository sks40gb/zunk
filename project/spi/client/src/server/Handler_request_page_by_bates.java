/* $Header: /home/common/cvsarea/ibase/dia/src/server/Handler_request_page_by_bates.java,v 1.18.6.2 2006/03/21 16:42:41 nancy Exp $ */

package server;

//import common.CodingData;
import common.ImageData;
import common.Log;
import common.msg.MessageWriter;

import org.w3c.dom.Element;

/**
 * Handler for request_page_by_bates message.  Obtains data for the given Bates to
 * be displayed on the image side of the viewer.
 * @see common.ImageData
 * @see MarshallPage
 * @see client.TaskRequestImage
 */
final public class Handler_request_page_by_bates extends Handler {

    /**
     * This class cannot be instantiated.
     */
    public Handler_request_page_by_bates() {}

    /** 
     * This handler is read-only.
     * @return true
     */
    public boolean isReadOnly() {
        return true;
    }

    public void run (ServerTask task, Element action)
    throws java.io.IOException, java.sql.SQLException {
        String batesNumber = action.getAttribute(A_BATES_NUMBER);
        boolean findEnd = "YES".equals(action.getAttribute(A_FIND_LAST));
        //Log.print("Request image by bates: "+batesNumber+""+findEnd);

        MarshallPage m = MarshallPage.makeInstance(task, action);
        int pos = m.findAbsolute(batesNumber);
        //Log.print("... pageId="+pos);

        ImageData data = null;
        if (pos != 0) {
            data = m.collectImageData(pos);
        }

        MessageWriter writer = task.getMessageWriter();
        if (data != null) {
            writer.startElement(T_IMAGE_DATA);
            writer.encode(ImageData.class, data);
            writer.endElement();
        } else {
            writer.startElement(T_FAIL);
            writer.writeContent("Page not found");
            writer.endElement();
        }
    }
}
