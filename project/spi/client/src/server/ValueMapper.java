/* $Header: /home/common/cvsarea/ibase/dia/src/server/ValueMapper.java,v 1.61.6.12 2006/03/21 16:42:41 nancy Exp $ */
package server;

import beans.IbaseConstants;
import common.Log;
import common.msg.MessageConstants;
import common.msg.MessageWriter;

import java.io.IOException;
import java.sql.*;
import java.util.Iterator;
import java.util.Map;
//import org.w3c.dom.Element;
//import org.w3c.dom.Node;
//import org.w3c.dom.NodeList;

/**
 * Conversion between XML and database tables for coded values.
 */
public class ValueMapper implements MessageConstants{
   
    private ServerTask task;
    private int volume_id;
    private int child_id;
    private int batch_id;

    // from project_fields, the size of the field
    private int fieldSize;

    // from project_fields, the minimum size of the field
    private int minimumSize;

    // from project_fields, the type of the field
    private String fieldType;

    // from project_fields, if the field is repeated
    private boolean isRepeated;

    // from the map, the fieldnames being provided for update
    private String fieldName;

    private int fieldCount;
    
    Connection con;
    Statement st;
    
    // 
    private String[] value;

    // MessageWriter for the task.  Null until something is written.
    private MessageWriter writer = null;
    // A StringBuffer for combining multiple values into one string
    private StringBuffer buffer;
    // The name of the field whose value is being constructed, or null.
    String priorFieldName;

    // Accumulate the number of rows deleted before the inserts to the value
    // tables for later use in determining whether the store is an add or an update.
    private int rowsDeleted;

