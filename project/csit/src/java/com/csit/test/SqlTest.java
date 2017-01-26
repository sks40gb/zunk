/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.csit.test;

import com.avi.sql.connection.DBConnection;
import com.csit.sql.table.model.UserModel;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 *
 * @author sunil
 */
public class SqlTest {

    static Connection con;
    static PreparedStatement  ps;
    
    public static void main(String[] args) throws Exception {
       // run("UPDATE COMMENT SET user_id = ?, subject = ?, comment = ?, comment_date = ? WHERE  comment_id = ?");
        UserModel user = new UserModel();
        user.setFirstName("mahendra");
        user.setLastName("kewat");
        user.setUserPassword("password");
        user.saveOrUpdate();
        user.setFirstName("mahendra new");
        user.saveOrUpdate();
    }

    private void select() throws SQLException{
        con = DBConnection.getConnection();
        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery("select * from USER");
        if (rs.next()) {
            System.out.println("------> " + rs.getString(2));

        } else {
            System.out.println("FAIL");
        }
    }

     private static void delete(String tableName) throws Exception {
        run("delete from " + tableName);
    }

     private static void run(String sql) throws Exception {
        con = DBConnection.getConnection();
        ps = con.prepareStatement(sql);
        ps.setInt(1,1076);
        ps.setString(2, "AAAA");
        ps.setString(3, "AAAA");
        ps.setString(4, "AAAA");
        ps.setInt(5, 0);
        int i = ps.executeUpdate();
        ps.close();
        con.commit();
        con.close();
    }
}
