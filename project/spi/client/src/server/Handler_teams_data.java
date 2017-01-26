/* $Header: /home/common/cvsarea/ibase/dia/src/server/Handler_teams_data.java,v 1.11.6.2 2006/03/22 20:27:15 nancy Exp $ */
package server;

import common.Log;

import java.io.IOException;
import java.sql.*;
import org.w3c.dom.Element;

/**
 * Handler for teams_data message to add or update <code>teams</code> data.
 */
final public class Handler_teams_data extends Handler {

    /**
     * This class cannot be instantiated.
     */
    public Handler_teams_data() {}

    Connection con;

    public void run (ServerTask task, Element action) 
    throws SQLException, IOException {
        Log.print("Handler_teams_data");
        Statement st = task.getStatement();
        PreparedStatement pst;
        int teams_id = Integer.parseInt(action.getAttribute(A_ID));
        int leader_users_id = Integer.parseInt(action.getAttribute(A_USERS_ID));
        String team_name = action.getAttribute(A_TEAM_NAME);
        Log.print("batch id: "+teams_id);
        //Element elementList = action;
        con = task.getConnection();
        Log.print("(Handler_teams_date.storeTeams) " + teams_id + "/" + leader_users_id + "/" + team_name);

        if (teams_id > 0) {
            if (team_name.length() > 0) {

                checkNameNotUsed (teams_id, team_name);

                // change existing team
                pst = task.prepareStatement(
                    "update teams set teams_id = ?"
                    +", users_id = ?, team_name = ?"
                    +" where teams_id = ?");
                pst.setInt(1, teams_id);
                pst.setInt(2, leader_users_id);
                pst.setString(3, team_name);
                pst.setInt(4, teams_id); // where
                pst.executeUpdate();
                pst.close();
            } else {
                // delete existing team

                assert leader_users_id == 0;

                ResultSet rs = st.executeQuery(
                    "select 0"
                    +" from users"
                    +" where teams_id = "+teams_id
                    +" and active");
                if (rs.next()) {
                    throw new ServerFailException(
                        "Team still has members.");
                }
                task.executeUpdate(
                    "delete from teams"
                    +" where teams_id="+teams_id);
            }
        } else {
            // add team

            checkNameNotUsed (0, team_name);

            pst = task.prepareStatement(
                "insert into teams"
                +"   (users_id, team_name)"
                +" values (?,?)");
            pst.setInt(1, leader_users_id);
            pst.setString(2, team_name);
            pst.executeUpdate();
            pst.close();

            ResultSet rs = st.executeQuery("select last_insert_id()");
            rs.next();
            teams_id = rs.getInt(1);
            rs.close();
        }

        // set team leader
        if (leader_users_id > 0) {
            ResultSet rs = st.executeQuery(
            "select teams_id"
            +" from users"
            +"   where users_id ="+leader_users_id);
            if (rs.next()
                && rs.getInt(1) != teams_id) {
                pst = task.prepareStatement(
                    "update users set "
                    +" teams_id = ?"
                    +" where users_id = ?");
                pst.setInt(1, teams_id);
                pst.setInt(2, leader_users_id);
                pst.executeUpdate();
                pst.close();
            }
            rs.close();
        }
    }

    // check that name isn't already used
    private void checkNameNotUsed (int teams_id, String team_name)
    throws SQLException {
        PreparedStatement pst = con.prepareStatement(
            "select 0"
            +" from teams"
            +" where team_name=?"
            +" and teams_id <> "+teams_id);
        pst.setString(1, team_name);
        ResultSet rs = pst.executeQuery();
        if (rs.next()) {
            throw new ServerFailException
                ("New Team Name is already used.");
        }
        pst.close();

    }
}
