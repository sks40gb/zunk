/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.fossa.servlet.command;

import com.fossa.servlet.common.CommonLogger;
import com.fossa.servlet.common.QueryData;
import com.fossa.servlet.common.msg.MessageWriter;
import com.fossa.servlet.common.msg.XmlReader;
import com.fossa.servlet.dao.DBTask;
import com.fossa.servlet.session.UserTask;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Date;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * This class handles in raising new queries,editing or deleting the queries
 * @author bmurali
 */
public class Command_query_data implements Command {
      
      private PreparedStatement insQueryTrckrPrepStmt;
      private PreparedStatement getQueryTrckrIdPrepStmt;
      private PreparedStatement updateProjFieldsPrepStmt;
      private PreparedStatement updatePagePrepStmt;
      private Connection con;
      private Statement st;
      
    public String execute(Element action, UserTask user, DBTask dbTask, MessageWriter writer) {
        
        int volume_id=0;        
        String image_path ="";
        int child_id =0;
        String volumeName ="";
        Element givenValueList = action;      
        Node firstChild = givenValueList.getFirstChild();
                while (firstChild != null && firstChild.getNodeType() != Node.ELEMENT_NODE) { 
                    firstChild = firstChild.getNextSibling();
                }
        
         if (firstChild != null) {
                    QueryData data = new QueryData();
                    // fill in the int and String fields of the UsersData
                    XmlReader reader=new XmlReader();
            try {                
                reader.decode(givenValueList, data);
            } catch (IOException ex) {
               CommonLogger.printExceptions(this, "IOException while reading the XMLReader.", ex);
            }
                    
           if (data != null) {
                try {
                    // update or insert the users row contained in data
                   con = dbTask.getConnection();
                   st = dbTask.getStatement();
                   Date date = new Date();
                   long time = date.getTime();
                   Timestamp timestamp = new Timestamp(time);
                   ResultSet getVolumeResultSet = st.executeQuery("select volume_id ,volume_name from volume " +
                                                                  "where project_id ="+data.project_id);
                   if(getVolumeResultSet.next()){
                    volume_id = getVolumeResultSet.getInt(1);
                    volumeName = getVolumeResultSet.getString(2);
                   } 
                   getVolumeResultSet.close();
                   getQueryTrckrIdPrepStmt = con.prepareStatement("select query_tracker_id from query_tracker " +
                                                                  "where project_id = ? and field_name =? and child_id = ?");
                   
                   getQueryTrckrIdPrepStmt.setInt(1, data.project_id);
                   getQueryTrckrIdPrepStmt.setString(2, data.field_name);
                   getQueryTrckrIdPrepStmt.setInt(3, data.childId);
                   getQueryTrckrIdPrepStmt.executeQuery();
                   ResultSet getQueryTrckrIdResultSet = getQueryTrckrIdPrepStmt.getResultSet();
                   if("Yes".equals(data.uploadImage)){
                        image_path= data.imagePath;
                    }else{
                      image_path= " ";
                    }
                   if(getQueryTrckrIdResultSet.next()){
                    int query_tracker_id = getQueryTrckrIdResultSet.getInt(1);
                    
                    insQueryTrckrPrepStmt = con.prepareStatement("update  query_tracker set project_id = ?, volume_id = ?," 
                            + " batch_id = ?, document_name = ?," 
                            + " field_name = ?, level = ?, collection = ?, dtyg = ?, dtys = ?," 
                            + " dtyg_spi = ?, dtys_spi = ?, description = ?,general_query = ?, specific_query = ?," 
                            + " answer = ?, posted_date = ?, answered_date = null, raised_to = ? ,imagePath = ?" 
                            + ",child_id = ?,posted_by = ?  where query_tracker_id = ?");
                    insQueryTrckrPrepStmt.setInt(1, data.project_id);
                    insQueryTrckrPrepStmt.setInt(2, volume_id);
                    insQueryTrckrPrepStmt.setInt(3, data.batch_id);
                    insQueryTrckrPrepStmt.setString(4, data.document_name);
                    insQueryTrckrPrepStmt.setString(5, data.field_name);
                    insQueryTrckrPrepStmt.setString(6, data.level);
                    insQueryTrckrPrepStmt.setString(7, data.collection);
                    insQueryTrckrPrepStmt.setString(8, data.dtyg);
                    insQueryTrckrPrepStmt.setString(9, data.dtys);
                    insQueryTrckrPrepStmt.setString(10, data.dtyg_spi);
                    insQueryTrckrPrepStmt.setString(11, data.dtys_spi);
                    insQueryTrckrPrepStmt.setString(12, data.description);
                    insQueryTrckrPrepStmt.setInt(13, data.generalQuestion);
                    insQueryTrckrPrepStmt.setString(14, data.specificQuestion);
                    insQueryTrckrPrepStmt.setString(15, data.answer);
                    insQueryTrckrPrepStmt.setTimestamp(16, timestamp);                   
                    insQueryTrckrPrepStmt.setInt(17, data.raised_to);
                    insQueryTrckrPrepStmt.setString(18, image_path);
                    insQueryTrckrPrepStmt.setInt(19, data.childId);
                    insQueryTrckrPrepStmt.setInt(20, user.getUsersId());
                    insQueryTrckrPrepStmt.setInt(21, query_tracker_id);
                    insQueryTrckrPrepStmt.executeUpdate();
                    insQueryTrckrPrepStmt.close();
                    
                    //insert into query tracker history
                    insQueryTrckrPrepStmt = con.prepareStatement("insert into history_query_tracker (project_id,volume_id,batch_id,document_name,field_name,"
                                          +" level,collection,dtyg,dtys,dtyg_spi,dtys_spi,description,general_query,specific_query,"
                                          +" answer,posted_date, answered_date,raised_to,imagePath,child_id,posted_by,query_tracker_id"
                                          + ",query_raised_time,query_answered_time,status, )"
                                          + "values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,null,?,?,?,?,?,?,null,?");
                    insQueryTrckrPrepStmt.setInt(1, data.project_id);
                    insQueryTrckrPrepStmt.setInt(2, volume_id);
                    insQueryTrckrPrepStmt.setInt(3, data.batch_id);
                    insQueryTrckrPrepStmt.setString(4, data.document_name);
                    insQueryTrckrPrepStmt.setString(5, data.field_name);
                    insQueryTrckrPrepStmt.setString(6, data.level);
                    insQueryTrckrPrepStmt.setString(7, data.collection);
                    insQueryTrckrPrepStmt.setString(8, data.dtyg);
                    insQueryTrckrPrepStmt.setString(9, data.dtys);
                    insQueryTrckrPrepStmt.setString(10, data.dtyg_spi);
                    insQueryTrckrPrepStmt.setString(11, data.dtys_spi);
                    insQueryTrckrPrepStmt.setString(12, data.description);
                    insQueryTrckrPrepStmt.setInt(13, data.generalQuestion);
                    insQueryTrckrPrepStmt.setString(14, data.specificQuestion);
                    insQueryTrckrPrepStmt.setString(15, data.answer);
                    insQueryTrckrPrepStmt.setTimestamp(16, timestamp);                   
                    insQueryTrckrPrepStmt.setInt(17, data.raised_to);
                    insQueryTrckrPrepStmt.setString(18, image_path);
                    insQueryTrckrPrepStmt.setInt(19, data.childId);
                    insQueryTrckrPrepStmt.setInt(20, user.getUsersId());
                    insQueryTrckrPrepStmt.setInt(21, query_tracker_id);
                    insQueryTrckrPrepStmt.setTimestamp(22, timestamp);
                    insQueryTrckrPrepStmt.setString(23, "Edited");                   
                    insQueryTrckrPrepStmt.executeUpdate();
                    insQueryTrckrPrepStmt.close();
                    
                   }else{ 
                    insQueryTrckrPrepStmt = con.prepareStatement("insert into query_tracker (project_id,volume_id,batch_id,document_name,field_name,"
                                          +" level,collection,dtyg,dtys,dtyg_spi,dtys_spi,description,general_query,specific_query,"
                                          +" answer,posted_date, answered_date,raised_to,imagePath,child_id,posted_by)"
                                          + "values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,null,?,?,?,?)");  
                      
                    insQueryTrckrPrepStmt.setInt(1, data.project_id);
                    insQueryTrckrPrepStmt.setInt(2, volume_id);
                    insQueryTrckrPrepStmt.setInt(3, data.batch_id);
                    insQueryTrckrPrepStmt.setString(4, data.document_name);
                    insQueryTrckrPrepStmt.setString(5, data.field_name);
                    insQueryTrckrPrepStmt.setString(6, data.level);
                    insQueryTrckrPrepStmt.setString(7, data.collection);
                    insQueryTrckrPrepStmt.setString(8, data.dtyg);
                    insQueryTrckrPrepStmt.setString(9, data.dtys);
                    insQueryTrckrPrepStmt.setString(10, data.dtyg_spi);
                    insQueryTrckrPrepStmt.setString(11, data.dtys_spi);
                    insQueryTrckrPrepStmt.setString(12, data.description);
                    insQueryTrckrPrepStmt.setInt(13, data.generalQuestion);
                    insQueryTrckrPrepStmt.setString(14, data.specificQuestion);
                    insQueryTrckrPrepStmt.setString(15, data.answer);
                    insQueryTrckrPrepStmt.setTimestamp(16, timestamp);                    
                    insQueryTrckrPrepStmt.setInt(17, data.raised_to);
                    insQueryTrckrPrepStmt.setString(18, image_path);
                    insQueryTrckrPrepStmt.setInt(19, data.childId);
                    insQueryTrckrPrepStmt.setInt(20, user.getUsersId());
                    insQueryTrckrPrepStmt.executeUpdate();
                    insQueryTrckrPrepStmt.close();
                     
                    int query_tracker_id = 0;
                    ResultSet getTopQueryTrckrIdResultSet = st.executeQuery("select top 1 query_tracker_id from query_tracker order by query_tracker_id desc");
                    if(getTopQueryTrckrIdResultSet.next()){
                      query_tracker_id = getTopQueryTrckrIdResultSet.getInt(1);
                    }              
                    //insert into query tracker history
                    PreparedStatement insertHistorytable = con.prepareStatement("insert into history_query_tracker (project_id,volume_id,batch_id,document_name,field_name,"
                                          +" level,collection,dtyg,dtys,dtyg_spi,dtys_spi,description,general_query,specific_query,"
                                          +" answer,posted_date, answered_date,raised_to,imagePath,child_id,posted_by,query_tracker_id,query_raised_time,query_answered_time,status)"
                                          + "values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,null,?,?,?,?,?,?,null,?)");
                    
                    insertHistorytable.setInt(1, data.project_id);
                    insertHistorytable.setInt(2, volume_id);
                    insertHistorytable.setInt(3, data.batch_id);
                    insertHistorytable.setString(4, data.document_name);
                    insertHistorytable.setString(5, data.field_name);
                    insertHistorytable.setString(6, data.level);
                    insertHistorytable.setString(7, data.collection);
                    insertHistorytable.setString(8, data.dtyg);
                    insertHistorytable.setString(9, data.dtys);
                    insertHistorytable.setString(10, data.dtyg_spi);
                    insertHistorytable.setString(11, data.dtys_spi);
                    insertHistorytable.setString(12, data.description);
                    insertHistorytable.setInt(13, data.generalQuestion);
                    insertHistorytable.setString(14, data.specificQuestion);
                    insertHistorytable.setString(15, data.answer);                     
                    insertHistorytable.setTimestamp(16, timestamp);                    
                    insertHistorytable.setInt(17, data.raised_to);
                    insertHistorytable.setString(18, image_path);
                    insertHistorytable.setInt(19, data.childId);
                    insertHistorytable.setInt(20, user.getUsersId());
                    insertHistorytable.setInt(21, query_tracker_id);
                    insertHistorytable.setTimestamp(22, timestamp);                    
                    insertHistorytable.setString(23, "New Query");
                    insertHistorytable.executeUpdate();
                    insertHistorytable.close();
                   
                    ResultSet getChildIdResultSet = st.executeQuery("select child_id from page where volume_id ="+volume_id+" and bates_number ='"+ data.batesNumber+"'");
                    
                    if(getChildIdResultSet.next()){
                     child_id = getChildIdResultSet.getInt(1);
                    } 
                    
                    //Update the query raised as 'Yes' or 'No' for the corresponding project field
                    updateProjFieldsPrepStmt = user.prepareStatement(dbTask, "update projectfields set queryraised  = ? where field_name = ? and project_id = ?");
                    updateProjFieldsPrepStmt.setString(1, "Yes");
                    updateProjFieldsPrepStmt.setString(2, data.field_name);
                    updateProjFieldsPrepStmt.setInt(3, data.project_id);
                    updateProjFieldsPrepStmt.executeUpdate();
                    updateProjFieldsPrepStmt.close();
                    
                    //Update the query raised as 'Yes' or 'No' for the corresponding page
                    updatePagePrepStmt =con.prepareStatement("update page set query_raised = ? where volume_id = ? and child_id = ?");
                    updatePagePrepStmt.setString(1, "Yes");
                    updatePagePrepStmt.setInt(2, volume_id);
                    updatePagePrepStmt.setInt(3, child_id);                    
                    updatePagePrepStmt.executeUpdate();
                    updatePagePrepStmt.close();
                   }
                } catch (SQLException ex) {
                    CommonLogger.printExceptions(this, "SQLException while raising a new query" , ex);
                }
           }
    }
         return null;
    }
    public boolean isReadOnly() {
        return true;
    }

}
