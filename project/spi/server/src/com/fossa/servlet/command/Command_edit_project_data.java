/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.fossa.servlet.command;

import com.fossa.servlet.common.CommonLogger;
import com.fossa.servlet.common.EditCodingManualDataServer;
import com.fossa.servlet.common.msg.MessageWriter;
import com.fossa.servlet.common.msg.XmlReader;
import com.fossa.servlet.dao.DBTask;
import com.fossa.servlet.exception.ServerFailException;
import com.fossa.servlet.session.UserTask;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * This class updates the image path for the Given volumes.
 * @author bmurali
 */
public class Command_edit_project_data implements Command{
    private  PreparedStatement updateVolumePrepStmt;    
    private Connection connection;
    
    public String execute(Element action, UserTask user, DBTask dbTask, MessageWriter writer) {        
        Element givenValueList = action;
        
        Node firstChild = givenValueList.getFirstChild();
        while (firstChild != null && firstChild.getNodeType() != Node.ELEMENT_NODE) {
            firstChild = firstChild.getNextSibling();
        } 
        if (firstChild != null) {
            EditCodingManualDataServer data = new EditCodingManualDataServer();
            // fill in the int and String fields of the UsersData
            XmlReader reader=new XmlReader();            
            try {                
                reader.decode(givenValueList, data);
            } catch (IOException ex) {                
                CommonLogger.printExceptions(this, "Exception while reading the XMLReader." ,ex);
            }
            if (data != null) {                
                connection = dbTask.getConnection();                
                try {                
                    updateVolumePrepStmt = connection.prepareStatement("UPDATE volume SET image_path = ? ,image_server =? ," +
                                                      "internal_volume_name =? ,vol_completed_date =?" +
                                                      "WHERE volume_id =?");                  
                    updateVolumePrepStmt.setString(1, data.existingFileName);
                    if(null != data.serverIP && !data.serverIP.isEmpty() && null != data.port && !data.port.isEmpty()){
                        updateVolumePrepStmt.setString(2, "image:"+data.serverIP+":"+data.port+":");
                    }else{
                       updateVolumePrepStmt.setString(2, "unc:");
                    }                     
                    updateVolumePrepStmt.setInt(5, data.volume_id);
                    updateVolumePrepStmt.setString(3, data.internal_volume);
                    updateVolumePrepStmt.setString(4, data.volume_completion_date);
                    updateVolumePrepStmt.executeUpdate();                   
                    updateVolumePrepStmt.close();                   
                }catch (ServerFailException s){
                     CommonLogger.printExceptions(this, "ServerFailException while updating the image path for volume.", s);
                     return s.getMessage();         
               }   catch (SQLException ex) {
                     CommonLogger.printExceptions(this, "Exception while updating the image path for volume.", ex);
                }
            }            
        }        
        return null;
    }

    public boolean isReadOnly() {
        return true;
    }

}
