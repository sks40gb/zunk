/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fossa.servlet.command;

import com.fossa.servlet.common.CommonLogger;
import org.w3c.dom.Element;
import com.fossa.servlet.dao.DBTask;
import com.fossa.servlet.common.msg.MessageWriter;
import com.fossa.servlet.session.UserTask;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;

/**
 * This class returns all validation functions records for a field.
 * @author sunil
 */
public class Command_view_advance_validations implements Command {

    private final int SELECT_ALL = -2;
    private final String FIELD = "Field";
    private final String DOCUMENT = "Document";
    private final String VOLUME = "Volume";

    int columnCount = 0;
    private int fieldId;
    private int projectId;
    private String scope;

    public String execute(Element action, UserTask user, DBTask dbTask, MessageWriter writer) {
        try {
            fieldId = Integer.parseInt(action.getAttribute(A_FIELD_ID));
            projectId = Integer.parseInt(action.getAttribute(A_PROJECT_ID));
            scope = action.getAttribute(A_FUNCTION_SCOPE);

            Connection con = dbTask.getConnection();
            Statement st = dbTask.getStatement();
            PreparedStatement getValidationFunctionsPrepStmt = null;
            String query;
            
            //"Function Name", "Description", "Type", "Error Message","Error Type", "Status", "Parameter"
            if (SELECT_ALL == fieldId) {
                query = "SELECT DISTINCT FM.function_name,FM.description,FM.error_message, FM.function_body " +
                        "FROM validation_functions_master FM";
                getValidationFunctionsPrepStmt = con.prepareStatement(query);
            } else if(scope.equals(FIELD)){
                
                query = " SELECT DISTINCT " +
                                  " FM.function_name," +
                                  " FM.description," +
                                  " coalesce(MD.type,FM.type) as \"ifnull(MD.type,FM.error_type)\",  " +
                                  " coalesce(MD.error_message,FM.error_message) as \"ifnull(MD.error_message,FM.error_message)\",  " +
                                  " coalesce(MD.status, 'False') as \"ifnull(MD.status, 'False')\"," +
                                  " MD.parameter," +
                                  " MD.validation_mapping_details_id," +
                                  " FM.validation_functions_master_id," +
                                  " FG.group_name," +
                                  " FG.validation_functions_group_id " +
                        " FROM validation_mapping_details MD " +
                        " INNER JOIN validation_mapping_master MM ON MD.validation_mapping_master_id = MM.validation_mapping_master_id " +
                        " LEFT OUTER JOIN validation_functions_group FG ON MM.validation_functions_group_id = FG.validation_functions_group_id " +
                        " INNER JOIN validation_functions_master FM ON MM.validation_functions_master_id = FM.validation_functions_master_id " +
                        " WHERE MD.projectfields_id = ? AND FM.scope = '" + FIELD + "'" +
                        
                        " UNION  " +
                        
                        " SELECT DISTINCT " +
                                  " FM.function_name," +
                                  " FM.description," +
                                  " coalesce(MD.type,FM.type) as \"ifnull(MD.type,FM.type)\",   " +
                                  " coalesce(MD.error_message,FM.error_message) as \"ifnull(MD.error_message,FM.error_message)\",   " +
                                  " coalesce(MD.status, 'False') as \"ifnull(MD.status, 'False')\",   " +
                                  " MD.parameter," +
                                  " MD.validation_mapping_details_id," +
                                  " FM.validation_functions_master_id,    " +
                                  " FG.group_name, " +
                                  " FG.validation_functions_group_id " +
                        " FROM projectfields PF   INNER JOIN validation_mapping_master MM " +
                        " ON PF.validation_functions_group_id = MM.validation_functions_group_id " +
                        " AND PF.projectfields_id = ? INNER JOIN validation_functions_master FM " +
                        " ON MM.validation_functions_master_id = FM.validation_functions_master_id " +
                        " LEFT OUTER JOIN validation_mapping_details MD  ON MM.validation_mapping_master_id = " +
                        " MD.validation_mapping_master_id AND MD.projectfields_id = ? " +
                        " INNER JOIN validation_functions_group FG " +
                        " ON MM.validation_functions_group_id = FG.validation_functions_group_id" +
                        " WHERE FM.scope = '" + FIELD +"'";

                getValidationFunctionsPrepStmt = con.prepareStatement(query);
                getValidationFunctionsPrepStmt.setInt(1, fieldId);
                getValidationFunctionsPrepStmt.setInt(2, fieldId);
                getValidationFunctionsPrepStmt.setInt(3, fieldId);
            }else if(scope.equals(DOCUMENT) || scope.equals(VOLUME)){
                System.out.println("scope ==========> " + scope);
                query = "SELECT DISTINCT " +
                        "function_name, " +
                        "description, " +
                        "type, " +
                        "error_message, " +
                        "status, " +
                        "parameter, " +
                        "'0', " +
                        "validation_functions_master_id, " +
                        "'0'," +
                        "'0'" +
                        " FROM validation_functions_master WHERE scope = ? AND project_id = ?";
                getValidationFunctionsPrepStmt = con.prepareStatement(query);
                getValidationFunctionsPrepStmt.setString(1, scope);
                getValidationFunctionsPrepStmt.setInt(2, projectId);
            }
            getValidationFunctionsPrepStmt.executeQuery();
            ResultSet getValdnFuncResultSet = getValidationFunctionsPrepStmt.getResultSet();
            ResultSetMetaData getColCountRsultySetMetaDeta = getValdnFuncResultSet.getMetaData();
            columnCount = getColCountRsultySetMetaDeta.getColumnCount();
            Command_sql_query.writeXmlFromResult(user, getValdnFuncResultSet, writer);
            getValdnFuncResultSet.close();

        } catch (Exception exc) {
            CommonLogger.printExceptions(this, "Exception while viewing the advance validations." , exc);
        }
        return null;
    }

    public boolean isReadOnly() {
        return true;
    }
}

