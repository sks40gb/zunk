/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.fossa.servlet.server;

import com.fossa.servlet.common.Log;
import com.fossa.servlet.common.MessageConstants;
import com.fossa.servlet.common.SQLQueries;
import com.fossa.servlet.dao.DBTask;
import com.fossa.servlet.exception.ServerFailException;
import com.fossa.servlet.session.UserTask;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.apache.log4j.Logger;

/**
 *
 * @author ashish
 */
public class BoundaryMapper implements MessageConstants{

    /**
     * Change boundary level for a page.  Adds or removes child and
     * range records as required from an unsplit document.
     */
   
    private static Logger logger = Logger.getLogger("com.fossa.servlet.server");
   
    public static void store(UserTask task, DBTask dbTask, int volumeId
                             , int pageId, String boundaryFlag,int batchId)
    throws SQLException {
        store(task,dbTask,volumeId,pageId,boundaryFlag,false,batchId);
    }

    /**
     * Change boundary level for a page.  Adds or removes child and
     * range records as required.  Allows specification that
     * this was called from split/unsplit document.
     * @param task the ServerTask for the client
     * @param volumeId the volume.volume_id containing the page
     * @param pageId the page.page_id of the page whose boundary is changing
     * @param boundaryFlag the new boundary, one of B_CHILD, B_NONE, B_RANGE
     * @param splitOk is it legal to change the boundary of a split document?
     */
    public static void store(UserTask task, DBTask dbTask, int volumeId
                             , int pageId, String boundaryFlag
                             , boolean splitOk,int batch_Id)
    throws SQLException {

        Statement st = null;
        ResultSet rs =  null;
        int child_id = 0;
        String level = "";
        int batch_id = 0;
        try{
            st = dbTask.getStatement();
            //Log.print("BoundaryMapper.store " + "'"+ boundaryFlag + "'");

        ResultSet resultSet = st.executeQuery("select batch_id,treatment_level from batch where batch_id="+batch_Id);
        if(resultSet.next()){
          level = resultSet.getString(2);         
        }
        
            int boundaryEnum = boundaryFlag.equals("C") ? B_CHILD
                             : boundaryFlag.equals("")  ? B_NONE
                             :                            B_RANGE;
          
            // Get information about boundaries
            // TBD: should be checking batch -- also, we don't always need all fields
            //for L1
            if(level.equals("L1")){            
	       rs = st.executeQuery("SELECT P.seq, C.child_id, C.lft, C.rgt, R.range_id, R.lft, R.rgt, C.batch_id," +
                       "  C.is_split FROM project_l1 P inner join child C on C.child_id = P.child_id " +
                       " inner join range R on R.range_id = C.range_id where P.project_l1_id= "+pageId+" " +
                       " and P.volume_id="+volumeId);	

            }else{
                
		rs = st.executeQuery("SELECT P.seq, C.child_id, C.lft, C.rgt, R.range_id, R.lft, R.rgt, C.batch_id," +
                        "  C.is_split FROM page P inner join child C on C.child_id = P.child_id inner join range R " +
                        " on R.range_id = C.range_id where P.page_id= "+pageId+" and P.volume_id="+volumeId);	
			
            }            
            if (! rs.next()) {
                Log.quit("page not found: "+pageId);
            }
            int seq  = rs.getInt(1);
            int childId  = rs.getInt(2);
            int childLft = rs.getInt(3);
            int childRgt = rs.getInt(4);
            int rangeId  = rs.getInt(5);
            int rangeLft = rs.getInt(6);
            int rangeRgt = rs.getInt(7);
            int batchId  = rs.getInt(8);
            boolean isSplit = rs.getBoolean(9);
            rs.close();

            if (isSplit && ! splitOk) {
                throw new ServerFailException(
                        "Cannot change boundary on split document.");
            }

            // Modify the data in page or project_l1
            if(level.equals("L1")){
              st.executeUpdate(
                "update project_l1"
                +" set boundary="+boundaryEnum
                +"   , boundary_flag='"+boundaryFlag+"'"
                +" where project_l1_id="+pageId);
            }else{
              st.executeUpdate(
                "update page"
                +" set boundary="+boundaryEnum
                +"   , boundary_flag='"+boundaryFlag+"'"
                +" where page_id="+pageId);
            }
            
            // Add the given child to the changes table, since this
            // may not happen below.
            // Note.  needed so child always changes when letter changed

            st.executeUpdate("INSERT INTO changes (table_nbr,id,age) VALUES("+Tables.child.getTableNumber()+","+childId+","+task.getUpdateAgeForUpdate(dbTask)+")");
            

            ////// Adjust the child and range structure above this page.
            // Note that nothing is done if change between letters within B_RANGE

            if (boundaryEnum < B_RANGE && rangeLft == seq) {
                // no longer start of range
                // expand range to the left

                // find the prior range

                ResultSet rs2 = st.executeQuery("SELECT TOP 1 range_id, lft FROM range WHERE volume_id="+volumeId+" and rgt < "+rangeLft+" ORDER BY rgt desc");
                

                if (! rs2.next()) {
                    Log.quit("prior range not found: "+rangeId);
                }
                int priorRangeId = rs2.getInt(1);
                int priorRangeLft = rs2.getInt(2);
                rs2.close();

                // note range is not managed
                st.executeUpdate(
                    "update range"
                    +" set rgt="+rangeRgt
                    +" where range_id="+priorRangeId);
                

                st.executeUpdate(SQLQueries.DEL_BMAP_RANGEID+rangeId);
                st.executeUpdate(
                    "update child"
                    +" set range_id="+priorRangeId
                    +" where range_id="+rangeId);
                

                rangeId = priorRangeId;
                rangeLft = priorRangeLft;
            }

            if (boundaryEnum < B_CHILD && childLft == seq) {
                // no longer start of child
                // expand child to the left

                ResultSet rs2 = st.executeQuery("SELECT TOP 1 child_id, lft FROM child WHERE volume_id="+volumeId+" and rgt < "+childLft+"  ORDER BY rgt desc");
                

                if (! rs2.next()) {
                    Log.quit("prior child not found: "+childId);
                }
                int priorChildId = rs2.getInt(1);
                int priorChildLft = rs2.getInt(2);
                rs2.close();

                task.executeUpdate(dbTask,
                    "update child"
                    +" set rgt="+childRgt
                    +" where child_id="+priorChildId);

                task.executeUpdate(dbTask,SQLQueries.DEL_BMAP_CHILD +childId);
                
                // Note. not managed
                //for L1
                if(level.equals("L1")){
                  st.executeUpdate(
                    "update project_l1"
                    +" set child_id="+priorChildId
                    +" where child_id="+childId);
                }else{
                  st.executeUpdate(
                    "update page"
                    +" set child_id="+priorChildId
                    +" where child_id="+childId);
                }
               
 
                st.executeUpdate(SQLQueries.DEL_BMAP_CHILDID +childId);
                st.executeUpdate(SQLQueries.DEL_BMAP_LVAL+childId);
                st.executeUpdate(SQLQueries.DEL_BMAP_NVAL+childId);

                childId = priorChildId;
                childLft = priorChildLft;
            }

            int priorSeq = 0;
            if (boundaryEnum >= B_CHILD && childLft != seq) {
                // new start of child
                // insert new child
                
                        
                ResultSet rs2 = null;        
                    if(level.equals("L1")){   
                        rs2 = st.executeQuery("SELECT TOP 1 P.seq FROM project_l1 P WHERE P.volume_id= "+volumeId+" and P.seq < "+seq+" ORDER BY P.seq desc");
                    }else{
                        rs2 = st.executeQuery("SELECT TOP 1 P.seq FROM page P WHERE P.volume_id= "+volumeId+" and P.seq < "+seq+" ORDER BY P.seq desc");
                    }
                
                
                if (! rs2.next()) {
                    Log.quit("prior page not found: "+pageId);
                }
                priorSeq = rs2.getInt(1);
                rs2.close();

                task.executeUpdate(dbTask,
                    "update child"
                    +" set rgt="+priorSeq
                    +" where child_id="+childId);
                

                task.executeUpdate(dbTask,"INSERT INTO child (volume_id,lft, rgt,range_id,batch_id) VALUES("+volumeId+","+seq+","+childRgt+","+rangeId+","+batchId+")");
                

                // Note. not managed

                if(level.equals("L1")){
                	st.executeUpdate("UPDATE project_l1 SET child_id= (SELECT TOP 1 child_id FROM child ORDER BY child_id DESC) WHERE child_id="+childId+" and seq >="+seq);
				}else{
					st.executeUpdate("UPDATE page SET child_id= (SELECT TOP 1 child_id FROM child ORDER BY child_id DESC) WHERE child_id="+childId+" and seq >="+seq);
				}                
            }

            if (boundaryEnum >= B_RANGE && rangeLft != seq) {
                // new start of range
                // insert new range to the left

                // only get priorSeq if we didn't already
                // OOPS!!!  we had this without the where .. volume id -- wbe 2005-02-13
                if (priorSeq == 0) {

                    ResultSet rs2 = null;
				if(level.equals("L1")){
                                    rs2 = st.executeQuery("SELECT TOP 1 P.seq FROM project_l1 P WHERE P.volume_id="+volumeId+" and P.seq < "+seq+" order by P.seq desc");
				}else{
                                    rs2 = st.executeQuery("SELECT TOP 1 P.seq FROM page P WHERE P.volume_id="+volumeId+" and P.seq < "+seq+" order by P.seq desc");
				}
                    

                    if (! rs2.next()) {
                        Log.quit("prior page not found: "+pageId);
                    }
                    priorSeq = rs2.getInt(1);
                    rs2.close();
                }

                // Note. range not managed
                st.executeUpdate(
                    "update range"
                    +" set rgt="+priorSeq
                    +" where range_id="+rangeId);

                st.executeUpdate("INSERT INTO range (volume_id,lft,rgt) VALUES("+volumeId+","+seq+","+rangeRgt+")");

                st.executeUpdate("UPDATE child SET range_id= (SELECT TOP 1 range_id FROM range ORDER BY range_id DESC) where range_id = "+rangeId+" and lft >="+seq);

            }
            
        } catch (SQLException sql) {
            logger.error("Exception in BoundaryMapper." + sql);
            StringWriter sw = new StringWriter();
            sql.printStackTrace(new PrintWriter(sw));
            logger.error(sw.toString());
        } catch (Exception exc) {
            logger.error("Exception in BoundaryMapper." + exc);
            StringWriter sw = new StringWriter();
            exc.printStackTrace(new PrintWriter(sw));
            logger.error(sw.toString());
        }
    }
}
