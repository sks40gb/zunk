/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.fossa.servlet.command;

import com.fossa.servlet.common.CommonLogger;
import com.fossa.servlet.common.Log;
import com.fossa.servlet.common.msg.MessageWriter;
import com.fossa.servlet.dao.DBTask;
import com.fossa.servlet.session.UserTask;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.Statement;
import org.w3c.dom.Element;

/**
 * This class returns the requested child id for a given page id.
 * @author ashish
 */
class Command_request_coding_values implements Command{

    public Command_request_coding_values() {
    }

    public String execute(Element action, UserTask task, DBTask dbTask, MessageWriter writer) {  
        int givenPageId = Integer.parseInt(action.getAttribute(A_PAGE_ID));
        int delta       = Integer.parseInt(action.getAttribute(A_DELTA));
        int boundary    = Integer.parseInt(action.getAttribute(A_BOUNDARY));
        
        try{
            MarshallPage m = MarshallPage.makeInstance(task, dbTask, action);        
            int [] pos = m.findRelativeInBatch(givenPageId, delta, boundary);            
            String userSessionId = task.getFossaSessionId();
            if (pos[0] != 0) {
                // get the corresponding child id (there must be one)
                Statement st = dbTask.getStatement();
                String level =""; 
                int batch_id =0;        
                ResultSet getBatchIdResultSet = st.executeQuery("select batch_id from child where child_id="+pos[1]);
                if(getBatchIdResultSet.next()){
                  batch_id = getBatchIdResultSet.getInt(1);        
                }
                ResultSet getTreatmentLevelResultSet = st.executeQuery("select batch_id,treatment_level from batch where batch_id="+batch_id);
                if(getTreatmentLevelResultSet.next()){
                  level = getTreatmentLevelResultSet.getString(2);        
                }
                ResultSet getChildIdResultSet =null;
                if(level.equals("L1")){
                     getChildIdResultSet = st.executeQuery(
                    "select child_id"
                    +" from project_l1"
                    +" where project_l1_id="+pos[0]);
                }else{
                  getChildIdResultSet = st.executeQuery(
                    "select child_id"
                    +" from page"
                    +" where page_id="+pos[0]);
                }                 
                if (! getChildIdResultSet.next()) {
                    Log.quit("Command_request_coding_values: no child");
                }
                int childId = getChildIdResultSet.getInt(1);
                getChildIdResultSet.close();                
                writer.startElement(T_PAGE_VALUES);
                writer.writeAttribute(A_FOSSAID, userSessionId);                 
                //Send the child id in XML
                ValueMapper.write(task, dbTask ,writer, m.getVolumeId(), childId);
                writer.endElement();
            } else {                
                writer.startElement(T_FAIL);
                writer.writeAttribute(A_FOSSAID, userSessionId);
                writer.writeContent("Page not found");
                writer.endElement();
            }
         } catch (IOException sql) {
            CommonLogger.printExceptions(this, "IOException while requesting the coding values." , sql);
            return null;
        } catch (Exception exc) {
            CommonLogger.printExceptions(this, "Exception while requesting the coding values." , exc);
            return null;
        }
        return null;
    }

    public boolean isReadOnly() {
        return false;
    }

}
