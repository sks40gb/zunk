/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.fossa.servlet.common;

/**
 *
 * @author cyrus
 */
/**
 * Define constant for batch status
 * */
public class CommonConstants {
    
    //batch status is Unitize
    public static final String PROCESS_UNITIZE = "Unitize";
    
    //batch status is UnitizeQC
    public static final String PROCESS_UNITIZE_QC = "UQC";
    
    //batch status is UnitizeComplete
    public static final String PROCESS_UNITIZE_COMPLETE = "UComplete";
    
    //batch status is UnitizeBatched(All the batches are unitized)
    public static final String PROCESS_UNITIZE_BATCHED = "UBatched";
    
    //batch status is Coding
    public static final String PROCESS_CODING = "Coding";
    
    //batch status is CodingQC
    public static final String PROCESS_CODING_QC = "CodingQC";
    
    //batch status is CodingQC Complete
    public static final String PROCESS_CODING_QC_COMPLETE = "QCComplete";
    
    //batch status is QA(Quality Analysis)
    public static final String PROCESS_QA = "QA";
    
    //batch status is QA Complete
    public static final String PROCESS_QA_COMPLETE = "QAComplete";    
    
     //batch status is QA Complete
    public static final String PROCESS_QA_REJECT = "QAReject"; 
    public static final String PROCESS_QA_ERROR = "QAError"; 
    public static final String BATCH_STATUS_QA_ERROR = "qaerror";
    
    
    //batch status is Masking (only for L1 batches)
    public static final String PROCESS_MASKING = "Masking";
    
     //batch status is Masking Complete
    public static final String PROCESS_MASKING_COMPLETE = "MaskingComplete";
    
    
     //batch status is Listing
    public static final String PROCESS_LISTING = "Listing";
    
     //batch status is Listing QC
    public static final String PROCESS_LISTING_QC = "ListingQC";
    
    
     //batch status is Listing Complete
    public static final String PROCESS_LISTING_COMPLETE = "LComplete";
    
    
     //batch status is ModifyErrors
    public static final String PROCESS_MODIFY_ERRORS = "ModifyErrors";
    
     //batch status is Tally
    public static final String PROCESS_TALLY = "Tally";
    
     //batch status is Tally
    public static final String PROCESS_TALLY_QC = "TallyQC";
    
     //batch status is Tally Complete
    public static final String PROCESS_TALLY_COMPLETE = "TComplete";
    
    
    //QA Group Assignment Status 
    public static final String STATUS_COMPLETED = "Completed";
    public static final String STATUS_INUSE = "In Use";
    public static final String STATUS_IDLE = "Idle";
    
    //common constants for sampling type
    public static final String SAMPLINGTYPE_FIELD_DISPLAY = "field_display";
    public static final String SAMPLINGTYPE_TAG_DISPLAY = "tag_display";
    
    //Commonconstants for FieldLevel (L1,L2)
    public static final String FIELD_LEVEL_L1 = "l1";
    public static final String FIELD_LEVEL_L2 = "l2";
    
    //CommonConstants for FieldType (name,text,date)
    public static final String FIELD_TYPE_NAME = "name";
    public static final String FIELD_TYPE_TEXT = "text";
    public static final String FIELD_TYPE_DATE = "date";
    
    //CommonConstants for L1 Information
    public static final String L1_INFORMATION_SOURCE = "Source";
    public static final String L1_INFORMATION_FOLDER = "Folder";
    
    //Constants For Sampling Status
     public static final String SAMPLING_STATUS_INPROGRESS = "In Progress";
     public static final String SAMPLING_STATUS_FINISHED = "Finished";
     
     //Constants For Field Error Type
     public static final String ERROR_TYPE_MISCODED = "MisCoded";
     public static final String ERROR_TYPE_UNCODED = "UnCoded";
     public static final String ERROR_TYPE_ADDED = "Added";
     
     //Constants For Sampling Result
     public static final String SAMPLING_RESULT_ACCEPT = "Accept";
     public static final String SAMPLING_RESULT_REJECT = "Reject";
     
     //common constants for sampling type
    
    public static String QA_SAMPLING_TYPE_ISO = "ISO 2859-1";
    public static String QA_SAMPLING_TYPE_FIXED = "Fixed Percentage";
    public static String QA_SAMPLING_CHARACTER = "Character Sampling";
    
    //Common constants for 'YES' and 'NO'
    public static String STATUS_YES = "Yes";
    public static String STATUS_NO = "No";
    
    //Common constants for 'Pass' and 'Fail'
    public static String STATUS_PASS = "Pass";
    public static String STATUS_FAIL = "Fail";
     
    
    
    
}
