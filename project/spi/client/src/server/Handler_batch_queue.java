/* $Header: /home/common/cvsarea/ibase/dia/src/server/Handler_batch_queue.java,v 1.7.6.2 2006/03/14 15:08:46 nancy Exp $ */
package server;

//import common.CodingData;
//import common.ImageData;
import common.Log;
//import common.msg.MessageWriter;

import java.io.IOException;
//import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.w3c.dom.Element;

/**
 * Handler for queuing batches and volumes to usersqueue and teamsqueue.
 * Update the usersqueue and teamsqueue tables for the given batch.batch_id.
 * @see client.TaskBatchQueue
 */
final public class Handler_batch_queue extends Handler {

    /**
     * This class cannot be instantiated.
     */
    public Handler_batch_queue() {}

    public void run (ServerTask task, Element action)
    throws SQLException, IOException {
        Log.print("Handler_batch_queue.run");

        Statement st = task.getStatement();

        /** 0 if queuing a volume; else batch_id to queue */
        int batch_id = Integer.parseInt(action.getAttribute(A_BATCH_ID));
        /** required - volume to queue if batch_id is 0; else volume_id containing the batch */
        int volume_id = Integer.parseInt(action.getAttribute(A_VOLUME_ID));
        /** 0 if queuing to a team; else users_id to use in usersqueue */
        int users_id = Integer.parseInt(action.getAttribute(A_USERS_ID));
        /** 0 if queuing to a user; else teams_id to use in teamsqueue */
        int teams_id = Integer.parseInt(action.getAttribute(A_TEAMS_ID));

        // clearQueues means a drop happened from the usersTree and all
        // occurrences of this batch should be removed from the queues
        // before the batch is added to the destination queue.
        boolean clearQueues = action.getAttribute(A_DELETE).equals("true") ? true : false;

        // make sure the batch is not assigned to a user
        if (batch_id > 0) {
            ResultSet rs = st.executeQuery(
                "select 0 from assignment A"
                +" inner join batch B using (batch_id)"
                +" where A.batch_id="+batch_id
                //+"   and B.volume_id="+volume_id  required???
                +" lock in share mode");
            if (rs.next()) {
                throw new ServerFailException("Batch is not available for queuing -- already assigned.");
            }
        }
        
        if (clearQueues) {
            task.executeUpdate("delete from usersqueue"
                               +" where batch_id = "+batch_id);
                               //+"   and volume_id = "+volume_id);
            task.executeUpdate("delete from teamsqueue"
                               +" where batch_id = "+batch_id);
                               //+"   and volume_id = "+volume_id);
        }

        try {
            if (batch_id == 0) {
                // create the volumequeue row
                Log.print("(Handler_batch_queue.run) insert volumequeue volume/team "
                          + volume_id + "/" + teams_id);
                
                // queue all of the batches in the volume
                //PreparedStatement ps = task.prepareStatement(
                //    "insert into teamsqueue"
                //    +" set batch_id=?"
                //    +"   , teams_id="+teams_id
                //    +"   , timestamp="+System.currentTimeMillis());

                //PreparedStatement psPriority = task.prepareStatement(
                //    "update batch"
                //    +" set priority = priority+1"
                //    +" where batch_id=?"
                //    +"   and volume_id="+volume_id);
                // See if there is a batch that can be queued.
                ResultSet rs = st.executeQuery(
                    "select B.batch_id from batch B"
                    +" left join assignment A using (batch_id)"
                    +" where B.volume_id="+volume_id
                    +"   and A.batch_id is null"
                    +"   and B.status not like 'QAComplete'"
                    +"   and B.status not like 'UBatched'"
                    +"   and B.status not like 'UComplete'"
                    +" limit 1");
                if (rs.next()) {
                    task.executeUpdate("insert into teamsvolume"
                                       +" set volume_id="+volume_id
                                       +"   , teams_id="+teams_id
                                       +"   , timestamp="+System.currentTimeMillis());
                //    Log.print("(Handler_batch_queue.run) batch " + rs.getInt(1));
                //    ps.setInt(1, rs.getInt(1));
                //    ps.executeUpdate();
                //    
                //    // increase the priority of the queued batch
                //    psPriority.setInt(1, rs.getInt(1));
                //    psPriority.executeUpdate();
                }
            } else {
                if (users_id > 0) {
                    // queue the batch to the user
                    task.executeUpdate(
                        "insert into usersqueue"
                        +" set batch_id="+batch_id
                        +"   , users_id="+users_id
                        +"   , timestamp="+System.currentTimeMillis());
                } else {
                    // queue the batch to the team
                    task.executeUpdate(
                        "insert into teamsqueue"
                        +" set batch_id="+batch_id
                        +"   , teams_id="+teams_id
                        +"   , timestamp="+System.currentTimeMillis());
                }

                // increase the priority of the queued batch
                task.executeUpdate(
                    "update batch"
                    +" set priority = priority+1"
                    +" where batch_id = "+batch_id);
                    //+"   and volume_id = "+volume_id);
            }
        } catch (SQLException e) {
            // ignore dups for now
            String sqlState = e.getSQLState();
            int errorCode = e.getErrorCode();
            Log.print(">>>"+e+" sqlState="+sqlState+" errorCode="+errorCode);
            if (errorCode == ServerTask.ER_DUP_ENTRY ) {
                // it's a dup, ignore it
                Log.print("(Handler_batch_queue.run) duplicate queue key");
            } else {
                throw e;
            }
        }
    }
}
