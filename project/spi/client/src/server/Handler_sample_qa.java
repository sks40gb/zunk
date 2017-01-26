/* $Header: /home/common/cvsarea/ibase/dia/src/server/Handler_sample_qa.java,v 1.6.6.2 2006/03/21 16:42:41 nancy Exp $ */

package server;

import common.Log;
import common.StatusConstants;
import common.msg.MessageWriter;

import java.io.IOException;
import java.util.ArrayList;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.w3c.dom.Element;

/**
 * Handler for sample_qa message.
 * Selects given percent of available children from
 * those in QA batches for the open volume.
 * @see client.TaskSampleQA
 */
public final class Handler_sample_qa extends Handler {

    /**
     * This class cannot be instantiated.
     */
    public Handler_sample_qa() {}

    public void run (ServerTask task, Element action)
    throws SQLException, IOException {

        int volumeId = task.getLockVolumeId();
        assert volumeId != 0;
        assert task.getLockBatchId() == 0;
        
        int percent = Integer.parseInt(action.getAttribute(A_PERCENT));
        String usersIdString = action.getAttribute(A_USERS_ID);
        String teamsIdString = action.getAttribute(A_TEAMS_ID);
        boolean newSample = "YES".equals(action.getAttribute(A_NEW_SAMPLE));
        Log.print("in Handler_sample_qa.run vol="+volumeId+" pct="+percent
                  +" user: "+usersIdString+" team: "+teamsIdString
                  +" new_sample: "+newSample);
        
        Statement st = null;
        int count;
        if (newSample) {
            // save batches around choose code
            st = task.getStatement();
            st.executeUpdate(
                "update batch"
                +" set status =''"
                +" where volume_id="+volumeId
                +"   and status = 'QA'");
        }
        if (usersIdString.length() > 0) {
            assert teamsIdString.length() == 0;
            if (newSample) {
                // move QCComplete batches to QA (managed)
                task.executeUpdate(
                    "update batch B"
                    +"   inner join batchuser BU using (batch_id)"
                    +" set B.status ='QA'"
                    +" where B.volume_id="+volumeId
                    +"   and B.status = 'QCComplete'"
                    +"   and BU.coder_id="+usersIdString);
                }
            count = chooseQAChildrenForCoder(
                    task, percent, Integer.parseInt(usersIdString));
        } else if (teamsIdString.length() > 0) {
            if (newSample) {
                // move QCComplete batches to QA (managed)
                task.executeUpdate(
                    "update batch B"
                    +"   inner join batchuser BU using (batch_id)"
                    +"   inner join users U on BU.coder_id=U.users_id"
                    +" set B.status ='QA'"
                    +" where B.volume_id="+volumeId
                    +"   and B.status = 'QCComplete'"
                    +"   and U.teams_id="+teamsIdString);
                }
            count = chooseQAChildrenForTeam(
                    task, percent, Integer.parseInt(teamsIdString));
        } else {
            if (newSample) {
                // move QCComplete batches to QA (managed)
                task.executeUpdate(
                    "update batch"
                    +" set status ='QA'"
                    +" where volume_id="+volumeId
                    +"   and status = 'QCComplete'");
                }
            count = chooseQAChildren(task, percent);
        }
        if (newSample) {
            // restore old QA batches
            st.executeUpdate(
                "update batch"
                +" set status ='QA'"
                +" where volume_id="+volumeId
                +"   and status = ''");
        }

        // send back info
        MessageWriter writer = task.getMessageWriter();
        writer.startElement(T_UPDATE_COUNT);
        writer.writeAttribute(A_COUNT, count);
        writer.endElement();
    }
    
