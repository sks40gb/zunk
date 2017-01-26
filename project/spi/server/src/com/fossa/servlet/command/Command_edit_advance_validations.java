/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fossa.servlet.command;

import com.fossa.servlet.common.CommonLogger;
import com.fossa.servlet.common.ValidationsData;
import java.sql.ResultSetMetaData;
import com.fossa.servlet.common.msg.MessageWriter;
import com.fossa.servlet.common.msg.XmlReader;
import com.fossa.servlet.dao.DBTask;
import com.fossa.servlet.session.UserTask;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * This class handles editing the validations.
 * @author sunil
 */
public class Command_edit_advance_validations implements Command {

    private final String DOCUMENT = "Document";
    private final String VOLUME = "Volume";
    private PreparedStatement pst;
    private int columnCount = 0;
    private String edit_or_display;
    private String function_name;
    private String desc;
    private String error_message;
    private String parameter;
    private String fun_status;
    private String function_body;
    private String type;
    private String scope;
    private int std_group_id;
    private int fieldId;
    private int validation_mapping_details_id;
    private int validation_functions_master_id;

    public String execute(Element action, UserTask user, DBTask dbTask, MessageWriter writer) {
        try {

            Element givenValueList = action;
            Node firstChild = givenValueList.getFirstChild();
            while (firstChild != null && firstChild.getNodeType() != Node.ELEMENT_NODE) {
                firstChild = firstChild.getNextSibling();
            }
            if (firstChild != null) {
                ValidationsData data = new ValidationsData();
                // fill in the int and String fields of the UsersData
                XmlReader reader = new XmlReader();

                try {
                    reader.decode(givenValueList, data);
                } catch (IOException ex) {
                    CommonLogger.printExceptions(this, "Exception while decoding the XMLReader.", ex);
                }

                fieldId = data.fieldId;
                validation_mapping_details_id = data.validation_mapping_details_id;
                validation_functions_master_id = data.validation_functions_master_id;
                edit_or_display = data.editOrDisplay;
                function_name = data.functionName;
                desc = data.description;
                error_message = data.errorMessage;
                parameter = data.userInput;
                fun_status = data.status;
                function_body = data.methodBody;
                type = data.type;
                scope = data.scope;
                std_group_id = data.std_group_id;

                //if  edit_or_display is equals to true, it will fetch data to display in edit mode of the function.                
                if (edit_or_display.equalsIgnoreCase("true")) {
                    display(action, user, dbTask, writer, fieldId, validation_mapping_details_id, validation_functions_master_id);
                } else {
                    edit(action, user, dbTask, writer, data);
                }
            }
        } catch (Exception exc) {
            CommonLogger.printExceptions(this, "Exception while editing the advance validations.", exc);
        }
        return null;
    }

    //displaying the records of selected functions corresponding to selected field.
    private void display(Element action, UserTask usertask, DBTask dbTask, MessageWriter writer, int fieldId, int validation_mapping_details_id, int validation_functions_master_id) throws IOException {
        try {
            Connection con = dbTask.getConnection();
            if (scope.equals(DOCUMENT) || scope.equals(VOLUME)) {                
                getDocumentOrVolumeFunctionsRecord(con);
            } else {                
                getFieldFunctionsRecord(con);
            }

            pst.executeQuery();
            ResultSet getValidationDetailsResultSet = pst.getResultSet();
            ResultSetMetaData getColumnCountResultSetMetaData = getValidationDetailsResultSet.getMetaData();
            columnCount = getColumnCountResultSetMetaData.getColumnCount();
            Command_sql_query.writeXmlFromResult(usertask, getValidationDetailsResultSet, writer);
            pst.close();
        } catch (SQLException e) {
            CommonLogger.printExceptions(this, "Exception while selecting the functions of validations.", e);
        }
    }

    private void getDocumentOrVolumeFunctionsRecord(Connection con) throws SQLException{
        pst = con.prepareStatement("SELECT function_name, description, type, error_message, parameter,status,function_body FROM validation_functions_master WHERE validation_functions_master_id = ?");
        pst.setInt(1, validation_functions_master_id);
    }

    private void getFieldFunctionsRecord(Connection con) throws SQLException{
                    if (validation_mapping_details_id > 0) {
                pst = con.prepareStatement("SELECT " +
                                                "FM.function_name," +
                                                "FM.description," +
                                                "MD.type," +
                                                "MD.error_message, " +
                                                "MD.parameter, " +
                                                "coalesce(MD.status, 'False') as \"ifnull(MD.status, 'False')\", " +
                                                "FM.function_body " +
                                        "FROM validation_functions_master FM " +
                                        "INNER JOIN validation_mapping_master MM " +
                                            "ON FM.validation_functions_master_id = MM.validation_functions_master_id " +
                                        "LEFT OUTER JOIN validation_mapping_details MD " +
                                            "ON MM.validation_mapping_master_id = MD.validation_mapping_master_id " +
                                        "WHERE MD.validation_mapping_details_id = ? AND MM.validation_functions_master_id = ? ");
                pst.setInt(1, validation_mapping_details_id);
                pst.setInt(2, validation_functions_master_id);
            } else {
                pst = con.prepareStatement("SELECT DISTINCT " +
                                                "FM.function_name," +
                                                "FM.description," +
                                                "MD.type," +
                                                "FM.error_message, " +
                                                "MD.parameter," +
                                                "coalesce(MD.status, 'False') as \"ifnull(MD.status, 'False')\"," +
                                                "FM.function_body " +
                                            "FROM validation_functions_master FM " +
                                            "INNER JOIN validation_mapping_master MM " +
                                                "ON FM.validation_functions_master_id = MM.validation_functions_master_id " +
                                            "LEFT OUTER JOIN validation_mapping_details MD " +
                                                "ON MM.validation_mapping_master_id = MD.validation_mapping_master_id " +
                                                "AND MD.validation_mapping_details_id = ? " +
                                            "WHERE  MM.validation_functions_master_id = ?");
                pst.setInt(1, validation_mapping_details_id);
                pst.setInt(2, validation_functions_master_id);
            }
    }

