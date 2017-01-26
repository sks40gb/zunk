/* $Header: /home/common/cvsarea/ibase/dia/src/dbload/DataWriter.java,v 1.5.6.5 2007/03/21 10:45:10 nancy Exp $ */
package com.fossa.servlet.dbload;

import com.fossa.servlet.common.Log;
import com.fossa.servlet.common.MessageConstants;
import com.fossa.servlet.common.PopulateData;
import com.fossa.servlet.common.SQLQueries;
import com.fossa.servlet.dao.DBTask;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.apache.log4j.Logger;

/**
 * A class to write coded data to the database.
 */


public class DataWriter implements MessageConstants {

    PopulateData delimiter;
    String database;
    String host;
    int port;
    String dbuser;
    String password;

    /** 
     * ManagedTable numbers correspond to table position in server.Tables.java, from 0.
     * Used to update the changes table when a managed table is updated
     */ 
    /** id of current project based on the given project name */
    private int projectId = 0;
    /** id of current volume */
    private int volumeId = 0;   
    /** track how many pages have been written */
    int pageCnt  = 0;
    /** count of batches */
    int batchCnt = 0;
    
    int age = 0;

    /* the names and types of the fields for the project, in the order
     * defined for the project */
    private Map fieldType;

    /** general use PreparedStatement and ResultSet */
    private PreparedStatement pst_getVolumeIdAndProjectId;
    private PreparedStatement psDelete;
    private PreparedStatement psInsert;
    private PreparedStatement psInsertName;
    private Statement stmt;
    private ResultSet rs;
    private static Connection con = null;

    private static String message;
    private boolean replaceData;
    private static Logger logger = Logger.getLogger("com.fossa.servlet.dbload");

    public DataWriter(String database, String host, int port
                    , String dbuser, String password, String message
                    , PopulateData delimiter)
    throws DataWriterException {
    
	 this.host = host;
         this.database = database;
         this.port = port;
         this.dbuser = dbuser;
         this.password = password;
         this.message = message;
         this.delimiter = delimiter;
         
         try {             
             DBTask dbTask=new DBTask();
             con = dbTask.getConnection();
             con.setAutoCommit(false);
         } catch (SQLException e) {
             logger.error("Exception while DataWriter execution." + e);
             StringWriter sw = new StringWriter();
             e.printStackTrace(new PrintWriter(sw));
             logger.error(sw.toString());
             throw new DataWriterException("SQL error during DataWriter execution");
         }
    }

    /**
     * Write data rows from array of Xref objects into codingdb.  All of the cross-reference
     * data should have been collected into a the array, and all of the data
     * for a volume is written as a single database transaction.
     * @param rowMap - hashMap of coded data composed of bates/valueMap pairs
     * @param project - user-given project name to be written to project.project_name
     * @param givenVolume - volume name given by the user to be written to volume.volume_name
     * @param replaceData - overwrite existing data
     */
    public void write(Map rowMap, String volume
                      , boolean replaceData, Map fieldType
                      , String message) 
    throws DataWriterException {
        this.fieldType = fieldType;
        this.replaceData = replaceData;
        String bates;
        Map valueMap;
        String fieldName;
        ArrayList values;
        String value;
        int childId;

        try {
    
            stmt = con.createStatement();
            
            verifyVolume(volume);
            prepareStatements();

            for (Iterator i=rowMap.entrySet().iterator(); i.hasNext(); ) {
                Map.Entry e = (Map.Entry) i.next();
                bates = (String)e.getKey();
                valueMap = (HashMap)e.getValue();
    
                // get the child_id, then write the values to the db for each field
                childId = getChildId(volumeId, bates);
                if (childId > 0) {
                    for (Iterator it=valueMap.entrySet().iterator(); it.hasNext(); ) {
                        Map.Entry entry = (Map.Entry) it.next();
                        fieldName = (String)entry.getKey();
                        values = (ArrayList)entry.getValue();
                        String originalFieldName = fieldName;
                        if (delimiter.brs_format.equals("Yes")) {                            
                           PreparedStatement pst_getFieldName = con.prepareStatement(SQLQueries.SEL_DWRITER_FNAME);
                            
                            pst_getFieldName.setString(1, fieldName);
                            rs = pst_getFieldName.executeQuery();
                            if (rs.next()) {
                                fieldName = rs.getString(1);
                            }
                            rs.close();
                        }
                        //Log.print("(DataWriter.write) field/type " + fieldName
                        //          + fieldType.get(originalFieldName));
                        writeValues(childId, fieldName, values, replaceData
                                    , (String)fieldType.get(originalFieldName));
                    }
                } else {
                    message = "Bates not found: " + bates;
                    throw new DataWriterException ("Bates not found: " + bates);
                }
            }
            con.commit();
            con.close();
        } catch (SQLException e) {
             logger.error("Exception while executing write() of DataWriter." + e);
             StringWriter sw = new StringWriter();
             e.printStackTrace(new PrintWriter(sw));
             logger.error(sw.toString());
            throw new DataWriterException("SQL error during write() execution");
        }
    }

