/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fossa.servlet.command;

import com.fossa.servlet.common.CommonLogger;
import com.fossa.servlet.common.msg.MessageWriter;
import com.fossa.servlet.dao.DBTask;
import com.fossa.servlet.session.UserTask;
import java.sql.CallableStatement;
import java.sql.Connection;
import org.w3c.dom.Element;

/**
 * This class handles in picking up sampled documents based on the sample size, project fields and the coder(s).
 * @author anurag
 */
public class Command_iso_sampling implements Command {

    private int volume_id = 0;
    private int project_id = 0;    
    private int sample_document_size = 0;
    private String coders = null;
    private String projectFields = null;    
    private Connection con;      

    public String execute(Element action, UserTask user, DBTask dbTask, MessageWriter writer) {
        try {
            con = dbTask.getConnection();
            volume_id = Integer.parseInt(action.getAttribute(A_VOLUME_ID));
            project_id = Integer.parseInt(action.getAttribute(A_PROJECT_ID));
            sample_document_size = Integer.parseInt(action.getAttribute(A_SAMPLE_SIZE));
            coders = action.getAttribute(A_CODERS);
            projectFields = action.getAttribute(A_FIELD_COUNTED); 
            
            logger.info("VolumeId: "+volume_id+" ProjectId : "+project_id+" ProjectsFields : "+projectFields+" Coders : "+coders);
            
            String storedProcedureString = "{ call sproc_createQASamples(?,?,?,5,?,?) }";            
            
            /* This procedure picks up various child ids randomnly. Each child id represents a document 
             * and its picked up based on the project field(s) and the coder(s) who has coded 
             * the document. The number of records picked up depends on how much the sample size should be.
             * The proc doesnt have a return statement
             */ 
            CallableStatement cs = con.prepareCall( storedProcedureString );            
            
            cs.setInt(1, project_id);               //project_id
            cs.setInt(2, volume_id);                //volume_id
            cs.setString(3, projectFields);         //projectfields
            cs.setInt(4, sample_document_size);     //sample_size
            cs.setString(5, coders);                //coders
            cs.execute();            
            cs.close();

            //Start writing the XML
            String userSessionId = user.getFossaSessionId();
            writer.startElement(T_ISO_SAMPLING);
            writer.writeAttribute(A_FOSSAID, userSessionId);
            writer.writeAttribute(A_COUNT, 1);
            writer.endElement();

        } catch (Exception exc) {
            CommonLogger.printExceptions(this, "Exception while doing iso sampling.", exc);
        }
        return null;
    }  
   
    public boolean isReadOnly() {
        return true;
    }
}
