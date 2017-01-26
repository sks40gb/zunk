/* $Header: /home/common/cvsarea/ibase/dia/src/server/Attic/Handler_close_event.java,v 1.1.2.3 2006/03/14 15:08:46 nancy Exp $ */

package server;

//import common.Log;
//import common.StatusConstants;
//import common.msg.MessageWriter;
//import common.msg.XmlUtil;

import java.sql.Connection;
//import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.w3c.dom.Element;

/**
 * Handler for close_event message.
 * This is enqueued from the closeMenuItem action in SplitPaneViewer to
 * update event.close_timestamp.
 */
final public class Handler_close_event extends Handler {

    /**
     * This class cannot be instantiated.
     */
    public Handler_close_event() {}

    public void run (ServerTask task, Element action)
    throws SQLException {
        int volumeId = task.getLockVolumeId();
        int batchId = task.getLockBatchId();
        String status = "";

        Connection con = task.getConnection();
        Statement st = task.getStatement();
        ResultSet rs = st.executeQuery(
                "select status"
                +" from batch"
                +" where batch_id="+batchId);
        if (rs.next()) {
            status = rs.getString(1);
        } else {
            // batchId will be 0 for QA
            status = "QA";
        }
        rs.close();

        st.executeUpdate(
                "update event set close_timestamp =" + System.currentTimeMillis()
                +" where users_id="+task.getUsersId()
                +"  and volume_id="+volumeId
                +"  and batch_id="+batchId
                +"  and status='"+status+"'"
                +"  and close_timestamp=0");
    }
}
