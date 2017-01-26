/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.fossa.servlet.command;

import com.fossa.servlet.common.CommonLogger;
import com.fossa.servlet.common.msg.MessageWriter;
import com.fossa.servlet.dao.DBTask;
import com.fossa.servlet.server.BoundaryMapper;
import com.fossa.servlet.session.UserTask;
import org.w3c.dom.Element;

/**
 * Handler for page_boundary message.  Handles updating of boundary, without
 * being recordes as a page save.  This is NOT called for unitizing, it
 * is called when a boundary is changed in a non-unitizing viewer.
 * @see client.TaskSendBoundary
 * @see BoundaryMapper
 * @author ashish
 */

class Command_page_boundary implements Command{
   
    public String execute(Element action, UserTask task, DBTask dbTask, MessageWriter writer) {      
        int volumeId = task.getVolumeId();
        int batchId = task.getBatchId();
        int givenPageId = Integer.parseInt(action.getAttribute(A_PAGE_ID));
        assert action.hasAttribute(A_BOUNDARY_FLAG);
        String givenBoundaryFlag = action.getAttribute(A_BOUNDARY_FLAG);        
        try {
            BoundaryMapper.store(task, dbTask, volumeId, givenPageId, givenBoundaryFlag.trim(),batchId);
        } catch (Exception exception) {
            CommonLogger.printExceptions(this, "Exception while updating the page bouandary." , exception);
        } 
        return null;
    }

    public boolean isReadOnly() {
        return true;
    }

}
