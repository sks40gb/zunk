/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.fossa.servlet.common;

/**
 *
 * @author Bala
 */
/**
 * Constants used to show the Batch status 
 */
public interface StatusConstants {
    
    /** Batch status is Unitization*/
    final public static int S_UNITIZE = 1;
    
    /** Batch status is UnitizationQC*/
    final public static int S_UQC = 2;
    
    /** Batch status is Unitization Complete*/
    final public static int S_UCOMPLETE = 3;
    
    /** Batch status is Unitization Batched*/
    final public static int S_UBATCHED = 4;
    
    /** Batch status is Coding*/
    final public static int S_CODING = 5;
    
    /** Batch status is CodingQC*/
    final public static int S_CODINGQC = 6;
    
    /** Batch status is CodingQC Complete*/
    final public static int S_QCOMPLETE = 7;
    
    /** Batch status is QA*/
    final public static int S_QA = 8;
    
    /** Batch status is QA Complete*/
    final public static int S_QACOMPLETE = 9;
    
    /** Batch status is Listing*/
    final public static int S_LISTING = 10;  
    
    /** Batch status is Listing Complete*/
    final public static int S_LCOMPLETE = 11;
    
    /** Batch status is ListingQC Complete*/
    final public static int S_LQCCOMPLETE = 12;
    
    /** Batch status is Tally Complete*/
    final public static int S_TCOMPLETE = 15;
    
    /** Batch status is Masking*/
    final public static int S_MASKING =18;
    
    /** Batch status is Masking Complete*/
    final public static int S_MASKINGCOMPLETE =19;
    
    /** Batch status is ModifyErrors*/
    final public static int S_MODIFYERRORS = 20;
}