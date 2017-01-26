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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.w3c.dom.Element;

/**
 * This class returns image path for the given volume
 * @author bmurali
 */
public class Command_edit_project implements Command
{

   /**
    * Returns the image path for given volume
    * @param action    XML Element object
    * @param user      UserTask object taken from the user's session object
    * @param dbTask    DBTask object taken from the user's session object
    * @param writer    MessageWriter object to write messages back to the client.
    * @return  String  A null String
    */
   public String execute(Element action, UserTask user, DBTask dbTask, MessageWriter writer)
   {
      try {
         Statement getImagePathStatement = null;
         ResultSet getImagePathResultSet = null;
         String imagepath = "";
         int projectId = Integer.parseInt(action.getAttribute(A_PROJECT_ID));

         getImagePathStatement = dbTask.getStatement();
         getImagePathResultSet = getImagePathStatement.executeQuery("select image_path from volume " +
                                                                        "where volume_id =" + projectId);
         while (getImagePathResultSet.next()) {
            imagepath = getImagePathResultSet.getString(1);
         }
         String userSessionId = user.getFossaSessionId();
         //Start writing the XML.
         writer.startElement(T_CODING_MANUAL_PATH);
         writer.writeAttribute(A_FOSSAID, userSessionId);
         writer.writeAttribute(A_PATH, imagepath);
         writer.endElement();
         getImagePathResultSet.close();
      } catch (IOException ex) {
         CommonLogger.printExceptions(this, "IOException while fetching the image path for the selected volume.", ex);
      } catch (SQLException ex) {
         CommonLogger.printExceptions(this, "SQLException while fetching the image path for the selected volume.", ex);
      }
      return null;
   }

   public boolean isReadOnly()
   {
      return true;
   }

}
