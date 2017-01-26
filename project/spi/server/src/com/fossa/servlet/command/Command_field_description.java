/*
 * Command_field_discription.java
 *
 * Created on December 18, 2007, 7:22 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.fossa.servlet.command;

import com.fossa.servlet.common.CommonLogger;
import com.fossa.servlet.common.SQLQueries;
import com.fossa.servlet.common.msg.MessageWriter;
import com.fossa.servlet.dao.DBTask;
import com.fossa.servlet.session.UserTask;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.w3c.dom.Element;

/**
 * Command Class to Get FieldDescription For Selected ProjectFields
 * @author bmurali
 */
public class Command_field_description implements Command{  
    
    private Connection con;    
    private UserTask user;
    private DBTask dbTask;
    private String fieldDescription = null;
    
    public String execute(Element action, UserTask user, DBTask dbTask, MessageWriter writer) {
        this.user = user;
        this.dbTask = dbTask;
        this.con = dbTask.getConnection();
        Element givenValueList = action;
        int projectId = Integer.parseInt(givenValueList.getAttribute(A_PROJECT_ID));
        String fieldName = givenValueList.getAttribute(A_SELECTED_FIELD_NAME);
        String userSessionId = user.getFossaSessionId();        
        ResultSet getFieldDescResultSet;
        try {
            PreparedStatement getFieldDescPrepStmt =  con.prepareStatement(SQLQueries.SEL_DESC);
            getFieldDescPrepStmt.setInt(1, projectId);
            getFieldDescPrepStmt.setString(2, fieldName);
            getFieldDescResultSet = getFieldDescPrepStmt.executeQuery();            
            if (! getFieldDescResultSet.next()) {
                fieldDescription = "Description not available for the selected fields";             
            }else{
               fieldDescription = getFieldDescResultSet.getString(1);               
            }          
            try {
               //Starts writing the XML
                writer.startElement(T_FIELD_VALUES);
                writer.writeAttribute(A_FOSSAID, userSessionId);
                writer.writeAttribute(A_SELECTED_FIELD_NAME, fieldDescription);
                writer.endElement();
            } catch (IOException ex) {
                CommonLogger.printExceptions(this, "IOException while writing field description." , ex);
            }
        } catch (SQLException ex) {
                CommonLogger.printExceptions(this, "SQLException while writing field description." , ex);
        }
        return null;
    }
    
    public boolean isReadOnly() {
        return false;
    }
    
}
