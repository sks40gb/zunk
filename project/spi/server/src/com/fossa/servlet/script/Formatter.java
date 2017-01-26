/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fossa.servlet.script;

import bsh.Interpreter;
import com.fossa.servlet.server.valueobjects.FunctionData;
import com.fossa.servlet.writer.ValueData;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Class <code>Formatter</code> is just for the fromatting the fields value of a docuemnt.
 * This is called while exporting the volumes record  to the files.
 * @see common.edit.ProjectEditor
 * @author sunil
 */
public class Formatter implements ScriptExecutable {

    private static final String INPUT_VALIDATION = "Input Validation";
    private static final String OUTPUT_FORMAT = "Output Format";
    private static final String OUTPUT_ERROR = "Output Error";
    private static final String OUTPUT_WARNING = "Output Warning";
    private Connection connection;
    private PreparedStatement pstmt;
    private String value;
    private String fieldName;
    private boolean isFieldValidation;

    /**
     * Craete an intance of class Formatter and remember the parameter.
     * @param connection Connection for DB operation.
     */
    public Formatter(Connection connection) {
        this.connection = connection;
    }

    /**
     * This method is used for formatting the values of fields at Field Level.
     * This is called for each field. It gets the list of scipt function of list and
     * get executed one by one.
     * @param projectfieldsId - Field Id for which the sctipt functions going to be executed.
     * @param fieldName       - Field Name
     * @param valueData       - Value coded for the Field.
     * @param fieldType       - Field Type
     * @return                - Return null at all cases. But it can be further implemented for input validation.
     */
    public String executeField(int projectfieldsId, String fieldName, ValueData valueData, String fieldType) {
        try {
            
            isFieldValidation = true;
            this.value = valueData.value;
            this.fieldName = fieldName;
            //Set the value to the FieldRecord so that it can reflect the value to main class when coded value changed
            //from the script function.
            FieldRecord.setValueData(valueData);           
            
            if(projectfieldsId < 0){
                return null;
            }
            
            String sql = "SELECT FM.function_name, FM.function_body, MD.error_message, MD.parameter, MD.type FROM validation_mapping_details MD " +
                    "INNER JOIN validation_mapping_master MM ON MM.validation_mapping_master_id = MD.validation_mapping_master_id  " +
                    "INNER JOIN validation_functions_master FM ON FM.validation_functions_master_id = MM.validation_functions_master_id " +
                    "WHERE MD.projectfields_id  = ? AND MD.status = 'true' AND FM.scope = ? AND FM.type = '" + OUTPUT_FORMAT + "' ";

            pstmt = connection.prepareStatement(sql);
            pstmt.setInt(1, projectfieldsId);
            pstmt.setString(2, "Field");
            ResultSet rs = pstmt.executeQuery();
            doFieldFormatting(rs);
        } catch (SQLException ex) {
            Logger.getLogger(Formatter.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }

    /**
     * This method is used for formatting all the fields of  a document at once.
     * It get all Document Level Script Functions applied to the document.l
     * @param volumeId - Volume Id
     * @return - null at all cases.
     */
    public String executeDocument(int volumeId) {
        try {
            isFieldValidation = false;
            String sql = "SELECT function_name, function_body, error_message, parameter, type " +
                    "FROM validation_functions_master WHERE project_id  = (select project_id from volume where volume_id = ? ) " +
                    "AND status = 'true' AND scope = ? AND type = '" + OUTPUT_FORMAT + "' ";
            pstmt = connection.prepareStatement(sql);
            pstmt.setInt(1, volumeId);
            pstmt.setString(2, "Document");
            ResultSet rs = pstmt.executeQuery();
            doDocumentFormatting(rs);
        } catch (SQLException ex) {
            Logger.getLogger(Formatter.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }

    /**
     * Run the script file to format the each field of the document.
     * @return
     */
    private String doDocumentFormatting(ResultSet rs) {
        String error = null;
        List validationList = getFormattingList(rs);
        //execute the formatting for the field if it is validatable.

        if (validationList != null) {
            error = formatFieldByAllFunction(validationList);
        }
        // if error occured while executing all the formatting function of all field then
        // stop execution and return error message.
        if (error != null) {
            return error;
        }
        return null;
    }

    /**
     * Run the script file to format the each field of the document.
     * @return
     */
    private String doFieldFormatting(ResultSet rs) {
        String error = null;
        List validationList = getFormattingList(rs);
        //execute the formatting for the field if it is Validatable.
        for (String input : getInputList(fieldName, value)) {
            //format for each input list coded by the user.
            FieldRecord.setCodedValue(input);
            if (validationList != null) {
                error = formatFieldByAllFunction(validationList);
            }
            // if error occured while executing all the formatting function of all field then
            // stop execution and return error message.
            if (error != null) {
                return error;
            }
        }

        return null;
    }

    private List getFormattingList(ResultSet rs) {
        List list = new ArrayList();
        FunctionData fd = null;
        try {
            while (rs.next()) {
                fd = new FunctionData();
                fd.setFunctionName(rs.getString(1));
                fd.setFunctionBody(rs.getString(2));
                fd.setErrorMessage(rs.getString(3));
                fd.setParameter(rs.getString(4));
                list.add(fd);
            }
        } catch (SQLException ex) {
            Logger.getLogger(Formatter.class.getName()).log(Level.SEVERE, null, ex);
        }
        return list;
    }

    /**
     * Separate the input field by Delimiter depend upons the field name and put
     * each input value to the <code>List<code>.
     * @param name   Field name
     * @param value  Input value coded by the user.
     * @return       List of values after separation.
     */
    private List<String> getInputList(String name, String value) {

        List<String> inputList = new ArrayList<String>();
        if (null != value) {
            StringTokenizer tokens = null;
            //if field name is AUTHOR,FIRSTNAME,LASTNAME,MIDDLEINITIAL,CC,RE - then
            //input will be saparated by ( ; & ,) else only by ( ; )
            if (name.equalsIgnoreCase("AUTHOR") ||
                    name.equalsIgnoreCase("FIRSTNAME") ||
                    name.equalsIgnoreCase("LASTNAME") ||
                    name.equalsIgnoreCase("MIDDLEINITIAL") ||
                    name.equalsIgnoreCase("CC") ||
                    name.equalsIgnoreCase("RE")) {
                tokens = new StringTokenizer(value, ";,");
            } //if the field type is not name, the input will be saparated by "," only .
            else {
                tokens = new StringTokenizer(value, ";");
            }

            while (tokens.hasMoreTokens()) {
                inputList.add(tokens.nextToken());
            }
        }
        return inputList;
    }

    /**
     * Format a field by different script functions enabled for this field.
     * @param validationList List of validation function record.
     * @return <code>NULL</code> if no error occured while Field Formatting else return error message.
     */
    private String formatFieldByAllFunction(List validationList) {
        String error = null;
        for (Object validation : validationList) {
            if (validation != null) {
                FunctionData functionData = (FunctionData) validation;
                error = formatFieldByOneFunction(functionData);
                // if the formatting of the field by a function is failed then stop validating and return
                // the error occured.
                if (error != null) {
                    return error;
                }
            }
        }
        return null;
    }

    /**
     * Formatting a Field value by one Formatting Script Function.
     * @param FunctionData Function record like function name, function body, error message, parameters.
     * @return Null if the function is executed successfully else return error message.
     */
    private String formatFieldByOneFunction(FunctionData functionData) {
        System.out.println("FUNCTION NAME    :  " + functionData.getFunctionName());
        //if there is no script function for the field or it is null then return null.
        if (functionData == null) {
            return null;
        }

        String validationFunctions = getDummyFunction();

        String methodBody = functionData.getFunctionBody();
        String functionName = functionData.getFunctionName();
        String parameteres = functionData.getParameter();
        String error_message = functionData.getErrorMessage();
        StringTokenizer parametertokens = new StringTokenizer(parameteres, ",");
        if(parametertokens.countTokens() == 0){
            parametertokens = new StringTokenizer(" ", ",");
        }
        String param = null;

        Object error = null;
        Interpreter interpreter = new Interpreter();
        try {
            String scriptFileContent = validationFunctions + "\n" + methodBody;
            //execute the validation for each parameter of the function.
            while (parametertokens.hasMoreTokens()) {
                param = parametertokens.nextToken();
                System.out.println("param : " + param);
                error = interpreter.eval(scriptFileContent + "execute(\"" + functionName + "\",\"" + error_message + "\",\"" + param + "\")");

                //getnerate the report
                ValidationReport report = new ValidationReport();
                report.setFieldName(fieldName);
                report.setFieldValue(value);
                report.setErrorMessage(error_message);
                report.setFunctionName(functionName);

            }
        } catch (Exception e) {
            e.printStackTrace();
            return error.toString();
        }
        //if the error is null return null else return the error.
        return error == null ? null : error.toString();
    }

    /**
     * Create a default function in string format to pass in script while executing the script functions.
     * @return
     */
    private String getDummyFunction() {
        String txt = "";
        if (isFieldValidation) {
            txt =
                    " public com.fossa.servlet.script.FieldRecord data = new com.fossa.servlet.script.FieldRecord();" +
                    " public String codedValue = data.getCodedValue();" +
                    " public String execute(String methodName, String errorMessage,String param) {" +
                    " String message = this.invokeMethod( methodName, new Object [] {errorMessage,param}); " +
                    " return message;" +
                    " }";
        } else {
            txt =
                    " public com.fossa.servlet.script.DocumentRecord data = new com.fossa.servlet.script.DocumentRecord();" +
                    " public String execute(String methodName, String errorMessage,String param) {" +
                    " String message = this.invokeMethod( methodName, new Object [] {errorMessage,param}); " +
                    " return message;" +
                    " }";
        }
        return txt;
    }

}
