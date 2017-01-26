/*
 * DbWriter.java
 *
 * Created on December 10, 2007, 6:57 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.fossa.servlet.dbload;

import com.fossa.servlet.command.Command_import_data;
import com.fossa.servlet.command.Mode;
import com.fossa.servlet.common.Log;
import com.fossa.servlet.common.MessageConstants;
import com.fossa.servlet.common.SQLQueries;
import com.fossa.servlet.dao.DBTask;
import com.fossa.servlet.server.valueobjects.BatchHistoryData;
import com.fossa.servlet.server.valueobjects.BatchProcessHistroyData;
import com.fossa.servlet.server.valueobjects.ProjectHistoryData;
import com.fossa.servlet.server.valueobjects.VolumeHistoryData;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.apache.log4j.Logger;

/**
 *
 * @author Bala
 */
public class DbWriter implements MessageConstants {

    // Amount to increment page number between pages
    // (Allows for page inserts, with too small a gap 
    // occurring very infrequently)
    final private static int PAGE_DELTA = 20;

    String database;
    String host;
    int port;
    String dbuser;
    String password;
    /** overwrite existing volume */
    boolean replaceVolume  = false;
    /** add a volume to the project */
    boolean appendVolume   = false;
    boolean split_documents = false;
    static boolean debug   = false;
    static boolean logging = false;

    /** user parameter - how many pages in a batch */
    int batchSpan = 0;
    /** user parameter - how many pages to search beyond batchSpan looking for where to break
     * a batch on a document boundary */
    int spanSearch = 0;
    boolean isL1Treatment = false;
    /** 
     * ManagedTable numbers correspond to table position in server.Tables.java, from 0.
     * Used to update the changes table when a managed table is updated
     */
    private final int PROJECT_TABLE = 3;
    private final int VOLUME_TABLE = 12;
    private final int UNITPRICE_TABLE = 16;
    private final int CUSTOMERPRICE_TABLE = 17;

    /** id of current project based on the given project name */
    private int projectId = 0;
    /** id of current volume */
    private int volumeId = 0;
    /** id of last batch written */
    private int batchId = 0;
    /** id of last range written */
    private int rangeId = 0;
    /** id of last child written */
    private int childId = 0;
    /** page.sequence of the last page written */
    private int lastRgt = 0;
    /** track how many pages have been written */
    int pageCnt  = 0;
    /** count of batches */
    int batchCnt = 0;
    /**  Number of pages in the current batch, reset to 0 as each new batch row is inserted */
    int spanCnt  = 0;
    /** Number of pages beyond batchSpan pages */
    int beyondSpan = 0;
    //document count 
     int docCnt  = 0;
    String volume;
    String original_volume = "";
    int volumeSequence = 0;
    int pageSeq = 0;
    
    int age = 0;
    int batchCount = 0;
   private String serverIP_port ="";
   String isUnitizeOptional ="";
   
   String internal_volume="";
   String  volume_completion_date ="";
   
    /** general use PreparedStatement and ResultSet */
    private PreparedStatement ps;
    PreparedStatement prepstmtCodingDb;
    PreparedStatement prepdbWriter;
    PreparedStatement prepstmtCusprice;
    private ResultSet rs;
	 private ResultSet rs1;
    Statement stmtCodingDb = null;
    ResultSet ressetWork;

    public Connection conCodingDb = null;
    public Connection conImageDb = null;

    private String message;
    private static Logger logger = Logger.getLogger("com.fossa.servlet.dbload");

    public DbWriter(String database, String host, int port
                    , String dbuser, String password, boolean replaceVolume
                    , boolean appendVolume, boolean split_documents, String message)
    {
	 this.host = host;
         this.database = database;
         this.port = port;
         this.dbuser = dbuser;
         this.password = password;
         this.replaceVolume = replaceVolume;
         this.appendVolume = appendVolume;
         this.split_documents = split_documents;
         this.message = message;
    }

