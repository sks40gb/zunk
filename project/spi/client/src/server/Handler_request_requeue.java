/* $Header: /home/common/cvsarea/ibase/dia/src/server/Handler_request_requeue.java,v 1.2.6.1 2006/03/14 15:08:47 nancy Exp $ */

package server;

import common.Log;

import java.sql.*;
import org.w3c.dom.Element;

/**
 * Add request to requeue to QCer when Coder has not opened batch
 * for a specified number of hours.  Note: does nothing unless the
 * batch specified was last opened for QC by the current user and 
 * is in Coding status.
 */
final public class Handler_request_requeue extends Handler {

    /**
     * This class cannot be instantiated.
     */
    public Handler_request_requeue() {}

    public void run (ServerTask task, Element action)
    throws SQLException {
        Log.print("in Handler_Handler_request_requeue.run");
        int batchId = Integer.parseInt(action.getAttribute(A_BATCH_ID));
        int hours = Integer.parseInt(action.getAttribute(A_HOURS));

        Statement st = task.getStatement();
        st.executeUpdate(
            "update usersqueue Q"
            +"   inner join batchuser BU on BU.batch_id = Q.batch_id"
            +"   inner join batch B using (batch_id)"
            +" set requeue_time="+(System.currentTimeMillis() + hours * 3600000)
            +"   , requeue_users_id=BU.qc_id"
            +" where Q.batch_id="+batchId
            +"   and BU.qc_id="+task.getUsersId()
            +"   and B.status='Coding'");
    }
}
