/*
 * Command_sql_update.java
 *
 * Created on November 21, 2007, 2:28 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package com.fossa.servlet.command;

import com.fossa.servlet.common.CommonLogger;
import com.fossa.servlet.common.SQLQueries;
import com.fossa.servlet.common.msg.MessageWriter;
import com.fossa.servlet.common.msg.XmlUtil;
import com.fossa.servlet.dao.DBTask;
import com.fossa.servlet.exception.ServerFailException;
import com.fossa.servlet.exception.UserErrorMessage;
import com.fossa.servlet.server.SqlUtil;
import com.fossa.servlet.session.UserTask;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * This class executes the sql update queries.
 * @author bmurali
 */
public class Command_sql_update implements Command {
   
    public String execute(Element action, UserTask user, DBTask dbTask, MessageWriter writer) {
        Connection con = dbTask.getConnection();
        String name = action.getAttribute(A_NAME);
        PreparedStatement getRolePrepStmt;

        try {
            getRolePrepStmt = con.prepareStatement(SQLQueries.SEL_UPD_ROLE);            
            getRolePrepStmt.setString(1, name);           
            ResultSet getRoleResultSet = getRolePrepStmt.executeQuery();
            if (!getRoleResultSet.next()) {
                throw new ServerFailException(UserErrorMessage.sqlNotDefined + name);
            }
            // Obtain the sql string and do substitutions
            String sql = SqlUtil.substitute(user, getRoleResultSet.getString(2)).trim();            

            getRolePrepStmt.close(); // for now, assume only one statement - maybe batches later
            NodeList parameterNodes = action.getElementsByTagName(T_PARAMETER);
            PreparedStatement delProjFieldsPrepStmt = null;
            // For now, don't treat replace as mangaged (kludge!!)
                // TBD: really need to indicate in sql_text whether or not stmt is managed
            if (sql.startsWith("replace")) {
                delProjFieldsPrepStmt = con.prepareStatement(sql);
            } else {                
                delProjFieldsPrepStmt = user.prepareStatement(dbTask, sql);            
            }
            // For deleting the project fields
            if (sql.startsWith("DELETE projectfields FROM projectfields PF INNER JOIN project")) {
                delProjFieldsPrepStmt.setInt(1, Integer.parseInt(XmlUtil.getTextFromNode(parameterNodes.item(0))));
            } else {
                setParameters(delProjFieldsPrepStmt, parameterNodes);
            }

            int updateCount = delProjFieldsPrepStmt.executeUpdate();
            delProjFieldsPrepStmt.close();            
            user.commitTransaction(dbTask);
            try {
                String userSessionId = user.getFossaSessionId();                
                writer.startElement(T_UPDATE_COUNT);
                writer.writeAttribute(A_FOSSAID, userSessionId);
                writer.writeAttribute(A_COUNT, Integer.toString(updateCount));
                writer.endElement();
            } catch (IOException ex) {
                CommonLogger.printExceptions(this, "IOException while executing the sql update query." , ex);
            }
        } catch (ServerFailException ex) {
            CommonLogger.printExceptions(this, "ServerFailException while executing the sql update query." , ex);
            return ex.getMessage();
        } catch (SQLException ex) {
            CommonLogger.printExceptions(this, "SQLException while executing the sql update query." , ex);
        }
        return null;
    }

    private void setParameters(PreparedStatement ps2, NodeList parameterNodes)
            throws SQLException {
        int end = parameterNodes.getLength();
        int ps2indx = 0;
        for (int i = 0; i < end; i++) {
            ps2indx++;            
            //As the parameters may be String and other format also, therefore setString 
           // is used. It will support all kind of format.
            ps2.setString(ps2indx, XmlUtil.getTextFromNode(parameterNodes.item(i)).toString());

        }
    }

    public boolean isReadOnly() {
        return false;
    }
}
