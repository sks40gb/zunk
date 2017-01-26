/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package client;

import common.msg.MessageWriter;
import java.io.IOException;
import java.sql.ResultSet;
import org.w3c.dom.Element;

/**
 * ClientTask for activiry summary for a user.
 * It gets the record like -
 *                      1.Activity
 *                      2.Duration
 *                      3.Start Time
 *                      4.End Time
 *                      5.Project
 *                      6.Volume
 *                      7.Batch
 */
 
public class TaskDisplaySummaryList extends ClientTask {
   
    /** User Id */
    private int userId;    
    /** The server connection */
    private ServerConnection scon = Global.theServerConnection;
 
    public TaskDisplaySummaryList(int userId){
       this.userId = userId;
      
    }
    //Requsets sent to the server in xml format.     
    @Override
    public void run() throws IOException {
        MessageWriter writer = scon.startMessage(T_DISPLAY_SUMMARY_LIST);
        writer.writeAttribute(A_USERS_ID, userId);
        writer.endElement();
        writer.close(); 
           
        Element reply = scon.receiveMessage();
        final ResultSet rsObj = Sql.resultFromXML(reply);

        setResult(rsObj);
                 
    }

}


