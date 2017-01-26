/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fossa.servlet.command;

import com.fossa.servlet.common.CommonLogger;
import com.fossa.servlet.common.msg.MessageWriter;
import com.fossa.servlet.dao.DBTask;
import com.fossa.servlet.session.UserTask;
import com.fossa.servlet.util.DataConversion;
import java.sql.Statement;
import java.io.IOException;
import org.w3c.dom.Element;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * This class handles the display of Task Summary.
 * @author anu
 */
public class Command_display_summary_list implements Command
{   
   private Connection con;
   private Statement getEventParamsStatement;
   private Statement st;
   private int userId = 0;
   private int projectId = 0;
   private int batchNumber = 0;
   private int eventBreakId = 0;
   private String volumeName = null;
   private String projectName = null;
   private String batch = null;
   private List userTaskList;	      //Holds tasks of each user
   private List allUserTaskList;      //Holds all userTaskList

   public String execute(Element action, UserTask user, DBTask dbTask, MessageWriter writer)
   {
      try {
            con = dbTask.getConnection();
            getEventParamsStatement = dbTask.getStatement();
            st = dbTask.getStatement();
            userId = Integer.parseInt(action.getAttribute(A_USERS_ID));
            allUserTaskList = new ArrayList();
            getEventParamsStatement = con.createStatement();
            ResultSet getEventParamsResultSet = getEventParamsStatement.executeQuery("select status, open_timestamp as ot," +
                                                                                      " close_timestamp as ct ,volume_id," +
                                                                                      "batch_id from event " +
                                                                                      "where  substring" +"(convert " +
                                                                                      "(varchar(16),open_timestamp,120),1,10)" +
                                                                                      " = SUBSTRING(CONVERT(VARCHAR(16), " +
                                                                                      "GETDATE(), 120), 1, 10)  and " +
                                                                                      "users_id = "+userId+"  union   " +
                                                                                      "(select status,open_timestamp as ot" +
                                                                                      ",close_timestamp as ct,null,null" +
                                                                                      " from event_break where " +
                                                                                      "substring(convert (varchar(16)," +
                                                                                      "open_timestamp,120),1,10) =  " +
                                                                                      "SUBSTRING(CONVERT(VARCHAR(16), " +
                                                                                      "GETDATE(), 120), 1, 10)  and " +
                                                                                      "user_id = "+userId+" )  order by ot");
            getEventParamsStatement = con.createStatement();
            while (getEventParamsResultSet.next()) {
               userTaskList = new ArrayList();
               int timeZoneDiff = (new java.util.Date().getTimezoneOffset() * 60 * 1000);            
               //long dur = rs.getLong(3) - rs.getLong(2) + d;
               String closeTimeStamp = getEventParamsResultSet.getString(3);
               long taskduration  = 0l;
               if(closeTimeStamp != null){
                   taskduration  = DataConversion.getDateDifference(getEventParamsResultSet.getString(3), getEventParamsResultSet.getString(2));
               }
               if (getEventParamsResultSet.getInt(4) != 0) {
                  eventBreakId = 0;
                  st = con.createStatement();
                  ResultSet getVolumeResultSet = st.executeQuery("select volume_name,project_id from volume " +
                                                                  "where volume_id = " + getEventParamsResultSet.getInt(4));
                  getVolumeResultSet.next();
                  volumeName = getVolumeResultSet.getString(1);
                  projectId = getVolumeResultSet.getInt(2);
                  st.close();
                  if (getEventParamsResultSet.getInt(5) != 0) {
                     st = con.createStatement();
                     ResultSet getBatchNumberResultSet = st.executeQuery("select batch_number from batch " +
                                                                        "where batch_id = " + getEventParamsResultSet.getInt(5));
                     getBatchNumberResultSet.next();
                     batchNumber = getBatchNumberResultSet.getInt(1);
                     batch = Integer.toString(batchNumber);
                     st.close();
                  }
                  else {
                     batch = "";
                  }
                  st = con.createStatement();
                  ResultSet getProjectResultSet = st.executeQuery("select project_name from project where project_id = " + projectId);
                  getProjectResultSet.next();
                  projectName = getProjectResultSet.getString(1);
                  st.close();
               }
               else {
                  if (getEventParamsResultSet.getString(1).equalsIgnoreCase("Others")) {
                     st = con.createStatement();
                     ResultSet getEventBreakIDResultSet = st.executeQuery("select event_break_id from event_break " +
                                                                           "where open_timestamp ='" + 
                                                                           getEventParamsResultSet.getString(2)+"' " +
                                                                           "and close_timestamp ='" + 
                                                                           getEventParamsResultSet.getString(3)+"'");
                     if(getEventBreakIDResultSet.next()){
                        eventBreakId = getEventBreakIDResultSet.getInt(1);
                     }
                     st.close();
                  }
                  volumeName = "";
                  projectName = "";
                  batch = "";
               }
               userTaskList.add(getEventParamsResultSet.getString(1));
               userTaskList.add(new java.sql.Time(taskduration+timeZoneDiff).toString());
               userTaskList.add(new java.sql.Time(DataConversion.getDateInLongFormat(getEventParamsResultSet.getString(2))).toString());
               if(closeTimeStamp!= null){
                   userTaskList.add(new java.sql.Time(DataConversion.getDateInLongFormat(getEventParamsResultSet.getString(3))).toString());
               }else{
                   userTaskList.add("");
               }
               userTaskList.add(projectName);
               userTaskList.add(volumeName);
               userTaskList.add(batch);
               userTaskList.add(eventBreakId);

               allUserTaskList.add(userTaskList);
            }
            getEventParamsStatement.close();
            writeXmlFromResult(user, writer, allUserTaskList);
      } catch (Exception exc) {
            CommonLogger.printExceptions(this, "Exception while displaying the task summary list." , exc);
            return null;
      }
      return null;
   }

   //Reply sent back to the client.
   public void writeXmlFromResult(UserTask task, MessageWriter writer, List ls)
           throws SQLException, IOException
   {
      String userSessionId = task.getFossaSessionId();
      writer.startElement(T_RESULT_SET);
      writer.writeAttribute(A_FOSSAID, userSessionId);
      writer.writeAttribute(A_COUNT, 8);
      for (int i = 0; i < ls.size(); i++) {
         writer.startElement(T_ROW);
         List innerList = (List) ls.get(i);
         for (int j = 0; j < innerList.size(); j++) {
            writer.startElement(T_COLUMN);
            writer.writeContent(innerList.get(j).toString());
            writer.endElement();
         }
         writer.endElement();
      }
      writer.endElement();
   }

   public boolean isReadOnly()
   {
      return true;
   }

}
