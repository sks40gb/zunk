/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.avi.config;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

/**
 *
 * @author sunil
 */
public class ApplicationLoader {

    private static String dbClassName;
    private static String dbConnectionUrl;
    private static List<Class> tableClassList;
    private static final String TABLE = "table-class-";
    

    static {
        ResourceBundle bundle = ResourceBundle.getBundle("avi-conf");
        dbClassName = bundle.getString("jdbc.default.driverClassName");
        String dbUrl = bundle.getString("jdbc.default.url");
        StringBuffer buffer = new StringBuffer(dbUrl);
        dbConnectionUrl = buffer.toString();
        init(bundle);
    }

    public static List<Class> getTableClassList() {
        return tableClassList;
    }

    public static void init(ResourceBundle bundle) {
        tableClassList = new ArrayList<Class>();
        for (int i = 1;; i++) {
            try {
                Class c = Class.forName(bundle.getString(TABLE + i));
                tableClassList.add(c);
            } catch (Exception e) {
                //e.printStackTrace();
                break;
            }
        }
    }

    public static String getDbClassName() {
        return dbClassName;
    }

    public static String getDbConnectionUrl() {
        return dbConnectionUrl;
    }

    

 /* 
 *  
  con = DriverManager.getConnection("jdbc:mysql://localhost:3306/csit?user=root&password=root");
 */

}
 











