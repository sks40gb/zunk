/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.avi.sql.exception;

import com.avi.sql.table.SColumn;

/**
 *
 * @author sunil
 */
public class InvalidFieldValueException extends Exception{
    String exception = "";
     public InvalidFieldValueException(SColumn column, String inputValue){
          super("value of " + column.getName() + " should be a type of " + column.getType().getName() + " ");       
          exception = "Invalid value : "+ inputValue + " : Value of " + column.getName() + " should be a type of " + column.getType().getName() + " ";
    }

    @Override
    public String toString() {
        return exception;
    }
}
