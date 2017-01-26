/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.fossa.servlet.command;

import com.fossa.servlet.common.CommonLogger;
import com.fossa.servlet.common.msg.MessageWriter;
import com.fossa.servlet.dao.DBTask;
import com.fossa.servlet.session.UserTask;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.w3c.dom.Element;

/**
 * This class updates the status of Listing or Tally batches as 'Complete'
 * @author bmurali
 */
public class Command_qc_done implements Command {
   
    private PreparedStatement pstmt = null;
    private ResultSet rs = null;
    private String status = "";
    private int projectId = 0;
    private int volumeId = 0;
    private String fieldName;

    public String execute(Element action, UserTask user, DBTask dbTask, MessageWriter writer) {
        projectId = Integer.parseInt(action.getAttribute(A_PROJECT_ID));
        volumeId = Integer.parseInt(action.getAttribute(A_VOLUME_ID));
        status = action.getAttribute(A_STATUS);
        fieldName = action.getAttribute(A_FIELD_NAME);        
        try {
             if("ListingQC".equals(status)){                
                    pstmt = user.prepareStatement(dbTask,"UPDATE listing_qc  SET  status =?  where field_name =? AND volume_id = ?");
             }else if("TallyQC".equals(status)){
                    pstmt = user.prepareStatement(dbTask,"UPDATE tally_qc  SET  status =?  where field_name =? AND volume_id = ?");
             }
             pstmt.setString(1, "Complete");
             pstmt.setString(2, fieldName);
             pstmt.setInt(3, volumeId);                
             pstmt.executeUpdate();             
             pstmt.close();                
        } catch (SQLException ex) {
            CommonLogger.printExceptions(this, "Exception while updating the listing qc as done." , ex);
        }
        return null;
    }

    public boolean isReadOnly() {
        return true;
    }
}
