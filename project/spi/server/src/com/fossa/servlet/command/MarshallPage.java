/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.fossa.servlet.command;

import com.fossa.servlet.common.CodingData;
import com.fossa.servlet.common.CommonLogger;
import com.fossa.servlet.common.ImageData;
import com.fossa.servlet.common.Log;
import com.fossa.servlet.common.MessageConstants;
import com.fossa.servlet.common.SQLQueries;
import com.fossa.servlet.dao.DBTask;
import com.fossa.servlet.exception.ServerFailException;
import com.fossa.servlet.session.UserTask;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import org.w3c.dom.Element;

/**
 * Given parameters, find the request page in the database.
 * @see MarshallQAPage
 *  @author prakash
 */

public class MarshallPage implements MessageConstants{

    final protected static boolean DEBUG = false;
    protected Connection con;
    protected Statement st;
    protected UserTask task;
    protected DBTask dbTask;
    protected int volumeId;
    private int batchId = 0;
    private boolean isBinder = false;
    private String whichStatus;    
    
    /**
     * Factory method to create MarshallPage.  Creates subclass
     * MarshallQAPage if this request is from a QA user.
     */
    public static MarshallPage makeInstance(UserTask task, DBTask dbTask, Element action)
    throws SQLException {
        String statusString = action.getAttribute(A_STATUS);
        if ("QA".equals(statusString)) {            
            MarshallQAPage result = new MarshallQAPage(task, dbTask);
            return result;
        } else {
            MarshallPage result;
            result = new MarshallPage(task, dbTask, statusString);
            return result;
        }
    }

    /**
     * Create a new <code>MarshallPage</code>.
     * @param statusString the status of the client user
     */
    protected MarshallPage(UserTask task, DBTask dbTask, String statusString)
    throws SQLException {
        this.task = task;
        ResultSet rs = null;
        try{
            volumeId = task.getVolumeId();
            con= dbTask.getConnection();
            st = dbTask.getStatement();
            whichStatus = statusString;
            isBinder = "Binder".equals(whichStatus);
            if(isBinder) {
                // Make sure volumeId is for the binder volume and get batch
                // Note.  there is only one binder volume and batch
                PreparedStatement select_mshall_vid =  task.prepareStatement(dbTask,SQLQueries.SEL_MSHALL_VID);
                select_mshall_vid.setInt(1, volumeId);
                rs = select_mshall_vid.executeQuery();
                
                if (! rs.next()) {
                    throw new ServerFailException(
                        "There is no binder for this project.");
                }
                volumeId = rs.getInt(1);
                batchId = rs.getInt(2);
                rs.close();
            } else { // since it's not for the binder
                batchId = task.getBatchId();
            }
        }catch (SQLException sql) {
            CommonLogger.printExceptions(this, "SQLException while getting binder for a project" , sql);
        } catch (Exception exc) {
            CommonLogger.printExceptions(this, "Exception while getting binder for a project" ,exc);
        }
    }

    /**
     * Return the volume determined for this MarshallPage instance.
     * @return the volume.volume_id
     */
    public int getVolumeId() {
        return volumeId;
    }

    /**
     * Return batch determined for this MarshallPage.
     */
    public int getBatchId() {
        return batchId;
    }

    /**
     * Find page, given relative position of page or child in entire batch.
     * @param delta the relative position of the return page to the beginning
     * or end of the batch; cannot be 0
     * @param boundary the kind of page to return, B_NONE = any kind; B_CHILD = child
     * and B_RANGE is invalid for this function
     * @return the page.page_id that fits the given parameters
     */
    public int [] findPositionInBatch(int delta, int boundary) throws SQLException {       
        String level ="";        
        ResultSet rst = st.executeQuery("select batch_id,treatment_level from batch where batch_id="+batchId);
        if(rst.next()){
          level = rst.getString(2);        
        }		
        int [] result = new int[2];
        assert delta != 0;  // delta == 0 handled by caller
        String sql;
        switch (boundary) {
        case B_NONE:
          assert (! isBinder);
         if(level.equals("L1")){
            if(delta > 0){
                sql = "SELECT P.project_l1_id, P.child_id FROM project_l1 P, batch B WHERE B.batch_id="+batchId+" and P.volume_id = B.volume_id and P.seq between B.lft and B.rgt and P.seq =  (SELECT TOP 1 seq  FROM (SELECT TOP "+(delta - 1)+" seq FROM project_l1 ORDER BY seq DESC) as TOPN(seq) ORDER BY seq ASC) ORDER BY P.seq";  
            }
            else{
                sql = "SELECT P.project_l1_id, P.child_id FROM project_l1 P, batch B WHERE B.batch_id="+batchId+" and P.volume_id = B.volume_id and P.seq between B.lft and B.rgt and P.seq = (SELECT TOP 1 seq  FROM (SELECT TOP "+(- delta - 1)+" seq FROM project_l1 ORDER BY seq desc) as TOPN(seq) ORDER BY seq ASC) ORDER BY P.seq desc";
            }
	}else{
            if(delta > 0){
                sql = "SELECT P.page_id,P.child_id FROM page P, batch B WHERE B.batch_id="+batchId+" and P.volume_id = B.volume_id and P.seq between B.lft and B.rgt and P.seq =  (SELECT TOP 1 seq  FROM (SELECT TOP "+(delta - 1)+" seq FROM page ORDER BY seq DESC) as TOPN(seq) ORDER BY seq ASC) ORDER BY P.seq";  
            }
            else{
                sql = "SELECT P.page_id,P.child_id FROM page P, batch B WHERE B.batch_id="+batchId+" and P.volume_id = B.volume_id and P.seq between B.lft and B.rgt and P.seq = (SELECT TOP 1 seq  FROM (SELECT TOP "+(- delta - 1)+" seq FROM page ORDER BY seq desc) as TOPN(seq) ORDER BY seq ASC) ORDER BY P.seq desc";
		}
        }
            
            break;
        case B_CHILD:
            if(level.equals("L1")){
            
            sql =  " Declare @delta int set @delta = "+ delta 
            + " declare @string varchar(5000) set @string = '' "
            + " if(@delta > 0) begin set @string = ' select top 1 obj.* " 
            + " from (select top ' + Convert(varchar(10),@delta)  +' P.project_l1_id, P.child_id  "
            + " from project_l1 P "
            + " inner join child c "
            + " on P.volume_id = c.volume_id and P.seq = c.lft , Batch B "
            + " where B.batch_id = " +batchId
            + " and C.volume_id = B.volume_id and C.lft between B.lft and B.rgt "
            + " order by c.lft) obj order by project_l1_id desc ' " 
            + " end else begin "
            + " set @string = 'select top 1 P.project_l1_id, P.child_id "
            + " from project_l1 P inner join child c  on  P.volume_id = c.volume_id "
            + " and P.seq = c.lft , Batch B "
            + " where B.batch_id = " +batchId+" and C.volume_id = B.volume_id "
            + " and C.lft between B.lft and B.rgt order by c.rgt' "
            + " end exec (@string)";
		}else{
		sql =  " Declare @delta int set @delta = "+ delta 
            + " declare @string varchar(5000) set @string = '' "
            + " if(@delta > 0) begin set @string = ' select top 1 obj.* " 
            + " from (select top ' + Convert(varchar(10),@delta)  +' P.page_id, P.child_id  "
            + " from page P "
            + " inner join child c "
            + " on P.volume_id = c.volume_id and P.seq = c.lft , Batch B "
            + " where B.batch_id = " +batchId
            + " and C.volume_id = B.volume_id and C.lft between B.lft and B.rgt "
            + " order by c.lft) obj order by page_id desc ' " 
            + " end else begin "
            + " set @string = 'select top 1 P.page_id,P.child_id "
            + " from page P inner join child c  on  P.volume_id = c.volume_id "
            + " and P.seq = c.lft , Batch B "
            + " where B.batch_id = " +batchId+" and C.volume_id = B.volume_id "
            + " and C.lft between B.lft and B.rgt order by c.rgt' "
            + " end exec (@string)";
		}
            break;
        default:
            Log.quit("MarshallPage.findPositionInBatch: invalid boundary: "+boundary);
            sql = null;
        }        
        ResultSet rs = st.executeQuery(sql);
        if (rs.next()) {           
            result[0] = rs.getInt(1);
            result[1] = rs.getInt(2);
             System.out.println("page_id   "+ result[0]);
             System.out.println("child_id  "+ result[1]);
        }
        rs.close();        
        return result;
    }


