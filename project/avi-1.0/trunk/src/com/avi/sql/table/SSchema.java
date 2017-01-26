/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.avi.sql.table;

import com.avi.config.ApplicationLoader;
import com.avi.sql.connection.DBConnection;
import com.avi.sql.exception.InvalidColumnOption;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;

/**
 *
 * @author sunil
 */
public class SSchema {

    private static Deque<STable> tableList;

    public static void main(String[] args) {
        createSchema();
    }

    public static void createSchema() {
        try {
            registerTables();
            while (getTableList().size() > 0) {
                createTable(getTableList().getFirst());
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private static void registerTables() throws SQLException, Exception {
        for (Class c : ApplicationLoader.getTableClassList()) {
            addTable((STable) c.newInstance());
        }
    }

    public static void addTable(STable table) {
        if (tableList == null) {
            tableList = new ArrayDeque<STable>();
        }
        tableList.add(table);
    }

    private static void createTable(STable table) throws SQLException, InvalidColumnOption, NoSuchFieldException {
        PreparedStatement ps = null;
        Connection con = null;
        try {
            StringBuffer buffer = new StringBuffer("CREATE TABLE IF NOT EXISTS ");
            buffer.append(table.getTableName() + "(");
            boolean flag = true;

            // create column with definitions.
            for (SColumn column : table.getColumnList()) {
                if (flag) {
                    buffer.append(getColumn(column));
                    flag = false;
                } else {
                    buffer.append("," + getColumn(column));
                }
            }

            // create set of primary key
            flag = true;
            List<SColumn> idColumnList = table.getIdColumnList();
            if (idColumnList.size() > 0) {
                buffer.append(", PRIMARY KEY(");
                for (SColumn column : idColumnList) {
                    if (flag) {
                        buffer.append(column.getName());
                        flag = false;
                    } else {
                        buffer.append("," + column.getName());
                    }
                }
                buffer.append(")");

                //create foreign key contraints               
                List<SColumn> fkColumnList = table.getForeignKeyColumnList();
                if (fkColumnList.size() > 0) {
                    for (SColumn column : fkColumnList) {
                        buffer.append(",");
                        buffer.append(column.getConstraints().toString());
                    }
                }

            }
            buffer.append(") ENGINE=INNODB");

            System.out.println(buffer);
            con = DBConnection.getConnection();
            ps = con.prepareStatement(buffer.toString());
            ps.executeUpdate();
            getTableList().removeFirst();
        } catch (SQLException e) {
            System.out.println("ERROR CODE : " + e.getErrorCode());
            //e.printStackTrace();
            if (e.getErrorCode() == 1005) {
                STable _table = getTableList().removeFirst();
                getTableList().addLast(_table);
            } else {
                throw new SQLException(e);
            }
        } finally {
            if (ps != null) {
                ps.close();
            }
            if (con != null && !con.isClosed()) {
                con.close();
            }
        }
    }

    private static String getColumn(SColumn column) {
        StringBuffer buffer = new StringBuffer();
        buffer.append(column.getName());
        buffer.append(" ");
        buffer.append(column.getType().getName());
        if (column.isNotNull()) {
            buffer.append(" ");
            buffer.append("NOT NULL");
        }
        if (column.isAutoIncrement()) {
            buffer.append(" ");
            buffer.append("AUTO_INCREMENT");
        }
        return buffer.toString();
    }

    private static Deque<STable> getTableList() {
        return tableList == null ? new ArrayDeque<STable>() : tableList;
    }
}

