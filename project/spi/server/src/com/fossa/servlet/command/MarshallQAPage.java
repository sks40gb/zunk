/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.fossa.servlet.command;

import com.fossa.servlet.common.CodingData;
import com.fossa.servlet.common.ImageData;
import com.fossa.servlet.common.Log;
import com.fossa.servlet.common.SQLQueries;
import com.fossa.servlet.dao.DBTask;
import com.fossa.servlet.session.UserTask;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Given parameters, find the request page in the database for
 * a QA user.
 * @see MarshallPage
 * @author bala
 */
public class MarshallQAPage extends MarshallPage {

    int batchId = -1; //// STUB

    /**
     * Create a new MarshallQAPage.  Used only by
     * MarshallPage.makeInstance.
     */
    MarshallQAPage(UserTask task, DBTask dbTask)
    throws SQLException {
        super(task, dbTask, "QA");
    }

    /**
     * Return batch determined for this MarshallPage.
     * There should be no batch for QA.
     * TBD: Should we return zero here?
     */
  
    public int getBatchId() {
         return batchId;
    }

    /**
     * Find page, given relative position of child in selected children.
     */
   //modified to suit for L1,int return type is converted into int []
    public int [] findPositionInBatch(int delta, int boundary) throws SQLException {                
        int [] result = new int [2];
        String level ="";        
        ResultSet rst = st.executeQuery("select batch_id,treatment_level from batch where batch_id="+batchId);
        if(rst.next()){
          level = rst.getString(2);        
        }
        
        assert delta != 0;  // delta == 0 handled by caller
        assert boundary == B_CHILD; // only one for QA
        String sql="";        
        if(level.equals("L1")){            
        sql = "Declare @delta int set @delta = "+delta 
            +"	declare @string varchar(5000) set @string = ''"
            +"	if(@delta > 0) begin Set @string = 'select top 1 obj.*"
            +"	from (select top '+ Convert(varchar(10),@delta)  +' "
            +"	P.project_l1_id, P.child_id from project_l1 P"
            +"	inner join child C "
            +"	on P.volume_id=C.volume_id and P.seq=C.lft"
            +"	inner join childcoded CC on CC.child_id = C.child_id"
            +"	where C.volume_id = "+volumeId
            +"	and CC.round = 0 and CC.status= ''QA''"
            +"	order by C.lft) obj"
            +"	order by page_id asc'"
            +"	end else begin"
            +"	Set @string = 'select top 1 P.project_l1_id, P.child_id "
            +"	from project_l1 P inner join child C "
            +"	on P.volume_id=C.volume_id and P.seq=C.lft"
            +"	inner join childcoded CC on CC.child_id = C.child_id"
            +"	where C.volume_id = "+volumeId
            +"	and CC.round = 0 and CC.status= ''QA''"
            +"  order by C.rgt desc'"
            +"  end  exec(@string)";
        }else{
          sql = "Declare @delta int set @delta = "+delta 
            +"	declare @string varchar(5000) set @string = ''"
            +"	if(@delta > 0) begin Set @string = 'select top 1 obj.*"
            +"	from (select top '+ Convert(varchar(10),@delta)  +' "
            +"	P.page_id,P.child_id from page P"
            +"	inner join child C "
            +"	on P.volume_id=C.volume_id and P.seq=C.lft"
            +"	inner join childcoded CC on CC.child_id = C.child_id"
            +"	where C.volume_id = "+volumeId
            +"	and CC.round = 0 and CC.status= ''QA''"
            +"	order by C.lft) obj"
            +"	order by page_id asc'"
            +"	end else begin"
            +"	Set @string = 'select top 1 P.page_id,P.child_id "
            +"	from page P inner join child C "
            +"	on P.volume_id=C.volume_id and P.seq=C.lft"
            +"	inner join childcoded CC on CC.child_id = C.child_id"
            +"	where C.volume_id = "+volumeId
            +"	and CC.round = 0 and CC.status= ''QA''"
            +"  order by C.rgt desc'"
            +"  end  exec(@string)";
        }
        ResultSet rs = st.executeQuery(sql);
        if (rs.next()) {
            result[0] = rs.getInt(1);
            result[1] = rs.getInt(2);
        }
        rs.close();        
        return result;
    }