    /**
     * Find the first page of first child, within the batch, that has not been coded.
     * @return The page_id of the page; 0 if the page was not found.
     */
    public int findUncoded() throws SQLException {                
        int result = 0;
        ResultSet rs;
        if (whichStatus.startsWith("U")) {
            rs = st.executeQuery(SQLQueries.SEL_MSHALL_PSEQ +batchId);                       
            int highUnitizedSeq = (rs.next() ? rs.getInt(1) : 0);
            PreparedStatement select_mshall_toppid =  con.prepareStatement(SQLQueries.SEL_MSHALL_TOPPID);
            select_mshall_toppid.setInt(1, highUnitizedSeq);
            select_mshall_toppid.setInt(2, batchId);
            rs = select_mshall_toppid.executeQuery();
            
        } else {
            rs = st.executeQuery(
                "select top 1 P.page_id"
                +" from child C, batch B, page P"
                +" left join childcoded CC"
                +"   on P.child_id =  CC.child_id"
                +"   and CC.round = 0"
                +" where B.batch_id = "+batchId
                +"   and C.volume_id = B.volume_id"
                +"   and C.lft between B.lft and B.rgt"
                +"   and P.volume_id = C.volume_id"
                +"   and P.seq = C.lft"
                +"   and CC.child_id is null"
                +" order by C.lft");
        }
        if (rs.next()) {
            result = rs.getInt(1);
        }
        rs.close();       
        return result;
    }

