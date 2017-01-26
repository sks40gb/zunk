/*
 * Command_users_data.java
 *
 * Created on November 20, 2007, 4:46 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package com.fossa.servlet.command;

import com.fossa.servlet.common.CommonLogger;
import com.fossa.servlet.common.SQLQueries;
import com.fossa.servlet.common.UsersData;
import com.fossa.servlet.common.msg.MessageWriter;
import com.fossa.servlet.common.msg.XmlReader;
import com.fossa.servlet.dao.DBTask;
import com.fossa.servlet.exception.ServerFailException;
import com.fossa.servlet.session.UserTask;
import java.io.IOException;
import java.sql.Timestamp;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * This class is used for adding,updating,deleting a user
 * @author bmurali
 */
public class Command_users_data implements Command {

    private PreparedStatement pst;
    private Connection con;
    private java.sql.Statement st;

    public String execute(Element action, UserTask user, DBTask dbTask, MessageWriter writer) {        
        Element givenValueList = action;
        int old_teams_id = 0;

        Node firstChild = givenValueList.getFirstChild();
        while (firstChild != null && firstChild.getNodeType() != Node.ELEMENT_NODE) {
            firstChild = firstChild.getNextSibling();
        }

        if (firstChild != null) {
            UsersData data = new UsersData();
            // fill in the int and String fields of the UsersData
            XmlReader reader = new XmlReader();
            try {

                reader.decode(givenValueList, data);
            } catch (IOException ex) {
                CommonLogger.printExceptions(this, "Exception while reading the XMLReader." , ex);
            }
            if (data != null) {
                // update or insert the users row contained in data                
                con = dbTask.getConnection();
                st = dbTask.getStatement();
                try {
                    pst = con.prepareStatement(SQLQueries.SEL_USERS_UID);
                    pst.setString(1, data.user_name);
                    ResultSet rs = pst.executeQuery();
                    int existingId = 0;
                    if (rs.next()) {
                        existingId = rs.getInt(1);
                    }
                    rs.close();
                    if ((existingId == 0 && data.users_id > 0) // user exists, changing user_name
                            || (data.users_id > 0 && existingId == data.users_id)) {  // user exists
                        // update or delete of existing user

                        rs = st.executeQuery(SQLQueries.SEL_USERS_TEAMSID + data.users_id);
                        if (rs.next()) {
                            old_teams_id = rs.getInt(1);
                        }
                        rs.close();

                        updateTeamLeader(user, dbTask, data.users_id, data.teams_id, old_teams_id);

                        if (data.last_name.length() > 0) {
                            pst = user.prepareStatement(dbTask, "UPDATE users SET teams_id = ?,user_name = ?, first_name = ?,last_name = ? , unitize = ?,uqc = ? , coding = ?, codingqc = ?,qa = ?,listing = ?, tally = ?, team_leader = ?,admin = ?,admin_users = ?, admin_project = ?,admin_batch = ?,admin_edit = ? ,admin_import = ?, admin_export = ?, admin_profit = ?,password = CASE WHEN ? = \'\' THEN password ELSE ? END ,DOJ = ?  WHERE users_id =?");
                            pst.setInt(1, data.teams_id);
                            pst.setString(2, data.user_name);
                            pst.setString(3, data.first_name);
                            pst.setString(4, data.last_name);
                            pst.setString(5, data.unitize);
                            pst.setString(6, data.uqc);
                            pst.setString(7, data.coding);
                            pst.setString(8, data.codingqc);
                            pst.setString(9, data.qa);
                            pst.setString(10, data.listing);
                            pst.setString(11, data.tally);
                            pst.setString(12, data.teamLeader);
                            pst.setString(13, data.admin);
                            pst.setString(14, data.canAdminUsers);
                            pst.setString(15, data.canAdminProject);
                            pst.setString(16, data.canAdminBatch);
                            pst.setString(17, data.canAdminEdit);
                            pst.setString(18, data.canAdminImport);
                            pst.setString(19, data.canAdminExport);
                            pst.setString(20, data.canAdminProfit);
                            pst.setString(21, data.password);
                            pst.setString(22, data.password);
                            pst.setString(23, data.dateOfJoin);
                            pst.setInt(24, data.users_id);
                           
                            pst.executeUpdate();
                            pst.close();

                            //insert record into the history table.
                            insertIntoHistoryTable(con, data.users_id, user.getUsersId(), Mode.EDIT);

                        } else {
                            // delete existing user                              
                            assert data.teams_id == 0; // because used for team leader fix                                

                            rs = st.executeQuery("SELECT 0 as '0' FROM assignment WHERE users_id=" + data.users_id + " UNION ALL SELECT 0 as '0' FROM usersqueue WHERE users_id= " + data.users_id + " UNION ALL SELECT 0 as '0' FROM session WHERE users_id=" + data.users_id);
                            if (rs.next()) {
                                throw new ServerFailException("User active or has batches queued or assigned.");
                            }
                            rs.close();

                            //insert record into the history table before deletion of records.
                            insertIntoHistoryTable(con, data.users_id, user.getUsersId(), Mode.DELETE);

                            pst = user.prepareStatement(dbTask, SQLQueries.UPD_USERS_TEAMID);
                            pst.setInt(1, data.users_id);
                            pst.executeUpdate();
                            pst.close();
                        }
                    } else if (existingId > 0 && existingId != data.users_id) {
                        try {
                            // verify duplicate user_id
                            writer.startElement(T_ERROR);
                            writer.writeAttribute(A_FOSSAID, user.getFossaSessionId());
                            writer.writeAttribute(A_DATA, "Duplicate User Name");
                            writer.endElement();                        
                        } catch (IOException ex) {                            
                             CommonLogger.printExceptions(this,"Exception while writing the results in XML."  , ex);
                        }
                        return null;
                    } else {
                        // add new user
                        assert data.last_name.length() > 0;
                        pst = user.prepareStatement(dbTask, SQLQueries.INS_USERS_USERS);
                        pst.setInt(1, data.teams_id);
                        pst.setString(2, data.user_name);
                        pst.setString(3, data.first_name);
                        pst.setString(4, data.last_name);
                        pst.setString(5, data.unitize);
                        pst.setString(6, data.uqc);
                        pst.setString(7, data.coding);
                        pst.setString(8, data.codingqc);
                        pst.setString(9, data.qa);
                        pst.setString(10, data.listing);
                        pst.setString(11, data.tally);
                        pst.setString(12, data.teamLeader);
                        pst.setString(13, data.admin);
                        pst.setString(14, data.canAdminUsers);
                        pst.setString(15, data.canAdminProject);
                        pst.setString(16, data.canAdminBatch);
                        pst.setString(17, data.canAdminEdit);
                        pst.setString(18, data.canAdminImport);
                        pst.setString(19, data.canAdminExport);
                        pst.setString(20, data.canAdminProfit);
                        pst.setString(21, data.password);
                        pst.setString(22, data.dateOfJoin);
                        pst.executeUpdate();
                        pst.close();
                        rs = st.executeQuery(SQLQueries.SEL_USERS_TOP1);
                        rs.next();
                        data.users_id = rs.getInt(1);

                        //insert the record into the history table
                        insertIntoHistoryTable(con, data.users_id, user.getUsersId(), Mode.ADD);

                    }
                    if (data.last_name.length() > 0) {
                        PreparedStatement getUserPrepStmt = con.prepareStatement(SQLQueries.SEL_USERS_UNAME);
                        getUserPrepStmt.setString(1, data.user_name);
                        getUserPrepStmt.setInt(2, data.users_id);
                        ResultSet getUserResultSet = getUserPrepStmt.executeQuery();
                        if (getUserResultSet.next()) {
                            throw new ServerFailException(
                                    "The new User Name is already in use.");
                        }
                        getUserPrepStmt.close();
                    }
                } catch (SQLException ex) {
                    CommonLogger.printExceptions(this, "Exception while getting the users data." , ex);
                }
            }
        }

        return null;

    }

