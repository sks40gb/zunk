/* $Header: /home/common/cvsarea/ibase/dia/src/server/Handler_page_boundary.java,v 1.3.6.2 2006/03/21 16:42:41 nancy Exp $ */
package server;

import client.MessageMap;
import common.Log;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.w3c.dom.Element;

/**
 * Handler for page_boundary message.  Handles updating of boundary, without
 * being recordes as a page save.  This is NOT called for unitizing, it
 * is called when a boundary is changed in a non-unitizing viewer.
 * @see client.TaskSendBoundary
 * @see BoundaryMapper
 */
final public class Handler_page_boundary extends Handler {

    /**
     * This class cannot be instantiated.
     */
    public Handler_page_boundary() {}

    public void run (ServerTask task, Element action) throws SQLException {
        int volumeId = task.getVolumeId();
        int batchId = task.getBatchId();
        int givenPageId = Integer.parseInt(action.getAttribute(A_PAGE_ID));

        assert action.hasAttribute(A_BOUNDARY_FLAG);
        String givenBoundaryFlag = action.getAttribute(A_BOUNDARY_FLAG);
        //Log.print("page boundary: boundary="+givenBoundaryFlag+" pageId="+givenPageId);
        BoundaryMapper.store(task, volumeId, givenPageId, givenBoundaryFlag.trim());
    }
}