    /**
     * Return the page.page_id of a page of type <code>boundary</code> and
     * <code>delta</code> number of boundaries away from <code>pageId</code>
     * Note: The page returned can be beyond the limit of the current batch
     * @param pageId must be > 0, the page.page_id of the current page
     * @param delta the relative position of the return page to pageId; can be 0
     * @param boundary the kind of page to return, B_NONE = any kind; B_CHILD = child
     * and B_RANGE is invalid for this function
     * @param findEnd true to find the last child in the range; false for the first
     * @return the page.page_id that fits the given parameters
     */
    public int[] findRelative(int pageId, int delta, int boundary, boolean findEnd)
    throws SQLException {
        String level ="";
        
        if(batchId <= 0){
            ResultSet pgid_tableresult = st.executeQuery("select * from project_l1 where project_l1_id = " +pageId);
            if(pgid_tableresult.next()){
                level = "L1";
            }else{
                level = "L2";
            }
        }
        ResultSet getLevelResultSet = st.executeQuery("select batch_id,treatment_level from batch where batch_id="+batchId);
        if(getLevelResultSet.next()){
          level = getLevelResultSet.getString(2);        
        }
        
        String sql;
        switch (boundary) {
        case B_NONE:            
            if (delta == 0) {
                sql =SQLQueries.SEL_MSHALL_PAGE +pageId;
            } else {  // since delta != 0
                if(level.equals("L1")){
                sql = "Declare @delta int set @delta = " +delta  
                        +" declare @string varchar(5000) set @string = ''" 
                        +" if(@delta > 0) begin "
                        +" Set @string = 'select top 1 obj.*" 
                        +" from (select top '+  Convert(varchar(10),@delta)  +' P2.project_l1_id,P2.child_id "
                        +" from project_l1 P, project_l1 P2 "
                        +" where P2.volume_id = P.volume_id and P2.seq > P.seq "  
                        +" and P.project_l1_id= "+pageId
                        +" order by P2.seq) obj order by project_l1_id asc'"   
                        +" end else begin  Set @string = 'select top 1 P2.project_l1_id, P2.child_id"     
                        +" from project_l1 P, project_l1 P2      where P2.volume_id = P.volume_id"
                        +" and P2.seq < P.seq and P.project_l1_id="+pageId
                        +" order by P2.seq desc' end exec (@string)";
                }else{
                    sql = "Declare @delta int set @delta = " +delta  
                        +" declare @string varchar(5000) set @string = ''" 
                        +" if(@delta > 0) begin "
                        +" Set @string = 'select top 1 obj.*" 
                        +" from (select top '+  Convert(varchar(10),@delta)  +' P2.page_id, P2.child_id "
                        +" from page P, page P2 "
                        +" where P2.volume_id = P.volume_id and P2.seq > P.seq "  
                        +" and P.page_id= "+pageId
                        +" order by P2.seq) obj order by page_id asc'"   
                        +" end else begin  Set @string = 'select top 1 P2.page_id,P2.child_id"     
                        +" from page P, page P2      where P2.volume_id = P.volume_id"
                        +" and P2.seq < P.seq and P.page_id="+pageId
                        +" order by P2.seq desc' end exec (@string)";
                }                                   
            }
            break;
        case B_CHILD:            
             if(level.equals("L1")){
             sql = "Declare @delta int  set @delta = "+delta+"  " +
                        " Declare @FindEnd as varchar(7) set @FindEnd =  '"+findEnd+"' " +
                        " declare @string varchar(5000) set @string = '' " +
                        " if(@delta >= 0) begin  if @FindEnd =  'True'  begin   " +
                        " Set @string = 'select top 1 obj.*" +
                        " from (select top '+  Convert(varchar(10),@delta + 1)  +' P2.project_l1_id,P2.child_id" +
                        " from project_l1 P, child C, project_l1 P2 where C.volume_id = P.volume_id " +
                        " and C.rgt >= P.seq and P.project_l1_id="+pageId+" and P2.volume_id=C.volume_id " +
                        " and P2.seq= C.rgt order by C.rgt asc) obj order by project_l1_id desc'   " +
                        " end  else begin   Set @string = 'select top 1 obj.* " +
                        " from (select top '+  Convert(varchar(10),@delta + 1)  +' P2.project_l1_id,P2.child_id" +
                        " from project_l1 P, child C, project_l1 P2 where C.volume_id = P.volume_id  " +
                        " and C.rgt >= P.seq and P.project_l1_id="+pageId+" " +
                        " and P2.volume_id=C.volume_id " +
                        " and P2.seq= C.lft order by C.rgt ) obj order by project_l1_id desc' " +
                        " end end else  begin  " +
                        " if @FindEnd =  'True'   begin    Set @string = 'select top 1 obj.*from (select top '+  Convert(varchar(10),@delta + 1)  +' " +
                        " P2.project_l1_id,P2.child_id from project_l1 P, child C, project_l1 P2" +
                        " where C.volume_id = P.volume_id and C.lft <= P.seq  " +
                        " and P.project_l1_id= "+pageId+" " +
                        " and P2.volume_id=C.volume_id  and P2.seq= C.rgt " +
                        " order by C.lft desc) obj order by project_l1_id asc'   end   else  begin   " +
                        " Set @string = 'select top 1 obj.*from (select top '+  Convert(varchar(10),1 - @delta)  +'  P2.project_l1_id, P2.child_id from project_l1 P, child C, project_l1 P2 " +
                        " where C.volume_id = P.volume_id  and C.lft <= P.seq and P.project_l1_id="+pageId+" " +
                        " and P2.volume_id=C.volume_id and P2.seq= C.lft order by C.lft desc) obj order by project_l1_id asc ' " +
                        " end  end  exec (@string)";
             }else{
             sql = "Declare @delta int  set @delta = "+delta+"  " +
                        "Declare @FindEnd as varchar(7) set @FindEnd =  '"+findEnd+"' " +
                        " declare @string varchar(5000) set @string = '' " +
                        " if(@delta >= 0) begin  if @FindEnd =  'True'  begin   " +
                        " Set @string = 'select top 1 obj.*" +
                        " from (select top '+  Convert(varchar(10),@delta + 1)  +' P2.page_id,P2.child_id  " +
                        " from page P, child C, page P2 where C.volume_id = P.volume_id " +
                        " and C.rgt >= P.seq and P.page_id="+pageId+" and P2.volume_id=C.volume_id " +
                        " and P2.seq= C.rgt order by C.rgt asc) obj order by page_id desc'   " +
                        " end  else begin   Set @string = 'select top 1 obj.* " +
                        " from (select top '+  Convert(varchar(10),@delta + 1)  +' P2.page_id,P2.child_id " +
                        " from page P, child C, page P2 where C.volume_id = P.volume_id  " +
                        " and C.rgt >= P.seq and P.page_id="+pageId+" " +
                        " and P2.volume_id=C.volume_id " +
                        " and P2.seq= C.lft order by C.rgt ) obj order by page_id desc' " +
                        " end end else  begin  " +
                        " if @FindEnd =  'True'   begin    Set @string = 'select top 1 obj.*from (select top '+  Convert(varchar(10),@delta + 1)  +' " +
                        " P2.page_id,P2.child_id from page P, child C, page P2" +
                        " where C.volume_id = P.volume_id and C.lft <= P.seq  " +
                        " and P.page_id= "+pageId+" " +
                        " and P2.volume_id=C.volume_id  and P2.seq= C.rgt " +
                        " order by C.lft desc) obj order by page_id asc'   end   else  begin   " +
                        " Set @string = 'select top 1 obj.*from (select top '+  Convert(varchar(10),1 - @delta)  +'  P2.page_id,P2.child_id from page P, child C, page P2 " +
                        " where C.volume_id = P.volume_id  and C.lft <= P.seq and P.page_id="+pageId+" " +
                        " and P2.volume_id=C.volume_id and P2.seq= C.lft order by C.lft desc) obj order by page_id asc ' " +
                        " end  end  exec (@string)";
             }
            break;
        case B_RANGE:            
            if(level.equals("L1")){
            sql = " Declare @delta int  set @delta = "+delta+"  " +
                    " Declare @FindEnd as varchar(7) set @FindEnd =  '"+findEnd+"' " +
                    " declare @string varchar(5000) set @string = '' " +
                    " if(@delta >= 0) begin  if @FindEnd =  'True'  begin   " +
                    " Set @string = 'select top 1 obj.*         " +
                    " from (select top '+  Convert(varchar(10),@delta)  +' P2.project_l1_id, P2.child_id " +
                    " from project_l1 P, range R, project_l1 P2 where R.volume_id = P.volume_id " +
                    " and R.rgt >= P.seq and P.project_l1_id="+pageId+" " +
                    " and P2.volume_id=R.volume_id and P2.seq= R.rgt order by R.rgt)" +
                    " obj order by project_l1_id asc'  end  else  Begin   " +
                    " Set @string = 'select top 1 obj.*" +
                    " from (select top '+  Convert(varchar(10),@delta)  +' P2.project_l1_id,P2.child_id " +
                    " from project_l1 P, range R, project_l1 P2 where R.volume_id = P.volume_id " +
                    " and R.rgt >= P.seq and P.project_l1_id="+pageId+" " +
                    " and P2.volume_id=R.volume_id and P2.seq= R.lft order by R.rgt) " +
                    " obj order by project_l1_id asc' end end else begin  " +
                    " if @FindEnd =  'True'  begin   Set @string = 'select top 1         " +
                    " P2.project_l1_id,P2.child_id from project_l1 P, range R, project_l1 P2 where R.volume_id = P.volume_id         " +
                    " and R.lft <= P.seq and P.project_l1_id="+pageId+"         " +
                    " and P2.volume_id=R.volume_id and P2.seq = R.rgt order by R.lft desc'" +
                    " end   else  Begin   Set @string = 'select top 1 P2.project_l1_id,P2.child_id" +
                    " from project_l1 P, range R, project_l1 P2 where R.volume_id = P.volume_id         " +
                    " and R.lft <= P.seq          and P.project_l1_id="+pageId+" " +
                    " and P2.volume_id=R.volume_id  and P2.seq = R.lft       " +
                    " order by R.lft desc'   End end exec (@string)";
            }else{
              sql = "Declare @delta int  set @delta = "+delta+"  " +
                    " Declare @FindEnd as varchar(7) set @FindEnd =  '"+findEnd+"' " +
                    " declare @string varchar(5000) set @string = '' " +
                    " if(@delta >= 0) begin  if @FindEnd =  'True'  begin   " +
                    " Set @string = 'select top 1 obj.*         " +
                    " from (select top '+  Convert(varchar(10),@delta)  +' P2.page_id, P2.child_id " +
                    " from page P, range R, page P2 where R.volume_id = P.volume_id " +
                    " and R.rgt >= P.seq and P.page_id="+pageId+" " +
                    " and P2.volume_id=R.volume_id and P2.seq= R.rgt order by R.rgt)" +
                    " obj order by page_id asc'  end  else  Begin   " +
                    " Set @string = 'select top 1 obj.*" +
                    " from (select top '+  Convert(varchar(10),@delta)  +' P2.page_id,P2.child_id" +
                    " from page P, range R, page P2 where R.volume_id = P.volume_id " +
                    " and R.rgt >= P.seq and P.page_id="+pageId+" " +
                    " and P2.volume_id=R.volume_id and P2.seq= R.lft order by R.rgt) " +
                    " obj order by page_id asc' end end else begin  " +
                    " if @FindEnd =  'True'  begin   Set @string = 'select top 1         " +
                    " P2.page_id,P2.child_id from page P, range R, page P2 where R.volume_id = P.volume_id         " +
                    " and R.lft <= P.seq and P.page_id="+pageId+"         " +
                    " and P2.volume_id=R.volume_id and P2.seq = R.rgt order by R.lft desc'" +
                    " end   else  Begin   Set @string = 'select top 1 P2.page_id,P2.child_id" +
                    " from page P, range R, page P2 where R.volume_id = P.volume_id         " +
                    " and R.lft <= P.seq and P.page_id="+pageId+" " +
                    " and P2.volume_id=R.volume_id  and P2.seq = R.lft       " +
                    " order by R.lft desc'   End end exec (@string)";  
            }        	
            break;
        default:
            Log.quit("MarshallPage: invalid boundary.findRelative: "+boundary);
            sql = null;
        }

        int [] result = new int[2];        
        ResultSet rs = st.executeQuery(sql);
        if (rs.next()) {
            result[0] = rs.getInt(1);
            result[1] = rs.getInt(2);
        }
        rs.close();               
        return result;
    }

