/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.fossa.servlet.command;

import com.fossa.servlet.common.CommonLogger;
import com.fossa.servlet.common.msg.MessageWriter;
import com.fossa.servlet.dao.DBTask;
import com.fossa.servlet.session.UserTask;
import java.io.IOException;
import org.w3c.dom.Element;

/**
 * This class handles in pinging the server
 * @author ashish
 */
class Command_ping implements Command{

    public String execute(Element action, UserTask task, DBTask dbTask, MessageWriter writer) {               
        try {
            String userSessionId = task.getFossaSessionId();
            writer.startElement(T_OK);
            writer.writeAttribute(A_FOSSAID, userSessionId);
            writer.endElement();
        } catch (IOException exc) {
            CommonLogger.printExceptions(this, "IOException while pinging the server." , exc);
        }catch (Exception exc) {
            CommonLogger.printExceptions(this, "Exception while pinging the server." , exc);
        }    
        return null;
    }

    public boolean isReadOnly() {
        return true;
    }

}
