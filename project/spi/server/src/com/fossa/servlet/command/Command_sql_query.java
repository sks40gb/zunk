/*
 * Command_sql_query.java
 *
 * Created on 12 November, 2007, 5:22 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package com.fossa.servlet.command;

import com.fossa.servlet.common.CommonLogger;
import com.fossa.servlet.common.MessageConstants;
import com.fossa.servlet.common.msg.MessageWriter;
import com.fossa.servlet.dao.DBTask;
import com.fossa.servlet.common.msg.XmlUtil;
import com.fossa.servlet.exception.ServerFailException;
import com.fossa.servlet.exception.ServerSQLFailException;
import com.fossa.servlet.server.SqlUtil;
import com.fossa.servlet.session.UserTask;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * This class executes the sql queries.
 * @author prakash
 */
public class Command_sql_query implements Command {

    public String execute(Element action, UserTask task, DBTask dbTask, MessageWriter writer) {        
        Connection con = dbTask.getConnection();
        ResultSet rs = null;
        PreparedStatement getProjPrepStmt = null;
        PreparedStatement getProjVolParamPrepStmt = null;
        PreparedStatement getProjParmetersPrepStmt = null;
        ResultSet getProjVolParamResultSet = null;
        Statement st = dbTask.getStatement();
        if (null != con) {
            String name = action.getAttribute(MessageConstants.A_NAME);
            boolean requestedMetaData = "YES".equals(action.getAttribute(MessageConstants.A_REQUEST_METADATA));
            try {
                //added for F10
                if ("queryTracker".equals(name)) {
                    NodeList nodes = action.getElementsByTagName(T_PARAMETER);
                    String[] parameter = new String[4];
                    int projectId = 0;
                    String fieldName = "";
                    int childId = 0;
                    int indx = 0;
                    String whichStatus = "";
                    for (int i = 0; i < nodes.getLength(); i++) {
                        indx++;                        
                        parameter[i] = XmlUtil.getTextFromNode(nodes.item(i));
                    }
                    fieldName = parameter[0];
                    projectId = Integer.parseInt(parameter[1]);
                    childId = Integer.parseInt(parameter[2]);
                    whichStatus = parameter[3];                    
                    getProjParmetersPrepStmt = con.prepareStatement("select p.project_name,v.volume_name ,q.field_name,q.level,q.collection," +
                            " q.dtyg,q.dtys,q.description,q.general_query,q.specific_query,q.raised_to,q.imagePath,q.answer " +
                            "from project p,volume v,query_tracker q where p.project_id = ? and v.project_id =? and q.field_name =?" +
                            " and v.project_id =? and q.child_id =?");
                    getProjParmetersPrepStmt.setInt(1, projectId);
                    getProjParmetersPrepStmt.setInt(2, projectId);
                    getProjParmetersPrepStmt.setString(3, fieldName);
                    getProjParmetersPrepStmt.setInt(4, projectId);
                    getProjParmetersPrepStmt.setInt(5, childId);
                    getProjParmetersPrepStmt.executeQuery();
                    rs = getProjParmetersPrepStmt.getResultSet();
                    if (rs.next()) {
                        ResultSetMetaData rsmd = rs.getMetaData();
                        int columnCount = rsmd.getColumnCount();
                        String[] results = new String[columnCount];
                        if (rs.next()) {
                            for (int i = 0; i < columnCount; i++) {
                                results[i] = rs.getString(i + 1);
                            }
                        } else {
                            Arrays.fill(results, "0");
                        }                        
                        getProjVolParamPrepStmt = con.prepareStatement("select p.project_name,v.volume_name ,q.field_name,q.level,q.collection," +
                                " q.dtyg,q.dtys,q.description,q.general_query,q.specific_query ,q.raised_to,q.imagePath,q.answer from project p,volume " +
                                " v,query_tracker q where p.project_id = ? and v.project_id =? and q.field_name =?" +
                                " and v.project_id =? and q.child_id =?");
                        getProjVolParamPrepStmt.setInt(1, projectId);
                        getProjVolParamPrepStmt.setInt(2, projectId);
                        getProjVolParamPrepStmt.setString(3, fieldName);
                        getProjVolParamPrepStmt.setInt(4, projectId);
                        getProjVolParamPrepStmt.setInt(5, childId);
                        try {
                            getProjVolParamResultSet = getProjVolParamPrepStmt.executeQuery();
                        } catch (SQLException e) {
                            CommonLogger.printExceptions(this, "SQLException while getting the project records." , e);
                            throw new ServerSQLFailException(e);
                        }
                        writeXmlFromResult(task, getProjVolParamResultSet, writer, requestedMetaData);
                    } else {
                        if (!"Admin".equals(whichStatus)) {
                            getProjPrepStmt = con.prepareStatement("select p.project_name,v.volume_name from project p,volume v " +
                                    " where p.project_id = v.project_id and p.project_id=?");
                            getProjPrepStmt.setInt(1, projectId);
                            rs = getProjPrepStmt.executeQuery();
                            if (rs.next()) {
                                ResultSetMetaData rsmd = rs.getMetaData();
                                int columnCount = rsmd.getColumnCount();
                                String[] results = new String[columnCount];
                                if (rs.next()) {
                                    for (int i = 0; i < columnCount; i++) {
                                        results[i] = rs.getString(i + 1);
                                    }
                                } else {
                                    Arrays.fill(results, "0");
                                }
                                getProjVolParamPrepStmt = con.prepareStatement("select p.project_name,v.volume_name from project p,volume v " +
                                        " where p.project_id = v.project_id and p.project_id=?");
                                getProjVolParamPrepStmt.setInt(1, projectId);
                                try {
                                    getProjVolParamResultSet = getProjVolParamPrepStmt.executeQuery();
                                } catch (SQLException e) {
                                     CommonLogger.printExceptions(this, "SQLException while getting the project records." , e);
                                    throw new ServerSQLFailException(e);
                                }
                                writeXmlFromResult(task, getProjVolParamResultSet, writer, requestedMetaData);
                            }
                        }
                    }
                }//ends             
                else {
                    //existing code                    
                    getProjPrepStmt = con.prepareStatement(
                            "select role, text from sql_text" + " where name=?" + " order by sequence");
                    getProjPrepStmt.setString(1, name);
                    rs = getProjPrepStmt.executeQuery();
                    if (!rs.next()) {
                        throw new ServerFailException("SQL not defined on server: " + name);
                    }

                    String sql = SqlUtil.substitute(task, rs.getString(2));
                    NodeList nodes = action.getElementsByTagName(T_PARAMETER);
                    PreparedStatement getSqlTextPrepStmt = con.prepareStatement(sql);
                    int index = 0;
                    for (int i = 0; i < nodes.getLength(); i++) {
                        index++;                        
                        getSqlTextPrepStmt.setString(index, XmlUtil.getTextFromNode(nodes.item(i)));
                    }

                    ResultSet getSqlTextResultSet;
                    try {
                        getSqlTextResultSet = getSqlTextPrepStmt.executeQuery();
                    } catch (SQLException e) {
                        CommonLogger.printExceptions(this, "Exception while getting the sql query for desired name.", e);
                        throw new ServerSQLFailException(e);
                    }

                    while (rs.next()) {
                        // get the results from the prior query
                        ResultSetMetaData rsmd = getSqlTextResultSet.getMetaData();
                        int columnCount = rsmd.getColumnCount();
                        String[] results = new String[columnCount];
                        if (rs.next()) {
                            for (int i = 0; i < columnCount; i++) {
                                results[i] = getSqlTextResultSet.getString(i + 1);
                            }
                        } else {
                            Arrays.fill(results, "0");
                        }

                        // prepare the next query and fill in parameters
                        // Note: we collect parameters in an array above,
                        // because we reuse PreparedStatement ps2.
                        sql = SqlUtil.substitute(task, rs.getString(2));
                        getSqlTextPrepStmt = con.prepareStatement(sql);
                        for (int i = 0; i < columnCount; i++) {
                            getSqlTextPrepStmt.setString(i + 1, results[i]);
                        }

                        // execute the next query
                        try {
                            getSqlTextResultSet = getSqlTextPrepStmt.executeQuery();
                        } catch (SQLException e) {
                            CommonLogger.printExceptions(this, "Exception while executing the sql query." , e);
                            throw new ServerSQLFailException(e);
                        }
                    }
                    getProjPrepStmt.close();

                    try {
                        writeXmlFromResult(task, getSqlTextResultSet, writer, requestedMetaData);
                    } catch (IOException ex) {
                        CommonLogger.printExceptions(this, "IOException while writing result in XML..", ex);
                    } catch (SQLException ex) {
                        CommonLogger.printExceptions(this, "SQLException while writing result in XML..", ex);
                    }
                    getSqlTextPrepStmt.close();
                }
            } catch (IOException ex) {
                 CommonLogger.printExceptions(this, "Exception while executing sql query." , ex);
            } catch (SQLException sqle) {
                 CommonLogger.printExceptions(this, "Exception while executing sql query." , sqle);
                 writeError("Transaction Failed. Please try again later.");
            }
        } else {
            writeError("Transaction Failed. Reset Server Connection.");
        }
        return null;
    }

