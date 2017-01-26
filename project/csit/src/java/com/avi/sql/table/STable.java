/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.avi.sql.table;

import com.avi.sql.exception.InvalidColumnOption;
import com.avi.sql.table.annotation.Column;
import com.avi.sql.table.annotation.ForeignKey;
import com.avi.sql.table.annotation.Table;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author sunil
 */
public class STable {

    private String tableName;
    private List<SColumn> columnList;
    private Map<String, SColumn> fieldMap;
    private List<SColumn> idColumnList;
    private List<SColumn> foreignKeyColumnList;
    private boolean isUpdatable;

    public STable() {
        try {
            createColumnList();
        } catch (NoSuchFieldException ex) {
            Logger.getLogger(STable.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvalidColumnOption ex) {
            ex.printStackTrace();
        }
    }

    protected List<SColumn> getColumnList() throws InvalidColumnOption, NoSuchFieldException {
        if (columnList == null) {
            createColumnList();
        }
        return columnList;
    }

    private Map<String, SColumn> getFieldMap() throws InvalidColumnOption, NoSuchFieldException {
        if (fieldMap == null) {
            createColumnList();
        }
        return fieldMap;
    }

    protected SColumn getColumnByFieldName(String fieldName) throws InvalidColumnOption, NoSuchFieldException {
        return getFieldMap().get(fieldName);
    }

    protected void createColumnList() throws InvalidColumnOption, NoSuchFieldException {
        Class cl = this.getClass();
        columnList = new ArrayList<SColumn>();
        fieldMap = new HashMap<String, SColumn>();
        idColumnList = new ArrayList<SColumn>();
        foreignKeyColumnList = new ArrayList<SColumn>();
        for (Field f : cl.getDeclaredFields()) {

            // get Field having annotation @Column and add to columnList.
            Column c = f.getAnnotation(Column.class);
            if (c != null) {
                SColumn column;
                if (c.name().equals(Column.DEFAULT_NAME)) {
                    column = new SColumn(f);
                } else {
                    column = new SColumn(c.name(), f);
                }
                // get Field having annotation @ForeignKey and add to columnList.
                ForeignKey fk = f.getAnnotation(ForeignKey.class);
                if (fk != null) {                  
                    SConstraints constraints = new SConstraints(column.getName(), fk);
                    column.setConstraints(constraints);
                    foreignKeyColumnList.add(column);
                }

                if (column.isPK()) {
                    idColumnList.add(column);
                }
                columnList.add(column);
                fieldMap.put(column.getFieldName(), column);

            }
        }
    }

    private void setTableName() {
        Table c = this.getClass().getAnnotation(Table.class);
        if (c != null) {
            if (c.name().equals(Column.DEFAULT_NAME)) {
                tableName = this.getClass().getSimpleName();
            } else {
                tableName = c.name();
            }
        } else {
            tableName = this.getClass().getSimpleName();
        }
    }

    protected String getTableName() {
        if (tableName == null) {
            setTableName();
        }
        return tableName;
    }

    protected boolean isUpdatable() throws IllegalArgumentException, Exception{       
        return isUpdatable;
    }

    public void setUpdatable(boolean isUpdatable){
        this.isUpdatable = isUpdatable;
    }

    public List<SColumn> getIdColumnList() {
        return idColumnList == null ? new ArrayList<SColumn>() : idColumnList;
    }

    public List<SColumn> getForeignKeyColumnList() {
        return foreignKeyColumnList == null ? new ArrayList<SColumn>() : foreignKeyColumnList;
    }
}
