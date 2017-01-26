/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.fossa.servlet.command;

import com.fossa.servlet.common.CommonConstants;
import com.fossa.servlet.common.SQLQueries;
import com.fossa.servlet.common.msg.MessageWriter;
import com.fossa.servlet.dao.DBTask;
import com.fossa.servlet.server.EventLog;
import com.fossa.servlet.session.UserTask;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.w3c.dom.Element;
import java.sql.Timestamp;
import java.util.Date;

/**
 *This Class will close the Selected QASampledGroup by a User
 * @author Prakasha
 */
public class Command_QA_Close_Group implements Command{

    public String execute(Element action, UserTask user, DBTask dbTask, MessageWriter writer) {
        try {
            PreparedStatement updateQAStatusStatement = null;
            Connection connection = null;
            ResultSet getSamplingIdResultSet = null;
            int groupNumber = Integer.parseInt(action.getAttribute(A_GROUP_NUMBER));
            int samplingId = 0;
            int volumeId = Integer.parseInt(action.getAttribute(A_VOLUME_ID));
            
            Date date = new Date();
            long time = date.getTime();
            Timestamp timestamp = new Timestamp(time);
        
            PreparedStatement getSamplingIdStatement = null;
            connection = dbTask.getConnection();
            getSamplingIdStatement = connection.prepareStatement(SQLQueries.SEL_SAMPLING_ID);
            getSamplingIdStatement.setInt(1, volumeId);
            getSamplingIdResultSet = getSamplingIdStatement.executeQuery();
            
            if(getSamplingIdResultSet.next()) {
                samplingId = getSamplingIdResultSet.getInt(1);
            }
            
            updateQAStatusStatement = connection.prepareStatement(SQLQueries.UPD_QA_GROUP_STATUS);
            updateQAStatusStatement.setString(1, "Completed");
            updateQAStatusStatement.setTimestamp(2, timestamp);
            updateQAStatusStatement.setInt(3, groupNumber);
            updateQAStatusStatement.setInt(4, volumeId);
            updateQAStatusStatement.setInt(5, samplingId);
            
            updateQAStatusStatement.executeUpdate();
            //batch id will be '0' for QA
            EventLog.close(user, dbTask, volumeId, 0, CommonConstants.PROCESS_QA);
                       
        } catch (SQLException ex) {
            Logger.getLogger(Command_QA_Close_Group.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public boolean isReadOnly() {
        return true;
    }

}
