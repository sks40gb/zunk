/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fossa.servlet.server;

import com.fossa.servlet.dao.DBTask;
import com.fossa.servlet.session.UserTask;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Date;
import org.apache.log4j.Logger;

/**
 * Make entries in the event table for timesheet reporting.  Events are tracked
 * by batch, so for an Open and Close even, a row is inserted into even with zero counts.
 * For an update, the field, page and child counts are added to the existing event
 * values.  If an Open is received for an unclosed batch, a Close with a highvalue
 * timestamp is inserted before reopening.  The Timesheet report will then use the
 * last update access timestamp for that event entry.
 */
public class EventLog {

    private UserTask task = null;
    private DBTask dbTask = null;
    private Connection con = null;
    private Statement st;    
    private static Logger logger = Logger.getLogger("com.fossa.servlet.server");
    private int sub_process = 0;
    // Create an instance of EventLog, remembering task
     
    private EventLog(UserTask task, DBTask dbTask) {
        this.task = task;
        this.dbTask = dbTask;
        con = dbTask.getConnection();
        st = dbTask.getStatement();
    }

    /**
     * Make an entry in the event table for a user logging in.
     */
    //public static void login(UserTask task) throws SQLException {
    //    (new EventLog(task)).login();
    //}
    //public void login() throws SQLException {
    //    writeEvent("Login",0,0,0,0,0,0,"",0,0);
    //}
    /**
     * Make an entry in the event table for a user logging out.
     */
    //public static void logout(UserTask task) throws SQLException {
    //    (new EventLog(task)).logout();
    //}
    //public void logout() throws SQLException {
    //    writeEvent("Logout",0,0,0,0,0,0,"",0,0);
    //}                                task.getUsersId()
    /**
     * Make an entry in the event table for a user logging out.
     */

    public static void logout(UserTask task, DBTask dbTask, int usersId) throws SQLException {
        (new EventLog(task, dbTask)).logout(usersId);
    }

    public void logout(int usersId) throws SQLException {
        writeEvent("Logout", usersId, 0, 0, "", 0, 0, 0);
    }

    /**
     * Make an entry in the event table for a user opening a Batch.
     */
    public static void open(UserTask task, DBTask dbTask, int volume_id, int batch_id, String status) throws SQLException {
        (new EventLog(task, dbTask)).open(volume_id, batch_id, status);
    }

    public void open(int volume_id, int batch_id, String status)
            throws SQLException {
        writeEvent("Open", task.getUsersId(), volume_id, batch_id, status, 0, 0, 0);
    }

    public static void open(UserTask task, DBTask dbTask, int volume_id, String status) throws SQLException {
        (new EventLog(task, dbTask)).open(volume_id, status);
    }

    public void open(int volume_id, String status)
            throws SQLException {
        writeEvent("Open", task.getUsersId(), volume_id, status, 0, 0, 0);
    }

    /**
     * Make an entry in the event table for a user closing a Batch.
     */
    public static void close(UserTask task, DBTask dbTask, int volume_id, int batch_id, String status) throws SQLException {
        (new EventLog(task, dbTask)).close(volume_id, batch_id, status);
    }

    public void close(int volume_id, int batch_id, String status)
            throws SQLException {
        writeEvent("Close", task.getUsersId(), volume_id, batch_id, status, 0, 0, 0);
    }

    /**
     * Make an entry in the event table for a user adding coded data.
     */
    public static void add(UserTask task, DBTask dbTask, int volume_id, int batch_id, String status, int child_count, int page_count, int field_count) throws SQLException {
        (new EventLog(task, dbTask)).add(volume_id, batch_id, status, child_count, page_count, field_count);
    }

    public void add(int volume_id, int batch_id, String status, int child_count, int page_count, int field_count)
            throws SQLException {
        writeEvent("Add", task.getUsersId(), volume_id, batch_id, status, child_count, page_count, field_count);
    }

