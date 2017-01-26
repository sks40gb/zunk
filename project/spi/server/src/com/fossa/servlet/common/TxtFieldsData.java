/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fossa.servlet.common;

/**
 *
 * @author sunil
 */
/**
 * variable used to read the brs file for import the volume  
 */

public class TxtFieldsData {

    /** Type of data (SSOU/SREV)*/
    private String group_Type = "";
    
    /** data path name*/
    private String group_one_path = "";
    
    /** data file name*/
    private String group_one_filename = "";

    public String getGroup_Type() {
        return group_Type;
    }

    public void setGroup_Type(String group_Type) {
        this.group_Type = group_Type;
    }

    public String getGroup_one_path() {
        return group_one_path;
    }

    public void setGroup_one_path(String group_one_path) {
        this.group_one_path = group_one_path;
    }

    public String getGroup_one_filename() {
        return group_one_filename;
    }

    public void setGroup_one_filename(String group_one_filename) {
        this.group_one_filename = group_one_filename;
    }
}
