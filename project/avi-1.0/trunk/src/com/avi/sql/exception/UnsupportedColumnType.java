/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.avi.sql.exception;

/**
 *
 * @author sunil
 */
public class UnsupportedColumnType extends Exception{
     public UnsupportedColumnType(Object type) {
        super(type + " is invalid Column Type ");
    }
}