    private void edit(Element action, UserTask user, DBTask dbTask, MessageWriter writer, ValidationsData data) {
        function_name = data.functionName.split(":")[0].trim();
        String isToBeGeneric = data.functionName.split(":")[1].trim();
        data.functionName = function_name;
        try {
            //if function status is neither true or false then make it false.
            if (!(fun_status.equalsIgnoreCase("true") || fun_status.equalsIgnoreCase("false"))) {
                fun_status = "false";
            }

            Connection con = dbTask.getConnection();
            //update the validation_function_master
            pst = user.prepareStatement(dbTask, "UPDATE validation_functions_master SET " +
                    " function_name =?," +
                    " description = ?, " +
                    " function_body = ?, " +
                    " type = ?, " +
                    " scope = ?, " +
                    " parameter = ?, " +
                    " status = ? , " +
                    " error_message = ?" +
                    " WHERE validation_functions_master_id =? ");
            pst.setString(1, function_name);
            pst.setString(2, desc);
            pst.setString(3, function_body);
            pst.setString(4, type);
            pst.setString(5, scope);
            pst.setString(6, parameter);
            pst.setString(7, fun_status);
            pst.setString(8, error_message);
            pst.setInt(9, validation_functions_master_id);
            pst.executeUpdate();
            pst.close();

            // If the function scope is Document or Volume then no need to edit remaining part.
            if(scope.equals(DOCUMENT) || scope.equals(VOLUME)){
                return;
            }

            if (validation_mapping_details_id > 0) {
                //update validation_mapping_details
                pst = user.prepareStatement(dbTask, "UPDATE validation_mapping_details SET error_message = ?" +
                        ", parameter = ?, status= ?, type = ?  " +
                        "WHERE validation_mapping_details_id =? ");
                pst.setString(1, error_message);
                pst.setString(2, parameter);
                pst.setString(3, fun_status);
                pst.setString(4, type);
                pst.setInt(5, validation_mapping_details_id);
                pst.executeUpdate();
                pst.close();

                //-- if the function to be updated is non-generic and user want to make it generic.                            
                if (isToBeGeneric.equalsIgnoreCase("true")) {
                    String validation_mapping_master_id = null;
                    PreparedStatement getValdnMapMasterIdPrepStmt = con.prepareStatement("SELECT validation_mapping_master_id" +
                            " FROM validation_mapping_details " +
                            "where validation_mapping_details_id = ?");
                    getValdnMapMasterIdPrepStmt.setInt(1, validation_mapping_details_id);
                    getValdnMapMasterIdPrepStmt.executeQuery();
                    ResultSet getValdnMapMasterIdResultSet = getValdnMapMasterIdPrepStmt.getResultSet();
                    if (getValdnMapMasterIdResultSet.next()) {
                        validation_mapping_master_id = getValdnMapMasterIdResultSet.getString(1);
                    }
                    getValdnMapMasterIdPrepStmt.close();
                    pst = user.prepareStatement(dbTask, "UPDATE validation_mapping_master " +
                            "SET validation_functions_group_id = ? " +
                            "WHERE validation_mapping_master_id =? ");
                    pst.setInt(1, std_group_id);
                    pst.setString(2, validation_mapping_master_id);
                    pst.executeUpdate();
                    pst.close();
                    //now the funtion has been generic
                    data.isGeneric = "True";
                }
                //insert the record into the history table                         
                data.insertIntoHistoryTable(con, user.getUsersId(), Mode.EDIT);
            } else {
                int validation_mapping_master_id = 0;
                pst = con.prepareStatement("SELECT validation_mapping_master_id FROM validation_mapping_master" +
                        " WHERE validation_functions_master_id = ? AND " +
                        "validation_functions_group_id = ?");
                pst.setInt(1, validation_functions_master_id);
                pst.setInt(2, std_group_id);
                pst.executeQuery();
                ResultSet getValdnMapMasterIdResultSet = pst.getResultSet();
                while (getValdnMapMasterIdResultSet.next()) {
                    validation_mapping_master_id = getValdnMapMasterIdResultSet.getInt(1);
                }
                pst.close();
                getValdnMapMasterIdResultSet.close();
                pst = user.prepareStatement(dbTask, "INSERT INTO  validation_mapping_details" +
                        " (projectfields_id, error_message, parameter, status, " +
                        "validation_mapping_master_id, type) VALUES(?,?,?,?,?,?)");
                pst.setInt(1, fieldId);
                pst.setString(2, error_message);
                pst.setString(3, parameter);
                pst.setString(4, fun_status);
                pst.setInt(5, validation_mapping_master_id);
                pst.setString(6, type);
                pst.executeUpdate();
                pst.close();
                //insert the record into the history table
                data.insertIntoHistoryTable(con, user.getUsersId(), Mode.EDIT);
            }
        } catch (SQLException e) {
            CommonLogger.printExceptions(this, "Exception while updating the advance validations.", e);
        }
    }


    public boolean isReadOnly() {
        return true;
    }
}

