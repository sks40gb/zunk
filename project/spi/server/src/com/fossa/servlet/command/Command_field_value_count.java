/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.fossa.servlet.command;

import com.fossa.servlet.common.BateNumbers;
import com.fossa.servlet.common.CommonLogger;
import com.fossa.servlet.common.msg.MessageWriter;
import com.fossa.servlet.dao.DBTask;
import com.fossa.servlet.session.UserTask;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.w3c.dom.Element;

/**
 * This class calculates the number of documents,fields and tags for a project & volume
 * @author bmurali
 */
public class Command_field_value_count implements Command {
    private Connection connection;    
    private PreparedStatement getUserIdPrepStmt = null;
    private PreparedStatement getFieldPrepStmt = null;
    private PreparedStatement getCountsPrepStmt = null;
    private PreparedStatement getBatchIdPrepStmt = null;
    private PreparedStatement getChildIdPrepStmt = null;
    private PreparedStatement getUserNamePrepStmt = null;
    private ResultSet getUserIdResultSet = null;
    private ResultSet getBatchIdResultSet = null;
    private ResultSet getChildIdResultSet = null;
    private ResultSet getFieldResultSet = null;
    private ResultSet getCountResultSet = null;
    private ResultSet getUserNameResultSet = null;    
    private int projectId = 0;
    private int volumeId = 0;
    private int userId = 0;
    private int batchId = 0;
    private int childId;
    private String fieldName;
    private String userName;    
    private ArrayList docList = new ArrayList();
    private ArrayList fieldList = new ArrayList();    
    private HashMap batesMap = new HashMap();  //Holds bates with corresponding user
            
    public String execute(Element action, UserTask user, DBTask dbTask, MessageWriter writer) {
        try {
            connection = dbTask.getConnection();            
            projectId = Integer.parseInt(action.getAttribute(A_PROJECT_ID));
            volumeId = Integer.parseInt(action.getAttribute(A_VOLUME_ID));  
            
            //Get Coders userid
            getUserIdPrepStmt = connection.prepareStatement("SELECT DISTINCT user_id FROM codinghistory " +
                                                            "WHERE volume_id = ? AND project_id = ?");
            getUserIdPrepStmt.setInt(1, volumeId);
            getUserIdPrepStmt.setInt(2, projectId);
            getUserIdPrepStmt.executeQuery();
            getUserIdResultSet = getUserIdPrepStmt.getResultSet();
            while (getUserIdResultSet.next()) {
                userId = getUserIdResultSet.getInt(1); 
                getUserNamePrepStmt = connection.prepareStatement("SELECT user_name from users where users_id = ?");
                getUserNamePrepStmt.setInt(1, userId);
                getUserNamePrepStmt.executeQuery();
                getUserNameResultSet = getUserNamePrepStmt.getResultSet();
                while (getUserNameResultSet.next()) {
                    userName = getUserNameResultSet.getString(1);
                }              
                getBatchIdPrepStmt = connection.prepareStatement("select DISTINCT batch_id from codinghistory where user_id = ?");
                getBatchIdPrepStmt.setInt(1, userId);                
                getBatchIdPrepStmt.executeQuery();
                getBatchIdResultSet = getBatchIdPrepStmt.getResultSet(); 
               while (getBatchIdResultSet.next()) {
                    batchId = getBatchIdResultSet.getInt(1);                                           
                    getChildIdPrepStmt = connection.prepareStatement("select DISTINCT child_id from codinghistory where batch_id = ?");
                    getChildIdPrepStmt.setInt(1, batchId);                
                    getChildIdPrepStmt.executeQuery();
                    getChildIdResultSet = getChildIdPrepStmt.getResultSet();  
                    while (getChildIdResultSet.next()) {                        
                        childId = getChildIdResultSet.getInt(1);   
                        docList.add(childId);                        
                     }                     
                 } 
                
                //Get FieldNames
                getFieldPrepStmt = connection.prepareStatement("select  DISTINCT field_name from codinghistory " +
                                                                 "where user_id = ? AND project_id = ?");
                getFieldPrepStmt.setInt(1, userId);
                getFieldPrepStmt.setInt(2, projectId);
                getFieldPrepStmt.executeQuery();
                getFieldResultSet = getFieldPrepStmt.getResultSet();
                int words =0;
                int characters =0;
                int tags =0;
               while (getFieldResultSet.next()) {
                     fieldName= getFieldResultSet.getString(1);
                     fieldList.add(fieldName);  
                     
                     //Get Word,character and Tag count for given Project FieldName
                     getCountsPrepStmt = connection.prepareStatement("select sum(words),sum(characters),sum(tags) " +
                                                                     "from codinghistory " +
                                                                     "where project_id = ? and field_name = ?");
                     getCountsPrepStmt.setInt(1, projectId);
                     getCountsPrepStmt.setString(2, fieldName);
                     getCountsPrepStmt.executeQuery();
                     getCountResultSet = getCountsPrepStmt.getResultSet();
                       
                     if (getCountResultSet.next()) {                           
                         int value1 = getCountResultSet.getInt(1);
                         words = words+value1;
                                
                         int value2   = getCountResultSet.getInt(2);
                         characters = characters+value2;
                         
                         int value3  = getCountResultSet.getInt(3);
                         tags= tags+value3;
                         
                     }
                 }                
                BateNumbers bates = createBates(docList,fieldList, words,characters,tags);
                batesMap.put(userName, bates);
             } 
            writeXmlFromResult(user, batesMap, writer, false);
        } catch (IOException ex) {
            CommonLogger.printExceptions(this, "IOException while getting the words,character,tags count." , ex);
        } catch (SQLException ex) {
            CommonLogger.printExceptions(this, "SQLException while getting the words,character,tags count." , ex);
        }
            return null;
    }

