/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fossa.servlet.command;

import com.fossa.servlet.session.UserTask;

/**
 * This class create the new thread in server side.
 * It is called for post validation.
 * It generate the post validation report, write the repost to 
 * the excel sheet and then insert the record into the database.  
 * @author sunil
 */
public class TimerTask implements Runnable {

    private int volumeId;
    private int projectId;
    private int postValidationId;
    private String IdString;
    private UserTask user;
   

    /**
     * Create an instance of Start timer 
     * @param volumeId - Volume Id
     * @param projectId -Project Id
     * @param IdString - String having the Fields Ids and corresponding 
     * Functions Ids.
     */
    public TimerTask(int volumeId, int projectId, String IdString, int post_validation_id, UserTask user) {
        this.volumeId = volumeId;
        this.projectId = projectId;
        this.IdString = IdString;
        this.postValidationId = post_validation_id;
        this.user = user;
    }

    /**
     * Start Post validation 
     */
    public void run() {
        // Instantiate the PostValidation and get the record for the project
        // and volume.
        PostValidation postvalidation = new PostValidation(volumeId, projectId,postValidationId,IdString);
        // Start the post validation and generate report.
        postvalidation.runScript();
        // Before writing the data set the current postValidation id and report 
        // Excel file name with path        
        // Write report to the Excelsheet 
        postvalidation.writeReportToExcelSheet(user);
        // Write report to the database.
        postvalidation.writeReportToDB();
    }    
}