    /**
     * Write data rows from array of Xref objects into codingdb.  All of the cross-reference
     * data should have been collected into a the array, and all of the data
     * for a volume is written as a single database transaction.
     * TBD: Probably want to make long inserts as prepared statements.
     * @param xrefData - array of xref objects describing the page data
     * @param project - user-given project name to be written to project.project_name
     * @param givenVolume - volume name given by the user to be written to volume.volume_name
     * @param batchspan - maximum number of pages to put in one unitize batch
     * @param spanSearch - number of pages to search beyond batchspan for a child
     * @param imagePath - drive & directory where the image data can be found
     */
    public void write(Xref[] xrefData, String project, String givenVolume
                      , int batchspan, int spanSearch, String imagePath,String serverIP_port ,String isUnitizeOptional,String internal_volume,String volume_completion_date)
    throws DbWriterException {         
        this.batchSpan = batchspan;
        this.spanSearch = spanSearch;
        original_volume = ((ImageXref) xrefData[0]).originalVolume;
        this.serverIP_port = serverIP_port;
        this.isUnitizeOptional = isUnitizeOptional;
        this.internal_volume = internal_volume;
        this.volume_completion_date = volume_completion_date;
        try {
            volume = givenVolume;
	    // look for different volumes in same xref file
            if (givenVolume == null) {
                volume = ((ImageXref) xrefData[0]).volume;
                //original_db = ((ImageXref) xrefData[0]).originalDb;
                for (int i=1; i < xrefData.length; i++) {
                    if (((ImageXref) xrefData[i]).volume != volume) {
                        message = "Multiple volume names in cross-reference file";
                        throw new DbWriterException (
                                "Multiple volume names in cross-reference file");
                    }
                }
            }
            if (volume.length() == 0) {
                message = "No volume name given";
                throw new DbWriterException ("No volume name given");
            }
       
	    // connect to the database
            //commented bala
            //conCodingDb = makeConnection(host,port,database,dbuser,password);
            DBTask dbTask=new DBTask();
            conCodingDb = dbTask.getConnection();
            stmtCodingDb = conCodingDb.createStatement();
            conCodingDb.setAutoCommit(false);
            rs = stmtCodingDb.executeQuery("SELECT age  FROM svrage WITH(UPDLOCK) WHERE dummy_key = 0");
            if (rs.next()) {
                age = 1 + rs.getInt(1);
            }
            rs.close();
            stmtCodingDb.executeUpdate("update svrage set age="+age+" where dummy_key = 0");
            
	    // DOES Project Exist???
            // We can have an active project, or we can have a deleted project

            prepstmtCodingDb = conCodingDb.prepareStatement(SQLQueries.SEL_DBWRITER_PID);
            prepstmtCodingDb.setString(1,project);
            ressetWork = prepstmtCodingDb.executeQuery();
            projectId = 0;
            boolean isActive = false;
            if (ressetWork.next()) {
                projectId = ressetWork.getInt(1);
                isActive = ressetWork.getBoolean(2);
                stmtCodingDb.executeUpdate(SQLQueries.UPD_DBWRITER_PROJ+projectId);
                
                ProjectHistoryData projectData = new ProjectHistoryData(conCodingDb, projectId);
                projectData.insertIntoHistoryTable(conCodingDb,Command_import_data.userId,Mode.EDIT);
                
            }
            if (! isActive) {
		// new project go ahead and insert
                if (projectId != 0) {
                    // TBD: user should be given a warning and the option to change name
                    Log.print ("Warning: NEW PROJECT HAS SAME NAME AS A DELETED PROJECT");
                }
		insertProject( stmtCodingDb, project );
                //PreparedStatement prepstmtLastInsert = conCodingDb.prepareStatement(
                //    "select last_insert_id()");
		//ressetWork = prepstmtLastInsert.executeQuery();
		//ressetWork.next();
                //projectId = ressetWork.getInt(1);
                //prepstmtLastInsert.close();
                //updateChanges(stmtCodingDb, PROJECT_TABLE, ressetWork.getInt(1));
            }  else {
		if( debug | logging ) Log.write("Attempting to replace or append volume "+volume+" for project "+project);
	    }

	    if( debug ) Log.write("Project Id: "+projectId);
            //prepstmtCodingDb.close();


	    // Check status of Volume - does it exist

            prepstmtCodingDb = conCodingDb.prepareStatement(SQLQueries.SEL_DBWRITER_PROJID);
            
	    if( debug | logging) Log.write("VOLUME: "+volume);
            prepstmtCodingDb.setString(1,volume);
            rs = prepstmtCodingDb.executeQuery();
	    // Did we get any hits?
            if (rs.next()) {
                if (replaceVolume) {
                    // A volume already exists under a different project name
                    if (rs.getInt(1) != projectId ) {
                        if ( debug | logging) Log.write ("Can't replace volume for project");
                        prepstmtCodingDb.close();
                        rs.close();
                        message = "Volume, "+volume+", already exists for a project.";
                        throw new DbWriterException(
                                "Volume, "+volume+", already exists for a project.");
                    } else  {
                        // proceed with replacement
                        volumeId = rs.getInt(2);
                        if ( debug | logging) Log.write ("Proceed with replacement " + volumeId);
                        rs.close();
    
                        // Can't replace a volume in use
                        rs = stmtCodingDb.executeQuery("SELECT 0  FROM session WHERE volume_id ="+volumeId);
                        if (rs.next()) {
                            message = "Volume has locked batches: "+volume;
                            throw new DbWriterException(
                                "Volume has locked batches: "+volume);
                        }
                        rs.close();
    
                        // Can't replace a queued volume
                        rs = stmtCodingDb.executeQuery(SQLQueries.SEL_DBWRITER_TVOL +volumeId);
//                        rs = stmtCodingDb.executeQuery("SELECT 0  FROM teamsvolume WHERE volume_id =" +volumeId);
                        
                        if (rs.next()) {
                            message = "Volume is in a teams's queue: "+volume;
                            throw new DbWriterException(
                                "Volume is in a teams's queue: "+volume);
                        }
                        rs.close();
    
                        // Can't replace a queued volume
                        rs = stmtCodingDb.executeQuery(SQLQueries.SEL_DBWRITER_TQ+volumeId);
                        
                        if (rs.next()) {
                            message = "A batch in volume is in a teams's queue: "+volume;
                            throw new DbWriterException(
                                "A batch in volume is in a teams's queue: "+volume);
                        }
                        rs.close();
    
                        // Can't replace a queued volume
                        rs = stmtCodingDb.executeQuery(SQLQueries.SEL_DBWRITER_UQ +volumeId);
                       
                        if (rs.next()) {
                            message = "A batch in volume is in a user's queue: "+volume;
                            throw new DbWriterException(
                                "A batch in volume is in a user's queue: "+volume);
                        }
                        rs.close();
    
                        // Can't replace an assigned volume
                        rs = stmtCodingDb.executeQuery(SQLQueries.SEL_DBWRITER_ASSIGN+volumeId);
                        
                        if (rs.next()) {
                            message = "A batch in volume is assigned: "+volume;
                            throw new DbWriterException(
                                "A batch in volume is assigned: "+volume);
                        }
                        rs.close();

                        // See if there is any coded data
                        rs = stmtCodingDb.executeQuery(SQLQueries.SEL_DBWRITER_TOP+ volumeId);
                        
                                                
                        if (rs.next()
                            && (rs.getObject(1) != null
                            || rs.getObject(2) != null
                            || rs.getObject(3) != null)
                            ) {
                            message = "A volume containing coded data cannot be overwritten."
                                +"\n\nYou may delete "+givenVolume+" using the Batching screen, then"
                                +"\nimport this volume.";
                            throw new DbWriterException(
                                "A volume containing coded data cannot be overwritten."
                                +"\n\nYou may delete "+givenVolume+" using the Batching screen, then"
                                +"\nimport this volume.");
                        }
                        Log.print("(DbWriter) no coded data");

                        //ok, go ahead and wipe out existing volume and children
                        stmtCodingDb.executeUpdate(
                            "delete from volume"
                            +" where volume_id="+volumeId);
                        updateChanges(stmtCodingDb, VOLUME_TABLE, volumeId);
                        
                        rs.close();
                        
                        rs = stmtCodingDb.executeQuery(
                        "select unitprice_id from unitprice"
                        +" where volume_id="+volumeId);
                        
                        Statement stNew = conCodingDb.createStatement();
                        stNew.executeUpdate(
                            "delete from unitprice"
                            +" where volume_id="+volumeId);
                        while (rs.next()) {
                            updateChanges(stmtCodingDb, UNITPRICE_TABLE, rs.getInt(1));
                        }
                        
                        rs.close();
                        Statement stObj = conCodingDb.createStatement();
                        rs = stObj.executeQuery(
                        "select customerprice_id from customerprice"
                        +" where volume_id="+volumeId);
                        stmtCodingDb.executeUpdate(
                            "delete from customerprice"
                            +" where volume_id="+volumeId);
                        while (rs.next()) {
                            updateChanges(stmtCodingDb, CUSTOMERPRICE_TABLE, rs.getInt(1));
                        }

                        // if batches already exist, save the active_group for use
                        // in the new batches.
                        //rs = stmtCodingDb.executeQuery(
                        //"select active_group from batch"
                        //+" where volume_id="+volumeId+" limit 1");
                        //if (rs.next()) {
                        //    activeGroup = rs.getInt(1) > 0 ? 1 : 0;
                        //}
                        stmtCodingDb.executeUpdate(
                            "delete from batch"
                            +" where volume_id="+volumeId);
                        stmtCodingDb.executeUpdate(
                            "delete from range"
                            +" where volume_id="+volumeId);
                        stmtCodingDb.executeUpdate(
                            "delete from child"
                            +" where volume_id="+volumeId);
                        stmtCodingDb.executeUpdate(
                            "delete from page"
                            +" where volume_id="+volumeId);
                        stmtCodingDb.executeUpdate(
                            "delete from pageissue"
                            +" where volume_id="+volumeId);
                        
                        setBatchCnt();
                        insertVolume(imagePath ,serverIP_port,internal_volume,volume_completion_date);
                    }
                } else {
                    // user did not say replace - new volume shouldn't exist
                    if (rs.getInt(1) != projectId ) {
                        if ( debug | logging) Log.write ("Can't replace volume for project");
                        prepstmtCodingDb.close();
                       // rs.close();
                        message = "Volume, "+volume+", already exists for a project.";
                        throw new DbWriterException(
                                "Volume, "+volume+", already exists for a project.");
                    } else {
                        message = "Volume: "+volume+" already exists for project: " + project;
                        throw new DbWriterException(
                                "Volume: "+volume+" already exists for project: " + project);
                    }
                }
            } else {
                // volume doesn't exist
                prepstmtCodingDb.close();

                setBatchCnt();
                insertVolume(imagePath,serverIP_port,internal_volume,volume_completion_date);
            }

            rs.close();

            // get the largest page.seq in the project
            ressetWork = stmtCodingDb.executeQuery("SELECT max(rgt) as 'max(rgt)' FROM project WHERE project_id ="+projectId); 
            
            if (ressetWork.next()) {
                pageSeq = ressetWork.getInt(1);
                if (debug) Log.write("Appending - retrieved sequence: "+pageSeq);
            }
                int j=0;
	    // traverse the Xref Array now              
            for (int i = 0; i < xrefData.length; i++) {
		j++;
               
		Xref theXref = xrefData[i];                
		// have to do an instanceof test because Xref type not being passed
                if (theXref instanceof ImageXref) {
		    if( debug ) Log.write("Do inserts of:"+i+" volumeId:"+volumeId+" projectId:"+projectId);
		    if( debug) Log.write("XREF: "+theXref+" instance of ImageXref");
		    pageSeq = pageSeq + PAGE_DELTA;

		    insertXref(stmtCodingDb, (ImageXref) theXref, volumeId, pageSeq);

		} else if (theXref instanceof IssueXref) {
		    if( debug) Log.write("XREF: "+theXref+" instance of IsueXref");
                    // get page.page_id for the latest page
                    ressetWork = stmtCodingDb.executeQuery(
                        "select top 1 page_id from page order by page_id desc ");
                        if (ressetWork.next()) {
                            insertXref(stmtCodingDb, (IssueXref) theXref
                                       , volumeId, ressetWork.getInt(1));
                        } else {
                            if( debug) Log.write("XREF: "+theXref+" can't get last_insert_id");
                        }

		} else {
		    if( debug) Log.write("XREF: "+theXref+" instance of ???");
		}
            }
            // end last volume, batch, range and child rows
                
            updateBatchRgt(stmtCodingDb);
            updateVolume(stmtCodingDb);
            updateProject(stmtCodingDb);
            updateRangeRgt(stmtCodingDb);
            updateChildRgt(stmtCodingDb);
            conCodingDb.commit();
            conCodingDb.close();
        } catch (SQLException e) {
	    logger.error("Exception while executing write() of DBWriter:." + e);
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            logger.error(sw.toString());
	    throw new DbWriterException("SQL Error During write() execution");
        }
    }

