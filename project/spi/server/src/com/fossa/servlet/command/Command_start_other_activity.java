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
import java.sql.*;
import java.sql.Timestamp;
import java.util.Date;

/**
 * This class handles the start of the 'Break' activities
 * @author anurag
 */
public class Command_start_other_activity implements Command{
    private Connection con;    
    private int event_break_id;
    private String userSessionId = null;
          
    public String execute(Element action, UserTask user, DBTask dbTask, MessageWriter writer) {
              
         try{             
              userSessionId = user.getFossaSessionId();                   
              con = dbTask.getConnection();              
              Date date = new Date();
              long time = date.getTime();
              Timestamp timestamp = new Timestamp(time);
              
              int userId = Integer.parseInt(action.getAttribute(A_USERS_ID));
              String status = action.getAttribute(A_STATUS);
              String notes = action.getAttribute(A_NOTES);             
              
              //Inserts values into event_break table after start button action performed.              
              PreparedStatement pst = con.prepareStatement("insert into event_break(user_id,status,notes,open_timestamp) values (?, ?, ?, ?)");
              pst.setInt(1, userId);
              
              pst.setString(2,status); 
              pst.setString(3,notes); 
              pst.setTimestamp(4,timestamp);
              pst.executeUpdate();
              pst.close();
                            
              //Fetches the last inserted id from event_break table.             
              pst = con.prepareStatement("SELECT TOP 1 event_break_id FROM event_break"
                                              + " ORDER BY event_break_id  DESC");

              pst.executeQuery();
              ResultSet resultSetObj =pst.getResultSet();
              while(resultSetObj.next()){
                event_break_id = resultSetObj.getInt(1);
              }
              pst.close();             
              //Reply the last event_break_id to the client.
              writer.startElement(T_SEND_EVENT_BREAK_ID);
              writer.writeAttribute(A_FOSSAID,userSessionId);
              writer.writeAttribute(A_EVENT_BREAK_ID,event_break_id);
              writer.endElement();
              writer.close();              
              
        } catch (Exception exc) {
            CommonLogger.printExceptions(this, "Exception while starting a break or other activity." , exc);
            return null;
        } 
        return null;
    }

    public boolean isReadOnly() {
        return true;
    }

}

