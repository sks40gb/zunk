package com.jdbc.connection;

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
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/portofentry","root","123456");

        } catch (Exception e) {
            e.printStackTrace();
        }
        return con;
    }
}