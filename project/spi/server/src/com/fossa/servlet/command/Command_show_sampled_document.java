/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 *
 */

package com.fossa.servlet.command;

import com.fossa.servlet.common.CommonLogger;
import com.fossa.servlet.common.Log;
import com.fossa.servlet.common.SQLQueries;
import com.fossa.servlet.common.msg.MessageWriter;
import com.fossa.servlet.dao.DBTask;
import com.fossa.servlet.session.UserTask;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.w3c.dom.Element;

/**
 * This Class to Get All the QASampledDocument Group for a given Project and Volume
 *
 * @author Prakasha
 */
public class Command_show_sampled_document implements Command {

    // Method to get all the QASampledDocument Group for a given Project and Volume
    public String execute(Element action, UserTask user, DBTask dbTask, MessageWriter writer) {
        

        PreparedStatement getDocumentChildIdStatement = null;
        PreparedStatement getStartBatesStatement = null;
        PreparedStatement getEndBatesStatement = null;

        ResultSet getDocumentResultSet = null;
        ResultSet getStartBatesResultSet = null;
        ResultSet getEndBatesResultSet = null;
        Connection connection = null;
        String startBates = null;
        String endBates = null;
        String sampledFields = "";
        
        // Get the Volume id and group number from the client-side
        int volumeId = Integer.parseInt(action.getAttribute(A_VOLUME_ID));
        int groupNo = Integer.parseInt(action.getAttribute(A_GROUP_NUMBER));
        
        int samplingId = 0;
        
        try {
            connection = dbTask.getConnection();
            getDocumentChildIdStatement = connection.prepareStatement(SQLQueries.SEL_QA_SAMPLED_DOCUMENT);
            getDocumentChildIdStatement.setInt(1, volumeId);
            getDocumentChildIdStatement.setInt(2, groupNo);
            getDocumentResultSet = getDocumentChildIdStatement.executeQuery();            
            writer.startElement(T_QA_PR_SAMPLED_DOCUMENT);
            String userSessionId = user.getFossaSessionId();
            writer.writeAttribute(A_FOSSAID, userSessionId);
            
            //Get All SampledDocument group for given Project and Volume
            while(getDocumentResultSet.next()) {
                int childId = getDocumentResultSet.getInt(1);
                int documentId = getDocumentResultSet.getInt(2);
                samplingId = getDocumentResultSet.getInt(3);
                sampledFields = getDocumentResultSet.getString(4);
                
                if(null == sampledFields) {
                    sampledFields = "ALL";
                }
                
                getStartBatesStatement = connection.prepareStatement(SQLQueries.SEL_START_BATES_NUMBER); 
                getStartBatesStatement.setInt(1, childId);
                //getStartBatesStatement.setString(2, "RANGE");
                getStartBatesResultSet = getStartBatesStatement.executeQuery();
                
                // Get the start bates from the result set
                if(getStartBatesResultSet.next()) {
                    startBates = getStartBatesResultSet.getString(1);
                }else {
                    startBates = "";
                }
                
                getEndBatesStatement= connection.prepareStatement(SQLQueries.SEL_END_BATES_NUMBER);  
                getEndBatesStatement.setInt(1, childId);
                getEndBatesResultSet = getEndBatesStatement.executeQuery();
                
                // Get the end bates from the result set
                if(getEndBatesResultSet.next()) {
                    endBates = getEndBatesResultSet.getString(1);
                }else {
                    endBates = "";
                }
                
                // Start writing the XML
                writer.startElement(T_ROW);
                writer.startElement(T_COLUMN);
                writer.writeContent(childId);
                writer.endElement();
                writer.startElement(T_COLUMN);
                writer.writeContent(startBates);
                writer.endElement();
                writer.startElement(T_COLUMN);
                writer.writeContent(endBates);
                writer.endElement();
                writer.startElement(T_COLUMN);
                writer.writeContent(documentId);
                writer.endElement();
                writer.startElement(T_COLUMN);
                writer.writeContent(samplingId);
                writer.endElement();
                writer.startElement(T_COLUMN);
                writer.writeContent(sampledFields);
                writer.endElement();
                writer.endElement();
            }
            writer.endElement();
        }catch(SQLException se) {
            String sqlState = se.getSQLState();
            int errorCode = se.getErrorCode();
            Log.print(">>>"+se+" sqlState="+sqlState+" errorCode="+errorCode);
            CommonLogger.printExceptions(this,"SQLException caught during showing sampled document", se);
        }catch(IOException ie) {
            CommonLogger.printExceptions(this,"IOException caught during showing sampled document", ie);
        }        
        return null;
    }

    public boolean isReadOnly() {
        return true;
    }

}
