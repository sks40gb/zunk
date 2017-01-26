/* $Header: /home/common/cvsarea/ibase/dia/src/server/Handler_mailreceived_data.java,v 1.4.6.2 2006/03/22 20:27:15 nancy Exp $ */
package server;

import client.MessageMap;
import common.Log;
import common.msg.MessageReader;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Handler for mailreceived message.  Use MessageMap to decode an XML message
 * that contains mailreceived/status pairs to update the status of mailreceived.
 * @see client.TaskSendMailreceivedData
 * @see client.MessageMap
 */
final public class Handler_mailreceived_data extends Handler {
    
    PreparedStatement pst;
    Connection con;
    Statement st;

    int mailreceivedId = 0;

    /**
     * This class cannot be instantiated.
     */
    public Handler_mailreceived_data() {
    }

    public void run (ServerTask task, Element action) throws SQLException {
        int usersId = task.getUsersId();
        Element givenValueList = action;

        Node firstChild = givenValueList.getFirstChild();
        while (firstChild != null && firstChild.getNodeType() != Node.ELEMENT_NODE) { 
            firstChild = firstChild.getNextSibling();
        }
        if (firstChild == null) {
            Log.print("Handler_mailreceived_data null firstChild");
            return;
        }

        Map idMap = new HashMap();
        // fill in the id and status from the xml
        try {
            idMap = MessageMap.decode((Element) firstChild);
        } catch (Throwable t) {
            Log.quit(t);
        }
        if (idMap.size() < 1) {
            Log.print("Handler_mailreceived_data empty map");
            return;
        }
        con = task.getConnection();
        st = task.getStatement();
        pst = task.prepareStatement(
            "update mailreceived set status = ?"
            +" where mailreceived_id = ?");
        for (Iterator i=idMap.entrySet().iterator(); i.hasNext(); ) {
            Map.Entry e = (Map.Entry) i.next();
            Log.print("Handler_mailreceived_data " + e.getKey());
            pst.setString(1, (String)e.getValue());
            pst.setInt(2, Integer.parseInt((String)e.getKey()));
            pst.executeUpdate();
        }

        pst.close();
    }
}
