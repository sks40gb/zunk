package com.core.jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 *
 * @author sunil
 */
public class DriverDemo {
    
    public static void registerConnection() throws ClassNotFoundException, SQLException{
        //registering and loading Driver Manager
        Class clazz = Class.forName("com.mysql.jdbc.Driver");        
        Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/dbName", "user", "password");        
        con.setAutoCommit(false);        
    }

}
