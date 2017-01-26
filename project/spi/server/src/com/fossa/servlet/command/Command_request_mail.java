/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.fossa.servlet.command;

import com.fossa.servlet.common.CommonLogger;
import com.fossa.servlet.common.MailText;
import com.fossa.servlet.common.SQLQueries;
import com.fossa.servlet.common.msg.MessageWriter;
import com.fossa.servlet.dao.DBTask;
import com.fossa.servlet.exception.ServerFailException;
import com.fossa.servlet.session.UserTask;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.w3c.dom.Element;

/**
 * This class handles the requests for mails.
 * @author ashish
 */
class Command_request_mail implements Command{

    public Command_request_mail() {
    }

    public String execute(Element action, UserTask task, DBTask dbTask, MessageWriter writer) {      
        String type = action.getAttribute(A_TYPE);
        int id = Integer.parseInt(action.getAttribute(A_ID));        
        Statement getSentMailsStatement = null;
        ResultSet rs = null;
        Connection con = null;
        try{
            getSentMailsStatement = dbTask.getStatement();
            con = dbTask.getConnection();

            writer.startElement(T_MAIL_DATA);

            MailText data = new MailText();
            
            //Get the received mails
            if (type.equals("mailreceived")) {
                PreparedStatement getRcvdMailPrepStmt =  con.prepareStatement(SQLQueries.SEL_MAIL_UNAME);
                getRcvdMailPrepStmt.setInt(1, id);
                rs = getRcvdMailPrepStmt.executeQuery();                
                if (! rs.next()) {
                    throw new ServerFailException("Mailreceived is not found.");
                }                
                data.mailsentUserName = rs.getString(1);
                data.recipientList = rs.getString(2);
                data.text = rs.getString(3);                
                data.recipientUserName = rs.getString(4);
                data.status = rs.getString(5);
            } else { //Get the sent mails
                rs = getSentMailsStatement.executeQuery(SQLQueries.SEL_MAIL_RLIST +id);
                if (! rs.next()) {
                    throw new ServerFailException("Mailsent is not found.");
                }                
                data.mailsentUserName = rs.getString(1);
                data.recipientUserName = "";
                data.recipientList = rs.getString(2);
                data.text = rs.getString(3);
                data.status = rs.getString(4);
            }
            writer.encode(MailText.class, data);
            writer.endElement();
        } catch (SQLException sql) {
            CommonLogger.printExceptions(this, "SQLException while requesting for the mails.", sql);
            return null;
        } catch (Exception exc) {
            CommonLogger.printExceptions(this, "Exception while requesting for the mails.", exc);
            return null;
        }    
        return null;
    }

    public boolean isReadOnly() {
        return false;
    }

}
