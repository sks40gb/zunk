/*
 * LoadManagedTable.java
 *
 * Created on November 23, 2007, 11:00 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package com.fossa.servlet;

import com.fossa.servlet.common.Log;
import com.fossa.servlet.common.SQLQueries;
import com.fossa.servlet.dao.DBTask;
import com.fossa.servlet.server.Tables;
import com.fossa.servlet.session.UserTask;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Connection;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;

/**
 * This class opens the server session.
 * @author bmurali
 */
public class LoadManagedTable extends HttpServlet {

    Connection con = null;    
    private int serverCount;     //Holds the server instance count
    private static Logger logger = Logger.getLogger("com.fossa.servlet");

    /**
     * This method is invoked when this class is initiliazed.
     */
    @Override
    public void init() {
        DBTask dbTask = new DBTask();
        UserTask task = new UserTask();
        dbTask.setAutoCommit(false); //The commit is done inside table.load(con) method
        con = dbTask.getConnection();        
        Tables.load(con);
        openServerSession();
        task.setServerCount(serverCount);
    }

    /**
     * This method fetched an server instance count and updates it.
     */
    private void openServerSession() {
        try {
            // Give this server an instance number
            con.setAutoCommit(false);
            Statement getServerInstanceCountStatement = con.createStatement();
            PreparedStatement updateServer = null;
            ResultSet getServerInstanceCountResultSet = getServerInstanceCountStatement.executeQuery(SQLQueries.SEL_SER_INSTANCE);
            getServerInstanceCountResultSet.next();
            serverCount = getServerInstanceCountResultSet.getInt(1) + 1;            
            Log.setLogFileName("server" + serverCount);
            updateServer = con.prepareStatement(SQLQueries.UPD_SERVER);
            updateServer.setInt(1, serverCount);
            updateServer.executeUpdate();

            // Lock this server so others know it's active
            /*lockName = "DIA."+database+"."+serverCount;            
            if (rs2.getInt(1) != 1) {
            Log.quit("Can't get lock for: "+lockName);
            }*/
            
            getServerInstanceCountStatement.close();
            con.commit();

        } catch (SQLException e) {
            logger.error("Exception while opening the server session." + e);
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            logger.error(sw.toString());
            Log.quit(e);
        }
    }

    public void doPost(HttpServletRequest request,
            HttpServletResponse response) throws IOException {        
    }
}
