/* $Header: /home/common/cvsarea/ibase/dia/src/server/Handler_request_page.java,v 1.19.6.2 2006/03/21 16:42:41 nancy Exp $ */

package server;

//import common.CodingData;
import common.ImageData;
import common.Log;
import common.msg.MessageWriter;

import org.w3c.dom.Element;

/**
 * Handler for request_page message.  Obtains data for a page (image) to
 * be displayed on the image side of the viewer.
 * @see common.ImageData
 * @see MarshallPage
 * @see client.TaskRequestImage
 */
final public class Handler_request_page extends Handler {

    /**
     * This class cannot be instantiated.
     */
    public Handler_request_page() {}

    /** 
     * This handler is read-only.
     * @return true
     */
    public boolean isReadOnly() {
        return true;
    }

    public void run (ServerTask task, Element action)
    throws java.io.IOException, java.sql.SQLException {
        int givenPageId = Integer.parseInt(action.getAttribute(A_PAGE_ID));
        int delta       = Integer.parseInt(action.getAttribute(A_DELTA));
        int boundary    = Integer.parseInt(action.getAttribute(A_BOUNDARY));
        boolean findEnd = "YES".equals(action.getAttribute(A_FIND_LAST));
        //Log.print("Request image: "+givenPageId+" "+delta+" "+boundary+" "+findEnd);

        MarshallPage m = MarshallPage.makeInstance(task, action);
        int pos = m.findRelative(givenPageId, delta, boundary, findEnd);
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