    /**
     * Make an entry in the event table for a user updating a batch.
     */
    public static void update(UserTask task, DBTask dbTask, int volume_id, int batch_id, String status, int child_count, int page_count, int field_count) throws SQLException {
        (new EventLog(task, dbTask)).update(volume_id, batch_id, status, child_count, page_count, field_count);
    }

    public void update(int volume_id, int batch_id, String status, int child_count, int page_count, int field_count)
            throws SQLException {
    // TBD:  do we need to keep updates for the timesheet?
        //writeEvent("Update", task.getUsersId(), volume_id,batch_id,status
        //           ,child_count, page_count, field_count);
    }


    /**
     *  Write one event to the event table.
     * @param event can be 'Open', 'Close', 'Add', 'Update', 'Logout'
     * @param users_id the users.users_id of the client user
     * @param volume_id volume.volume_id containing the batch_id
     * @param batch_id batch.batch_id of the batch being updated
     * @param status the status of the batch when the updates were made
     * @param child_count the number of child rows updated
     * @param page_count the number of pages affected by the update
     * @param field_count the number of fields affected by the update
     */
    private void writeEvent(String event, int users_id, int volume_id, int batch_id, String status, int child_count, int page_count, int field_count) throws SQLException {
        /* TODO: Cyrus - Please find a way to implement the Log using log 4j
         Log.print("(EventLog.writeEvent) event/user/volume/batch/status "
                  + event + "/" + users_id + "/" + volume_id + "/" + batch_id + "/" + status);
         */
        try {
            Connection con = null;
            con = dbTask.getConnection();
            Timestamp open = null;
            Timestamp close = null;
            Timestamp add = null;
            if ("Add".equals(event)) {
                Date date = new Date();
                long time = date.getTime();
                add = new Timestamp(time);
            } else if ("Open".equals(event)) {
                Date date = new Date();
                long time = date.getTime();
                open = new Timestamp(time);
            } else if ("Close".equals(event) || "Logout".equals(event)) {
                Date date = new Date();
                long time = date.getTime();
                close = new Timestamp(time);
            }
            ResultSet getSubProcess = null;
            
            if(status.equals("ModifyErrors")){
                 getSubProcess = st.executeQuery("select sub_process from batch where batch_id="+batch_id);                
                 if(getSubProcess.next()){
                    sub_process = getSubProcess.getInt(1);                     
                 }else{
                    sub_process = 0;
                 }
            }
            
            if ("Open".equals(event)) {                 
                
                ResultSet rs = st.executeQuery("select close_timestamp" + " from event" + " where users_id=" + users_id + "  and volume_id=" + volume_id + "  and batch_id=" + batch_id + "  and status='" + status + "'" //                    +"  and close_timestamp = 0");
                        + "  and close_timestamp is NULL");
                if (rs.next()) {
                    Date date = new Date();
                    long time = date.getTime();
                    Timestamp timestamp = new Timestamp(time);
                    rs.close();
                    // This batch has been opened but never closed.  Close it now
                    // and start a new row for this open action.
                    st.executeUpdate("update event" + " set close_timestamp='" + timestamp + "'" + " where users_id=" + users_id + "   and volume_id=" + volume_id + "   and batch_id=" + batch_id + "   and status='" + status + "'" //                        +"  and close_timestamp = 0");
                            + "  and close_timestamp is NULL");
                }

                st.executeUpdate("insert into event (users_id,volume_id,batch_id,status,child_count,page_count,field_count,open_timestamp,close_timestamp,add_timestamp,sub_process) values(" + users_id + "," + volume_id + "," + batch_id + ",'" + status + "',0,0,0,'" + open + "'," + close + "," + add + ","+sub_process+")");

            } else if ("Close".equals(event) || "Logout".equals(event)) {
                st.executeUpdate("update event" + " set close_timestamp='" + close + "', sub_process ="+sub_process +""
                        + " where users_id=" + users_id + "   and volume_id=" + volume_id + "   " +
                        " and batch_id=" + batch_id + "   and status='" + status + "'" + "  and close_timestamp is NULL");
            } else { // add
                // increment the counts with the current update counts and set
                // add_timestamp to the current date/time.  Add_timestamp will be used as
                // the end date/time if there is no close_timestamp.
                int count = st.executeUpdate("update event" + "  set child_count= " + child_count + "   , page_count= " + page_count + "   , field_count= " + field_count + "   , add_timestamp='" + add + "' ,sub_process = "+sub_process+ " where users_id=" + users_id + "   and volume_id=" + volume_id + "   and batch_id=" + batch_id + "   and status='" + status + "'" + "  and close_timestamp is NULL");
            }
        } catch (SQLException e) {
            logger.error("Exception during updating event." + e);
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            logger.error(sw.toString());
        }
    }

