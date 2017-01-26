/*
 * DBTask.java
 *
 * Created on 15 November, 2007, 5:14 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.fossa.servlet.dao;

import com.fossa.servlet.common.Log;
import com.fossa.servlet.exception.ServerFailException;
import com.fossa.servlet.session.UserTask;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 *
 * @author prakash
 */
/**
 * Class used to get the DB connection
 */
public class DBTask {
    
    Connection connection;
    Statement statement;
    ResultSet resultSet;
    
    /** Creates a new instance of DBTask */
    public DBTask() {
    }
    
    /** Getting the database connection using datasource*/
    public Connection getConnection() {
        
        try{
            if (null == connection) {
                //connection = new FossaDataSource().getConnection();//existing code
                connection = FossaDataSource.getConnection();
            }
        } catch (Exception ex) {
            throw new ServerFailException("Error getting a connection from Datasource.");
        }
        return connection;
    }
    
    /** Method used to get the statement object from the connection object*/
    public Statement getStatement() {
       
        if(null == connection) {
            getConnection();
        }
        
        if(null == statement) {
            try {
                statement = connection.createStatement();
            } catch (SQLException ex) {
                throw new ServerFailException("Error getting a statement from Connection.");
            }
        }
        return statement;
    }
    
     /** Method used to get the resultSet object from the statement object*/
    public ResultSet getResultSet() {
        if(null == statement) {
            getStatement();
        }
        if(null == resultSet) {
            try {
                resultSet = statement.getResultSet();
            } catch (SQLException ex) {
                throw new ServerFailException("Error getting a result set from statement.");
            }
        }
        return resultSet;
    }
    
    public void setAutoCommit(boolean doCommit) {
        if(null == connection) {
            getConnection();
        }
        try {
            connection.setAutoCommit(doCommit);
        } catch (SQLException ex) {
            throw new ServerFailException("Error when turning autocommit to "+ doCommit + ".");
        }
    }
    
    public void setTransactionIsolation(int type) {
        if(null == connection) {
            getConnection();
        }
        try {
            connection.setTransactionIsolation(type);
        } catch (SQLException ex) {
            throw new ServerFailException("Error in setting transaction isolation.");
        }
    }
    
    public void commitTransaction(UserTask task) throws SQLException {
        if(null == connection) {
            getConnection();
        }
        if (task.getTransactionStartTime() == 0) {
            // no transaction is in progress
            return;
        }
        if (task.isIsReadOnly()) {
            // Show read-only commits only for 200 ms or longer transactions
            if ((System.currentTimeMillis() - task.getTransactionStartTime()) >= 200) {
                Log.print("$$$$$$ COMMIT READ-ONLY TRANSACTION: "
                        +(System.currentTimeMillis() - task.getTransactionStartTime()));
            }
        } else {
            //if (propagateTableSet != null && ! propagateTableSet.isEmpty()) {
            //    //try {
            //    //    ManagedTable.recordChangedTables(this, propagateTableSet);
            //    //} catch (SQLException e) {
            //    //    Log.quit(e);
            //    //}
            //}
            Log.print("$$$$$$ COMMIT TRANSACTION: "
                    +(System.currentTimeMillis() - task.getTransactionStartTime()));
        }
        connection.commit();
    }
    

    
    public String getLockVolumeId() {
        return null;
    }
    
    
}
