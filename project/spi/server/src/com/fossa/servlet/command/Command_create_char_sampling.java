/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fossa.servlet.command;

import com.fossa.servlet.common.CommonConstants;
import com.fossa.servlet.common.CommonLogger;
import com.fossa.servlet.common.SQLQueries;
import com.fossa.servlet.common.msg.MessageWriter;
import com.fossa.servlet.dao.DBTask;
import com.fossa.servlet.exception.ServerFailException;
import com.fossa.servlet.session.UserTask;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import org.w3c.dom.Element;

/**
 * This class handles in picking up sampled documents based on the sample size.
 * @author anurag
 */
public class Command_create_char_sampling implements Command {

    private int volume_id = 0;
    private int project_id = 0;
    private int sample_document_size = 0;
    private float requiredAccuracy = 0;
    private Connection con;

    public String execute(Element action, UserTask user, DBTask dbTask, MessageWriter writer) {
        try {
            con = dbTask.getConnection();
            volume_id = Integer.parseInt(action.getAttribute(A_VOLUME_ID));
            project_id = Integer.parseInt(action.getAttribute(A_PROJECT_ID));
            sample_document_size = Integer.parseInt(action.getAttribute(A_SAMPLE_SIZE));
            String samplingType = action.getAttribute(A_SAMPLING_TYPE);
            requiredAccuracy = Float.parseFloat(action.getAttribute(A_REQUIRED_ACCURACY));
            PreparedStatement getVolumeCharCountPStatement = null;
            PreparedStatement updateSamplingPStatement = null;
            PreparedStatement getUnSampledCharCountPStatement = null;
            ResultSet getCharCountResultSet = null;
            ResultSet getUnSampledCharCountResultSet = null;
            double charCount = 0;
            
            logger.info("VolumeId: " + volume_id + " ProjectId : " + project_id + " sample_document_size:" + sample_document_size+" Sampling type: " + samplingType);
            getVolumeCharCountPStatement = con.prepareStatement(SQLQueries.SEL_CHAR_COUNT_FOR_VOLUME);
            getVolumeCharCountPStatement.setInt(1, volume_id);
            getCharCountResultSet = getVolumeCharCountPStatement.executeQuery();

            if (getCharCountResultSet.next()) {
                charCount = getCharCountResultSet.getDouble(1);
            }

            double f = (Double.parseDouble("" + sample_document_size) / 100f);
            charCount = f * charCount;
            
            //get character count of all unsampled document
            double unSampledDocumentCharCount = 0;
            getUnSampledCharCountPStatement = con.prepareStatement(SQLQueries.SEL_CHAR_COUNT_OF_UNSAMPLED_DOCUMENT);
            getUnSampledCharCountPStatement.setInt(1, volume_id);
            getUnSampledCharCountPStatement.setString(2, CommonConstants.STATUS_NO);
            getUnSampledCharCountResultSet = getUnSampledCharCountPStatement.executeQuery();
            
            if(getUnSampledCharCountResultSet.next()) {
                unSampledDocumentCharCount = getUnSampledCharCountResultSet.getDouble(1);
            }
            

            System.out.println("Charcount:" + charCount);
            
            if(charCount < unSampledDocumentCharCount) {
                if (CommonConstants.QA_SAMPLING_CHARACTER.equals(samplingType)) {

                String storedProcedureString = "{ call Sproc_CreateCharacterSampling(?,?,?,5,?) }";

                /* This procedure picks up various child ids randomnly. Each child id represents a document 
                 * and its picked up until the documents size reaches the sampling size.
                 * The number of records picked up depends on how much the sample size should be.
                 * The proc doesnt have a return statement
                 */
                CallableStatement cs = con.prepareCall(storedProcedureString);

                cs.setInt(1, project_id);               //project_id

                cs.setInt(2, volume_id);                //volume_id

                cs.setDouble(3, charCount);    //sample group size
                cs.setFloat(4, requiredAccuracy);    //required accuracy

                cs.execute();
                cs.close();
                logger.info("Procedure Executed");
                
                updateSamplingPStatement = con.prepareStatement(SQLQueries.UPD_SAMPLING);
                updateSamplingPStatement.setString(1, ""+charCount);
                updateSamplingPStatement.setInt(2, volume_id);
                updateSamplingPStatement.setString(3, CommonConstants.SAMPLING_STATUS_INPROGRESS);
                updateSamplingPStatement.executeUpdate();

            }else {
                    throw new ServerFailException("No More Documents Available for Sampling");
            }

            //get project fields and coders for a given project and volume
            PreparedStatement getProjectFieldsPStatement = null;
            PreparedStatement getCodersPStatement = null;
            ResultSet getProjectFieldsResultSet = null;
            ResultSet getCodersResultSet = null;
            String fieldNames = "";
            String coders = "";

            getProjectFieldsPStatement = con.prepareStatement(SQLQueries.SEL_PROJECT_FIELDS);
            getProjectFieldsPStatement.setInt(1, project_id);
            getProjectFieldsResultSet = getProjectFieldsPStatement.executeQuery();

            //get project fields
            while (getProjectFieldsResultSet.next()) {
                if (fieldNames.equals("")) {
                    fieldNames = getProjectFieldsResultSet.getString(1);
                } else {
                    fieldNames = fieldNames + "," + getProjectFieldsResultSet.getString(1);
                }
            }

            getCodersPStatement = con.prepareStatement(SQLQueries.SEL_CODERS_FOR_GIVEN_VOLUME);
            getCodersPStatement.setInt(1, volume_id);
            getCodersPStatement.setString(2, CommonConstants.PROCESS_CODING);
            getCodersResultSet = getCodersPStatement.executeQuery();

            //get coders
            while (getCodersResultSet.next()) {
                if (coders.equals("")) {
                    coders = getCodersResultSet.getString(1);
                } else {
                    coders = coders + "," + getCodersResultSet.getString(1);
                }
            }

            //Start writing the XML
            String userSessionId = user.getFossaSessionId();
            writer.startElement(T_CHARACTER_SAMPLING);
            writer.writeAttribute(A_FOSSAID, userSessionId);
            writer.writeAttribute(A_SELECTED_FIELD_NAME, fieldNames);
            writer.writeAttribute(A_CODERS, coders);
            writer.endElement();
            }

            

        } catch (Exception exc) {
            CommonLogger.printExceptions(this, "Exception while doing Character Sampling.", exc);
        }
        return null;
    }

    public boolean isReadOnly() {
        return true;
    }
}
