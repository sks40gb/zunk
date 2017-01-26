/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fossa.servlet.command;

import com.fossa.servlet.common.CommonLogger;
import com.fossa.servlet.common.InvestigationFields;
import com.fossa.servlet.common.msg.XmlReader;
import java.util.Date;
import org.w3c.dom.Node;

import com.fossa.servlet.common.msg.MessageWriter;
import com.fossa.servlet.dao.DBTask;
import com.fossa.servlet.session.UserTask;
import java.io.IOException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.w3c.dom.Element;

/**
 * This class handles the request for investigation data.
 * @author sunil
 */
public class Command_request_investigation_data implements Command {

   private PreparedStatement pst;
   /** Project Id, the feedback required for */
   private int projectId;
   /** Batch Id*/
   private String batchId;
   /** Document Number */
   private String docNumber;
   /** <code>Feedback</code> fields contains the investigatoin data. */
   private Feedback feedback;
   /** for writing the reply message to the client in xml format */
   private MessageWriter writer;
   
   /** Treatment level of batch (L1/L2)*/
    private String treatmentLevel;
    
    /** Table name (page/project_l1 table)*/
     private String tableName ;
     
     /**Bates number*/
     private String bates_number;
     
     /**Document number*/
     private String documentOrbatesNumber;
     
     /**volumeId*/
     private int volumeId=0;
     
     /**Field Name*/
     private String fieldName;
     
     /**PageId*/
     private int page_id;
     
     /**Filename*/
     private String fileName;
     
     /**SqlQuery string*/
    private String sub_query;
    
    /**Child Id or Document Id*/
     private int child_id;
     
