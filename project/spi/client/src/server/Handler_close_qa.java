/* $Header: /home/common/cvsarea/ibase/dia/src/server/Handler_close_qa.java,v 1.6.6.3 2006/03/14 15:08:46 nancy Exp $ */

package server;

import common.Log;
import common.StatusConstants;
import common.msg.MessageWriter;
import common.msg.XmlUtil;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.w3c.dom.Element;

/**
 * Handler for close_qa message which updates the database on the closing
 * of a qa selection.  When reject, the batches are marked as rework; when
 * close, check that each batch was saved, then change the status to QAComplete
 * if there are no groups in this project or this is the last group,
 * and back to Coding if there are groups and this is not the last group.
 * <p> Keep statistics in childcoded, volumeerror and childerror.
 */
final public class Handler_close_qa extends Handler {

    /**
     * This class cannot be instantiated.
     */
    public Handler_close_qa() {}

    public void run (ServerTask task, Element action)
    throws SQLException, IOException {

        String teamsIdString = action.getAttribute(A_TEAMS_ID);
        String usersIdString = action.getAttribute(A_USERS_ID);
        boolean reject = "YES".equals(action.getAttribute(A_REJECT));
        String comments = XmlUtil.getTextFromNode(action);
        String newStatus = (reject ? "CodingQC" : "QAComplete");
        long now = System.currentTimeMillis();
        int activeGroup = 0;
        int nextGroup = 0;
        int maxGroup = 0;

        int volumeId = task.getLockVolumeId();
        Log.print("in Handler_close_qa.run teams="+teamsIdString+"/"+teamsIdString.length()
                  +" users="+usersIdString+"/"+usersIdString.length()+" reject="+reject
                  +" volume="+volumeId);

        EventLog.close(task, volumeId, /* batchId */ 0, "QA");

        Connection con = task.getConnection();
        Statement st = task.getStatement();

        // see if we're dealing with groups
        ResultSet rs = st.executeQuery(
            "select max(PF.field_group)"
            +" from volume"
            +"   inner join project using (project_id)"
            +"   inner join projectfields PF using (project_id)"
            +" where volume_id="+volumeId);
        if (rs.next()) {
            maxGroup = rs.getInt(1);
        }
        Log.print("(Handler_close_qa) maxGroup is " + maxGroup);
        rs.close();

        // mark batches to be closed
        int count = st.executeUpdate(
            "update batch B"
            +(teamsIdString.length() == 0 && usersIdString.length() == 0  ? ""
              : " inner join batchuser BU using (batch_id)")
            +(teamsIdString.length() == 0  ? ""
              : " inner join users U on U.users_id = BU.coder_id")
            +" set status=''"
            +" where B.volume_id="+volumeId
            +"   and B.status='QA'"
            +(usersIdString.length() == 0  ? ""
              : " and BU.coder_id="+usersIdString)
            +(teamsIdString.length() == 0  ? ""
              : " and U.teams_id="+teamsIdString) );

        //System.out.println("...batches selected: "+count);
        if (count > 0) {
            // there are some batches selected.  Close/reject them.
            ResultSet rs1 = st.executeQuery(
                "select max(round)"
                +" from childcoded CC"
                +"   inner join child C using (child_id)"
                +"   inner join batch B using (batch_id)"
                +" where B.volume_id="+volumeId
                +"   and B.status=''");
            if (rs1.next()) {
                int round = 1+rs1.getInt(1);
                //System.out.println("...round="+round);

                if (reject) {
                    // For reject, mark batches as qa rework
                    st.executeUpdate(
                        "update batchuser BU"
                        +" inner join batch B using (batch_id)"
                        +" set qa_rework=1"
                        +" where B.volume_id="+volumeId
                        +"   and B.status=''");
                } else {
                    // For accept, check that all selected children have been saved
                    rs1 = st.executeQuery(
                        "select 0"
                        +" from childcoded CC"
                        +"   inner join child C using (child_id)"
                        +"   inner join batch B using (batch_id)"
                        +" where B.volume_id="+volumeId
                        +"   and B.status=''"
                        +"   and CC.round=0"
                        +"   and CC.coded_time = 0"
                        +" limit 1");
                    if (rs1.next()) {
                        throw new ServerFailException("There are unsaved documents.");
                    }
                }

                st.executeUpdate(
                    "update childcoded CC"
                    +"   inner join child C using (child_id)"
                    +"   inner join batch B using (batch_id)"
                    +" set CC.round = "+round
                    +" where B.volume_id="+volumeId
                    +"   and B.status=''"
                    +"   and CC.round=0");

                // Roll error statistics into volumeerror table
                // Note: X.round=1 selects first Coding round,
                //   so errors associated with original coder
                st.executeUpdate(
                    "insert into volumeerror"
                    +"   (volume_id, users_id, credit_time, round, rework"
                    +"   , field_count, change_count, error_count)"
                    +" select B.volume_id, X.users_id,"+now+","+round
                    +"   ,BU.rework"
                    +"   ,sum(E.field_count),sum(E.change_count),sum(E.error_count)"
                    +" from childcoded X"
                    +"   inner join childerror E using(child_id)"
                    +"   inner join child C using (child_id)"
                    +"   inner join batch B using (batch_id)"
                    +"   inner join batchuser BU using (batch_id)"
                    +" where B.volume_id="+volumeId
                    +"   and B.status = ''"
                    +"   and X.round=1"
                    +" group by users_id, BU.rework");
                // Clear error statistics for children
                st.executeUpdate(
                    "delete childerror.*"
                    +" from childerror"
                    +"   inner join child C using (child_id)"
                    +"   inner join batch B using (batch_id)"
                    +" where B.volume_id="+volumeId
                    +"   and B.status = ''");

                // add to batch comments
                if (comments.length() > 0) {
                    comments += "\n";
                }
                rs = st.executeQuery(
                    "select substring(now(),1,16), user_name"
                    +" from users"
                    +" where users_id="+task.getUsersId());
                rs.next();
                comments += rs.getString(1)+" QA"
                            + (reject ? " REJECTED " : " ")
                            + rs.getString(2); 
                rs.close();
                PreparedStatement ps1 = con.prepareStatement(
                    "update batch_comments BC"
                    +" inner join batch B using (batch_id)"
                    +" set BC.comments = concat(BC.comments, ?)"
                    +" where B.volume_id=?"
                    +" and B.status=''");
                ps1.setString(1, "\n"+comments);
                ps1.setInt(2, volumeId);
                ps1.executeUpdate();
                ps1.close();
                // just in case there are batches without comments -- there shouldn't be
                PreparedStatement ps2 = con.prepareStatement(
                    "insert ignore into batch_comments (batch_id, comments)"
                    +" select batch_id, ?"
                    +" from batch"
                    +" where volume_id=?"
                    +" and status=''");
                ps2.setString(1, "\n"+comments);
                ps2.setInt(2, volumeId);
                ps2.executeUpdate();
                ps2.close();


                // Set the final status
                //System.out.println("...about to set status");
                if (maxGroup > 0
                    && newStatus.equals("QAComplete")) {
                    // If we're dealing with groups, set the status back to Coding
                    // for the next group.
                    task.executeUpdate(
                        "update batch"
                        +" set status = if(active_group < "+maxGroup+", 'Coding','"+newStatus+"')"
                        +"   , active_group = if(active_group < "+maxGroup+", active_group+1,active_group)"
                        +" where volume_id="+volumeId
                        +"   and status=''");
                } else {
                    // 2007-05-25: self-test, Modified by V.Sathiyanarayanan
                    /*task.executeUpdate(
                        "update batch"
                        +" set status = "+newStatus+"'"
                        +" where volume_id="+volumeId
                        +"   and status=''");*/
                    
                    task.executeUpdate(
                        "update batch"
                        +" set status = '"+newStatus+"'"
                        +" where volume_id="+volumeId
                        +"   and status=''");
                    // ends here
                }
            }
        }

        // return the count of batches to the client
        MessageWriter writer = task.getMessageWriter();
        writer.startElement(T_UPDATE_COUNT);
        writer.writeAttribute(A_COUNT, count);
        writer.endElement();
    }
}
