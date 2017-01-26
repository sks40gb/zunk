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
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.w3c.dom.Element;

/**
 * This class handles in saving the ListingQC process
 * @author bmurali
 */
public class Command_save_listing_qc implements Command {

    private Connection connection;
    private PreparedStatement pstmt = null;
    private ResultSet rs = null;    
    private String userName = "";
    private String batchStatus = "";
    private int userId = 0;    
    private int fieldId = 0;    
    private String insertQuery;

    public String execute(Element action, UserTask user, DBTask dbTask, MessageWriter writer) {
        try {
            String fieldName = action.getAttribute(A_FIELD_NAME);
            int volumeId = Integer.parseInt(action.getAttribute(A_VOLUME_ID));
            int projectId = Integer.parseInt(action.getAttribute(A_PROJECT_ID));
            userId = Integer.parseInt(action.getAttribute(A_USERS_ID));
            batchStatus = action.getAttribute(A_STATUS);

            connection = dbTask.getConnection();
            //Get user name with project fields id
            pstmt = connection.prepareStatement("SELECT U.user_name,P.projectfields_id FROM users U,projectfields P " +
                                                "WHERE U.users_id = ? AND P.field_name =? AND P.project_id =?");
            pstmt.setInt(1, userId);
            pstmt.setString(2, fieldName);
            pstmt.setInt(3, projectId);
            pstmt.executeQuery();
            rs = pstmt.getResultSet();
            while (rs.next()) {
                userName = rs.getString(1);
                fieldId = rs.getInt(2);
            }
            pstmt.close();
            rs.close();

            //Get the listing_qc_id & status 
            if ("Listing".equals(batchStatus)) {
                pstmt = connection.prepareStatement("SELECT listing_qc_id,status FROM listing_qc WHERE field_name = ?" +
                        " AND project_id = ? and volume_id = ?");
            } else if ("Tally".equals(batchStatus)) { //Get the tally_qc_id & status 
                pstmt = connection.prepareStatement("SELECT tally_qc_id,status FROM tally_qc WHERE field_name = ? " +
                        "AND project_id = ? and volume_id = ?");
            }
            pstmt.setString(1, fieldName);
            pstmt.setInt(2, projectId);
            pstmt.setInt(3, volumeId);
            pstmt.executeQuery();
            rs = pstmt.getResultSet();
            if (rs.next()) {                
                String status = rs.getString(2);
                pstmt.close();
                //Update the batch status
                if ("Idle".equals(status)) {
                    if ("Listing".equals(batchStatus)) {                        
                        pstmt = user.prepareStatement(dbTask, "UPDATE listing_qc  SET  user_name =? ,user_id =? where field_name =?");
                    } else if ("Tally".equals(batchStatus)) {                        
                        pstmt = user.prepareStatement(dbTask, "UPDATE tally_qc  SET  user_name =? ,user_id =? where field_name =?");
                    }
                    pstmt.setString(1, userName);
                    pstmt.setInt(2, userId);
                    pstmt.setString(3, fieldName);
                    pstmt.executeUpdate();
                } else if ("Assigned".equals(status)) {
                    throw new ServerFailException("Field is already Assigned");
                } else if ("Complete".equals(status)) {
                    throw new ServerFailException("ListingQc is completed for this field ");
                }

            } else { //add a new row 
                if ("Listing".equals(batchStatus)) {
                    insertQuery = "Insert into listing_qc (project_id, volume_id,user_id,field_name, user_name,status)" +
                                                   " values (?,?,?,?,?,?)";
                } else if ("Tally".equals(batchStatus)) {
                    insertQuery = "Insert into tally_qc (project_id, volume_id,user_id,field_name, user_name,status)" +
                                                   " values (?,?,?,?,?,?)";
                }
                pstmt = user.prepareStatement(dbTask, insertQuery);
                pstmt.setInt(1, projectId);
                pstmt.setInt(2, volumeId);
                pstmt.setInt(3, userId);
                pstmt.setString(4, fieldName);
                pstmt.setString(5, userName);
                pstmt.setString(6, "Idle");
                pstmt.executeUpdate();
            }
        } catch (SQLException ex) {
            CommonLogger.printExceptions(this, "SQLException while updating the listing qc." , ex);
        } catch (ServerFailException exc) {
            CommonLogger.printExceptions(this, "ServerFailException while updating the listing qc." , exc);
            return exc.getMessage();
        }
        return null;
    }

    public boolean isReadOnly() {
        return true;
    }
}
