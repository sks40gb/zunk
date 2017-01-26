/* $Header: /home/common/cvsarea/ibase/dia/src/server/BatchIO.java,v 1.68.2.5 2006/03/22 20:27:15 nancy Exp $ */
package server;

import common.Log;
import common.StatusConstants;
import common.msg.MessageConstants;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Creation and maintenance of batches.
 */
public class BatchIO implements MessageConstants
{

   /**
     * This class cannot be instantiated.
     */
   private BatchIO()
   {
   }

   /**
     * Store new status in the batch table when a batch is closed.
     * Batch may be rejected and sent to previous stage or accepted
     * and sent to next stage.
     * Also delete any current assignment.
     * @param task current ServerTask to handle the connection from
     * the calling client to the coding server
     * @param volume_id the volume.volume_id that contains the given batch_id
     * @param batch_id the batch.batch_id of the batch whose status
     * is about to be updated
     * @param status the current (old) status of the batch
     * @param reject true to reject this batch; false to close it for
     * the current status
     */
   public static void updateStatus(ServerTask task, int volume_id, int batch_id, String status, boolean reject)
           throws SQLException
   {

      // TBD: don't really need to do query first
      Connection con = task.getConnection();
      Statement st = task.getStatement();
      //Log.print("(BatchIO).updateStatus ");

      int oldStatus = 0;
      int newStatus = 0;
      String rejectStatus = null;
      if (reject) {
         if (status.equals("CodingQC")) {
            oldStatus = StatusConstants.S_CODINGQC;
            newStatus = StatusConstants.S_CODING;
            rejectStatus = "Coding";
         }
         else if (status.equals("UQC")) {
            oldStatus = StatusConstants.S_UQC;
            newStatus = StatusConstants.S_UNITIZE;
            rejectStatus = "Unitize";
         }
         else {
            throw new ServerFailException("Invalid reject batch status.");
         }
      }
      else { // accept
         if (status.equals("Unitize")) {
            oldStatus = StatusConstants.S_UNITIZE;
            newStatus = StatusConstants.S_UQC;
         }
         else if (status.equals("UQC")) {
            oldStatus = StatusConstants.S_UQC;
            newStatus = StatusConstants.S_UCOMPLETE;
         }
         else if (status.equals("Coding")) {
            oldStatus = StatusConstants.S_CODING;
            newStatus = StatusConstants.S_CODINGQC;
         }
         else if (status.equals("CodingQC")) {
            oldStatus = StatusConstants.S_CODINGQC;
            newStatus = StatusConstants.S_QCOMPLETE;
         }
         else {
            throw new ServerFailException("Invalid close batch status.");
         }
      }

      // Make sure the batch is there and has the given status.
        // At the same time, determine the batch number and rework state.
        // wbe 2005-05-28 removed join with volume - not used, and was causing deadlock
      int batchNumber;
      int activeGroup;
      int rework;
      ResultSet rs = st.executeQuery("select B.batch_number, X.rework, X.qa_rework, B.active_group" + " from batch B" //+"   inner join volume V using (volume_id)"
              + "   left join batchuser X on X.batch_id=B.batch_id" + " where B.volume_id =" + volume_id + "   and B.batch_id =" + batch_id + "   and B.status+0 ='" + oldStatus + "'" + " for update");

      if (!rs.next()) {
         rs.close();
         Log.print("(BatchIO) batch verification failed volume/batch/oldstatus " + volume_id + "/" + batch_id + "/" + oldStatus);
         throw new ServerFailException("Unable to close batch.");
      }


      batchNumber = rs.getInt(1);
      rework = rs.getInt(2) > 0 || rs.getInt(3) > 0 ? 1 : 0;
      activeGroup = rs.getInt(4);
      Log.print("(BatchIO) activeGroup is " + activeGroup);
      rs.close();

      // update the batch comments
      String comments = "";
      rs = st.executeQuery("select comments from batch_comments where batch_id=" + batch_id);
      if (rs.next()) {
         comments = rs.getString(1) + "\n";
      }
      rs.close();
      rs = st.executeQuery("select substring(now(),1,16), user_name" + " from users" + " where users_id=" + task.getUsersId());
      rs.next();
      comments += rs.getString(1) + " " + status + ((activeGroup > 0) ? ", Group " + activeGroup + "," : "") + (reject ? " REJECTED " : " ") + rs.getString(2);
      rs.close();

      PreparedStatement ps = con.prepareStatement("replace into batch_comments" + " set batch_id=" + batch_id + "   , comments=?");
      ps.setString(1, comments);
      ps.executeUpdate();
      ps.close();

      // If this is a reject QC -> Coding AND the batch not already rework
        // add childcoded records so QCer gets credit for entire batch
        // these entries can be distinguisjed because coded_time = 0
      if (newStatus == StatusConstants.S_CODING && rework == 0) {
         st.executeUpdate("insert into childcoded" + "    (child_id, status, users_id)" + " select C.child_id, 'CodingQC'," + task.getUsersId() + " from child C" + "   left join childcoded CC" + "     on C.child_id = CC.child_id" + "       and CC.round = 0" + " where C.batch_id=" + batch_id + "   and CC.child_id is null");
      }

      // Write batchcredit record for payroll.
        // Note: this must come before status change, since status is used
      long now = System.currentTimeMillis();
      if (status.startsWith("U")) { // Unitize or UQC
            // credit last user to open the batch (if a batch has never
            // been opened, it's credited to nobody -- This could happen
            // if batch was opened only by an administrator AND an
            // administrator were allowed to close it.
            // In this case, the users_id is 0.)
            // wbe 2005-01-04 always report unitize/UQC as level 0
         st.executeUpdate("insert into batchcredit" + " (batch_id, project_id, volume_id, batch_number" + "  , credit_time, users_id, status, field_level" + "  , rework, child_count, page_count, active_group)" + " select " + batch_id + "       ,V.project_id" + "       ,V.volume_id" + "       ," + batchNumber + "       ," + now + "       ,coalesce(if(B.status='Unitize',U.coder_id,U.qc_id),0)" + "       ,'" + status + "'" + "       , 0" + "       ," + rework + "       ,count(distinct C.child_id)" + "       ,count(*)" + "       ," + activeGroup + " from page P" + "   inner join child C using (child_id)" + "   inner join batch B using (batch_id)" + "   inner join volume V using (volume_id)" + "   inner join project PR using (project_id)" + "   left join batchuser U on U.batch_id=C.batch_id" + " where B.batch_id=" + batch_id + " group by B.batch_id");
         // Since this is Unitize or UQC, delete the high-water mark
         st.executeUpdate("update batchuser" + " set last_unitized_page_id = 0" + " where batch_id=" + batch_id);
      }
      else { // Coding or CodingQC
         st.executeUpdate("insert into batchcredit" + " (batch_id, project_id, volume_id, batch_number" + " , credit_time, users_id, status, field_level" + " , rework, child_count, page_count, active_group)" + " select " + batch_id + "       ,V.project_id" + "       ,V.volume_id" + "       ," + batchNumber + "       ," + now + "       , coalesce(X.users_id, 0)" + "       ,'" + status + "'" + "       , coalesce(T.field_level, 0)" + "       ," + rework + "       ,count(distinct X.child_id)" + "       ,count(*)" + "       ," + activeGroup + " from page P" + "   inner join child C using (child_id)" + "   inner join volume V using (volume_id)" + "   inner join project PR using (project_id)" + "   left join childcoded X on X.child_id=C.child_id" + "   left join value Y on Y.child_id = C.child_id" + "                    and Y.field_name = PR.level_field_name" + "   left join projectfields F on F.project_id = PR.project_id" + "                            and F.field_name = PR.level_field_name" + "   left join tablevalue T on T.tablespec_id = F.tablespec_id" + "                         and T.value = Y.value" + " where C.batch_id=" + batch_id + "   and (X.child_id is null or X.round=0)" + " group by X.users_id, coalesce(T.field_level, 0)");
         // force a credit record for current user, even if nothing saved
         st.executeUpdate("insert into batchcredit" + " (batch_id, project_id, volume_id, batch_number" + " , credit_time, users_id, status" + " , rework, active_group)" + " select B.batch_id" + "       ,V.project_id" + "       ,V.volume_id" + "       ," + batchNumber + "       ," + now + "       ," + task.getUsersId() + "       ,'" + status + "'" + "       ," + rework + "       ," + activeGroup + " from batch B" + "   inner join volume V using (volume_id)" + "   left join batchcredit BC" + "     on BC.batch_id = B.batch_id" + "       and BC.users_id=" + task.getUsersId() + "       and BC.credit_time=" + now + " where B.batch_id=" + batch_id + "   and BC.batch_id is null");


         // Change round 0 (current round) to use a new round number
            // TBD: do we need to keep these?  Maybe we only need current round.
         ResultSet rsround = st.executeQuery("select max(X.round)+1" + " from childcoded X" + "   inner join child C using (child_id)" + " where C.batch_id=" + batch_id);
         rsround.next();
         int round = rsround.getInt(1);
         rsround.close();
         st.executeUpdate("update childcoded X" + "   inner join child C using (child_id)" + " set X.round=" + round + " where C.batch_id=" + batch_id + "   and X.round=0");

         if (oldStatus == StatusConstants.S_CODINGQC) {
            // Roll error statistics into batcherror table
                // Note: X.round=1 selects first Coding round,
                //   so errors associated with original coder
            st.executeUpdate("insert into batcherror" + "   (batch_id, users_id, credit_time, round, rework" + "   , field_count, change_count, error_count, active_group)" + " select C.batch_id, X.users_id," + now + "," + round + "," + rework + "   ,sum(E.field_count),sum(E.change_count),sum(E.error_count)" + "   ," + activeGroup + " from childcoded X" + "   inner join childerror E using(child_id)" + "   inner join child C using (child_id)" + " where C.batch_id=" + batch_id + "   and X.round=1" + " group by users_id");
            // Clear error statistics for children
            st.executeUpdate("delete childerror.*" + " from childerror" + "   inner join child C using (child_id)" + " where C.batch_id=" + batch_id);
         }
      }

      // change the status
      task.executeUpdate("update batch" + " set status = " + newStatus + " where volume_id = " + volume_id + "   and batch_id = " + batch_id);

      if (batch_id != 0) {
         // delete the assignment for the given batch
         task.executeUpdate("delete from assignment where batch_id=" + batch_id);
         // release from session
         task.executeUpdate("update session" + " set volume_id=0" + "   , batch_id=0" + " where volume_id=" + volume_id + "   and batch_id=" + batch_id);
      }

      // queue the batch to the team or former QC'er for QC or reject to user
      if (newStatus == StatusConstants.S_UQC || newStatus == StatusConstants.S_CODINGQC || newStatus == StatusConstants.S_UNITIZE || newStatus == StatusConstants.S_CODING) {
         if (reject) {
            task.executeUpdate("insert into usersqueue (users_id, batch_id, timestamp)" + " select coder_id, batch_id," + System.currentTimeMillis() + "   from batchuser" + " where batch_id =" + batch_id);
            // mark batch as rework
            st.executeUpdate("update batchuser" + " set rework=1" + " where batch_id=" + batch_id);
         }
         else {
            //Log.print("queueing: rework="+rework);
            if (rework != 0) {
               //Log.print("inserting in usersqueue");
               task.executeUpdate("insert into usersqueue (users_id, batch_id, timestamp)" + " select qc_id,batch_id," + System.currentTimeMillis() + "   from batchuser" + " where batch_id =" + batch_id);
            }
            else {
               //Log.print("inserting in teamsqueue");
               task.executeUpdate("insert into teamsqueue (teams_id, batch_id, timestamp)" + " select teams_id," + batch_id + "," + System.currentTimeMillis() + "   from users" + " where users_id =" + task.getUsersId() + "   and teams_id != 0");
            }
         }
      }

      task.commitTransaction();

   //// return credit statistics
        //ResultSet rs3 = st.executeQuery(
        //    "select P.project_id, X.batch_number, X.status, X.rework"
        //    +"    , coalesce(U.user_name,''), X.child_count, X.page_count"
        //    +" from childcredit X"
        //    +"   inner join volume V using (volume_id)"
        //    +"   inner project P using (project_id)"
        //    +"   left join users U on U.users_id=X.users_id"
        //    +" where X.volume_id="+volume_id
        //    +"   and X.batch_number="+batchNumber
        //    +"   and X.credit_time="+now
        //    +" order by U.user_name");

   }

