/*
 * Command_edit_codingManual.java
 *
 * Created on January 26, 2008, 3:54 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package com.fossa.servlet.command;

import com.fossa.servlet.common.CommonLogger;
import com.fossa.servlet.common.Log;
import com.fossa.servlet.common.SQLQueries;
import com.fossa.servlet.common.msg.MessageWriter;
import com.fossa.servlet.dao.DBTask;
import com.fossa.servlet.session.UserTask;
import java.sql.ResultSet;
import java.sql.Statement;
import org.w3c.dom.Element;

/**
 * This class handles the editing of coding manual.
 * @author murali
 */
public class Command_edit_codingManual implements Command
{

   public String execute(Element action, UserTask user, DBTask dbTask, MessageWriter writer)
   {
      int projectId = Integer.parseInt(action.getAttribute(A_PROJECT_ID));
      Log.print("Command_edit_codingManual projectId=" + projectId);
      Statement getProjectStatement = null;
      Statement getCodingManualStatement = null;
      ResultSet getProjectResultSet = null;
      ResultSet getCodingManualResultSet = null;
      String projectName = "";
      String fieldName = "";
      String fieldPath = "";
      try {
          //Get ProjectId and ProjectName
         getProjectStatement = dbTask.getStatement();
         getProjectResultSet = getProjectStatement.executeQuery(SQLQueries.SEL_CM_PROJID + projectId);
         while (getProjectResultSet.next()) {
            projectName = getProjectResultSet.getString(2);
            System.out.println("projectName" + projectName);
         }
         
         //Get Coding Manual id,filename and filepath
         getCodingManualStatement = dbTask.getStatement();
         getCodingManualResultSet = getCodingManualStatement.executeQuery(SQLQueries.SEL_CM + projectName + "'");
         while (getCodingManualResultSet.next()) {
            fieldName = getCodingManualResultSet.getString(2);
            fieldPath = getCodingManualResultSet.getString(3);
            System.out.println("fieldName" + fieldName);
            System.out.println("fieldPath" + fieldPath);
         }
         String userSessionId = user.getFossaSessionId();
         writer.startElement(T_CODING_MANUAL_PATH);
         writer.writeAttribute(A_FOSSAID, userSessionId);
         writer.writeAttribute(A_PATH, fieldPath);
         writer.endElement();
         getProjectResultSet.close();
         getCodingManualResultSet.close();
      } catch (Exception e) {
         CommonLogger.printExceptions(this, "Exception while editing the coding manual.", e);
      }
      return null;
   }

   public boolean isReadOnly()
   {
      return true;
   }

}
