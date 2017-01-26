/*
 * Command_coding_manule.java
 *
 * Created on January 22, 2008, 4:02 PM
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
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import org.w3c.dom.Element;

/**
 * This class returns the coding manual path of the projects.
 * @author bmurali
 */
public class Command_coding_manual implements Command
{

   private Connection connection;
   private ResultSet getProjectResultSet = null;
   private ResultSet getCodingManualResultSet = null;
   private PreparedStatement getProjectPrepStmt = null;
   private PreparedStatement getCodingManualPrepStmt = null;
   private String projectName = null;
   private String filepath = "";    //Holds the coding manual file path


   public String execute(Element action, UserTask user, DBTask dbTask, MessageWriter writer)
   {
      try {
         connection = dbTask.getConnection();
         int projectId = Integer.parseInt(action.getAttribute(A_PROJECT_ID));
         
         //Get Project Details
         getProjectPrepStmt = connection.prepareStatement(SQLQueries.SEL_PROJ_ID);
         getProjectPrepStmt.setInt(1, projectId);
         getProjectPrepStmt.executeQuery();
         getProjectResultSet = getProjectPrepStmt.getResultSet();
         while (getProjectResultSet.next()) {
            projectName = getProjectResultSet.getString(2);
         }
         
         //Get Coding Manual FilePath
         getCodingManualPrepStmt = connection.prepareStatement(SQLQueries.SEL_COD_MANUAL);
         getCodingManualPrepStmt.setString(1, projectName);
         getCodingManualPrepStmt.executeQuery();
         getCodingManualResultSet = getCodingManualPrepStmt.getResultSet();
         while (getCodingManualResultSet.next()) {
            filepath = getCodingManualResultSet.getString(1);
         }
         String userSessionId = user.getFossaSessionId();

         writer.startElement(T_CODING_MANUAL_PATH);
         writer.writeAttribute(A_FOSSAID, userSessionId);
         writer.writeAttribute(A_PATH, filepath);
         writer.endElement();
         getProjectResultSet.close();
         getCodingManualResultSet.close();
      } catch (Exception ex) {
         CommonLogger.printExceptions(this, "Exception while getting the coding manual path.", ex);
      }
      return null;
   }

   public boolean isReadOnly()
   {
      return false;
   }

}
