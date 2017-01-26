/* $Header: /home/common/cvsarea/ibase/dia/src/server/Handler_binder_update.java,v 1.8.6.1 2006/03/14 15:08:46 nancy Exp $ */

package server;

import common.CodingData;
import common.Log;
import common.msg.MessageConstants;
import common.msg.MessageWriter;
                         
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;
import org.w3c.dom.Element;

/**
 * Handler for binder_update message
 * Dispatch to add or remove from binder depending on the value
 * of the remove message attribute.
 */
final public class Handler_binder_update extends Handler implements MessageConstants {

    /**
     * This class cannot be instantiated.
     */
    public Handler_binder_update() {}

    public void run (ServerTask task, Element action)
    throws java.io.IOException, java.sql.SQLException {
        int givenPageId = Integer.parseInt(action.getAttribute(A_PAGE_ID));
        boolean remove = "YES".equals(action.getAttribute(A_REMOVE));
        assert givenPageId > 0;
        int givenVolumeId = task.getVolumeId();

        if (remove) {
            removeFromBinder(task, givenPageId, givenVolumeId);
        } else {
            addToBinder(task, givenPageId, givenVolumeId);
        }

        MessageWriter writer = task.getMessageWriter();
        writer.startElement(T_OK);
        writer.endElement();
    }


    private void addToBinder (ServerTask task, int givenPageId, int givenVolumeId)
    throws SQLException {
        Connection con=task.getConnection();
        Statement st=task.getStatement();
        ResultSet rs;

        // find the corresponding child, etc.
        rs = st.executeQuery(
            "select C.child_id, C.lft, C.rgt"
            +"    , V.project_id, V.sequence, V.image_path"
            +"    , J.project_name"
            +"    , V2.volume_id, V2.lft"
            +"    , B2.batch_id, R2.range_id"
            +" from page P"
            +"   inner join child C using (child_id)"
            +"   inner join volume V using (volume_id)"
            +"   inner join project J using (project_id)"
            +"   left join volume V2"
            +"     on V2.project_id = V.project_id and V2.sequence=-1"
            +"   left join batch B2 on B2.volume_id = V2.volume_id"
            +"   left join range R2 on R2.volume_id = V2.volume_id"
            +" where P.page_id="+givenPageId
            +"   and V.volume_id="+givenVolumeId
            +"   and V.sequence > 0");
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
            st.executeUpdate(
                "insert into volume"
                +" set volume_name='["+projectName+"]'"
                +"   , project_id="+projectId
                +"   , sequence=-1"
                +"   , image_path='*'");
            binderVolumeId = lastInsertId(st);

            st.executeUpdate(
                "insert into batch"
                +" set status=0"
                +"   , volume_id="+binderVolumeId);
            binderBatchId = lastInsertId(st);

            st.executeUpdate(
                "insert into range"
                +" set volume_id="+binderVolumeId);
            binderRangeId = lastInsertId(st);

            newBoundary = B_RANGE;

        } else {

            // binder exists, check that this doc. not in it
            rs = st.executeQuery(
                "select 0 from child"
                +" where volume_id="+binderVolumeId
                +"   and "+lft+" <= child.rgt"
                +"   and "+rgt+" >= child.lft"
                +" limit 1");
            if (rs.next()) {
                throw new ServerFailException(
                    "Pages of document already in binder.");
            }

            // adjust first page boundary, if inserting at beginning
            if (lft < binderLft) {
                newBoundary = B_RANGE;
                st.executeUpdate(
                    "update page"
                    +" set boundary='CHILD'"
                    +" where volume_id="+binderVolumeId
                    +"   and seq="+binderLft);
            } else {
                newBoundary = B_CHILD;
            }
        }

        // create a binder child (managed)
        // Note:  is_update is not copied
        task.executeUpdate(
            "insert into child"
            +" set volume_id="+binderVolumeId
            +"   , batch_id="+binderBatchId
            +"   , range_id="+binderRangeId
            +"   , lft="+lft
            +"   , rgt="+rgt);
        int newChildId = lastInsertId(st);

        // copy the pages and image files to the database

        PreparedStatement psPage = con.prepareStatement(
            "insert into page"
            +" set volume_id ="+ binderVolumeId
            +"   , seq=?"
            +"   , child_id="+newChildId
            +"   , bates_number=?"
            +"   , path='*'"
            +"   , filename=?"
            +"   , offset=?"
            +"   , file_type=?"
            +"   , boundary_flag=?"
            +"   , rotate=?"
            +"   , boundary=?");

        PreparedStatement psImage = con.prepareStatement(
            "insert into binder_image"
            +" set page_id=?"
            +"   , image = ?");

        Statement st2 = con.createStatement();
        rs = st2.executeQuery(
            "select seq, bates_number, path, filename"
            +"    , offset, file_type, boundary_flag, rotate"
            +"    , page_id"
            +" from page"
            +" where child_id="+childId);

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

