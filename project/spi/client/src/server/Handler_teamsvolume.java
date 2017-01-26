/* $Header: /home/common/cvsarea/ibase/dia/src/server/Handler_teamsvolume.java,v 1.3.6.2 2006/03/22 20:27:15 nancy Exp $ */
package server;

import common.Log;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.w3c.dom.Element;

/**
 * Handler for removing a volume from teamsqueue, usersqueue and teamsqueue.
 * @see client.TaskTeamsvolume
 */
final public class Handler_teamsvolume extends Handler {

    /**
     * This class cannot be instantiated.
     */
    public Handler_teamsvolume() {}

    public void run (ServerTask task, Element action)
    throws SQLException, IOException {
        Log.print("Handler_teamsvolume.run");

        Statement st = task.getStatement();

        /** required - volume to remove from all queues */
        int volume_id = Integer.parseInt(action.getAttribute(A_VOLUME_ID));
        /** 0 if removing all occurrences of the volume and batches of the volume */
        int teams_id = Integer.parseInt(action.getAttribute(A_TEAMS_ID));

        // delete the volume from teamsvolume
        if (teams_id > 0) {
            // delete volume from a particular team
            task.executeUpdate("delete from teamsvolume"
                               +" where volume_id="+volume_id
                               +"   and teams_id="+teams_id);
            // TBD - what is the following????
            //task.executeUpdate(
            //    "delete from teamsqueue"
            //    +" where batch_id=?"
            //    +"   and teams_id=?");
            //task.executeUpdate(
            //    "delete from usersqueue Q"
            //    +" inner join users U using (users_id)"
            //    +" where Q.batch_id=?"
            //    +"   and U.teams_id=?");
        } else {
            // delete the volume from all teams
            task.executeUpdate("delete from teamsvolume"
                               +" where volume_id="+volume_id);
            // now delete all batches in the volume from all teams
            PreparedStatement psTeams = task.prepareStatement(
                "delete from teamsqueue"
                +" where batch_id=?");
            PreparedStatement psUsers = task.prepareStatement(
                "delete from usersqueue"
                +" where batch_id=?");
            // get the batch_id's
            ResultSet rs = st.executeQuery(
                "select batch_id from batch"
                +" where volume_id="+volume_id);
            if (rs.next()) {
                psTeams.setInt(1, rs.getInt(1));
                psTeams.executeUpdate();
                psUsers.setInt(1, rs.getInt(1));
                psUsers.executeUpdate();
            }
        }
    }
}