    /**
     * Return the page.page_id of a page of type <code>boundary</code> and
     * <code>delta</code> number of boundaries away from <code>pageId</code>
     * @param pageId must be > 0, the page.page_id of the current page
     * @param delta the relative position of the return page to pageId; can be 0
     * @param boundary the kind of page to return, B_NONE = any kind; B_CHILD = child
     * and B_RANGE is invalid for this function
     * @return the page.page_id that fits the given parameters
     */
    public int [] findRelativeInBatch(int pageId, int delta, int boundary)
    throws SQLException {     

        String level ="";        
        ResultSet getLevelResultSet = st.executeQuery("select batch_id,treatment_level from batch where batch_id="+batchId);
        if(getLevelResultSet.next()){
          level = getLevelResultSet.getString(2);        
        }
                
        String sql;
        switch (boundary) {
        case B_NONE:
            if (delta == 0) {
                if(level.equals("L1")){
                        sql = "SELECT P.project_l1_id, P.child_id" 
                        +" FROM project_l1 P" 
                        +" inner join child C on C.child_id = P.child_id" 
                        +" WHERE P.project_l1_id=" +pageId 
                        +" and C.batch_id=" +batchId; 
                }else{
                        sql = "SELECT P.page_id, P.child_id" 
                        +" FROM page P" 
                        +" inner join child C on C.child_id = P.child_id" 
                        +" WHERE P.page_id=" +pageId 
                        +" and C.batch_id=" +batchId;					
                }   
            } else { // since delta != 0                
            if(level.equals("L1")){
                	sql = "Declare @delta int set @delta =  "+delta
                            +" declare @string varchar(5000) set @string = ''"
                            +" if(@delta > 0) begin"
                            +" Set @string =  'select top 1 obj.*"
                            +" from (select top '+  Convert(varchar(10),@delta)  +' P2.project_l1_id,P2.child_id"
                            +" from project_l1 P, project_l1 P2, batch B where P2.volume_id = P.volume_id"
                            +" and P2.seq > P.seq and P.project_l1_id="+pageId
                            +" and B.batch_id="+batchId
                            +" and P2.seq between B.lft and B.rgt"
                            +" order by P2.seq ) obj"
                            +" order by  project_l1_id asc'"
                            +" end else begin"
                            +" Set @string = 'select  top 1  p2.project_l1_id,P2.child_id from project_l1 P,project_l1 P2, batch B"
                            +" where P2.volume_id = P.volume_id and P2.seq < P.seq"
                            +" and P.project_l1_id="+pageId
                            +" and B.batch_id="+batchId
                            +" and P2.seq between B.lft and B.rgt"
                            +" order by P2.seq asc'"
                        +" end exec (@string)"; 
			}else{
			sql = "Declare @delta int set @delta =  "+delta
                            +" declare @string varchar(5000) set @string = ''"
                            +" if(@delta > 0) begin"
                            +" Set @string =  'select top 1 obj.*"
                            +" from (select top '+  Convert(varchar(10),@delta)  +' P2.page_id,P2.child_id"
                            +" from page P, page P2, batch B where P2.volume_id = P.volume_id"
                            +" and P2.seq > P.seq and P.page_id="+pageId
                            +" and B.batch_id="+batchId
                            +" and P2.seq between B.lft and B.rgt"
                            +" order by P2.seq ) obj"
                            +" order by  page_id asc'"
                            +" end else begin"
                            +" Set @string = 'select  top 1  p2.page_id,P2.child_id from page P,page P2, batch B"
                            +" where P2.volume_id = P.volume_id and P2.seq < P.seq"
                            +" and P.page_id="+pageId
                            +" and B.batch_id="+batchId
                            +" and P2.seq between B.lft and B.rgt"
                            +" order by P2.seq asc'"
                        +" end exec (@string)";
			}            	
            }
            break;
        case B_CHILD:

            if(level.equals("L1")){
	            sql = "Declare @delta int set @delta = "+delta 
	                +" declare @string varchar(5000) set @string = ''"
	                +" if(@delta > 0) begin Set @string = 'select top 1 obj.*"
	                +" from (select top '+  Convert(varchar(10),@delta+1)  +' P2.project_l1_id,P2.child_id"
	                +" from project_l1 P, child C, batch B, project_l1 P2 where C.volume_id = P.volume_id"
	                +" and C.rgt >= P.seq and P.project_l1_id="+pageId
	                +" and B.batch_id=" +batchId
	                +" and C.rgt between B.lft and B.rgt and P2.volume_id=C.volume_id"
	                +" and P2.seq=C.lft order by C.rgt) obj"
	                +" order by project_l1_id desc'"					
	                +" end else begin"
	                +" Set @string = 'select top 1 P2.project_l1_id,P2.child_id"
	                +" from project_l1 P, child C, batch B, project_l1 P2"
	                +" where C.volume_id = P.volume_id"	
	                +" and C.rgt <= P.seq"	
	                +" and P.project_l1_id="+pageId
	                +" and B.batch_id="+batchId
	                +" and C.rgt between B.lft and B.rgt"
	                +" and P2.volume_id=C.volume_id"
	                +" and P2.seq=C.lft"	
	                +" order by C.lft desc '"
	                +" end exec (@string)";
			}else{
				sql = "Declare @delta int set @delta = "+delta 
	                +" declare @string varchar(5000) set @string = ''"
	                +" if(@delta > 0) begin Set @string = 'select top 1 obj.*"
	                +" from (select top '+  Convert(varchar(10),@delta+1)  +' P2.page_id, P2.child_id"
	                +" from page P, child C, batch B, page P2 where C.volume_id = P.volume_id"
	                +" and C.rgt >= P.seq and P.page_id="+pageId
	                +" and B.batch_id=" +batchId
	                +" and C.rgt between B.lft and B.rgt and P2.volume_id=C.volume_id"
	                +" and P2.seq=C.lft order by C.rgt) obj"
	                +" order by page_id desc'"					
	                +" end else begin"
	                +" Set @string = 'select top 1 P2.page_id, P2.child_id"
	                +" from page P, child C, batch B, page P2"
	                +" where C.volume_id = P.volume_id"	
	                +" and C.rgt <= P.seq"	
	                +" and P.page_id="+pageId
	                +" and B.batch_id="+batchId
	                +" and C.rgt between B.lft and B.rgt"
	                +" and P2.volume_id=C.volume_id"
	                +" and P2.seq=C.lft"	
	                +" order by C.lft desc '"
	                +" end exec (@string)";
			}        	
            break;      
        default:
            Log.quit("MarshallPage: invalid boundary.findRelativeInBatch: "+boundary);
            sql = null;
        }

        int [] result = new int[2];        
        ResultSet rs = st.executeQuery(sql);
        if (rs.next()) {
            result[0] = rs.getInt(1);
            result[1] = rs.getInt(2);
        }
        rs.close();        
        return result;
    }
    
