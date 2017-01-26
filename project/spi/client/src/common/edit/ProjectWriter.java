/* $Header: /home/common/cvsarea/ibase/dia/src/common/edit/ProjectWriter.java,v 1.15.6.5 2006/03/21 16:42:41 nancy Exp $ */
package common.edit;

import common.Log;
import common.msg.MessageConstants;
import common.msg.MessageWriter;
import server.ServerTask;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;


/**
 * Writes the XML project description.
 */
public class ProjectWriter implements MessageConstants {
   
    private ServerTask task;
    private int volumeId;

    /** MessageWriter for the task.  Null until something is written. */
    private MessageWriter writer = null;

    /** A String for combining multiple values into one string */
    private String buffer;
    /** The name of the field whose value is being constructed, or null. */
    String fieldName;

    /** Create an instance of ProjectWriter, a utility to write the XML
     * description of a project, saving task and volume_id.  Note
     * that volumeId may be any volume for the project.  (In particular,
     * viewers for Project Binder may span multiple volumes.)
     */
    private ProjectWriter(ServerTask task, int volumeId) {
        this.task = task;
        this.volumeId = volumeId;
    }

    /**
     * Write projectfields for project associated with given volume.
     */
    public static void write(ServerTask task, int volumeId) {
        (new ProjectWriter(task, volumeId)).write();
    }

    /**
     * Write the XML description of project_fields.
     */
    private void write() {
        try {
            loadProjectDescription();
            if (writer != null) {
                writer.endElement();
            }
        } catch (SQLException e) {
            Log.quit(e);
        } catch (IOException e) {
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
    private void loadProjectDescription() 
    throws SQLException, IOException
    {
        try {
            Statement st = task.getStatement();
            Log.print("(ProjectWriter.loadProjectDescription) volume " + volumeId);
            ResultSet rs = st.executeQuery(
                "select V.project_id,"
                      +"F.sequence,"
                      +"field_name,"
                      +"field_type,"
                      +"field_size,"
                      +"minimum_size,"
                      +"repeated,"
                      +"required,"
                      +"unitize,"
                      +"spell_check,"
                      +"default_value,"
                      +"min_value,"
                      +"max_value,"
                      +"tablespec_id,"
                      +"table_mandatory,"
                      +"mask,"
                      +"valid_chars,"
                      +"invalid_chars,"
                      +"charset,"
                      +"type_field,"
                      +"type_value,"
                      +"field_level,"
                      +"if(field_name = P.level_field_name, 'Yes','No') as is_level_field,"
                      +"field_group"
                +" from projectfields F"
                +"   inner join project P using (project_id)"
                +"   inner join volume V using (project_id)"
                +" where V.volume_id="+volumeId
                +" order by F.field_group, F.sequence");
            ResultSetMetaData rsmd = rs.getMetaData();
            while (rs.next()) {
                //Log.print("(ProjectWriter) columnCount " + rsmd.getColumnCount());
                int seq = rs.getInt(2);
                int max = rsmd.getColumnCount();
                for (int i = 2; i <= max; i++) {
                    fieldName = Integer.toString(seq) + "#" + rsmd.getColumnName(i);
                    //Log.print("(ProjectWriter) rsmd name is " + fieldName
                    //          + "/" + rsmd.getColumnType(i));
                    if (rsmd.getColumnType(i) == Types.VARCHAR
                        || rsmd.getColumnType(i) == Types.CHAR /* enum */) {
                        //Log.print("    value is " + rs.getString(i));
                        buffer = rs.getString(i);
                    } else {
                        //Log.print("    value is " + rs.getInt(i));
                        buffer = Integer.toString(rs.getInt(i));
                    }
                    flush(T_VALUE);
                }
            }
            rs.close();
        } catch (SQLException e) {
            Log.quit(e);
        }
    }

    /** 
     * Write the value that has been constructed in buffer, if any.
     * @param elementName - XML element Name to give this message
     */
    private void flush(String elementName) throws IOException {
        // if there's something in buffer
        if (buffer.length() > 0) {

            // start the value_list, if it's not already started
            if (writer == null) {
                writer = task.getMessageWriter();
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
