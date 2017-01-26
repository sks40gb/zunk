package com.elearn.db.connection;


import java.sql.*;

/**
 *
 * @author sunil
 */
public class ElearnConnection {

    public static Connection getConnection() {
        Connection con = null;        
        try {
        Class.forName("jstels.jdbc.mdb.MDBDriver");
            con = DriverManager.getConnection("jdbc:jstels:mdb:/home/sunil/Desktop/elearn.mdb");
        } catch (Exception e) {
        }
        return con;
    }
}
