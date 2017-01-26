/* $Header: /home/common/cvsarea/ibase/dia/src/dbload/DbWriter.java,v 1.41.2.9 2007/03/28 03:35:25 bill Exp $ */
package dbload;

import common.Log;
import common.msg.MessageConstants;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * A class to write Xref data to the database.
 */
public class DbWriter implements MessageConstants
{

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
   boolean replaceVolume = false;
   /** add a volume to the project */
   boolean appendVolume = false;
   boolean split_documents = false;
   static boolean debug = false;
   static boolean logging = false;

   /** user parameter - how many pages in a batch */
   int batchSpan = 0;
   /** user parameter - how many pages to search beyond batchSpan looking for where to break
     * a batch on a document boundary */
   int spanSearch = 0;

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
   int pageCnt = 0;
   /** count of batches */
   int batchCnt = 0;
   /**  Number of pages in the current batch, reset to 0 as each new batch row is inserted */
   int spanCnt = 0;
   /** Number of pages beyond batchSpan pages */
   int beyondSpan = 0;
   String volume;
   String original_volume = "";
   int volumeSequence = 0;
   int pageSeq = 0;
   int age = 0;

   /** general use PreparedStatement and ResultSet */
   private PreparedStatement ps;
   PreparedStatement prepstmtCodingDb;
   private ResultSet rs;
   private ResultSet rs1;
   Statement stmtCodingDb = null;
   ResultSet ressetWork;
   public Connection conCodingDb = null;
   public Connection conImageDb = null;
   private String message;

