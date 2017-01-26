/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package script;

import bsh.Interpreter;
import common.Validation;
import common.edit.ProjectMapper;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * Class <code>Validator</code> is just for the validation for all the fields of a document.
 * This is called while saving the value into the database.
 * @see common.edit.ProjectEditor
 * @author sunil
 */
public class Validator {

    private ProjectMapper projectMap;
    private Map valueMap;
    private Map validationMap;    
    private boolean force;
    private String treatment_level;
    private String whichStatus;
    private static String fieldName;    
    private final String DOCUMENT_FUNCTIONS = "$DOCUMENT VALIDATION FUNCTION$";


    /**
     * Instantiate an object of class Validator with following parameters.
     * @param mapper           Project Mapper
     * @param valueMap         Value Mapper
     * @param validationMap    Validation Map having all validation funtions.
     * @param force            Force to save.
     * @param treatment_level  Treatement Level whether it is Level L1 or Level L2
     * @param whichStatus      Status or operation for which the validation is being performed.
     */
    public Validator(ProjectMapper mapper, Map valueMap, Map validationMap, boolean force, String treatment_level, String whichStatus) {
        this.projectMap = mapper;
        this.valueMap = valueMap;
        this.validationMap = validationMap;        
        this.force = force;
        this.treatment_level = treatment_level;        

        //reset the document data so that previous value should be erased.
        DocumentRecord.reset();
        //Add all the field name and value for the document.
        for (int i = 0; i < projectMap.getSize(); i++) {
            String name = projectMap.getFieldName(i);
            String value = (String) valueMap.get(name);
            FieldRecord.setCodedValue(value);
            FieldData fd = new FieldData().addName(name).addValue(value).addType("field_type");
            DocumentRecord.addField(fd);
        }
    }

    /**
     * Run the script file to check the each field of the document.
     * @return
     */
    public String run() {
        String error = null;
        //get all field and record of the project.
        for (int i = 0; i < projectMap.getSize(); i++) {
            String name = projectMap.getFieldName(i);

            fieldName = name;
            String value = (String) valueMap.get(name);

            //validate for each Fields
            error = validateForField(name, value);
            if (error != null) {
                return error;
            }
        }
        //valdiate for document.
        error = validateForDocument();
        if (error != null) {
            return error;
        }
        return null;
    }

    /**
     * Validate For the Field
     * @param name   Field Name
     * @param value  Coded Value of Field
     * @return       Error message if validation fails else return null.
     */
    private String validateForField(String name, String value) {
        String error = null;
        List validationList = (ArrayList) validationMap.get(name);
        //execute the validation for the field if it is Validatable.
        if (isValidatable(name)) {
            for (String input : getInputList(name, value)) {
                //validate for each input list coded by the user.
                FieldRecord.setCodedValue(input);
                if (validationList != null) {
                    error = validateFieldByAllFunction(validationList);
                }
            }
        }
        return error;
    }

    
    /**
     * Validate for the document. By using document level functions.
     * @return       Error message if validation fails else return null.
     */
    private String validateForDocument() {
        String error = null;
        //for document level validation the active Field will be blank.
        fieldName = "";
        List validationList = (ArrayList) validationMap.get(DOCUMENT_FUNCTIONS);
        //validate for each input list coded by the user.
        FieldRecord.setCodedValue(null);
        if (validationList != null) {
            error = validateFieldByAllFunction(validationList);
        }

        return error;
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
            //If there is no input for the field then assign a default value <code>""</code> as input.
            if(tokens.countTokens() == 0){
                inputList.add("");
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
                Validation functionData = (Validation) validation;
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
    private String validateFieldByOneFunction(Validation functionData)
    {
        //if there is no script function for the field or it is null then return null.
        if(functionData == null){
            return null;
        }
        
        String validationFunctions =
                " public script.DocumentRecord data = new script.DocumentRecord();" +
                " public String codedValue = data.getCodedValue();" +
                " public String execute(String methodName, String errorMessage,String param) {" +
                " String message = this.invokeMethod( methodName, new Object [] {errorMessage,param}); " +
                " return message;" +
                " }";

        String methodBody = functionData.getFunctionBody();
        String functionName = functionData.getFunctionName();
        String parameteres = functionData.getParameter();
        String error_message = functionData.getErrorMessage();
        StringTokenizer parametertokens = new StringTokenizer(parameteres, ",");        
        String param = null;

        Object error = null;
        Interpreter interpreter = new Interpreter();
        try {
            String scriptFileContent = validationFunctions + "\n" + methodBody;
            //execute the validation for each parameter of the function.
            //run the validation at least for once.
            do{
                //if there is no parameter for the validation function then assign a default value <code>""</code>
                param = (parametertokens.countTokens() == 0) ? "" : parametertokens.nextToken();
                
                System.out.println("param : " + param);
                error = interpreter.eval(scriptFileContent + "execute(\"" + functionName + "\",\"" + error_message + "\",\"" + param + "\")");
                if(error != null){
                    return error.toString();
                }
            }while (parametertokens.hasMoreTokens());
        } catch (Exception e) {
            e.printStackTrace();
            return error.toString();
        }
        //if the error is null return null else return the error.
        return error == null ? null : error.toString();
    }

    /**
     * Check whether field is Validatable to not.
     * This is calculated on the basis of field level and treatement level.
     * @param fieldName Field Name
     * @return true if the field is Validatable else return false.
     */
    private boolean isValidatable(String fieldName) {
        
        ProjectMapper.HashValue hv = projectMap.getHashValue(fieldName);        
        if (((hv.fieldLevel == 1 && treatment_level.equals("L1"))
                || (hv.fieldLevel == 0 && treatment_level.equals("L2")))
                && !hv.fieldName.equals("General Document Type")) {
            return true;
        }

        if (whichStatus != null && whichStatus.equals("Tally")) {
            return true;
        }
        return false;
    }

    /**
     * Get field name for which the validation is being performed.
     * @return Field Name
     */
    public static String getActiveFieldName(){
        return fieldName;
    }

}