   /**
     * Create coding batches composed of documentCount documents in the 
     * batch table for a given volume and unitize batch.
     * The unitize batch becomes UBatched.
     * @param task current ServerTask to handle the connection from
     * the calling client to the coding server
     * @param batch_id the batch.batch_id of the batch to use as input to
     * the coding batches about to be created
     * @param documentCount the approximate number of documents to put in
     * each coding batch
     */
   public static void createCodingBatches(ServerTask task, int batch_id, int documentCount)
           throws SQLException
   {
      Connection con = task.getConnection();
      Statement st = task.getStatement();
      //Log.print("(BatchIO.createCodingBatches) getting batch/UComplete "+ batch_id);

      // make sure count is positive
      if (documentCount <= 0) {
         throw new ServerFailException("Documents per batch is not positive");
      }

      // make sure the given batch_id is still status UComplete
        // also, get highest used batch number
        // Note: batch with UComplete status should never be active
      ResultSet rs = st.executeQuery("select B.volume_id, P.high_coding_batch, B.lft, B.rgt, P.project_id, active_group" + " from batch B" + "   inner join volume V using (volume_id)" + "   inner join project P using (project_id)" + " where B.batch_id = " + batch_id + "   and B.status = 'UComplete'");
      if (!rs.next()) {
         throw new ServerFailException("Batch is not unitize complete");
      }
      int volumeId = rs.getInt(1);
      int highCodingBatch = rs.getInt(2);
      int ubatchLft = rs.getInt(3);
      int ubatchRgt = rs.getInt(4);
      int projectId = rs.getInt(5);
      int activeGroup = rs.getInt(6);
      rs.close();

      PreparedStatement psBatch = task.prepareStatement("insert into batch" + " (volume_id, lft, rgt, batch_number, status, active_group)" + " values (?, ?, ?, ?, 'Coding', ?)");
      psBatch.setInt(1, volumeId);

      // Note.  not managed -- new batch not expanded yet on client, old goes away
      PreparedStatement psRange = con.prepareStatement("update child" + " set batch_id=last_insert_id()" + " where volume_id=?" + "   and lft between ? and ?");
      psRange.setInt(1, volumeId);

      // loop through all children for this batch
      Statement st2 = con.createStatement();
      rs = st2.executeQuery("select C.lft, C.rgt, (C.rgt = R.rgt)" + " from child C" + "   inner join range R using (range_id)" + " where C.volume_id=" + volumeId + " and C.lft between " + ubatchLft + " and " + ubatchRgt + " order by C.lft");
      while (rs.next()) {
         final int lft = rs.getInt(1);
         int rgt = rs.getInt(2);
         boolean isEndRange = rs.getBoolean(3);
         // count up to documentCount children
         for (int i = 1; i < documentCount; i++) {
            if (!rs.next()) {
               break;
            }
            rgt = rs.getInt(2);
            isEndRange = rs.getBoolean(3);
         }
         // TBD: need to allow for continuing to end of range
            // continue until end of range
            //while (! isEndRange) {
            //    rs.next(); // better be true, since ! isEndRange
            //    rgt = rs.getInt(2);
            //    isEndRange = rs.getBoolean(3);
            //}

         // create a new batch
         highCodingBatch++;
         psBatch.setInt(2, lft);
         psBatch.setInt(3, rgt);
         psBatch.setInt(4, highCodingBatch);
         psBatch.setInt(5, activeGroup > 0 ? 1 : 0);
         psBatch.executeUpdate();

         // adjust batch_id in children
         psRange.setInt(2, lft);
         psRange.setInt(3, rgt);
         psRange.executeUpdate();
      }

      st2.close();
      psBatch.close();
      psRange.close();

      // Adjust the high-water mark
        // Note.  not managed.
      st.executeUpdate("update project" + " set high_coding_batch=" + highCodingBatch + " where project_id=" + projectId);

      // update the status of the Unitize batch being split into batches
      task.executeUpdate("update batch" + " set status = 'UBatched'" + " where batch_id = " + batch_id);
   }


