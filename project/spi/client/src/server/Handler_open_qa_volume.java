/* $Header: /home/common/cvsarea/ibase/dia/src/server/Handler_open_qa_volume.java,v 1.6.2.3 2006/03/21 16:42:41 nancy Exp $ */

package server;

import common.Log;
import common.StatusConstants;
import common.msg.MessageWriter;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.w3c.dom.Element;

/**
 * Handler for open_qa_volume message.  Lock the volume for the given 
 * volume.volume_id then get and return the project.project_name and
 * project.project_id.
 * @see client.TaskOpenQAVolume
 */
final public class Handler_open_qa_volume extends Handler {

    /**
     * This class cannot be instantiated.
     */
    public Handler_open_qa_volume() {}

    public void run (ServerTask task, Element action)
    throws SQLException, IOException {

        int volumeId = Integer.parseInt(action.getAttribute(A_VOLUME_ID));
        Log.print("in Handler_open_qa_volume.run vol="+volumeId);
        assert ! task.isAdmin();

        Statement st = task.getStatement();

        String project = null;
        String volume = null;
        int returnProjectId = 0;
        String splitDocuments = null;
        int usersId = task.getUsersId();

        // verify volume id
        // get volume and project information
        // Note.  If there is session with this volume and batch_id=0,
        //   then somebody (else) has this volume open for QA
        ResultSet rs0 = st.executeQuery(
            "select P.project_name, V.volume_name, split_documents, P.project_id"
            +" from project P"
            +"   inner join volume V using (project_id)"
            +"   left join session S"
            +"     on S.volume_id=V.volume_id and S.batch_id=0"
            +" where P.active"
            +"   and V.volume_id="+volumeId
            +"   and S.volume_id is null");
        if (rs0.next()) {
            project = rs0.getString(1);
            volume = rs0.getString(2);
            splitDocuments = rs0.getBoolean(3) ? "Yes" : "No";
            returnProjectId = rs0.getInt(4);
            rs0.close();
        } else {
            rs0.close();
            throw new ServerFailException("Volume not available for QA");
        }

        task.lockVolume(volumeId);

        EventLog.open(task, volumeId, /* batchId */ 0, "QA");

        task.commitTransaction();

        MessageWriter writer = task.getMessageWriter();
        writer.startElement(T_BATCH_OPENED);
        writer.writeAttribute(A_BATCH_ID, 0);
        writer.writeAttribute(A_PROJECT_NAME, project);
        writer.writeAttribute(A_SPLIT_DOCUMENTS, splitDocuments);
        writer.writeAttribute(A_PROJECT_ID, returnProjectId);
        writer.endElement();
    }
}
