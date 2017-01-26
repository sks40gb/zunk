/*
 * ValidationData.java
 *
 * Created on October 24, 2007, 10:21 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package common;

/**
 * Class to Hold Validation Data
 * @author bmurali
 */
public class ValidationData{

   public int rowCount=0;
   
   public String [] validationValue=null;
    
    
   public ValidationData(String [] rowValue,int row){
     rowCount=row;  
     validationValue = rowValue; 
    
    
    }
}
