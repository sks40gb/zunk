/* $Header: /home/common/cvsarea/ibase/dia/src/server/DiaDaemonTask.java,v 1.19.2.2 2006/03/22 20:27:15 nancy Exp $ */
package server;

import common.Log;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * A daemon task which runs periodically (once a minute) to do housekeeping.
 * Housekeeping to be performed is:
 * <p><ul>
 * <li> Remove sessions which have not been heard from for a while
 * <li> Remove obsolete rows from changes table (when all sessions have a later age)
 * </ul>
 */

final public class DiaDaemonTask extends Thread {

    final private static long DAEMON_DELAY = 60000;  // 1 minute
    final private static long ACTIVE_TIMEOUT = 5*60000;  // 5 minutes
    final private static long INACTIVE_TIMEOUT = 12*60000;  // 12 minutes
    private int serverCount;
    private int dbPort;
    private String dbName;
    private Connection con;
    private int age;
    private int lastDeleteAge = 0;

    /**
     * Create an instance of the daemon and remember the parameters.
     * @param serverCount the number of servers running at this moment
     * @param dbPort the database port from the properties file
     * @param dbName the database name from the properties file
     */
    DiaDaemonTask (int serverCount, int dbPort, String dbName) {
        this.serverCount = serverCount;
        this.dbPort = dbPort;
        this.dbName = dbName;
    }

    /**
     * Start the daemon.
     */
    public void run() {
        try {
            con = DiaDatabaseOpener.open(dbPort,dbName);
            con.setAutoCommit(false);

            for (;;) {
                try {
                    ServerProperties.reopenProperties();
                    long time = System.currentTimeMillis();
                    if ("yes".equalsIgnoreCase(ServerProperties.getProperty("daemon", "NO"))) {
                        //Log.print("DAEMON running at "+time);

                        age = 0;
                        checkSessions(time);
                        checkChanges();
                    }
                    checkRequeue(time);
                    Thread.sleep(DAEMON_DELAY);
                } catch (SQLException e) {
                    String sqlState = e.getSQLState();
                    int errorCode = e.getErrorCode();
                    Log.print(">>>"+e+" sqlState="+sqlState+" errorCode="+errorCode);
                    if (errorCode == ServerTask.ER_LOCK_DEADLOCK) {
                        // it's a deadlock, try again
                        Log.print("DAEMON - DEADLOCK DETECTED");
                        e.printStackTrace();
                        e.printStackTrace(Log.getWriter());
                        con.rollback();
                    } else if ("85S01".equals(sqlState)) {
                        // It's a lost connection, reconnect.
                        // This seems to happen in test on PC, perhaps when 
                        // machine comes out of long standby/hibernate.
                        // It has not happened on Linux server.
                        Log.print("DAEMON - LOST CONNECTION - RECONNECT");
                        con = DiaDatabaseOpener.open(dbPort,dbName);
                        con.setAutoCommit(false);
                    } else {
                        throw e;
                    }
                } catch (InterruptedException e) {
                    Log.print("DAEMON: "+e);
                }
            }
        } catch (Throwable th) {
            th.printStackTrace();
            th.printStackTrace(Log.getWriter());
            Log.print("DAEMON TERMINATING - Exiting Server");
            System.exit(1);
                    }
    }

    private void checkSessions(long time) throws SQLException {
        //Log.print("DAEMON - check sessions");
        Statement st = con.createStatement();
        // note.  not locked -- ok if someone else messes with it
        ResultSet rs = st.executeQuery(
            "select server_nbr, session_id, live, interaction_time, users_id"
            +" from session"
            +" where interaction_time<"+(time - INACTIVE_TIMEOUT)
            +"    or live"
            +"       and (interaction_time<"+(time - ACTIVE_TIMEOUT)
            +"            or (server_nbr<>"+serverCount+"))");
        while (rs.next()) {
            int serverNbr = rs.getInt(1);
            int sessionId = rs.getInt(2);
            boolean live = rs.getBoolean(3);
            long interactionTime = rs.getLong(4);
            int usersId = rs.getInt(5);

            Log.print("DAEMON svr="+serverNbr+" session="+sessionId
                      +" live="+live+" interactionTime="+interactionTime);
            if (serverNbr != serverCount) {
                // check if the other server is live
                // Lock this server so others know it's active
                String lockName = "DIA."+dbName+"."+serverNbr;
                Statement st2 = con.createStatement();
                ResultSet rs2 = st2.executeQuery(
                    "select is_free_lock('"+lockName+"')");
                rs2.next();
                boolean isFree = (rs2.getInt(1) == 1);
                st2.close();
                if (! isFree) {
                    // belongs to another server -- leave it alone
                    Log.print("DAEMON - held by other server");
                    continue;
                }
                Log.print("DAEMON - other server is dead");
            }
            Statement st3 = con.createStatement();
            if (interactionTime < (time - INACTIVE_TIMEOUT)) {
                // kill the task
                Log.print("DAEMON - kill "+sessionId);
                st3.executeUpdate(
                    "delete from session"
                    +" where session_id="+sessionId);
                //EventLog.logout(task, usersId);
            } else { // since live and active timeout
                Log.print("DAEMON - disconnect "+sessionId);
                st3.executeUpdate(
                    "update session"
                    +" set live=0"
                    +"   , volume_id=0, batch_id=0, lock_time=0"
                    +" where session_id="+sessionId);
            }
            if (age == 0) {
                ResultSet rs3 = st3.executeQuery(
                    "select age from svrage where dummy_key = 0 for update");
                rs3.next();
                age = rs3.getInt(1) + 1;
                Log.print("DAEMON - update svrage="+age);
                st3.executeUpdate(
                    "update svrage set age = "+age
                    +" where dummy_key = 0");
            }
            // Following moved out of above if stmt - wbe 2005-04-28
            // Was losing changes for kills, when multiple kills at same time
            st.executeUpdate(
                "insert ignore into changes"
                +"  set table_nbr="+Tables.session.getTableNumber()
                +"    , id="+sessionId
                //+"    , propagate=0"
                +"    , age="+age);
            st3.close();
        }
        st.close();
        con.commit();
    }


