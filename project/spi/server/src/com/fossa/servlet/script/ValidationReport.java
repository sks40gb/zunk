/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fossa.servlet.script;

/**
 * Class <code>ValidationReport</code> contains all the Field required to generate the Validation Report.
 * This class is used while running the validation. If the validation fails the error message and value for
 * which the validation is failed saved as this class along with some other informations so that it can
 * generate complete validation report.
 * @author sunil
 */
public class ValidationReport {

    /** Field Name */
    private String fieldName;
    /** Field Value */
    private String fieldValue;
    /** Function Name */
    private String functionName;
    /** Error Type */
    private String errorType;
    /** Error Message */
    private String errorMessage;
    /** Batch Number */
    private String batchNumber;
    /** Document Number */
    private String documentNumber;

    /**
     * Get the Batch Number for the document. 
     * @return Batch Number
     */
    public String getBatchNumber() {
        return batchNumber;
    }

    /**
     * Set the Batch Number
     * @param batchNumber Batch Number
     */
    public void setBatchNumber(String batchNumber) {
        this.batchNumber = batchNumber;
    }

    /**
     * Get the Document Number
     * @return Document Number
     */
    public String getDocumentNumber() {
        return documentNumber;
    }

    /**
     * Set the Document Number
     * @param documentNumber 
     */
    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }

    /**
     * Get the Error Message for the Field Value.
     * @return Error Message.
     */
    public String getErrorMessage() {
        return errorMessage;
    }

    /**
     * Set the Error Message
     * @param errorMessage
     */
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    /**
     * Get the Error Type whether it is just Warning or Error
     * @return Error Type
     */
    public String getErrorType() {
        return errorType;
    }

    /**
     * Set Error Type 
     * @param errorType
     */
    public void setErrorType(String errorType) {
        this.errorType = errorType;
    }

    /**
     * Get the Field Name for which the validation is failed.
     * @return Field Name
     */
    public String getFieldName() {
        return fieldName;
    }

    /**
     * Set the Field Name 
     * @param fieldName Field Name
     */
    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    /**
     * Get Field Value for which the validation is failed.
     * @return Field Value
     */
    public String getFieldValue() {
        return fieldValue;
    }

    /**
     * Set the Field Value
     * @param fieldValue Field value
     */
    public void setFieldValue(String fieldValue) {
        this.fieldValue = fieldValue;
    }

    /**
     * Get the Function which validated the coded value of field.
     * @return
     */
    public String getFunctionName() {
        return functionName;
    }

    /**
     * Set the Function Name
     * @param functionName Function Name
     */
    public void setFunctionName(String functionName) {
        this.functionName = functionName;
    }
}