    //Get the relative page
    public int findRelativeInBatch(int pageId, int delta, int boundary,int batchid,int volumeid)
    throws SQLException {       
       
        String sql;
        switch (boundary) {
        case B_NONE:
            if (delta == 0) {
                sql = "SELECT page_id"
                	  +" FROM page P"
                	  +" inner join child C on C.child_id = P.child_id"
                	  +" WHERE P.page_id="+pageId 
                	  +" and C.batch_id="+batchid;  
                                
            } else { // since delta != 0
            	String where = null;
            	String orderBy = "";
            	String dir = "";
            	int topUp = 0;
            	if(delta > 0) {
            		where = "  and C.rgt >= P.seq";
            		topUp = (delta - 1);
            		dir = "rgt";
            	} else {
            		where = "  and C.lft <= P.seq";
            		topUp = (- delta - 1);
            		dir = "lft";
            		orderBy = "DESC";
            	}
            	sql = "SELECT P2.page_id"
            		  + "FROM page P, child C, batch B, page P2"
            		  + "WHERE C.volume_id = P.volume_id"
            		  + where//and C.rgt >= P.seq
            		  + "and P.page_id=" +pageId
            		  + "and B.batch_id=" +batchId
            		  + "and C.rgt between B.lft and B.rgt"
            		  + "and P2.volume_id=C.volume_id" 
            		  + "and P2.seq=C.lft"
            		  + "and C."+dir+" =" 
            		  + "(SELECT TOP 1" +dir
            		  + "FROM (SELECT TOP" + topUp +"" +dir+ " FROM child"
            		  + "ORDER BY "+dir+" DESC) as TOPN("+dir+")" 
            		  + "ORDER BY" +dir+" ASC)"
            	      + "ORDER BY C."+dir+""+orderBy;            	            
            }
            break;
        case B_CHILD:
        	String where = null;
        	String orderBy = "";
        	String dir = "";
        	String fn = "";
        	int topUp = 0;
        	String findEnd = "";
        	if(delta > 0) {
        		where = " and C.rgt >= P.seq";
        		topUp = (delta - 1);
        		dir = "rgt";
        		fn = "dbo.fn_Test";
        		findEnd = "TRUE";
        	} else {
        		where = "  and C.lft <= P.seq ";
        		topUp = (- delta - 1);
        		dir = "lft";
        		fn = "dbo.fn_CHKBOOLEAN";
        		orderBy = "DESC";
        		findEnd = "FALSE";
        	}
        	sql = "SELECT P2.page_id"
        		  + "FROM page P, child C, page P2"
        	      + "WHERE C.volume_id = P.volume_id"
        	      + where//and C.rgt >= P.seq 
        	      + "and P.page_id= 1"
        	      + "and P2.volume_id=C.volume_id" 
        	      + "and P2.seq = ("+fn+"("+findEnd+", C.rgt, C.lft))"
        	      + "and C.rgt =" 
        	      + "(SELECT TOP 1" +dir
        	      + "FROM (SELECT TOP" + topUp +"" +dir+ " FROM child"
        	      + "ORDER BY" +dir+" DESC) as TOPN("+dir+")" 
        	      + "ORDER BY" +dir+" ASC)"
        	      + "ORDER BY C."+dir+""+orderBy;                       
            break;
        default:            
            sql = null;
        }

        int result = 0;
        ResultSet rs = st.executeQuery(sql);
        if (rs.next()) {            
            result = rs.getInt(1);
        }
        rs.close();
        return result;
    }
    
    //Get the absolute page
    public int [] findAbsolute(String bates) throws SQLException {        
        String level ="";        
        ResultSet rst = st.executeQuery("select batch_id,treatment_level from batch where batch_id="+batchId);
        if(rst.next()){
          level = rst.getString(2);        
        }
         PreparedStatement ps =null;
        if(level.equals("L1")){
            
          ps = con.prepareStatement(
            "select project_l1_id,child_id"
            +" from project_l1"
            +" where volume_id=?"
            +"   and bates_number=?");
        }else{
             ps = con.prepareStatement(
                "select page_id,child_id"
                +" from page"
                +" where volume_id=?"
                +"   and bates_number=?");
        }
        ps.setInt(1,volumeId);
        ps.setString(2,bates);
        ResultSet rs = ps.executeQuery();
       int [] result = new int[2];
        if (rs.next()) {            
            result[0] = rs.getInt(1);
            result[1] = rs.getInt(2);
        }
        ps.close();       
        return result;
    }
    
