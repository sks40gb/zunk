/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fossa.servlet.command;

import com.fossa.servlet.common.CommonLogger;
import com.fossa.servlet.common.DelimiterData;
import com.fossa.servlet.common.Log;
import com.fossa.servlet.common.SQLQueries;
import com.fossa.servlet.common.msg.MessageWriter;
import com.fossa.servlet.common.msg.XmlReader;
import com.fossa.servlet.dao.DBTask;
import com.fossa.servlet.session.UserTask;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * This class handles the delimiter data for a project
 * @author ashish
 */
class Command_delimiter_data implements Command
{

   public String execute(Element action, UserTask task, DBTask dbTask, MessageWriter writer)
   {
      try {
         Element givenValueList = action;
         //Note.  "child" may be ignored white space, if not validating parser
         Node firstChild = givenValueList.getFirstChild();
         while (firstChild != null && firstChild.getNodeType() != Node.ELEMENT_NODE) {
            firstChild = firstChild.getNextSibling();
         }
         if (firstChild != null) {
            DelimiterData data = new DelimiterData();
            // fill in the int and String fields of DelimiterData
            XmlReader xmlReader = new XmlReader();
            xmlReader.decode(givenValueList, data, /* trim? */ false);

            if (data != null) {
               // update or insert the export row contained in data
                    //Log.print("(Handler_delimiter_data.run) project is " + data.delimiter_set_name);
               saveDelimiterData(task, dbTask, writer, data);
            }
         }
      } catch (Throwable t) {
         logger.error("Exception while decoding the XMLReader." + t);
         StringWriter sw = new StringWriter();
         t.printStackTrace(new PrintWriter(sw));
         logger.error(sw.toString());
         Log.quit(t);
      }
      return null;
   }

   private void saveDelimiterData(UserTask task, DBTask dbTask, MessageWriter writer, DelimiterData data)
           throws java.io.IOException
   {
      Connection con = null;
      PreparedStatement doExportOperationsPrepStmt = null;
      ResultSet rs = null;
      try {
         con = dbTask.getConnection();
         doExportOperationsPrepStmt = null;
         if (data.force.equals("")) {
            // delete export
            doExportOperationsPrepStmt = con.prepareStatement(SQLQueries.DEL_EXPORT);
            doExportOperationsPrepStmt.setString(1, data.delimiter_set_name);
            doExportOperationsPrepStmt.executeUpdate();
         }
         else {
            // see if the export_name exists
            doExportOperationsPrepStmt = con.prepareStatement(SQLQueries.SEL_EXPORT_NAME);
            doExportOperationsPrepStmt.setString(1, data.delimiter_set_name);
            rs = doExportOperationsPrepStmt.executeQuery();
            if (rs.next()) {
               // update export
               rs.close();
               doExportOperationsPrepStmt = con.prepareStatement(SQLQueries.UPD_EXPORT);
               doExportOperationsPrepStmt.setString(1, data.force);
               doExportOperationsPrepStmt.setString(2, data.uppercase);
               doExportOperationsPrepStmt.setString(3, data.uppercase_names);
               doExportOperationsPrepStmt.setString(4, data.field_delimiter);
               doExportOperationsPrepStmt.setString(5, data.text_qualifier);
               doExportOperationsPrepStmt.setString(6, data.value_separator);
               doExportOperationsPrepStmt.setString(7, data.date_format);
               doExportOperationsPrepStmt.setString(8, data.missing_date);
               doExportOperationsPrepStmt.setString(9, data.missing_year);
               doExportOperationsPrepStmt.setString(10, data.missing_month);
               doExportOperationsPrepStmt.setString(11, data.missing_day);
               doExportOperationsPrepStmt.setString(12, data.missing_date_character);
               doExportOperationsPrepStmt.setString(13, data.name_mask1);
               doExportOperationsPrepStmt.setString(14, data.name_mask2);
               doExportOperationsPrepStmt.setString(15, data.name_mask3);
               doExportOperationsPrepStmt.setString(16, data.name_mask4);
               doExportOperationsPrepStmt.setString(17, data.brs_format);
               doExportOperationsPrepStmt.setString(18, data.delimiter_set_name);
               doExportOperationsPrepStmt.executeUpdate();
               doExportOperationsPrepStmt.close();
            }
            else {
               rs.close();
               // insert into export
               doExportOperationsPrepStmt = con.prepareStatement(SQLQueries.INS_EXPORT);
               doExportOperationsPrepStmt.setString(1, data.force);
               doExportOperationsPrepStmt.setString(2, data.uppercase);
               doExportOperationsPrepStmt.setString(3, data.uppercase_names);
               doExportOperationsPrepStmt.setString(4, data.field_delimiter);
               doExportOperationsPrepStmt.setString(5, data.text_qualifier);
               doExportOperationsPrepStmt.setString(6, data.value_separator);
               doExportOperationsPrepStmt.setString(7, data.date_format);
               doExportOperationsPrepStmt.setString(8, data.missing_date);
               doExportOperationsPrepStmt.setString(9, data.missing_year);
               doExportOperationsPrepStmt.setString(10, data.missing_month);
               doExportOperationsPrepStmt.setString(11, data.missing_day);
               doExportOperationsPrepStmt.setString(12, data.missing_date_character);
               doExportOperationsPrepStmt.setString(13, data.delimiter_set_name);
               doExportOperationsPrepStmt.setString(14, data.name_mask1);
               doExportOperationsPrepStmt.setString(15, data.name_mask2);
               doExportOperationsPrepStmt.setString(16, data.name_mask3);
               doExportOperationsPrepStmt.setString(17, data.name_mask4);
               doExportOperationsPrepStmt.setString(18, data.brs_format);
               doExportOperationsPrepStmt.executeUpdate();
               doExportOperationsPrepStmt.close();
            }
         }
      } catch (Exception e) {
         CommonLogger.printExceptions(this, "Exception while saving the delimiter data.", e);
      }
      String userSessionId = task.getFossaSessionId();
      //Start writing the XML
      writer.startElement(T_OK);
      writer.writeAttribute(A_FOSSAID, userSessionId);
      writer.endElement();
   }

   public boolean isReadOnly()
   {
      return true;
   }

}