    private void insertVolume(String imagePath,String serverIP_port,String internal_volume,String volume_completion_date)
    throws DbWriterException {
        try {
            // get the new sequence.  (Note: getInt returns 0 if there are
            // no existing volumes in the project.)
          
            ressetWork = stmtCodingDb.executeQuery("SELECT high_volume  FROM project  WHERE project_id=" +projectId);
            
            if(ressetWork.next())
            volumeSequence = 1 + ressetWork.getInt(1);
            ressetWork.close();

            prepstmtCodingDb = conCodingDb.prepareStatement(SQLQueries.UPD_DBWRITER_HVOL);
            prepstmtCodingDb.setInt(1,volumeSequence);
            prepstmtCodingDb.setInt(2,projectId);
            prepstmtCodingDb.executeUpdate();
            prepstmtCodingDb.close();
            
            ProjectHistoryData projectData = new ProjectHistoryData(conCodingDb, projectId);
            projectData.insertIntoHistoryTable(conCodingDb,Command_import_data.userId,Mode.EDIT);
            //
            // create the volume record

            prepstmtCodingDb = conCodingDb.prepareStatement(SQLQueries.INS_DBWRITER_VOL);
           
            prepstmtCodingDb.setString(1, volume);
            prepstmtCodingDb.setInt(2, projectId);
            prepstmtCodingDb.setInt(3, volumeSequence);
            prepstmtCodingDb.setString(4, imagePath);
            prepstmtCodingDb.setString(5, original_volume);
            prepstmtCodingDb.setString(6, serverIP_port);
            prepstmtCodingDb.setString(7, internal_volume);
            if(isUnitizeOptional.equals("true")){
                 prepstmtCodingDb.setString(8, "Yes");
            }else{
                prepstmtCodingDb.setString(8, "No");
            }
            prepstmtCodingDb.setString(9, volume_completion_date);
            prepstmtCodingDb.executeUpdate();
            prepstmtCodingDb.close();
            // get the last auto_increment generated volumeId
    
            ressetWork = stmtCodingDb.executeQuery("SELECT TOP 1 volume_id FROM volume ORDER BY volume_id DESC");
            ressetWork.next();
            volumeId = ressetWork.getInt(1);            
            ressetWork.close();

             VolumeHistoryData volumeData = new VolumeHistoryData(conCodingDb,volumeId);
             volumeData.insertIntoHistoryTable(conCodingDb,Command_import_data.userId,Mode.ADD);
            
            // add a row to unitprice for this volume for each level in the project

            prepstmtCodingDb = conCodingDb.prepareStatement(SQLQueries.INS_DBWRITER_UPRICE);
            prepstmtCodingDb.setInt(1,volumeId);
            prepstmtCodingDb.setInt(2,projectId);
            int i = prepstmtCodingDb.executeUpdate();
            prepstmtCodingDb.close();

            Log.print("(DbWriter) " + i + " unitprice rows written");
            if (i < 1) {

                stmtCodingDb.executeUpdate("INSERT into unitprice(project_id, volume_id, field_level) VALUES ("+projectId+","+volumeId+",0)");
            }

            rs = stmtCodingDb.executeQuery("SELECT unitprice_id  FROM unitprice WHERE volume_id=" +volumeId);                        
	    // code modified starts
			int id = -1;
            while (rs.next()) {
				id = rs.getInt(1);				
            }
			if(id != -1) {
				//updateChanges(stmtCodingDb, UNITPRICE_TABLE, rs.getInt(1));
				updateChanges(stmtCodingDb, UNITPRICE_TABLE, id);
			}
           

            // add a row to customerprice for this volume for each level in the project

            prepstmtCodingDb = conCodingDb.prepareStatement("INSERT into customerprice (project_id, volume_id, field_level," +
                    " unitize_page_price, unitize_doc_price   , coding_page_price, coding_doc_price)   " +
                    " SELECT DISTINCT P.project_id,  " + volumeId + " AS vol, coalesce(PF.field_level, 0) , " +
                    " CP.unitize_page_price, CP.unitize_doc_price , CP.coding_page_price, CP.coding_doc_price   " +
                    " FROM project P      inner join projectfields PF on P.project_id = PF.project_id " +
                    " left join customerprice CP ON CP.project_id = P.project_id AND CP.volume_id = 0 " +
                    " AND CP.field_level = PF.field_level " +
                    " WHERE P.project_id = "+ projectId +" and P.active = 1 GROUP BY P.project_id, volume_id, " +
                    " coalesce(PF.field_level, 0),CP.unitize_page_price, CP.unitize_doc_price, CP.coding_page_price," +
                    " CP.coding_doc_price");            
            i = prepstmtCodingDb.executeUpdate();
            prepstmtCodingDb.close();

            Log.print("(DbWriter) " + i + " customerprice rows written");
            if (i < 1) {

                stmtCodingDb.executeUpdate( "INSERT into customerprice (project_id, volume_id, field_level)"
                    +" values ("+projectId+","+volumeId+",0)"); 
            }

            rs1 = stmtCodingDb.executeQuery(
            "select customerprice_id from customerprice"
            +" where volume_id="+volumeId);
            
            int id1 = -1;
            while (rs1.next()) {
                id1 = rs1.getInt(1);
            }
            if (id1 != -1) {               
                updateChanges(stmtCodingDb, CUSTOMERPRICE_TABLE, id1);
            }
       // code modifies ends   
        } catch (SQLException e) {
            logger.error("Exception while insertVolume() execution of DBWriter." + e);
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            logger.error(sw.toString());
            throw new DbWriterException("SQL Error During insertVolume");
        }
    }