    public boolean isReadOnly() {
        return true;
    }
    
    /**
     * Method to Create BatesNumber
     * 
     * @param docList //Holds list of Documents
     * @param fieldList //holds list of Fields
     * @param first     //word sequence 1
     * @param second    //word sequence 2
     * @param third     //word sequence 3
     * @return
     */
    private BateNumbers createBates(List docList, List fieldList,int first,int second,int third){
        BateNumbers bates = null;
        bates = new BateNumbers();
        int document =  docList.size();
                                    
        bates.setDocuments(document);                                     
        int fieldCount =  fieldList.size();
                      
        bates.setFields(fieldCount);
        bates.setWords(first); 
        bates.setCharacters(second); 
        bates.setTags(third);     
        return bates;
    }
     
     /**
      * Method to Write output XML Data
      *  
      * @param user //UserTask
      * @param map  //Holds bates with corresponding user
      * @param writer //MessageWriter
      * @param b     //Boolean
      * @throws java.sql.SQLException
      * @throws java.io.IOException
      */
     private void writeXmlFromResult(UserTask user, HashMap map, MessageWriter writer, boolean b)throws SQLException, IOException {
        
        String userSessionId = user.getFossaSessionId();
        int columnCount = 5;
        writer.startElement(T_RESULT_SET);
        writer.writeAttribute(A_FOSSAID, userSessionId);
        writer.writeAttribute(A_COUNT, Integer.toString(columnCount));
        writer.startElement(T_HEADING);

        writer.startElement(T_COLUMN);
        writer.writeContent("Coder");
        writer.endElement();

        writer.startElement(T_COLUMN);
        writer.writeContent("Documents");
        writer.endElement();

        writer.startElement(T_COLUMN);
        writer.writeContent("Fields");
        writer.endElement();

        writer.startElement(T_COLUMN);
        writer.writeContent("Words");
        writer.endElement();

        writer.startElement(T_COLUMN);
        writer.writeContent("Characters");
        writer.endElement();

        writer.endElement();   
        
        if(null != map) {              
              Set keys = map.keySet();
               for(Iterator iterator = keys.iterator(); iterator.hasNext();) {
                    writer.startElement(T_ROW);
                    String key = (String)iterator.next();   
                    BateNumbers bateNumber = (BateNumbers)map.get(key);                                           
                        writer.startElement(T_COLUMN);               
                        writer.writeContent(key); //Coder
                        writer.endElement();
                        writer.startElement(T_COLUMN);               
                        writer.writeContent(bateNumber.getDocuments()); //Documents
                        writer.endElement();
                        writer.startElement(T_COLUMN);                        
                        writer.writeContent(bateNumber.getFields()); //Fields
                        writer.endElement();
                        writer.startElement(T_COLUMN);                       
                        writer.writeContent(bateNumber.getWords()); //Words
                        writer.endElement();
                        writer.startElement(T_COLUMN);                       
                        writer.writeContent(bateNumber.getCharacters()); //Characters
                        writer.endElement();                         
                writer.endElement(); //row
               }
              writer.endElement();//result set
         }        
     }      
}
