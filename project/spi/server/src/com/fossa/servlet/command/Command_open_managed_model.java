/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.fossa.servlet.command;

import com.fossa.servlet.common.CommonLogger;
import com.fossa.servlet.common.msg.MessageWriter;
import com.fossa.servlet.dao.DBTask;
import com.fossa.servlet.server.ManagedModelPeer;
import com.fossa.servlet.session.UserTask;
import java.io.IOException;
import org.w3c.dom.Element;

/**
 * This class handles opening of managed model
 * @author ashish
 */
class Command_open_managed_model implements Command{
   
    public String execute(Element action, UserTask task, DBTask dbTask, MessageWriter writer) {
        String name = action.getAttribute(A_NAME);        
        try {
            ManagedModelPeer modelPeer = new ManagedModelPeer(task,dbTask,name,writer);
        } catch (IOException ex) {
             CommonLogger.printExceptions(this, "Exception while opening managed model." , ex);
        }   
        return null;
    }

    public boolean isReadOnly() {
        return true;
    }

}
