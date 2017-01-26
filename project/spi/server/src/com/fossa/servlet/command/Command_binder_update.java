/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.fossa.servlet.command;

import com.fossa.servlet.common.CommonConstants;
import com.fossa.servlet.common.CommonLogger;
import com.fossa.servlet.common.SQLQueries;
import com.fossa.servlet.common.msg.MessageWriter;
import com.fossa.servlet.dao.DBTask;
import com.fossa.servlet.exception.ServerFailException;
import com.fossa.servlet.server.valueobjects.BatchHistoryData;
import com.fossa.servlet.server.valueobjects.BatchProcessHistroyData;
import com.fossa.servlet.server.valueobjects.VolumeHistoryData;
import com.fossa.servlet.session.UserTask;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.w3c.dom.Element;

/**
 * This updates/adds/removes a project binder with provisions to change the document boundaries of the binder.
 * @author Bala
 */
class Command_binder_update implements Command{
    
    public String execute(Element action, UserTask task, DBTask dbTask, MessageWriter writer) {     
        
       int givenPageId = Integer.parseInt(action.getAttribute(A_PAGE_ID));
       boolean remove = "YES".equals(action.getAttribute(A_REMOVE));
       assert givenPageId > 0;
       int givenVolumeId = task.getVolumeId();
        
        try {
            if (remove) {
                removeFromBinder(task, dbTask , givenPageId, givenVolumeId);
            } else {
                addToBinder(task, dbTask , givenPageId, givenVolumeId);
            }
            String userSessionId = task.getFossaSessionId();
            //Start writing the XML.
            writer.startElement(T_OK);
            writer.writeAttribute(A_FOSSAID, userSessionId);
            writer.endElement();
        } catch (IOException exc) {
            CommonLogger.printExceptions(this, "IOException while binder update.", exc);
        } catch (Exception exc) {
            CommonLogger.printExceptions(this, "Exception while binder update." , exc);
        }       
        return null;
    }

