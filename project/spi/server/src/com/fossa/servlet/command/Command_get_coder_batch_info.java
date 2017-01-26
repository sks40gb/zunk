/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fossa.servlet.command;

import com.fossa.servlet.common.CommonLogger;
import com.fossa.servlet.common.msg.MessageWriter;
import com.fossa.servlet.dao.DBTask;
import com.fossa.servlet.session.UserTask;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.w3c.dom.Element;
import java.util.ArrayList;
import java.util.List;

/**
 * This class returns the list of coders per batch
 * @author anurag
 */
public class Command_get_coder_batch_info implements Command
{   
   private Connection con;
   private Statement getBatchIdStatement;
   private Statement getUserStatement;  
   private int volumeId = 0;
   private int batchId = 1;
   private ResultSet getCodersNameResultSet;
   private ResultSet getCheckersNameResultSet;
   private String codersName = "";
   private String checkersName = "";
   private String codersWithIdString = "";
   private List codersList;   //It contains coders,checkers & batches
   private List allCodersList; //It contains all the codersList

   public String execute(Element action, UserTask user, DBTask dbTask, MessageWriter writer)
   {
      try {
         con = dbTask.getConnection();
         getBatchIdStatement = dbTask.getStatement();
         getUserStatement = dbTask.getStatement();
         volumeId = Integer.parseInt(action.getAttribute(A_VOLUME_ID));
         allCodersList = new ArrayList();

         getBatchIdStatement = con.createStatement();

         //Fetch the rows from event table for selected volume id
         ResultSet rs = getBatchIdStatement.executeQuery("select distinct batch_id from event where volume_id = "+volumeId);
         
         while (rs.next()) {
            codersList = new ArrayList();          
            batchId = rs.getInt(1);
            
            getUserStatement = con.createStatement();
            ResultSet getUsersIdResultSet = getUserStatement.executeQuery("select distinct users_id from event " +
                                                                          "where status = 'Coding' and batch_id = " + batchId);
            
            if(getUsersIdResultSet.next()){
               int users_id = getUsersIdResultSet.getInt(1);
               getCodersNameResultSet = getUserStatement.executeQuery("select user_name from users where users_id = " + users_id);      
               if(getCodersNameResultSet.next())
                  codersName = getCodersNameResultSet.getString(1);
                  codersWithIdString = codersName + "," + users_id;
            }
                      
            getUserStatement.close();
            getUserStatement = con.createStatement();
           
            ResultSet getUserIdResultSet = getUserStatement.executeQuery("select distinct users_id from event " +
                                                                         "where status = 'CodingQC' and batch_id = " + batchId);
            if(getUserIdResultSet.next()){
             getCheckersNameResultSet = getUserStatement.executeQuery("select user_name from users " +
                                                                      "where users_id = " + getUserIdResultSet.getInt(1));            
             if(getCheckersNameResultSet.next())
               checkersName = getCheckersNameResultSet.getString(1);
            }
            getUserStatement.close();
           
            if(batchId >= 0 && !codersName.isEmpty()){   
               codersList.add(false);
               codersList.add(codersWithIdString);
               codersList.add(checkersName);
               codersList.add(batchId);
            }
            if(codersList != null && codersList.size() > 0){
               allCodersList.add(codersList);
            }            
         }
         getBatchIdStatement.close();
         writeXmlFromResult(user, writer, allCodersList);
      } catch (Exception exc) {
         CommonLogger.printExceptions(this, "Exception while getting the coders per batch." , exc);
         return null;
      }
      return null;
   }

   /**
    * Method to Write output XML
    * 
    * @param task                  //UserTask
    * @param writer                //MessageWriter
    * @param ls                    //Holds codersList
    * @throws java.sql.SQLException
    * @throws java.io.IOException
    */
   public void writeXmlFromResult(UserTask task, MessageWriter writer, List ls)
           throws SQLException, IOException
   {
      String userSessionId = task.getFossaSessionId();
      writer.startElement(T_RESULT_SET);
      writer.writeAttribute(A_FOSSAID, userSessionId);
      writer.writeAttribute(A_COUNT, 4);
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
