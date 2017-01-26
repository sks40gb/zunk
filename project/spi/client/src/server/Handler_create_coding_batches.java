/* $Header: /home/common/cvsarea/ibase/dia/src/server/Handler_create_coding_batches.java,v 1.5.6.1 2006/03/14 15:08:46 nancy Exp $ */
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
 * Call BatchIO to create the coding batches of the given document
 * count for the given unitize batch.batch_id.
 * @see BatchIO
 * @see client.TaskCreateCodingBatches
 */
public class Handler_create_coding_batches extends Handler {

    /**
     * This class cannot be instantiated.
     */
    public Handler_create_coding_batches() {}

    public void run (ServerTask task, Element action) throws SQLException {
        int givenBatchId = Integer.parseInt(action.getAttribute(A_BATCH_ID));
        int givenDocumentCount = Integer.parseInt(action.getAttribute(A_COUNT));
        Log.print("create_coding_batches: batch id=" + givenBatchId+" count="+givenDocumentCount);
        Element elementList = action;

        BatchIO.createCodingBatches(task, givenBatchId, givenDocumentCount);

        Log.print("... updated batchId="+givenBatchId);
    }
}