    private void setBatchCnt() 
        throws DbWriterException {
        // get the greatest volume.batch_id
        //Log.print("(DbWriter.setBatchCnt) for " + projectId);
        try {

            ressetWork = stmtCodingDb.executeQuery("SELECT high_unitize_batch  FROM project WHERE project_id=" +projectId);
            
            
            if (ressetWork.next()) {
                batchCnt = ressetWork.getInt(1);
                //Log.print("(DbWriter.setBatchCnt) " + batchCnt);
            }
        } catch (SQLException e) {
            logger.error("Exception while setBatchCnt() execution of DBWriter." + e);
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            logger.error(sw.toString());
            throw new DbWriterException("SQL Error During setBatchCount");
        }
    }

    public void debugOn() {
	debug = true;
    }

    public void loggingOn() {
	logging = true;
    }

    private void insertBatch(Statement st, int volume_id, int lft, int rgt
                             , int batch_number, int activeGroup) throws SQLException {
	if ( debug ) Log.write("doing batch insert: volume_id:"+volume_id+" lft:"+lft+" rgt:"+lft+" batch_number:"+batch_number);

	String query1 = "INSERT INTO batch  (volume_id,lft,rgt,batch_number,status,priority,active_group) VALUES (?,?,?,?,'Unitize',0,?)";
        
        String query2 = "INSERT INTO batch  (volume_id,lft,rgt,batch_number,status,priority,active_group) VALUES (?,?,?,?,'Coding',0,?)";
    if(isUnitizeOptional.equals("true")){
       prepstmtCodingDb = conCodingDb.prepareStatement(query2);
    }else{
       prepstmtCodingDb = conCodingDb.prepareStatement(query1);
    } 
    
    prepstmtCodingDb.setInt(1,volume_id);
    prepstmtCodingDb.setInt(2,lft);
    prepstmtCodingDb.setInt(3,rgt);
    prepstmtCodingDb.setInt(4,batch_number);
    prepstmtCodingDb.setInt(5,activeGroup);
    prepstmtCodingDb.executeUpdate();
    prepstmtCodingDb.close();
    
    //insert into history_batch for audit-history.
    st = conCodingDb.createStatement();
    ResultSet rs_batchId = st.executeQuery(SQLQueries.SEL_TOP_BATCH_ID);
    rs_batchId.next();
    int batch_Id = rs_batchId.getInt(1);
    
    BatchHistoryData batchData = new BatchHistoryData(conCodingDb,batch_Id);       
    batchData.insertIntoHistoryTable(conCodingDb, Command_import_data.userId, Mode.ADD);


    BatchProcessHistroyData data = new BatchProcessHistroyData();
    data.setBatch_id(batch_Id);
    data.setVolume_id(volume_id);
    data.setProcess("Unitize");
    data.setIs_ready("Yes");

    data.insertIntoHistoryTable(conCodingDb);
}