    /**
     * Find the page_id corresponding to a given child.
     * (Used by validate_batch.)
     */
    public int [] findChild(int childId) throws SQLException {        
                String level =""; 
                int batch_id =0;        
                ResultSet rst = st.executeQuery("select batch_id from child_id where child_id="+childId);
                if(rst.next()){
                  batch_id = rst.getInt(1);        
                }
                ResultSet rst1 = st.executeQuery("select batch_id,treatment_level from batch where batch_id="+batch_id);
                if(rst1.next()){
                  level = rst1.getString(2);        
                }
                 ResultSet rs =null;
                if(level.equals("L1")){
                rs = st.executeQuery(
                "select P.project_l1_id,P.child_id"
                +" from child C inner join project_l1 P"
                +"     on C.volume_id=P.volume_id and C.lft=P.seq"
                +" where C.child_id="+childId);
                }else{
                 rs = st.executeQuery(
                "select P.page_id,P.child_id"
                +" from child C inner join page P"
                +"     on C.volume_id=P.volume_id and C.lft=P.seq"
                +" where C.child_id="+childId);
                }
         
        if (! rs.next()) {
            Log.quit("findChild: child not found");
        }
        int [] result = new int [2];
        result[0] = rs.getInt(1);
        result[1] = rs.getInt(2);
        rs.close();
        return result;
    }

    /**
     * For the given page.page_id, create an instance of ImageData for use
     * in retrieving an image from the server.
     * @param pageId the page.page_id for which image data is being requested
     * @return an ImageData instance for the given pageId
     * @see common.ImageData
     */
    public ImageData collectImageData(int pageId) throws SQLException {               
        ImageData data = new ImageData();
         int child_id = 0;
        String level = "";
        int batch_id = 0;
        ResultSet rst = st.executeQuery("select child_id from page where page_id="+pageId +" and volume_id=" + volumeId);
        if(rst.next()){
            child_id = rst.getInt(1);            
        }else{
          ResultSet rs = st.executeQuery("select child_id from project_l1 where project_l1_id="+pageId +" and volume_id=" + volumeId);
          if(rs.next()){
            child_id = rs.getInt(1);            
          }           
        }
        ResultSet rust = st.executeQuery("select distinct batch_id from child where child_id="+child_id);
        if(rust.next()){
          batch_id = rust.getInt(1);         
        }
        if(batchId <= 0){
            ResultSet pgid_tableresult = st.executeQuery("select * from project_l1 where project_l1_id = " +pageId);
            if(pgid_tableresult.next()){
                level = "L1";
            }else{
                level = "L2";
            }
        }
              
        ResultSet resultSet = st.executeQuery("select batch_id,treatment_level from batch where batch_id="+batch_id);
        if(resultSet.next()){
          level = resultSet.getString(2);         
        }
        
        ResultSet rs;
        if(level.equals("L1")){
            rs = st.executeQuery("SELECT P.seq, B.batch_id, P.bates_number, sum(case when (P2.seq <= P.seq) then 1 else 0 end) as 'sum(P2.seq <= P.seq)', count(*)       , V.lft, V.rgt, B.lft, B.rgt       , R.lft, R.rgt, C.lft, C.rgt       , P.boundary_flag, P.path, P.filename, P.child_id       , V.volume_id, V.volume_name, V.image_path       , P.offset, P.group_one_path, P.group_one_filename       , P.document_number ,V.image_server FROM project_l1 P      inner join child C on C.child_id = P.child_id      inner join range R on R.range_id = C.range_id      inner join batch B on C.batch_id=B.batch_id      inner join volume V on V.volume_id = C.volume_id      inner join project_l1 P2 on P2.child_id = C.child_id WHERE P.project_l1_id = "+pageId+"  GROUP BY P.project_l1_id,P.seq,B.batch_id, P.bates_number,V.lft,V.rgt,B.lft,B.rgt,R.lft,          R.rgt,C.lft,C.rgt,P.boundary_flag,P.path,P.filename,P.child_id,V.volume_id,          V.volume_name,V.image_path,P.offset,P.group_one_path,P.group_one_filename       , P.document_number,V.image_server");
        }else{
            rs = st.executeQuery("SELECT P.seq, B.batch_id, P.bates_number, sum(case when (P2.seq <= P.seq) then 1 else 0 end) as 'sum(P2.seq <= P.seq)', count(*)       , V.lft, V.rgt, B.lft, B.rgt       , R.lft, R.rgt, C.lft, C.rgt       , P.boundary_flag, P.path, P.filename, P.child_id       , V.volume_id, V.volume_name, V.image_path       , P.offset, P.group_one_path, P.group_one_filename       , P.document_number ,V.image_server FROM page P      inner join child C on C.child_id = P.child_id      inner join range R on R.range_id = C.range_id      inner join batch B on C.batch_id=B.batch_id      inner join volume V on V.volume_id = C.volume_id      inner join page P2 on P2.child_id = C.child_id WHERE P.page_id ="+pageId+"  GROUP BY P.page_id,P.seq,B.batch_id, P.bates_number,V.lft,V.rgt,B.lft,B.rgt,R.lft,          R.rgt,C.lft,C.rgt,P.boundary_flag,P.path,P.filename,P.child_id,V.volume_id,          V.volume_name,V.image_path,P.offset,P.group_one_path,P.group_one_filename       , P.document_number,V.image_server");
        }
        if (! rs.next()) {
            Log.quit("collectImageData: no rows in result");
        }
        storeImageData(pageId, data, rs);
        rs.close();        
        return data;
    }

