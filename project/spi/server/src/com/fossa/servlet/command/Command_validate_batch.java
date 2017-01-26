/*
 * Command_validate_batch.java
 *
 * Created on December 4, 2007, 3:23 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package com.fossa.servlet.command;

import com.fossa.servlet.common.CodingData;
import com.fossa.servlet.common.CommonLogger;
import com.fossa.servlet.common.msg.MessageWriter;
import com.fossa.servlet.dao.DBTask;
import com.fossa.servlet.server.BatchValidate;
import com.fossa.servlet.session.UserTask;
import java.io.IOException;
import java.sql.SQLException;
import org.w3c.dom.Element;

/**
 * This class validate the batch
 * @author bmurali
 */
public class Command_validate_batch implements Command {

    public String execute(Element action, UserTask user, DBTask dbTask, MessageWriter writer) {
        boolean unitize = action.getAttribute(A_IS_UNITIZE).equals("yes") ? true : false;
        int activeGroup = Integer.parseInt((String) action.getAttribute(A_GROUP));

        int errorChildId = 0;
        CodingData data = null;
        MarshallPage m = null;
        try {
            m = MarshallPage.makeInstance(user, dbTask, action);
        } catch (SQLException ex) {            
            CommonLogger.printExceptions(this,"Exception while validating the batch.", ex);
        }        
        try {
            if (0 < (errorChildId = BatchValidate.execute(
                    user, dbTask, m.getVolumeId(), m.getBatchId(), unitize, activeGroup))) {
                int[] pos = new int[2];
                try {
                    pos = m.findChild(errorChildId);
                    assert pos[0] != 0;
                    data = m.collectCodingData(pos);
                } catch (SQLException ex) {                     
                     CommonLogger.printExceptions(this, "SQLException while validating the batch.", ex);
                }
            }
        } catch (SQLException ex) {
            CommonLogger.printExceptions(this, "SQLException while validating the batch.", ex);
        }
        try {
            if (data != null) {
                // return codingData of first error page
                writer.startElement(T_CODING_DATA);
                writer.encode(CodingData.class, data);
                ValueMapper.write(user, dbTask, writer, m.getVolumeId(), data.childId);
                writer.endElement();
            } else {
                writer.startElement(T_OK);
                writer.endElement();
            }
        } catch (IOException ex) {            
            CommonLogger.printExceptions(this, "IOException while writing the results in XML." , ex);
        }
        return null;
    }

    public boolean isReadOnly() {
        return false;
    }
}
