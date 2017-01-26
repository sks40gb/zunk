/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.fossa.servlet.command;

import com.fossa.servlet.common.CommonConstants;
import com.fossa.servlet.common.CommonLogger;
import com.fossa.servlet.common.msg.MessageWriter;
import com.fossa.servlet.dao.DBTask;
import com.fossa.servlet.exception.ServerFailException;
import com.fossa.servlet.session.UserTask;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.w3c.dom.Element;

/**
 * This class check the batch availability for the user.
 * @author bmurali
 */
public class Command_check_batch_available implements Command {
    
    private Connection connection;
    private PreparedStatement getBatchIDPrepStmt;
    private PreparedStatement getUserIDPrepStmt;    
    private ResultSet getBatchIDResultSet;
    private ResultSet getUserIDResultSet;
    private String batchStatus ="";
    private String status ="";   //Holds the batch status like Listing,ListingQC,Tally,TallyQC.
    private int projectId = 0;
    private int volumeId = 0;
    private int batchId = 0;
    
    public String execute(Element action, UserTask user, DBTask dbTask, MessageWriter writer) {
        try {
            connection = dbTask.getConnection();
            projectId = Integer.parseInt(action.getAttribute(A_PROJECT_ID));
            volumeId = Integer.parseInt(action.getAttribute(A_VOLUME_ID));
            status = action.getAttribute(A_STATUS);            
            if (CommonConstants.PROCESS_LISTING.equals(status)) {
                batchStatus = CommonConstants.PROCESS_LISTING;
            } else if (CommonConstants.PROCESS_LISTING_QC.equals(status)) {
                batchStatus = CommonConstants.PROCESS_LISTING;
            } else if (CommonConstants.PROCESS_TALLY.equals(status)) {
                batchStatus = CommonConstants.PROCESS_TALLY;
            } else if (CommonConstants.PROCESS_TALLY_QC.equals(status)) {
                batchStatus = CommonConstants.PROCESS_TALLY;
            }
            getBatchIDPrepStmt = connection.prepareStatement("SELECT batch_id  FROM batch WHERE volume_id = ? AND status = ?");
            getBatchIDPrepStmt.setInt(1, volumeId);
            getBatchIDPrepStmt.setString(2, batchStatus);
            getBatchIDPrepStmt.executeQuery();
            getBatchIDResultSet = getBatchIDPrepStmt.getResultSet();
            boolean flag = true;
            while (getBatchIDResultSet.next()) {     
                
                flag = false;
                batchId = getBatchIDResultSet.getInt(1);                                     
                System.out.println("batchId=========>"+batchId);
                if (CommonConstants.PROCESS_LISTING.equals(status)) {
                   batchStatus = CommonConstants.PROCESS_LISTING;
                   getUserIDPrepStmt = connection.prepareStatement("SELECT DISTINCT U.users_id FROM usersqueue U," +
                                                                   "batch B WHERE U.batch_id = B.batch_id AND " +
                                                                   "B.volume_id =? AND B.status =? ");
                }else if (CommonConstants.PROCESS_LISTING_QC.equals(status)) {
                   batchStatus = CommonConstants.PROCESS_LISTING;
                   getUserIDPrepStmt = connection.prepareStatement("SELECT DISTINCT U.users_id FROM usersqueue U," +
                                                                   "batch B HERE U.batch_id = B.batch_id AND " +
                                                                    "B.volume_id =? AND B.status =? ");
                }else if (CommonConstants.PROCESS_TALLY.equals(status)) {
                   batchStatus = CommonConstants.PROCESS_TALLY;
                   
                   getUserIDPrepStmt = connection.prepareStatement("SELECT DISTINCT U.users_id FROM usersqueue U," +
                                                                   "batch B WHERE U.batch_id = B.batch_id AND " +
                                                                   "B.volume_id =? AND B.status =? ");
                }else if (CommonConstants.PROCESS_TALLY_QC.equals(status)) {
                   batchStatus = CommonConstants.PROCESS_TALLY;
                   getUserIDPrepStmt = connection.prepareStatement("SELECT DISTINCT U.users_id FROM usersqueue U," +
                                                                   "batch B WHERE U.batch_id = B.batch_id AND " +
                                                                   "B.volume_id =? AND B.status =? ");
                }            
                getUserIDPrepStmt.setInt(1, volumeId);  
                getUserIDPrepStmt.setString(2, batchStatus);
                getUserIDPrepStmt.executeQuery();
                getUserIDResultSet = getUserIDPrepStmt.getResultSet();
                while(getUserIDResultSet.next()){
                  
                   int user_id = getUserIDResultSet.getInt(1);
                     System.out.println("user_id=========>"+user_id);
                   int login_id =  user.getUsersId();
                   if(login_id != user_id){
                        throw new ServerFailException("No batches assigned for this User :   ");  
                   }                    
                }
            }
            Statement  st = dbTask.getStatement();
            ResultSet getFieldCount = st.executeQuery("select count(projectfields_id) from projectfields where project_id="+projectId+" and field_name !='General Document Type'");
            if(getFieldCount.next()){
              int fieldCount =  getFieldCount.getInt(1);
              int tallyGroupFieldCount = 0;
              ResultSet getTallyGroup = st.executeQuery("select count(project_field_id) from tally_mapping where project_id="+projectId);
              if(getTallyGroup.next()){
                tallyGroupFieldCount = getTallyGroup.getInt(1);
              }
              if(fieldCount == tallyGroupFieldCount){
               // throw new ServerFailException("All fields are already  grouped: ");  
              }
            }
            if(flag){
                throw new ServerFailException("No batches assigned for this User :   ");  
            }
        } catch (SQLException ex) {
            CommonLogger.printExceptions(this, "SQLException while checking batch availability.", ex);
        } catch(ServerFailException excp){
           CommonLogger.printExceptions(this, "ServerFailException while checking batch availability.", excp);
           return excp.getMessage();            
        }
        return null;
    }

    public boolean isReadOnly() {
        return true;
    }

}
