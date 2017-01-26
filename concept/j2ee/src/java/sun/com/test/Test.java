/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sun.com.test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 *
 * @author administrator
 */
public class Test {

    public static void main(String[] args) throws Exception {


        new Test().exeQuery("select * from conversion");
    }

    protected void exeQuery(String sqlStatement)
            throws Exception {

        try {
            Statement cs = makeCon().createStatement();
            ResultSet sqls = cs.executeQuery(sqlStatement);

            while (sqls.next()) {
//                                String id = (sqls.getObject("id").toString());
//                                String data = (sqls.getObject("data").toString());
//                                System.out.println(id + " " + data);
            }
            sqls.close();

        } catch (SQLException e) {
            System.out.println("Error executing sql statement");
            throw (e);
        }
    }

    protected Connection makeCon() throws Exception {
        String host = "cong46";
        String database = "seacorp_change";
        String user = "root";
        String password = "root";
        String url = "";
        try {
            url = "jdbc:mysql://" + host + ":3308/" + database;
            Connection con = DriverManager.getConnection(url, user, password);
            System.out.println("Connection established to " + url + "...");
            return con;
        } catch (java.sql.SQLException e) {
            System.out.println("Connection couldn't be established to " + url);
            throw (e);
        }
    }
}
