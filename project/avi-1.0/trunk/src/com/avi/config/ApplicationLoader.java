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
        dbClassName = bundle.getString("driver-class");
        String type = bundle.getString("db-type");
        String driverType = bundle.getString("driver-type");
        String host = bundle.getString("db-host");
        String port = bundle.getString("db-port");
        String schema = bundle.getString("db-schema");
        String user = bundle.getString("db-user");
        String password = bundle.getString("db-password");

        StringBuffer buffer = new StringBuffer();
        buffer.append(driverType);
        buffer.append(":");
        buffer.append(type);
        buffer.append("://");
        buffer.append(host);
        buffer.append(":");
        buffer.append(port);
        buffer.append("/");
        buffer.append(schema);
        buffer.append("?");
        buffer.append("user=");
        buffer.append(user);
        buffer.append("&");
        buffer.append("password=");
        buffer.append(password);
        dbConnectionUrl = buffer.toString();
        init(bundle);
    }

    public static List<Class> getTableClassList() {
        return tableClassList;
    }

    public static void main(String[] args) {
        System.out.println("==> " + dbConnectionUrl);
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

    

    /* driver-class=com.mysql.jdbc.Driver
db-type=mysql
db-host=localhost
db-port=3306
db-schema=csit
db-user=root
db-password=root
 *
 *     Class.forName("com.mysql.jdbc.Driver").newInstance();
con = DriverManager.getConnection("jdbc:mysql://localhost:3306/csit?user=root&password=root");
 */

}
 











