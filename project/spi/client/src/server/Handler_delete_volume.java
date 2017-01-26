package server;

import common.Log;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.w3c.dom.Element;

/**
 * Delete a volume and all associated data.
 * @see client.TaskDeleteVolume
 */
public class Handler_delete_volume extends Handler {

    /**
     * This class cannot be instantiated.
     */
    public Handler_delete_volume() {}

    public void run (ServerTask task, Element action) 
    throws SQLException, IOException {
        int projectId = 0;
        int highUnitizeBatch = 0;
        int highCodingBatch = 0;
        int highVolume = 0;
        int volumeId = Integer.parseInt(action.getAttribute(A_VOLUME_ID));
        Log.print("Handler_delete_volume volumeId="+volumeId);

        Statement st = task.getStatement();
        ResultSet rs = st.executeQuery(
            "select 0"
            +" from assignment A"
            +"   inner join batch B on (B.batch_id = A.batch_id)"
            +"   inner join volume V on (V.volume_id = B.volume_id)"
            +" where V.volume_id="+volumeId
            +" UNION ALL"
            +" select 0"
            +" from session S, volume V"
            +" where V.volume_id="+volumeId
            +"   and S.volume_id = V.volume_id"
            +" limit 1");
        if (rs.next()) {
            throw new ServerFailException(
                "Volume has batches assigned or in use.");
        }

        st.executeUpdate(
            "delete pageissue.*"
            +" from pageissue"
            +" where volume_id="+volumeId);

        st.executeUpdate(
            "delete page.*"
            +" from page"
            +" where volume_id="+volumeId);

        st.executeUpdate(
            "delete childcoded.*"
            +" from childcoded"
            +"   inner join child using (child_id)"
            +"   inner join volume V using (volume_id)"
            +" where V.volume_id="+volumeId);

        st.executeUpdate(
            "delete fieldchange.*"
            +" from fieldchange"
            +"   inner join child using (child_id)"
            +"   inner join volume V using (volume_id)"
            +" where V.volume_id="+volumeId);

        st.executeUpdate(
            "delete value.*"
            +" from value"
            +"   inner join child using (child_id)"
            +"   inner join volume V using (volume_id)"
            +" where V.volume_id="+volumeId);

        st.executeUpdate(
            "delete namevalue.*"
            +" from namevalue"
            +"   inner join child using (child_id)"
            +"   inner join volume V using (volume_id)"
            +" where V.volume_id="+volumeId);

        st.executeUpdate(
            "delete longvalue.*"
            +" from longvalue"
            +"   inner join child using (child_id)"
            +"   inner join volume V using (volume_id)"
            +" where V.volume_id="+volumeId);

        //st.executeUpdate(
        //    "delete childerror.*"
        //    +" from childerror"
        //    +"   inner join child using (child_id)"
        //    +"   inner join volume V using (volume_id)"
        //    +" where V.volume_id="+volumeId);

        task.executeUpdate(
            "delete child.*"
            +" from child"
            +" where volume_id="+volumeId);

        //st.executeUpdate(
        //    "update batchcredit"
        //    +" set batch_id=0"
        //    +" where volume_id="+volumeId);

        st.executeUpdate(
            "delete batcherror.*"
            +" from batcherror"
            +"   inner join batch B using (batch_id)"
            +"   inner join volume V using (volume_id)"
            +" where V.volume_id="+volumeId);

        st.executeUpdate(
            "delete batchuser.*"
            +" from batchuser"
            +"   inner join batch B using (batch_id)"
            +"   inner join volume V using (volume_id)"
            +" where V.volume_id="+volumeId);

        task.executeUpdate(
            "delete teamsqueue.*"
            +" from teamsqueue"
            +"   inner join batch B using (batch_id)"
            +"   inner join volume V using (volume_id)"
            +" where V.volume_id="+volumeId);

        task.executeUpdate(
            "delete usersqueue.*"
            +" from usersqueue"
            +"   inner join batch B using (batch_id)"
            +"   inner join volume V using (volume_id)"
            +" where V.volume_id="+volumeId);

        task.executeUpdate(
            "delete batch.*"
            +" from batch"
            +" where volume_id="+volumeId);

        st.executeUpdate(
            "delete range.*"
            +" from range"
            +" where volume_id="+volumeId);

        task.executeUpdate(
            "delete teamsvolume.*"
            +" from teamsvolume"
            +" where volume_id="+volumeId);

        st.executeUpdate(
            "delete volumeerror.*"
            +" from volumeerror"
            +" where volume_id="+volumeId);

        task.executeUpdate(
            "delete from volume"
            +" where volume_id="+volumeId);
    }
}