    private void addToBinder (UserTask task, DBTask dbTask, int givenPageId, int givenVolumeId)
    throws SQLException {
        
        Connection con = null;
        Statement getLastBatchIdStatement = null;
        ResultSet rs;       
        try{
            con=dbTask.getConnection();
            getLastBatchIdStatement=dbTask.getStatement();
            // find the corresponding child, etc.
            PreparedStatement getChidIDPrepStmt =  con.prepareStatement(SQLQueries.SELECT_CHILD_ID);
            getChidIDPrepStmt.setInt(1, givenPageId);
            getChidIDPrepStmt.setInt(2, givenVolumeId);
            rs = getChidIDPrepStmt.executeQuery();
            
            if (! rs.next()) {
                throw new ServerFailException("Document not found.");
            }
            int childId = rs.getInt(1);
            int lft = rs.getInt(2);
            int rgt = rs.getInt(3);
            int projectId = rs.getInt(4);
            int sequence = rs.getInt(5);
            String imagePath = rs.getString(6);
            String projectName = rs.getString(7);
            int binderVolumeId = rs.getInt(8);
            int binderLft = rs.getInt(9);
            int binderBatchId = rs.getInt(10);
            int binderRangeId = rs.getInt(11);
            rs.close();

            int newBoundary;

            if (binderVolumeId == 0) {
                // create a binder
                PreparedStatement insertVolumePrepStmt =  task.prepareStatement(dbTask,SQLQueries.INSERT_TEST);
                insertVolumePrepStmt.setString(1, projectName);
                insertVolumePrepStmt.setInt(2, projectId);
                insertVolumePrepStmt.executeUpdate();
                binderVolumeId = lastInsertId(getLastBatchIdStatement);
                
                //insert into volume history.
                VolumeHistoryData volumeData = new VolumeHistoryData(con,binderVolumeId);
                volumeData.insertIntoHistoryTable(con,task.getUsersId(),Mode.ADD);
                
                PreparedStatement insertBatchPrepStmt =  task.prepareStatement(dbTask,SQLQueries.INSERT_BATCH_VOLID);
                insertBatchPrepStmt.setInt(1, binderVolumeId);
                insertBatchPrepStmt.executeUpdate();
                getLastBatchIdStatement = con.createStatement();
                //insert into history_batch for audit-history.                   
                ResultSet getTopBatchIdResultSet = getLastBatchIdStatement.executeQuery(SQLQueries.SEL_TOP_BATCH_ID);
                getTopBatchIdResultSet.next();
                int batchId = getTopBatchIdResultSet.getInt(1);                                  
                
                BatchHistoryData batchData = new BatchHistoryData(con,batchId);                
                batchData.insertIntoHistoryTable(con, task.getUsersId(), Mode.ADD);           
                        
                BatchProcessHistroyData data = new BatchProcessHistroyData();
                data.setBatch_id(batchId);
                data.setVolume_id(binderVolumeId);
                data.setProcess(CommonConstants.PROCESS_UNITIZE);
                data.setIs_ready(CommonConstants.STATUS_YES);          
                data.insertIntoHistoryTable(con);                
                binderBatchId = lastInsertId(getLastBatchIdStatement);
                
                PreparedStatement insertRangePrepStmt =  task.prepareStatement(dbTask,SQLQueries.INSERT_RANGE);
                insertRangePrepStmt.setInt(1, binderVolumeId);
                insertRangePrepStmt.executeUpdate();
                
                binderRangeId = lastInsertId(getLastBatchIdStatement);
                newBoundary = B_RANGE;
            } else {
                // binder exists, check that this doc. not in it            	
                PreparedStatement getBinderPrepStmt =  con.prepareStatement(SQLQueries.SEL_BINDER);
                getBinderPrepStmt.setInt(1, binderVolumeId);
                getBinderPrepStmt.setInt(2, lft);
                getBinderPrepStmt.setInt(3, rgt);
                getBinderPrepStmt.executeUpdate();
                
                if (rs.next()) {
                    throw new ServerFailException("Pages of document already in binder.");
                }

                // adjust first page boundary, if inserting at beginning
                if (lft < binderLft) {
                    newBoundary = B_RANGE;
                    PreparedStatement updatePagePrepStmt =  task.prepareStatement(dbTask,SQLQueries.UPD_PAGE);
                    updatePagePrepStmt.setInt(1, binderVolumeId);
                    updatePagePrepStmt.setInt(2, binderLft);
                    updatePagePrepStmt.executeUpdate();
                    
                } else {
                    newBoundary = B_CHILD;
                }
            }

            // create a binder child (managed)
            // Note:  is_update is not copied
            PreparedStatement insertChildPrepStmt =  task.prepareStatement(dbTask,SQLQueries.INSERT_CHILD);
            insertChildPrepStmt.setInt(1, binderVolumeId);
            insertChildPrepStmt.setInt(2, binderBatchId);
            insertChildPrepStmt.setInt(3, binderRangeId);
            insertChildPrepStmt.setInt(4, lft);
            insertChildPrepStmt.setInt(5, rgt);
            insertChildPrepStmt.executeUpdate();
            
            int newChildId = lastInsertId(getLastBatchIdStatement);

            // copy the pages and image files to the database
            PreparedStatement insertPagePrepStmt = con.prepareStatement("INSERT INTO page VALUES(?,?,?,?,default," +
                                                                        "default,'*',default,?,default,?,?,'F',0,?,default)");
            PreparedStatement insertBinderImgPrepStmt = con.prepareStatement("INSERT INTO binder_image VALUES(13310, ? ");
            Statement getPageDetailsStatement = con.createStatement();
            rs = getPageDetailsStatement.executeQuery(SQLQueries.SELECT_SEQ +childId);
            String batesPrefix = "[" + Integer.toString(sequence+1000).substring(1) + "]";
            try {
                while (rs.next()) {
                    int seq = rs.getInt(1);
                    String batesNumber = rs.getString(2);
                    String path = rs.getString(3);
                    String filename = rs.getString(4);
                    int offset = rs.getInt(5);
                    int fileType = rs.getInt(6);
                    String boundaryFlag = rs.getString(7);
                    int rotate = rs.getInt(8);
                    int originalPageId = rs.getInt(9);

                    insertPagePrepStmt.setInt(1,seq);  // seq
                    insertPagePrepStmt.setString(2,batesPrefix + batesNumber);  // bates_number
                    insertPagePrepStmt.setInt(3,originalPageId);  // filename
                    insertPagePrepStmt.setInt(4,offset);  // offset
                    insertPagePrepStmt.setInt(5,fileType);  // file_type
                    insertPagePrepStmt.setString(6,boundaryFlag);  // boundary_flag
                    insertPagePrepStmt.setInt(7,rotate);  // rotate
                    insertPagePrepStmt.setInt(8,newBoundary);  // boundary
                    insertPagePrepStmt.executeUpdate();

                    newBoundary = B_NONE;

                    File imageFile = new File(imagePath, path + "/" + filename);
                    long imageLength = imageFile.length();
                    if (imageLength >= Integer.MAX_VALUE) {
                        throw new ServerFailException("Image too large: "+batesNumber);
                    }
                    InputStream stream = new FileInputStream(imageFile);
                    insertBinderImgPrepStmt.setInt(1, originalPageId);
                    insertBinderImgPrepStmt.setBinaryStream(2, stream, (int) imageLength);
                    insertBinderImgPrepStmt.executeUpdate();
                }
            } catch (IOException e) {
                throw new ServerFailException("Error reading image: "+rs.getString(4));
            }
            getPageDetailsStatement.close();
            insertPagePrepStmt.close();
            insertBinderImgPrepStmt.close();

            // Copy the data values
            PreparedStatement insertvaluePrepStmt =  task.prepareStatement(dbTask,SQLQueries.INSERT_VALUE);
            insertvaluePrepStmt.setInt(1, newChildId);
            insertvaluePrepStmt.setInt(2, childId);
            insertvaluePrepStmt.executeUpdate();
            
            PreparedStatement insertnamevaluePrepStmt =  task.prepareStatement(dbTask,SQLQueries.INSERT_NAME_VALUE);
            insertnamevaluePrepStmt.setInt(1, newChildId);
            insertnamevaluePrepStmt.setInt(2, childId);
            insertnamevaluePrepStmt.executeUpdate();
           
            PreparedStatement insertlongvaluePrepStmt =  task.prepareStatement(dbTask,SQLQueries.INSERT_LONG_VALUE);
            insertlongvaluePrepStmt.setInt(1, newChildId);
            insertlongvaluePrepStmt.setInt(2, childId);
            insertlongvaluePrepStmt.executeUpdate();

            adjustBounds(task, dbTask, binderVolumeId);

        }catch (SQLException sql) {
            CommonLogger.printExceptions(this, "SQLException while adding binder.", sql);
        } catch (Exception exc) {
            CommonLogger.printExceptions(this, "Exception while adding binder.", exc);
        }      
    }
    