    /**
     * Add the XML representation of a result set to the output message.
     * Column name metadata is not included.
     * @param task The server task.
     * @param rs The result set.
     */
    public static void writeXmlFromResult(UserTask task, ResultSet rs, MessageWriter writer)
            throws SQLException, IOException {
        writeXmlFromResult(task, rs, writer, false);
    }

    /**
     * Add the XML representation of a result set to the output message.
     * <p>Note: this may be called from other classes, as well as this one.
     * @param task The server task.
     * @param rs The result set.
     * @param requestedMetaData If true, column names are included for metadata.
     */
    public static void writeXmlFromResult(UserTask task, ResultSet rs, MessageWriter writer, boolean requestedMetaData)
            throws SQLException, IOException {
        ResultSetMetaData rsmd = rs.getMetaData();
        int columnCount = rsmd.getColumnCount();
        String userSessionId = task.getFossaSessionId();

        writer.startElement(T_RESULT_SET);
        writer.writeAttribute(A_FOSSAID, userSessionId);
        writer.writeAttribute(A_COUNT, Integer.toString(columnCount));
        if (requestedMetaData) {
            writer.startElement(T_HEADING);
            for (int j = 1; j <= columnCount; j++) {
                writer.startElement(T_COLUMN);
                writer.writeContent(rsmd.getColumnName(j));
                writer.endElement();
            }
            writer.endElement();
        }
        while (rs.next()) {
            writer.startElement(T_ROW);
            for (int j = 1; j <= columnCount; j++) {
                writer.startElement(T_COLUMN);
                String value = rs.getString(j);
                if (rs.wasNull()) {
                    writer.writeAttribute(A_IS_NULL, "YES");
                } else {
                    writer.writeContent(value);
                }
                writer.endElement();
            }
            writer.endElement();
        }
        writer.endElement();
    }

    private void writeError(String string) {
    }

    public boolean isReadOnly() {
        return false;
    }
}
