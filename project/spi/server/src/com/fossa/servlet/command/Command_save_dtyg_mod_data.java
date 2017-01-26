/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fossa.servlet.command;

import com.fossa.servlet.common.CommonLogger;
import com.fossa.servlet.common.MessageConstants;
import com.fossa.servlet.common.msg.MessageWriter;
import com.fossa.servlet.dao.DBTask;
import com.fossa.servlet.session.UserTask;
import org.w3c.dom.Element;

/**
 * This class returns the server path of given project & volume.
 * @author sunil
 */
public class Command_save_dtyg_mod_data implements Command {
   
    private String project_name = "";
    private String volume_name = "";
    private String filePath = "";    
    
    public String execute(Element action, UserTask user, DBTask dbTask, MessageWriter writer) {
        try {
            project_name = action.getAttribute(A_PROJECT_NAME);
            volume_name = action.getAttribute(A_VOLUME_NAME);
            filePath = MessageConstants.serverPath + project_name + "/" + volume_name + "/" + project_name + "_" + volume_name;
            //Send the XML
            String userSessionId = user.getFossaSessionId();
            writer.startElement(T_SAVE_DTYG_MOD_DATA);
            writer.writeAttribute(A_FOSSAID, userSessionId);
            writer.writeAttribute(A_PATH, filePath);
            writer.endElement();
        } catch (Exception exc) {
            CommonLogger.printExceptions(this, "Exception while saving the DTYG mod data.", exc);
        }
        return null;
    }

    public boolean isReadOnly() {
        return true;
    }
}
