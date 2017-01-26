/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fossa.servlet.command;

import com.fossa.servlet.common.CommonLogger;
import com.fossa.servlet.common.IbaseConstants;
import com.fossa.servlet.common.Log;
import com.fossa.servlet.common.MessageConstants;
import com.fossa.servlet.common.SQLQueries;
import com.fossa.servlet.common.msg.MessageWriter;
import com.fossa.servlet.dao.DBTask;
import com.fossa.servlet.server.EventLog;
import com.fossa.servlet.session.UserTask;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import org.apache.log4j.Logger;

/** 
 * Get values and put in the map for different operations.
 * @author ashish
 */
public class ValueMapper implements MessageConstants {

    // private ServerTask task;
    private UserTask task;
    private DBTask dbTask;
    private int volume_id;
    private int[] child_id = new int[2];
    private int batch_id;
    private int project_id = 0;
    private int childId = 0;
    private int firstPageId = 0;
    private String level = "";
    // from project_fields, the size of the field
    private int fieldSize;

    // from project_fields, the minimum size of the field
    private int minimumSize;

    // from project_fields, the type of the field
    private String fieldType;

    // from project_fields, if the field is repeated
    private boolean isRepeated;

    //from project_fields,  F10 raised
    private String queryraised;
    //from project_fields,  F10 raised
    private String queryanswered;
    // from the map, the fieldnames being provided for update
    private String l1_information;
    private String fieldName;
    private int fieldCount;
    private String processLevel = null;
    private Connection con;
    private Statement st;
    private String[] value;
    // MessageWriter for the task.  Null until something is written.
    private MessageWriter writer = null;
    // A StringBuffer for combining multiple values into one string
    private StringBuffer buffer;
    // The name of the field whose value is being constructed, or null.
    String priorFieldName;
    boolean w = true;
    // Accumulate the number of rows deleted before the inserts to the value
    // tables for later use in determining whether the store is an add or an update.
    private int rowsDeleted;
    private String fieldValue;
    private String status;
    private String status_level;
    private int pageId;
    private static Logger logger = Logger.getLogger("com.fossa.servlet.command");
    private int sub_process;
    public ValueMapper() {
    }

    /**
     * Craete an instamce of this class and remeber the parameteres.
     * @param task
     * @param dbTask
     * @param writer
     * @param volume_id
     * @param child_id
     * @param pageId
     * @param level
     * @param status
     * @param firstPageId
     */
    private ValueMapper(UserTask task, DBTask dbTask, MessageWriter writer, int volume_id, int child_id, int pageId, 
            String level, String status, int firstPageId) {
        this.task = task;
        this.dbTask = dbTask;
        this.volume_id = volume_id;
        this.child_id[0] = child_id;
        this.writer = writer;
        this.pageId = pageId;
        this.level = level;
        this.status_level = status;
        this.firstPageId = firstPageId;
        rowsDeleted = 0;
        con = dbTask.getConnection();
        st = dbTask.getStatement();
    }

    // Create an instance of ValueMapper, remembering task, volume and page
    private ValueMapper(UserTask task, DBTask dbTask, MessageWriter writer, int volume_id, int child_id) {
        this.task = task;
        this.dbTask = dbTask;
        this.volume_id = volume_id;
        this.child_id[0] = child_id;
        this.childId = child_id;
        this.writer = writer;
        rowsDeleted = 0;
        con = dbTask.getConnection();
        st = dbTask.getStatement();
    }

    /**
     * Append a value_list element to this task's message, with data
     * for a given volume and page.  If no data are found, the value_list
     * element is not created.
     * @param task the ServerTask upon whose message the values will be
     * appended
     * @param volume_id the volume.volume_id containing the child
     * @param child_id the child.child_id of the coded data being requested
     */
    public static void write(UserTask task, DBTask dbTask, MessageWriter writer, int volume_id, int child_id, int pageId,
            String level, String status, int firstPageId) {
        (new ValueMapper(task, dbTask, writer, volume_id, child_id, pageId, level, status, firstPageId)).write();
    }

    public static void write(UserTask task, DBTask dbTask, MessageWriter writer, int volume_id, int child_id) {
        (new ValueMapper(task, dbTask, writer, volume_id, child_id)).write();
    }

