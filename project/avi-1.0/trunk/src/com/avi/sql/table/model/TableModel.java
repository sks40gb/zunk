/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.avi.sql.table.model;

import com.avi.sql.table.*;
import com.avi.sql.connection.DBConnection;
import com.avi.sql.exception.InvalidColumnOption;
import java.sql.Statement;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


/**
 *
 * @author Admin
 */
public class TableModel extends STable implements Cloneable {

    private PreparedStatement ps;
    private ResultSet rs;
    private Statement st;
    private Connection conn;
    private Criteria criteria;
    private List tableList;
    protected final String BLANK = "";    

    public void delete() throws SQLException, Exception {
        try {
            getConnection();
            StringBuffer sql = new StringBuffer("DELETE FROM " + getTableName());
            //if no criteria, then delete by id
            if (criteria == null) {
                for (SColumn column : getIdColumnList()) {
                    addCriteria(column);
                }
            }
            sql.append(criteria.createCriteria());
            ps = getConnection().prepareStatement(sql.toString());
            System.out.println(sql);
            int i = ps.executeUpdate();
            if (i < 1) {
                throw new Exception(this.getClass() + " Deletion Failed");
            }
        } finally {
            close();
        }
    }

    public void save() throws SQLException, Exception {
        try {
            List<SColumn> columnList = getColumnList();
            if (getIdColumnList() != null) {
                for (SColumn column : getIdColumnList()) {
                    if (column.isAutoIncrement()) {
                        columnList.remove(column);
                    }
                }
            }

            getConnection();
            StringBuffer sql = new StringBuffer("INSERT INTO " + getTableName() + "( ");
            StringBuffer queMark = new StringBuffer();
            boolean isFirst = true;
            for (SColumn column : columnList) {
                String columnName = column.getName();
                if (isFirst) {
                    sql.append(columnName);
                    queMark.append("(?");
                    isFirst = false;
                } else {
                    sql.append(", " + columnName);
                    queMark.append(",?");
                }
            }
            queMark.append(")");
            sql.append(") VALUES ");
            sql.append(queMark);
            System.out.println(sql);
            ps = getConnection().prepareStatement(sql.toString());
            setPreparedStatment(columnList);
            int i = ps.executeUpdate();
            if (i < 1) {
                throw new Exception(this.getClass() + " Insertion Failed");
            } else {
                setIds();
                setUpdatable(true);
            }
        } finally {
            close();
        }
    }

    public void update() throws SQLException, Exception {
        try {
            getConnection();
            List<SColumn> columnList = getColumnList();
            for (SColumn column : getIdColumnList()) {
                columnList.remove(column);
            }

            StringBuffer sql = new StringBuffer("UPDATE " + getTableName() + " SET ");
            boolean isFirst = true;
            for (SColumn column : columnList) {
                String columnName = column.getName();
                if (isFirst) {
                    sql.append(columnName + " = ?");
                    isFirst = false;
                } else {
                    sql.append(", " + columnName + " = ?");
                }
            }
            if (criteria == null) {
                for (SColumn column : getIdColumnList()) {
                    addCriteria(column);
                }
            }
            sql.append(criteria.createCriteria());
            ps = getConnection().prepareStatement(sql.toString());
            System.out.println(sql);
            setPreparedStatment(columnList);
            int i = ps.executeUpdate();
            if (i < 1) {
                throw new Exception(this.getClass() + " cannot be updated");
            }else{
                setUpdatable(true);
            }
        } finally {
            close();
        }
    }
    
    public void saveOrUpdate() throws IllegalArgumentException, Exception{
        if(isUpdatable()){
            update();
        }else{
            save();
        }
    }

    public List getModelList() throws SQLException, IllegalArgumentException, IllegalAccessException, Exception {
        try {
            tableList = new ArrayList();
            st = getConnection().createStatement();
            rs = st.executeQuery(getSelectQuery(getColumnList()));
            while (rs.next()) {
                TableModel t = this.getClass().newInstance();
                setFieldValueFromRS(t, t.getColumnList());
                t.setUpdatable(true);
                tableList.add(t);
            }
        } finally {
            close();
        }
        return tableList;
    }

    public TableModel getModel() throws SQLException, IllegalArgumentException, IllegalAccessException, Exception {
        try {
            st = getConnection().createStatement();
            rs = st.executeQuery(getSelectQuery(getColumnList()));
            if (rs.next()) {
                setFieldValueFromRS(this, this.getColumnList());
                setUpdatable(true);
            } else {
                System.out.println("NO RESULT FOUND");
            }
        } finally {
            close();
        }
        return this;
    }

    private void setIds() throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException, Exception {
        try {
            if (getIdColumnList().size() > 0) {
                st = getConnection().createStatement();
                StringBuffer buffer = new StringBuffer(getSelectQuery(getIdColumnList()));
                buffer.append(" ORDER BY ");
                buffer.append(getIdColumnList().get(0).getName());
                buffer.append(" DESC LIMIT 1");
                System.out.println(buffer);
                rs = st.executeQuery(buffer.toString());
                if (rs.next()) {
                    setFieldValueFromRS(this, getIdColumnList());
                } else {
                    System.out.println("NO RESULT FOUND");
                }
            }
        } finally {
            close();
        }
    }

    private String getSelectQuery(Collection<SColumn> collection) throws NoSuchFieldException, IllegalArgumentException, Exception {
        StringBuffer sql = new StringBuffer("SELECT ");
        boolean isFirst = true;
        for (SColumn column : collection) {
            if (isFirst) {
                sql.append(column.getName());
                isFirst = false;
            } else {
                sql.append("," + column.getName());
            }
        }
        sql.append(" FROM  " + getTableName());
        if (criteria != null) {
            sql.append(criteria.createCriteria());
        }

        System.out.println(sql);
        return sql.toString();
    }

