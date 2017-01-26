/*
 * UnitpriceData.java
 *
 * Created on November 21, 2007, 12:14 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.fossa.servlet.common;

/**
 *
 * @author Bala
 */
public class UnitpriceData {
    
   /** id of the unitprice row, or 0 for insert */
    public int unitprice_id = 0;

    /** id of the project the unitprice belongs to */
    public int project_id = 0;

    /** id of the volume the unitprice belongs to, or 0 for project default */
    public int volume_id = 0;

    /** price applies to this level */
    public int field_level = 0;

    /** price of unitized page - make it a string for xml message */
    public String unitize_page_price = "0";
    
    /** price of unitized document - make it a string for xml message */
    public String unitize_doc_price = "0";
    
    /** price of uqc page - make it a string for xml message */
    public String uqc_page_price = "0";
    
    /** price of uqc document - make it a string for xml message */
    public String uqc_doc_price = "0";
    
    /** price of coded page - make it a string for xml message */
    public String coding_page_price = "0";
    
    /** price of coded document - make it a string for xml message */
    public String coding_doc_price = "0";
    
    /** price of codingQc'ed page - make it a string for xml message */
    public String codingqc_page_price = "0";
    
    /** price of codingQc'ed document - make it a string for xml message */
    public String codingqc_doc_price = "0";

}
