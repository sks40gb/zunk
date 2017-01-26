/* $Header: /home/common/cvsarea/ibase/dia/src/server/Handler_validate_batch.java,v 1.19.8.3 2006/03/22 20:27:15 nancy Exp $ */
package server;

import common.CodingData;
import common.Log;
import common.msg.MessageWriter;

import org.w3c.dom.Element;

/**
 * Handler for validating batch message; returns the coding_data of the first
 * page containing an error.
 * @see client.TaskValidateBatch
 * @see MarshallPage
 * @see BatchValidate
 * @see ValueMapper
 */
final public class Handler_validate_batch extends Handler {

    /**
     * This class cannot be instantiated.
     */
    public Handler_validate_batch() {}

    /** 
     * This handler is read-only.
     * @return true
     */
    public boolean isReadOnly() {
        return true;
    }

    public void run (ServerTask task, Element action)
    throws java.io.IOException, java.sql.SQLException {
        boolean unitize = action.getAttribute(A_IS_UNITIZE).equals("yes") ? true : false;
        int activeGroup = Integer.parseInt((String)action.getAttribute(A_GROUP));

        int errorChildId;
        CodingData data = null;
        MarshallPage m = MarshallPage.makeInstance(task, action);
        Log.print("Validate batch: "+m.getBatchId());

        if (0 < (errorChildId = BatchValidate.run(
                                    task, m.getVolumeId(), m.getBatchId(), unitize, activeGroup)))
        {
            // error found, child id of error is returned
            int pos = m.findChild(errorChildId);
            assert pos != 0;
            data = m.collectCodingData(pos);
        }
        Log.print("... error childId=" + errorChildId);

        MessageWriter writer = task.getMessageWriter();
        if (data != null) {
            // return codingData of first error page
            writer.startElement(T_CODING_DATA);
            writer.encode(CodingData.class, data);
            ValueMapper.write(task, m.getVolumeId(), data.childId);
            writer.endElement();
        } else {
            writer.startElement(T_OK);
            writer.endElement();
        }
    }
}
