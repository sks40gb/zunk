/*
 * Command_edit_coding_manual_data.java
 *
 * Created on January 27, 2008, 11:23 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package com.fossa.servlet.command;

import com.fossa.servlet.common.CommonLogger;
import com.fossa.servlet.common.EditCodingManualDataServer;
import com.fossa.servlet.common.Log;
import com.fossa.servlet.common.SQLQueries;
import com.fossa.servlet.common.msg.MessageWriter;
import com.fossa.servlet.common.msg.XmlReader;
import com.fossa.servlet.dao.DBTask;
import com.fossa.servlet.exception.ServerFailException;
import com.fossa.servlet.session.UserTask;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * This class handles editing of coding manual data.
 * @author murali
 */
public class Command_edit_coding_manual_data implements Command
{

   private PreparedStatement updateCodingManualPrepStmt;
   private PreparedStatement getProjectPrepStmt;
   private Connection connection;
   private ResultSet getProjectResultSet;
   private String projectName = "";

   public String execute(Element action, UserTask user, DBTask dbTask, MessageWriter writer)
   {
      String contextPath = "";
      String codingManual = "";
      String fileNoSpace = "";
      Log.print("Command_edit_coding_manual_data");
      Element givenValueList = action;

      Node firstChild = givenValueList.getFirstChild();
      while (firstChild != null && firstChild.getNodeType() != Node.ELEMENT_NODE) {
         firstChild = firstChild.getNextSibling();
      }
      if (firstChild != null) {
         EditCodingManualDataServer data = new EditCodingManualDataServer();
         // fill in the int and String fields of the UsersData
         XmlReader reader = new XmlReader();
         try {
            reader.decode(givenValueList, data);
         } catch (IOException ex) {
            CommonLogger.printExceptions(this, "Exception while decoding the XMLReader.", ex);
         }
         if (data != null) {
            connection = dbTask.getConnection();
            try {
               getProjectPrepStmt = connection.prepareStatement(SQLQueries.SEL_EDIT_CM);
               getProjectPrepStmt.setInt(1, data.projectId);
               getProjectPrepStmt.executeQuery();
               getProjectResultSet = getProjectPrepStmt.getResultSet();
               while (getProjectResultSet.next()) {
                  projectName = getProjectResultSet.getString(2);
               }
               contextPath = user.getContextPath();
               codingManual = data.changeFileName;
            
               int spliting = 0;
                if(codingManual.startsWith("http")){
                    spliting =codingManual.lastIndexOf("/", codingManual.length());
                }else{
                    spliting =codingManual.lastIndexOf("\\", codingManual.length());
                }
                String fileName = codingManual.substring(spliting + 1, codingManual.length());
                String replacedFileName = "";
                int index = fileName.indexOf(".");
                String subString = fileName.substring(++index);
                if (subString.matches("TXT")) {
                    subString = "txt";
                    replacedFileName = fileName.replaceFirst(".TXT", ".txt");
                } else if (subString.matches("PDF")) {
                    subString = "pdf";
                    replacedFileName = fileName.replaceFirst(".PDF", ".pdf");
                } else if (subString.matches("DOC")) {
                    subString = "doc";
                    replacedFileName = fileName.replaceFirst(".DOC", ".doc");
                } else {
                    replacedFileName = fileName;
                }
                String[] fname = replacedFileName.split(" ");                
                String ff = "%20";
                for (int i = 0; i < fname.length; i++) {
                    if (i == fname.length - 1) {
                        fileNoSpace = fileNoSpace + fname[i];
                    } else {
                        fileNoSpace = fileNoSpace + fname[i] + ff;
                    }
                 }                
                 String path = codingManual.substring(0, spliting+1) + fileNoSpace;
                 int coding_manual_id =0;
                 PreparedStatement pst = connection.prepareStatement("SELECT coding_manual_id FROM coding_manual WHERE project_name = ?");                        
                pst.setString(1, projectName);
                pst.executeQuery();
                ResultSet rs = pst.getResultSet();
                while (rs.next()) {
                    coding_manual_id = rs.getInt(1);
                }
                 if(coding_manual_id != 0){     //Update                
                      updateCodingManualPrepStmt = user.prepareStatement(dbTask, SQLQueries.UPD_CMEDIT_CM);
                      updateCodingManualPrepStmt.setString(1, fileNoSpace);
                      updateCodingManualPrepStmt.setString(2,path);
                      updateCodingManualPrepStmt.setString(3, projectName);
                      updateCodingManualPrepStmt.executeUpdate();
                      updateCodingManualPrepStmt.close();
                      getProjectPrepStmt.close();               
                 }else{         //Insert
                    updateCodingManualPrepStmt = connection.prepareStatement("INSERT INTO coding_manual (project_name,codingManual_fileName,codingManual_filepath) VALUES(?,?,?)");                            
                    updateCodingManualPrepStmt.setString(1, projectName);
                    updateCodingManualPrepStmt.setString(2, fileNoSpace);
                    updateCodingManualPrepStmt.setString(3, path);
                    updateCodingManualPrepStmt.executeUpdate();
                    updateCodingManualPrepStmt.close();
                 }
            } catch (ServerFailException s) {
               CommonLogger.printExceptions(this, "ServerFailException while editing the coding manual data.", s);
               return s.getMessage();
            } catch (SQLException ex) {
               CommonLogger.printExceptions(this, "Exception while editing the coding manual data.", ex);
            }
         }
      }
      return null;
   }

   public boolean isReadOnly()
   {
      return false;
   }

}
