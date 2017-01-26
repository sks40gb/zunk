/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package client;

import common.msg.MessageWriter;
import java.io.IOException;
import org.w3c.dom.Element;

/**
 * ClientTask to save QA notes.
 * @author anurag
 */
public class TaskSaveQAIRNotes extends ClientTask {

    /** Server connection */
    final private ServerConnection scon = Global.theServerConnection;
    /** Sample Id */
    private int sampling_id;
    /** Proof read by */
    private String proofReadBy;
    /** File compared by */
    private String fileCompareBy;
    /** mdb checked by */
    private String mdbCheckBy;
    /** output checked by */
    private String outputCheckBy;
    private String outputCheckResult;
    private String otherNonConformance;
    private String recommendations;
    private String productionFacility;
    private String erringSection;
    private String preparedByQAStaff;
    private String notedByQASupvDate;
    private String approvedByQASupvDate;
    private String previousSubmissionErrorCorrected;
    private String qairNumber;
    private String samplingResult = "";

    /**
     * Instantiate this class with following parameters.
     * @param sampling_id       Sampling Id
     * @param proofReadBy       Proof read by
     * @param fileCompareBy     File compared by
     * @param mdbCheckBy        MDB checked by
     * @param outputCheckBy     Output checked by
     * @param outputCheckResult
     * @param otherNonConformance
     * @param recommendations
     * @param productionFacility
     * @param erringSection
     * @param preparedByQAStaff
     * @param notedByQASupvDate
     * @param approvedByQASupvDate
     * @param previousSubmissionErrorCorrected
     * @param qairNumber
     */
    public TaskSaveQAIRNotes(int sampling_id, String proofReadBy, String fileCompareBy, 
            String mdbCheckBy, String outputCheckBy, String outputCheckResult, 
            String otherNonConformance, String recommendations, String 
            productionFacility, String erringSection, String preparedByQAStaff, 
            String notedByQASupvDate, String approvedByQASupvDate, String 
            previousSubmissionErrorCorrected, String qairNumber, String samplingResult) {

        this.qairNumber = qairNumber;
        this.sampling_id = sampling_id;
        this.proofReadBy = proofReadBy;
        this.fileCompareBy = fileCompareBy;
        this.mdbCheckBy = mdbCheckBy;
        this.outputCheckBy = outputCheckBy;
        this.outputCheckResult = outputCheckResult;
        this.otherNonConformance = otherNonConformance;
        this.recommendations = recommendations;
        this.productionFacility = productionFacility;
        this.erringSection = erringSection;
        this.preparedByQAStaff = preparedByQAStaff;
        this.notedByQASupvDate = notedByQASupvDate;
        this.approvedByQASupvDate = approvedByQASupvDate;
        this.previousSubmissionErrorCorrected = previousSubmissionErrorCorrected;
        this.samplingResult = samplingResult;
    }

    /**
     * Write the message with attributes.
     * Message should be in XML format. 
     */
    @Override
    public void run() throws IOException {
        MessageWriter writer;
        writer = scon.startMessage(TASK_SAVE_QAIR_NOTES);
        writer.writeAttribute(A_SAMPLING_ID, sampling_id);
        writer.writeAttribute(A_PROOF_READ_BY, proofReadBy);
        writer.writeAttribute(A_FILE_COMPARE_BY, fileCompareBy);
        writer.writeAttribute(A_MDB_CHECK_BY, mdbCheckBy);
        writer.writeAttribute(A_OUTPUT_CHECK_BY, outputCheckBy);
        writer.writeAttribute(A_OUTPUT_CHECK_RESULT, outputCheckResult);
        writer.writeAttribute(A_OTHER_NON_CONFORMANCE, otherNonConformance);
        writer.writeAttribute(A_RECOMMENDATIONS, recommendations);
        writer.writeAttribute(A_PRODUCTION_FACILITY, productionFacility);
        writer.writeAttribute(A_ERRING_SECTION, erringSection);
        writer.writeAttribute(A_PREPARED_BY_QA_STAFF, preparedByQAStaff);
        writer.writeAttribute(A_NOTED_BY_QA_SUPV_DATE, notedByQASupvDate);
        writer.writeAttribute(A_APPROVED_BY_QA_SUPV_DATE, approvedByQASupvDate);
        writer.writeAttribute(A_PREVIOUS_SUBMISSION_ERROR_CORRECTED, previousSubmissionErrorCorrected);
        writer.writeAttribute(A_QAIR_NUMBER, qairNumber);
        writer.writeAttribute(A_SAMPLING_RESULT, samplingResult);
        writer.endElement();
        writer.close();
        Element reply =  scon.receiveMessage();
        setResult(reply);
       
    }
}