    private void insertProject( Statement st, String projectName ) throws SQLException {
       

        String sql = "insert into project(project_name,split_documents) values('"+projectName+"', 0)";
            st.executeUpdate("insert into project(project_name,split_documents) values('"+projectName+"', 0)");
          
           rs = stmtCodingDb.executeQuery(SQLQueries.SEL_DBWRITER_PRID);
           rs.next();
           projectId = rs.getInt(1);
           
           ProjectHistoryData projectData = new ProjectHistoryData(conCodingDb, projectId);
           projectData.insertIntoHistoryTable(conCodingDb, Command_import_data.userId,Mode.ADD);
           // create volume-default unitprice rows
          
	    prepstmtCodingDb = conCodingDb.prepareStatement("INSERT INTO unitprice (project_id,volume_id,field_level,unitize_page_price,unitize_doc_price,  uqc_page_price,uqc_doc_price,coding_page_price,coding_doc_price,codingqc_page_price,codingqc_doc_price) VALUES(?,0,0,0,0,0,0,0,0,0,0)");
	    prepstmtCodingDb.setInt(1,projectId);
	    prepstmtCodingDb.executeUpdate();
	    prepstmtCodingDb.close();

           // create volume-default customerprice rows
	    prepstmtCusprice = conCodingDb.prepareStatement(SQLQueries.INS_DBWRITER_CUSPRICE);
	    prepstmtCusprice.setInt(1,projectId);
	    prepstmtCusprice.executeUpdate();
	    prepstmtCusprice.close();
    }

