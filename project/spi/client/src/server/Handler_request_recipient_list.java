package server;

import com.lexpar.util.Log;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.Statement;
import org.w3c.dom.Element;

/**
 * Handler for request_recipient_list message.
 * Admin users get all users, all teams and all team leaders;
 * non admin/qa users get only their team members and
 * all team leaders for projects in their queues.
 * @see client.TaskRequestRecipientList
 */
final public class Handler_request_recipient_list extends Handler {

    /**
     * This class cannot be instantiated.
     */
    public Handler_request_recipient_list() {}

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
        int usersId = task.getUsersId();
        boolean admin;
        boolean qa;
        boolean teamLeader;
        
        ResultSet rs = st.executeQuery(
            "select admin-0, qa-0, team_leader-0"
            +" from users"
            +" where users_id = "+usersId);
        if (rs.next()) {
            admin = rs.getInt(1) == 2 ? true : false;
            qa = rs.getInt(2) == 2 ? true : false;
            teamLeader = rs.getInt(3) == 2 ? true : false;
            rs.close();
        } else {
            Log.quit("(Handler_request_recipient) user not found " + usersId);
            return; // compiler
        }

        if (admin || qa || teamLeader) {
            // admin gets all users, all teams and all team leaders
            //
            // NOTE:  If you change the following text, make sure 
            // Handler_mailsent_data gets the users_id to match!
            //
            rs = st.executeQuery(
                "select user_name"
                +" from users where active"
                +" UNION ALL"
                +" select concat(T.team_name,', All Team Members')"
                +" from teams T"
                +" UNION ALL"
                +" select concat(T.team_name,', Leader ',U.user_name)"
                +" from teams T"
                +"   inner join users U on (T.users_id = U.users_id)"
                +" UNION ALL"
                +" select concat(P.project_name, ', All Teams')"
                +" from project P"
                +" where active"
                +" UNION ALL"
                +" select concat(P.project_name, ', All Leaders')"
                +" from project P"
                +" where active"
                +" UNION ALL"
                +" select 'All Users'"
                +" UNION ALL"
                +" select 'All Team Leaders'");
        } else {
            // -- non admin/qa users get only their team members and
            // all team leaders for projects in their queues --
            rs = st.executeQuery(
                " select U.user_name"
                +" from users U, users U1"
                +"  where U1.users_id = " + usersId
                +"    and U.teams_id = U1.teams_id"
                +"    and U.active"
                +"  UNION ALL"
                +"  select concat(T.team_name,', All Team Members')"
                +"  from teams T"
                +"      inner join users U on (T.teams_id = U.teams_id"
                +"         and U.users_id = "+ usersId + ")"
                +"  UNION ALL"
                +"  SELECT DISTINCT concat(P.project_name, ', All Leaders')"
                +"   from project P, teams T, users U, volume V, teamsqueue TQ, batch B"
                +"     where U.users_id = " + usersId
                +"     and (U.teams_id = T.teams_id AND TQ.teams_id = T.teams_id)"
                +"     and TQ.batch_id = B.batch_id"
                +"     and B.volume_id = V.volume_id"
                +"     and V.project_id = P.project_id"
                +"     AND P.active"
                +"  UNION ALL"
                +"  SELECT DISTINCT concat(P.project_name, ', All Leaders')"
                +"   from project P, users U, volume V, usersqueue UQ, batch B"
                +"     where U.users_id = " + usersId
                +"     and (UQ.users_id = U.users_id)"
                +"     and UQ.batch_id = B.batch_id"
                +"     and B.volume_id = V.volume_id"
                +"     and V.project_id = P.project_id"
                +"     AND P.active"
                +"  UNION ALL"
                +"  SELECT DISTINCT concat(P.project_name, ', All Leaders')"
                +"   from project P, users U, volume V, assignment A, batch B"
                +"     where U.users_id = " + usersId
                +"     and (A.users_id = U.users_id)"
                +"     and A.batch_id = B.batch_id"
                +"     and B.volume_id = V.volume_id"
                +"     and V.project_id = P.project_id"
                +"     AND P.active"
                +"  UNION ALL"
                +"  SELECT DISTINCT concat(P.project_name, ', All Leaders')"
                +"    from project P, users U, teams T, volume V, teamsvolume TV, batch B"
                +"     where U.users_id = " + usersId
                +"     and (U.teams_id = T.teams_id AND TV.teams_id = T.teams_id)"
                +"     and V.volume_id = TV.volume_id"
                +"     and V.project_id = P.project_id"
                +"     AND P.active"
                +"  UNION ALL"
                +"  select concat(T.team_name,', Leader ',U.user_name)"
                +"  from teams T, users U1"
                +"    inner join users U on (T.users_id = U.users_id"
                +"       and U1.teams_id = T.teams_id)"
                +"  where U1.users_id = " + usersId
                );
        }
        //Log.print("Handler_request_recipient_list write xml");
        Handler_sql_query.writeXmlFromResult(task, rs);
        rs.close();
    }
}
