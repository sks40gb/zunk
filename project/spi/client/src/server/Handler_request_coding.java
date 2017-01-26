/* $Header: /home/common/cvsarea/ibase/dia/src/server/Handler_request_coding.java,v 1.22.6.2 2006/03/21 16:42:41 nancy Exp $ */

package server;

import common.CodingData;
import common.Log;
import common.msg.MessageWriter;

import org.w3c.dom.Element;

/**
 * Handler for request_coding message, used to request project data for one
 * document from the server.
 * @see client.TaskRequestCoding
 * @see server.MarshallPage
 */
final public class Handler_request_coding extends Handler {

    /**
     * This class cannot be instantiated.
     */
    public Handler_request_coding() {}

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
        //boolean findEnd = "YES".equals(action.getAttribute(A_FIND_LAST));
        String status = action.getAttribute(A_STATUS);

        int pos;
        MarshallPage m = MarshallPage.makeInstance(task, action);
        //Log.print("Request coding: "+givenPageId+" "+delta+" "+boundary
        //          +" v="+m.getVolumeId());
        if (givenPageId == 0) {
            if (delta == 0) {
                assert boundary == 0;
                pos = m.findUncoded();  // TBD: remove use of magic numbers 
            } else {
                pos = m.findPositionInBatch(delta, boundary);
            }
        } else {
            pos = m.findRelativeInBatch(givenPageId, delta, boundary);
        }
        //Log.print("(Handler_request_coding)... pos="+pos);

        CodingData data = null;
        if (pos != 0) {
            data = m.collectCodingData(pos);
            if ("CodingQC".equals(status) || "QA".equals(status)) {
                data.errorFlagMap = m.collectErrorFlagData(data.childId);
            }
        }

        MessageWriter writer = task.getMessageWriter();
        if (data != null) {
            writer.startElement(T_CODING_DATA);
            writer.encode(CodingData.class, data);
            ValueMapper.write(task, m.getVolumeId(), data.childId);
            if (data.errorFlagMap != null) {
                ValueMapper.writeErrorFlags(task, data.errorFlagMap);
            }
            writer.endElement();
        } else {
            writer.startElement(T_FAIL);
            writer.writeContent("Page not found");
            writer.endElement();
        }

    }
}
