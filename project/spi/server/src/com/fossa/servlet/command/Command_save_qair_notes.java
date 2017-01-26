/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fossa.servlet.command;

import com.fossa.servlet.common.CommonLogger;
import com.fossa.servlet.common.msg.MessageWriter;
import com.fossa.servlet.dao.DBTask;
import com.fossa.servlet.exception.ServerFailException;
import com.fossa.servlet.server.ServerProperties;
import com.fossa.servlet.session.UserTask;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.w3c.dom.Element;

/**
 * This class saves the qair notes after QA sampling
 * @author anurag
 */
public class Command_save_qair_notes implements Command {

    private int sampling_id = 0;
    private String proofReadBy = null;
    private String fileCompareBy = null;
    private String mdbCheckBy = null;
    private String outputCheckBy = null;
    private String outputCheckResult = null;
    private String otherNonConformance = null;
    private String recommendations = null;
    private String productionFacility = null;
    private String erringSection = null;
    private String preparedByQAStaff = null;
    private String notedByQASupvDate = null;
    private String approvedByQASupvDate = null;
    private String previousSubmissionErrorCorrected = null;
    private String qairNumber = null;
    private Connection con;   
    private Statement getSamplingStatement;
    private PreparedStatement insQANotesPrepStmt;
    private String samplingResult = null;
    String qair_path = null;

    public String execute(Element action, UserTask user, DBTask dbTask, MessageWriter writer) {
        try {
            con = dbTask.getConnection();       
            //Get all the aatributes from client requests
            this.qairNumber = action.getAttribute(A_QAIR_NUMBER);
            this.sampling_id = Integer.parseInt(action.getAttribute(A_SAMPLING_ID));
            this.proofReadBy = action.getAttribute(A_PROOF_READ_BY);
            this.fileCompareBy = action.getAttribute(A_FILE_COMPARE_BY);
            this.mdbCheckBy = action.getAttribute(A_MDB_CHECK_BY);
            this.outputCheckBy = action.getAttribute(A_OUTPUT_CHECK_BY);
            this.outputCheckResult = action.getAttribute(A_OUTPUT_CHECK_RESULT);
            this.otherNonConformance = action.getAttribute(A_OTHER_NON_CONFORMANCE);
            this.recommendations = action.getAttribute(A_RECOMMENDATIONS);
            this.productionFacility = action.getAttribute(A_PRODUCTION_FACILITY);
            this.erringSection = action.getAttribute(A_ERRING_SECTION);
            this.preparedByQAStaff = action.getAttribute(A_PREPARED_BY_QA_STAFF);
            this.notedByQASupvDate = action.getAttribute(A_NOTED_BY_QA_SUPV_DATE);
            this.approvedByQASupvDate = action.getAttribute(A_APPROVED_BY_QA_SUPV_DATE);
            this.previousSubmissionErrorCorrected = action.getAttribute(A_PREVIOUS_SUBMISSION_ERROR_CORRECTED);
            this.samplingResult = action.getAttribute(A_SAMPLING_RESULT);        
            //verify qair_number is already existing or not
            getSamplingStatement = con.createStatement();
            ResultSet rsObj = getSamplingStatement.executeQuery("select * from sampling where qair_number = '" + qairNumber + "'");
            if (rsObj.next()) {
                // then don't insert any value --------------
                throw new ServerFailException("QAIR Number already exists");
            } else {
                //insert a new row into qa_notes table
                
                insQANotesPrepStmt = con.prepareStatement("insert into qa_notes(sampling_id,proof_read_by,file_compare_by,output_check_by,mdb_check_by,output_check_result,other_non_conformance,recommendations,production_facility,erring_section,prepared_by_qastaff,noted_by_qasupv_date,approved_by_qasupv_grouphead_date,previous_submission_error_corrected) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
                insQANotesPrepStmt.setInt(1, sampling_id);
                insQANotesPrepStmt.setString(2, proofReadBy);
                insQANotesPrepStmt.setString(3, fileCompareBy);
                insQANotesPrepStmt.setString(4, outputCheckBy);
                insQANotesPrepStmt.setString(5, mdbCheckBy);
                insQANotesPrepStmt.setString(6, outputCheckResult);
                insQANotesPrepStmt.setString(7, otherNonConformance);
                insQANotesPrepStmt.setString(8, recommendations);
                insQANotesPrepStmt.setString(9, productionFacility);
                insQANotesPrepStmt.setString(10, erringSection);
                insQANotesPrepStmt.setString(11, preparedByQAStaff);
                insQANotesPrepStmt.setString(12, notedByQASupvDate);
                insQANotesPrepStmt.setString(13, approvedByQASupvDate);
                insQANotesPrepStmt.setString(14, previousSubmissionErrorCorrected);
                int i = insQANotesPrepStmt.executeUpdate();
                insQANotesPrepStmt.close();
                
                PreparedStatement updateSamplingPrepStmt = con.prepareStatement("update sampling " +
                        "set qair_number = ?, sampling_status = ?, sampling_result = ?" +
                        "where sampling_id =? ");
                updateSamplingPrepStmt.setString(1, qairNumber);
                updateSamplingPrepStmt.setString(2, "Finished");
                updateSamplingPrepStmt.setString(3, samplingResult);
                updateSamplingPrepStmt.setInt(4, sampling_id);
                int j = updateSamplingPrepStmt.executeUpdate();
                updateSamplingPrepStmt.close();
                

            Statement getQANotesStatement = con.createStatement();
            ResultSet rs =getQANotesStatement.executeQuery("select qa_notes_id from qa_notes where sampling_id = "+sampling_id);
            if(rs.next()){
                //Fetch the http qair path from the server.properties file.
                qair_path = ServerProperties.getProperty("QAIR_PATH");
                writer.startElement(TASK_SAVE_QAIR_NOTES);                
                writer.writeAttribute(A_FOSSAID,user.getFossaSessionId());
                writer.writeAttribute(A_PATH,qair_path+qairNumber);
                writer.endElement();
            }else {
                //QAReport is not saved
               throw new ServerFailException("QAIR Report is not saved");
               }
            }

         } catch (IOException ex) {
              CommonLogger.printExceptions(this, "IOException while saving the qair notes." , ex);
              throw new ServerFailException("QAIR Report is not saved");
        } catch (SQLException sqlexc) {
            CommonLogger.printExceptions(this, "Exception while saving the qair notes." , sqlexc);
            throw new ServerFailException("QAIR Report is not saved");
        }catch(ServerFailException e){
            try {
               writer.startElement(T_FAIL);
               String message = e.getMessage();
               writer.writeContent(message);
               writer.endElement();
            } catch (IOException ex) {
              CommonLogger.printExceptions(this, "IOException while saving the qair notes." , ex);
            }
        }catch(Exception e){
           CommonLogger.printExceptions(this, "Exception while saving the qair notes." ,e);
           throw new ServerFailException("QAIR Report is not saved");
        }
        return null;
    }

    public boolean isReadOnly() {
        return true;
    }
}
