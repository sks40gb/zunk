/*
 * EditCodingManualData.java
 *
 * Created on January 27, 2008, 11:26 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package com.fossa.servlet.common;

/**
 *
 * @author bala
 */
/**
 * A container for data required to Edit CodingManual.
 */
public class EditCodingManualDataServer {
    
    //Existing File name for CodingManual
    public String existingFileName = "";
    
    //New File name for CodingManual
    public String changeFileName = "";
    
    
    public int projectId = 0;
    
    //File path for CodingManual
    public String filePath = "";
    
    public int volume_id = 0;
    
    //Server IP for CodingManual where the new file is existing
    public String serverIP = "";
    
    //Server port for CodingManual where the new file is existing
    public String port = "";
    
    //Internal volume name for CodingManual 
    public String internal_volume = "";
    
    // Volume completion name for CodingManual 
    public String volume_completion_date = "";
}
