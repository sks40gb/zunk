/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fossa.servlet.command;

import com.fossa.servlet.common.CommonLogger;
import com.fossa.servlet.common.msg.MessageWriter;
import com.fossa.servlet.dao.DBTask;
import com.fossa.servlet.exception.ServerFailException;
import com.fossa.servlet.session.UserTask;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import org.w3c.dom.Element;

/**
 * This class is used to add new project
 * It performs the action - add_new_project
 * @author anurag
 */
public class CommandAddNewProject implements Command
{
   private Connection con = null;
   private Statement getProjectIdStatement = null;
   private ResultSet getProjectIdResultSet = null;
           
   public String execute(Element action, UserTask user, DBTask dbTask, MessageWriter writer)
   {
      try {
         con = dbTask.getConnection();
         String projectName = action.getAttribute(A_PROJECT_NAME);
         
         //Verify whether the project already exists
         getProjectIdStatement = con.createStatement();
         getProjectIdResultSet = getProjectIdStatement.executeQuery("select project_id from project " +
                                                                     "where project_name = '" + projectName + "'");
         if(getProjectIdResultSet.next()){
            //Prompt the user
            throw new ServerFailException("Project Name already exists.\n Please try some different name.");
         }else{
            //add the project            
            PreparedStatement insertProjectPrepStmt = user.prepareStatement(dbTask, "insert into project(project_name) values(?)");
            insertProjectPrepStmt.setString(1, projectName);
            insertProjectPrepStmt.executeUpdate();
         }         
      }catch(ServerFailException fail){
         try {
            writer.startElement(T_FAIL);
            String message = fail.getMessage();
            writer.writeContent(message == null ? fail.toString() : message);
            writer.endElement();
         } catch (IOException ex) {
             CommonLogger.printExceptions(this, "Exception caught during writing XML.", ex);
         }
      }
      catch (Exception ex) {
         CommonLogger.printExceptions(this, "Exception caught during adding new projects.", ex);
      }
      return null;
   }

   public boolean isReadOnly()
   {
      return true;
   }
}