    //removes the project binder on basis of a document
    private void removeFromBinder (UserTask task, DBTask dbTask, int givenPageId, int givenVolumeId)
    throws SQLException {
        
        Statement st = null;
        ResultSet getChildIDVolIDResultSet;     
        try{
            st=dbTask.getStatement();
            Connection con = dbTask.getConnection();
            PreparedStatement getChildIDVolIDPrepStmt =  con.prepareStatement(SQLQueries.SEL_CHILD_VOLUME);
            getChildIDVolIDPrepStmt.setInt(1, givenPageId);
            getChildIDVolIDPrepStmt.setInt(2, givenVolumeId);
            getChildIDVolIDResultSet = getChildIDVolIDPrepStmt.executeQuery();
            
            if (! getChildIDVolIDResultSet.next()) {
                throw new ServerFailException("Nothing to remove.");
            }
            int childId = getChildIDVolIDResultSet.getInt(1);
            int volumeId = getChildIDVolIDResultSet.getInt(2);
            getChildIDVolIDResultSet.close();
            st.executeUpdate(SQLQueries.DEL_P +childId);

            task.executeUpdate(dbTask,SQLQueries.DEL_CHILD +childId);
            st.executeUpdate(SQLQueries.DEL_VALUE +childId);
            st.executeUpdate(SQLQueries.DEL_LONG_VALUE +childId);
            st.executeUpdate(SQLQueries.DEL_NAME_VALUE +childId);

            adjustBounds(task, dbTask, volumeId);
        }catch (SQLException sql) {
            CommonLogger.printExceptions(this, "SQLException while removing binder." , sql);
        } catch (Exception exc) {
            CommonLogger.printExceptions(this, "Exception while removing binder." , exc);
        }      
    }