    private void checkChanges() throws SQLException {
        //Log.print("DAEMON - checkChanges");
        Statement st = con.createStatement();
        // not locked, ok if someone else increases age of session
        ResultSet rs = st.executeQuery(
            "select min(age)"
            +" from session"
            +" where live");
        rs.next();
        int minAge = rs.getInt(1);
        rs.close();
        if (! rs.wasNull() && minAge > lastDeleteAge) {
            lastDeleteAge = minAge;

            //// look for big deletes - bug trap
            //rs = st.executeQuery(
            //    "select count(*) from changes where age <"+minAge);
            //rs.next();
            //if (rs.getInt(1) >= 10000) {
            //    rs.close();
            //    rs = st.executeQuery(
            //        "select age, table_nbr, count(*)"
            //        +" from changes"
            //        +" where age <"+minAge
            //        +" group by age, table_nbr"
            //        +" order by age, table_nbr");
            //    while (rs.next()) {
            //        Log.print("DAEMON changes: age="+rs.getInt(1)
            //                  +" table="+rs.getInt(2)
            //                  +" count="+rs.getInt(3));
            //    }
            //}
            //rs.close();

            int count = st.executeUpdate(
                "delete from changes where age <"+minAge);
            if (count > 0) {
                Log.print("DAEMON deleted "+count+" changes < "+minAge);
            }
        }
        st.close();
        con.commit();
    }


    // If there are any current requeue requests, handle one of them.
    // (If more than one, this will be repeated in a minute.)
    private void checkRequeue(long time) throws SQLException {
        Statement st = con.createStatement();
        // See if there are any -- nonblocking
        ResultSet rs = st.executeQuery(
            "select usersqueue_id"
            +" from usersqueue"
            +" where requeue_time between 1 and "+time
            +" limit 1");
        if (rs.next()) {
            // yes -- requeue the one we found, if it's still there
            int usersqueueId = rs.getInt(1);
            rs = st.executeQuery(
                "select batch_id, users_id, requeue_users_id, age"
                +" from usersqueue, svrage"
                +" where usersqueue_id="+usersqueueId
                +"   and requeue_time between 1 and "+time
                +" for update");
            if (rs.next()) {
                int batchId = rs.getInt(1);
                int usersId = rs.getInt(2);
                int requeueUsersId = rs.getInt(3);
                int newAge = rs.getInt(4) + 1;
                rs = st.executeQuery(
                    "select comments from batch_comments"
                    +" where batch_id="+batchId
                    +" for update");
                String comments = (rs.next() ? rs.getString(1) + "\n" : "");
                st.executeUpdate(
                    "update svrage set age="+newAge);
                st.executeUpdate(
                    "update batch set status='CodingQC'"
                    +" where batch_id="+batchId);
                st.executeUpdate(
                    "update usersqueue"
                    +" set users_id="+requeueUsersId
                    +"   , timestamp="+System.currentTimeMillis()
                    +"   , requeue_time=0"
                    +"   , requeue_users_id=0"
                    +" where usersqueue_id="+usersqueueId);
                // record changes for all tables we touched
                // Note: insert ignore because coder and QCer could be the same
                st.executeUpdate(
                    "insert ignore into changes (table_nbr, id, age)"
                    +" values ("+Tables.batch.getTableNumber()+","+batchId+","+newAge+")"
                    +"      , ("+Tables.users.getTableNumber()+","+usersId+","+newAge+")"
                    +"      , ("+Tables.users.getTableNumber()+","+requeueUsersId+","+newAge+")"
                    +"      , ("+Tables.usersqueue.getTableNumber()+","+usersqueueId+","+newAge+")");
                // Append indication to batch comments
                PreparedStatement ps = con.prepareStatement(
                    "update batch_comments"
                    +" set comments=?"
                    +" where batch_id=?");
                ps.setString(1, comments+"AUTOMATICALLY REQUEUED TO QC");
                ps.setInt(2, batchId);
                ps.executeUpdate();
                ps.close();
            }
        }
        st.close();
        con.commit();
    }


    //// Open database connection
    //private static Connection openDatabase(int dbPort, String dbName) {
    //
    //    String dbUrl = "jdbc:mysql://localhost:"+dbPort+"/"+dbName;
    //    Log.print("DiaDaemonTask: open: "+dbUrl);
    //
    //    try {
    //        Connection con = DriverManager.getConnection(dbUrl, "dia", "dia4ibase");
    //        return con;
    //    } catch (SQLException e) {
    //        Log.quit("Can't connect: " + dbUrl);
    //        return null;
    //    }
    //}
}
