/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.fossa.servlet.common;

/**
 *
 * @author bala
 */

/**
 * Define constant for batch status
 * */
public class BatchStatus {
    
     //batch status is Listing
    final public static String S_LISTING = "Listing Idle";
    
    //batch status is ListingComplete
    final public static String S_LISTINGCOMPLETE = "Listing Complete";
    
    //batch status is Tally
    final public static String S_TALLY = "Tally Idle";
    
    //batch status is TallyComplete
    final public static String S_TALLYCOMPLETE = "Tally Complete";
    
    //batch status is Masking
    final public static String S_MASKING = "Masking Idle";
    
     //batch status is Masking
    final public static String S_MASKINGQUEUE = "Masking Queue";
    
     //batch status is Masking
    final public static String S_MASKINGASSIGN = "Masking Assign";
    
    //batch status is MaskingComplete
    final public static String S_MASKINGCOMPLETE = "Masking Complete";
    
    //batch status is ModifyErrors
    final public static String S_MODIFYERRORS = "ModifyErrors";

}
