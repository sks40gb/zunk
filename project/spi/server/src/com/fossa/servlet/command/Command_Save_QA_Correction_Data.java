/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.fossa.servlet.command;

import com.fossa.servlet.common.SQLQueries;
import com.fossa.servlet.common.msg.MessageWriter;
import com.fossa.servlet.dao.DBTask;
import com.fossa.servlet.session.UserTask;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import org.w3c.dom.Element;

/**
 *This Class is to Save the Correction Data done by QAProofReader
 * @author Prakasha
 */
public class Command_Save_QA_Correction_Data implements Command {

    public String execute(Element action, UserTask user, DBTask dbTask, MessageWriter writer) {
        
        PreparedStatement getCorrectedDataStatement = null;
        PreparedStatement saveCorrectionData = null;
        Connection connection = null;
        ResultSet getCorrectionResult = null;
        //TODO: Cyrus:
        //If there is a NumberFormatException then we need to send a error message back.       
        int docId = Integer.parseInt(action.getAttribute(A_DOCUMENT_FIRST_ID));
        int projectFieldId = Integer.parseInt(action.getAttribute(A_PROJECT_FIELD_ID));
        int childId = Integer.parseInt(action.getAttribute(A_CHILD_ID));
        int tagSequence = Integer.parseInt(action.getAttribute(A_TAG_SEQUENCE));
        int samplingId = Integer.parseInt(action.getAttribute(A_SAMPLING_ID));
        
        String codedData = action.getAttribute(A_CODED_DATA);
        String correctionData = action.getAttribute(A_CORRECTION_DATA);
        String correctionType = action.getAttribute(A_CORRECTION_TYPE);
       
        //Get Current Date & Time
        Date date = new Date();
        long time = date.getTime();
        Timestamp timestamp = new Timestamp(time);
        
        /**
         * First get the correction data for given field, child and samplingId
         * if the data is exists update qa_corections with the latest correction data
         * else insert the correction data
         */
        
        try{
            connection = dbTask.getConnection();
            getCorrectedDataStatement = connection.prepareStatement(SQLQueries.SEL_QA_CORRECTION_DATA);
            getCorrectedDataStatement.setInt(1, projectFieldId);
            getCorrectedDataStatement.setInt(2, tagSequence);
            getCorrectedDataStatement.setInt(3, childId);
            getCorrectedDataStatement.setInt(4, samplingId);
            getCorrectionResult = getCorrectedDataStatement.executeQuery();
            
            if(getCorrectionResult.next()) {
                //update correction data
                int correctionId = getCorrectionResult.getInt(1);
                saveCorrectionData = connection.prepareStatement(SQLQueries.UPD_QA_CORRECTION_DATA);
                saveCorrectionData.setInt(1, docId);
                saveCorrectionData.setInt(2, childId);
                saveCorrectionData.setInt(3, projectFieldId);
                saveCorrectionData.setString(4, codedData);
                saveCorrectionData.setString(5, correctionData);
                saveCorrectionData.setString(6, correctionType);
                saveCorrectionData.setInt(7, tagSequence);
                saveCorrectionData.setTimestamp(8, timestamp);
                saveCorrectionData.setInt(9, correctionId);
                saveCorrectionData.executeUpdate();
                
            }else {
                //insert correction data
                saveCorrectionData = connection.prepareStatement(SQLQueries.INS_QA_CORRECTION_DATA);
                saveCorrectionData.setInt(1, docId);
                saveCorrectionData.setInt(2, childId);
                saveCorrectionData.setInt(3, projectFieldId);
                saveCorrectionData.setString(4, codedData);
                saveCorrectionData.setString(5, correctionData);
                saveCorrectionData.setString(6, correctionType);
                saveCorrectionData.setInt(7, tagSequence);
                saveCorrectionData.setTimestamp(8, timestamp);
                saveCorrectionData.setInt(9, samplingId);
                saveCorrectionData.executeUpdate();
            }
            
            //Start writing the XML
            writer.startElement(T_SAVE_QA_CORRECTION_DATA);
            String userSessionId = user.getFossaSessionId();
            writer.writeAttribute(A_FOSSAID, userSessionId);
            writer.endElement();
        }catch(SQLException ex) {
            ex.printStackTrace();
        }catch(IOException io) {
            io.printStackTrace();
        }
        
        return null;
    }

    public boolean isReadOnly() {
        return true;
    }

}
