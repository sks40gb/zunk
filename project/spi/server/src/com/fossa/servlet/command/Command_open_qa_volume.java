/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.fossa.servlet.command;

import com.fossa.servlet.common.CommonLogger;
import com.fossa.servlet.common.Log;
import com.fossa.servlet.common.msg.MessageWriter;
import com.fossa.servlet.dao.DBTask;
import com.fossa.servlet.exception.ServerFailException;
import com.fossa.servlet.exception.UserErrorMessage;
import com.fossa.servlet.server.EventLog;
import com.fossa.servlet.session.UserTask;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.w3c.dom.Element;

/**
 * This class verifies whether selected volume is available for QA or not.
 * @author ashish
 */
class Command_open_qa_volume implements Command{
   
    public String execute(Element action, UserTask task, DBTask dbTask, MessageWriter writer) {    
       
        int volumeId = Integer.parseInt(action.getAttribute(A_VOLUME_ID));
        task.setVolumeId(volumeId);        
        Log.print("in Command_open_qa_volume.run vol="+volumeId);
        assert ! task.isAdmin();
        Statement verifyQAVolumeStatement = null;        
        ResultSet verifyQAVolumeResultSet = null;
        try{
            verifyQAVolumeStatement = dbTask.getStatement();
            String project = null;
            String volume = null;
            int returnProjectId = 0;
            String splitDocuments = null; 
            // verify volume id
            // get volume and project information
            // Note.  If there is session with this volume and batch_id=0,
            //   then somebody (else) has this volume open for QA         
            verifyQAVolumeResultSet = verifyQAVolumeStatement.executeQuery("SELECT P.project_name, V.volume_name, split_documents, P.project_id " +
                                          "FROM project P inner join volume V on V.project_id = P.project_id " +
                                          "left join session S on S.volume_id=V.volume_id and S.batch_id=0 " +
                                          "WHERE P.active = 1 and V.volume_id= "+volumeId+" and S.volume_id is null");
                        
            if (verifyQAVolumeResultSet.next()) {
                //Volume is available for QA
                project = verifyQAVolumeResultSet.getString(1);
                volume = verifyQAVolumeResultSet.getString(2);
                splitDocuments = verifyQAVolumeResultSet.getBoolean(3) ? "Yes" : "No";
                returnProjectId = verifyQAVolumeResultSet.getInt(4);
                verifyQAVolumeResultSet.close();
            } else {
                //Volume is not available for QA
                verifyQAVolumeResultSet.close();
                throw new ServerFailException(UserErrorMessage.volumeNotAvailable);
            }
            task.lockVolume(dbTask,volumeId);
            //Log the event table
            EventLog.open(task, dbTask, volumeId, /* batchId */ 0, "QA");            
            task.commitTransaction(dbTask);           
            //Start writing the XML
            String userSessionId = task.getFossaSessionId();
            writer.startElement(T_BATCH_OPENED);
            writer.writeAttribute(A_FOSSAID, userSessionId);
            writer.writeAttribute(A_BATCH_ID, 0);
            writer.writeAttribute(A_PROJECT_NAME, project);
            writer.writeAttribute(A_SPLIT_DOCUMENTS, splitDocuments);
            writer.writeAttribute(A_PROJECT_ID, returnProjectId);
            writer.endElement();
       
        } catch (SQLException sql) {
            CommonLogger.printExceptions(this, "SQLException while opening a new volume for QA." , sql);
            return null;
        } catch (ServerFailException exc) {
            CommonLogger.printExceptions(this, "ServerFailException while opening a new volume for QA." , exc);
            return exc.getMessage();
        } catch (Exception exc) {
            CommonLogger.printExceptions(this, "Exception while opening a new volume for QA." , exc);
            return null;
        } 
    return null;
    
    }

    public boolean isReadOnly() {
        return false;
    }

}
