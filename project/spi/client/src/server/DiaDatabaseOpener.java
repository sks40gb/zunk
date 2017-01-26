/* $Header: /home/common/cvsarea/ibase/dia/src/server/DiaDatabaseOpener.java,v 1.1 2004/07/07 15:56:30 weaston Exp $ */

package server;

import com.lexpar.util.Log;
import server.ServerProperties;

import java.net.*;                                                         
import java.io.*;
import java.sql.*;
import javax.net.*;
import javax.net.ssl.*;

/**
 * A common class for opening the DIA database connection.
 */
final public class DiaDatabaseOpener {

    // The JCBC driver class.
    // This variable used only to record the fact that it has been loaded.
    private static Class jdbcClass = null;

    private DiaDatabaseOpener() {}

    /**
     * Open the database connection.
     */
    public static Connection open(int dbPort, String dbName) {

        // load the JDBC driver class, if necessary (used by all tasks)
        if (jdbcClass == null) {
            synchronized (DiaDatabaseOpener.class) {
                try {

                    jdbcClass = Class.forName("org.gjt.mm.mysql.Driver");

                    // The newInstance() call is a work around for some 
                    // broken Java implementations
                    Object dummy = jdbcClass.newInstance();

                } catch (ClassNotFoundException e) {
                    Log.quit("Can't find JDBC driver: org.gjt.mm.mysql.Driver", e);
                } catch (InstantiationException e) {
                    Log.quit(
                        "Can't instantiate JDBC driver: org.gjt.mm.mysql.Driver", e);
                } catch (IllegalAccessException e) {
                    Log.quit(
                        "Can't instantiate JDBC driver: org.gjt.mm.mysql.Driver", e);
                }
            }
        }

        String dbUrl = "jdbc:mysql://localhost:"+dbPort+"/"+dbName;

        try {
            Connection con = DriverManager.getConnection(dbUrl, "dia", "dia4ibase");
            return con;
        } catch (SQLException e) {
            Log.quit("Can't connect to database: " + dbUrl, e);
            return null;
        }
    }
}