   public String execute(Element action, UserTask user, DBTask dbTask, MessageWriter writer) {
      try {
         feedback = new Feedback();
         Element givenValueList = action;
         Node firstChild = givenValueList.getFirstChild();
         while (firstChild != null && firstChild.getNodeType() != Node.ELEMENT_NODE) {
            firstChild = firstChild.getNextSibling();
         }

         if (firstChild != null) {
            InvestigationFields ifields = new InvestigationFields();

            try {
               //Feed the values to the ifields object of <code>InvestigationFields</code>.
               XmlReader.decode(givenValueList, ifields);
            } catch (IOException ex) {
               CommonLogger.printExceptions(this, "Exception while reading the XMLReader.", ex);
            }

            projectId = ifields.projecId;
            batchId = ifields.batchId;
            docNumber = ifields.docNumber;
            fieldName= ifields.tagName; 
            
            Connection con = dbTask.getConnection();
            //Get the event,userid and image path for the document number
            Statement st = dbTask.getStatement();
             
            System.out.println("batchId==========>"+ batchId);
            ResultSet getTreatmentLevel = st.executeQuery("select treatment_level,volume_id from batch where batch_id="+batchId);
            if(getTreatmentLevel.next()){//get the batch leve(L1/L2)
              treatmentLevel = getTreatmentLevel.getString(1);
              volumeId = getTreatmentLevel.getInt(2);
              System.out.println("treatmentLevel==========>"+ treatmentLevel);
            }else{//if no level 
              treatmentLevel="";
            }
            if(treatmentLevel.equals("L2")){//set the table name and document number for page               
             tableName = "page";
             documentOrbatesNumber = "document_number";
            }else{//set the table name and document number for project_l1 table
             tableName = "project_l1";
             ResultSet getPageId = st.executeQuery("select page_id ,boundary_flag from page where document_number="+docNumber);
             if(getPageId.next()){
                page_id = getPageId.getInt(1);
               String boundary_flag = getPageId.getString(2);
               if(!boundary_flag.equals("D")){                
                boolean flag = true;
                while(flag){
                    page_id = page_id--;
                    ResultSet getNextPageId = st.executeQuery("select page_id  from page where page_id="+page_id+" and boundary_flag='D'");
                    if(getNextPageId.next()){
                    page_id = getNextPageId.getInt(1); 
                    flag = false;
                    }
                }               
               }
               
             }
             ResultSet getFileNameFromProject_l1 = st.executeQuery("select filename  from project_l1 where page_id="+page_id);
             if(getFileNameFromProject_l1.next()){
              fileName = getFileNameFromProject_l1.getString(1);
             }
             //sub_query = fileName;
             //documentOrbatesNumber = "bates_number";
            }
            if(treatmentLevel.equals("L2")){
                sub_query =" select distinct filename from "+tableName+" where "+documentOrbatesNumber+"="+docNumber+
                            " and volume_id ="+volumeId+" and (boundary_flag ='D' or boundary_flag ='C')";
            }else{
              sub_query = fileName;
            }
            String queryToGetInvestigationValue ="SELECT distinct CH.event, CH.user_id,CH.codinghistory_id , V.image_path, P.child_id ,CH.value " +
                    "FROM codinghistory CH inner join "+tableName+" P on P.child_id = CH.child_id " +
                    "inner join volume V on V.volume_id = P.volume_id " +
                    "WHERE P.filename like ("+sub_query+") AND CH.project_id = ? AND CH.batch_id = ? AND field_name = ?";
            
            pst = con.prepareStatement(queryToGetInvestigationValue);
            pst.setInt(1, projectId);
            pst.setString(2, batchId);
            pst.setString(3, fieldName);
            pst.executeQuery();
            ResultSet rs = pst.getResultSet();

            //setting coder, checker, tally, listing , qa and its values.
                //setting image path of the docN.
            feedback.setTypeAndEmpId(rs);
            
            if (feedback.getChecker() == null || feedback.getChecker().equals("")) {
               feedback.setChecker(feedback.getCoder());
               feedback.setCheckerValue(feedback.getCoderValue());
            }
            //SQL query to get the child_id
            String sql_query_getChildId = "select distinct child_id from "+tableName+" where "+documentOrbatesNumber+" ="+docNumber+" and volume_id="+volumeId;
            
            ResultSet getChildId = st.executeQuery(sql_query_getChildId);
            if(getChildId.next()){
             child_id= getChildId.getInt(1);
            }
            
            //SQL query to get the Tally values
            String sql_query_tallyValues = "select  TD.field_value,TD.correction_data, TA.user_id from tally_dictionary TD " +
                                           " inner join tally_mapping TM on TM.tally_mapping_id = TD.tally_mapping_id "+
                                           " inner join projectfields PF on PF.projectfields_id = TM.project_field_id "+
                                           " inner join tally_assignment TA on TA.tally_dictionary_group_id = " +
                                           " TD.tally_dictionary_group_id "+
                                           " where TD.batch_id = "+batchId+" and TD.child_id ="+child_id+" and(word_sequence > 0 or tag_sequence >= 0)";
            
            ResultSet getMultipleTally = st.executeQuery(sql_query_tallyValues);
            feedback.setMultipleTally(getMultipleTally);
            
            //Get filename for the selected child id
            pst = con.prepareStatement("SELECT filename FROM page where child_id = " + feedback.getChildId() + "");
            pst.executeQuery();
            rs = pst.getResultSet();
           
            //saving the image filename
            feedback.setImages(rs);
            pst.close();
            rs.close();

            writeXmlFromFeedBack(user, writer);
         }
      } catch (Exception exc) {
         CommonLogger.printExceptions(this, "Exception while getting the investigation data.", exc);
      }
      return null;
   }

   public boolean isReadOnly() {
      return true;
   }

   /**
     * Writing the XML as the reponse message from the server.
     * @param task                     Get fossa session id 
     * @param writer                   <code>MessageWriter</code> to write the xml and send it to the client.
     * @throws java.sql.SQLException   If any SQL error occured. 
     * @throws java.io.IOException     If any i/o error takes place while writing the XML.
     */
   private void writeXmlFromFeedBack(UserTask task, MessageWriter writer)
           throws SQLException, IOException {
      this.writer = writer;
      //get fossa session id for the user.
      String userSessionId = task.getFossaSessionId();
      //start to write the XML.
      writer.startElement(T_RESULT_SET);
      writer.writeAttribute(A_FOSSAID, userSessionId);
      writer.writeAttribute(A_COUNT, 14);
      writer.startElement(T_ROW);
      writeAttribute(feedback.getCoder());
      writeAttribute(feedback.getCoderValue());
      writeAttribute(feedback.getChecker());
      writeAttribute(feedback.getCheckerValue());
      writeAttribute(feedback.getListing());
      writeAttribute(feedback.getListingValue());
      writeAttribute(feedback.getTally());
      writeAttribute(feedback.getTallyValue());
      writeAttribute(feedback.getQa());
      writeAttribute(feedback.getQa());
      //added to get the multiple Tallier
      writeAttribute(feedback.getMultipleTally());
      writeAttribute(feedback.getMultipleTallyUser());
      writeAttribute(feedback.getImagePath());
      writeAttribute(feedback.getImages());
      writer.endElement();
      //end XML
      writer.endElement();
   }

