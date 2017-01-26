/* $Header: /home/common/cvsarea/ibase/dia/src/server/Handler_delete_project.java,v 1.5.6.1 2006/03/14 15:08:46 nancy Exp $ */
package server;

import common.Log;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.w3c.dom.Element;

/**
 * Mark a project as deleted by setting project.active to zero and
 * delete all associated data.  Return an error message and do not
 * delete if the project is assigned to a user.
 * @see client.TaskDeleteProject
 */
public class Handler_delete_project extends Handler {

    /**
     * This class cannot be instantiated.
     */
    public Handler_delete_project() {}

    public void run (ServerTask task, Element action) 
    throws SQLException, IOException {
        int projectId = Integer.parseInt(action.getAttribute(A_PROJECT_ID));
        Log.print("Handler_delete_project projectId="+projectId);

        Statement st = task.getStatement();
        ResultSet rs = st.executeQuery(
            "select 0"
            +" from assignment"
            +"   inner join batch using (batch_id)"
            +"   inner join volume using (volume_id)"
            +" where project_id="+projectId
            +" UNION ALL"
            +" select 0"
            +" from session"
            +"   inner join volume using (volume_id)"
            +" where project_id="+projectId
            +" limit 1");
        if (rs.next()) {
            throw new ServerFailException(
                "Project has batches assigned or in use.");
        }

        st.executeUpdate(
            "delete pageissue.*"
            +" from pageissue"
            +"   inner join volume V using (volume_id)"
            +" where V.project_id="+projectId);

        st.executeUpdate(
            "delete binder_image.*"
            +" from binder_image"
            +"   inner join page using (page_id)"
            +"   inner join volume V using (volume_id)"
            +" where V.project_id="+projectId
            +"   and V.sequence = -1");

        st.executeUpdate(
            "delete page.*"
            +" from page"
            +"   inner join volume V using (volume_id)"
            +" where V.project_id="+projectId);

        st.executeUpdate(
            "delete childcoded.*"
            +" from childcoded"
            +"   inner join child using (child_id)"
            +"   inner join volume V using (volume_id)"
            +" where V.project_id="+projectId);

        st.executeUpdate(
            "delete fieldchange.*"
            +" from fieldchange"
            +"   inner join child using (child_id)"
            +"   inner join volume V using (volume_id)"
            +" where V.project_id="+projectId);

        st.executeUpdate(
            "delete value.*"
            +" from value"
            +"   inner join child using (child_id)"
            +"   inner join volume V using (volume_id)"
            +" where V.project_id="+projectId);

        st.executeUpdate(
            "delete namevalue.*"
            +" from namevalue"
            +"   inner join child using (child_id)"
            +"   inner join volume V using (volume_id)"
            +" where V.project_id="+projectId);

        st.executeUpdate(
            "delete longvalue.*"
            +" from longvalue"
            +"   inner join child using (child_id)"
            +"   inner join volume V using (volume_id)"
            +" where V.project_id="+projectId);

        st.executeUpdate(
            "delete childerror.*"
            +" from childerror"
            +"   inner join child using (child_id)"
            +"   inner join volume V using (volume_id)"
            +" where V.project_id="+projectId);

        task.executeUpdate(
            "delete child.*"
            +" from child"
            +"   inner join volume V using (volume_id)"
            +" where V.project_id="+projectId);

        st.executeUpdate(
            "update batchcredit"
            +" set batch_id=0"
            +" where project_id="+projectId);

        st.executeUpdate(
            "delete batcherror.*"
            +" from batcherror"
            +"   inner join batch B using (batch_id)"
            +"   inner join volume V using (volume_id)"
            +" where V.project_id="+projectId);

        st.executeUpdate(
            "delete batchuser.*"
            +" from batchuser"
            +"   inner join batch B using (batch_id)"
            +"   inner join volume V using (volume_id)"
            +" where V.project_id="+projectId);

        task.executeUpdate(
            "delete teamsqueue.*"
            +" from teamsqueue"
            +"   inner join batch B using (batch_id)"
            +"   inner join volume V using (volume_id)"
            +" where V.project_id="+projectId);

        task.executeUpdate(
            "delete usersqueue.*"
            +" from usersqueue"
            +"   inner join batch B using (batch_id)"
            +"   inner join volume V using (volume_id)"
            +" where V.project_id="+projectId);

        task.executeUpdate(
            "delete batch.*"
            +" from batch"
            +"   inner join volume V using (volume_id)"
            +" where V.project_id="+projectId);

        st.executeUpdate(
            "delete range.*"
            +" from range"
            +"   inner join volume V using (volume_id)"
            +" where V.project_id="+projectId);

        task.executeUpdate(
            "delete teamsvolume.*"
            +" from teamsvolume"
            +"   inner join volume V using (volume_id)"
            +" where V.project_id="+projectId);

        st.executeUpdate(
            "delete volumeerror.*"
            +" from volumeerror"
            +"   inner join volume V using (volume_id)"
            +" where V.project_id="+projectId);

        task.executeUpdate(
            "delete from volume"
            +" where project_id="+projectId);

        task.executeUpdate(
            "delete from projectfields"
            +" where project_id="+projectId);

        task.executeUpdate(
            "delete tablevalue.*"
            +" from tablevalue"
            +"   inner join tablespec TS using (tablespec_id)"
            +" where TS.project_id="+projectId);

        task.executeUpdate(
            "delete from tablespec"
            +" where project_id="+projectId);

        task.executeUpdate(
            "update project"
            +" set active=0"
            +"   , level_field_name = null"
            +"   , high_volume = 0"
            +"   , lft = 0"
            +"   , rgt = 0"
            +" where project_id="+projectId);
    }
}
