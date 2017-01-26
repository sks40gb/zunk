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
 * This class performs the delete operations for sent mails.
 * @author ashish
 */
class Command_delete_mailsent implements Command
{

   private PreparedStatement updateMailSentPrepStmt;

   public String execute(Element action, UserTask task, DBTask dbTask, MessageWriter writer)
   {

      Element givenValueList = action;
      Node firstChild = givenValueList.getFirstChild();
      while (firstChild != null && firstChild.getNodeType() != Node.ELEMENT_NODE) {
         firstChild = firstChild.getNextSibling();
      }
      if (firstChild == null) {
         Log.print("Command_delete_mailsent null firstChild");
         return null;
      }

      Map idMap = new HashMap();
      // fill in the ids from the xml
      try {
         idMap = MessageMap.decode((Element) firstChild);
      } catch (Throwable t) {
         logger.error("Exception while reading the MessageMap." + t);
         StringWriter sw = new StringWriter();
         t.printStackTrace(new PrintWriter(sw));
         logger.error(sw.toString());
         Log.quit(t);
      }
      if (idMap.size() < 1) {
         Log.print("Command_delete_mailsent empty map");
         return null;
      }
      try {
         updateMailSentPrepStmt = task.prepareStatement(dbTask, SQLQueries.UPD_MAIL_SENT);
         for (Iterator i = idMap.entrySet().iterator(); i.hasNext();) {
            Map.Entry e = (Map.Entry) i.next();
            Log.print("Handler_delete_mailsent " + e.getKey());
            updateMailSentPrepStmt.setString(1, (String) e.getKey());
            updateMailSentPrepStmt.executeUpdate();
         }
         updateMailSentPrepStmt.close();
      } catch (SQLException sql) {
         CommonLogger.printExceptions(this, "SQLException while deleting the sent mails.", sql);
         return null;
      } catch (Exception exc) {
         CommonLogger.printExceptions(this, "Exception while deleting the sent mails.", exc);
         return null;
      }
      return null;
   }

   public boolean isReadOnly()
   {
      return true;
   }

}