    /**
     * Create batches, ranges, childs and pages.
     * @param x - ImageXref view of the page
     * @param volumeId - volume.volume_id of the volume being loaded
     * @param sequence - page.seq of the page
     */
    // Insert row in table - for ImageXref
    private void insertXref(Statement st, ImageXref x, int volumeId, int sequence) throws SQLException {
	if( debug) Log.write("vi:"+volumeId+"||bates:"+'"'+x.bates+'"'+"||bflag:"+x.boundary+"||boundary:"+(x.boundary == ' ' ? B_NONE  :(x.boundary == 'C' ? B_CHILD  : B_RANGE))+"||offset:"+x.offset+"||path:"+x.path+"||fname:"+x.fileName+"||fType:"+x.fileType+"||rotate:"+x.rotation+"||");

        // count this page in the batch size
        spanCnt++;
        
        if (spanCnt > batchSpan) {
            // count the number of pages beyond the preferred batch size
            beyondSpan++;
        }
        if(x.boundary == 'C' || x.boundary == 'D'){            
         docCnt++;
        
        }
        if(docCnt == batchSpan){
         batchCount++;
        }
         if(isUnitizeOptional.equals("true")){               
                     // see if the batch is complete or if this is the first batch
                if ( (docCnt > batchSpan           // batch is big enough
                      && (x.boundary != ' '         // and
                          && x.boundary != 'C'))    // it's a document
                                                         // or, force a batch
                    || batchId == 0) {              // first page: must make batch
                    if ( debug ) Log.write("  PageCnt "+pageCnt+" / batchspan "+batchSpan+"  inserting Batch");
                    if ( debug ) Log.write("  sequence "+sequence);
        //             Log.print("insertXref-----------");
                    // start a new batch
                    batchCnt++;
                    //docCnt++;
                    if (batchId > 0) {
                        // not first batch of volume
                        // End the last batch row by adding a batch.rgt
                        updateBatchRgt(st);
                    }
                    
                    // create the entry in the batch table

                    insertBatch(st, volumeId, /* lft-> */ sequence, /* rgt, update later-> */ 0
                                , batchCnt, x.activeGroup);
                    // get the last auto_increment generated batchId       
       
                    rs = st.executeQuery("SELECT TOP 1 batch_id FROM batch ORDER BY batch_id DESC");
                    if (rs.next()) {
                        batchId = rs.getInt(1);

                        insertRange(st, sequence);
                        insertChild(st, sequence);
                    } else {
                        if (debug | logging ) Log.write("Bad batch insert (volume::sequence::batchCount): "
                                                        +volumeId+" :: "+sequence+" :: "+batchCnt);
                    }
                    //rs.close();

                    if (beyondSpan > spanSearch) {
                        Log.print("--- Forcing batch " + batchCnt + " start: " + x.bates);
                    }
                    docCnt =1;
                    spanCnt = 1; // one because this page is the first page of the batch
                    beyondSpan = 0;
                    x.boundary = 'D'; // force first page of range to be a document
                } else if (x.boundary != ' ') {
                    // first page of child
                    if (x.boundary != 'C') {
                        insertRange(st, sequence);
                    }

                    insertChild(st, sequence);
                }              
         }
         else{
                     // see if the batch is complete or if this is the first batch
                if ( (spanCnt > batchSpan           // batch is big enough
                      && (x.boundary != ' '         // and
                          && x.boundary != 'C'))    // it's a document
                    || beyondSpan > spanSearch      // or, force a batch
                    || batchId == 0) {              // first page: must make batch
                    if ( debug ) Log.write("  PageCnt "+pageCnt+" / batchspan "+batchSpan+"  inserting Batch");
                    if ( debug ) Log.write("  sequence "+sequence);
        //             Log.print("insertXref-----------");
                    // start a new batch
                    batchCnt++;

                    if (batchId > 0) {
                        // not first batch of volume
                        // End the last batch row by adding a batch.rgt
                        updateBatchRgt(st);
                    }
                    // create the entry in the batch table                   
                    insertBatch(st, volumeId, /* lft-> */ sequence, /* rgt, update later-> */ 0
                                , batchCnt, x.activeGroup);
                    // get the last auto_increment generated batchId        
                    rs = st.executeQuery("SELECT TOP 1 batch_id FROM batch ORDER BY batch_id DESC");
                    if (rs.next()) {
                        batchId = rs.getInt(1);

                        insertRange(st, sequence);
                        insertChild(st, sequence);
                    } else {
                        if (debug | logging ) Log.write("Bad batch insert (volume::sequence::batchCount): "
                                                        +volumeId+" :: "+sequence+" :: "+batchCnt);
                    }
                    //rs.close();

                    if (beyondSpan > spanSearch) {
                        Log.print("--- Forcing batch " + batchCnt + " start: " + x.bates);
                    }

                    spanCnt = 1; // one because this page is the first page of the batch
                    beyondSpan = 0;
                    x.boundary = 'D'; // force first page of range to be a document
                } else if (x.boundary != ' ') {
                    // first page of child
                    if (x.boundary != 'C') {
                        insertRange(st, sequence);
                    }

                    insertChild(st, sequence);
                }
         }
     
        st.executeUpdate("INSERT INTO page (volume_id,seq,child_id,bates_number,original_flag,original_rotate,path,filename,"+
                " offset,file_type,boundary_flag,rotate,boundary,group_one_path,  group_one_filename,document_number)" 
                +"  VALUES("+volumeId
                +"  ,"  +sequence
                +"  ,"  +childId
                +"  ,"  +"'"+x.bates+"'"
                +"  ,"  +"'"+x.boundary+"'"
                +"  ,"  +x.rotation
                +"  ,"  +"'"+x.path+"'"
                +"  ,"  +"'"+x.fileName+"'"
                +"  ,"  +x.offset
                +"  ,"  +"'"+x.fileType+"'"
                +"  ,"  +"'"+x.boundary+"'"
                +"  ,"  +x.rotation
//                +"  ,"  +(x.boundary == ' ' ? B_NONE                 
//                        :(x.boundary == 'C' ? B_CHILD
//                        : B_RANGE))
                +"  ,"  +(x.boundary == ' ' ? "'NONE'"                 
                        :(x.boundary == 'C' ? "'CHILD'"
                        : "'RANGE'"))
                +"  ,"  +"'"+x.groupOnePath+"'"
                +"  ,"  +"'"+x.groupOneFileName+"'"
                +"  ,"  +"'"+x.documentNumber+"'"
                +")");
        lastRgt = sequence;
    }

