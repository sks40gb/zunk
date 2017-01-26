package com.fossa.servlet.command;

import com.fossa.servlet.common.CommonLogger;
import com.fossa.servlet.common.SQLQueries;
import com.fossa.servlet.common.msg.MessageWriter;
import com.fossa.servlet.dao.DBTask;
import com.fossa.servlet.session.UserTask;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.w3c.dom.Element;

/**
 * This class handles the post validations operations
 * @author sunil
 */
public class Command_send_post_validation implements Command {    
    public static final String BLANK = "";
    private int volumeId = 0;
    private int projectId = 0;
    public int post_validaton_id = 0;    
    public  String contextPath;
    private String sendIDsString;
    //Variables for DB operation.
    public DBTask dbTask;
    private Connection connection;
    private PreparedStatement pstmt = null;
    private ResultSet rs = null;

    public String execute(Element action, UserTask user, DBTask dbTask, MessageWriter writer) {
        try {
            this.dbTask = dbTask;
            connection = dbTask.getConnection();
            volumeId = Integer.parseInt(action.getAttribute(A_VOLUME_ID));
            projectId = Integer.parseInt(action.getAttribute(A_PROJECT_ID));
            sendIDsString = action.getAttribute(A_POST_VALIDATION_STR);
            contextPath = user.getContextPath();
            // set the project name and volume name
            //setProjectAndVolumeName(projectId, volumeId);
            // first insert the post validation record and then perform the 
            // post validation operation.
            insertInitialRecordToPostValidation(user);
            // get the last inserted record means the current record on which the 
            // post validation operation is being perform.
            // write the post validation record mainly the post validation Id and
            // send back to client as a result.
            getLastInsertedRecord(user, writer);
            // Start separate thread to perform the post valition operation.
            startTimer(user);

        } catch (Exception ex) {
            CommonLogger.printExceptions(this, "Exception while saving records for post validations.", ex);
        }
        return null;
    }

    /**
     * Before starting post validation process, write post validation record 
     * to DB .
     * Set the status as IN PROGRESS which implies that operaion is in progress.      
     * @param user -UserTask
     */
    private void insertInitialRecordToPostValidation(UserTask user) {
        int userId = user.getUsersId();
        try {
            //Report Excel Sheet with complete path.
            String fileName = getFileNameWithPath(user);            
            pstmt = connection.prepareStatement(SQLQueries.INS_POST_VALIDATION_PVR);
            pstmt.setInt(1, projectId);
            pstmt.setInt(2, volumeId);
            pstmt.setString(3, fileName);
            pstmt.setString(4, "IN PROGRESS");
            pstmt.setInt(5, userId);
            pstmt.setTimestamp(6, new Timestamp(new Date().getTime()));
            pstmt.executeUpdate();
        } catch (Exception e) {
            CommonLogger.printExceptions(this, "Exception while saving the records for post validations." , e);
        }
    }

    /**
     * Start separate Thread to perform the Post Validation Process.
     */
    private void startTimer(UserTask user) {        
        TimerTask timertask = new TimerTask(volumeId, projectId, sendIDsString, post_validaton_id,user);
        Thread t = new Thread(timertask);
        t.start();
    }

    /**
     * Get Post validation report Excelsheet File name with complete path.
     * The File name of ExcelSheet will be generated as -
     *         pvr_timestamps_username.xls
     * The File path is generated as - 
     *         contextPath + "UPLOAD" + Project Name  + Volume Name
     * @param user - UserTask
     * @return - Excelsheet File name with complete path
     * @throws java.sql.SQLException 
     */
    private String getFileNameWithPath(UserTask user) throws SQLException {
        int userId = user.getUsersId();                
        pstmt = connection.prepareStatement(SQLQueries.SEL_USER_NAME);
        //generate file name
        
        SimpleDateFormat format = new SimpleDateFormat("yyyy_MM_dd_hh_mm");        
        String strDate = format.format(new Date());   
        String fileName = "pvr_" + strDate + ".xls";
        pstmt.setInt(1, userId);
        pstmt.executeQuery();
        rs = pstmt.getResultSet();
        if (rs.next()) {
            fileName = "pvr_" + rs.getString(1) + "_" + strDate + ".xls";
        }               
        return  fileName;
    }

    /**
     * Get the current Post Validation Record on which the process in being 
     * performed. Write these as result for the client so that when client check
     * the status for these PVR it will be either IN PROGRESS or COMPLETED.
     * After each short interval user check the status and can get the status
     * and verify the process is under execution or its completed. 
     * @param user - UserTask
     * @param writer - MessageWriter to write the record back to client.
     * @throws java.sql.SQLException
     * @throws java.io.IOException
     */
    private void getLastInsertedRecord(UserTask user, MessageWriter writer) throws SQLException, IOException {
        pstmt = connection.prepareStatement(SQLQueries.SEL_POST_VALIDATION_LAST_RECORD);
        pstmt.executeQuery();
        rs = pstmt.getResultSet();        
        if(rs.next()){
            post_validaton_id = rs.getInt(1);
        }
        Command_sql_query.writeXmlFromResult(user, rs, writer);
        rs.close();
        pstmt.close();
    }

    public boolean isReadOnly() {
        return true;
    }   
}