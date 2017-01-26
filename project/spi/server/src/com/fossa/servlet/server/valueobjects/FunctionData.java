/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.fossa.servlet.server.valueobjects;

import com.fossa.servlet.command.PostValidation;
import com.fossa.servlet.common.SQLQueries;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import org.apache.log4j.Logger;

/**
 *
 * @author sunil
 */

public class FunctionData {
  private int functionId;
    private String functionName;
    private String description;
    private String functionBody;
    private String errorMessage;
    private String parameter;
    private String status;
    private String type;
    PreparedStatement pstmt = null;
    ResultSet rs = null;
    private Connection connection;
    private static Logger logger = Logger.getLogger("com.fossa.servlet.server.valueobjects");
    /**
     * Set the functionId for the further operation
     * Get the connection.
     * @param functionId - Functions Id 
     */
    public FunctionData() {

    }
    public FunctionData(int functionId) {
        this.functionId = functionId;
        connection = PostValidation.getConnection();
        //fill initial required data like function name, function body, error message etc.
        fillInitialData();

    }

    private void fillInitialData() {
        try {
            //String sql = "SELECT DISTINCT FM.function_name,FM.description,     coalesce(MD.error_message,FM.error_message) as \"ifnull(MD.error_message,FM.error_message)\",  coalesce(MD.status, 'False') as \"ifnull(MD.status, 'False')\",  MD.parameter,MD.validation_mapping_details_id,FM.validation_functions_master_id,  FG.group_name,FG.validation_functions_group_id  FROM validation_mapping_details MD   INNER JOIN validation_mapping_master MM    ON MD.validation_mapping_master_id = MM.validation_mapping_master_id      LEFT OUTER JOIN validation_functions_group FG    ON MM.validation_functions_group_id = FG.validation_functions_group_id      INNER JOIN validation_functions_master FM    ON MM.validation_functions_master_id = FM.validation_functions_master_id WHERE MD.projectfields_id = ? UNION  SELECT    DISTINCT FM.function_name,FM.description,   coalesce(MD.error_message,FM.error_message) as \"ifnull(MD.error_message,FM.error_message)\",   coalesce(MD.status, 'False') as \"ifnull(MD.status, 'False')\",   MD.parameter,MD.validation_mapping_details_id,FM.validation_functions_master_id,    FG.group_name, FG.validation_functions_group_id  FROM projectfields PF   INNER JOIN validation_mapping_master MM    ON PF.validation_functions_group_id = MM.validation_functions_group_id     AND PF.projectfields_id = ?      INNER JOIN validation_functions_master FM          ON MM.validation_functions_master_id = FM.validation_functions_master_id      LEFT OUTER JOIN validation_mapping_details MD    ON MM.validation_mapping_master_id = MD.validation_mapping_master_id    AND MD.projectfields_id = ?     INNER JOIN validation_functions_group FG    ON MM.validation_functions_group_id = FG.validation_functions_group_id";

            pstmt = connection.prepareStatement(SQLQueries.SEL_VALIDATION_FUNCTIONS_MASTER_COMP_PVR);
            pstmt.setInt(1, functionId);
            pstmt.executeQuery();
            rs = pstmt.getResultSet();
            if (rs.next()) {
                functionName = rs.getString(1);
                description = rs.getString(2);
                functionBody = rs.getString(3);
                errorMessage = rs.getString(4);
            }
            rs.close();
            pstmt.close();
        } catch (Exception e) {
            logger.error("Exception while filling the initial data in FunctionData." + e);
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            logger.error(sw.toString());
        }
    }

    /**
     * Get Description
     * @return - Description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Set the description for the function.
     * @param description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Get the error message of a function.
     * @return - error message
     */
    public String getErrorMessage() {
        return errorMessage;
    }

    /**
     * Set the error message for the functions.
     * @param errorMessage -  error message.
     */
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    /**
     * Get the function body for the method
     * @return - function body
     */
    public String getFunctionBody() {
        return functionBody;
    }

    /**
     * Set the function body
     * @param functionBody - Function Body
     */
    public void setFunctionBody(String functionBody) {
        this.functionBody = functionBody;
    }

    /**
     * Get Functions master Id 
     * @return - Function Id
     */
    public int getFunctionId() {
        return functionId;
    }

    /**
     * Set function id
     * @param functionId - function id
     */
    public void setFunctionId(int functionId) {
        this.functionId = functionId;
    }

    /**
     * Get function name
     * @return - Function Name
     */
    public String getFunctionName() {
        return functionName;
    }

    /**
     * Set the function name 
     * @param functionName - Function Name
     */
    public void setFunctionName(String functionName) {
        this.functionName = functionName;
    }

    /**
     * Get the parameter for the function.
     * @return - parameter for the function
     */
    public String getParameter() {
        return (parameter == null) ? "" : parameter;
    }

    /**
     * set Parameter for the function
     * @param - parameter of function
     */
    public void setParameter(String parameter) {
        this.parameter = parameter;
    }

    /**
     * Get the status of function whether it is in true or false status.
     * @return - status
     */
    public String getStatus() {
        return status;
    }

    /**     
     * Set the status for the function
     * @param status - Function Status
     */
    public void setStatus(String status) {
        this.status = status;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }


}
