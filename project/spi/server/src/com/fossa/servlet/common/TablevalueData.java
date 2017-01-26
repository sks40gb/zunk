/*
 * TablevalueData.java
 *
 * Created on November 21, 2007, 1:59 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.fossa.servlet.common;

/**
 *
 * @author Bala
 */
public class TablevalueData {
    
    /** id of the tablespec row, or 0 for insert */
    public int tablevalue_id;

    /** tablespec_id of the table */
    public int tablespec_id;

    /** value to be written to the table or "" for delete */
    public String value;

    /** document level indicator */
    public int level;

    /** the value in tablespec.model_table that controls this tablevalue.value */
    public String model_value;

    /** original value of this tablevalue.value */
    public String old_value;
    
}