   /**
     * Write the value to the column and if check the value 
     * if it is null, set the attribut <code>A_IS_NULL</code> to <cod>"Yes"</code>
     * else not needed. 
     * @param value                 Value to be written in the column.
     * @throws java.io.IOException  If any i/o error occured.
     */
   private void writeAttribute(String value) throws IOException {
      //start writing column
      writer.startElement(T_COLUMN);
      if (value == null) {
         writer.writeAttribute(A_IS_NULL, "YES");
      }
      else {
         writer.writeContent(value);
      }
      //column end.
      writer.endElement();
   }

}

/**
 * This class contains the fields for the investingation records. 
 * The investingation data is fed to the <code>Feedback</code>
 * and used while writing the XML as the reposnse from the server.
 * If the values are not set the defaut values are assumed to be 
 * blank.
 * @see Command_request_investigation_data
 */
class Feedback {

   private final String CODING = "Coding";
   private final String CODING_QC = "CodingQC";
   private final String TALLY = "Tally";
   private final String LISTING = "Listing";
   private final String QA = "QA";
   private String imagePath;
   private java.util.Date reportDate;
   private String validity;
   private String comments;
   private String xeroxComments;
   private String samplingused;
   private String coveredByQa;
   private String QALevel;
   private String remarks;
   private String dtys;
   private String coder = "";
   private String coderValue = "";
   private String checker = "";
   private String checkerValue = "";
   private String listing = "";
   private String listingValue = "";
   private String tally = "";
   private String tallyValue = "";
   private String qa = "";
   private String qaValue = "";
   private String errorClass;
   private String typeOfDoc;
   private String location;
   private String legiblity;
   private String images;
   private String childId;
   
   private String multipleTallyValue ="";
   private String multipleTallyUser ="";

   /**
     * Set the coded values and the user who coded the value.
     * @param rs <code>ResultSet</code> having coded value and 
     * corresponding user who did the code.
     */
   public void setTypeAndEmpId(ResultSet rs) {
      try {
         String eventType;
         String user_id;
         String coding_history_id;
         // set the image path and child id for the first iteration.
         boolean isToBeExecuted = true;

         while (rs.next()) {
            if ((eventType = rs.getString(1)) != null &&
                    ((user_id = rs.getString(2)) != null) &&
                    ((coding_history_id = rs.getString(3)) != null)) {
               if (eventType.equalsIgnoreCase(CODING)) {
                  setCoder(user_id);
                  setCoderValue(rs.getString(6));
               }
               else if (eventType.equalsIgnoreCase(CODING_QC)) {
                  setChecker(user_id);
                  setCheckerValue(rs.getString(6));
               }
               else if (eventType.equalsIgnoreCase(LISTING)) {
                  setListing(user_id);
                  setListingValue(rs.getString(6));
               }
               else if (eventType.equalsIgnoreCase(TALLY)) {
                  setTally(user_id);
                  setTallyValue(rs.getString(6));
               }
               else if (eventType.equalsIgnoreCase(QA)) {
                  setQa(user_id);
                  setQaValue(rs.getString(6));
               }

               if (isToBeExecuted) {
                  setImagePath(rs.getString(4));
                  setChildId(rs.getString(5));
                  isToBeExecuted = false;
               }
            }
         }
      } catch (Exception e) {
         CommonLogger.printExceptions(this, "Exception while setting the event type and userid.", e);
      }
   }

   /**
     * Get QA Level.
     * @return
     */
   public String getQALevel() {
      return QALevel;
   }

   /**
     * Set QA level
     * @param QALevel QA level
     */
   public void setQALevel(String QALevel) {
      this.QALevel = QALevel;
   }

   /**
     * Get checker
     * @return checker
     */
   public String getChecker() {
      return checker;
   }

   /**
     * Set Checker
     * @param checker Checker
     */
   public void setChecker(String checker) {
      this.checker = checker;
   }

   /**
     * Get the checker value means what the value he checked.
     * @return Checker value.
     */
   public String getCheckerValue() {
      return checkerValue;
   }

   public void setCheckerValue(String checkerValue) {
      this.checkerValue = checkerValue;
   }

   public String getCoder() {
      return coder;
   }

   public void setCoder(String coder) {
      this.coder = coder;
   }

   public String getCoderValue() {
      return coderValue;
   }

   public void setCoderValue(String coderValue) {
      this.coderValue = coderValue;
   }

   public String getComments() {
      return comments;
   }

