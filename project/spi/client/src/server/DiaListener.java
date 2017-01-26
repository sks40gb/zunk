/* $Header: /home/common/cvsarea/ibase/dia/src/server/DiaListener.java,v 1.20.2.1 2006/03/22 20:27:15 nancy Exp $ */

package server;

import com.lexpar.util.Log;
import server.ServerProperties;

import java.net.*;                                                         
import java.io.*;
import java.sql.*;
import javax.net.*;
import javax.net.ssl.*;

/**
 * A listener to listen for SSL connections.
 * @see DiaDaemonTask
 */
public class DiaListener {

    private DiaListener() {
    }

    private static int serverCount;
    private static String lockName;
    private static int taskCount = 0;
    private static boolean isTerminating = false;
    private static ServerSocket listenerSocket;
    private static Connection con;
    private static String host;
    private static int    port;
    private static String database;
    private static int    dbport;

    public static void start() {

        host      = ServerProperties.getProperty("host");
        port     = Integer.parseInt(ServerProperties.getProperty("port"));
        database = ServerProperties.getProperty("database");
        dbport   = Integer.parseInt(ServerProperties.getProperty("dbport"));

        // Make a db connection for server cotrol
        // must remain open, to retain lock on server name
        con = DiaDatabaseOpener.open(dbport, database);

        openServerSession();

        // Load managed table information
        Tables.load(con);

        // Start server daemon
        Log.print("starting daemon");
        (new DiaDaemonTask(serverCount, dbport, database)).start();

        // listen for SSL connections.
        try {
            synchronized (DiaListener.class) {
                ServerSocketFactory factory = SSLServerSocketFactory.getDefault();
                listenerSocket = factory.createServerSocket(port, 0, InetAddress.getByName(host));
            }
            while (! isTerminating) {
                Socket incoming = null;
                try {
                    incoming = listenerSocket.accept();
                } catch (SocketException e) {
                    if (isTerminating) {
                        break;
                    }
                    Log.quit(e);
                }
                new ServerTask(incoming, serverCount, dbport, database).start();
            }
        } catch (IOException e) {
            Log.quit(e);
        }

        // wait for existing connections to terminate
        Log.print("waiting for termination");
        for (;;) {
            try {
                Thread.sleep(60000);
            } catch (InterruptedException e) {
                Log.quit(e);
            }
        }

        // never returns
        //Log.quit("sslLoop returned");
    }

    private static void openServerSession() {
        try {
            // Give this server an instance number
            con.setAutoCommit(false);
            Statement st = con.createStatement();
            ResultSet rs1 = st.executeQuery(
                "select server_instance_count from svradmin"
                +"  where dummy_key = 0"
                +" for update");
            rs1.next();
            serverCount = rs1.getInt(1) + 1;

            Log.setLogFileName("server"+serverCount);

            Log.print("Server starting on "+host+" port " +port
                      +"; db "+database+" port "+dbport);
            Log.print("Image databases on "+ServerProperties.getProperty("imagelocalhost")
                      +"; port "+ServerProperties.getProperty("imageport"));

            st.executeUpdate("update svradmin set server_instance_count="+serverCount
                             +" where dummy_key = 0");

            // Lock this server so others know it's active
            lockName = "DIA."+database+"."+serverCount;
            ResultSet rs2 = st.executeQuery(
                "select get_lock('"+lockName+"', 0)");
            rs2.next();
            if (rs2.getInt(1) != 1) {
                Log.quit("Can't get lock for: "+lockName);
            }
            st.close();
            con.commit();

        } catch (SQLException e) {
            Log.quit(e);
        }   
    }       


    //// Open database connection
    //private static Connection openDatabase(int dbPort, String dbName) {
    //
    //    String dbUrl = "jdbc:mysql://localhost:"+dbPort+"/"+dbName;
    //
    //    try {
    //        Connection con = DriverManager.getConnection(dbUrl, "dia", "dia4ibase");
    //        return con;
    //    } catch (SQLException e) {
    //        Log.quit("Can't connect: " + dbUrl);
    //        return null;
    //    }
    //}

    /**
     *
     */
    public synchronized static void shutdown() {
        isTerminating = true;
        try {
            if (listenerSocket != null) {
                Log.print("closing listenerSocket");
                listenerSocket.close();
            } else {
                Log.print("closing listenerSocket - socket is null");
            }
        } catch (IOException e) {
            Log.quit(e);
        }
    }

    public synchronized static void checkForShutdown() {
        while (isTerminating) {
            try {
                Statement st = con.createStatement();
                ResultSet rs = st.executeQuery(
                    "select 0 from session"
                    +" where server_nbr = "+serverCount
                    +"   and live"
                    +" limit 1");
                if (! rs.next()) {
                    Log.print("Shutting down - no open sessions");
                    con.close();
                    System.exit(0);
                }
            } catch (SQLException e) {
                String sqlState = e.getSQLState();
                int errorCode = e.getErrorCode();
                Log.print(">>>"+e+" sqlState="+sqlState+" errorCode="+errorCode);
                if (errorCode == ServerTask.ER_LOCK_DEADLOCK) {
                    // it's a deadlock, try again
                    Log.print("checkForShutdown - DEADLOCK DETECTED");
                    e.printStackTrace();
                    e.printStackTrace(Log.getWriter());
                    try {
                        con.rollback();
                    } catch (SQLException e2) {
                        Log.quit(e2);
                    }
                } else {
                    Log.quit(e);
                }
            }
        }
    }
}
