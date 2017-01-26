/* $Header: /home/common/cvsarea/ibase/dia/src/server/Handler_delete_mailsent.java,v 1.4.6.1 2006/03/14 15:08:46 nancy Exp $ */
package server;

import client.MessageMap;
import common.Log;
//import common.msg.MessageReader;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Handler to mark the given mailsent.mailsent_id's as deleted by
 * changing mailsent.status to Deleted.
 * @see client.TaskDeleteMailsent
 */
final public class Handler_delete_mailsent extends Handler {
    
    PreparedStatement pst;
    Connection con;
    Statement st;

    /**
     * This class cannot be instantiated.
     */
    public Handler_delete_mailsent() {
        Log.print("Handler_delete_mailsent");
    }

    public void run (ServerTask task, Element action) throws SQLException {
        int usersId = task.getUsersId();
        Element givenValueList = action;

        Node firstChild = givenValueList.getFirstChild();
        while (firstChild != null && firstChild.getNodeType() != Node.ELEMENT_NODE) { 
            firstChild = firstChild.getNextSibling();
        }
        if (firstChild == null) {
            Log.print("Handler_delete_mailsent null firstChild");
            return;
        }

        Map idMap = new HashMap();
        // fill in the ids from the xml
        try {
            idMap = MessageMap.decode((Element) firstChild);
        } catch (Throwable t) {
            Log.quit(t);
        }
        if (idMap.size() < 1) {
            Log.print("Handler_delete_mailsent empty map");
            return;
        }
        con = task.getConnection();
        st = task.getStatement();
        pst = task.prepareStatement(
            "update mailsent set status = 'Deleted'"
            +" where mailsent_id = ?");
        for (Iterator i=idMap.entrySet().iterator(); i.hasNext(); ) {
            Map.Entry e = (Map.Entry) i.next();
            Log.print("Handler_delete_mailsent " + e.getKey());
            pst.setString(1, (String)e.getKey());
            pst.executeUpdate();
        }

        pst.close();
    }
}
