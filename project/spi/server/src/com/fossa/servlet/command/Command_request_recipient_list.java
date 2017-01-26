/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.fossa.servlet.command;

import com.fossa.servlet.common.CommonLogger;
import com.fossa.servlet.common.Log;
import com.fossa.servlet.common.SQLQueries;
import com.fossa.servlet.common.msg.MessageWriter;
import com.fossa.servlet.dao.DBTask;
import com.fossa.servlet.session.UserTask;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import org.w3c.dom.Element;

/**
 * This class handles the requests for receipents list of mails.
 * @author ashish
 */
class Command_request_recipient_list implements Command{

    public String execute(Element action, UserTask task, DBTask dbTask, MessageWriter writer) {     
        Statement st = null;
        ResultSet rs = null;
        Connection con = dbTask.getConnection();
       try{
            st = dbTask.getStatement();
            int usersId = task.getUsersId();
            boolean admin;
            boolean qa;
            boolean teamLeader;
            
            //Verify the user is a admin or teamLead or QA
            rs = st.executeQuery(SQLQueries.SEL_REC_ADMIN +usersId);
            
            String _admin="";
            String _qa ="";
            String _tl ="";
            if(rs.next()) {             
                 _admin = rs.getString(1);
                 _qa = rs.getString(1);
                 _tl = rs.getString(1);                
                if("YES".equalsIgnoreCase(_admin)) {
                    admin = true;
                }
                else{
                    admin = false;
                }
                if("YES".equalsIgnoreCase(_qa)) {
                    qa = true;
                }
                else{
                    qa = false;
                }
                if("YES".equalsIgnoreCase(_tl)) {
                    teamLeader = true;
                }
                else{
                    teamLeader = false;
                }            
            } else {
                Log.quit("(Handler_request_recipient) user not found " + usersId);
                return null; // compiler
            }

            if (admin || qa || teamLeader) {
                // admin gets all users, all teams and all team leaders                
                // NOTE:  If you change the following text, make sure 
                // Handler_mailsent_data gets the users_id to match!                            	
                rs = st.executeQuery(SQLQueries.SEL_REC_UNAME);                
            } else {
                // -- non admin/qa users get only their team members and
                // all team leaders for projects in their queues --           
                PreparedStatement select_rec_usrname =  con.prepareStatement(SQLQueries.SEL_REC_USRNAME);
                select_rec_usrname.setInt(1, usersId);
                select_rec_usrname.setInt(2, usersId);
                select_rec_usrname.setInt(3, usersId);
                select_rec_usrname.setInt(4, usersId);
                select_rec_usrname.setInt(5, usersId);
                select_rec_usrname.setInt(6, usersId);
                select_rec_usrname.setInt(7, usersId);
                rs = select_rec_usrname.executeQuery();                
            }            
            CommandFactory factory = CommandFactory.getInstance();
            Command_sql_query sqlCommand = (Command_sql_query)factory.getCommand(T_SQL_QUERY);
            sqlCommand.writeXmlFromResult(task, rs, writer, false);
            rs.close();
         } catch (IOException sql) {
            CommonLogger.printExceptions(this, "IOException while getting the user reciepent list." , sql);
            return null;
        } catch (Exception exc) {
            CommonLogger.printExceptions(this, "Exception while getting the user reciepent list." ,exc);
            return null;
        }  
        return null;
    }

    public boolean isReadOnly() {
        return false;
    }

}
