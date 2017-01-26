/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fossa.servlet.command;

import com.fossa.servlet.client.MessageMap;
import com.fossa.servlet.common.CommonLogger;
import com.fossa.servlet.common.Log;
import com.fossa.servlet.common.SQLQueries;
import com.fossa.servlet.common.msg.MessageWriter;
import com.fossa.servlet.dao.DBTask;
import com.fossa.servlet.session.UserTask;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * This class handles the updation of mails received.
 * @author ashish
 */
class Command_mailreceived_data implements Command
{

   private PreparedStatement updateMailRcvdPrepStmt;

   public String execute(Element action, UserTask task, DBTask dbTask, MessageWriter writer)
   {
      Element givenValueList = action;

      Node firstChild = givenValueList.getFirstChild();
      while (firstChild != null && firstChild.getNodeType() != Node.ELEMENT_NODE) {
         firstChild = firstChild.getNextSibling();
      }
      if (firstChild == null) {
         Log.print("Handler_mailreceived_data null firstChild");
         return null;
      }

      Map mailRcvdIDMap = new HashMap();   //Contains the mail received id and mail status
        // fill in the id and status from the xml
      try {
         mailRcvdIDMap = MessageMap.decode((Element) firstChild);
      } catch (Throwable t) {
         logger.error("Exception while reading the MessageMap." + t);
         StringWriter sw = new StringWriter();
         t.printStackTrace(new PrintWriter(sw));
         logger.error(sw.toString());
         Log.quit(t);
      }
      if (mailRcvdIDMap.size() < 1) {
         Log.print("Handler_mailreceived_data empty map");
         return null;
      }
      try {
         updateMailRcvdPrepStmt = task.prepareStatement(dbTask, SQLQueries.UPD_MAIL_RCVD);
         for (Iterator i = mailRcvdIDMap.entrySet().iterator(); i.hasNext();) {
            Map.Entry e = (Map.Entry) i.next();
            Log.print("Handler_mailreceived_data " + e.getKey());
            updateMailRcvdPrepStmt.setString(1, (String) e.getValue());
            updateMailRcvdPrepStmt.setInt(2, Integer.parseInt((String) e.getKey()));
            updateMailRcvdPrepStmt.executeUpdate();
         }
         updateMailRcvdPrepStmt.close();
      } catch (SQLException sql) {
         CommonLogger.printExceptions(this, "SQLException while updating the mail received data.", sql);
         return null;
      } catch (Exception exc) {
         CommonLogger.printExceptions(this, "Exception while updating the mail received data.", exc);
         return null;
      }
      return null;
   }

   public boolean isReadOnly()
   {
      return true;
   }

}
