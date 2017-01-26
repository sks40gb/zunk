/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.fossa.servlet.command;

import com.fossa.servlet.common.CommonLogger;
import com.fossa.servlet.common.ImageData;
import com.fossa.servlet.common.msg.MessageWriter;
import com.fossa.servlet.dao.DBTask;
import com.fossa.servlet.session.UserTask;
import java.sql.SQLException;
import org.w3c.dom.Element;

/**
 * This class handles the request for page by bates number.
 * @author ashish
 */
class Command_request_page_by_bates implements Command{

    public String execute(Element action, UserTask task, DBTask dbTask, MessageWriter writer) {        
        String batesNumber = action.getAttribute(A_BATES_NUMBER);                
        try{
            MarshallPage m = MarshallPage.makeInstance(task, dbTask, action);
            int [] pos = m.findAbsolute(batesNumber);
            ImageData data = null;
            if (pos[0] != 0) {
                data = m.collectImageData(pos[0]);
            }
            if (data != null) {
                writer.startElement(T_IMAGE_DATA);
                writer.encode(ImageData.class, data);
                writer.endElement();
            } else {
                writer.startElement(T_FAIL);
                writer.writeContent("Page not found");
                writer.endElement();
            }
        } catch (SQLException sql) {
            CommonLogger.printExceptions(this, "SQLException while requesting for the page using bates." , sql);
            return null;
        } catch (Exception exc) {
            CommonLogger.printExceptions(this, "Exception while requesting for the page using bates." ,exc);            
            return null;
        }
    return null;
    }

    public boolean isReadOnly() {
        return false;
    }

}