   public DbWriter(String database, String host, int port, String dbuser, String password, boolean replaceVolume, boolean appendVolume, boolean split_documents, String message)
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
   public void write(Xref[] xrefData, String project, String givenVolume, int batchspan, int spanSearch, String imagePath)
           throws DbWriterException
   {
      this.batchSpan = batchspan;
      this.spanSearch = spanSearch;
      original_volume = ((ImageXref) xrefData[0]).originalVolume;
      try {
         volume = givenVolume;
         // look for different volumes in same xref file
         if (givenVolume == null) {
            volume = ((ImageXref) xrefData[0]).volume;
            //original_db = ((ImageXref) xrefData[0]).originalDb;
            for (int i = 1; i < xrefData.length; i++) {
               if (((ImageXref) xrefData[i]).volume != volume) {
                  message = "Multiple volume names in cross-reference file";
                  throw new DbWriterException("Multiple volume names in cross-reference file");
               }
            }
         }
         if (volume.length() == 0) {
            message = "No volume name given";
            throw new DbWriterException("No volume name given");
         }

         // connect to the database
         conCodingDb = makeConnection(host, port, database, dbuser, password);

         stmtCodingDb = conCodingDb.createStatement();
         conCodingDb.setAutoCommit(false);
         rs = stmtCodingDb.executeQuery("select age from svrage where dummy_key = 0 for update");
         if (rs.next()) {
            age = 1 + rs.getInt(1);
         }
         rs.close();
         stmtCodingDb.executeUpdate("update svrage set age=" + age + " where dummy_key = 0");


         // DOES Project Exist???
            // We can have an active project, or we can have a deleted project
         prepstmtCodingDb = conCodingDb.prepareStatement("select project_id, active from project" + " where project_name = ?" + " order by active desc" + " lock in share mode");
         prepstmtCodingDb.setString(1, project);
         ressetWork = prepstmtCodingDb.executeQuery();
         projectId = 0;
         boolean isActive = false;
         if (ressetWork.next()) {
            projectId = ressetWork.getInt(1);
            isActive = ressetWork.getBoolean(2);
            stmtCodingDb.executeUpdate("update project" + " set split_documents=" + (split_documents ? 1 : 0) + " where project_id=" + projectId);
         }
         if (!isActive) {
            // new project go ahead and insert
            if (projectId != 0) {
               // TBD: user should be given a warning and the option to change name
               Log.print("Warning: NEW PROJECT HAS SAME NAME AS A DELETED PROJECT");
            }
            insertProject(stmtCodingDb, project);
         //PreparedStatement prepstmtLastInsert = conCodingDb.prepareStatement(
                //    "select last_insert_id()");
		//ressetWork = prepstmtLastInsert.executeQuery();
		//ressetWork.next();
                //projectId = ressetWork.getInt(1);
                //prepstmtLastInsert.close();
                //updateChanges(stmtCodingDb, PROJECT_TABLE, ressetWork.getInt(1));
         }
         else {
            if (debug | logging) {
               Log.write("Attempting to replace or append volume " + volume + " for project " + project);
            }
         }

         if (debug) {
            Log.write("Project Id: " + projectId);
         }
         prepstmtCodingDb.close();


         // Check status of Volume - does it exist
         prepstmtCodingDb = conCodingDb.prepareStatement("select project_id, volume_id from volume" + " where volume_name = ?" + " lock in share mode");
         if (debug | logging) {
            Log.write("VOLUME: " + volume);
         }
         prepstmtCodingDb.setString(1, volume);
         rs = prepstmtCodingDb.executeQuery();
         // Did we get any hits?
         if (rs.next()) {
            if (replaceVolume) {
               // A volume already exists under a different project name
               if (rs.getInt(1) != projectId) {
                  if (debug | logging) {
                     Log.write("Can't replace volume for project");
                  }
                  prepstmtCodingDb.close();
                  rs.close();
                  message = "Volume, " + volume + ", already exists for a project.";
                  throw new DbWriterException("Volume, " + volume + ", already exists for a project.");
               }
               else {
                  // proceed with replacement
                  volumeId = rs.getInt(2);
                  if (debug | logging) {
                     Log.write("Proceed with replacement " + volumeId);
                  }
                  rs.close();

                  // Can't replace a volume in use
                  rs = stmtCodingDb.executeQuery("select 0 from session" + " where volume_id =" + volumeId + " lock in share mode");
                  if (rs.next()) {
                     message = "Volume has locked batches: " + volume;
                     throw new DbWriterException("Volume has locked batches: " + volume);
                  }
                  rs.close();

                  // Can't replace a queued volume
                  rs = stmtCodingDb.executeQuery("select 0 from teamsvolume" + " where volume_id =" + volumeId + " lock in share mode");
                  if (rs.next()) {
                     message = "Volume is in a teams's queue: " + volume;
                     throw new DbWriterException("Volume is in a teams's queue: " + volume);
                  }
                  rs.close();

                  // Can't replace a queued volume
                  rs = stmtCodingDb.executeQuery("select 0 from teamsqueue" + " inner join batch B using (batch_id)" + " where B.volume_id =" + volumeId + " lock in share mode");
                  if (rs.next()) {
                     message = "A batch in volume is in a teams's queue: " + volume;
                     throw new DbWriterException("A batch in volume is in a teams's queue: " + volume);
                  }
                  rs.close();

                  // Can't replace a queued volume
                  rs = stmtCodingDb.executeQuery("select 0 from usersqueue" + " inner join batch B using (batch_id)" + " where B.volume_id =" + volumeId + " lock in share mode");
                  if (rs.next()) {
                     message = "A batch in volume is in a user's queue: " + volume;
                     throw new DbWriterException("A batch in volume is in a user's queue: " + volume);
                  }
                  rs.close();

                  // Can't replace an assigned volume
                  rs = stmtCodingDb.executeQuery("select 0 from assignment" + " inner join batch B using (batch_id)" + " where B.volume_id =" + volumeId + " lock in share mode");
                  if (rs.next()) {
                     message = "A batch in volume is assigned: " + volume;
                     throw new DbWriterException("A batch in volume is assigned: " + volume);
                  }
                  rs.close();

                  // See if there is any coded data
                  rs = stmtCodingDb.executeQuery("select Va.child_id, NV.child_id, LV.child_id FROM volume V, child C" + "   left join value Va on (Va.child_id = C.child_id)" + "   left join namevalue NV on (NV.child_id = C.child_id)" + "   left join longvalue LV on (LV.child_id = C.child_id)" + " where V.volume_id=" + volumeId + " AND V.volume_id = C.volume_id" + " limit 1");
                  if (rs.next() && (rs.getObject(1) != null || rs.getObject(2) != null || rs.getObject(3) != null)) {
                     message = "A volume containing coded data cannot be overwritten." + "\n\nYou may delete " + givenVolume + " using the Batching screen, then" + "\nimport this volume.";
                     throw new DbWriterException("A volume containing coded data cannot be overwritten." + "\n\nYou may delete " + givenVolume + " using the Batching screen, then" + "\nimport this volume.");
                  }
                  Log.print("(DbWriter) no coded data");

                  //ok, go ahead and wipe out existing volume and children
                  stmtCodingDb.executeUpdate("delete from volume" + " where volume_id=" + volumeId);
                  updateChanges(stmtCodingDb, VOLUME_TABLE, volumeId);

                  rs = stmtCodingDb.executeQuery("select unitprice_id from unitprice" + " where volume_id=" + volumeId);
                  stmtCodingDb.executeUpdate("delete from unitprice" + " where volume_id=" + volumeId);
                  while (rs.next()) {
                     updateChanges(stmtCodingDb, UNITPRICE_TABLE, rs.getInt(1));
                  }

                  rs = stmtCodingDb.executeQuery("select customerprice_id from customerprice" + " where volume_id=" + volumeId);
                  stmtCodingDb.executeUpdate("delete from customerprice" + " where volume_id=" + volumeId);
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
                  stmtCodingDb.executeUpdate("delete from batch" + " where volume_id=" + volumeId);
                  stmtCodingDb.executeUpdate("delete from range" + " where volume_id=" + volumeId);
                  stmtCodingDb.executeUpdate("delete from child" + " where volume_id=" + volumeId);
                  stmtCodingDb.executeUpdate("delete from page" + " where volume_id=" + volumeId);
                  stmtCodingDb.executeUpdate("delete from pageissue" + " where volume_id=" + volumeId);

                  setBatchCnt();
                  insertVolume(imagePath);
               }
            }
            else {
               // user did not say replace - new volume shouldn't exist
               if (rs.getInt(1) != projectId) {
                  if (debug | logging) {
                     Log.write("Can't replace volume for project");
                  }
                  prepstmtCodingDb.close();
                  rs.close();
                  message = "Volume, " + volume + ", already exists for a project.";
                  throw new DbWriterException("Volume, " + volume + ", already exists for a project.");
               }
               else {
                  message = "Volume: " + volume + " already exists for project: " + project;
                  throw new DbWriterException("Volume: " + volume + " already exists for project: " + project);
               }
            }
         }
         else {
            // volume doesn't exist
            prepstmtCodingDb.close();

            setBatchCnt();
            insertVolume(imagePath);
         }

         rs.close();

         // get the largest page.seq in the project
         ressetWork = stmtCodingDb.executeQuery("select max(rgt) from project" + "   where project_id = " + projectId + " lock in share mode");
         if (ressetWork.next()) {
            pageSeq = ressetWork.getInt(1);
            if (debug) {
               Log.write("Appending - retrieved sequence: " + pageSeq);
            }
         }

         // traverse the Xref Array now
         for (int i = 0; i < xrefData.length; i++) {

            Xref theXref = xrefData[i];

            // have to do an instanceof test because Xref type not being passed
            if (theXref instanceof ImageXref) {
               if (debug) {
                  Log.write("Do inserts of:" + i + " volumeId:" + volumeId + " projectId:" + projectId);
               }
               if (debug) {
                  Log.write("XREF: " + theXref + " instance of ImageXref");
               }
               pageSeq = pageSeq + PAGE_DELTA;
               insertXref(stmtCodingDb, (ImageXref) theXref, volumeId, pageSeq);

            }
            else if (theXref instanceof IssueXref) {
               if (debug) {
                  Log.write("XREF: " + theXref + " instance of IsueXref");
               }
               // get page.page_id for the latest page
               ressetWork = stmtCodingDb.executeQuery("select last_insert_id()");
               if (ressetWork.next()) {
                  insertXref(stmtCodingDb, (IssueXref) theXref, volumeId, ressetWork.getInt(1));
               }
               else {
                  if (debug) {
                     Log.write("XREF: " + theXref + " can't get last_insert_id");
                  }
               }

            }
            else {
               if (debug) {
                  Log.write("XREF: " + theXref + " instance of ???");
               }
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
         Log.write("Sql error " + e);
         e.printStackTrace();
         throw new DbWriterException("SQL Error During write() execution");
      }
   }

   private void insertVolume(String imagePath)
           throws DbWriterException
   {
      try {
         // get the new sequence.  (Note: getInt returns 0 if there are
            // no existing volumes in the project.)
            //ressetWork = stmtCodingDb.executeQuery(
            //    "select max(sequence) from volume"
            //    +" where project_id="+projectId
            //    +" lock in share mode");
         ressetWork = stmtCodingDb.executeQuery("select high_volume from project" + " where project_id=" + projectId + " for update");
         ressetWork.first();
         volumeSequence = 1 + ressetWork.getInt(1);
         ressetWork.close();
         stmtCodingDb.executeUpdate("update project" + " set high_volume=" + volumeSequence + " where project_id=" + projectId);
         //
            // create the volume record
            //
         prepstmtCodingDb = conCodingDb.prepareStatement("insert into volume" + " set volume_name=?" + "   , project_id=?" + "   , sequence=?" + "   , lft=0" // update later
                 + "   , rgt=0" // update later
                 + "   , image_path=?" + "   , original_volume_name=?");
         prepstmtCodingDb.setString(1, volume);
         prepstmtCodingDb.setInt(2, projectId);
         prepstmtCodingDb.setInt(3, volumeSequence);
         prepstmtCodingDb.setString(4, imagePath);
         prepstmtCodingDb.setString(5, original_volume);
         prepstmtCodingDb.executeUpdate();
         prepstmtCodingDb.close();
         // get the last auto_increment generated volumeId
         ressetWork = stmtCodingDb.executeQuery("select last_insert_id()");
         ressetWork.next();
         volumeId = ressetWork.getInt(1);
         //updateChanges(stmtCodingDb, VOLUME_TABLE, volumeId);
         ressetWork.close();

         // add a row to unitprice for this volume for each level in the project
         prepstmtCodingDb = conCodingDb.prepareStatement("INSERT unitprice (project_id, volume_id, field_level" + ", unitize_page_price, unitize_doc_price" + ", uqc_page_price, uqc_doc_price" + ", coding_page_price, coding_doc_price" + ", codingqc_page_price, codingqc_doc_price)" + " SELECT DISTINCT P.project_id, " + volumeId + " AS vol, coalesce(PF.field_level, 0)" + " , UP.unitize_page_price, UP.unitize_doc_price" + " , UP.uqc_page_price, UP.uqc_doc_price" + " , UP.coding_page_price, UP.coding_doc_price" + " , UP.codingqc_page_price, UP.codingqc_doc_price" + " FROM project P" + " inner join projectfields PF using (project_id)" + " left join unitprice UP ON UP.project_id = P.project_id" + "   AND UP.volume_id = 0 AND UP.field_level = PF.field_level" + " WHERE P.project_id =" + projectId + " and P.active" + " GROUP BY P.project_id, vol, coalesce(PF.field_level, 0)");

         int i = prepstmtCodingDb.executeUpdate();
         prepstmtCodingDb.close();

         Log.print("(DbWriter) " + i + " unitprice rows written");
         if (i < 1) {
            stmtCodingDb.executeUpdate("INSERT unitprice (project_id, volume_id, field_level)" + " values (" + projectId + "," + volumeId + ",0)");
         }

         rs = stmtCodingDb.executeQuery("select unitprice_id from unitprice" + " where volume_id=" + volumeId);

         /* while (rs.next()) {
                updateChanges(stmtCodingDb, UNITPRICE_TABLE, rs.getInt(1));
            }

            // add a row to customerprice for this volume for each level in the project
            prepstmtCodingDb = conCodingDb.prepareStatement(
                "INSERT customerprice (project_id, volume_id, field_level"
                +", unitize_page_price, unitize_doc_price"
                +", coding_page_price, coding_doc_price)"
                +" SELECT DISTINCT P.project_id, " + volumeId + " AS vol, coalesce(PF.field_level, 0)"
                +" , CP.unitize_page_price, CP.unitize_doc_price"
                +" , CP.coding_page_price, CP.coding_doc_price"
                +" FROM project P"
                +" inner join projectfields PF using (project_id)"
                +" left join customerprice CP ON CP.project_id = P.project_id"
                +"   AND CP.volume_id = 0 AND CP.field_level = PF.field_level"
                +" WHERE P.project_id ="+ projectId +" and P.active"
                +" GROUP BY P.project_id, vol, coalesce(PF.field_level, 0)");
            
            i = prepstmtCodingDb.executeUpdate();
            prepstmtCodingDb.close();

            Log.print("(DbWriter) " + i + " customerprice rows written");
            if (i < 1) {
                stmtCodingDb.executeUpdate(
                    "INSERT customerprice (project_id, volume_id, field_level)"
                    +" values ("+projectId+","+volumeId+",0)");
            }

            rs = stmtCodingDb.executeQuery(
            "select customerprice_id from customerprice"
            +" where volume_id="+volumeId);
            while (rs.next()) {
                updateChanges(stmtCodingDb, CUSTOMERPRICE_TABLE, rs.getInt(1));
            }*/
         // code modified starts
         int id = -1;
         while (rs.next()) {
            id = rs.getInt(1);
            System.out.println("id is " + id);
         }
         if (id != -1) {
            //updateChanges(stmtCodingDb, UNITPRICE_TABLE, rs.getInt(1));
            updateChanges(stmtCodingDb, UNITPRICE_TABLE, id);
         }
         // while (rs.next()) {
           //     updateChanges(stmtCodingDb, UNITPRICE_TABLE, rs.getInt(1));
          //  }


         // add a row to customerprice for this volume for each level in the project
         prepstmtCodingDb = conCodingDb.prepareStatement("INSERT customerprice (project_id, volume_id, field_level" + ", unitize_page_price, unitize_doc_price" + ", coding_page_price, coding_doc_price)" + " SELECT DISTINCT P.project_id, " + volumeId + " AS vol, coalesce(PF.field_level, 0)" + " , CP.unitize_page_price, CP.unitize_doc_price" + " , CP.coding_page_price, CP.coding_doc_price" + " FROM project P" + " inner join projectfields PF using (project_id)" + " left join customerprice CP ON CP.project_id = P.project_id" + "   AND CP.volume_id = 0 AND CP.field_level = PF.field_level" + " WHERE P.project_id =" + projectId + " and P.active" + " GROUP BY P.project_id, vol, coalesce(PF.field_level, 0)");

         i = prepstmtCodingDb.executeUpdate();
         prepstmtCodingDb.close();

         Log.print("(DbWriter) " + i + " customerprice rows written");
         if (i < 1) {
            stmtCodingDb.executeUpdate("INSERT customerprice (project_id, volume_id, field_level)" + " values (" + projectId + "," + volumeId + ",0)");
         }

         rs1 = stmtCodingDb.executeQuery("select customerprice_id from customerprice" + " where volume_id=" + volumeId);
         // while (rs.next()) {
          //      updateChanges(stmtCodingDb, CUSTOMERPRICE_TABLE, rs.getInt(1));
           // }

         int id1 = -1;
         while (rs1.next()) {
            id1 = rs1.getInt(1);
            System.out.println("id1 is............. " + id1);
         }
         if (id != -1) {
            //updateChanges(stmtCodingDb, UNITPRICE_TABLE, rs.getInt(1));
            updateChanges(stmtCodingDb, UNITPRICE_TABLE, id1);
         }
      // code modifies ends   
      } catch (SQLException e) {
         Log.write("Sql error " + e);
         e.printStackTrace();
         throw new DbWriterException("SQL Error During insertVolume");
      }
   }

   private void setBatchCnt()
           throws DbWriterException
   {
      // get the greatest volume.batch_id
        //Log.print("(DbWriter.setBatchCnt) for " + projectId);
      try {
         ressetWork = stmtCodingDb.executeQuery("select high_unitize_batch from project" + " where project_id=" + projectId + " lock in share mode");
         if (ressetWork.next()) {
            batchCnt = ressetWork.getInt(1);
         //Log.print("(DbWriter.setBatchCnt) " + batchCnt);
         }
      } catch (SQLException e) {
         Log.write("Sql error " + e);
         e.printStackTrace();
         throw new DbWriterException("SQL Error During setBatchCount");
      }
   }

   public void debugOn()
   {
      debug = true;
   }

   public void loggingOn()
   {
      logging = true;
   }

   private void insertBatch(Statement st, int volume_id, int lft, int rgt, int batch_number, int activeGroup) throws SQLException
   {
      if (debug) {
         Log.write("doing batch insert: volume_id:" + volume_id + " lft:" + lft + " rgt:" + lft + " batch_number:" + batch_number);
      }
      st.executeUpdate("insert into batch" + " set volume_id=" + volume_id + " , lft=" + lft + " , rgt=" + rgt + " , batch_number=" + batch_number + " , status='Unitize'" + " , priority=" + 0 + " , active_group=" + activeGroup);
   }

   private void insertProject(Statement st, String projectName) throws SQLException
   {
      st.executeUpdate("insert into project" + " set project_name=" + '"' + projectName + '"' + ", split_documents=" + (split_documents ? 1 : 0));

      // get the last auto_increment generated projectId
      rs = stmtCodingDb.executeQuery("select last_insert_id()");
      rs.next();
      projectId = rs.getInt(1);

      // create volume-default unitprice rows
      st.executeUpdate("insert into unitprice" + " set project_id=" + projectId + " , volume_id=0" + " , field_level=0" + " , unitize_page_price=0, unitize_doc_price=0" + " , uqc_page_price=0, uqc_doc_price=0" + " , coding_page_price=0, coding_doc_price=0" + " , codingqc_page_price=0, codingqc_doc_price=0");

      // create volume-default customerprice rows
      st.executeUpdate("insert into customerprice" + " set project_id=" + projectId + " , volume_id=0" + " , field_level=0" + " , unitize_page_price=0, unitize_doc_price=0" + " , coding_page_price=0, coding_doc_price=0");
   }

   /**
     * Create batches, ranges, childs and pages.
     * @param x - ImageXref view of the page
     * @param volumeId - volume.volume_id of the volume being loaded
     * @param sequence - page.seq of the page
     */
   // Insert row in table - for ImageXref

   private void insertXref(Statement st, ImageXref x, int volumeId, int sequence) throws SQLException
   {
      if (debug) {
         Log.write("vi:" + volumeId + "||bates:" + '"' + x.bates + '"' + "||bflag:" + x.boundary + "||boundary:" + (x.boundary == ' ' ? B_NONE : (x.boundary == 'C' ? B_CHILD : B_RANGE)) + "||offset:" + x.offset + "||path:" + x.path + "||fname:" + x.fileName + "||fType:" + x.fileType + "||rotate:" + x.rotation + "||");
      }

      // count this page in the batch size
      spanCnt++;

      if (spanCnt > batchSpan) {
         // count the number of pages beyond the preferred batch size
         beyondSpan++;
      }

      // see if the batch is complete or if this is the first batch
      if ((spanCnt > batchSpan // batch is big enough
              && (x.boundary != ' ' // and
              && x.boundary != 'C')) // it's a document
              || beyondSpan > spanSearch // or, force a batch
              || batchId == 0) {              // first page: must make batch
         if (debug) {
            Log.write("  PageCnt " + pageCnt + " / batchspan " + batchSpan + "  inserting Batch");
         }
         if (debug) {
            Log.write("  sequence " + sequence);
         }

         // start a new batch
         batchCnt++;

         if (batchId > 0) {
            // not first batch of volume
                // End the last batch row by adding a batch.rgt
            updateBatchRgt(st);
         }
         // create the entry in the batch table
         insertBatch(st, volumeId, /* lft-> */ sequence, /* rgt, update later-> */ 0, batchCnt, x.activeGroup);
         // get the last auto_increment generated batchId
         rs = st.executeQuery("select last_insert_id()");
         if (rs.next()) {
            batchId = rs.getInt(1);
            insertRange(st, sequence);
            insertChild(st, sequence);
         }
         else {
            if (debug | logging) {
               Log.write("Bad batch insert (volume::sequence::batchCount): " + volumeId + " :: " + sequence + " :: " + batchCnt);
            }
         }
         rs.close();

         if (beyondSpan > spanSearch) {
            Log.print("--- Forcing batch " + batchCnt + " start: " + x.bates);
         }

         spanCnt = 1; // one because this page is the first page of the batch
         beyondSpan = 0;
         x.boundary = 'D'; // force first page of range to be a document
      }
      else if (x.boundary != ' ') {
         // first page of child
         if (x.boundary != 'C') {
            insertRange(st, sequence);
         }

         insertChild(st, sequence);
      }

      st.executeUpdate("insert into page" + " set volume_id=" + volumeId + "   , seq=" + sequence + "   , child_id=" + childId // from last child insert
              + "   , bates_number=" + '"' + x.bates + '"' + "   , original_flag=" + '"' + x.boundary + '"' + "   , original_rotate=" + x.rotation + "   , path=" + '"' + x.path + '"' + "   , filename=" + '"' + x.fileName + '"' + "   , offset=" + x.offset + "   , file_type=" + '"' + x.fileType + '"' + "   , boundary_flag=" + '"' + x.boundary + '"' + "   , rotate=" + x.rotation + "   , boundary=" + (x.boundary == ' ' ? B_NONE
              : (x.boundary == 'C' ? B_CHILD
              : B_RANGE)) + "   , group_one_path=" + '"' + x.groupOnePath + '"' + "   , group_one_filename=" + '"' + x.groupOneFileName + '"' + "   , document_number=" + '"' + x.documentNumber + '"');
      // Save latest sequence for use in updating range.rgt
        // when next/last range encountered.
      lastRgt = sequence;
   }

   private void insertRange(Statement st, int sequence) throws SQLException
   {
      // first page of range
      if (rangeId > 0) {
         // not first range of volume
            // End the last range row by adding a range.rgt
         updateRangeRgt(st);
      }
      // Save range_id for use in updating range.rgt
        // when next/last range encountered.
      st.executeUpdate("insert into range" + " set volume_id=" + volumeId + "   , lft=" + sequence + "   , rgt=0"); // update later
            //+"   , batch_id="+batchId);
        // get the last auto_increment generated rangeId
      rs = st.executeQuery("select last_insert_id()");
      if (rs.next()) {
         rangeId = rs.getInt(1);
      }
      rs.close();
      if (debug) {
         Log.write("    doing range insert: range_id: " + rangeId + "/" + sequence);
      }
   }

   private void insertChild(Statement st, int sequence) throws SQLException
   {
      if (childId > 0) {
         // not first child of volume
            // End the last child row by adding a child.rgt
         updateChildRgt(st);
      }
      // Save child_id for use in updating child.rgt
        // when next/last child encountered.
      st.executeUpdate("insert into child" + " set volume_id=" + volumeId + "   , lft=" + sequence + "   , rgt=0" + "   , range_id=" + rangeId + "   , batch_id=" + batchId);
      // get the last auto_increment generated rangeId
      rs = st.executeQuery("select last_insert_id()");
      if (rs.next()) {
         childId = rs.getInt(1);
      }
      if (debug) {
         Log.write("    doing child insert: child_id: " + childId + "/" + sequence);
      }
      rs.close();
      // increment the total number of children seen
      pageCnt++;
   }

   /**
     * Finish the project record by updating the high_unitize_batch.
     * high_unitize_batch is the highest batch_number of a batch with
     * unitize status.
     */
   private void updateProject(Statement st) throws SQLException
   {
      //rs = st.executeQuery(
        //    "select V.project_id, max(batch_number) from batch B"
        //    +"  inner join volume V using (volume_id)"
        //    +" where B.volume_id = "+volumeId
        //    +" group by V.project_id");
        //if (rs.next()) {
        //    projectId = rs.getInt(1);
      st.executeUpdate("update project" + " set high_unitize_batch=" + batchCnt + " where project_id=" + projectId);
   //}
   }

   /**
     * Finish the volume and project records by updating the lft and rgt
     * fields with actual data.  lft and rgt are the first and last page.sequence
     * numbers of the volume and project.
     */
   private void updateVolume(Statement st) throws SQLException
   {
      rs = st.executeQuery("select max(rgt), min(lft) from batch" + " where volume_id = " + volumeId);
      if (rs.next()) {
         st.executeUpdate("update volume" + " set lft=" + rs.getInt(2) + "   , rgt=" + rs.getInt(1) + " where volume_id=" + volumeId);
      }
      else {
         Log.print("No batches for project " + projectId + ", volume " + volumeId);
      }
      rs = st.executeQuery("select max(rgt), min(lft) from volume" + " where project_id = " + projectId);
      if (rs.next()) {
         st.executeUpdate("update project" + " set lft=" + rs.getInt(2) + "   , rgt=" + rs.getInt(1) + " where project_id=" + projectId);
      }
      else {
         Log.print("No volumes for project " + projectId + ", volume " + volumeId);
      }
      updateChanges(st, PROJECT_TABLE, projectId);
      updateChanges(st, VOLUME_TABLE, volumeId);
   }

   private void updateBatchRgt(Statement st) throws SQLException
   {
      st.executeUpdate("update batch" + " set rgt=" + lastRgt // rgt saved from last page insert
              + " where batch_id=" + batchId); // batchId saved from last batch insert
   }

   private void updateRangeRgt(Statement st) throws SQLException
   {
      st.executeUpdate("update range" + " set rgt=" + lastRgt // rgt saved from last page insert
              + " where range_id=" + rangeId); // rangeId saved from last range insert
   }

   private void updateChildRgt(Statement st) throws SQLException
   {
      st.executeUpdate("update child" + " set rgt=" + lastRgt // rgt saved from last page insert
              + " where child_id=" + childId); // childId saved from last child insert
   }

   // Insert row in table - for IssueXref

   private void insertXref(Statement st, IssueXref x, int volumeId, int page_id) throws SQLException
   {
      // get the greatest sequence
      int sequence = 1;
      ResultSet rs = st.executeQuery("select max(sequence) from pageissue" + "  where page_id=" + page_id);
      if (rs.next()) {
         sequence = 1 + rs.getInt(1);
      }
      if (debug) {
         Log.write("    doing pageissue insert: page_id: " + page_id + "/" + sequence);
      }
      st.executeUpdate("insert into pageissue" + "   set page_id=" + page_id + "   , volume_id=" + volumeId + "   , sequence=" + sequence + "   , issue_name=" + '"' + x.issueName + '"');
   }

   private void updateChanges(Statement st, int tableNumber, int id)
   {
      try {
         st.executeUpdate("insert ignore into changes" + " set table_nbr=" + tableNumber + "   , id = " + id //+"   , propagate=0"
                 + "   , age=" + age);
      } catch (SQLException e) {
         if (debug | logging) {
            Log.write("SQLException: " + e.getMessage());
         }
         e.printStackTrace();
         Log.write("changes failure: table #: " + tableNumber + " / id: " + id);
      }
   }

   /** Make a JDBC connection.
     */
   private Connection makeConnection(String dbHost, int dbPort, String dbName, String dbUser, String dbPassword)
           throws DbWriterException
   {

      String dbUrl = "jdbc:mysql://" + dbHost + ":" + dbPort + "/" + dbName;

      try {
         try {
            // The newInstance() call is a work around for some 
                // broken Java implementations

            Class.forName("org.gjt.mm.mysql.Driver").newInstance();
         //Class.forName("com.mysql.jdbc.Driver").newInstance(); 
         } catch (Exception ex) {
            Log.quit("Can't Instantiate Driver: com.mysql.jdbc.Driver");

         }
         return DriverManager.getConnection(dbUrl, dbUser, dbPassword);

      } catch (SQLException e) {
         if (debug | logging) {
            Log.write("SQLException: " + e.getMessage());
         }
         Log.write("Failed to connect with: " + dbUrl + " user: " + dbUser + " p: " + dbPassword);
         e.printStackTrace();
         return null;
      //throw new DbWriterException("Failed to connect with: "+dbUrl);
      }
   }

   /**
     * Exception to rollback and return error message if there
     * is an error while writing.
     */
   public class DbWriterException extends Exception
   {

      DbWriterException(String msg)
      {
         super(msg);
         if (conCodingDb != null) {
            try {
               conCodingDb.rollback();
               conCodingDb.close();
            } catch (SQLException e) {
               //Log.quit("Sql error " + e);
               e.printStackTrace();
            }
         }
      }

   }

}