    public boolean isReadOnly() {
        return false;
    }

    //update the teams data
    private void updateTeamLeader(UserTask user, DBTask dbTask, int users_id, int teams_id, int old_teams_id)
            throws SQLException {
        if ((old_teams_id == 0 && teams_id == 0) || old_teams_id == teams_id) {
            // no update necessary
            return;
        }
        ResultSet getUserIdResultSet = st.executeQuery(SQLQueries.SEL_USERS_USRID + old_teams_id);

        if (getUserIdResultSet.next()) {
            if (users_id == getUserIdResultSet.getInt(1)) {
                // this users has been assigned to a different team,
                // so can no longer be leader of his old team.
                pst = user.prepareStatement(dbTask, SQLQueries.UPD_USERS_TEAMS);
                pst.setInt(1, 0);
                pst.setInt(2, old_teams_id);
                pst.executeUpdate();
                pst.close();
            }
        }
        getUserIdResultSet.close();
    }

    /**
     * Put the users record into the history table while Adding, 
     * Editing, or Deleting the records
     * @param con - Connection 
     * @param user_id - users data whome data needs to be stored.
     * @param h_userId - userid of user performing these operation
     * @param mode - Operation mode can be
     *                a) ADD
     *                b) EDIT
     *                C) DELETE 
     */
    public void insertIntoHistoryTable(Connection con, int user_id, int h_userId, String mode) {
        try {

            String sql = "INSERT INTO history_users (" +
                    "  users_id, active, teams_id, user_name, last_name, " +
                    "  first_name, unitize, uqc, coding, codingqc, qa, team_leader, " +
                    "  admin, admin_users, admin_project, admin_batch, admin_edit," +
                    "  admin_import, admin_export, admin_profit, password, DOJ, " +
                    "  listing, tally, h_users_id, mode, date) " +
                    "SELECT " +
                    "  users_id, active, teams_id, user_name, last_name, first_name," +
                    "  unitize, uqc, coding, codingqc, qa, team_leader, admin, " +
                    "  admin_users, admin_project, admin_batch, admin_edit," +
                    "  admin_import, admin_export, admin_profit, password, DOJ, " +
                    "  listing, tally, ?, ? , ? FROM users WHERE users_id = ?";

            PreparedStatement pst = con.prepareStatement(sql);
            pst.setInt(1, h_userId);
            pst.setString(2, mode);
            pst.setTimestamp(3, new Timestamp(new Date().getTime()));
            pst.setInt(4, user_id);
            pst.executeUpdate();
            pst.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