   public void setComments(String comments) {
      this.comments = comments;
   }

   public String getCoveredByQa() {
      return coveredByQa;
   }

   public void setCoveredByQa(String coveredByQa) {
      this.coveredByQa = coveredByQa;
   }

   public String getDtys() {
      return dtys;
   }

   public void setDtys(String dtys) {
      this.dtys = dtys;
   }

   public String getErrorClass() {
      return errorClass;
   }

   public void setErrorClass(String errorClass) {
      this.errorClass = errorClass;
   }

   public String getImagePath() {
      return imagePath;
   }

   public void setImagePath(String imagePath) {
      this.imagePath = imagePath;
   }

   public String getImages() {
      return images;
   }

   public void setImages(String images) {
      this.images = images;
   }

   /**
     * Get the images from the <code>resultSet</code> and put into the 
     * single string that will contains all the list of images.
     * The images are separated by <code>|</code>;  
     * @param rs
     */
   public void setImages(ResultSet rs) {
      try {
         images = "";
         if (rs.next()) {

            images += rs.getString(1);
         }
         while (rs.next()) {
            images += "|" + rs.getString(1);
         }

         if (images == null) {
            images = "";
         }
      } catch (SQLException ex) {
         CommonLogger.printExceptions(this, "Exception while setting the images.", ex);
      }
   }

   
   public void setMultipleTally(ResultSet rs) {
      try {
          multipleTallyValue = "";
          multipleTallyUser = "";
         if (rs.next()) {

            multipleTallyValue += rs.getString(2);
            multipleTallyUser += rs.getString(3);
         } 
         while (rs.next()) {
            multipleTallyValue += "|" + rs.getString(2);
            multipleTallyUser += "|" + rs.getString(3);
         }

         if (multipleTallyValue == null ) {
            multipleTallyValue = "";  
         }
          
        if ( multipleTallyUser == null) {       
           multipleTallyUser = "";
        }
      } catch (SQLException ex) {
         CommonLogger.printExceptions(this, "Exception while setting the multipleTally.", ex);
      }
   }
   
   
   
   
   public String getLegiblity() {
      return legiblity;
   }

   public void setLegiblity(String legiblity) {
      this.legiblity = legiblity;
   }

   public String getListing() {
      return listing;
   }

   public void setListing(String listing) {
      this.listing = listing;
   }

   public String getListingValue() {
      return listingValue;
   }

   public void setListingValue(String listingValue) {
      this.listingValue = listingValue;
   }

   public String getLocation() {
      return location;
   }

   public void setLocation(String location) {
      this.location = location;
   }

   public String getQa() {
      return qa;
   }

   public void setQa(String qa) {
      this.qa = qa;
   }

   public String getQaValue() {
      return qaValue;
   }

   public void setQaValue(String qaValue) {
      this.qaValue = qaValue;
   }

   public String getRemarks() {
      return remarks;
   }

   public void setRemarks(String remarks) {
      this.remarks = remarks;
   }

   public Date getReportDate() {
      return reportDate;
   }

   public void setReportDate(Date reportDate) {
      this.reportDate = reportDate;
   }

   public String getSamplingused() {
      return samplingused;
   }

   public void setSamplingused(String samplingused) {
      this.samplingused = samplingused;
   }

   public String getTally() {
      return tally;
   }

   public void setTally(String tally) {
      this.tally = tally;
   }

   public String getTallyValue() {
      return tallyValue;
   }

   public void setTallyValue(String tallyValue) {
      this.tallyValue = tallyValue;
   }

   public String getTypeOfDoc() {
      return typeOfDoc;
   }

   public void setTypeOfDoc(String typeOfDoc) {
      this.typeOfDoc = typeOfDoc;
   }

   public String getValidity() {
      return validity;
   }

   public void setValidity(String validity) {
      this.validity = validity;
   }

   public String getXeroxComments() {
      return xeroxComments;
   }

   public void setXeroxComments(String xeroxComments) {
      this.xeroxComments = xeroxComments;
   }

   public String getChildId() {
      return childId;
   }

   public void setChildId(String childId) {
      this.childId = childId;
   }

    public String getMultipleTally() {
        return multipleTallyValue;
    }

    public void setMultipleTally(String multipleTally) {
        this.multipleTallyValue = multipleTally;
    }

    public String getMultipleTallyUser() {
        return multipleTallyUser;
    }

    public void setMultipleTallyUser(String multipleTallyUser) {
        this.multipleTallyUser = multipleTallyUser;
    }

}
