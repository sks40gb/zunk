/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.avi.sql.table;

/**
 *
 * @author sunil
 */
public enum Type {

    INT("INT"),
    FLOAT("FLOAT"),
    DOUBLE("DOUBLE"),
    STRING("TEXT"),
    DATE("DATE"),
    BOOLEAN("BOOLEAN");

    String name;
    Type(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
