/* $Header: /home/common/cvsarea/ibase/dia/src/server/Handler_batch_boundary.java,v 1.6.6.2 2006/03/14 15:08:46 nancy Exp $ */
package server;

//import client.MessageMap;
import common.Log;
//import common.msg.MessageReader;
//import common.msg.MessageWriter;

import java.sql.SQLException;
//import java.util.Map;
import org.w3c.dom.Element;
//import org.w3c.dom.Node;

/**
 * Handler for changing batch boundaries.
 * Read the message with attributes and call <code>BatchIo.batchBoundary</code>
 * to perform the task.
 */
public class Handler_batch_boundary extends Handler {

    /**
     * This class cannot be instantiated.
     */
    public Handler_batch_boundary() {}

    public void run (ServerTask task, Element action) throws SQLException {
        int givenBatchId = getInt(action, A_BATCH_ID);
        int givenChildId = getInt(action, A_ID);
        int givenDelta = getInt(action, A_DELTA);
        Log.print("Handler_batch_boundary: " + givenBatchId 
                  + "/" + givenChildId + "/" + givenDelta);
        Element elementList = action;
        
        BatchIO.batchBoundary(task, givenBatchId, givenChildId, givenDelta);
    }

    // get integer attribute, or zero if empty string
    private int getInt(Element action, String attribute) {
        String attributeString = action.getAttribute(attribute);
        return (attributeString.length() == 0 ? 0 : Integer.parseInt(attributeString));
    }
}
