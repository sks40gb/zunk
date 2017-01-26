/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.fossa.servlet.command;

/**
 * This interface is used for managing the Mode in history tables
 * @author sunil
 */
/**
 * variable required to show the status for  particula action
 * 
 */
public interface Mode {
    
    /** variable shows the status for adding a row of field data*/
    String ADD = "Add";
    
    /** variable shows the status for editing a row of field data*/
    String EDIT = "Edit";
    
    /** variable shows the status for deleting a row of field data*/
    String DELETE = "Delete";
    
    /** variable shows whether the status changed or not */
    String STATUS_CHANGED = "Status Changed";    
}