    /**
     * This method fetches the last inserted volume id.
     * @param st  : is a Statement object
     * @return the last inserted volume id.
     * @throws java.sql.SQLException
     */
    private static int lastInsertId(Statement st) throws SQLException {      
        ResultSet rs = st.executeQuery(SQLQueries.SEL_TOP_VOLID);
        rs.next();
        int result = rs.getInt(1);
        rs.close();      
        return result;
    }

    // This method changes the boudary of the project binder
    private static void adjustBounds(UserTask task, DBTask dbTask, int volumeId) 
            throws SQLException {
        
        Statement getLFTRGTStatement = null;
        ResultSet getLFTRGTResultSet;        
        try{
            getLFTRGTStatement=dbTask.getStatement();
            getLFTRGTResultSet = getLFTRGTStatement.executeQuery(SQLQueries.SEL_MIN_LFT +volumeId);
            
            getLFTRGTResultSet.next();
            int lft=getLFTRGTResultSet.getInt(1);
            int rgt=getLFTRGTResultSet.getInt(2);
            if (rgt != 0) {                
                PreparedStatement updateRangePrepStmt =  task.prepareStatement(dbTask,SQLQueries.UPD_VOLUME_VRB);
                updateRangePrepStmt.setInt(1, lft);
                updateRangePrepStmt.setInt(2, rgt);
                updateRangePrepStmt.setInt(3, volumeId);
                updateRangePrepStmt.executeUpdate();
                
                VolumeHistoryData volumeData = new VolumeHistoryData(dbTask.getConnection(),volumeId);
                volumeData.insertIntoHistoryTable(dbTask.getConnection(),task.getUsersId(),Mode.EDIT);
                
                // Make first page start of range
                // (Old first page is already child, if required)                
                PreparedStatement updateboundaryPrepStmt =  task.prepareStatement(dbTask,SQLQueries.UPD_BOUNDARY);
                updateboundaryPrepStmt.setInt(1, volumeId);
                updateboundaryPrepStmt.setInt(2, lft);
                updateboundaryPrepStmt.executeUpdate();
            } else {
                // Binder is empty, remove volume and range and batch
                PreparedStatement deleteVolPrepStmt =  task.prepareStatement(dbTask,SQLQueries.DEL_V);
                deleteVolPrepStmt.setInt(1, volumeId);
                deleteVolPrepStmt.executeUpdate();
                //Take batch history 
                BatchHistoryData batchData = new  BatchHistoryData(dbTask.getConnection(),volumeId);                             
                batchData.insertIntoHistoryTable(dbTask.getConnection(),task.getUsersId(), Mode.DELETE);                
            }
            getLFTRGTResultSet.close();
        }catch (SQLException sql) {
            logger.error("SQLException while adjusting bounds in binder update." + sql);
            StringWriter sw = new StringWriter();
            sql.printStackTrace(new PrintWriter(sw));
            logger.error(sw.toString());
        } catch (Exception exc) {
            logger.error("Exception while adjusting bounds in binder update." + exc);
            StringWriter sw = new StringWriter();
            exc.printStackTrace(new PrintWriter(sw));
            logger.error(sw.toString());
        }
    }
    
    public boolean isReadOnly() {
         return true;
    }    
}
