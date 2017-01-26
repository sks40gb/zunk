/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.avi.sql.table;

import com.avi.sql.exception.InvalidColumnOption;
import com.avi.sql.exception.TableFieldException;
import com.avi.sql.table.annotation.Column;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Date;

/**
 *
 * @author sunil
 */
public class SColumn {

    private Type type;
    private String name;
    private Field field;
    private SColumnOptions options;
    private SConstraints constraints;

    public SColumn(Field field) throws InvalidColumnOption {
        this.field = field;
        this.name = field.getName();
        init();
    }

    public SColumn(String name, Field field) throws InvalidColumnOption {
        this.name = name;
        this.field = field;
        init();
    }

    public SColumn(String name, Field field, boolean isPk) throws InvalidColumnOption {
        this.name = name;
        this.field = field;
        options.setPK(isPk);
        init();
    }

    public void init() throws InvalidColumnOption {
        options = new SColumnOptions(getType());
        Column anno = field.getAnnotation(Column.class);
        if (anno != null) {
            if (anno.isPK()) {
                options.setPK(true);
            }
            if (anno.autoIncrement()) {
                options.setAutoIncrement(true);
            }
            if (anno.notNull()) {
                options.setNotNull(true);
            }
        }
    }

    public Field getField() {
        return field;
    }

    public void setField(Field field) {
        this.field = field;
    }

    public String getFieldName() {
        return field.getName();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setValue(STable table, Object value) throws NoSuchMethodException, IllegalAccessException, IllegalArgumentException, TableFieldException, InvocationTargetException {
        getSetter(table).invoke(table, value);
    }

    public Type getType() {
        if (type == null) {
            if (getField().getType().toString().equals("int")) {
                type = Type.INT;
            } else if (getField().getType().toString().equals("double")) {
                type = Type.DOUBLE;
            } else if (getField().getType().toString().equals("float")) {
                type = Type.FLOAT;
            } else if (getField().getType().toString().equals("boolean")) {
                type = Type.BOOLEAN;
            } else if (getField().getType().toString().equals("class java.lang.String")) {
                type = Type.STRING;
            } else if (getField().getType().toString().equals("class java.util.Date")) {
                type = Type.DATE;
            } else {
                //throw new UnsupportedColumnType(getField().getType());
            }
        }
        return type;
    }

//    public boolean isValueSet(STable table) throws IllegalArgumentException, Exception {
//        if (getType() == null) {
//            if (getType() == Type.INT) {
//                return getInt(table) == 0;
//            } else if (getType() == Type.DOUBLE) {
//                return getDouble(table) == 0.0;
//            } else if (getType() == Type.FLOAT) {
//                return getFloat(table) == 0.0;
//            } else if (getType() == Type.BOOLEAN) {
//                return getBoolean(table);
//            } else {
//                return getValue(table) == null;
//            }
//        } else {
//            return false;
//        }
//    }

    public boolean isPK() {
        return options.isPK();
    }

    public boolean isAutoIncrement() {
        return options.isAutoIncrement();
    }

    public boolean isNotNull() {
        return options.isNotNull();
    }

    public void setConstraints(SConstraints constraints) {
        this.constraints = constraints;
    }

    public SConstraints getConstraints() {
        return constraints;
    }

    public Object getValue(STable table) throws IllegalArgumentException, IllegalAccessException, Exception {
        return getGetter(table).invoke(table);
    }

    public int getInt(STable table) throws IllegalArgumentException, IllegalAccessException, Exception {
        return Integer.parseInt(getValue(table).toString());
    }

    public double getDouble(STable table) throws IllegalArgumentException, IllegalAccessException, Exception {
        return Double.parseDouble(getValue(table).toString());
    }

    public float getFloat(STable table) throws IllegalArgumentException, IllegalAccessException, Exception {
        return Float.parseFloat(getValue(table).toString());
    }

    public boolean getBoolean(STable table) throws IllegalArgumentException, IllegalAccessException, Exception {
        return Boolean.valueOf(getValue(table).toString());
    }

    public String getString(STable table) throws IllegalArgumentException, IllegalAccessException, Exception {
        return getValue(table) == null ? null : getValue(table).toString();
    }

    public Date getDate(STable table) throws IllegalArgumentException, IllegalAccessException, Exception {
        return (Date) getValue(table);
    }

    private Method getGetter(STable table) throws NoSuchMethodException, TableFieldException {
        return table.getClass().getMethod(getMethodName(field, "get"));
    }

    private Method getSetter(STable table) throws NoSuchMethodException, TableFieldException {
        return table.getClass().getMethod(getMethodName(field, "set"), field.getType());
    }

    /**
     * TODO: needs to be implemented
     */
    private void validateColumn() {
    }

    private String getMethodName(Field f, String s) {
        String fName = f.getName();
        StringBuffer MName = new StringBuffer(s);
        MName.append(fName.substring(0, 1).toUpperCase());
        MName.append(fName.substring(1));
        return MName.toString();
    }
}