   /**
     * Add or remove a batch based on the values of batch_id and child_id; move
     * a document to the next or the previous batch.  
     * <table border="1">
     * <th>    batchId     <th>childId     <th>delta     <th>action            <tb>
     * <tr><td>batch       <td>any         <td>any       <td>remove batch bdry </tr>
     * <tr><td>0           <td>range       <td>0         <td>add batch bdry    </tr>
     * <tr><td>0           <td>range       <td>-1        <td>move range up     </tr>
     * <tr><td>0           <td>range       <td>+1        <td>move range down   </tr>
     * </table>
     * @param task current ServerTask to handle the connection from
     * the calling client to the coding server
     * @param batchId - 0 if this is an add batch or move range up or down;
     * otherwise, the batch_id of the batch to be removed
     * @param childId - 0 of this is a remove batch; otherwise, the child_id of the
     * first page of the batch being added.
     * @param delta - 0 if this is an add or remove batch; 1 if it is a move
     * document up; -1 if move document down
     */
   public static void batchBoundary(ServerTask task, int batchId,
           int childId, int delta)
           throws SQLException
   {

      if (batchId > 0) {
         removeBatch(task, batchId);
      }
      else if (delta == 0) {
         addBatch(task, childId);
      }
      else if (delta > 0) {
         moveChildDown(task, childId);
      }
      else { // since delta < 0
         moveChildUp(task, childId);
      }

   }

