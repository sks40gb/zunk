/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.avi.sql.exception;

import com.avi.sql.table.model.TableModel;
import java.lang.reflect.Field;

/**
 *
 * @author Admin
 */
public class TableFieldException extends Exception{
    public TableFieldException(Field f, TableModel t) {
        super(f.getName() + " is invalid Field in Table " + t.toString());
    }
    }
