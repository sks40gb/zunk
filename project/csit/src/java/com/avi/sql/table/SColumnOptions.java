/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.avi.sql.table;

import com.avi.sql.exception.InvalidColumnOption;

/**
 *
 * @author sunil
 */
public class SColumnOptions {
    private Type type;
    private boolean PK;
    private boolean notNull;
    private boolean autoIncrement;

    public SColumnOptions(Type type) {
        this.type = type;
    } 

    public boolean isPK() {
        return PK;
    }

    public void setPK(boolean PK) {
        this.PK = PK;
    }

    public boolean isAutoIncrement() {
        return autoIncrement;
    }

    public void setAutoIncrement(boolean autoIncrement) throws InvalidColumnOption {
        if(type == Type.BOOLEAN || type == Type.INT ||type == Type.FLOAT ||type == Type.DOUBLE){
            this.autoIncrement = autoIncrement;
        }else{
            throw new InvalidColumnOption("AutoIncrement");
        }
    }

    public boolean isNotNull() {
        return notNull;
    }

    public void setNotNull(boolean notNull) {
        this.notNull = notNull;
    }

}

