/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fossa.servlet.command;

import com.fossa.servlet.common.CommonLogger;
import com.fossa.servlet.common.CustomerpriceData;
import com.fossa.servlet.common.Log;
import com.fossa.servlet.common.SQLQueries;
import com.fossa.servlet.common.msg.MessageWriter;
import com.fossa.servlet.common.msg.XmlReader;
import com.fossa.servlet.dao.DBTask;
import com.fossa.servlet.session.UserTask;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * This class handles the customer price operations.
 * @author ashish
 */
class Command_customerprice implements Command
{

   private PreparedStatement pst;

   public String execute(Element action, UserTask task, DBTask dbTask, MessageWriter writer)
   {
      Element givenValueList = action;
      try {
         Node firstChild = givenValueList.getFirstChild();
         while (firstChild != null && firstChild.getNodeType() != Node.ELEMENT_NODE) {
            firstChild = firstChild.getNextSibling();
         }
         if (firstChild != null) {
            CustomerpriceData data = new CustomerpriceData();
            // fill in the int and String fields of the CustomerpriceData
            try {
               XmlReader xmlReader = new XmlReader();
               xmlReader.decode(givenValueList, data);
            } catch (Throwable t) {
               logger.error("Exception while reading XMLReader." + t);
               StringWriter sw = new StringWriter();
               t.printStackTrace(new PrintWriter(sw));
               logger.error(sw.toString());
               Log.quit(t);
            }
            if (data != null) {
               // update or insert the customerprice row contained in data                                  
               if (data.customerprice_id > 0) {
                  // change existing customerprice
                  try {
                     pst = task.prepareStatement(dbTask, SQLQueries.UPD_CUSTOMER_PRICE);
                     pst.setInt(1, getInt(data.unitize_page_price));
                     pst.setInt(2, getInt(data.unitize_doc_price));
                     pst.setInt(3, getInt(data.coding_page_price));
                     pst.setInt(4, getInt(data.coding_doc_price));
                     pst.setInt(5, data.customerprice_id);
                     pst.executeUpdate();
                     pst.close();
                  } catch (SQLException e) {
                     String sqlState = e.getSQLState();
                     int errorCode = e.getErrorCode();
                     Log.print(">>> Handler_customerprice update" + e + " sqlState=" + sqlState + " errorCode=" + errorCode);
                     if (errorCode == UserTask.ER_DUP_ENTRY) {
                        // it's a dup, ignore it
                        Log.print("(Handler_customerprice update) duplicate");
                     }
                     CommonLogger.printExceptions(this, "Exception while customerprice update.", e);
                  }

               }
               else {
                  // add new price
                  try {
                     pst = task.prepareStatement(dbTask, SQLQueries.INS_CUSTOMER_PRICE);
                     pst.setInt(1, data.project_id);
                     pst.setInt(2, data.volume_id);
                     pst.setInt(3, data.field_level);
                     pst.setInt(4, getInt(data.unitize_page_price));
                     pst.setInt(5, getInt(data.unitize_doc_price));
                     pst.setInt(6, getInt(data.coding_page_price));
                     pst.setInt(7, getInt(data.coding_doc_price));
                     pst.executeUpdate();
                     pst.close();
                  } catch (SQLException e) {
                     String sqlState = e.getSQLState();
                     int errorCode = e.getErrorCode();
                     Log.print(">>> Handler_customerprice insert" + e + " sqlState=" + sqlState + " errorCode=" + errorCode);
                     if (errorCode == UserTask.ER_DUP_ENTRY) {
                        // it's a dup, ignore it
                        Log.print("(Handler_customerprice insert) duplicate");
                     }
                     CommonLogger.printExceptions(this, "Exception while customerprice insert.", e);
                  }
               }
            }
         }

      } catch (Exception exc) {
         CommonLogger.printExceptions(this, "Exception while handling customerprice.", exc);
         return null;
      }
      return null;
   }

   /**
     * This method converts a double value into integer.
     * @param str : a 
     * @return
     */
   private int getInt(String str)
   {
      double d = Float.parseFloat(str) / .1;
      String s = Double.toString(d);
      return Integer.parseInt(s.substring(0, s.indexOf(".")));
   }

   public boolean isReadOnly()
   {
      return true;
   }

}
