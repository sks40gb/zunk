/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fossa.servlet.common;

import com.fossa.servlet.common.msg.MessageWriter;
import com.fossa.servlet.dao.DBTask;
import com.fossa.servlet.session.UserTask;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import org.apache.log4j.Logger;

/**
 *
 * @author Bala
 */
public class ProjectWriter implements MessageConstants {

    private int volumeId;
    private UserTask task;
    private DBTask dbTask;
    /** MessageWriter for the task.  Null until something is written. */
    private MessageWriter writer = null;
    /** A String for combining multiple values into one string */
    private String buffer;
    /** The name of the field whose value is being constructed, or null. */
    String fieldName;
    private static Logger logger = Logger.getLogger("com.fossa.servlet.common");
    /** Create an instance of ProjectWriter, a utility to write the XML
     * description of a project, saving task and volume_id.  Note
     * that volumeId may be any volume for the project.  (In particular,
     * viewers for Project Binder may span multiple volumes.)
     */

    private ProjectWriter(UserTask task, DBTask dbTask, MessageWriter writer, int volumeId) {
        this.task = task;
        this.dbTask = dbTask;
        this.volumeId = volumeId;
        this.writer = writer;
    }

    /**
     * Write projectfields for project associated with given volume.
     */
    public static void write(UserTask task, DBTask dbTask, MessageWriter writer, int volumeId) {
        (new ProjectWriter(task, dbTask, writer, volumeId)).write(writer);
    }

    /**
     * Write the XML description of project_fields.
     */
    private void write(MessageWriter writer) {
        try {
            loadProjectDescription(writer);
            if (writer != null) {
                writer.endElement();
            }

        } catch (SQLException e) {
            logger.error("Exception while writing XML descriptions of project fields." + e);
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            logger.error(sw.toString());
            Log.quit(e);
        } catch (IOException e) {
            logger.error("Exception while writing XML descriptions of project fields." + e);
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            logger.error(sw.toString());
            Log.quit(e);
        }
    }

    /**
     * Read all rows from project_fields for the current project_id and write
     * the values in XML format for download to the client.
     * <p>
     * Output format:  <value name="1#sequence">1</value>
     *                 <value name="1#field_name">Recip</value>
     *                 <value name="1#field_type">name</value>
     *                      ...
     */
    private void loadProjectDescription(MessageWriter writer)
            throws SQLException, IOException {
        try {
            Statement st = dbTask.getStatement();
            Log.print("(ProjectWriter.loadProjectDescription) volume " + volumeId);


            ResultSet rs = st.executeQuery("SELECT V.project_id,F.sequence,field_name,F.project_id,field_type,field_size," +
                    " minimum_size,repeated,required,unitize,spell_check,default_value,min_value,max_value,tablespec_id," +
                    " table_mandatory,mask,valid_chars,invalid_chars,charset,type_field,type_value,field_level, " +
                    " CASE WHEN (field_name = P.level_field_name) THEN 'Yes' ELSE 'No' END as 'is_level_field', " +
                    " field_group,queryraised,queryanswered,l1_information ,listing_marking FROM projectfields F " +
                    " inner join project P on P.project_id = F.project_id  inner join volume V " +
                    " on V.project_id = P.project_id WHERE V.volume_id = " + volumeId + " ORDER BY F.field_group, " +
                    " F.sequence");


            ResultSetMetaData rsmd = rs.getMetaData();
            while (rs.next()) {
                //Log.print("(ProjectWriter) columnCount " + rsmd.getColumnCount());
                int seq = rs.getInt(2);
                int max = rsmd.getColumnCount();
                for (int i = 2; i <= max; i++) {
                    fieldName = Integer.toString(seq) + "#" + rsmd.getColumnName(i);
                    //Log.print("(ProjectWriter) rsmd name is " + fieldName
                    //          + "/" + rsmd.getColumnType(i));
                    if (rsmd.getColumnType(i) == Types.VARCHAR || rsmd.getColumnType(i) == Types.CHAR /* enum */) {
                        //Log.print("    value is " + rs.getString(i));
                        buffer = rs.getString(i);
                    } else {
                        //Log.print("    value is " + rs.getInt(i));
                        buffer = Integer.toString(rs.getInt(i));
                    }
                    flush(T_VALUE, writer);
                }
            }
            rs.close();
        } catch (SQLException e) {
            logger.error("Exception while loading descriptions of project fields." + e);
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            logger.error(sw.toString());
            Log.quit(e);
        }
    }

    /** 
     * Write the value that has been constructed in buffer, if any.
     * @param elementName - XML element Name to give this message
     */
    private void flush(String elementName, MessageWriter writer) throws IOException {

        // if there's something in buffer
        if (buffer.length() > 0) {
            // start the value_list, if it's not already started
            if (writer == null) {
                writer.startElement(T_VALUE_LIST);
            }

            //Log.print("(ProjectWriter).flush " + fieldName + "/" + buffer);
            // write the data from the buffer
            writer.startElement(elementName);
            writer.writeAttribute(A_NAME, fieldName);
            writer.writeContent(buffer);
            writer.endElement();
            // empty the buffer
            buffer = "";
        }
    }
}
