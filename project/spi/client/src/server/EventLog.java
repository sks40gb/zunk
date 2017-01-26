/* $Header: /home/common/cvsarea/ibase/dia/src/server/Attic/EventLog.java,v 1.1.2.10 2006/03/22 20:27:15 nancy Exp $ */
package server;

import common.Log;

import java.io.IOException;
import java.sql.*;

/**
 * Make entries in the event table for timesheet reporting.  Events are tracked
 * by batch, so for an Open and Close even, a row is inserted into even with zero counts.
 * For an update, the field, page and child counts are added to the existing event
 * values.  If an Open is received for an unclosed batch, a Close with a highvalue
 * timestamp is inserted before reopening.  The Timesheet report will then use the
 * last update access timestamp for that event entry.
 */
public class EventLog {
   
    private ServerTask task = null;
    private Connection con = null;
    private Statement st;
    
    // Create an instance of EventLog, remembering task
    private EventLog(ServerTask task) {
        this.task = task;
        con = task.getConnection();
        st = task.getStatement();
    }
    
    /**
     * Make an entry in the event table for a user logging in.
     */
    //public static void login(ServerTask task) throws SQLException {
    //    (new EventLog(task)).login();
    //}
    //public void login() throws SQLException {
    //    writeEvent("Login",0,0,0,0,0,0,"",0,0);
    //}
    /**
     * Make an entry in the event table for a user logging out.
     */
    //public static void logout(ServerTask task) throws SQLException {
    //    (new EventLog(task)).logout();
    //}
    //public void logout() throws SQLException {
    //    writeEvent("Logout",0,0,0,0,0,0,"",0,0);
    //}                                task.getUsersId()
    /**
     * Make an entry in the event table for a user logging out.
     */
    public static void logout(ServerTask task, int usersId) throws SQLException {
        (new EventLog(task)).logout(usersId);
    }
    public void logout(int usersId) throws SQLException {
        writeEvent("Logout",usersId,0,0,"",0,0,0);
    }

    /**
     * Make an entry in the event table for a user opening a Batch.
     */
    public static void open(ServerTask task, int volume_id, int batch_id, String status) throws SQLException {
        (new EventLog(task)).open(volume_id,batch_id,status);
    }
    public void open(int volume_id, int batch_id, String status) 
    throws SQLException {
        writeEvent("Open", task.getUsersId(), volume_id, batch_id,status,0,0,0);
    }

    /**
     * Make an entry in the event table for a user closing a Batch.
     */
    public static void close(ServerTask task, int volume_id, int batch_id, String status) throws SQLException {
        (new EventLog(task)).close(volume_id, batch_id, status);
    }
    public void close(int volume_id, int batch_id, String status) 
    throws SQLException {
        writeEvent("Close", task.getUsersId(), volume_id, batch_id, status, 0,0,0);
    }

    /**
     * Make an entry in the event table for a user adding coded data.
     */
    public static void add(ServerTask task, int volume_id, int batch_id, String status
                           , int child_count, int page_count, int field_count) throws SQLException {
        (new EventLog(task)).add(volume_id, batch_id, status
                                 , child_count, page_count, field_count);
    }
    public void add(int volume_id, int batch_id, String status
                    , int child_count, int page_count, int field_count)
    throws SQLException {
        writeEvent("Add", task.getUsersId(), volume_id, batch_id,status
                   ,child_count, page_count, field_count);
    }

    /**
     * Make an entry in the event table for a user updating a batch.
     */
    public static void update(ServerTask task, int volume_id, int batch_id, String status
                              , int child_count, int page_count, int field_count) throws SQLException {
        (new EventLog(task)).update(volume_id, batch_id, status
                                    , child_count, page_count, field_count);
    }
    public void update(int volume_id, int batch_id, String status
                       , int child_count, int page_count, int field_count)
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
    private void writeEvent(String event, int users_id
                            , int volume_id, int batch_id, String status
                            , int child_count, int page_count, int field_count
                            ) throws SQLException {
        Log.print("(EventLog.writeEvent) event/user/volume/batch/status "
                  + event + "/" + users_id + "/" + volume_id + "/" + batch_id + "/" + status);
        try {
            long open = 0;
            long close = 0;
            long add = 0;
            if ("Add".equals(event)) {
                add = System.currentTimeMillis();
            } else if ("Open".equals(event)) {
                open = System.currentTimeMillis();
            } else if ("Close".equals(event)
                       || "Logout".equals(event)) {
                close = System.currentTimeMillis();
            }
            if ("Open".equals(event)) {
                ResultSet rs = st.executeQuery(
                    "select close_timestamp"
                    +" from event"
                    +" where users_id="+users_id
                    +"  and volume_id="+volume_id
                    +"  and batch_id="+batch_id
                    +"  and status='"+status+"'"
                    +"  and close_timestamp = 0");
                if (rs.next()) {
                    rs.close();
                    // This batch has been opened but never closed.  Close it now
                    // and start a new row for this open action.
                    st.executeUpdate(
                        "update event"
                        +" set close_timestamp="+Integer.MAX_VALUE
                        +" where users_id="+users_id
                        +"   and volume_id="+volume_id
                        +"   and batch_id="+batch_id
                        +"   and status='"+status+"'"
                        +"  and close_timestamp = 0");
                }
                st.executeUpdate(
                    "insert into event"
                    +" set users_id="+users_id
                    +"   , volume_id="+volume_id
                    +"   , batch_id="+batch_id
                    +"   , status='"+status+"'"
                    +"   , child_count="+0
                    +"   , page_count="+0
                    +"   , field_count="+0
                    +"   , open_timestamp="+open
                    +"   , close_timestamp="+close
                    +"   , add_timestamp="+add);
            } else if ("Close".equals(event)
                || "Logout".equals(event)) {
                st.executeUpdate(
                    "update event"
                    +" set close_timestamp="+close
                    +" where users_id="+users_id
                    +"   and volume_id="+volume_id
                    +"   and batch_id="+batch_id
                    +"   and status='"+status+"'"
                    +"  and close_timestamp = 0");
            } else { // add
                // increment the counts with the current update counts and set
                // add_timestamp to the current date/time.  Add_timestamp will be used as
                // the end date/time if there is no close_timestamp.
                int count = st.executeUpdate(
                    "update event"
                    +"  set child_count=child_count + "+child_count
                    +"   , page_count=page_count + "+page_count
                    +"   , field_count=field_count + "+field_count
                    +"   , add_timestamp="+add
                    +" where users_id="+users_id
                    +"   and volume_id="+volume_id
                    +"   and batch_id="+batch_id
                    +"   and status='"+status+"'"
                    +"  and close_timestamp = 0");
            }
        } catch (SQLException e) {
            Log.quit(e);
        }
    }
}
