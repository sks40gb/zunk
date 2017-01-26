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
 * A container for data required for display of QueryTracker.
 * 
 */
public class QueryData {
    
    
    public int query_tracker_id ;
  
    public int project_id = 0;
    
    public int volume_id = 0;
    
    public int batch_id = 0;
    
    public int userId = 0;

    public String project_name = "";
    
    public String volume_name = "";
  
    public String document_name = "";

    public String field_name = "";
    
    /** Level of volume(L1/L2)*/
    public String level = "";
    
    public String collection = "";
    
    
    public String dtys = "";
 
    public String dtyg = "";
 
    public String dtyg_spi = "";
  
    public String dtys_spi = "";
   
    /** description for particular query*/
    public String description = "";
    
    /** Default Question*/
    public int generalQuestion = 0;
    
    /** Specific question the user can raise*/
    public String specificQuestion = "";
    
    /** Answer for the raised query*/
    public String answer = "";
   
    /** Query posted date*/
    public String posted_date = "";
    
    /** Query answered date*/
    public String answered_date = "";
    
    /** query is raised to which user*/
    public int raised_to = 0;
    
    /** batesNumber for that document*/
    public String batesNumber  = "";
    
    /** Is Image uploaded */
    public String uploadImage = "No";
    
    /** Query assigned to which user*/
    public String assignUser ="";
    
    public String imagePath = "";
    
    /** Query raised for what type of field*/
    public String field_type = "";
    
    public int childId =0;
}
