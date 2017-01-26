/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.fossa.servlet.command;

import com.fossa.servlet.common.CommonLogger;
import com.fossa.servlet.common.msg.MessageWriter;
import com.fossa.servlet.dao.DBTask;
import com.fossa.servlet.session.UserTask;
import org.w3c.dom.Element;
import java.sql.Timestamp;
import java.util.Date;

/**
 * This class handles the end of 'Break' activities
 * @author anurag
 */
public class Command_stop_other_activity implements Command{
       
       private int event_break_id;
       
       public String execute(Element action, UserTask user, DBTask dbTask, MessageWriter writer) {
        try{  
              Date date = new Date();
              long time = date.getTime();
              Timestamp timestamp = new Timestamp(time);
              
              event_break_id = Integer.parseInt(action.getAttribute(A_EVENT_BREAK_ID));              
             
              //updates the event_break table with inserting close_timestamp for last inserted id.              
              user.executeUpdate(dbTask, "update event_break set close_timestamp = '"+timestamp+"' where event_break_id = " +event_break_id);              
              
          } catch (Exception exc) {
               CommonLogger.printExceptions(this, "Exception while ending a break or other activity." , exc);
               return null;
          } 
        return null;
    }

    public boolean isReadOnly() {
        return true;
    }

}