    /**
     * Store the values to the history table while adding , editing or deleting.
     */
    private void storeInCodingHistory() {
        try {
            Date date = new Date();
            long time = date.getTime();
            //time when the operation is performed.
            Timestamp timestamp = new Timestamp(time);
            String empty = "";
            ResultSet getProjectId = st.executeQuery("select project_id from volume where volume_id = " + volume_id);
            while (getProjectId.next()) {
                project_id = getProjectId.getInt(1);
            }
 
            ResultSet getBatchId = st.executeQuery("select batch_id from child where child_id = " + child_id[0]);
            while (getBatchId.next()) {
                batch_id = getBatchId.getInt(1);
            }
            //Result set will return the sub process
            ResultSet getSubProcess = st.executeQuery("select sub_process from batch where batch_id = " + batch_id);
            while (getSubProcess.next()) {
                sub_process = getSubProcess.getInt(1);
            }
            

            PreparedStatement fromHistoryPrepareStatment = con.prepareStatement("select event,codinghistory_id from codinghistory " +
                                                          "where child_id = ? and field_name=? and event = ?");
            fromHistoryPrepareStatment.setInt(1, child_id[0]);
            fromHistoryPrepareStatment.setString(2, fieldName);
            fromHistoryPrepareStatment.setString(3, status);
            ResultSet fromHistoryResultSet = fromHistoryPrepareStatment.executeQuery();

            String[] value = fieldValue.split(" ");
            int words = value.length;
            String[] value1 = fieldValue.split(";");
            int tags = value1.length;

            if (fromHistoryResultSet.next()) {
                
                PreparedStatement updateHistoryPrepareStatement = con.prepareStatement(" update codinghistory set value = ?  , logged_time = ? ," +
                                                               " words = ?,characters = ? ,tags = ? ,sub_process =?" +
                                                               " where codinghistory_id =? ");
                updateHistoryPrepareStatement.setString(1, fieldValue);
                updateHistoryPrepareStatement.setTimestamp(2, timestamp);
                updateHistoryPrepareStatement.setInt(3, words);
                updateHistoryPrepareStatement.setInt(4, fieldValue.length());
                updateHistoryPrepareStatement.setInt(5, tags);
                updateHistoryPrepareStatement.setInt(7, fromHistoryResultSet.getInt(2));
                if(sub_process >0){
                 updateHistoryPrepareStatement.setInt(6,sub_process);  
                }else{
                 updateHistoryPrepareStatement.setInt(6,0);     
                } 
                updateHistoryPrepareStatement.executeUpdate();
                updateHistoryPrepareStatement.close();
            } else {
                PreparedStatement insertHistoryPrepareStatement = con.prepareStatement("insert into codinghistory(child_id,project_id," +
                                                                "volume_id,batch_id," +
                                                                "field_name," +
                                                                "field_type," +
                                                                "value," +
                                                                "event," +
                                                                "user_id," +
                                                                "logged_time," +
                                                                "words," +
                                                                "characters,tags,sub_process) " +
                                                                "values(?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
                insertHistoryPrepareStatement.setInt(1, child_id[0]);
                insertHistoryPrepareStatement.setInt(2, project_id);
                insertHistoryPrepareStatement.setInt(3, volume_id);
                insertHistoryPrepareStatement.setInt(4, batch_id);
                insertHistoryPrepareStatement.setString(5, fieldName);
                insertHistoryPrepareStatement.setString(6, fieldType);
                insertHistoryPrepareStatement.setString(7, fieldValue);
                insertHistoryPrepareStatement.setString(8, status);
                insertHistoryPrepareStatement.setInt(9, task.getUsersId());
                insertHistoryPrepareStatement.setTimestamp(10, timestamp);
                insertHistoryPrepareStatement.setInt(11, words);
                insertHistoryPrepareStatement.setInt(12, fieldValue.length());
                insertHistoryPrepareStatement.setInt(13, tags);
                if(sub_process >0){
                 insertHistoryPrepareStatement.setInt(14,sub_process);  
                }else{
                 insertHistoryPrepareStatement.setInt(14,0);     
                }                                   
                insertHistoryPrepareStatement.executeUpdate();
                insertHistoryPrepareStatement.close();
            }
        } catch (SQLException ex) {            
            CommonLogger.printExceptions(this, "Exception during storing into coding history.", ex);
        }
    }

    /**
     * added to save the QAIR values
     */ 
    private void storeQair() {
        
        int sampling_id = 0;
        int batchId = 0;
        String bates_number = "";
        int coderId = 0;
        int checkerId = 0;
        String coded_data = "";
        String correction_data = "";
        String error_type = "";
        try {
            ResultSet fromSamplingResutlSet = st.executeQuery("select sampling_id from sampling where qair_number = 0 and " +
                                            "volume_id = " + volume_id);
            while (fromSamplingResutlSet.next()) {
                sampling_id = fromSamplingResutlSet.getInt(1);
            }

            ResultSet selectBatchResultSet = st.executeQuery("select batch_id from child  where child_id = " + child_id[0]);
            while (selectBatchResultSet.next()) {
                batchId = selectBatchResultSet.getInt(1);
            }
            ResultSet rs3 = st.executeQuery("select bates_number from page  where child_id = " + child_id[0]);
            while (rs3.next()) {
                bates_number = rs3.getString(1);
            }
            PreparedStatement selectUserPrepareStatement = con.prepareStatement("select user_id, value from codinghistory " +
                                                         "where child_id = ? " +
                                                         "and field_name=? and event ='CodingQC'");
            selectUserPrepareStatement.setInt(1, child_id[0]);
            selectUserPrepareStatement.setString(2, fieldName);
            ResultSet selectUserResultSet = selectUserPrepareStatement.executeQuery();
            if (selectUserResultSet.next()) {
                coderId = selectUserResultSet.getInt(1);
                coded_data = selectUserResultSet.getString(2);
            }
            PreparedStatement userFromHistoryPrepareStatement = con.prepareStatement("select user_id, value,error_type from codinghistory " +
                                                           "where child_id = ? and field_name=? and event ='QA'");
            userFromHistoryPrepareStatement.setInt(1, child_id[0]);
            userFromHistoryPrepareStatement.setString(2, fieldName);
            ResultSet userFromHistoryResultSet = userFromHistoryPrepareStatement.executeQuery();
            if (userFromHistoryResultSet.next()) {
                checkerId = userFromHistoryResultSet.getInt(1);
                correction_data = userFromHistoryResultSet.getString(2);
                error_type = userFromHistoryResultSet.getString(3);
            }
            PreparedStatement insertQairPreparedStatement = con.prepareStatement("insert into  qair(" +
                                                                        "sampling_id," +
                                                                        "batch_id,record," +
                                                                        "coder_id," +
                                                                        "checker_id," +
                                                                        "field_name," +
                                                                        "field_data," +
                                                                        "correction_data," +
                                                                        "error_type) " +
                                                            "values(?,?,?,?,?,?,?,?,?)");
            insertQairPreparedStatement.setInt(1, sampling_id);
            insertQairPreparedStatement.setInt(2, batchId);
            insertQairPreparedStatement.setString(3, bates_number);
            insertQairPreparedStatement.setInt(4, coderId);
            insertQairPreparedStatement.setInt(5, checkerId);
            insertQairPreparedStatement.setString(6, fieldName);
            insertQairPreparedStatement.setString(7, coded_data);
            insertQairPreparedStatement.setString(8, correction_data);
            insertQairPreparedStatement.setString(9, error_type);
            insertQairPreparedStatement.executeUpdate();
            insertQairPreparedStatement.close();

        } catch (SQLException ex) {            
            CommonLogger.printExceptions(this, "Exception during storing into qair", ex);
        }
    }

    /**
     * Write the values 
     *       1. value    
     *       2. longvalue
     *       3. namevalue
     */
    private void write() {
        try {
            if (level.equals("L2")) {
                ResultSet selectChildResultSet = st.executeQuery("select child_id from page where page_id=" + pageId);
                if (selectChildResultSet.next()) {
                    child_id[0] = selectChildResultSet.getInt(1);
                    int page_id = 0;
                    ResultSet selectPageResultSet = st.executeQuery("select page_id ,boundary_flag from page where page_id=" + pageId);
                    if (selectPageResultSet.next()) {
                        page_id = selectPageResultSet.getInt(1);
                        String boundary = selectPageResultSet.getString(2);
                        if (boundary.equals("D")) {
                            pageId = page_id;
                        } else {
                            ResultSet selectPageFromPageResultSet = st.executeQuery("select page_id from page where volume_id = " + volume_id 
                                                            + " and page_id <" + page_id + " and original_flag ='D'");
                            while (selectPageFromPageResultSet.next()) {
                                pageId = selectPageFromPageResultSet.getInt(1);
                            }
                        }
                    }

                    ResultSet selectChildResultSe = st.executeQuery("select child_id  from project_l1 where page_id=" + pageId);
                    if (selectChildResultSe.next()) {
                        child_id[1] = selectChildResultSe.getInt(1);                
                    }

                }
            } else {          
                ResultSet selectFromProjectL1ResultSet = st.executeQuery("select child_id,page_id from project_l1 where project_l1_id=" + pageId);

                if (selectFromProjectL1ResultSet.next()) {
                    child_id[0] = selectFromProjectL1ResultSet.getInt(1);
                    int page_id = selectFromProjectL1ResultSet.getInt(2);

                    ResultSet childFromPageResultSet = st.executeQuery("select child_id  from page where page_id=" + page_id);
                    if (childFromPageResultSet.next()) {
                        child_id[1] = childFromPageResultSet.getInt(1);                  
                    }
                }

            }
            ResultSet getProjectIdResultSet = st.executeQuery("select project_id from volume where volume_id = " + volume_id);
            if (getProjectIdResultSet.next()) {
                project_id = getProjectIdResultSet.getInt(1);
            }
            writeValues("value", T_VALUE, writer);
            writeValues("longvalue", T_LONGVALUE, writer);
            writeNameValues("namevalue", T_NAMEVALUE, writer);            
      
            if (w == false) {
                writer.endElement();
                w = true;
            }

      
        } catch (SQLException e) {            
           CommonLogger.printExceptions(this, "Exception during getting pages.", e);
           Log.quit(e);
        } catch (IOException e) {            
            CommonLogger.printExceptions(this, "Exception during getting pages.", e);
            Log.quit(e);
        }
    }
 
    /**
     * write values for table <code>tableName</code>
     * @param tableName     Table name
     * @param elementName   Element name
     * @param writer        Writer to write the message
     * @throws java.sql.SQLException if any SQL error occured while retriving the record from DB.
     * @throws java.io.IOException If any i/o occured while writing the message.
     */
    private void writeValues(String tableName, String elementName, MessageWriter writer)
            throws SQLException, IOException {
        try {
            //added to get the all the field values (L1 and L2)         
            ResultSet rs = null;
            ResultSet selectFieldNameResultSet = null;
            ResultSet fieldTypeResultSet = null;
            for (int i = 0; i < child_id.length; i++) {                
                Statement selectFieldNameStatement = con.createStatement();
                selectFieldNameResultSet = selectFieldNameStatement.executeQuery("select field_name from " + tableName
                                                                                + " where child_id = " + child_id[i]);
                boolean flag = true;
                while (selectFieldNameResultSet.next()) {
                    flag = false;
                    String field_Name = selectFieldNameResultSet.getString(1);
                    Statement fieldTypeStatement = con.createStatement();
                    fieldTypeResultSet = fieldTypeStatement.executeQuery("select field_type from projectfields where project_id = " 
                                                + project_id + " and field_name ='" + field_Name + "'");
                    if (fieldTypeResultSet.next()) {
                        int count = 0;
                        String field_type = fieldTypeResultSet.getString(1);
                        if (field_type.equals("text") || field_type.equals("date")) {
                            if (status_level != null && status_level.equals("QA")) {                      
                                rs = st.executeQuery("select  V.field_name,V.value" 
                                                        + " from " + tableName + " V where V.child_id =" 
                                                        + child_id[i] 
                                                        + " " 
                                                        + " UNION" 
                                                        + " select  C.field_name,C.value" 
                                                        + " from codinghistory C where C.child_id =" 
                                                        + child_id[i]
                                                        + "  AND C.event = 'QA'");
                            } else {                          
                                rs = st.executeQuery("select field_name, value"
                                                    + " from " + tableName + " where child_id=" + child_id[i] 
                                                    + " order by field_name, sequence");
                            }

                            priorFieldName = null;
                            buffer = new StringBuffer();

                            while (rs.next()) {
                                count++;
                                String _fieldName = rs.getString(1);
                                String _value = rs.getString(2);
                                System.out.println("VALUE : " + _value);
                                if (_fieldName.equals(priorFieldName)) {
                                    buffer.append("; ");
                                } else {
                                    flush(elementName);
                                    priorFieldName = _fieldName;
                                }
                                buffer.append(_value);
                            }
                        }
                        if (count > 0) {
                            flush(elementName);
                        }
                    }
                }
                if (flag) {
                    selectFieldNameResultSet = selectFieldNameStatement.executeQuery("select field_name from codinghistory" +
                                                                                     " where child_id = " + child_id[i]);
                    while (selectFieldNameResultSet.next()) {
                        String field_Name = selectFieldNameResultSet.getString(1);
                        Statement stObj = con.createStatement();
                        fieldTypeResultSet = stObj.executeQuery("select field_type from projectfields where " +
                                                                "project_id = " + project_id
                                                                 + " and field_name ='" + field_Name + "'");
                        if (fieldTypeResultSet.next()) {
                            int count = 0;
                            String field_type = fieldTypeResultSet.getString(1);
                            if (field_type.equals("text") || field_type.equals("date")) {
                                if (status_level != null && status_level.equals("QA")) {
                                    rs = st.executeQuery("select  V.field_name,V.value" + " from " + tableName 
                                                        + " V where V.child_id =" + child_id[i] + " " 
                                                        + " UNION" 
                                                        + " select  C.field_name,C.value" 
                                                        + " from codinghistory C where C.child_id =" + child_id[i] 
                                                        + " AND C.event = 'QA'");
                                } else {
                                    rs = st.executeQuery("select field_name, value" + " from " + tableName 
                                                        + " where child_id=" + child_id[i] 
                                                        + " order by field_name, sequence");
                                }

                                priorFieldName = null;
                                buffer = new StringBuffer();
                                while (rs.next()) {
                                    count++;
                                    String _fieldName = rs.getString(1);
                                    String _value = rs.getString(2);
                                    if (_fieldName.equals(priorFieldName)) {
                                        buffer.append("; ");
                                    } else {
                                        flush(elementName);
                                        priorFieldName = _fieldName;
                                    }
                                    buffer.append(_value);
                                }
                            }
                            if (count > 0) {
                                flush(elementName);
                            }
                        }
                    }
                }
            }
        } catch (SQLException e) {            
            CommonLogger.printExceptions(this, "Exception during writing field value of type text/date.", e);
            Log.quit(e);
        }
    }

    /**
     * write values for table namevalue
     */ 
    private void writeNameValues(String tableName, String elementName, MessageWriter writer)
            throws SQLException, IOException {
        try {
            //added to get the all the field values (L1 and L2)
            ResultSet rs = null;
            ResultSet selectFieldNameResultSet = null;
            ResultSet fieldTypeResultSet = null;
            for (int i = 0; i < child_id.length; i++) {
                Statement selectFieldNameStatement = con.createStatement();
                selectFieldNameResultSet = selectFieldNameStatement.executeQuery("select field_name from " + tableName + " where child_id = " + child_id[i]);
                boolean flag = true;
                while (selectFieldNameResultSet.next()) {
                    flag = false;
                    String field_Name = selectFieldNameResultSet.getString(1);
                    Statement fieldTypeStatement = con.createStatement();
                    fieldTypeResultSet = fieldTypeStatement.executeQuery("select field_type from projectfields where project_id = " 
                                                + project_id + " and field_name ='" + field_Name + "'");
                    if (fieldTypeResultSet.next()) {
                        int count = 0;
                        String field_type = fieldTypeResultSet.getString(1);
                        if (field_type.equals("name")) {
                            if (status_level != null && status_level.equals("QA")) {

                                rs = st.executeQuery("select V.field_name, V.last_name, V.first_name, V.middle_name, V.organization" 
                                        + " from " + tableName + " V where V.child_id=" + child_id[i]
                                        + " union " + " select  C.field_name,C.value,'','',''" 
                                        + " from codinghistory C where C.child_id =" + child_id[i] 
                                        + " AND C.event = 'QA'");
                            } else {

                                rs = st.executeQuery("select field_name, last_name, first_name, middle_name, organization" 
                                        + " from " + tableName + " where child_id=" + child_id[i]);
                            }

                            priorFieldName = null;
                            buffer = new StringBuffer();
                            while (rs.next()) {
                                count++;
                                String fieldName = rs.getString(1);
                                String lastName = rs.getString(2);
                                String firstName = rs.getString(3);
                                String middleName = rs.getString(4);
                                String organization = rs.getString(5);
                                if (fieldName.equals(priorFieldName)) {
                                    buffer.append("; ");
                                } else {
                                    flush(elementName);
                                    priorFieldName = fieldName;
                                }
                                if (lastName.length() > 0) {
                                    buffer.append(lastName);
                                }
                                if (firstName.length() > 0) {
                                    buffer.append(", ");
                                    buffer.append(firstName);
                                }
                                if (middleName.length() > 0) {
                                    buffer.append(' ');
                                    buffer.append(middleName);
                                }
                                if (organization.length() > 0) {
                                    buffer.append(" / ");
                                    buffer.append(organization);
                                }
                            }
                        }
                        if (count > 0) {
                            flush(elementName);
                        }
                    }
                }
                if (flag) {
                    selectFieldNameResultSet = selectFieldNameStatement.executeQuery("select field_name from codinghistory where child_id = " + child_id[i]);
                    while (selectFieldNameResultSet.next()) {
                        flag = false;
                        String field_Name = selectFieldNameResultSet.getString(1);
                        Statement selectFieldTypeStatement = con.createStatement();
                        fieldTypeResultSet = selectFieldTypeStatement.executeQuery("select field_type from projectfields where project_id = " 
                                + project_id + " and field_name ='" + field_Name + "'");
                        if (fieldTypeResultSet.next()) {
                            int count = 0;
                            String field_type = fieldTypeResultSet.getString(1);
                            if (field_type.equals("name")) {
                                if (status_level != null && status_level.equals("QA")) {
                                    rs = st.executeQuery("select V.field_name, V.last_name, V.first_name, V.middle_name, V.organization" 
                                            + " from " + tableName + " V where V.child_id=" + child_id[i] + " union " 
                                            + " select  C.field_name,C.value,'','',''" 
                                            + " from codinghistory C where C.child_id =" + child_id[i] + " AND C.event = 'QA'");
                                } else {
                                    rs = st.executeQuery("select field_name, last_name, first_name, middle_name, organization"
                                            + " from " + tableName + " where child_id=" + child_id[i]);
                                }
                                priorFieldName = null;
                                buffer = new StringBuffer();
                                while (rs.next()) {
                                    count++;
                                    String fieldName = rs.getString(1);
                                    String lastName = rs.getString(2);
                                    String firstName = rs.getString(3);
                                    String middleName = rs.getString(4);
                                    String organization = rs.getString(5);
                                    if (fieldName.equals(priorFieldName)) {
                                        buffer.append("; ");
                                    } else {
                                        flush(elementName);
                                        priorFieldName = fieldName;
                                    }
                                    if (lastName.length() > 0) {
                                        buffer.append(lastName);
                                    }
                                    if (firstName.length() > 0) {
                                        buffer.append(", ");
                                        buffer.append(firstName);
                                    }
                                    if (middleName.length() > 0) {
                                        buffer.append(' ');
                                        buffer.append(middleName);
                                    }
                                    if (organization.length() > 0) {
                                        buffer.append(" / ");
                                        buffer.append(organization);
                                    }
                                }
                            }
                            if (count > 0) {
                                flush(elementName);
                            }
                        }
                    }
                }
            }

        } catch (SQLException e) {            
            CommonLogger.printExceptions(this, "Exception during writing field value of type name.", e);
            Log.quit(e);
        }
    }

    // write the value that has been constructed in buffer, if any
    private void flush(String elementName) throws IOException {
        // wbe 2004-09-02 Write even if blank, because it could
        // be an explicit blank for a field with default

        if (priorFieldName != null) {

            // start the value_list, if it's not already started
            //writer=null;
            if (w == true) {
                writer.startElement(T_VALUE_LIST);
                w = false;
            }
            // write the data from the buffer
            writer.startElement(elementName);
            writer.writeAttribute(A_NAME, priorFieldName);
            writer.writeContent(buffer.toString());
            writer.endElement();

            // empty the buffer
            buffer.setLength(0);
        }
    }

    /**
     * Store a list of values in the database for a given volume and child.
     * @param task the ServerTask for the client
     * @param volume_id the volume.volume_id containing the child
     * @param child_id the child.child_id of the coded data being stored
     * @param valueMap the coded data to store
     * @param status the status of the batch containing child_id for timesheet logging
     * @param pageCount the number of pages being updated for timesheet logging
     * @param rework true of the stored data is reworked, thus should be logged
     */
    public static void store(UserTask task, DBTask dbTask, MessageWriter writer, int volume_id, int child_id, 
            Map valueMap, String status, int pageCount, boolean rework, String processLevel)
            throws SQLException {
        (new ValueMapper(task, dbTask, writer, volume_id, child_id)).store(valueMap, status, pageCount, rework, processLevel);
    }

    private void store(Map valueMap, String status, int pageCount, boolean rework, String processLevel) throws SQLException {
        ResultSet rs;
        fieldCount = 0;
        rowsDeleted = 0;
        this.processLevel = processLevel;
        for (Iterator i = valueMap.entrySet().iterator(); i.hasNext();) {
            Map.Entry e = (Map.Entry) i.next();
            //Log.print(e.getKey() + ": " + e.getValue());
            this.status = status;
            fieldName = (String) e.getKey();
            fieldValue = (String) e.getValue();
            
            PreparedStatement select_vmap_ftype = con.prepareStatement(SQLQueries.SEL_VMAP_FTYPE);
            select_vmap_ftype.setInt(1, volume_id);
            select_vmap_ftype.setString(2, fieldName);
            rs = select_vmap_ftype.executeQuery();

            if (rs.next()) {

                String field_type = rs.getString(1);
                if (field_type.equals(IbaseConstants.DYNAMIC_FIELD_IS_NAME)) {
                    // names are stored in the namevalue table
                    fieldType = "name";
                } else {
                    // it is one of: 'text','unsigned','signed','date'
                    // and will be stored in the value or longvalue table, depending on size
                    fieldType = "value";
                }

                fieldSize = rs.getInt(2);
                minimumSize = rs.getInt(3);
                isRepeated = ("Yes".equals(rs.getString(4)));
                queryraised = rs.getString(5);
                queryanswered = rs.getString(6);
                l1_information = rs.getString(7);
            } else {
                Log.print("!!! (UpdateTable).updatePageValues: failure reading fieldname " + fieldName);
                return;
            }
            rs.close();
            if (status.equals("Coding") || status.equals("CodingQC") || status.equals("Masking")|| status.equals("ModifyErrors")) {
                storeInCodingHistory();
            } else if (status.equals("QA") && l1_information.equals("Document")) {
                storeInCodingHistory();
            }
            if (status.equals("QA")) {
                storeQair();
            }

            if (fieldType.equals("name") && !status.equals("QA") && !status.equals("Tally") && !status.equals("TallyQC")) {
                
                storeNameValues((String) e.getKey(), (String) e.getValue(), status, queryraised, queryanswered);
            } else {
                value = ((String) e.getValue()).split(";");
                if (fieldSize <= 255 && !status.equals("QA") && !status.equals("Tally") && !status.equals("TallyQC")) {   
                    
                    storeValues("value", (String) e.getKey(), (String) e.getValue(), status, queryraised, queryanswered);                
                }
                else if (fieldSize > 255 && !status.equals("QA") && !status.equals("Tally") && !status.equals("TallyQC")) {                 
                    storeValues("longvalue", (String) e.getKey(), (String) e.getValue(), status, queryraised, queryanswered);                
                }
            }

        }
        if (!task.isAdmin()) {
            // if this is not rework, make an event entry
            if (!rework) {
                if (rowsDeleted > 0) {                    
                } else {
                    EventLog.add(task, dbTask, volume_id, task.getLockBatchId(), status, /* child count */ 1, pageCount, fieldCount);
                }
            }
        }
    }

    /**
     * Inset the values to the dynamic table passing from the parameter <code>tableName</code>.
     * @param tableName  Table name in which the data needs to be stored.
     * @param name
     * @param dataString
     * @param status
     * @param queryraised
     * @param queryanswered
     * @throws java.sql.SQLException
     */
    private void storeValues(String tableName, String name, String dataString, String status, String queryraised, 
            String queryanswered) throws SQLException {
        boolean storeFlag = false;
        ResultSet fieldTypeResultSet = st.executeQuery("select field_level from projectfields where project_id =" + project_id 
                                     + " and field_name = '" + name + "'");
        if (fieldTypeResultSet.next()) {
            int level = fieldTypeResultSet.getInt(1);
            if (processLevel.equals("L1") && level == 1) {
                storeFlag = true;
            } else if (processLevel.equals("L2") && level == 0) {
                storeFlag = true;
            } else {
                storeFlag = false;
            }
        }
        if (storeFlag) {
            PreparedStatement deleteFromDynamicTable = con.prepareStatement("delete from " + tableName + " where child_id = ?" 
                                                           +"   and field_name = ?");
            deleteFromDynamicTable.setInt(1, child_id[0]);
            deleteFromDynamicTable.setString(2, name);
            rowsDeleted = rowsDeleted + deleteFromDynamicTable.executeUpdate();
            deleteFromDynamicTable.close();
            String data[];

            if (isRepeated) {
                data = dataString.split(";");
            } else {
                data = new String[]{dataString};
            }

            PreparedStatement insertIntoDynTablePrepareStatement = con.prepareStatement("insert into " + tableName 
                                    + "   (child_id, field_name, sequence, value,queryraised)" + " values (?,?,?,?,?)");

            boolean itemSaved = false;
            System.out.println("tableName----------> " +tableName);
            for (int i = 0; i < data.length; i++) {
                String value = data[i].trim();            
                if ("No".equals(queryraised) && "No".equals(queryanswered)) {                
                    if (value.length() > 0) {
                        itemSaved = true;
                        insertIntoDynTablePrepareStatement.setInt(1, child_id[0]);
                        insertIntoDynTablePrepareStatement.setString(2, name);
                        insertIntoDynTablePrepareStatement.setInt(3, i);
                        insertIntoDynTablePrepareStatement.setBytes(4, value.getBytes());                    
                        insertIntoDynTablePrepareStatement.setString(5, "No");                        
                        insertIntoDynTablePrepareStatement.executeUpdate();
                    }             
                } 
                else if ("Yes".equals(queryraised) || "Yes".equals(queryanswered)) {                
                    itemSaved = true;
                    insertIntoDynTablePrepareStatement.setInt(1, child_id[0]);
                    insertIntoDynTablePrepareStatement.setString(2, name);
                    insertIntoDynTablePrepareStatement.setInt(3, i);
                    insertIntoDynTablePrepareStatement.setBytes(4, value.getBytes());              
                    insertIntoDynTablePrepareStatement.setString(5, "Yes");                    
                    insertIntoDynTablePrepareStatement.executeUpdate();                
                }
              
                // We save blank, because it could be a non-required field with default
                if (!itemSaved) {
                    insertIntoDynTablePrepareStatement.setInt(1, child_id[0]);
                    insertIntoDynTablePrepareStatement.setString(2, name);
                    insertIntoDynTablePrepareStatement.setInt(3, 0);
                    insertIntoDynTablePrepareStatement.setString(4, "");            
                    insertIntoDynTablePrepareStatement.setString(5, "No");                    
                    insertIntoDynTablePrepareStatement.executeUpdate();
                }
                fieldCount = fieldCount + 1;
                insertIntoDynTablePrepareStatement.close();
            }
            storeFlag = false;
        }
    }

    /**
     * Insert the data to the namevalue table
     * @param name
     * @param dataString
     * @param status
     * @param queryraised
     * @param queryanswered
     * @throws java.sql.SQLException
     */
    private void storeNameValues(String name, String dataString, String status, String queryraised, String queryanswered)
            throws SQLException {
        boolean storeFlag = false;
        ResultSet fieldLevelResutlSet = st.executeQuery("select field_level from projectfields where project_id =" 
                                                      + project_id  + " and field_name = '" + name + "'");
        if (fieldLevelResutlSet.next()) {
            int level = fieldLevelResutlSet.getInt(1);
            if (processLevel.equals("L1") && level == 1) {
                storeFlag = true;
            } else if (processLevel.equals("L2") && level == 0) {
                storeFlag = true;
            } else {
                storeFlag = false;
            }
        }
        if (storeFlag) {
            PreparedStatement deleteFromNameValuePrepareStatement = con.prepareStatement("delete from namevalue" 
                                                                                        + " where child_id = ?" 
                                                                                        + "   and field_name = ?");
            deleteFromNameValuePrepareStatement.setInt(1, child_id[0]);
            deleteFromNameValuePrepareStatement.setString(2, name);
            rowsDeleted = rowsDeleted + deleteFromNameValuePrepareStatement.executeUpdate();
            deleteFromNameValuePrepareStatement.close();

            String data[];
            if (isRepeated) {
                data = dataString.split(";");
            } else {
                data = new String[]{dataString};
            }

            // Note.  we do save blank, because it could be a non-required field with default
            // We should never see blanks in a repeated field, except as the only item

            PreparedStatement insertInto_namevalue_PrepareStatement = con.prepareStatement("insert into namevalue (child_id, " +
                                                                                  "field_name, " +
                                                                                  "sequence, " +
                                                                                  "last_name, " +
                                                                                  "first_name, " +
                                                                                  "middle_name, " +
                                                                                  "organization, " +
                                                                                  "value,queryraised)" +
                                                                                  " values (?,?,?,?,?,?,?,?,?)");

            boolean itemSaved = false;
            for (int i = 0; i < data.length; i++) {

                String nameData = data[i].trim();


                String firstName = "";
                String lastName = "";
                String middleName = "";
                String organization = "";

                int slashPos = nameData.lastIndexOf('/');
                // If no name, discard the slash - use org as name
                if (slashPos == 0) {
                    nameData = nameData.substring(1).trim();
                    slashPos = -1;
                }
                if (slashPos >= 0) {
                    if (slashPos < nameData.length()) {
                        organization = nameData.substring(slashPos + 1).trim();
                    }
                    nameData = nameData.substring(0, slashPos).trim();
                }
                int commaPos = nameData.indexOf(",");
                if (commaPos == 0) {
                    nameData = nameData.substring(1).trim();
                    commaPos = -1;
                }
                if (commaPos < 0) {
                    lastName = nameData;
                } else {
                    lastName = nameData.substring(0, commaPos).trim();
                    nameData = nameData.substring(commaPos + 1).trim();
                    int spacePos = nameData.lastIndexOf(' ');
                    if (spacePos < 0) {
                        firstName = nameData.trim();
                    } else {
                        firstName = nameData.substring(0, spacePos).trim();
                        middleName = nameData.substring(spacePos + 1).trim();
                    }
                }             
                if ("No".equals(queryraised) && "No".equals(queryanswered)) {
                    //existing code
                    if (lastName.length() > 0) {
                        itemSaved = true;
                        insertInto_namevalue_PrepareStatement.setInt(1, child_id[0]);
                        insertInto_namevalue_PrepareStatement.setString(2, name);
                        insertInto_namevalue_PrepareStatement.setInt(3, i);
                        insertInto_namevalue_PrepareStatement.setString(4, lastName);
                        insertInto_namevalue_PrepareStatement.setString(5, firstName);
                        insertInto_namevalue_PrepareStatement.setString(6, middleName);
                        insertInto_namevalue_PrepareStatement.setString(7, organization);
                        insertInto_namevalue_PrepareStatement.setString(8, data[i].trim());                  
                        insertInto_namevalue_PrepareStatement.setString(9, "No");                        
                        insertInto_namevalue_PrepareStatement.executeUpdate();

                    }               
                } 
                else if ("Yes".equals(queryraised) || "Yes".equals(queryanswered)) {                  
                    itemSaved = true;
                    insertInto_namevalue_PrepareStatement.setInt(1, child_id[0]);
                    insertInto_namevalue_PrepareStatement.setString(2, name);
                    insertInto_namevalue_PrepareStatement.setInt(3, i);
                    insertInto_namevalue_PrepareStatement.setString(4, lastName);
                    insertInto_namevalue_PrepareStatement.setString(5, firstName);
                    insertInto_namevalue_PrepareStatement.setString(6, middleName);
                    insertInto_namevalue_PrepareStatement.setString(7, organization);
                    insertInto_namevalue_PrepareStatement.setString(8, data[i].trim());
                    insertInto_namevalue_PrepareStatement.setString(9, "Yes");
                    insertInto_namevalue_PrepareStatement.executeUpdate();                
                }    
                
                ResultSet getListingOccurrence = st.executeQuery("select field_value,marking,view_marking from listing_occurrence where project_id="+project_id+
                        " and child_id="+child_id[0]+" and field_name ='"+name+"'");
                //update the listing marking
                if(getListingOccurrence.next()){
                 String fValue = getListingOccurrence.getString(1);
                 String marking = getListingOccurrence.getString(2);
                 String viewMarking = getListingOccurrence.getString(3);
                 
                 if((marking.equals("Yes") || viewMarking.equals("Yes")) && !fValue.equals(data[i].trim())){                             
                     PreparedStatement update_listing_Occurrence = con.prepareStatement("update listing_occurrence set marking = ? " +
                    " , view_marking =? where child_id=? and project_id=? and field_name =?  ");
                    update_listing_Occurrence.setString(1, "No");
                    update_listing_Occurrence.setString(2, "No");
                    update_listing_Occurrence.setInt(3, child_id[0]);
                    update_listing_Occurrence.setInt(4, project_id);
                    update_listing_Occurrence.setString(5, name);
                    update_listing_Occurrence.executeUpdate();
                 }
                }
                 
            }
            // We save blank, because it could be a non-required field with default
            if (!itemSaved) {
                insertInto_namevalue_PrepareStatement.setInt(1, child_id[0]);
                insertInto_namevalue_PrepareStatement.setString(2, name);
                insertInto_namevalue_PrepareStatement.setInt(3, 0);
                insertInto_namevalue_PrepareStatement.setString(4, "");
                insertInto_namevalue_PrepareStatement.setString(5, "");
                insertInto_namevalue_PrepareStatement.setString(6, "");
                insertInto_namevalue_PrepareStatement.setString(7, "");
                insertInto_namevalue_PrepareStatement.setString(8, "");             
                insertInto_namevalue_PrepareStatement.setString(9, "No");                
                insertInto_namevalue_PrepareStatement.executeUpdate();
            }
            fieldCount = fieldCount + 1;

            insertInto_namevalue_PrepareStatement.close();
            
           
            storeFlag = false;
        }
    }

    public static void storeErrorFlags(UserTask task, DBTask dbTask, int volume_id, int child_id, Map errorFlagMap, Map errorTypeMap)
            throws SQLException {

        Connection con = null;
        Statement st = null;
        try {

            con = dbTask.getConnection();
            st = dbTask.getStatement();

            // verify allowed volume
            ResultSet selectFromChildResultSet = st.executeQuery("select 0 from child where volume_id="+volume_id +" and child_id="+ child_id);
            if (!selectFromChildResultSet.next()) {
                Log.quit("storeErrorFlags: invalid volume");
            }
            selectFromChildResultSet.close();

            PreparedStatement ps = null;
            ResultSet selectFromfieldchangeResultSet = st.executeQuery("select * from fieldchange where child_id = " + child_id);
            if (selectFromfieldchangeResultSet.next()) {
                ps = con.prepareStatement(" UPDATE fieldchange  SET child_id=  ?, field_name = ?, codererror = ? " +
                                          " where child_id = " + child_id);
            } else {
                ps = con.prepareStatement(" insert into fieldchange values(?,?,?)");
            }

            Iterator it = errorFlagMap.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry entry = (Map.Entry) it.next();
                ps.setInt(1, child_id);
                ps.setString(2, (String) entry.getKey());
                ps.setBoolean(3, "Yes".equalsIgnoreCase((String) entry.getValue()));
                ps.executeUpdate();
            }
            ps.close();
         
            for (Iterator i = errorTypeMap.entrySet().iterator(); i.hasNext();) {
                Map.Entry e = (Map.Entry) i.next();
                String fieldName = (String) e.getKey();
                String error_type = (String) e.getValue();

                PreparedStatement selectCodingHistoryPrepareStatement = con.prepareStatement("select codinghistory_id from codinghistory " +
                                                             "where child_id = ? and field_name=? " +
                                                             "and volume_id = ? and event = 'QA'");
                selectCodingHistoryPrepareStatement.setInt(1, child_id);
                selectCodingHistoryPrepareStatement.setString(2, fieldName);
                selectCodingHistoryPrepareStatement.setInt(3, volume_id);
                ResultSet rsObj = selectCodingHistoryPrepareStatement.executeQuery();
                if (rsObj.next()) {
                    PreparedStatement updateCodingHistoryPrepareStatement = con.prepareStatement("update codinghistory set error_type=? " +
                                                                   "where codinghistory_id =? ");
                    updateCodingHistoryPrepareStatement.setString(1, error_type);
                    updateCodingHistoryPrepareStatement.setInt(2, rsObj.getInt(1));
                    updateCodingHistoryPrepareStatement.executeUpdate();
                    updateCodingHistoryPrepareStatement.close();
                }
            }

        } catch (SQLException sql) {           
            
            logger.error("SQL Exception during deleting the error types." + sql);
            StringWriter sw = new StringWriter();
            sql.printStackTrace(new PrintWriter(sw));
            logger.error(sw.toString());
        } catch (Exception exc) {                        
            logger.error("Exception during deleting the error types." + exc);
            StringWriter sw = new StringWriter();
            exc.printStackTrace(new PrintWriter(sw));
            logger.error(sw.toString());
        }
    }