   private static void removeBatch(ServerTask task, int batchId)
           throws SQLException
   {

      Statement st = task.getStatement();

      // find the preceding batch
      ResultSet rs = st.executeQuery("select PB.batch_id, B.rgt" + "  , (B.status=PB.status and S.batch_id is null" + "       and PS.batch_id is null)" + " from batch B" + "   inner join batch PB using (volume_id)" + "   left join session S on S.batch_id=B.batch_id" + "   left join session PS on PS.batch_id=PB.batch_id" + " where B.batch_id=" + batchId + "   and PB.status-0 > 4" + "   and PB.lft < B.lft" + " order by PB.lft desc" + " limit 1");
      if (!rs.next()) {
         throw new ServerFailException("no prior batch for remove");
      }
      int priorBatchId = rs.getInt(1);
      int batchRgt = rs.getInt(2);
      boolean ok = rs.getBoolean(3);
      rs.close();

      if (!ok) {
         throw new ServerFailException("1 batches in use or different status");
      }

      // delete the batch
      task.executeUpdate("delete from batch" + " where batch_id=" + batchId);
      // fix up boundary of preceding batch
            // Note.  does not need to be managed
      st.executeUpdate("update batch" + " set rgt=" + batchRgt + " where batch_id=" + priorBatchId);
      // move the children to the preceding batch
      task.executeUpdate("update child" + " set batch_id=" + priorBatchId + " where batch_id=" + batchId);

      // remove from the assignment table and the queues
      task.executeUpdate("delete from assignment" + " where batch_id=" + batchId);
      task.executeUpdate("delete from teamsqueue" + " where batch_id=" + batchId);
      task.executeUpdate("delete from usersqueue" + " where batch_id=" + batchId);
   }