    private void insertRange(Statement st, int sequence) throws SQLException {
        // first page of range
        if (rangeId > 0) {
            // not first range of volume
            // End the last range row by adding a range.rgt
            updateRangeRgt(st);
        }
        // Save range_id for use in updating range.rgt
        // when next/last range encountered.
        prepstmtCodingDb = conCodingDb.prepareStatement(SQLQueries.INS_DBWRITER_RANGER);
        prepstmtCodingDb.setInt(1,volumeId);
        prepstmtCodingDb.setInt(2,sequence);
        prepstmtCodingDb.executeUpdate();
        prepstmtCodingDb.close();
        
//        rs = st.executeQuery("select last_insert_id()");
        rs = st.executeQuery(SQLQueries.SEL_DBWRITER_RANGEID);
        if (rs.next()) {
            rangeId = rs.getInt(1);
        }
        rs.close();
        if ( debug ) Log.write("    doing range insert: range_id: " + rangeId
                               + "/" + sequence);
    }

    private void insertChild(Statement st, int sequence) throws SQLException {
        if (childId > 0) {
            // not first child of volume
            // End the last child row by adding a child.rgt
            updateChildRgt(st);
        }
        // Save child_id for use in updating child.rgt
        // when next/last child encountered.
        st.executeUpdate("INSERT INTO child (volume_id,lft,rgt,range_id,batch_id) VALUES("+volumeId+"," +
                " "+sequence+",0,"+rangeId+","+batchId+")");

        // get the last auto_increment generated rangeId
        rs = st.executeQuery(SQLQueries.SEL_DBWRITER_CHILDID);
        
        if (rs.next()) {
            childId = rs.getInt(1);
        }
        if ( debug ) Log.write("    doing child insert: child_id: " + childId
                               + "/" + sequence);
        rs.close();
        // increment the total number of children seen
        pageCnt++;
    }

    /**
     * Finish the project record by updating the high_unitize_batch.
     * high_unitize_batch is the highest batch_number of a batch with
     * unitize status.
     */
    private void updateProject(Statement st) throws SQLException {
        
            prepstmtCodingDb = conCodingDb.prepareStatement(SQLQueries.UPD_DBWRITER_PROJECT);
            prepstmtCodingDb.setInt(1,batchCnt);
            prepstmtCodingDb.setInt(2,projectId);
            prepstmtCodingDb.executeUpdate();
            prepstmtCodingDb.close();
          
            ProjectHistoryData projectData = new ProjectHistoryData(conCodingDb, projectId);
            projectData.insertIntoHistoryTable(conCodingDb,Command_import_data.userId,Mode.EDIT);
        
    }

    /**
     * Finish the volume and project records by updating the lft and rgt
     * fields with actual data.  lft and rgt are the first and last page.sequence
     * numbers of the volume and project.
     */
    private void updateVolume(Statement st) throws SQLException {

        rs = st.executeQuery(SQLQueries.SEL_DBWRITER_MAXRGT +volumeId);
        
        if (rs.next()) {

            prepstmtCodingDb = conCodingDb.prepareStatement(SQLQueries.UPD_DBWRITER_VOLUME);
            prepstmtCodingDb.setInt(1,rs.getInt(2));
            prepstmtCodingDb.setInt(2,rs.getInt(1));
            prepstmtCodingDb.setInt(3,volumeId);
            prepstmtCodingDb.executeUpdate();
            prepstmtCodingDb.close();
            
            VolumeHistoryData volumeData = new VolumeHistoryData(conCodingDb,volumeId);
            volumeData.insertIntoHistoryTable(conCodingDb,Command_import_data.userId,Mode.EDIT);
            
        } else {
            Log.print("No batches for project " + projectId + ", volume " + volumeId);
        }

        rs = st.executeQuery(SQLQueries.SEL_DBWRITER_MINLFT +projectId);
        
        if (rs.next()) {

            prepstmtCodingDb = conCodingDb.prepareStatement(SQLQueries.UPD_DBWRITER_PROJLFT);
            prepstmtCodingDb.setInt(1,rs.getInt(2));
            prepstmtCodingDb.setInt(2,rs.getInt(1));
            prepstmtCodingDb.setInt(3,projectId);
            prepstmtCodingDb.executeUpdate();
            prepstmtCodingDb.close();
            
            ProjectHistoryData projectData = new ProjectHistoryData(conCodingDb, projectId);
            projectData.insertIntoHistoryTable(conCodingDb,Command_import_data.userId,Mode.EDIT);
        } else {
            Log.print("No volumes for project " + projectId + ", volume " + volumeId);
        }
        updateChanges(st, PROJECT_TABLE, projectId);
        updateChanges(st, VOLUME_TABLE, volumeId);
    }

