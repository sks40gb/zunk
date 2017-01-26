/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fossa.servlet.script;

import com.fossa.servlet.writer.ValueData;

/**
 * Class FieldRecord cantains all the record related to the Field.
 * This state of this class is saved in SPiCA java file and that value is used while
 * running the Field Level Scipt for the validatio and formatting. 
 * 
 * @author sunil
 */
public class FieldRecord {

    /** Value Data contains the coded value, Field Name, sequence of field 
     *  This value is set while running the format script.     
     */
    private static ValueData valueData;
    /** Field value  for which the validation ot formatter function is going to be executed */
    private static String codedValue;

    /**
     * Set the ValueData. This method is used for formatting the data not for validating the coded value 
     * or document record. For validation set teh null to the ValueData because this value is reflects from
     * the scipt and that should be in case of formatting only not in any kind of validation.
     * @param valueData ValueData
     */
    public static synchronized void setValueData(ValueData valueData) {
        FieldRecord.valueData = valueData;
        if(valueData != null){
            FieldRecord.codedValue = valueData.value;
        }
    }

    /**
     * Reset all Field records.
     */
    public static synchronized void reset() {
        codedValue = null;
        valueData = null;
    }

    /**
     * Get the coded value for the field for which the function is being applied.
     * @return Coded value of the field.
     */
    public static synchronized String getCodedValue() {
        return codedValue == null ? "" : codedValue;
    }

    /**
     * Set the coded value for the field.
     * @param codedValue - Coded Value
     */
    public static synchronized void setCodedValue(String value) {
        codedValue = value;
        if (valueData != null) {
            valueData.value = value;
        }
    }
}
