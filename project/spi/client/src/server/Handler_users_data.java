/* $Header: /home/common/cvsarea/ibase/dia/src/server/Handler_users_data.java,v 1.21.6.4 2006/03/22 20:27:15 nancy Exp $ */
package server;

//import client.MessageMap;
import common.Log;
import common.msg.MessageReader;
import common.msg.MessageWriter;
import common.UsersData;

import java.io.IOException;
import java.sql.*;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Handler for users_data message to add, update or delete
 * <code>users</code> data.
 * @see common.UsersData
 * @see client.TaskSendUsersData
 */
final public class Handler_users_data extends Handler {
    
    PreparedStatement pst;
    Connection con;
    Statement st;

    /**
     * This class cannot be instantiated.
     */
    public Handler_users_data() {
    }

    public void run (ServerTask task, Element action) 
    throws SQLException, IOException {
        Log.print("Handler_users_data");
        Element givenValueList = action;
        int old_teams_id = 0;

        //Note.  "child" may be ignored white space, if not validating parser
        //if (givenValueList.hasChildNodes()) {
        Node firstChild = givenValueList.getFirstChild();
        while (firstChild != null && firstChild.getNodeType() != Node.ELEMENT_NODE) { 
            firstChild = firstChild.getNextSibling();
        }
        if (firstChild != null) {
            UsersData data = new UsersData();
            // fill in the int and String fields of the UsersData
            MessageReader.decode(givenValueList, data);
            if (data != null) {
                // update or insert the users row contained in data
                Log.print("(Handler_users_data.run) users_id=" + data.users_id);
                con = task.getConnection();
                st = task.getStatement();
                pst = con.prepareStatement(
                    "select users_id"
                    +" from users"
                    +"   where user_name = ?"
                    +"     and active");
                pst.setString(1, data.user_name);
                ResultSet rs = pst.executeQuery();
                int existingId = 0;
                if (rs.next()) {
                    existingId = rs.getInt(1);
                }
                rs.close();
                if ( (existingId == 0
                      && data.users_id > 0) // user exists, changing user_name
                    || (data.users_id > 0
                      && existingId == data.users_id)) {  // user exists
                    // update or delete of existing user
                    rs = st.executeQuery(
                        "select teams_id"
                        +" from users"
                        +"   where users_id ="+data.users_id);
                    if (rs.next()) {
                        old_teams_id = rs.getInt(1);
                    }
                    rs.close();

                    updateTeamLeader(task, data.users_id, data.teams_id, old_teams_id);

                    if (data.last_name.length() > 0) {
                        // change existing user
                        pst = task.prepareStatement(
                            "update users"
                            +" set teams_id = ?,"
                            +" user_name = ?, first_name = ?,"
                            +" last_name = ?, unitize = ?,"
                            +" uqc = ?, coding = ?, codingqc = ?,"
                            +" qa = ?, team_leader = ?, admin = ?,"
                            +" admin_users = ?, admin_project = ?, admin_batch = ?,"
                            +" admin_edit = ?, admin_import = ?, admin_export = ?, admin_profit = ?,"
                            +" password = IF(?=\'\', password, ?)"
                            +" where users_id = ?");
                        pst.setInt(1, data.teams_id);
                        pst.setString(2, data.user_name);
                        pst.setString(3, data.first_name);
                        pst.setString(4, data.last_name);
                        pst.setString(5, data.unitize);
                        pst.setString(6, data.uqc);
                        pst.setString(7, data.coding);
                        pst.setString(8, data.codingqc);
                        pst.setString(9, data.qa);
                        pst.setString(10, data.teamLeader);
                        pst.setString(11, data.admin);
                        pst.setString(12, data.canAdminUsers);
                        pst.setString(13, data.canAdminProject);
                        pst.setString(14, data.canAdminBatch);
                        pst.setString(15, data.canAdminEdit);
                        pst.setString(16, data.canAdminImport);
                        pst.setString(17, data.canAdminExport);
                        pst.setString(18, data.canAdminProfit);
                        pst.setString(19, data.password);
                        pst.setString(20, data.password);
                        pst.setInt(21, data.users_id); // where
                        pst.executeUpdate();
                        pst.close();
                    } else {
                        // delete existing user
                        assert data.teams_id == 0; // because used for team leader fix

                        rs = st.executeQuery(
                            "select 0"
                            +" from assignment where users_id="+data.users_id
                            +" UNION ALL"
                            +" select 0"
                            +" from usersqueue where users_id="+data.users_id
                            +" UNION ALL"
                            +" select 0"
                            +" from session where users_id="+data.users_id);
                        if (rs.next()) {
                            throw new ServerFailException
                                ("User active or has batches queued or assigned.");
                        }
                        rs.close();

                        pst = task.prepareStatement(
                            "update users"
                            +" set teams_id = 0,"
                            +" active = 0,"
                            +" unitize = 'No',"
                            +" uqc = 'No', coding = 'No', codingqc = 'No',"
                            +" qa = 'No', team_leader = 'No', admin = 'No',"
                            +" admin_users = 'No', admin_project = 'No', admin_batch = 'No',"
                            +" admin_edit = 'No', admin_import = 'No', admin_export = 'No',"
                            +" admin_profit = 'No',"
                            +" password = ''"
                            +" where users_id = ?");
                        pst.setInt(1,data.users_id);
                        pst.executeUpdate();
                        pst.close();
                    }
                } else if (existingId > 0
                           && existingId != data.users_id) {
                    // duplicate user_id
                    MessageWriter writer = task.getMessageWriter();
                    writer.startElement(T_ERROR);
                    writer.writeAttribute(A_DATA, "Duplicate User Name");
                    writer.endElement();
                    return;
                } else {
                    // add new user
                    assert data.last_name.length() > 0;
                    pst = task.prepareStatement(
                        "insert into users"
                        +" set active = 1, teams_id = ?,"
                        +" user_name = ?, first_name = ?,"
                        +" last_name = ?, unitize = ?,"
                        +" uqc = ?, coding = ?, codingqc = ?,"
                        +" qa = ?, team_leader = ?, admin = ?,"
                        +" admin_users = ?, admin_project = ?, admin_batch = ?,"
                        +" admin_edit = ?, admin_import = ?, admin_export = ?, admin_profit = ?,"
                        +" password = ?");
                    pst.setInt(1, data.teams_id);
                    pst.setString(2, data.user_name);
                    pst.setString(3, data.first_name);
                    pst.setString(4, data.last_name);
                    pst.setString(5, data.unitize);
                    pst.setString(6, data.uqc);
                    pst.setString(7, data.coding);
                    pst.setString(8, data.codingqc);
                    pst.setString(9, data.qa);
                    pst.setString(10, data.teamLeader);
                    pst.setString(11, data.admin);
                    pst.setString(12, data.canAdminUsers);
                    pst.setString(13, data.canAdminProject);
                    pst.setString(14, data.canAdminBatch);
                    pst.setString(15, data.canAdminEdit);
                    pst.setString(16, data.canAdminImport);
                    pst.setString(17, data.canAdminExport);
                    pst.setString(18, data.canAdminProfit);
                    pst.setString(19, data.password);
                    pst.executeUpdate();
                    pst.close();

                    rs = st.executeQuery("select last_insert_id()");
                    rs.next();
                    data.users_id = rs.getInt(1);
                }
                if (data.last_name.length() > 0) {

                    // Check that we are not using an active user_name
                    // (We'll rollback and tell the user if we are.)
                    PreparedStatement pst2 = con.prepareStatement(
                         "select 0"
                         +" from users"
                         +" where user_name=?"
                         +"   and users_id<>"+data.users_id
                         +"   and active"
                         +" limit 1");
                    pst2.setString(1, data.user_name);
                    ResultSet rs2 = pst2.executeQuery();
                    if (rs2.next()) {
                        throw new ServerFailException (
                                "The new User Name is already in use.");
                    }
                    pst2.close();
                }
            }
        }
    }

    /**
     * If the leader of a team as been assigned to a different team,
     * remove his teams_id from his old team.
     * @param users_id - if of the user being updated
     * @param teams_id - user's current team
     * @param old_teams_id - team from which the user has been removed
     */
    private void updateTeamLeader(ServerTask task, int users_id, int teams_id, int old_teams_id)
    throws SQLException {
        if ((old_teams_id == 0 
             && teams_id == 0)
            || old_teams_id == teams_id) {
            // no update necessary
            return;
        }
        ResultSet rs = st.executeQuery(
            "select users_id"
            +" from teams"
            +"   where teams_id ="+old_teams_id);
        if (rs.next()) {
            if (users_id == rs.getInt(1)) {
                // this users has been assigned to a different team,
                // so can no longer be leader of his old team.

                pst = task.prepareStatement(
                    "update teams set users_id = ?"
                    +" where teams_id = ?");
                pst.setInt(1, 0);
                pst.setInt(2, old_teams_id);
                pst.executeUpdate();
                pst.close();
            }
        }
        rs.close();
    }
}