    public static void main(String[] args) {
    }

    private void setPreparedStatment(List<SColumn> cList) throws IllegalArgumentException, SQLException, IllegalAccessException, Exception {
        int count = 0;
        for (SColumn column : cList) {
            count++;
            if (column.getType() == Type.INT) {
                ps.setInt(count, column.getInt(this));
            } else if (column.getType() == Type.DOUBLE) {
                ps.setDouble(count, column.getDouble(this));
            } else if (column.getType() == Type.FLOAT) {
                ps.setFloat(count, column.getFloat(this));
            } else if (column.getType() == Type.STRING) {
                ps.setString(count, column.getString(this));
            } else if (column.getType() == Type.DATE) {
                ps.setDate(count, getSqlDate(column.getDate(this)));
            } else {
                throw new Exception(column.getType() + " type IS NOT SUPPORTED YET");
            }
        }
    }

    private void setFieldValueFromRS(STable table, List<SColumn> columnList) throws IllegalArgumentException, SQLException, IllegalAccessException, Exception {
        for (SColumn column : columnList) {
            setFieldValueFromRS(table, column);
        }
    }

    private void setFieldValueFromRS(STable table, SColumn column) throws IllegalArgumentException, SQLException, IllegalAccessException, Exception {
        Field f = column.getField();
        if (f == null) {
            throw new Exception("column does not exits in class ");
        }
        if (f.getType().toString().equals("int")) {
            column.setValue(table, rs.getInt(column.getName()));
        } else if (f.getType().toString().equals("double")) {
            column.setValue(table, rs.getDouble(column.getName()));
        } else if (f.getType().toString().equals("float")) {
            column.setValue(table, rs.getFloat(column.getName()));
        } else if (f.getType().toString().equals("class java.lang.String")) {
            column.setValue(table, rs.getString(column.getName()));
        } else if (f.getType().toString().equals("class java.util.Date")) {
            column.setValue(table, getUtilDate(rs.getDate(column.getName())));
        } else {
            throw new Exception(f.getType() + " : IS NOT SUPPORTED YET");
        }
    }

    private void addCriteria(SColumn column) throws IllegalArgumentException, Exception {

        if (criteria == null) {
            criteria = new Criteria(this);
        }
        criteria.addCriteria(column, column.getValue(this), CriteriaType.STRING_EQUAL);
    }

    public void addCriteria(String fieldName) throws IllegalArgumentException, Exception {

        SColumn column = getColumnByFieldName(fieldName);
        if (criteria == null) {
            criteria = new Criteria(this);
        }
        criteria.addCriteria(column, column.getValue(this), CriteriaType.STRING_EQUAL);
    }

    public void addCriteria(String fieldName, Object matchTo) throws IllegalArgumentException,InvalidColumnOption, Exception {
        if (criteria == null) {
            criteria = new Criteria(this);
        }
        SColumn column = getColumnByFieldName(fieldName);
        criteria.addCriteria(column, matchTo, CriteriaType.STRING_EQUAL);
    }

    public void addCriteria(String fieldName, CriteriaType type) throws IllegalArgumentException,InvalidColumnOption, Exception {
        if (criteria == null) {
            criteria = new Criteria(this);
        }
        SColumn column = getColumnByFieldName(fieldName);
        criteria.addCriteria(column, column.getValue(this), CriteriaType.STRING_EQUAL);
    }

    public void addCriteria(String fieldName, Object matchTo, CriteriaType type) throws InvalidColumnOption, NoSuchFieldException {
        if (criteria == null) {
            criteria = new Criteria(this);
        }
        SColumn column = getColumnByFieldName(fieldName);
        criteria.addCriteria(column, matchTo, CriteriaType.STRING_EQUAL);
    }

    public void removeAllCriteria() {
        criteria = null;
    }

    public Connection getConnection() throws SQLException {
        if (conn == null || conn.isClosed()) {
            conn = DBConnection.getConnection();
        }
        return conn;
    }

    private void close() throws SQLException {
        if (ps != null) {
            ps.close();
        }
        if (st != null) {
            st.close();
        }
        if (ps != null) {
            ps.close();
        }
        if (conn != null && !conn.isClosed()) {
            //conn.commit();
            conn.close();
        }
        if (criteria != null) {
            criteria = null;
        }
    }

    private java.sql.Date getSqlDate(java.util.Date date) {
        java.sql.Date sqlDate = null;
        if (date != null) {
            sqlDate = new java.sql.Date(date.getTime());
        }
        return sqlDate;
    }

    private java.util.Date getUtilDate(java.sql.Date sqlDate) {
        java.util.Date date = null;
        if (sqlDate != null) {
            date = new java.util.Date(sqlDate.getTime());
        }
        return date;
    }    

    private String getSeparators(String str) {
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < (50 - str.length()); i++) {
            buffer.append(" ");
        }
        return buffer.toString();
    }

    @Override
    public String toString() {
        StringBuffer buffer = new StringBuffer("========================== " + getTableName() + " ================================\n");
        try {
            for (SColumn column : getColumnList()) {
                Field f = column.getField();
                buffer.append(f.getName());
                buffer.append(getSeparators(f.getName()));
                buffer.append(column.getValue(this));
                buffer.append("\n");
            }
            buffer.append("=====================================================================");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return buffer.toString();
    }
}
