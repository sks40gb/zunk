/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.fossa.servlet.command;

import com.fossa.servlet.common.Log;
import com.fossa.servlet.common.msg.MessageWriter;
import com.fossa.servlet.dao.DBTask;
import com.fossa.servlet.session.UserTask;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Command Class to Save ALL the Project FieldValue Occurrance 
 * in Given Project and Volume for Listing Fix.
 * @author balab
 */
public class Command_save_listing_occurrence_list implements Command{
    
    Connection connection;
    PreparedStatement pstmt; //PStatement to Update Listing Occurrence
    PreparedStatement pstmt2; //PStatement to Update ProjectFields 
    Statement st ;            //Statement to select ChildId
    ResultSet rs;             //ResultSet to store selected ChildId
    
    public String execute(Element action, UserTask user, DBTask dbTask, MessageWriter writer) {
        
        connection = dbTask.getConnection();
        ArrayList valueList = new ArrayList();
        String fieldName = action.getAttribute(A_FIELD_NAME);        
        int projectId = Integer.parseInt(action.getAttribute(A_PROJECT_ID));
        int volumeId =  Integer.parseInt(action.getAttribute(A_VOLUME_ID));
        Node firstChild = action.getFirstChild();
        while (firstChild != null && firstChild.getNodeType() != Node.ELEMENT_NODE) {
            firstChild = firstChild.getNextSibling();
        }
        if (firstChild != null) {       
            try {               
                 Node child = action.getFirstChild();
                 child=child.getNextSibling();
                while (child != null) {
                    if (child.getNodeType() == Node.ELEMENT_NODE
                            && T_LISTING_OCCURRENCE_LIST.equals(child.getNodeName())) {                        
                        String  value = com.fossa.servlet.common.msg.XmlUtil.getTextFromNode(child);       
                         valueList.add(value);                        
                    }
                    child = child.getNextSibling();
                }              
            } catch (Throwable t) {
               logger.error("Exception while reading the XMLReader." + t);
               StringWriter sw = new StringWriter();
               t.printStackTrace(new PrintWriter(sw));
               logger.error(sw.toString());
               Log.quit(t);
            }
            try {
                      
                for(int i=0;valueList.size()>i;i++){ 
                    String value = (String) valueList.get(i);
                    String [] split = value.split("-");                    
                    pstmt = connection.prepareStatement("UPDATE listing_occurrence SET marking =? WHERE project_id =? AND field_name = ? AND field_value = ? AND volume_id = ?");
                    if(split[1].equals("true")){
                       pstmt.setString(1, "Yes");
                    }else{
                       pstmt.setString(1, "No");
                    }                   
                    pstmt.setInt(2, projectId);
                    pstmt.setString(3, fieldName);
                    pstmt.setObject(4, split[0]);
                    pstmt.setInt(5, volumeId);
                    pstmt.executeUpdate();                     
                }
                st = connection.createStatement();
                ResultSet rs = st.executeQuery("select child_id from listing_occurrence where project_id ="+projectId+" AND field_name = '"+fieldName+"'AND marking ='Yes' ");
                    while(rs.next()){
                        int child_id = rs.getInt(1);
                        System.out.println("child_id=======>"+ child_id);
                       pstmt2 = connection.prepareStatement("UPDATE child SET marking =? WHERE child_id =? ");
                       pstmt2.setString(1, "Yes");
                       pstmt2.setInt(2, child_id);                       
                       pstmt2.executeUpdate();System.out.println("==========>");                       
                    }
                   pstmt.close();
                  
                   pstmt2 = connection.prepareStatement("UPDATE projectfields SET listing_marking =? WHERE project_id =? AND field_name = ? ");
                   pstmt2.setString(1, "Yes");
                   pstmt2.setInt(2, projectId);
                   pstmt2.setString(3, fieldName);
                   pstmt2.executeUpdate();
            } catch (SQLException ex) {
            Logger.getLogger(Command_save_listing_marking_list.class.getName()).log(Level.SEVERE, null, ex);
          }           
        }
                
        return null;
    }

    public boolean isReadOnly() {
        return true;
    }

}
