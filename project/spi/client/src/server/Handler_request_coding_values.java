/* $Header: /home/common/cvsarea/ibase/dia/src/server/Handler_request_coding_values.java,v 1.10.6.2 2006/03/21 16:42:41 nancy Exp $ */
package server;

//import common.CodingData;
import common.Log;
import common.msg.MessageWriter;

import java.sql.ResultSet;
import java.sql.Statement;
import org.w3c.dom.Element;

/**
 * Handler for request_coding_values message.  Returns the coded data
 * for a given child, relative to the current page.
 * @see client.TaskRequestCodingValues
 * @see MarshallPage
 * @see ValueMapper
 */
final public class Handler_request_coding_values extends Handler {


    /**
     * This class cannot be instantiated.
     */
    public Handler_request_coding_values() {}

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
        //Log.print("Request page_values: "+givenPageId+" "+delta+" "+boundary);

        MarshallPage m = MarshallPage.makeInstance(task, action);
        //int pos = m.findRelativeInBatch
        //        (givenPageId, delta, boundary, /* findEnd => */ false);
        int pos = m.findRelativeInBatch(givenPageId, delta, boundary);
        //Log.print("... pageId="+pos);

        MessageWriter writer = task.getMessageWriter();
        if (pos != 0) {

            // get the corresponding child id (there must be one)
            Statement st = task.getStatement();
            ResultSet rs = st.executeQuery(
                "select child_id"
                +" from page"
                +" where page_id="+pos);
            if (! rs.next()) {
                Log.quit("Handler_request_coding_values: no child");
            }
            int childId = rs.getInt(1);
            rs.close();

            writer.startElement(T_PAGE_VALUES);
            ValueMapper.write(task, m.getVolumeId(), childId);
            writer.endElement();
        } else {
            writer.startElement(T_FAIL);
            writer.writeContent("Page not found");
            writer.endElement();
        }
    }
}
