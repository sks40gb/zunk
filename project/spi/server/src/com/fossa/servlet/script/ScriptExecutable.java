/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fossa.servlet.script;

import com.fossa.servlet.writer.ValueData;

/**
 * The inteface <code>ScriptExecutable</code> makes the implemented class to able to validate and format the data.
 * Currently this is implemented by the class <code>Validator</code> and <code>Formatter</code>.
 * @see Validator
 * @see Formatter
 * @author sunil 
 */
public interface ScriptExecutable {

    /**
     * Run the script for the Field only. Therefore just pass all the parameters required for the field operation.
     * @param projectfieldsId  Field Id for which the script is needs to be executed.
     * @param fieldName        Field Name
     * @param valueData        Coded Value for the Field.
     * @param fieldType        Field Type
     * @return                 Return Error Message if the Script Function fails else return null.
     */
    public String executeField(int projectfieldsId, String fieldName, ValueData valueData, String fieldType);

    /**
     * Run the script for the document level. It requires for the volume Id for which document level script funtions
     * needs to be executed. It will get all the script function applied for the volume.
     * @param volumeId   Volume Id for which script will execute.
     * @return           Return error message if the script function fails else return null.
     */
    public String executeDocument(int volumeId);
    
}