                psPage.setInt(1,seq);  // seq
                psPage.setString(2,batesPrefix + batesNumber);  // bates_number
                psPage.setInt(3,originalPageId);  // filename
                psPage.setInt(4,offset);  // offset
                psPage.setInt(5,fileType);  // file_type
                psPage.setString(6,boundaryFlag);  // boundary_flag
                psPage.setInt(7,rotate);  // rotate
                psPage.setInt(8,newBoundary);  // boundary
                psPage.executeUpdate();

                newBoundary = B_NONE;

                File imageFile = new File(imagePath, path + "/" + filename);
                long imageLength = imageFile.length();
                if (imageLength >= Integer.MAX_VALUE) {
                    throw new ServerFailException("Image too large: "+batesNumber);
                }
                InputStream stream = new FileInputStream(imageFile);
                psImage.setInt(1, originalPageId);
                psImage.setBinaryStream(2, stream, (int) imageLength);
                psImage.executeUpdate();
            }
        } catch (IOException e) {
            throw new ServerFailException("Error reading image: "+rs.getString(4));
        }
        st2.close();
        psPage.close();
        psImage.close();

        // Copy the data values
        st.executeUpdate(
            "insert into value"
            +"   (child_id, field_name, sequence, value)"
            +" select "+newChildId+", field_name, sequence, value"
            +" from value"
            +" where child_id="+childId);

        st.executeUpdate(
            "insert into namevalue"
            +"   (child_id, field_name, sequence"
            +"  , last_name, first_name, middle_name, organization, value)"
            +" select "+newChildId+", field_name, sequence"
            +"      , last_name, first_name, middle_name, organization, value"
            +" from namevalue"
            +" where child_id="+childId);

        st.executeUpdate(
            "insert into longvalue"
            +"   (child_id, field_name, sequence, value)"
            +" select "+newChildId+", field_name, sequence, value"
            +" from longvalue"
            +" where child_id="+childId);

        adjustBounds(task, binderVolumeId);
    }



    private void removeFromBinder (ServerTask task, int givenPageId, int givenVolumeId)
    throws SQLException {
        Statement st=task.getStatement();
        ResultSet rs1 = st.executeQuery(
            "select child_id, V.volume_id"
            +" from page P"
            +"   inner join volume V using (volume_id)"
            +"   inner join volume V0 using (project_id)"
            +" where P.page_id="+givenPageId
            +"   and V0.volume_id="+givenVolumeId
            +"   and V.sequence=-1");
        if (! rs1.next()) {
            throw new ServerFailException("Nothing to remove.");
        }
        int childId = rs1.getInt(1);
        int volumeId = rs1.getInt(2);
        rs1.close();

        st.executeUpdate(
            "delete page.*, binder_image.*"
            +" from page"
            +"   inner join binder_image"
            +"     on binder_image.page_id=page.filename"
            +" where page.child_id="+childId);

        task.executeUpdate(
            "delete from child"
            +" where child_id="+childId);
        st.executeUpdate(
            "delete from value"
            +" where child_id="+childId);
        st.executeUpdate(
            "delete from longvalue"
            +" where child_id="+childId);
        st.executeUpdate(
            "delete from namevalue"
            +" where child_id="+childId);

        adjustBounds(task, volumeId);
    }


    private static int lastInsertId(Statement st) throws SQLException {
        ResultSet rs = st.executeQuery(
            "select last_insert_id()");
        rs.next();
        int result = rs.getInt(1);
        rs.close();
        return result;
    }


    private static void adjustBounds(ServerTask task, int volumeId)
    throws SQLException {
        Statement st=task.getStatement();

        ResultSet rs2 = st.executeQuery(
            "select min(lft), max(rgt)"
            +" from child"
            +" where volume_id="+volumeId);
        rs2.next();
        int lft=rs2.getInt(1);
        int rgt=rs2.getInt(2);
        if (rgt != 0) {
            // set volume and range bounds
            st.executeUpdate(
                "update volume V"
                +"   inner join range R using (volume_id)"
                +"   inner join batch B using (volume_id)"
                +" set V.lft="+lft
                +"   , V.rgt="+rgt
                +"   , R.lft="+lft
                +"   , R.rgt="+rgt
                +"   , B.lft="+lft
                +"   , B.rgt="+rgt
                +" where V.volume_id="+volumeId);
            // Make first page start of range
            // (Old first page is already child, if required)
            st.executeUpdate(
                "update page"
                +" set boundary='RANGE'"
                +" where volume_id="+volumeId
                +"   and seq="+lft);
        } else {
            // Binder is empty, remove volume and range and batch
            st.executeUpdate(
                "delete volume.*, range.*, batch.*"
                +" from volume"
                +"   inner join range using (volume_id)"
                +"   inner join batch using (volume_id)"
                +" where volume.volume_id="+volumeId);
        }
        rs2.close();
    }
}
