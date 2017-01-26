/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package valueobjects;

import java.util.Date;
import java.util.List;

/**
 *
 * @author sunil
 */
public class Feedback {
    public static final String BATCH_ID = "Batch Id";
    public static final String BOX_ID = "BOX ID";
    public static final String DOC_N = "DOCN";
    public static final String TAG_NAME = "TAG Name";
    public static final String TAG_VALUE = "TAG Value";
    public static final String CORRECT_TAG_VALUE = "Correct TAG Value";
    public static final String AUDIT_PROBLEM_DESC_CODE = "Audit Problem Desc Code";
    public static final String PROB_TEXT = "Prob Text";
    public static final int totolColumns = 8;  
    
    public static int  projecId;
    private String batchId;
    private String boxId;
    private String docNumber;
    private String tagName;
    private String tagValue;
    private String correctTagValue;
    private String auditProblem;
    private String procText;
    
    private String coder;
    private String coderValue;    
    private String checker;
    private String checkerValue;
    private String listing;
    private String listingValue;
    private String tally;
    private String tallyValue;
    private String qa;
    private String qaValue;
    private String errorClass;
    private String typeOfDoc;
    private String location;
    private String legiblity;
    private String images;
    private String imagePath;
    
    private boolean isCoderSelected;   
    private boolean isCheckerSelected;   
    private boolean isListingSelected;   
    private boolean isTallySelected;   
    private boolean isQaSelected;   
    
    //variable required while exporting excel sheet
    private java.util.Date reportDate;
    private String validity;
    private String comments;
    private List   imageList;
    private String xeroxComments;
    private String samplingused;
    private String coveredByQa;
    private String QALevel;
    private String remarks;
    private String dtys;   
    
    private List<String> selectedImageList;
    
    private List<String> multipleTallyList;
    
     private List<String> multipleTallyValueList;
     
     private  List<String> selectedTallier;
    
    public String getAuditProblem() {
        return auditProblem;
    }

    public void setAuditProblem(String auditProblem) {
        this.auditProblem = auditProblem;
    }

    public String getBatchId() {
        return batchId;
    }

    public void setBatchId(String batchId) {
        this.batchId = batchId;
    }

    public String getBoxId() {
        return boxId;
    }

    public void setBoxId(String boxId) {
        this.boxId = boxId;
    }

    public String getCorrectTagValue() {
        return correctTagValue;
    }

    public void setCorrectTagValue(String correctTagValue) {
        this.correctTagValue = correctTagValue;
    }

    public String getDocNumber() {
        return docNumber;
    }

    public void setDocNumber(String docNumber) {
        this.docNumber = docNumber;
    }

    public String getProcText() {
        return procText;
    }

    public void setProcText(String procText) {
        this.procText = procText;
    }

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

    public String getTagValue() {
        return tagValue;
    }

    public void setTagValue(String tagValue) {
        this.tagValue = tagValue;
    }

    public String getChecker() {
        return checker;
    }

    public void setChecker(String checker) {
        this.checker = checker;
    }

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

    public String getErrorClass() {
        return errorClass;
    }

    public void setErrorClass(String errorClass) {
        this.errorClass = errorClass;
    }

    public String getImages() {
        return images;
    }

    public void setImages(String images) {
        this.images = images;
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

    public static int getProjecId() {
        return projecId;
    }

    public static void setProjecId(int projecId) {
        Feedback.projecId = projecId;
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

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getQALevel() {
        return QALevel;
    }

    public void setQALevel(String QALevel) {
        this.QALevel = QALevel;
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

    public List getImageList() {
        return imageList;
    }

    public void setImageList(List imageList) {
        this.imageList = imageList;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getSamplingused() {
        return samplingused;
    }

    public void setSamplingused(String samplingused) {
        this.samplingused = samplingused;
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

    public Date getReportDate() {
        return reportDate;
    }

    public void setReportDate(Date reportDate) {
        this.reportDate = reportDate;
    }

    public List<String> getSelectedImageList() {
        return selectedImageList;
    }

    public void setSelectedImageList(List<String> selectedImageList) {
        this.selectedImageList = selectedImageList;
    }

    public boolean isCheckerSelected() {
        return isCheckerSelected;
    }

    public void setCheckerSelected(boolean isCheckerSelected) {
        this.isCheckerSelected = isCheckerSelected;
    }

    public boolean isCoderSelected() {
        return isCoderSelected;
    }

    public void setCoderSelected(boolean isCoderSelected) {
        this.isCoderSelected = isCoderSelected;
    }

    public boolean isListingSelected() {
        return isListingSelected;
    }

    public void setListingSelected(boolean isListingSelected) {
        this.isListingSelected = isListingSelected;
    }

    public boolean isQaSelected() {
        return isQaSelected;
    }

    public void setQaSelected(boolean isQaSelected) {
        this.isQaSelected = isQaSelected;
    }

    public boolean isTallySelected() {
        return isTallySelected;
    }

    public void setTallySelected(boolean isTallySelected) {
        this.isTallySelected = isTallySelected;
    }

    public List<String> getMultipleTallyList() {
        return multipleTallyList;
    }

    public void setMultipleTallyList(List<String> multipleTallyList) {
        this.multipleTallyList = multipleTallyList;
    }

    public List<String> getMultipleTallyValueList() {
        return multipleTallyValueList;
    }

    public void setMultipleTallyValueList(List<String> multipleTallyValueList) {
        this.multipleTallyValueList = multipleTallyValueList;
    }

    public List<String> getSelectedTallier() {
        return selectedTallier;
    }

    public void setSelectedTallier(List<String> selectedTallier) {
        this.selectedTallier = selectedTallier;
    }
    
    
}