    /**
     * Fill in the given ImageData instance with the given pageId and
     * ResultSet columns.
     * @param pageId the page.page_id that keys the given ResultSet
     * @param data an instance of ImageData that will be returned to the caller
     * @param rs a ResultSet containing the ImageData information
     */
    protected void storeImageData(int pageId, ImageData data, ResultSet rs)
    throws SQLException {
        data.pageId = pageId;
        int seq = rs.getInt(1);
        data.batchId = rs.getInt(2);
        data.batesNumber = rs.getString(3);
        data.childImagePosition = rs.getInt(4);
        data.childImageCount = rs.getInt(5);
        int vlft = rs.getInt(6);
        int vrgt = rs.getInt(7);
        int blft = rs.getInt(8);
        int brgt = rs.getInt(9);
        int rlft = rs.getInt(10);
        int rrgt = rs.getInt(11);
        int clft = rs.getInt(12);
        int crgt = rs.getInt(13);
        if (clft == blft) { data.boundaryInfo |= FIRST_CHILD_OF_BATCH; }
        if (crgt == brgt) { data.boundaryInfo |=  LAST_CHILD_OF_BATCH; }
        if (seq == blft)  { data.boundaryInfo |= FIRST_PAGE_OF_BATCH; }
        if (seq == brgt)  { data.boundaryInfo |=  LAST_PAGE_OF_BATCH; }
        if (rlft == vlft) { data.boundaryInfo |= FIRST_RANGE_OF_VOLUME; }
        if (rrgt == vrgt) { data.boundaryInfo |=  LAST_RANGE_OF_VOLUME; }
        if (clft == vlft) { data.boundaryInfo |= FIRST_CHILD_OF_VOLUME; }
        if (crgt == vrgt) { data.boundaryInfo |=  LAST_CHILD_OF_VOLUME; }
        if (seq == vlft)  { data.boundaryInfo |= FIRST_PAGE_OF_VOLUME; }
        if (seq == vrgt)  { data.boundaryInfo |=  LAST_PAGE_OF_VOLUME; }
        if (seq == clft)  { data.boundaryInfo |= FIRST_PAGE_OF_CHILD; }
        if (seq == crgt)  { data.boundaryInfo |=  LAST_PAGE_OF_CHILD; }
        data.boundaryFlag = rs.getString(14);  // TBD: move to coding?
        data.path = rs.getString(15);
        data.filename = rs.getString(16);
        data.childId = rs.getInt(17);
        data.volumeId = rs.getInt(18);
        data.volumeName = rs.getString(19);
        data.imagePath = rs.getString(20);
        data.offset = rs.getInt(21);
        data.groupOnePath = rs.getString(22);
        data.groupOneFilename = rs.getString(23);
        data.documentNumber = rs.getString(24);
        data.serverIP_port = rs.getString(25);
        
    }