    /**
     * Choose given percentage of children in QA status from all teams
     * from the volume open for QA by the current user.  Selecton is
     * done on a team-by-team basis, so each team has at least the
     * given percentage.  A batch is assumed to belong to the team
     * indicated by the last coder to open it.  Selection is also made
     * for a "null" team, for users who have no team assigned.
     * @param task current ServerTask to handle the connection from
     * the client to the coding server
     * @param percent the percentage of children to sample
     * @return the number of children chosen
     */
    public static int chooseQAChildren(ServerTask task, int percent)
    throws SQLException, IOException {

        Statement st = task.getStatement();

        ResultSet rs = st.executeQuery(
            "select teams_id, count(*)"
            +" from child C"
            +"   inner join batch B using (batch_id)"
            +"   left join batchuser BU using (batch_id)"
            +"   left join users U on U.users_id = BU.coder_id"
            +"   left join childcoded CC"
            +"     on C.child_id=CC.child_id and CC.round=0" //  and CC.users_id=0"
            +" where B.volume_id="+task.getLockVolumeId()
            +"   and B.status='QA'"
            +"   and CC.child_id is null"
            +" group by U.teams_id");
        ArrayList teams = new ArrayList();
        ArrayList counts = new ArrayList();
        while (rs.next()) {
            teams.add(new Integer(rs.getInt(1)));
            counts.add(new Integer(rs.getInt(2)));
        }
        rs.close();

        int count=0;
        for (int i=0; i < teams.size(); i++) {
            int availableCount = ((Integer) counts.get(i)).intValue();
            int teamsId = ((Integer) teams.get(i)).intValue();
            int selectCount = (availableCount * percent + 99) / 100;

            count += st.executeUpdate(
                "insert into childcoded (child_id, status)"
                +" select C.child_id, 'QA'"
                +" from child C"
                +"   inner join batch B using (batch_id)"
                +"   left join batchuser BU using (batch_id)"
                +"   left join users U on U.users_id = BU.coder_id"
                +"   left join childcoded CC"
                +"     on C.child_id=CC.child_id and CC.round=0" //  and CC.users_id=0"
                +" where B.volume_id="+task.getLockVolumeId()
                +"   and B.status='QA'"
                +"   and CC.child_id is null"
                +"   and (teams_id="+teamsId
                +"        or teams_id is null and "+teamsId+"=0)"
                +" order by rand()"
                +" limit "+selectCount);
            System.out.println("... team="+teamsId+" selectCount="+selectCount
                               +" count="+count);
        }

        Log.print("chooseQAChildren: volume="+task.getLockVolumeId()+" count="+count);

        return count;
    }

    private static int chooseQAChildrenForTeam(
            ServerTask task, int percent, int teamsId)
    throws SQLException, IOException {

        Statement st = task.getStatement();

        ResultSet rs = st.executeQuery(
            "select count(*)"
            +" from child C"
            +"   inner join batch B using (batch_id)"
            +"   left join batchuser BU using (batch_id)"
            +"   left join users U on U.users_id = BU.coder_id"
            +"   left join childcoded CC"
            +"     on C.child_id=CC.child_id and CC.round=0"
            +" where B.volume_id="+task.getLockVolumeId()
            +"   and B.status='QA'"
            +"   and U.teams_id="+teamsId
            +"   and CC.child_id is null"
            +" group by U.teams_id");
        int availableCount = (rs.next() ? rs.getInt(1) : 0);
        rs.close();

        int count = 0;
        if (availableCount > 0) {
            int selectCount = (availableCount * percent + 99) / 100;
            // Note: this SQL is the same as in chooseQAChildren
            count = st.executeUpdate(
                "insert into childcoded (child_id, status)"
                +" select C.child_id, 'QA'"
                +" from child C"
                +"   inner join batch B using (batch_id)"
                +"   left join batchuser BU using (batch_id)"
                +"   left join users U on U.users_id = BU.coder_id"
                +"   left join childcoded CC"
                +"     on C.child_id=CC.child_id and CC.round=0"
                +" where B.volume_id="+task.getLockVolumeId()
                +"   and B.status='QA'"
                +"   and CC.child_id is null"
                +"   and (teams_id="+teamsId+" or teams_id is null and "+teamsId+"=0)"  
                +" order by rand()"
                +" limit "+selectCount);
        }

        Log.print("chooseQAChildrenForTeam: volume="+task.getLockVolumeId()
                  +" count="+count+" teamsId="+teamsId);

        return count;
    }

    private static int chooseQAChildrenForCoder(
            ServerTask task, int percent, int usersId)
    throws SQLException, IOException {

        Statement st = task.getStatement();

        ResultSet rs = st.executeQuery(
            "select count(*)"
            +" from child C"
            +"   inner join batch B using (batch_id)"
            +"   left join batchuser BU using (batch_id)"
            +"   left join childcoded CC"
            +"     on C.child_id=CC.child_id and CC.round=0"
            +" where B.volume_id="+task.getLockVolumeId()
            +"   and B.status='QA'"
            +"   and BU.coder_id="+usersId
            +"   and CC.child_id is null");
        rs.next();
        int availableCount = rs.getInt(1);
        rs.close();

        int selectCount = (availableCount * percent + 99) / 100;
        int count = st.executeUpdate(
            "insert into childcoded (child_id, status)"
            +" select C.child_id, 'QA'"
            +" from child C"
            +"   inner join batch B using (batch_id)"
            +"   left join batchuser BU using (batch_id)"
            +"   left join childcoded CC"
            +"     on C.child_id=CC.child_id and CC.round=0"
            +" where B.volume_id="+task.getLockVolumeId()
            +"   and B.status='QA'"
            +"   and CC.child_id is null"
            +"   and BU.coder_id="+usersId
            +" order by rand()"
            +" limit "+selectCount);

        Log.print("chooseQAChildrenForCoder: volume="+task.getLockVolumeId()
                  +" count="+count+" usersId="+usersId);

        return count;
    }
}