   // move the child to the preceding batch

   private static void moveChildUp(ServerTask task, int childId)
           throws SQLException
   {

      Statement st = task.getStatement();

      // find the current and preceding batches
      ResultSet rs = st.executeQuery("select C.batch_id, PB.batch_id, C.rgt" + "  , (B.status=PB.status and S.batch_id is null and PS.batch_id is null)" + " from child C" + "   inner join batch B using (batch_id)" + "   inner join batch PB using (volume_id)" + "   left join session S on S.batch_id=B.batch_id" + "   left join session PS on S.batch_id=PB.batch_id" + " where C.child_id=" + childId + "   and PB.volume_id = C.volume_id" + "   and PB.rgt < C.lft" + " order by PB.rgt desc" + " limit 1");
      if (!rs.next()) {
         throw new ServerFailException("no prior child for move up");
      }
      int batchId = rs.getInt(1);
      int priorBatchId = rs.getInt(2);
      int childRgt = rs.getInt(3);
      boolean ok = rs.getBoolean(4);
      rs.close();

      if (!ok) {
         throw new ServerFailException("2 batches in use or different status");
      }

      // find the following child
        // must be in current batch
      rs = st.executeQuery("select lft" + " from child" + " where batch_id=" + batchId + "   and lft>" + childRgt + " order by lft" + " limit 1");
      if (!rs.next()) {
         throw new ServerFailException("no next child for move up");
      }
      int nextChildLft = rs.getInt(1);
      rs.close();

      // fix up boundaries of batches
        // Note.  does not need to be managed
      st.executeUpdate("update batch" + " set lft=" + nextChildLft + " where batch_id=" + batchId);
      st.executeUpdate("update batch" + " set rgt=" + childRgt + " where batch_id=" + priorBatchId);

      // move the children to the preceding batch
      task.executeUpdate("update child" + " set batch_id=" + priorBatchId + " where batch_id=" + batchId + "   and lft <= " + childRgt);
   }


   // move the child to the following batch