    private int getChildId(int volumeId, String bates)
    throws DataWriterException {
        int id = 0;
        try {
            PreparedStatement pst_getChildId = con.prepareStatement(SQLQueries.SEL_DWRITER_CHILDID);
            pst_getChildId.setInt(1, volumeId);
            pst_getChildId.setString(2, bates);
            rs = pst_getChildId.executeQuery();
            if (rs.next()) {
                id = rs.getInt(1);
            }
            rs.close();
        } catch (SQLException e) {
             logger.error("Exception while executing getChildId() of DataWriter." + e);
             StringWriter sw = new StringWriter();
             e.printStackTrace(new PrintWriter(sw));
             logger.error(sw.toString());
            throw new DataWriterException("SQL error during getChildId() execution");
        }
        return id;
    }

    private void writeValues(int childId, String fieldName
                             , ArrayList values, boolean replaceData, String type)
    throws DataWriterException {
        try {
            if (replaceData) {
                // User said replace the existing data, so delete all of it.
                psDelete = con.prepareStatement(SQLQueries.DEL_DWRITER_VALUE);  
                //Log.print("(DataWriter.writeValues) value(s) " + childId + "/" + fieldName);
                psDelete.setInt(1, childId);
                psDelete.setString(2, fieldName);
               
                psDelete.executeUpdate();
//                psDelete = con.prepareStatement("delete from longvalue"
//                                          +" where child_id = " + childId
//                                          +"   and field_name = ?");
                
                psDelete = con.prepareStatement(SQLQueries.DEL_DWRITER_LVDALUE);
                psDelete.setInt(1, childId);
                psDelete.setString(2, fieldName);                
                psDelete.executeUpdate();
       
                psDelete = con.prepareStatement(SQLQueries.DEL_DWRITER_NVALUE);
                
                psDelete.setInt(1, childId);
                psDelete.setString(2, fieldName);                
                psDelete.executeUpdate();
    	    }
            int seq = 0;
            for (int i = 0; i < values.size(); i++) {
                seq++;
                if (type.equals("namevalue")) {
                    String nameData = (String)values.get(i);
                    String firstName = "";
                    String lastName = "";
                    String middleName = "";
                    String organization = "";

                    int slashPos = nameData.lastIndexOf("/");
                    // If no name, discard the slash - use org as name
                    if (slashPos <= 0) {
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
                    psInsertName.setInt(1, childId);
                    psInsertName.setString(2, fieldName);
                    psInsertName.setInt(3, seq);
                    psInsertName.setString(4, lastName);
                    psInsertName.setString(5, firstName);
                    psInsertName.setString(6, middleName);
                    psInsertName.setString(7, organization);
                    psInsertName.setString(8, ((String)values.get(i)).trim());
                    psInsertName.executeUpdate();
                } else if (type.equals("date")) {
                    String value = (String)values.get(i);
                    if (value.equals(delimiter.missing_date)) {
                        value = "00000000"; // internal dia format for no date
                    } else if (! delimiter.date_format.equals("yyyyMMdd")) {
                        // not internal dia format for dates, so convert.
                        try {
                            DateFormat f = new SimpleDateFormat("yyyyMMdd");
                            Date valueDate = (Date)f.parseObject(value);
                            value = f.format(valueDate);
                        } catch (ParseException e) {
                            logger.error("Exception while bad date parse (CsvWriter.dateValue):." + e);
                            StringWriter sw = new StringWriter();
                            e.printStackTrace(new PrintWriter(sw));
                            logger.error(sw.toString());                            
                        }
                    }
                    writeValue("value", childId, fieldName, seq, value);
                } else {
                    String table;
                    String value = (String)values.get(i);
                    if (value.length() < 256) {
                        table = "value";
                    } else {
                        table = "longvalue";
                    }
                    writeValue(table, childId, fieldName, seq, value);
                }
            }
        } catch (SQLException e) {
            logger.error("Exception during writeValues() execution of DataWriter:." + e);
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            logger.error(sw.toString()); 
	    message = "SQL error during writeValues() execution " + e;            
	    throw new DataWriterException("SQL error during writeValues() execution");
        }
    }

    private void writeValue(String table, int childId, String fieldName
                            , int seq, String value)
    throws DataWriterException {
        try {
            //Log.print("(DataWriter.writeValue) " + fieldName + " " + table + " " + value);     
            psInsert = con.prepareStatement(SQLQueries.INS_DWRITER_NVALUE);
            
            psInsert.setInt(1, childId);
            psInsert.setString(2, fieldName);
            psInsert.setInt(3, seq);
            psInsert.setString(4, value);
            psInsert.executeUpdate();
        } catch (SQLException e) {
            logger.error("Exception during writeValue() execution of DataWriter:." + e);
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            logger.error(sw.toString());
            message = "SQL error during writeValues() execution " + e;            
            throw new DataWriterException("SQL error during writeValues() execution");
        }
    }

    private void verifyVolume(String volume)
    throws DataWriterException {
        try {
       
            rs = stmt.executeQuery(SQLQueries.SEL_DWRITER_AGE);
            if (rs.next()) {
                age = 1 + rs.getInt(1);
            }
            rs.close();
       
            PreparedStatement pst_updateServerAge = con.prepareStatement(SQLQueries.UPD_DWRITER_AGE);
            pst_updateServerAge.setInt(1,age);
            pst_updateServerAge.executeUpdate();
            
	    // does volume exist?
 
            pst_getVolumeIdAndProjectId = con.prepareStatement(SQLQueries.SEL_DWRITER_VID);
            
            pst_getVolumeIdAndProjectId.setString(1, volume);
            rs = pst_getVolumeIdAndProjectId.executeQuery();
            if (! rs.next()) {
                message = "Volume does not exist: "+volume;
                throw new DataWriterException(
                    "Volume does not exist: "+volume);
            }
            volumeId = rs.getInt(1);
            projectId = rs.getInt(2);
            rs.close();

            // Can't replace a volume in use

            PreparedStatement pst_getSessionTableValue = con.prepareStatement(SQLQueries.SEL_DWRITER_SESSION);
            pst_getSessionTableValue.setInt(1,volumeId);
            rs = pst_getSessionTableValue.executeQuery();
           
            if (rs.next()) {
                message = "Volume has locked batches: "+volume;
                //throw new DataWriterException(
                //    "Volume has locked batches: "+volume);
            }
            rs.close();
            // Can't replace a queued volume

            PreparedStatement ps_getTeamsVolume = con.prepareStatement(SQLQueries.SEL_DWRITER_TVOL);
            ps_getTeamsVolume.setInt(1,volumeId);
            rs = ps_getTeamsVolume.executeQuery();
                        
            if (rs.next()) {
                message = "Volume is in a teams's queue: "+volume;
                //throw new DataWriterException(
                //    "Volume is in a teams's queue: "+volume);
            }
            rs.close();

            // Can't replace a queued volume
            PreparedStatement pst_getTeamsQueue = con.prepareStatement(SQLQueries.SEL_DWRITER_TQUEUE);
            pst_getTeamsQueue.setInt(1,volumeId);
            rs = pst_getTeamsQueue.executeQuery();
            
            if (rs.next()) {
                message = "A batch in volume is in a teams's queue: "+volume;
                //throw new DataWriterException(
                //    "A batch in volume is in a teams's queue: "+volume);
            }
            rs.close();

            // Can't replace a queued volume
            PreparedStatement pst_getUserQueue = con.prepareStatement(SQLQueries.SEL_DWRITER_UQUEUE);
            pst_getUserQueue.setInt(1,volumeId);
            rs = pst_getUserQueue.executeQuery();
            
            if (rs.next()) {
                message = "A batch in volume is in a user's queue: "+volume;
                //throw new DataWriterException(
                //    "A batch in volume is in a user's queue: "+volume);
            }
            rs.close();

            // Can't replace an assigned volume
           
            PreparedStatement pst_getAssignment = con.prepareStatement(SQLQueries.SEL_DWRITER_ASSIGN);
            pst_getAssignment.setInt(1,volumeId);
            rs = pst_getAssignment.executeQuery();
            if (rs.next()) {
                message = "A batch in volume is assigned: "+volume;
                //throw new DataWriterException(
                //    "A batch in volume is assigned: "+volume);
            }
            rs.close();
        } catch (SQLException e) {
	    logger.error("Exception during verifyVolume() execution of DataWriter:." + e);
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            logger.error(sw.toString());
	    throw new DataWriterException("SQL Error During verifyVolume() execution");
        }
    }

    protected ArrayList loadFieldList(String project)
    throws DataWriterException {
        try {
            ArrayList fields = new ArrayList();  
            PreparedStatement pst_getFieldName = con.prepareStatement(SQLQueries.SEL_DWRITER_FIELDNAME);
            
            pst_getFieldName.setString(1, project);
            rs = pst_getFieldName.executeQuery();
            while (rs.next()) {
                fields.add(rs.getString(1));
            }
            return fields;
        } catch (SQLException e) {
            logger.error("Exception during loadFieldList() execution of DataWriter:." + e);
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            logger.error(sw.toString());
            throw new DataWriterException("SQL Error During loadFieldList() execution");
        }
    }

    /**
     * Load a HashMap with key=projectfields.project_name and
     * value=projectfields.project_type.
     * @param project - the project name provided by the user
     */
    protected Map loadFieldType(String project)
    throws DataWriterException {
        try {
            Map field_type = new HashMap();
            
            PreparedStatement pst_getFieldNameAndType = con.prepareStatement(SQLQueries.SEL_DWRITER_FTYPE);            
            pst_getFieldNameAndType.setString(1, project);
            rs = pst_getFieldNameAndType.executeQuery();
            while (rs.next()) {
                field_type.put(rs.getString(1), rs.getString(2));
            }
            return field_type;
        } catch (SQLException e) {
            logger.error("Exception during loadFieldType() execution of DataWriter:." + e);
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            logger.error(sw.toString());
            throw new DataWriterException("SQL Error During loadFieldType() execution");
        }
    }

    private void prepareStatements()
    throws DataWriterException {
        try {
    
            psInsertName = con.prepareStatement(SQLQueries.INS_DWRITER_NAMEVALUE);
            
        } catch (SQLException e) {
            logger.error("Exception during prepareStatements() execution of DataWriter:." + e);
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            logger.error(sw.toString());
            throw new DataWriterException("SQL Error During prepareStatements() execution");
        }
    }

    /** Make a JDBC connection.
     */
    //Not in Use
  /*  private static Connection makeConnection (
        String dbHost, int dbPort, String dbName, String dbUser, String dbPassword)
    throws DataWriterException {

        String dbUrl = "jdbc:mysql://"+dbHost+":"+dbPort+"/"+dbName;

        try {
            try {
                // The newInstance() call is a work around for some 
                // broken Java implementations
        
                Class.forName("org.gjt.mm.mysql.Driver").newInstance(); 
                //Class.forName("com.mysql.jdbc.Driver").newInstance(); 
             } catch (Exception ex) { 
                 logger.error("Exception while instantiating jdbc drivers:." + ex);
                 StringWriter sw = new StringWriter();
                 ex.printStackTrace(new PrintWriter(sw));
                 logger.error(sw.toString());
		 Log.quit("Can't Instantiate Driver: com.mysql.jdbc.Driver");
                
             }
            return DriverManager.getConnection(dbUrl, dbUser, dbPassword);

        } catch (SQLException e) {	    
            Log.write("Failed to connect with: " + dbUrl + " user: " + dbUser + " p: " + dbPassword);
            logger.error("Exception while establishing connection with db:." + e);
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            logger.error(sw.toString());
	    throw new DataWriterException("Failed to connect with: "+dbUrl);
        }
    }
*/
    /**
     * Exception to rollback and return error message if there
     * is an error while writing.
     */
    public static class DataWriterException extends Exception {

        DataWriterException(String msg) {
            super(msg);
            if (con != null) {
                try {
                    con.rollback();
                    con.close();
                } catch (SQLException e) {
                    logger.error("Exception while doing rollback or closing the connection:." + e);
                    StringWriter sw = new StringWriter();
                    e.printStackTrace(new PrintWriter(sw));
                    logger.error(sw.toString());
		    Log.quit("Sql error " + e);                    
                }
            }
        }
    }
}