    /**
     * Find the first page of first selected child, that has not been QAed.
     * @return The page_id of the page; 0 if the page was not found.
     */
    public int findUncoded() throws SQLException {                
        PreparedStatement pst = dbTask.getConnection().prepareStatement(SQLQueries.SEL_MSHALLQA_PGID);
        pst.setInt(1, volumeId);
        
        ResultSet rs = pst.executeQuery();
        int result = 0;
        if (rs.next()) {
            result = rs.getInt(1);
        }
        pst.close();        
        return result;
    }

    //public int findRelativeInBatch(int pageId, int delta, int boundary, boolean findEnd)
    public int [] findRelativeInBatch(int pageId, int delta, int boundary)
    throws SQLException {  
        System.out.println("------------------------- INSIDE findRelativeInBatch OF " + this.getClass().getName());
        long startTime = System.currentTimeMillis();
        boolean uncoded = false;
        if (boundary == B_UNCODED) {
            boundary = B_CHILD;
            uncoded = true;
        }
        String level =""; 
        if(batchId <= 0){
            ResultSet pgid_tableresult = st.executeQuery("select * from project_l1 where project_l1_id = " +pageId);
            if(pgid_tableresult.next()){
                level = "L1";
            }else{
                level = "L2";
            }
        }
        
        ResultSet rst = st.executeQuery("select batch_id,treatment_level from batch where batch_id="+batchId);
        if(rst.next()){
          level = rst.getString(2);        
        }
        String sql;
        switch (boundary) {
        case B_NONE:
            assert(! uncoded);
            if (delta == 0) {
	if(level.equals("L1")){
		sql = "SELECT P.project_l1_id,P.child_id FROM project_l1 P   " +
                        " inner join childcoded CC on CC.child_id = P.child_id " +
                        " WHERE P.project_l1_id ="+pageId;
	}else{
		sql = "SELECT P.page_id,P.child_id FROM page P   inner join childcoded CC " +
                        " on CC.child_id = P.child_id WHERE P.page_id ="+pageId;
	}
                
                
            } else { // since delta != 0

  if(level.equals("L1")){              
          sql = "Declare @delta int set @delta = "+delta+"  declare @string varchar(5000) " +
                " set @string = '' if(@delta > 0) begin  Set @string = 'select top 1 obj.*    " +
                " from (select top '+  Convert(varchar(10),@delta)  +' P2.project_l1_id,P2.child_id " +
                " from project_l1 P, project_l1 P2     inner join childcoded CC on P2.child_id = CC.child_id    " +
                " where P2.volume_id = P.volume_id     and P2.seq > P.seq     and P.project_l1_id="+pageId+"    " +
                " and CC.round = 0     and CC.status=''QA''    order by P2.seq)obj  " +
                " order by project_l1_id asc' end else  begin  Set @string = 'select top 1 P.project_l1_id, P.child_id     " +
                " from project_l1 P, project_l1 P2 inner join childcoded CC on P2.child_id = CC.child_id " +
                " where P2.volume_id = P.volume_id and P2.seq < P.seq and P.project_l1_id="+pageId+
                " order by P2.seq desc'  end exec (@string)";
	}else{
            sql = "Declare @delta int set @delta = "+delta+"  declare @string varchar(5000) " +
                    " set @string = '' if(@delta > 0) begin  Set @string = 'select top 1 obj.*   " +
                    "  from (select top '+  Convert(varchar(10),@delta)  +' P2.page_id, P2.child_id " +
                    " from page P, page P2     inner join childcoded CC on P2.child_id = CC.child_id    " +
                    " where P2.volume_id = P.volume_id  and P2.seq > P.seq and P.page_id="+pageId+" " +
                    " order by P2.seq)obj " +
                    " order by page_id asc' end else  begin  Set @string = 'select top 1 P.page_id, P.child_id " +
                    " from page P, page P2 inner join childcoded CC on P2.child_id = CC.child_id  " +
                    " where P2.volume_id = P.volume_id and P2.seq < P.seq and P.page_id="+pageId+" " +
                    " order by P2.seq desc'  end exec (@string)";
        }
            }
            break;
        case B_CHILD:
            System.out.println("Inside B_CHILD");
            assert(delta > 0 || ! uncoded);  
            System.out.println("uncoded-------------->" +uncoded);
            System.out.println("pageId-------------->" +pageId);
            System.out.println("delta-------------->" +delta);
 	if(level.equals("L1")){
            sql = "Declare @delta int set @delta = "+delta+"  Declare @uncoded as varchar(7) " +
                    " set @uncoded= '"+uncoded+"' declare @string varchar(5000) set @string = '' if(@delta > 0) " +
                    " begin  if @uncoded= 'True'  begin   Set @string = 'select top 1 obj.*      " +
                    " from (select top '+  Convert(varchar(10),@delta)  +' P2.project_l1_id,P2.child_id, " +
                    " from project_l1 P, child C, project_l1 P2 inner join childcoded CC on P2.child_id = CC.child_id      " +
                    " where C.volume_id = P.volume_id and C.lft > P.seq and P.page_id= "+pageId+
                    " and P.volume_id=C.volume_id and P2.seq=C.lft and CC.users_id = 0     " +
                    " order by C.rgt)obj order by project_l1_id asc ' end else begin  Set @string = 'select top 1 obj.*    " +
                    " from (select top '+  Convert(varchar(10),@delta)  +' P2.project_l1_id,P2.child_id        " +
                    " from project_l1 P, child C, project_l1 P2 inner join childcoded CC on P2.child_id = CC.child_id  " +
                    " where C.volume_id = P.volume_id and C.lft > P.seq and P.page_id= "+pageId+
                    " and P.volume_id=C.volume_id  and P2.seq=C.lft" +
                    " order by C.rgt)obj order by project_l1_id asc ' end end else begin  if @uncoded= 'True' begin  " +
                    " Set @string = 'select top 1 P.project_l1_id,P.child_id from project_l1 P, child C, project_l1 P2 " +
                    " inner join childcoded CC on P2.child_id = CC.child_id where C.volume_id = P.volume_id and C.lft <= P.seq " +
                    " and P.page_id= "+pageId+" and P.volume_id=C.volume_id and P2.seq=C.lft " +
                    " and CC.users_id = 0 order by C.lft desc' end  else begin  " +
                    " Set @string = 'select top 1 P.project_l1_id, P.child_id from project_l1 P, child C, project_l1 P2 " +
                    " inner join childcoded CC on P2.child_id = CC.child_id where C.volume_id = P.volume_id  and C.lft <= P.seq " +
                    " and P.page_id= "+pageId+" and P.volume_id=C.volume_id and P2.seq=C.lft " +
                    " order by C.lft desc' end  end exec(@string) ";
	}else{
                
            sql = "Declare @delta int set @delta = "+delta+"  " +
                    " Declare @uncoded as varchar(7) " +
                    " set @uncoded= '"+uncoded+"' " +
                    " declare @string varchar(5000) set @string = '' " +
                    " if(@delta > 0) begin  if @uncoded= 'True'   begin   " +
                    " Set @string = 'select top 1 obj.*      " +
                    " from (select top '+  Convert(varchar(10),@delta)  +' P2.page_id,P2.child_id  " +
                    " from page P, child C, page P2 inner join childcoded CC on P2.child_id = CC.child_id " +
                    " where C.volume_id = P.volume_id and C.lft > P.seq and P.page_id= "+pageId+
                    " and P.volume_id=C.volume_id and P2.seq=C.lft" +
                    " and CC.users_id = 0 order by C.rgt)" +
                    " obj order by page_id asc '  " +
                    " end else begin  " +
                    " Set @string = 'select top 1 obj.* " +
                    " from (select top '+  Convert(varchar(10),@delta)  +' P2.page_id,P2.child_id " +
                    " from page P, child C, page P2 inner join childcoded CC on P2.child_id = CC.child_id  " +
                    " where C.volume_id = P.volume_id  and C.lft > P.seq   and P.page_id= "+pageId+
                    " and P.volume_id=C.volume_id and P2.seq=C.lft" +
                    " order by C.rgt) obj " +
                    " order by page_id asc ' end " +
                    " end else begin  if @uncoded= 'True' " +
                    " begin  Set @string = 'select top 1 P.page_id,P.child_id from page P, child C, page P2 " +
                    " inner join childcoded CC on P2.child_id = CC.child_id where C.volume_id = P.volume_id and C.lft <= P.seq " +
                    " and P.page_id= "+pageId+"  and P.volume_id=C.volume_id and P2.seq=C.lft" +
                    " and CC.users_id = 0 order by C.lft desc' end  else begin   " +
                    " Set @string = 'select top 1 P.page_id,P.child_id  " +
                    " from page P, child C, page P2 inner join childcoded CC on P2.child_id = CC.child_id  " +
                    " where C.volume_id = P.volume_id and C.lft <= P.seq and P.page_id= "+pageId+" and P.volume_id=C.volume_id " +
                    " and P2.seq=C.lft" +
                    " order by C.lft desc' end  end exec(@string) ";
            
            System.out.println("sql-------------->" +sql);
	}
            break;
            case B_RANGE: 
            
            if(level.equals("L1")){
                               
            sql = " Declare @delta int  set @delta = "+delta+"  " +
                    " Declare @FindEnd as varchar(7) set @FindEnd =  '"+uncoded+"' " +
                    " declare @string varchar(5000) set @string = '' " +
                    " if(@delta > 0) begin  if @FindEnd =  'True'  begin   " +
                    " Set @string = 'select top 1 obj.*" +
                    " from (select top 1 P2.project_l1_id, P2.child_id " +
                    " from project_l1 P, range R, project_l1 P2 where R.volume_id = P.volume_id " +
                    " and R.rgt >= P.seq and P.project_l1_id="+pageId+
                    " and P2.volume_id=R.volume_id and P2.seq= R.rgt order by R.rgt)" +
                    " obj order by project_l1_id asc'  end  else  Begin   " +
                    " Set @string = 'select top 1 obj.*" +
                    " from (select top 1 P2.project_l1_id,P2.child_id " +
                    " from project_l1 P, range R, project_l1 P2 where R.volume_id = P.volume_id " +
                    " and R.rgt >= P.seq and P.project_l1_id="+pageId+
                    " and P2.volume_id=R.volume_id and P2.seq= R.lft order by R.rgt) " +
                    " obj order by project_l1_id asc' end end else begin" +
                    " if @FindEnd =  'True'  begin   Set @string = 'select top 1         " +
                    " P2.project_l1_id,P2.child_id from project_l1 P, range R, project_l1 P2 where R.volume_id = P.volume_id" +
                    " and R.lft <= P.seq and P.project_l1_id="+pageId+
                    " and P2.volume_id=R.volume_id and P2.seq = R.rgt order by R.lft desc'" +
                    " end   else  Begin   Set @string = 'select top 1 P2.project_l1_id,P2.child_id" +
                    " from project_l1 P, range R, project_l1 P2 where R.volume_id = P.volume_id         " +
                    " and R.lft <= P.seq and P.project_l1_id="+pageId+
                    " and P2.volume_id=R.volume_id  and P2.seq = R.lft" +
                    " order by R.lft '   End end exec (@string)";
            }else{
              sql = "Declare @delta int  set @delta = "+delta+
                    " Declare @FindEnd as varchar(7) set @FindEnd =  '"+uncoded+"' " +
                    " declare @string varchar(5000) set @string = '' " +
                    " if(@delta > 0) begin  if @FindEnd =  'True'  begin   " +
                    " Set @string = 'select top 1 obj.*" +
                    " from (select top '+  Convert(varchar(10),@delta)  +' P2.page_id, P2.child_id " +
                    " from page P, range R, page P2 where R.volume_id = P.volume_id " +
                    " and R.rgt >= P.seq and P.page_id="+pageId+
                    " and P2.volume_id=R.volume_id and P2.seq= R.rgt order by R.rgt)" +
                    " obj order by page_id asc'  end  else  Begin   " +
                    " Set @string = 'select top 1 obj.*" +
                    " from (select top '+  Convert(varchar(10),@delta)  +' P2.page_id,P2.child_id" +
                    " from page P, range R, page P2 where R.volume_id = P.volume_id " +
                    " and R.rgt >= P.seq and P.page_id="+pageId+
                    " and P2.volume_id=R.volume_id and P2.seq= R.lft order by R.rgt) " +
                    " obj order by page_id asc' end end else begin  " +
                    " if @FindEnd =  'True'  begin   Set @string = 'select top 1" +
                    " P2.page_id,P2.child_id from page P, range R, page P2 where R.volume_id = P.volume_id" +
                    " and R.lft <= P.seq and P.page_id="+pageId+
                    " and P2.volume_id=R.volume_id and P2.seq = R.rgt order by R.lft desc'" +
                    " end   else  Begin   Set @string = 'select top 1 P2.page_id,P2.child_id" +
                    " from page P, range R, page P2 where R.volume_id = P.volume_id" +
                    " and R.lft <= P.seq and P.page_id="+pageId+
                    " and P2.volume_id=R.volume_id  and P2.seq = R.lft" +
                    " order by R.lft desc'   End end exec (@string)";  
            } 
        break;    
        default:
            Log.quit("MarshallQAPage: invalid boundary.findRelativeInBatch: "+boundary);
            sql = null;
        }
        int [] result = new int[2];
        ResultSet rs = st.executeQuery(sql);
        if (rs.next()) {
            result[0] = rs.getInt(1);
            result[1] = rs.getInt(2);
            System.out.println("result"+result[0]);
            System.out.println("result"+result[1]);
        }
        rs.close();
        return result;
    }

