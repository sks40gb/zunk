/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package common;

/**
 *Class to recieve QAInspectionReportData
 * @author Prakasha
 */
public class QAInspectionReportData {
    
    //Variable Declaration
    public int projectId;
    public int volumeId;
    public int rejectionNo;
    public int samplingId;
    public String samplingMethod = "";
    public String samplingType = "";
    public String accuracyRequired;
    public String QALevel = "";    
    public String AQL_Value = "";
    public int correctionDataCount;
    public String projectName = "";
    public String volumeName = "";
    public int documentCount;
    public int fieldCount;
    public int tagCount;
    public int sampledDocumentCount;
    public int sampledFieldCount;
    public String sampledTagCount;
    public int numberOfDocWithError;
    public int numberOfFieldWithError;
    public int numberOfTagWithError;
    public int numberOfMisCoded;
    public int numberOfUnCoded;
    public int numberOfAdded;
    
    public String getAQL_Value() {
        return AQL_Value;
    }

    public int getSamplingId() {
        return samplingId;
    }

    public void setAQL_Value(String AQL_Value) {
        this.AQL_Value = AQL_Value;
    }

    public String getQALevel() {
        return QALevel;
    }

    public void setQALevel(String QALevel) {
        this.QALevel = QALevel;
    }

    public String getAccuracyRequired() {
        return accuracyRequired;
    }

    public void setAccuracyRequired(String accuracyRequired) {
        this.accuracyRequired = accuracyRequired;
    }

    public int getCorrectionDataCount() {
        return correctionDataCount;
    }

    public void setCorrectionDataCount(int correctionDataCount) {
        this.correctionDataCount = correctionDataCount;
    }

    public int getDocumentCount() {
        return documentCount;
    }

    public void setDocumentCount(int documentCount) {
        this.documentCount = documentCount;
    }

    public int getFieldCount() {
        return fieldCount;
    }

    public void setFieldCount(int fieldCount) {
        this.fieldCount = fieldCount;
    }

    public int getNumberOfAdded() {
        return numberOfAdded;
    }

    public void setNumberOfAdded(int numberOfAdded) {
        this.numberOfAdded = numberOfAdded;
    }

    public int getNumberOfDocWithError() {
        return numberOfDocWithError;
    }

    public void setNumberOfDocWithError(int numberOfDocWithError) {
        this.numberOfDocWithError = numberOfDocWithError;
    }

    public int getNumberOfFieldWithError() {
        return numberOfFieldWithError;
    }

    public void setNumberOfFieldWithError(int numberOfFieldWithError) {
        this.numberOfFieldWithError = numberOfFieldWithError;
    }

    public int getNumberOfMisCoded() {
        return numberOfMisCoded;
    }

    public void setNumberOfMisCoded(int numberOfMisCoded) {
        this.numberOfMisCoded = numberOfMisCoded;
    }

    public int getNumberOfTagWithError() {
        return numberOfTagWithError;
    }

    public void setNumberOfTagWithError(int numberOfTagWithError) {
        this.numberOfTagWithError = numberOfTagWithError;
    }

    public int getNumberOfUnCoded() {
        return numberOfUnCoded;
    }

    public void setNumberOfUnCoded(int numberOfUnCoded) {
        this.numberOfUnCoded = numberOfUnCoded;
    }

    public int getProjectId() {
        return projectId;
    }

    public void setProjectId(int projectId) {
        this.projectId = projectId;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public int getRejectionNo() {
        return rejectionNo;
    }

    public void setRejectionNo(int rejectionNo) {
        this.rejectionNo = rejectionNo;
    }

    public int getSampledDocumentCount() {
        return sampledDocumentCount;
    }

    public void setSampledDocumentCount(int sampledDocumentCount) {
        this.sampledDocumentCount = sampledDocumentCount;
    }

    public int getSampledFieldCount() {
        return sampledFieldCount;
    }

    public void setSampledFieldCount(int sampledFieldCount) {
        this.sampledFieldCount = sampledFieldCount;
    }

    public String getSampledTagCount() {
        return sampledTagCount;
    }

    public void setSampledTagCount(String sampledTagCount) {
        this.sampledTagCount = sampledTagCount;
    }

    public String getSamplingMethod() {
        return samplingMethod;
    }

    public void setSamplingMethod(String samplingMethod) {
        this.samplingMethod = samplingMethod;
    }

    public String getSamplingType() {
        return samplingType;
    }

    public void setSamplingType(String samplingType) {
        this.samplingType = samplingType;
    }

    public int getTagCount() {
        return tagCount;
    }

    public void setTagCount(int tagCount) {
        this.tagCount = tagCount;
    }

    public int getVolumeId() {
        return volumeId;
    }

    public void setVolumeId(int volumeId) {
        this.volumeId = volumeId;
    }

    public String getVolumeName() {
        return volumeName;
    }

    public void setVolumeName(String volumeName) {
        this.volumeName = volumeName;
    }
    

}
