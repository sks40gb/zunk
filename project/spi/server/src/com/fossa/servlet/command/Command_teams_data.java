package com.fossa.servlet.command;

import com.fossa.servlet.common.CommonLogger;
import com.fossa.servlet.common.SQLQueries;
import com.fossa.servlet.common.msg.MessageWriter;
import com.fossa.servlet.dao.DBTask;
import com.fossa.servlet.exception.ServerFailException;
import com.fossa.servlet.session.UserTask;
import java.sql.Timestamp;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import org.w3c.dom.Element;

/**
 * This class handles the request for adding,updating,deleting a team
 * @author bmurali
 */
public class Command_teams_data implements Command {

    private int leader_users_id;
    private int teams_id;
    private String team_name;
    private Connection con;

    public String execute(Element action, UserTask user, DBTask dbTask, MessageWriter writer) {
        Statement st = dbTask.getStatement();
        PreparedStatement pst;
        teams_id = Integer.parseInt(action.getAttribute(A_ID));
        leader_users_id = Integer.parseInt(action.getAttribute(A_USERS_ID));
        team_name = action.getAttribute(A_TEAM_NAME);        
        con = dbTask.getConnection();

        if (teams_id > 0) {
            if (team_name.length() > 0) {
                try {
                    checkNameNotUsed(teams_id, team_name);
                } catch (SQLException ex) {
                   CommonLogger.printExceptions(this, "SQLException while getting the teams data.", ex);
                }
                try {
                    // update existing teams data
                    pst = user.prepareStatement(dbTask,
                            "update teams set " + " users_id = ?, team_name = ?" + " where teams_id = ?");
                    pst.setInt(1, leader_users_id);
                    pst.setString(2, team_name);
                    pst.setInt(3, teams_id);
                    pst.executeUpdate();
                    pst.close();
                    //insert the updated data into the history table (history_teams)
                    insertIntoHistoryTable(user.getUsersId(), Mode.EDIT);
                } catch (SQLException ex) {
                   CommonLogger.printExceptions(this, "Exception while updating the teams data.", ex);
                }
            } else {
                // delete existing team
                assert leader_users_id == 0;
                ResultSet getUserResultSet;
                try {
                    getUserResultSet = st.executeQuery("SELECT 0 as '0' FROM users  WHERE teams_id = " + teams_id + " and active =1");
                    if (getUserResultSet.next()) {
                        throw new ServerFailException(
                                "Team still has members.");
                    }
                    //keep the team record into the history table (history_teams) before deletitons.
                    insertIntoHistoryTable(user.getUsersId(), Mode.DELETE);
                    user.executeUpdate(dbTask, SQLQueries.DEL_TEAMS_TEAMID + teams_id);
                } catch (SQLException ex) {
                    CommonLogger.printExceptions(this, "Exception while getting the teams data.", ex);
                }
            }
        } else {
            try {
                // add team
                checkNameNotUsed(0, team_name);
            } catch (SQLException ex) {
                CommonLogger.printExceptions(this, "SQLException while getting the teams data.", ex);
            }
            try {
                pst = user.prepareStatement(dbTask, SQLQueries.INS_TEAMS_USERID);
                pst.setInt(1, leader_users_id);
                pst.setString(2, team_name);
                pst.executeUpdate();
                pst.close();
                ResultSet rs = st.executeQuery("select top 1 teams_id from teams order by teams_id desc");
                rs.next();
                teams_id = rs.getInt(1);
                rs.close();

                //insert the newly added teams record into the history table.
                insertIntoHistoryTable(user.getUsersId(), Mode.ADD);

            } catch (SQLException ex) {
                CommonLogger.printExceptions(this, "SQLException while getting the teams data.", ex);
            }
        }

        // set team leader
        if (leader_users_id > 0) {
            ResultSet getTeamsIdResultSet;
            try {
                getTeamsIdResultSet = st.executeQuery("select teams_id" + " from users" + "   where users_id =" + leader_users_id);
                if (getTeamsIdResultSet.next() && getTeamsIdResultSet.getInt(1) != teams_id) {
                    pst = user.prepareStatement(dbTask, SQLQueries.UPD_TEAMS_USERS);
                    pst.setInt(1, teams_id);
                    pst.setInt(2, leader_users_id);
                    pst.executeUpdate();
                    pst.close();
                }
                getTeamsIdResultSet.close();
            } catch (SQLException ex) {
                CommonLogger.printExceptions(this, "SQLException while getting the teams data.", ex);
            }
        }
        return null;
    }

    public boolean isReadOnly() {
        return false;
    }

    private void insertIntoHistoryTable(int userId, String mode) {
        try {
            //if the mode is delete then get the record for the teams
            if (mode.equals(Mode.DELETE)) {
                String sql = "SELECT users_id, team_name FROM teams WHERE teams_id = ?";
                PreparedStatement pst = con.prepareStatement(sql);
                pst.setInt(1, teams_id);
                ResultSet rs = pst.executeQuery();
                if (rs.next()) {
                    leader_users_id = rs.getInt(1);
                    team_name = rs.getString(2);
                }
            }
            //add team history
            String insertQuery = "INSERT INTO history_teams (teams_id, users_id, team_name, h_users_id, mode, date) VALUES (?,?,?,?,?,?)";
            PreparedStatement insTeamHistoryPrepStmt = con.prepareStatement(insertQuery);
            insTeamHistoryPrepStmt.setInt(1, teams_id);
            insTeamHistoryPrepStmt.setInt(2, leader_users_id);
            insTeamHistoryPrepStmt.setString(3, team_name);
            insTeamHistoryPrepStmt.setInt(4, userId);
            insTeamHistoryPrepStmt.setString(5, mode);
            insTeamHistoryPrepStmt.setTimestamp(6, new Timestamp(new Date().getTime()));
            insTeamHistoryPrepStmt.executeUpdate();
        } catch (Exception e) {
            CommonLogger.printExceptions(this, "Exception while getting the teams data.", e);
        }
    }

    /**
     * Verifies whether the team name already exists
     * @param teams_id
     * @param team_name
     * @throws java.sql.SQLException
     */
    private void checkNameNotUsed(int teams_id, String team_name)
            throws SQLException {
        PreparedStatement getTeamPrepStmt = con.prepareStatement("SELECT 0 as '0' FROM teams WHERE team_name=? and teams_id <> " + teams_id);
        getTeamPrepStmt.setString(1, team_name);
        ResultSet getTeamResultSet = getTeamPrepStmt.executeQuery();
        if (getTeamResultSet.next()) {
            throw new ServerFailException("New Team Name is already used.");
        }
        getTeamPrepStmt.close();

    }
}