    private void writeEvent(String event, int users_id, int volume_id, String status, int child_count, int page_count, int field_count) throws SQLException {
        /* TODO: Cyrus - Please find a way to implement the Log using log 4j
         Log.print("(EventLog.writeEvent) event/user/volume/batch/status "
                  + event + "/" + users_id + "/" + volume_id + "/" + batch_id + "/" + status);
         */
        try {

            Timestamp open = null;
            Timestamp close = null;
            Timestamp add = null;
            if ("Add".equals(event)) {
                Date date = new Date();
                long time = date.getTime();
                add = new Timestamp(time);
            } else if ("Open".equals(event)) {
                Date date = new Date();
                long time = date.getTime();
                open = new Timestamp(time);
            } else if ("Close".equals(event) || "Logout".equals(event)) {

                Date date = new Date();
                long time = date.getTime();
                close = new Timestamp(time);

            }
            

            if ("Open".equals(event)) {

                ResultSet rs = st.executeQuery("select close_timestamp" + " from event" + " where users_id=" + users_id + "  and volume_id=" + volume_id + "  and batch_id= 0" + "  and status='" + status + "'" + "  and close_timestamp is NULL");
                if (rs.next()) {
                    rs.close();
                    Date date = new Date();
                    long time = date.getTime();
                    Timestamp timestamp = new Timestamp(time);

                    // This batch has been opened but never closed.  Close it now
                       // and start a new row for this open action.
                    st.executeUpdate("update event" + " set close_timestamp='" + timestamp + "'" + " where users_id=" + users_id + "   and volume_id=" + volume_id + "   and batch_id=0" + "   and status='" + status + "'" + "  and close_timestamp is NULL");
                }

                st.executeUpdate("insert into event(users_id,volume_id,batch_id,status,child_count,page_count,field_count,open_timestamp,close_timestamp,add_timestamp) values(" + users_id + "," + volume_id + ",0,'" + status + "',0,0,0,'" + open + "'," + close + "," + add + ")");


            } else if ("Close".equals(event) || "Logout".equals(event)) {

                st.executeUpdate("update event" + " set close_timestamp=" + close + "'" + " where users_id=" + users_id + "   and volume_id=" + volume_id + "   and batch_id=0" + "   and status='" + status + "'" + "  and close_timestamp is NULL");
            } else { // add

                // increment the counts with the current update counts and set
                   // add_timestamp to the current date/time.  Add_timestamp will be used as
                   // the end date/time if there is no close_timestamp.
                int count = st.executeUpdate("update event" + "  set child_count=child_count + " + child_count + "   , page_count=page_count + " + page_count + "   , field_count=field_count + " + field_count + "   , add_timestamp=" + add + "'" + " where users_id=" + users_id + "   and volume_id=" + volume_id + "   and batch_id=0" + "   and status='" + status + "'" + "  and close_timestamp is NULL");
            }
        // }
        } catch (SQLException e) {
            logger.error("Exception during updating event." + e);
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            logger.error(sw.toString());
        }
    }
}

