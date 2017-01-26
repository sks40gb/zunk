/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fossa.servlet.command;

import com.fossa.servlet.common.CommonLogger;
import com.fossa.servlet.common.ValidationsData;
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
 * This class adds a new validation.
 * @author Bala
 */
public class Command_add_validation_data implements Command {

    private final String DOCUMENT = "Document";
    private final String VOLUME = "Volume";
    private PreparedStatement pst;
    private Connection connection;
    private int validation_functions_master_id = 0;
    private int validation_functions_group_id = 0;
    private int validation_mapping_master_id = 0;
    
    public String execute(Element action, UserTask user, DBTask dbTask, MessageWriter writer) {

        Element givenValueList = action;
        Node firstChild = givenValueList.getFirstChild();
        while (firstChild != null && firstChild.getNodeType() != Node.ELEMENT_NODE) {
            firstChild = firstChild.getNextSibling();            
        }

        if (firstChild != null) {
            ValidationsData data = new ValidationsData();
            // fill in the int and String fields of the UsersData            
            try {
                XmlReader.decode(givenValueList, data);
            } catch (IOException ex) {
               CommonLogger.printExceptions(this, "Exception while decoding XMLReader.", ex);
            }
            //inserting functions record into tables 
            if (data != null) {                                
                connection = dbTask.getConnection();                
                try {                 

                    //insert the validation parameters
                    pst = user.prepareStatement(dbTask, "INSERT INTO  validation_functions_master " +
                                                             "(function_name," +
                                                             "description," +
                                                             "function_body, " +
                                                             "error_message, " +
                                                             "type, " +
                                                             "scope, " +
                                                             "parameter, " +
                                                             "status, " +
                                                             "project_id) " +
                                                         "VALUES(?,?,?,?,?,?,?,?,?)");
                    pst.setString(1, data.functionName);
                    pst.setString(2, data.description);
                    pst.setString(3, data.methodBody);
                    pst.setString(4, data.errorMessage);
                    pst.setString(5, data.type);
                    pst.setString(6, data.scope);
                    pst.setString(7, data.userInput);
                    pst.setString(8, data.status);
                    pst.setInt(9, data.projectId);
                    pst.executeUpdate();
                    pst.close();

                    // If the function scope is Document or Volume then no need to execute remaining queries.
                    if(data.scope.equals(DOCUMENT) || data.scope.equals(VOLUME)){
                        System.out.println("0 scope =================================================> " + data.scope);
                        return null;
                    }else{
                        System.out.println("1 scope =================================================> " + data.scope);
                    }


                    pst = connection.prepareStatement("SELECT validation_functions_group_id FROM projectfields " +
                                                                     "WHERE project_id=? AND projectfields_id =?");
                    pst.setInt(1, data.projectId);
                    pst.setInt(2, data.fieldId);
                    pst.executeQuery();
                    ResultSet getValidationGroupIdResultSet = pst.getResultSet();
                    while (getValidationGroupIdResultSet.next()) {
                        validation_functions_group_id = getValidationGroupIdResultSet.getInt(1);
                    }
                    pst.close();


                    pst = connection.prepareStatement("SELECT validation_functions_master_id FROM validation_functions_master");
                    pst.executeQuery();
                    ResultSet getValidationMasterIdResultSet = pst.getResultSet();
                    while (getValidationMasterIdResultSet.next()) {                       
                        validation_functions_master_id = getValidationMasterIdResultSet.getInt(1);
                        data.validation_functions_master_id = validation_functions_master_id;
                    }
                    pst.close();
                    getValidationMasterIdResultSet.close();

                    //data.status is equals to true if the funtion is generic.
                    if (data.isGeneric.equalsIgnoreCase("true")) {
                        pst = user.prepareStatement(dbTask, "INSERT INTO  validation_mapping_master " +
                                                            "(validation_functions_master_id,validation_functions_group_id)" +
                                                            " VALUES(?,?)");
                        pst.setInt(1, validation_functions_master_id);
                        pst.setInt(2, validation_functions_group_id);
                    } else {
                        pst = user.prepareStatement(dbTask, "INSERT INTO  validation_mapping_master " +
                                                            "(validation_functions_master_id) VALUES(?)");
                        pst.setInt(1, validation_functions_master_id);
                    }
                    pst.executeUpdate();
                    pst.close();

                    pst = connection.prepareStatement("SELECT validation_mapping_master_id FROM validation_mapping_master");
                    pst.executeQuery();
                    ResultSet getValidationMappingIdResultSet = pst.getResultSet();
                    while (getValidationMappingIdResultSet.next()) {
                        validation_mapping_master_id = getValidationMappingIdResultSet.getInt(1);
                    }
                    pst.close();
                    getValidationMappingIdResultSet.close();

                    pst = user.prepareStatement(dbTask, "INSERT INTO  validation_mapping_details " +
                                                        "(projectfields_id,error_message, parameter, status," +
                                                        " validation_mapping_master_id, type) VALUES(?,?,?,?,?,?)");
                    pst.setInt(1, data.fieldId);
                    pst.setString(2, data.errorMessage);
                    pst.setString(3, data.userInput);
                    pst.setString(4, data.status);
                    pst.setInt(5, validation_mapping_master_id);
                    pst.setString(6, data.type);
                    pst.executeUpdate();
                    pst.close();
                    
                    //insert the record into the history table
                    data.insertIntoHistoryTable(connection, user.getUsersId(), Mode.ADD);
                    return null;

                } catch (SQLException ex) {
                    CommonLogger.printExceptions(this, "Exception during adding new validation functions." , ex);
                }
            }
        }
        return null;
    }

    public boolean isReadOnly() {
        return true;
    }
}

