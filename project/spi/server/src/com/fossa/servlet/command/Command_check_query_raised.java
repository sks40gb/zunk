/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.fossa.servlet.command;

import com.fossa.servlet.common.CommonLogger;
import com.fossa.servlet.common.msg.MessageWriter;
import com.fossa.servlet.dao.DBTask;
import com.fossa.servlet.exception.ServerFailException;
import com.fossa.servlet.session.UserTask;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.w3c.dom.Element;

/**
 * This class checks that any query is raised for a required field.
 * @author bmurali
 */
public class Command_check_query_raised implements Command{
    private Connection connection;
    private PreparedStatement getQueryTrackerIdPrepStmt;    
    private ResultSet getQueryTrackerIdResultSet;    
    private String fieldName ="";
    private int projectId = 0;
    
    public String execute(Element action, UserTask user, DBTask dbTask, MessageWriter writer) {
        try {
            connection = dbTask.getConnection();
            projectId = Integer.parseInt(action.getAttribute(A_PROJECT_ID));
            fieldName = action.getAttribute(A_FIELD_NAME);
            //Check whether any query is raised for the given field
            getQueryTrackerIdPrepStmt = connection.prepareStatement("SELECT query_tracker_id  " +
                                                                     "FROM query_tracker" +
                                                                     " WHERE project_id = ? AND field_name = ?");
            getQueryTrackerIdPrepStmt.setInt(1, projectId);
            getQueryTrackerIdPrepStmt.setString(2, fieldName);
            getQueryTrackerIdPrepStmt.executeQuery();
            getQueryTrackerIdResultSet = getQueryTrackerIdPrepStmt.getResultSet();
           if(getQueryTrackerIdResultSet.next()){
               //Start writing the XML
                String userSessionId = user.getFossaSessionId();        
                writer.startElement(T_YES_QUERY_RAISED);
                writer.writeAttribute(A_FOSSAID, userSessionId);
                writer.writeAttribute(A_COUNT, "YES");
                writer.endElement();             
           }
        } catch (IOException ex) {
            CommonLogger.printExceptions(this, "IOException while checking if any query is raised for a field.", ex);
        } catch (SQLException ex) {
            CommonLogger.printExceptions(this, "SQLException while checking if any query is raised for a field.", ex);
        }catch(ServerFailException excp){
            CommonLogger.printExceptions(this, "ServerFailException while checking if any query is raised for a field.", excp);
            return excp.getMessage();            
        }
         return null;
    }

    public boolean isReadOnly() {
        return true;
    }

}