   private static void moveChildDown(ServerTask task, int childId)
           throws SQLException
   {

      Statement st = task.getStatement();

      // find the current and following batches
      ResultSet rs = st.executeQuery("select C.batch_id, NB.batch_id, C.lft" + "  , (B.status=NB.status and S.batch_id is null and NS.batch_id is null)" + " from child C" + "   inner join batch B using (batch_id)" + "   inner join batch NB using (volume_id)" + "   left join session S on S.batch_id=B.batch_id" + "   left join session NS on S.batch_id=NB.batch_id" + " where C.child_id=" + childId + "   and NB.volume_id = C.volume_id" + "   and NB.lft > C.rgt" + " order by NB.lft" + " limit 1");
      if (!rs.next()) {
         throw new ServerFailException("no next batch for move down");
      }
      int batchId = rs.getInt(1);
      int nextBatchId = rs.getInt(2);
      int childLft = rs.getInt(3);
      boolean ok = rs.getBoolean(4);
      rs.close();

      if (!ok) {
         throw new ServerFailException("3 batches in use or different status");
      }

      // find the preceding child
        // must be in current batch
      rs = st.executeQuery("select rgt" + " from child C" + " where batch_id=" + batchId + "   and rgt<" + childLft + " order by rgt desc" + " limit 1");
      if (!rs.next()) {
         throw new ServerFailException("no prior child for move down");
      }
      int priorChildRgt = rs.getInt(1);
      rs.close();

      // fix up boundaries of batches
        // Note.  does not need to be managed
      st.executeUpdate("update batch" + " set rgt=" + priorChildRgt + " where batch_id=" + batchId);
      st.executeUpdate("update batch" + " set lft=" + childLft + " where batch_id=" + nextBatchId);

      // move the children to the preceding batch
      task.executeUpdate("update child" + " set batch_id=" + nextBatchId + " where batch_id=" + batchId + "   and rgt >= " + childLft);
   }

   // create a new batch starting at the given child

   private static void addBatch(ServerTask task, int childId)
           throws SQLException
   {

      Statement st = task.getStatement();

      // find the current batch and volume, as well as the preceding child
      ResultSet rs = st.executeQuery("select C.batch_id, CR.rgt, C.lft, B.rgt, B.volume_id" + "    , B.status, B.priority, P.high_coding_batch, P.high_unitize_batch" + "    , (S.batch_id is null), P.project_id, B.active_group" + " from child C" + "   inner join batch B using (batch_id)" + "   inner join volume V on V.volume_id=C.volume_id" + "   inner join project P on P.project_id=V.project_id" + "   inner join child CR on CR.volume_id=C.volume_id" + "   left join session S on S.batch_id=B.batch_id" + " where C.child_id=" + childId + "   and CR.rgt < C.lft" + "   and V.sequence > 0" + " order by CR.rgt desc" + " limit 1");
      if (!rs.next()) {
         throw new ServerFailException("no prior child for add batch");
      }
      int batchId = rs.getInt(1);
      int priorChildRgt = rs.getInt(2);
      int childLft = rs.getInt(3);
      int batchRgt = rs.getInt(4);
      int volumeId = rs.getInt(5);
      String status = rs.getString(6);
      int priority = rs.getInt(7);
      int highCoding = rs.getInt(8);
      int highUnitize = rs.getInt(9);
      boolean ok = rs.getBoolean(10);
      int projectId = rs.getInt(11);
      int activeGroup = rs.getInt(12);
      rs.close();

      if (!ok) {
         throw new ServerFailException("batch is in use");
      }

      // fix up boundary of old batches
        // Note.  does not need to be managed
      st.executeUpdate("update batch" + " set rgt=" + priorChildRgt + " where batch_id=" + batchId);

      // compute new batch number and update volume fields
        // Note.  does not need to be managed
      int batchNumber;
      if (status.charAt(0) == 'U') {
         batchNumber = highUnitize + 1;
         st.executeUpdate("update project" + " set high_unitize_batch=" + batchNumber + " where project_id=" + projectId);
      }
      else { // since it's a coding batch
         batchNumber = highCoding + 1;
         st.executeUpdate("update project" + " set high_coding_batch=" + batchNumber + " where project_id=" + projectId);
      }

      // create the new batch
      task.executeUpdate("insert into batch" + " set volume_id=" + volumeId + "   , lft=" + childLft + "   , rgt=" + batchRgt + "   , batch_number=" + batchNumber + "   , status='" + status + "'" + "   , priority=" + priority + "   , active_group=" + activeGroup);

      // move the children to the new batch
      task.executeUpdate("update child" + " set batch_id=last_insert_id()" + " where batch_id=" + batchId + "   and rgt >= " + childLft);
   }

}
