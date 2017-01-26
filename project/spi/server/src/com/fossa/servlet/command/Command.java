/*
 * Command.java
 *
 * Created on 13 November, 2007, 3:54 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.fossa.servlet.command;

import com.fossa.servlet.common.MessageConstants;
import com.fossa.servlet.common.msg.MessageWriter;
import com.fossa.servlet.dao.DBTask;
import com.fossa.servlet.session.UserTask;
import org.apache.log4j.Logger;
import org.w3c.dom.Element;

/**
 * This interface has the pattern for other Command classes to follow. 
 * @author prakash
 */
public interface Command extends MessageConstants{
   
    //For logging purpose can be used by the implementing class
    public static Logger logger = Logger.getLogger("com.fossa.servlet.command");
    
    //Method to be overriden by the implementing the class    
    public String execute(Element action, UserTask user, DBTask dbTask,MessageWriter writer);
    public boolean isReadOnly();
}