    //find absolute page
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
            "select page_id"
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
        int [] result = new int[2];
        result[0] = rs.getInt(1);
        result[1] = rs.getInt(2);
        rs.close();
        return result;
    }

    //Get the image data
    public ImageData collectImageData(int pageId) throws SQLException {        
        long startTime = System.currentTimeMillis();
        ImageData data = new ImageData();
        System.out.println("Inside collectImageData++++++++++++++" +pageId);
        PreparedStatement select_mshallqa_pseq =  task.prepareStatement(dbTask,SQLQueries.SEL_MSHALLQA_PSEQ);
        select_mshallqa_pseq.setInt(1, pageId);
        ResultSet rs = select_mshallqa_pseq.executeQuery();
        
        if (! rs.next()) {
            Log.quit("collectImageData: no rows in result");
        }
        storeImageData(pageId, data, rs);
        rs.close();        
        return data;
    }

    //Save the image data
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
        int rlft = rs.getInt(10);
        int rrgt = rs.getInt(11);
        int clft = rs.getInt(12);
        int crgt = rs.getInt(13);        
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

    //Get the coding data
    public CodingData collectCodingData(int pageId) throws SQLException {
        System.out.println("collectCodingData #######################");
        System.out.println("pageId #######################" +pageId);
        CodingData data = new CodingData();
        String level = null;
        ResultSet rs = null;
        if(batchId <= 0){
            ResultSet pgid_tableresult = st.executeQuery("select * from project_l1 where project_l1_id = " +pageId);
            if(pgid_tableresult.next()){
                level = "L1";
            }else{
                level = "L2";
            }
        }
        
        
        if(level.equals("L1")){
            System.out.println(" collectCodingData L1");
            rs = st.executeQuery("SELECT P.seq, 0, P.bates_number, sum(case when (P2.seq <= P.seq) then 1 else 0 end), count(*)  , V.lft, V.rgt, 0, 1999999999       , R.lft, R.rgt, C.lft, C.rgt  , P.boundary_flag, P.path , P.filename, P.child_id  , V.volume_id, V.volume_name, V.image_path  , P.offset  , FCP.bates_number, LCP.bates_number  , FRP.bates_number, LRP.bates_number  , 0 , C.is_split  , P.group_one_path,  P.group_one_filename  , P.document_number, B.active_group , V.image_server FROM project_l1 P  inner join child C on C.child_id = P.child_id     inner join range R on R.range_id = C.range_id  inner join batch B on C.batch_id=B.batch_id     inner join volume V on V.volume_id = B.volume_id  inner join project_l1 FCP on FCP.volume_id=C.volume_id and FCP.seq=C.lft  inner join project_l1 LCP on LCP.volume_id=C.volume_id and LCP.seq=C.rgt  inner join project_l1 FRP on FRP.volume_id=R.volume_id and FRP.seq=R.lft  inner join project_l1 LRP on LRP.volume_id=R.volume_id and LRP.seq=R.rgt inner join project_l1 P2 on P2.child_id = C.child_id  WHERE P.project_l1_id = "+pageId+" GROUP BY P.page_id,P.seq,P.bates_number, V.lft, V.rgt,R.lft, R.rgt, C.lft, C.rgt,P.boundary_flag, P.path,    P.filename, P.child_id, V.volume_id, V.volume_name, V.image_path ,P.offset,FCP.bates_number, LCP.bates_number,FRP.bates_number, LRP.bates_number,C.is_split,P.group_one_path,P.group_one_filename  , P.document_number, B.active_group, V.image_server ");
        } else{
            rs = st.executeQuery("SELECT P.seq, 0, P.bates_number, sum(case when (P2.seq <= P.seq) then 1 else 0 end)" +
                ", count(*)  , V.lft, V.rgt, 0, 1999999999       , R.lft, R.rgt, C.lft, C.rgt  , P.boundary_flag, P.path" +
                ", P.filename, P.child_id  , V.volume_id, V.volume_name, V.image_path  , P.offset  , FCP.bates_number," +
                " LCP.bates_number  , FRP.bates_number, LRP.bates_number  , 0 , C.is_split  , P.group_one_path, " +
                "P.group_one_filename  , P.document_number, B.active_group , V.image_server FROM page P " +
                "inner join child C on C.child_id = P.child_id    inner join range R on R.range_id = C.range_id " +
                "inner join batch B on C.batch_id=B.batch_id    inner join volume V on V.volume_id = B.volume_id " +
                "inner join page FCP on FCP.volume_id=C.volume_id and FCP.seq=C.lft inner join page LCP " +
                "on LCP.volume_id=C.volume_id and LCP.seq=C.rgt inner join page FRP on FRP.volume_id=R.volume_id " +
                "and FRP.seq=R.lft inner join page LRP on LRP.volume_id=R.volume_id and LRP.seq=R.rgt" +
                " inner join page P2 on P2.child_id = C.child_id WHERE P.page_id= "+pageId+"" +
                " GROUP BY P.page_id,P.seq,P.bates_number, V.lft, V.rgt,R.lft, R.rgt, C.lft, C.rgt,P.boundary_flag," +
                "P.path,    P.filename, P.child_id, V.volume_id, V.volume_name, V.image_path ,P.offset,FCP.bates_number," +
                " LCP.bates_number,FRP.bates_number, LRP.bates_number,C.is_split,P.group_one_path,P.group_one_filename " +
                ", P.document_number, B.active_group");
        }
        
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
        data.serverIP_port = rs.getString(32);
        int seq = rs.getInt(1);        
        int clft = rs.getInt(12);
        int crgt = rs.getInt(13);
        rs.close();

        // For QA batch boundaries, use first and last selected children        
        rs = st.executeQuery("SELECT sum(case when (lft <= "+seq+") then 1 else 0 end) , count(*) as 'count(*)', min(lft) as 'min(lft)' , max(rgt) as 'max(rgt)' FROM child C   inner join childcoded CC on CC.child_id = C.child_id WHERE volume_id= "+volumeId+"  and CC.status = 'QA'   and CC.round = 0");
        
        if (! rs.next()) {
            Log.quit("collectCodingData: no rows in result #2");
        }
        data.batchChildPosition = rs.getInt(1);
        data.batchChildCount = rs.getInt(2);
        int blft = rs.getInt(3);
        int brgt = rs.getInt(4);
        rs.close();

        if (clft == blft) { data.boundaryInfo |= FIRST_CHILD_OF_BATCH; }
        if (crgt == brgt) { data.boundaryInfo |=  LAST_CHILD_OF_BATCH; }
        if (seq == blft)  { data.boundaryInfo |= FIRST_PAGE_OF_BATCH; }
        if (seq == brgt)  { data.boundaryInfo |=  LAST_PAGE_OF_BATCH; }
        return data;
    }
}
