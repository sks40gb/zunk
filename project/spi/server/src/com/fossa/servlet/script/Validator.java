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
 * Class <code>Validator</code> is just for the validation for all the fields of a document.
 * This is called while saving the value into the database.
 * @see common.edit.ProjectEditor
 * @author sunil
 */
public class Validator implements ScriptExecutable {

    private static final String INPUT_VALIDATION = "Input Validation";
    private static final String OUTPUT_FORMAT = "Output Format";
    private static final String OUTPUT_ERROR = "Output Error";
    private static final String OUTPUT_WARNING = "Output Warning";
    private static List<ValidationReport> reportList;
    private Connection connection;
    private PreparedStatement pstmt;
    private String value;
    private String fieldName;
    private boolean isFieldValidation;
    private static boolean isError =  false;

    /**
     * Create an instance of class Validator with followin parameters.
     * @param connection Connection to perform the DB operation.
     */
    public Validator(Connection connection) {
        this.connection = connection;
        reportList = new ArrayList<ValidationReport>();
        isError = false;
    }

    public String executeField(int projectfieldsId, String fieldName, ValueData valueData, String fieldType) {
        try {
            isFieldValidation = true;
            this.value = valueData.value;
            this.fieldName = fieldName;
            FieldRecord.setCodedValue(value);
            
            FieldRecord.setValueData(null);
            
            if(projectfieldsId < 0 ){
                return null;
            }
            
            String sql = "SELECT FM.function_name, FM.function_body, MD.error_message, MD.parameter, MD.type FROM validation_mapping_details MD " +
                    "INNER JOIN validation_mapping_master MM ON MM.validation_mapping_master_id = MD.validation_mapping_master_id  " +
                    "INNER JOIN validation_functions_master FM ON FM.validation_functions_master_id = MM.validation_functions_master_id " +
                    "WHERE MD.projectfields_id  = ? AND MD.status = 'true' AND FM.scope = ? AND ( FM.type = '" + OUTPUT_ERROR + "' OR " +
                    "FM.type = '" + OUTPUT_WARNING + "' ) ";

            pstmt = connection.prepareStatement(sql);
            pstmt.setInt(1, projectfieldsId);
            pstmt.setString(2, "Field");
            ResultSet rs = pstmt.executeQuery();
            doFieldValidation(rs);
        } catch (SQLException ex) {
            Logger.getLogger(Validator.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }

    public String executeDocument(int volumeId) {
        try {
            
            isFieldValidation = false;
            String sql = "SELECT function_name, function_body, error_message, parameter, type " +
                    "FROM validation_functions_master WHERE project_id  = (select project_id from volume where volume_id = ? ) " +
                    "AND status = 'true' AND scope = ? AND ( type = '" + OUTPUT_ERROR + "' OR type = '" + OUTPUT_WARNING + "' ) ";

            pstmt = connection.prepareStatement(sql);
            pstmt.setInt(1, volumeId);
            pstmt.setString(2, "Document");
            ResultSet rs = pstmt.executeQuery();
            doDocumentValidation(rs);
        } catch (SQLException ex) {
            Logger.getLogger(Validator.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }

    /**
     * Run the script file to check the each field of the document.
     * @return
     */
    private String doDocumentValidation(ResultSet rs) {
        String error = null;
        List validationList = getValidationList(rs);
        //execute the validation for the field if it is Validatable.

        if (validationList != null) {
            error = validateFieldByAllFunction(validationList);
        }
        // if error occured while executing all the validation function of all field then
        // stop execution and return error message.
        if (error != null) {
            return error;
        }
        return null;
    }

    /**
     * Run the script file to check the each field of the document.
     * @return
     */
    private String doFieldValidation(ResultSet rs) {
        String error = null;
        List validationList = getValidationList(rs);
        //execute the validation for the field if it is Validatable.
        for (String input : getInputList(fieldName, value)) {
            //validate for each input list coded by the user.
            System.out.println("input              :  " + input);
            FieldRecord.setCodedValue(input);
            if (validationList != null) {
                error = validateFieldByAllFunction(validationList);
            }
            // if error occured while executing all the validation function of all field then
            // stop execution and return error message.
            if (error != null) {
                return error;
            }
        }

        return null;
    }

    private List getValidationList(ResultSet rs) {
        List list = new ArrayList();
        FunctionData fd = null;
        try {
            while (rs.next()) {
                fd = new FunctionData();
                fd.setFunctionName(rs.getString(1));
                fd.setFunctionBody(rs.getString(2));
                fd.setErrorMessage(rs.getString(3));
                fd.setParameter(rs.getString(4));
                fd.setType(rs.getString(5));
                list.add(fd);
            }
        } catch (SQLException ex) {
            Logger.getLogger(Validator.class.getName()).log(Level.SEVERE, null, ex);
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
     * Validate a field by different script functions enabled for this field.
     * @param validationList List of validation function record.
     * @return <code>NULL</code> if no error occured while field validation else return error message.
     */
    private String validateFieldByAllFunction(List validationList) {
        String error = null;
        for (Object validation : validationList) {
            if (validation != null) {
                FunctionData functionData = (FunctionData) validation;
                error = validateFieldByOneFunction(functionData);
                // if the validation of the field by a function is failed then stop validating and retrun
                // the error occured.
                if (error != null) {
                    return error;
                }
            }
        }
        return null;
    }

    /**
     * Validate the function for the field.
     * @param functionData Function record like function name, function body, error message, parameters.
     * @return Null if the function is executed successfully else return error message.
     */
    private String validateFieldByOneFunction(FunctionData functionData) {
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

                //Add validation report to the report list.
                if (error != null) {
                    ValidationReport report = new ValidationReport();
                    report.setFieldName(fieldName);
                    report.setFieldValue(value);
                    report.setErrorMessage(error_message);
                    report.setFunctionName(functionName);
                    report.setErrorType(functionData.getType());
                    isError = functionData.getType().equals(OUTPUT_ERROR);
                    report.setDocumentNumber(DocumentRecord.getDocumentNumber());
                    report.setBatchNumber(Integer.toString(DocumentRecord.getBatchNumber()));
                    reportList.add(report);
                }
            //--------------------------------------------

            }
        } catch (Exception e) {
            e.printStackTrace();
            return error.toString();
        }
        //if the error is null return null else return the error.
        return error == null ? null : error.toString();
    }

    /**
     * Get the Dummmy Function required to pass to BSF to run the validation functions.
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

    /**
     * Get the validation report list. If the script validation fails for any coded value then error report is listed 
     * in the reprotList.
     * @return Report List
     */
    public static List<ValidationReport> getReportList() {
        return reportList;
    }

    /**
     * Clear all the values from the Report List.
     */
    public static void resetReportList() {
        reportList = new ArrayList<ValidationReport>();
    }
    
    public static boolean hasError(){
        return isError;
    }
}