    // Create an instance of ValueMapper, remembering task, volume and page
    private ValueMapper(ServerTask task, int volume_id, int child_id) {
        this.task = task;
        this.volume_id = volume_id;
        this.child_id = child_id;
        rowsDeleted = 0;
        con = task.getConnection();
        st = task.getStatement();
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
    public static void write(ServerTask task, int volume_id, int child_id) {
        (new ValueMapper(task,volume_id,child_id)).write();
    }

    private void write() {
        try {
            writeValues("value",T_VALUE);
            writeValues("longvalue",T_LONGVALUE);
            writeNameValues("namevalue",T_NAMEVALUE);
            if (writer != null) {
                writer.endElement();
            }
        } catch (SQLException e) {
            Log.quit(e);
        } catch (IOException e) {
            Log.quit(e);
        }
    }

    // write values for tables value and longvalue
    private void writeValues(String tableName, String elementName)
    throws SQLException, IOException
    {
        try {
            ResultSet rs = st.executeQuery(
                "select field_name, value"
                +" from "+tableName
                +" where child_id="+child_id
                +" order by field_name, sequence");

            priorFieldName = null;
            buffer = new StringBuffer();
            while (rs.next()) {
                String fieldName = rs.getString(1);
                String value = rs.getString(2);
                if (fieldName.equals(priorFieldName)) {
                    buffer.append("; ");
                } else{
                    flush(elementName);
                    priorFieldName = fieldName;
                }
                buffer.append(value);
            }
            flush(elementName);
        } catch (SQLException e) {
            Log.quit(e);
        }
    }

    // write values for table namevalue
    private void writeNameValues(String tableName, String elementName) 
    throws SQLException, IOException
    {

        try {
            ResultSet rs = st.executeQuery(
                "select field_name, last_name, first_name, middle_name, organization"
                +" from "+tableName
                +" where child_id="+child_id
                +" order by field_name, sequence");

            priorFieldName = null;
            buffer = new StringBuffer();
            while (rs.next()) {
                String fieldName = rs.getString(1);
                String lastName = rs.getString(2);
                String firstName = rs.getString(3);
                String middleName = rs.getString(4);
                String organization = rs.getString(5);
                if (fieldName.equals(priorFieldName)) {
                    buffer.append("; ");
                } else{
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
            flush(elementName);
        } catch (SQLException e) {
            Log.quit(e);
        }
    }

    // write the value that has been constructed in buffer, if any
    private void flush(String elementName) throws IOException {
        // wbe 2004-09-02 Write even if blank, because it could
        // be an explicit blank for a field with default

        if (priorFieldName != null) {
            // start the value_list, if it's not already started
            if (writer == null) {                
                writer = task.getMessageWriter();
                writer.startElement(T_VALUE_LIST);
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


    // NOT CURRENTLY USED
    //private void renameValues(ArrayList oldName, ArrayList newName) {
    //    try {
    //        for (int i = 0; i < oldName.size(); i++) {
    //            //Log.print("(ValueMapper).renameValues old/new " + oldName.get(i) + "/" 
    //            //          + newName.get(i));
    //            PreparedStatement pst1 = con.prepareStatement(
    //                "update value set field_name = ?"
    //                +" where volume_id like ?"
    //                +"   and field_name like ?");
    //            pst1.setString(1,(String)newName.get(i));
    //            pst1.setInt(2,volume_id);
    //            pst1.setString(3,(String)oldName.get(i));
    //            pst1.executeUpdate();
    //            //pst1.executeUpdate("commit");
    //            pst1.close();
    //            
    //            pst1 = con.prepareStatement(
    //                "update longvalue set field_name = ?"
    //                +" where volume_id like ?"
    //                +"   and field_name like ?");
    //            pst1.setString(1,(String)newName.get(i));
    //            pst1.setInt(2,volume_id);
    //            pst1.setString(3,(String)oldName.get(i));
    //            pst1.executeUpdate();
    //            //pst1.executeUpdate("commit");
    //            pst1.close();
    //            
    //            pst1 = con.prepareStatement(
    //                "update namevalue set field_name = ?"
    //                +" where volume_id like ?"
    //                +"   and field_name like ?");
    //            pst1.setString(1,(String)newName.get(i));
    //            pst1.setInt(2,volume_id);
    //            pst1.setString(3,(String)oldName.get(i));
    //            pst1.executeUpdate();
    //            //pst1.executeUpdate("commit");
    //            pst1.close();
    //        }
    //    } catch (SQLException e) {
    //        Log.quit(e);
    //    }
    //}

    // NOT CURRENTLY USED
    //private void deleteFromValues(String fieldName) {
    //    try {
    //        PreparedStatement pst1 = con.prepareStatement(
    //                "delete from value"
    //                +" where volume_id = ?"
    //                +"   and field_name = ?");
    //            pst1.setInt(1,volume_id);
    //            pst1.setString(2,fieldName);
    //            pst1.executeUpdate();
    //            pst1.close();
    //        pst1 = con.prepareStatement(
    //                "delete from longvalue"
    //                +" where volume_id = ?"
    //                +"   and field_name = ?");
    //            pst1.setInt(1,volume_id);
    //            pst1.setString(2,fieldName);
    //            pst1.executeUpdate();
    //            pst1.close();
    //        pst1 = con.prepareStatement(
    //                "delete from namevalue"
    //                +" where volume_id = ?"
    //                +"   and field_name = ?");
    //            pst1.setInt(1,volume_id);
    //            pst1.setString(2,fieldName);
    //            pst1.executeUpdate();
    //            pst1.close();
    //            
    //    } catch (SQLException e) {
    //        Log.quit(e);
    //    }
    //
    //}

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
    public static void store(ServerTask task, int volume_id, int child_id
                             , Map valueMap, String status, int pageCount, boolean rework)
    throws SQLException {
        (new ValueMapper(task,volume_id,child_id)).store(valueMap, status, pageCount, rework);
    }

    private void store(Map valueMap, String status, int pageCount, boolean rework) throws SQLException {
        ResultSet rs;
        fieldCount = 0;
        rowsDeleted = 0;
        for (Iterator i=valueMap.entrySet().iterator(); i.hasNext(); ) {
            Map.Entry e = (Map.Entry) i.next();
            //Log.print(e.getKey() + ": " + e.getValue());
            fieldName = (String)e.getKey();

            // get this projectfields row for this fieldname
            //Log.print("search for field: projectId="+project_id+" fieldName="+fieldName);
            rs = st.executeQuery(
                "select field_type, field_size, minimum_size, repeated"
                +" from projectfields F"
                +"   inner join volume using (project_id)"
                +" where volume_id ="+volume_id
                +"   and field_name ='"+fieldName+"'");
            if (rs.next()) {
                if (rs.getString(1).equals(IbaseConstants.DYNAMIC_FIELD_IS_NAME)) {
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
            } else {
                Log.print("!!! (UpdateTable).updatePageValues: failure reading fieldname "
                          + fieldName);
                return;
            }
            rs.close();
            if (fieldType.equals("name")) {
                storeNameValues((String)e.getKey(), (String)e.getValue());
                //name = new NameValue((String)e.getValue());
                //updateNamevalue(pageId);
            } else {
                value = ((String)e.getValue()).split(";");
                if (fieldSize <= 255) {
                    storeValues("value", (String)e.getKey(), (String)e.getValue());
                    //updateValue(pageId, "value");
                } else {
                    storeValues("longvalue", (String)e.getKey(), (String)e.getValue());
                    //updateValue(pageId, "longvalue");
                }
            }
        }
        if (! task.isAdmin()) {
            // if this is not rework, make an event entry
            if (! rework) {
                if (rowsDeleted > 0) {
                    // this could be QC or QA actual updates (errors)
                    //EventLog.update(task, volume_id, task.getLockBatchId(), rs.getString(1)
                    //                , /* child count */ 1, rs.getInt(2), fieldCount); 
                } else {
                    EventLog.add(task, volume_id, task.getLockBatchId(), status
                                 , /* child count */ 1, pageCount, fieldCount); 
                }
            }
        }
    }

    private void storeValues(String tableName, String name, String dataString) throws SQLException {

        PreparedStatement pst1 = con.prepareStatement(
            "delete from "+tableName
            +" where child_id = ?"
            +"   and field_name = ?");
        pst1.setInt(1,child_id);
        pst1.setString(2,name);
        rowsDeleted = rowsDeleted + pst1.executeUpdate();
        pst1.close();

        String data[];
        if (isRepeated) {
            data = dataString.split(";");
        } else {
            data = new String[] {dataString};
        }

        PreparedStatement pst2 = con.prepareStatement(
            "insert into "+tableName
            +"   (child_id, field_name, sequence, value)"
            +" values (?,?,?,?)");

        boolean itemSaved = false;
        for (int i = 0; i < data.length; i++) {
            String value = data[i].trim();
            if (value.length() > 0) {
                itemSaved = true;
                pst2.setInt(1,child_id);
                pst2.setString(2,name);
                pst2.setInt(3,i);
                pst2.setBytes(4,value.getBytes());
                pst2.executeUpdate();
            }
        }
        // We save blank, because it could be a non-required field with default
        if (! itemSaved) {
            pst2.setInt(1,child_id);
            pst2.setString(2,name);
            pst2.setInt(3,0);
            pst2.setString(4,"");
            pst2.executeUpdate();
        }
        fieldCount = fieldCount + 1;
        pst2.close();
    }

    private void storeNameValues(String name, String dataString) throws SQLException {

        PreparedStatement pst1 = con.prepareStatement(
            "delete from namevalue"
            +" where child_id = ?"
            +"   and field_name = ?");
        pst1.setInt(1,child_id);
        pst1.setString(2,name);
        rowsDeleted = rowsDeleted + pst1.executeUpdate();
        pst1.close();

        String data[];
        if (isRepeated) {
            data = dataString.split(";");
        } else {
            data = new String[] {dataString};
        }

        // Note.  we do save blank, because it could be a non-required field with default
        // We should never see blanks in a repeated field, except as the only item
        PreparedStatement pst2 = con.prepareStatement(
            "insert into namevalue"
            +"   (child_id, field_name, sequence, last_name, first_name, middle_name, organization, value)"
            +" values (?,?,?,?,?,?,?,?)");

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
                nameData = nameData.substring(0,slashPos).trim();
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
                    firstName = nameData.substring(0,spacePos).trim();
                    middleName = nameData.substring(spacePos + 1).trim();
                }
            }

            if (lastName.length() > 0) {
                itemSaved = true;
                pst2.setInt(1,child_id);
                pst2.setString(2,name);
                pst2.setInt(3,i);
                pst2.setString(4,lastName);
                pst2.setString(5,firstName);
                pst2.setString(6,middleName);
                pst2.setString(7,organization);
                pst2.setString(8, data[i].trim());
                pst2.executeUpdate();
            }
        }
        // We save blank, because it could be a non-required field with default
        if (! itemSaved) {
            pst2.setInt(1,child_id);
            pst2.setString(2,name);
            pst2.setInt(3,0);
            pst2.setString(4,"");
            pst2.setString(5,"");
            pst2.setString(6,"");
            pst2.setString(7,"");
            pst2.setString(8,"");
            pst2.executeUpdate();
        }
        fieldCount = fieldCount + 1;

        pst2.close();
    }

    public static void storeErrorFlags(ServerTask task, int volume_id, int child_id, Map errorFlagMap)
    throws SQLException {
        Connection con = task.getConnection();
        Statement st = task.getStatement();

        // verify allowed volume
        ResultSet rs = st.executeQuery(
            "select 0 from child"
            +" where volume_id="+volume_id
            +"   and child_id="+child_id);
        if (! rs.next()) {
            Log.quit("storeErrorFlags: invalid volume");
        }
        rs.close();

        PreparedStatement ps = con.prepareStatement(
            "replace into fieldchange"
            +" set child_id=?"
            +"   , field_name=?"
            +"   , codererror=?");
        Iterator it = errorFlagMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry entry = (Map.Entry) it.next();
            ps.setInt(1, child_id);
            ps.setString(2, (String) entry.getKey());
            ps.setBoolean(3, "Yes".equalsIgnoreCase((String) entry.getValue()));
            ps.executeUpdate();
        }
        ps.close();
    }

    public static void clearErrorFlags(ServerTask task, int volume_id, int child_id)
    throws SQLException {
        Statement st = task.getStatement();

        st.executeUpdate(
            "delete FC.* from fieldchange FC"
            +"   inner join child C using (child_id)"
            +" where C.child_id="+child_id
            +"   and C.volume_id="+volume_id);
    }

    public static void writeErrorFlags(ServerTask task, Map errorFlagMap)
    throws IOException {
        MessageWriter writer = task.getMessageWriter();
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
}
