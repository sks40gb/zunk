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
 * This class handles the requests for pages.
 * @author ashish
 */
class Command_request_page implements Command{

    public String execute(Element action, UserTask task, DBTask dbTask, MessageWriter writer) {        
        int givenPageId = Integer.parseInt(action.getAttribute(A_PAGE_ID));
        int delta       = Integer.parseInt(action.getAttribute(A_DELTA));
        int boundary    = Integer.parseInt(action.getAttribute(A_BOUNDARY));
        boolean findEnd = "YES".equals(action.getAttribute(A_FIND_LAST));        
        try{
            MarshallPage m = MarshallPage.makeInstance(task, dbTask, action);
            int pos[] = m.findRelative(givenPageId, delta, boundary, findEnd);
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
        }catch (SQLException sql) {
            CommonLogger.printExceptions(this, "SQLException while requesting for the page." , sql);
            return null;
        } catch (Exception exc) {
            CommonLogger.printExceptions(this, "Exception while requesting for the page." , exc);
            return null;
        }  
        return null;
    }

    public boolean isReadOnly() {
        return false;
    }
}
