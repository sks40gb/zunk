/* $Header: /home/common/cvsarea/ibase/dia/src/server/Handler_request_project_list.java,v 1.18.8.1 2006/03/14 15:08:47 nancy Exp $ */

package server;

//import common.CodingData;
//import common.ImageData;
//import common.Log;
//import common.msg.MessageWriter;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.Statement;
import org.w3c.dom.Element;

/**
 * Handler for request_project_list message
 */
final public class Handler_request_project_list extends Handler {

    /**
     * This class cannot be instantiated.
     */
    public Handler_request_project_list() {}

    /** 
     * This handler is read-only.
     * @return true
     */
    public boolean isReadOnly() {
        return true;
    }

    public void run (ServerTask task, Element action)
    throws SQLException, IOException {
         
        Statement st = task.getStatement();
        String whichStatus = action.getAttribute(A_STATUS);

        // We suppress writing the log for the temporary table
        // Note.  there's no index, so batches can be inserted twice
        task.createTemporaryTable(
            "create temporary table TEMP"
            +" (project_id int not null,"
            +"  batch_id int"
            +" ) type=heap");

        // We find all projects that are relevant and all batches
        // the user can open for the given status.  We put them in
        // a temporary table and count them.  Note:  We could have
        // replaced the inserts with a great big UNION ALL, but
        // it would be harder to understand, and we need the temporary
        // table, anyway.

        // A project is relevant if it has:
        //    a volume queued to the user's team,
        //    or a batch queued to the user or the user's team
        //    or a batch assigned to the user
        // If a relevant project has no available batches in this
        // status for this user, it will show up with a count of zero.

        // Find batches in usersqueue
        st.executeUpdate(
            "insert into TEMP"
            +" select V.project_id"
            +"   , if(B.status ='"+whichStatus+"', Q.batch_id, NULL)"
            +" from usersqueue Q"
            +"   inner join batch B using (batch_id)"
            +"   inner join volume V using (volume_id)"
            +" where Q.users_id="+task.getUsersId());

        // Find batches in teamqueue
        if (whichStatus.equals("Unitize") || whichStatus.equals("Coding")) {
            st.executeUpdate(
                "insert into TEMP"
                +" select V.project_id"
                +"   , if(B.status ='"+whichStatus+"'"
                +"          and UQ.batch_id is null"
                +"       , Q.batch_id, NULL)"
                +" from users U"
                +"   inner join teamsqueue Q using (teams_id)"
                +"   inner join batch B using (batch_id)"
                +"   inner join volume V using (volume_id)"
                +"   left join usersqueue UQ on UQ.batch_id=Q.batch_id"
                +" where U.users_id="+task.getUsersId());
        } else { // since whichStatus in (UQC, CodingQC)
            // Note: project but not batch if this user was the original coder
            st.executeUpdate(
                "insert into TEMP"
                +" select V.project_id"
                +"   , if(BU.coder_id <> U.users_id"
                +"          and B.status ='"+whichStatus+"'"
                +"          and UQ.batch_id is null"
                +"       , Q.batch_id, NULL)"
                +" from users U"
                +"   inner join teamsqueue Q using (teams_id)"
                +"   inner join batch B using (batch_id)"
                +"   inner join volume V using (volume_id)"
                +"   left join batchuser BU on BU.batch_id = B.batch_id"
                +"   left join usersqueue UQ on UQ.batch_id=Q.batch_id"
                +" where U.users_id="+task.getUsersId());
        }

        // Find batches in teamsvolume
        // Done in two statements, because otherwise it gets complex
        int tvCount = st.executeUpdate(
            "insert into TEMP (project_id)"
            +" select distinct V.project_id"
            +" from users U"
            +"   inner join teamsvolume Q using (teams_id)"
            +"   inner join volume V using (volume_id)");
        if (tvCount != 0
        && (whichStatus.equals("Unitize") || whichStatus.equals("Coding"))) {
            st.executeUpdate(
                "insert into TEMP"
                +" select V.project_id, B.batch_id"
                +" from users U"
                +"   inner join teamsvolume Q using (teams_id)"
                +"   inner join volume V using (volume_id)"
                +"   inner join batch B using (volume_id)"
                +"   left join assignment A on A.batch_id=B.batch_id"
                +"   left join usersqueue UQ on UQ.batch_id=B.batch_id"
                +"   left join teamsqueue TQ on TQ.batch_id=B.batch_id"
                +" where U.users_id="+task.getUsersId()
                +"   and B.status='"+whichStatus+"'"
                +"   and A.batch_id is null"
                +"   and UQ.batch_id is null"
                +"   and TQ.batch_id is null");
        }

        // add projects for user's currently-assigned batches, if any
        // batch_id is set to null.  This gives a line
        // (with zero available count) if user's active
        // batch has volume not queued for team
        // we add for all statuses
        st.executeUpdate(
            "insert into TEMP (project_id)"
            +" select V.project_id"
            +" from assignment A"
            +"   inner join batch B using (batch_id)"
            +"   inner join volume V using (volume_id)"
            +" where A.users_id="+task.getUsersId());

        // Finished updating temp table, start logging again
        task.finishedWritingTemporaryTable();

        // get projects and batch counts
        // Note.  count(distinct ... ) should give count of non-null batch_id's
        // so projects assigned to team with no batches should have 0
        ResultSet rs1 = st.executeQuery(
            "select P.project_name, count(distinct TEMP.batch_id), P.project_id"
            +" from TEMP inner join project P using (project_id)"
            +" group by project_id"
            +" order by project_name, project_id");

        Handler_sql_query.writeXmlFromResult(task, rs1);
        rs1.close();
    }
}