    private void updateBatchRgt(Statement st) throws SQLException {

        prepstmtCodingDb = conCodingDb.prepareStatement(SQLQueries.UPD_DBWRITER_RGTBID);
        prepstmtCodingDb.setInt(1,lastRgt);
        prepstmtCodingDb.setInt(2,batchId);
        prepstmtCodingDb.executeUpdate();
        prepstmtCodingDb.close();
        
        BatchHistoryData batchData = new  BatchHistoryData(conCodingDb,batchId);                             
        batchData.insertIntoHistoryTable(conCodingDb,Command_import_data.userId, Mode.EDIT);
        
    }

    private void updateRangeRgt(Statement st) throws SQLException {

        prepstmtCodingDb = conCodingDb.prepareStatement(SQLQueries.UPD_DBWRITER_RANGE);
        prepstmtCodingDb.setInt(1,lastRgt);
        prepstmtCodingDb.setInt(2,rangeId);
        prepstmtCodingDb.executeUpdate();
        prepstmtCodingDb.close();
    }

    private void updateChildRgt(Statement st) throws SQLException {

        prepstmtCodingDb = conCodingDb.prepareStatement(SQLQueries.UPD_DBWRITER_CHILD);
        prepstmtCodingDb.setInt(1,lastRgt);
        prepstmtCodingDb.setInt(2,childId);
        prepstmtCodingDb.executeUpdate();
        prepstmtCodingDb.close();
    }

    // Insert row in table - for IssueXref
    private void insertXref(Statement st, IssueXref x, int volumeId, int page_id) throws SQLException {
        // get the greatest sequence
        int sequence = 1;

        ResultSet rs = st.executeQuery(SQLQueries.SEL_DBWRITER_MAXSEQ +page_id);
        
        if (rs.next()) {
            sequence = 1 + rs.getInt(1);
        }
        if ( debug ) Log.write("    doing pageissue insert: page_id: "
                               + page_id + "/" + sequence);

        prepstmtCodingDb = conCodingDb.prepareStatement(SQLQueries.INS_DBWRITER_PGISSUE);
        prepstmtCodingDb.setInt(1,page_id);
        prepstmtCodingDb.setInt(2,volumeId);
        prepstmtCodingDb.setInt(3,sequence);
        prepstmtCodingDb.setString(4,x.issueName);
        prepstmtCodingDb.executeUpdate();
        prepstmtCodingDb.close();
    }

    private void updateChanges(Statement st, int tableNumber, int id) {
        try {

            prepstmtCodingDb = conCodingDb.prepareStatement(SQLQueries.INS_DBWRITER_CHANGES);
            prepstmtCodingDb.setInt(1,tableNumber);
            prepstmtCodingDb.setInt(2,id);
            prepstmtCodingDb.setInt(3,age);
            prepstmtCodingDb.executeUpdate();
            prepstmtCodingDb.close();
            
        } catch (SQLException e) {
	    if( debug | logging) Log.write("SQLException: " + e.getMessage());
            logger.error("Exception while updating changes table:." + e);
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            logger.error(sw.toString());
            Log.write("changes failure: table #: " + tableNumber + " / id: " + id );
        }
    }

    /** Make a JDBC connection.
     */
    //Not in use
   /* private Connection makeConnection (
        String dbHost, int dbPort, String dbName, String dbUser, String dbPassword)
    throws DbWriterException {

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
	    if( debug | logging) Log.write("SQLException: " + e.getMessage());
            Log.write("Failed to connect with: " + dbUrl + " user: " + dbUser + " p: " + dbPassword);
            logger.error("Exception while establishing connection with db:." + e);
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            logger.error(sw.toString());
            return null;
	    //throw new DbWriterException("Failed to connect with: "+dbUrl);
        }
    }*/

    /**
     * Exception to rollback and return error message if there
     * is an error while writing.
     */
    public class DbWriterException extends Exception {

        DbWriterException(String msg) {
            super(msg);
            if (conCodingDb != null) {
                try {
                    conCodingDb.rollback();
                    conCodingDb.close();
                } catch (SQLException e) {
		    //Log.quit("Sql error " + e);
                    logger.error("Exception while doing rollback or closing the connection:." + e);
                    StringWriter sw = new StringWriter();
                    e.printStackTrace(new PrintWriter(sw));
                    logger.error(sw.toString());
                }
            }
        }
    }
}