    /**
     * Gather image, type and positional data on the given page, as well
     * as its associated bates.
     * @param pageId the page.page_id of the child whose data is
     * being requested
     * @return an instance of <code>common.CodingData</code> whose fields
     * contain the data for the given <code>pageId</code>
     */
    public CodingData collectCodingData(int [] possition) throws SQLException {       
        int child_id = 0;
        String level = "";
        int batch_id = 0;
        int [] pos = possition;
       
        CodingData data = new CodingData();
        ResultSet rst = st.executeQuery("select child_id from page where page_id="+pos[0] +" and volume_id=" + volumeId +" and child_id ="+pos[1]);
        if(rst.next()){
            child_id = rst.getInt(1);
        }else{
          ResultSet rs = st.executeQuery("select child_id from project_l1 where project_l1_id="+pos[0] +" and volume_id=" + volumeId +" and child_id ="+pos[1]);
          if(rs.next()){
            child_id = rs.getInt(1);   
          }           
        }
        ResultSet rust = st.executeQuery("select distinct batch_id from child where child_id="+child_id);
        if(rust.next()){
          batch_id = rust.getInt(1);      
        }
        
        if(batch_id <= 0){
            ResultSet pgid_tableresult = st.executeQuery("select * from project_l1 where child_id = " +pos[1]);
            if (pgid_tableresult.next()){
                level = "L1";
            }else{
                level = "L2";
            }
        }
        ResultSet resultSet = st.executeQuery("select batch_id,treatment_level from batch where batch_id="+batch_id);
        if(resultSet.next()){
          level = resultSet.getString(2);
        }
        ResultSet rs;
      if(level.equals("L1")) {   
             rs = st.executeQuery("SELECT P.seq, B.batch_id, P.bates_number, sum(case when (P2.seq <= P.seq) then 1 else 0 end) as 'sum(P2.seq <= P.seq)', count(*), V.lft, V.rgt, B.lft, B.rgt, R.lft, R.rgt, C.lft, C.rgt, P.boundary_flag, P.path, P.filename, P.child_id, V.volume_id, V.volume_name, V.image_path, P.offset, FCP.bates_number, LCP.bates_number, FRP.bates_number,  LRP.bates_number, B.batch_number, C.is_split , P.group_one_path, P.group_one_filename ,  P.document_number, B.active_group, P.query_raised, B.treatment_level, V.image_server,C.marking FROM project_l1 P inner join child C  on C.child_id = P.child_id  inner join range R  on R.range_id = C.range_id  inner join batch B  on C.batch_id=B.batch_id  inner join volume V on V.volume_id = B.volume_id  inner join project_l1 FCP on FCP.volume_id=C.volume_id and FCP.seq=C.lft inner join project_l1 LCP on LCP.volume_id=C.volume_id     and LCP.seq=C.rgt inner join project_l1 FRP on FRP.volume_id=R.volume_id        and FRP.seq=R.lft  inner join project_l1 LRP on LRP.volume_id=R.volume_id       and LRP.seq=R.rgt  inner join project_l1 P2 on P2.child_id = C.child_id  WHERE P.project_l1_id = "+pos[0]+"  GROUP BY P.project_l1_id,P.seq,B.batch_id, P.bates_number,V.lft, V.rgt, B.lft, B.rgt,  R.lft, R.rgt, C.lft, C.rgt,P.boundary_flag, P.path, P.filename, P.child_id, V.volume_id, V.volume_name, V.image_path, P.offset,FCP.bates_number,  LCP.bates_number, FRP.bates_number, LRP.bates_number, B.batch_number, C.is_split, P.group_one_path, P.group_one_filename, P.document_number, B.active_group, P.query_raised, B.treatment_level,V.image_server,C.marking");
      }else{
             rs = st.executeQuery("SELECT P.seq, B.batch_id, P.bates_number, sum(case when (P2.seq <= P.seq) then 1 else 0 end) as 'sum(P2.seq <= P.seq)', count(*), V.lft, V.rgt, B.lft, B.rgt, R.lft, R.rgt, C.lft, C.rgt, P.boundary_flag, P.path, P.filename, P.child_id, V.volume_id, V.volume_name, V.image_path, P.offset, FCP.bates_number, LCP.bates_number, FRP.bates_number,  LRP.bates_number, B.batch_number, C.is_split , P.group_one_path, P.group_one_filename ,  P.document_number, B.active_group, P.query_raised, B.treatment_level,V.image_server,C.marking  FROM page P inner join child C  on C.child_id = P.child_id  inner join range R  on R.range_id = C.range_id  inner join batch B  on C.batch_id=B.batch_id  inner join volume V on V.volume_id = B.volume_id  inner join page FCP on FCP.volume_id=C.volume_id and FCP.seq=C.lft inner join page LCP on LCP.volume_id=C.volume_id     and LCP.seq=C.rgt inner join page FRP on FRP.volume_id=R.volume_id        and FRP.seq=R.lft  inner join page LRP on LRP.volume_id=R.volume_id       and LRP.seq=R.rgt  inner join page P2 on P2.child_id = C.child_id  WHERE P.page_id = "+pos[0]+"  GROUP BY P.page_id,P.seq,B.batch_id, P.bates_number,V.lft, V.rgt, B.lft, B.rgt,  R.lft, R.rgt, C.lft, C.rgt,P.boundary_flag, P.path, P.filename, P.child_id, V.volume_id, V.volume_name, V.image_path, P.offset,FCP.bates_number,  LCP.bates_number, FRP.bates_number, LRP.bates_number, B.batch_number, C.is_split, P.group_one_path, P.group_one_filename, P.document_number, B.active_group, P.query_raised, B.treatment_level,V.image_server,C.marking");
      }	        
        if (! rs.next()) {
            Log.quit("collectCodingData: no rows in result");
        }
        storeImageData(pos[0], data, rs);
        data.currentBatesOfChild = rs.getString(3);
        data.firstBatesOfChild = rs.getString(22);
        data.lastBatesOfChild = rs.getString(23);
        data.firstBatesOfRange = rs.getString(24);
        data.lastBatesOfRange = rs.getString(25);
        data.batchNumber = rs.getInt(26);
        data.isSplit = rs.getBoolean(27);
        data.groupOnePath = rs.getString(28);
        data.groupOneFilename = rs.getString(29);
        data.documentNumber = rs.getString(30);
        data.activeGroup = rs.getInt(31);
        data.query_raised =rs.getString(32);
        data.treatment_level =rs.getString(33);
        data.serverIP_port =rs.getString(34);
        data.listing_marking =rs.getString(35);
        int seq = rs.getInt(1);
        int blft = rs.getInt(8);
        int brgt = rs.getInt(9);
        rs.close();

        rs = st.executeQuery("SELECT sum(case when (lft <= "+seq+") then 1 else 0 end), count(*) FROM child WHERE volume_id = "+volumeId+"  and lft between "+blft+" and "+brgt);              
        if (! rs.next()) {
            Log.quit("collectCodingData: no rows in result #2");
        }
        data.batchChildPosition = rs.getInt(1);
        data.batchChildCount = rs.getInt(2);
        rs.close();
        return data;
    }
    
    
    public CodingData collectCodingData(int pageId,int volumeid,int batchid) throws SQLException {        
        long startTime = System.currentTimeMillis();
        CodingData data = new CodingData();
        ResultSet rs = st.executeQuery("SELECT P.seq, B.batch_id, P.bates_number, sum(case when (P2.seq <= P.seq) then 1 else 0 end) as 'sum(P2.seq <= P.seq)', count(*), V.lft, V.rgt,  B.lft, B.rgt    , R.lft, R.rgt, C.lft, C.rgt, P.boundary_flag, P.path,  P.filename, P.child_id    , V.volume_id, V.volume_name, V.image_path, P.offset,  FCP.bates_number, LCP.bates_number    , FRP.bates_number, LRP.bates_number,  B.batch_number, C.is_split    , P.group_one_path, P.group_one_filename , P.document_number, B.active_group, P.query_raised  , V.image_server,C.marking FROM page P inner join child C on C.child_id = P.child_id  inner join range R on R.range_id = C.range_id inner join batch B on C.batch_id=B.batch_id  inner join volume V on V.volume_id = B.volume_id inner join page FCP on FCP.volume_id=C.volume_id and FCP.seq=C.lft inner join page LCP on LCP.volume_id=C.volume_id and LCP.seq=C.rgt  inner join page FRP on FRP.volume_id=R.volume_id and FRP.seq=R.lft inner join page LRP on LRP.volume_id=R.volume_id and LRP.seq=R.rgt inner join page P2 on P2.child_id = C.child_id WHERE P.page_id="+pageId+" GROUP BY P.page_id,P.seq,B.batch_id, P.bates_number,V.lft, V.rgt, B.lft, B.rgt, R.lft, R.rgt, C.lft, C.rgt,P.boundary_flag, P.path, P.filename, P.child_id,V.volume_id, V.volume_name, V.image_path, P.offset,FCP.bates_number, LCP.bates_number,FRP.bates_number, LRP.bates_number, B.batch_number, C.is_split,P.group_one_path, P.group_one_filename, P.document_number, B.active_group,P.query_raised,V.image_server,C.marking");

        if (! rs.next()) {
            Log.quit("collectCodingData: no rows in result");
        }
        storeImageData(pageId, data, rs);
        data.currentBatesOfChild = rs.getString(3);
        data.firstBatesOfChild = rs.getString(22);
        data.lastBatesOfChild = rs.getString(23);
        data.firstBatesOfRange = rs.getString(24);
        data.lastBatesOfRange = rs.getString(25);
        data.batchNumber = rs.getInt(26);
        data.isSplit = rs.getBoolean(27);
        data.groupOnePath = rs.getString(28);
        data.groupOneFilename = rs.getString(29);
        data.documentNumber = rs.getString(30);
        data.activeGroup = rs.getInt(31);
        data.query_raised =rs.getString(32);
        data.serverIP_port =rs.getString(33);
        data.listing_marking = rs.getString(34);
        int seq = rs.getInt(1);
        int blft = rs.getInt(8);
        int brgt = rs.getInt(9);
        rs.close();
        
        rs = st.executeQuery("SELECT sum(case when (lft <= "+seq+") then 1 else 0 end), count(*) FROM child WHERE volume_id = "+volumeid+"  and lft between "+blft+" and " +brgt);        
        if (! rs.next()) {
            Log.quit("collectCodingData: no rows in result #2");
        }
        data.batchChildPosition = rs.getInt(1);
        data.batchChildCount = rs.getInt(2);
        rs.close();
        return data;
    }   
    
    public Map collectErrorFlagData(int childId)
    throws SQLException {
       
        Map result = null;
        ResultSet rs = st.executeQuery(SQLQueries.SEL_MSHALL_FNAME +childId);        
        while (rs.next()) {
            if (result == null) {
                result = new HashMap();
            }
            String fieldName = rs.getString(1);
            boolean coderError = rs.getBoolean(2);
            result.put(fieldName, (coderError ? "Yes" : "No"));
        }
        rs.close();
        return result;
    }
    
    public Map collectErrorTypeData(int childId)
    throws SQLException {
        Map result = null;
        
        ResultSet rs = st.executeQuery("select field_name,error_type from codinghistory where event = 'QA' and child_id = "+childId);      
        
        while (rs.next()) {
            if (result == null) {
                result = new HashMap();
            }
            String fieldName = rs.getString(1);
            String errorType = rs.getString(2);
            result.put(fieldName,errorType);
        }
        rs.close();
        return result;
    }
}

