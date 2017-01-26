/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fossa.servlet.command;

import com.fossa.servlet.common.CommonLogger;
import com.fossa.servlet.common.msg.MessageWriter;
import com.fossa.servlet.dao.DBTask;
import com.fossa.servlet.server.valueobjects.BatchProcessHistroyData;
import com.fossa.servlet.session.UserTask;
import java.sql.ResultSet;
import java.sql.Statement;
import org.w3c.dom.Element;
import java.sql.Timestamp;
import java.util.Date;

/**
 * This class puts the tally batches in queue
 * @author bmurali
 */
public class Command_tally_queue implements Command {

    public String execute(Element action, UserTask user, DBTask dbTask, MessageWriter writer) {
        Statement getBatchIdStatement = null;
        try {
            getBatchIdStatement = dbTask.getStatement();
            int batch_id = 0;
            Date date = new Date();
            long time = date.getTime();
            Timestamp timestamp = new Timestamp(time);

            int volume_id = Integer.parseInt(action.getAttribute(A_VOLUME_ID));
            int users_id = Integer.parseInt(action.getAttribute(A_USERS_ID));
            if (volume_id > 0) {
                ResultSet getBatchIdResultSet = getBatchIdStatement.executeQuery(
                        "select batch_id FROM batch" + " WHERE volume_id='" + volume_id + "'AND status = 'Tally'" + " lock in share mode");
                while (getBatchIdResultSet.next()) {                    
                    batch_id = getBatchIdResultSet.getInt(1);
                    if (users_id > 0) {
                        // queue the tally batch to the users queue
                        user.executeUpdate(dbTask,
                                "insert into usersqueue" + " set batch_id=" + batch_id + "   , users_id=" + users_id + 
                                " , timestamp='" + timestamp + "'");

                        //add in batch history
                        BatchProcessHistroyData data = new BatchProcessHistroyData();
                        data.setBatch_id(batch_id);
                        data.setVolume_id(volume_id);
                        data.setProcess("Tally");
                        data.setIs_ready("Yes");
                        data.setIn_queue("Yes");
                        data.setQueued_time(BatchIO.getCurrentDateTime());
                        data.setQueued_to(users_id);
                        data.insertIntoHistoryTable(dbTask.getConnection());
                    }
                }
            }

        } catch (Exception exe) {
            CommonLogger.printExceptions(this, "Exception while queueing up a tally batch.", exe);
        }
        return null;
    }

    public boolean isReadOnly() {
        return true;
    }
}
