/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.avi.sql.exception;

/**
 *
 * @author sunil
 */
public class InvalidColumnOption extends Exception{
 String exception = "";
     public InvalidColumnOption(String option){
          super("Invalid Column Option :" + option);
    }

    @Override
    public String toString() {
        return exception;
    }
}