    public static void clearErrorFlags(UserTask task, DBTask dbTask, int volume_id, int child_id)
            throws SQLException {
        Connection con = null;      
        try {            
            PreparedStatement delete_vmap_fchange = con.prepareStatement(SQLQueries.DEL_VMAP_FCHANGE);
            delete_vmap_fchange.setInt(1, child_id);
            delete_vmap_fchange.setInt(2, volume_id);
            delete_vmap_fchange.executeUpdate();

        } catch (SQLException sql) {          
            logger.error("SQL Exception during deleting the error types." + sql);
            StringWriter sw = new StringWriter();
            sql.printStackTrace(new PrintWriter(sw));
            logger.error(sw.toString());
        } catch (Exception exc) {
            logger.error("Exception during deleting the error types." + exc);
            StringWriter sw = new StringWriter();
            exc.printStackTrace(new PrintWriter(sw));
            logger.error(sw.toString());
        }
    }

    public void writeErrorFlags(UserTask task, DBTask dbTask, MessageWriter writer, Map errorFlagMap)
            throws IOException {

        writer.startElement(T_ERROR_FLAG_LIST);
        Iterator it = errorFlagMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry entry = (Map.Entry) it.next();
            writer.startElement(T_VALUE);
            writer.writeAttribute(A_NAME, (String) entry.getKey());
            writer.writeContent((String) entry.getValue());
            writer.endElement();
        }
        writer.endElement();
    }

    public void writeErrorTypes(UserTask task, DBTask dbTask, MessageWriter writer, Map errorTypeMap)
            throws IOException {

        writer.startElement(T_ERROR_TYPE_LIST);
        Iterator it = errorTypeMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry entry = (Map.Entry) it.next();
            writer.startElement(T_VALUE);
            writer.writeAttribute(A_NAME, (String) entry.getKey());
            writer.writeContent((String) entry.getValue());
            writer.endElement();
        }
        writer.endElement();
    }
}
