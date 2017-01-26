package com.avi.sql.connection;

import com.avi.config.ApplicationLoader;
import com.avi.sql.table.SSchema;
import java.sql.Connection;
import java.sql.DriverManager;

/**
 *
 * @author sunil
 */
public class DBConnection {

    static {
        SSchema.createSchema();
    }

    public static Connection getConnection() {
        Connection con = null;
        try {          

            /** MYSQL driver */            
            Class.forName(ApplicationLoader.getDbClassName()).newInstance();
            con = DriverManager.getConnection(ApplicationLoader.getDbConnectionUrl());

//            Class.forName("com.mysql.jdbc.Driver").newInstance();
//            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/csit?" + "user=root&password=root");


        } catch (Exception e) {
            e.printStackTrace();
        }
        return con;
    }
}