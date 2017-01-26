/*
 * Command_shutdown.java
 *
 * Created on November 21, 2007, 2:51 PM
 *
 * To change this template, choose Tools | Template Manager
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
 * This class is invoked during shutdown
 * @author bmurali
 */
public class Command_shutdown implements Command{
    
    public String execute(Element action, UserTask user, DBTask dbTask, MessageWriter writer) {
        //DiaListener.shutdown();
        user.commitTransaction(dbTask);
        try {
            writer.startElement(T_OK);        
            writer.endElement();
        } catch (IOException ex) {
            CommonLogger.printExceptions(this, "Exception while commiting the transactions.", ex);
        }
        return null;
    }

    public boolean isReadOnly() {
        return false;
    }
    
}
