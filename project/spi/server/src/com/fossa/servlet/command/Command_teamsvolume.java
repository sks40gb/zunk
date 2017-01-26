/*
 * Command_teamsvolume.java
 *
 * Created on November 21, 2007, 12:39 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package com.fossa.servlet.command;

import com.fossa.servlet.common.CommonLogger;
import com.fossa.servlet.common.Log;
import com.fossa.servlet.common.SQLQueries;
import com.fossa.servlet.common.msg.MessageWriter;
import com.fossa.servlet.dao.DBTask;
import com.fossa.servlet.session.UserTask;
import java.sql.*;

import org.w3c.dom.Element;

/**
 * This class handles the request for teams volume
 * @author bmurali
 */
public class Command_teamsvolume implements Command {

    /** Creates a new instance of Command_teamsvolume */
    public Command_teamsvolume() {
    }

    public String execute(Element action, UserTask user, DBTask dbTask, MessageWriter writer) {
        Log.print("Handler_teamsvolume.run");
        Statement st = dbTask.getStatement();
        /** required - volume to remove from all queues */
        int volume_id = Integer.parseInt(action.getAttribute(A_VOLUME_ID));
        /** 0 if removing all occurrences of the volume and batches of the volume */
        int teams_id = Integer.parseInt(action.getAttribute(A_TEAMS_ID));

        // delete the volume from teamsvolume
        if (teams_id > 0) {
            try {
                // delete volume from a particular team
                PreparedStatement delTeamsVolPrepStmt = user.prepareStatement(dbTask, SQLQueries.DEL_TVOL_VTID);
                delTeamsVolPrepStmt.setInt(1, volume_id);
                delTeamsVolPrepStmt.setInt(2, teams_id);
                delTeamsVolPrepStmt.executeUpdate();

            } catch (SQLException ex) {
                CommonLogger.printExceptions(this, "Exception while deleting from the teams volume." , ex);
            }       
        } else {
            try {
                // delete the volume from all teams
                user.executeUpdate(dbTask, SQLQueries.DEL_TVOL_VID + volume_id);
                PreparedStatement delTeamQueuePrepStmt;
                delTeamQueuePrepStmt = user.prepareStatement(dbTask, SQLQueries.DEL_TVOL_TQUEUE);
                PreparedStatement delUserQueuePrepStmt = user.prepareStatement(dbTask, SQLQueries.DEL_TVOL_UQUEUE);
                // get the batch_id's
                ResultSet getBatchIdResultSet = st.executeQuery(SQLQueries.SEL_TVOL_BATCHID + volume_id);
                if (getBatchIdResultSet.next()) {
                    delTeamQueuePrepStmt.setInt(1, getBatchIdResultSet.getInt(1));
                    delTeamQueuePrepStmt.executeUpdate();
                    delUserQueuePrepStmt.setInt(1, getBatchIdResultSet.getInt(1));
                    delUserQueuePrepStmt.executeUpdate();
                }
            } catch (SQLException ex) {
                 CommonLogger.printExceptions(this, "SQLException while deleting from the teams and users queue." , ex);
            }
        }
        return null;
    }

    public boolean isReadOnly() {
        return false;
    }
}
