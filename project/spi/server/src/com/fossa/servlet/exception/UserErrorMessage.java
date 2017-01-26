/*
 * UserErrorMessage.java
 *
 * Created on February 1, 2008, 12:40 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package com.fossa.servlet.exception;

/**
 *
 * @author Bala
 */
/**
 * Constants used to define the Error Message
 */
public class UserErrorMessage {

    public static final String userInUse = "Can't connect -- User name in use";
    public static final String invalidPassword = "Can't connect -- Invalid password";
    public static final String providePassword = "Please provide a password";
    public static final String adminAuthentication = "Administration application not permitted for this userid";
    public static final String userAuthentication = "Viewer application not permitted for this userid";
    public static final String sqlNotDefined = "SQL not defined on server: ";
    public static final String unsavedDoc = "There are unsaved documents.";
    public static final String volumeNotAvailable = "Volume not available for QA";
    public static final String alreadyAssigned = "Batch is not available for queuing -- already assigned.";
    public static final String batchInUse = "Volume has batches assigned or in use.";
    public static final String rejectBatchStatus = "Invalid reject batch status.";
    public static final String closeBatchStatus = "Invalid close batch status.";
    public static final String unableToClose = "Unable to close batch.";
}
