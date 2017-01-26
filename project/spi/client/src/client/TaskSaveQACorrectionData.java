/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package client;

import common.msg.MessageWriter;
import java.io.IOException;
import org.w3c.dom.Element;

/**
 * Task Class to Save QACorrection Data
 * @author Prakasha
 */
public class TaskSaveQACorrectionData extends ClientTask{
    private int docId;
    private int projectFieldId;
    private int childId;
    private int samplingId;
    int tagSequence;
    private String codedData;
    private String correctionData;
    private String correctionType;
    private String createdDate;
    
    final private ServerConnection scon = Global.theServerConnection;
    
    /**
     * Instantiate this class with following parameters.
     * 
     * @param docId            Document Id
     * @param projectFieldId   Project FieldId
     * @param childId          ChildId - Document
     * @param tagSequence      Tag Sequence for this value
     * @param codedData        Coded Data
     * @param correctionData   Correction Data made by QA
     * @param correctionType   Correction Type - UnCoded,MisCoded and Added
     * @param createdDate      Created Date - Current Date
     * @param samplingId       Sampling
     */
    public TaskSaveQACorrectionData(int docId, int projectFieldId, int childId, int tagSequence, String codedData, String correctionData,
            String correctionType, String createdDate, int samplingId) {
        this.docId = docId;
        this.projectFieldId = projectFieldId;
        this.childId = childId;
        this.tagSequence = tagSequence;
        this.codedData = codedData;
        this.correctionData = correctionData;
        this.correctionType = correctionType;
        this.createdDate = createdDate;
        this.samplingId = samplingId;
        
    }

    public void run() throws IOException {
      MessageWriter writer;
      writer = scon.startMessage(T_SAVE_QA_CORRECTION_DATA);  
      writer.writeAttribute(A_DOCUMENT_FIRST_ID, docId);
      writer.writeAttribute(A_PROJECT_FIELD_ID, projectFieldId);
      writer.writeAttribute(A_CHILD_ID, childId);
      writer.writeAttribute(A_TAG_SEQUENCE, tagSequence);
      writer.writeAttribute(A_CODED_DATA, codedData);
      writer.writeAttribute(A_CORRECTION_DATA, correctionData);
      writer.writeAttribute(A_CORRECTION_TYPE, correctionType);
      writer.writeAttribute(A_CREATED_DATE, createdDate);
      writer.writeAttribute(A_SAMPLING_ID, samplingId);
      writer.endElement();
      writer.close();
     
      Element reply = scon.receiveMessage();
    }
}
