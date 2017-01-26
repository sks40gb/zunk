/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.fossa.servlet.command;

import com.fossa.servlet.common.msg.MessageWriter;
import com.fossa.servlet.dao.DBTask;
import com.fossa.servlet.session.UserTask;
import java.io.IOException;
import java.net.InetAddress;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.w3c.dom.Element;

/**
 * Command Class to get Last Inserted ServerTaskQueueId of Export Process(Output Generation)
 * @author balab
 */
public class CommandTaskServerQueue implements Command{

    public String execute(Element action, UserTask user, DBTask dbTask, MessageWriter writer) {
        try {
            Connection connction = dbTask.getConnection();
            String userSessionId = user.getFossaSessionId();
            Statement getServerTaskQueue = dbTask.getStatement();
            //Get ServerTaskQueueId    
            ResultSet rs = getServerTaskQueue.executeQuery("select top 1 servertaskqueue_id from servertaskqueue order by servertaskqueue_id desc");
            if(rs.next()){
                  int servertaskqueue_id = rs.getInt(1);
                  String serverIp =  InetAddress.getLocalHost().toString();
                  String split[] = serverIp.split("/");  
                  
                  //Update ServerTask with Corresponding Server IP 
                  PreparedStatement updateservertaskqueue = connction.prepareStatement("Update servertaskqueue set server_ip=? " +
                                                                                      " where servertaskqueue_id=?");
                 updateservertaskqueue.setString(1, split[1]);
                 updateservertaskqueue.setInt(2, servertaskqueue_id);
                 updateservertaskqueue.executeUpdate();
                 //Write to XML
                 writer.startElement(T_SERVER_QUEUE_ID);
                 writer.writeAttribute(A_FOSSAID, userSessionId);
                 writer.writeAttribute(A_SERVER_QUEUE_ID, Integer.toString(servertaskqueue_id));                               
                 writer.endElement();            
            }
        } catch (IOException ex) {
            Logger.getLogger(CommandTaskServerQueue.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(CommandTaskServerQueue.class.getName()).log(Level.SEVERE, null, ex);
        }
          return null;
    }

    public boolean isReadOnly() {
        return true;
    }

}
