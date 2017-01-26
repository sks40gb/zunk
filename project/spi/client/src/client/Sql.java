/* $Header: /home/common/cvsarea/ibase/dia/src/client/Sql.java,v 1.14.8.3 2006/03/14 15:08:46 nancy Exp $ */
package client;

import common.Log;
import common.msg.MessageConstants;
import common.msg.MessageWriter;
import common.msg.XmlUtil;

import java.io.IOException;
import java.sql.ResultSet;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Execute the given SQL on the server, returning a <code>ResultSet</code>
 * from queries and a count of rows affected for updates.
 * @see TaskExecuteQuery
 * @see server.Handler_sql_query
 */
public class Sql implements MessageConstants {
   
    
    static MessageWriter writer=null;
    
    /**
     * This class cannot be instantiated.
     */
    private Sql() {}

    /**
     * Execute a named query on the server with a given array of parameters.
     * No metadata is returned.
     * @param scon The server connection.
     * @param sqlName The name of the SQL statement stored on the server.
     * @param parameters An array of parameters for a prepared statement.
     *        May be null, indicating no parameters.
     * @return The ResultSet created by the query; null if none.
     *         (SQLException in the log.)
     */
    public static ResultSet executeQuery(ServerConnection scon,
                                         ClientTask ctask,
                                         String sqlName,
                                         String[] parameters)
    throws IOException, ClassNotFoundException {
        return executeQuery(scon, ctask,sqlName,parameters,false);
    }

    /** Convenience method to execute a named query on the server
     * with no parameters
     */
    public static ResultSet executeQuery(ServerConnection scon,
                                         ClientTask ctask,
                                         String sqlName)
    throws IOException  {
        return executeQuery(scon, ctask,sqlName,null,false);
    }

    /** Convenience method to execute a named query on the server
     * with one given parameter
     */
    public static ResultSet executeQuery(ServerConnection scon,
                                         ClientTask ctask,
                                         String sqlName,
                                         String parameter1)
    throws IOException, ClassNotFoundException {
        return executeQuery(scon, ctask,sqlName,new String[] {parameter1}, false);
    }

    /**
     * Convenience method to execute a named query on the server
     * with two given parameters
     */
    public static ResultSet executeQuery(ServerConnection scon,
                                         ClientTask ctask,
                                         String sqlName,
                                         String parameter1,
                                         String parameter2)
    throws IOException, ClassNotFoundException {
        return executeQuery(
                scon,ctask,sqlName,new String[] {parameter1, parameter2}, false);
    }

    /**
     * Execute a named query on the server with a given array of parameters,
     * returning metadata.
     * @param scon The server connection.
     * @param sqlName The name of the SQL statement stored on the server.
     * @param parameters An array of parameters for a prepared statement.
     *        May be null, indicating no parameters.
     * @return The ResultSet created by the query; null if none.
     *         (SQLException in the log.)
     */
    public static ResultSet executeQueryWithMetaData(ServerConnection scon,
                                         ClientTask ctask,
                                         String sqlName,
                                         String[] parameters)
    throws IOException, ClassNotFoundException {
        return executeQuery(scon, ctask,sqlName,parameters,true);
    }

    /** Convenience method to execute a named query on the server
     * with no parameters
     */
    public static ResultSet executeQueryWithMetaData(ServerConnection scon,
                                         ClientTask ctask,
                                         String sqlName)
    throws IOException, ClassNotFoundException {
        return executeQuery(scon, ctask,sqlName,null,true);
    }

    /** Convenience method to execute a named query on the server
     * with one given parameter
     */
    public static ResultSet executeQueryWithMetaData(ServerConnection scon,
                                         ClientTask ctask,
                                         String sqlName,
                                         String parameter1)
    throws IOException, ClassNotFoundException {
        return executeQuery(scon, ctask,sqlName,new String[] {parameter1}, true);
    }

    /** Convenience method to execute a named query on the server
     * with two given parameters
     */
    public static ResultSet executeQueryWithMetaData(ServerConnection scon,
                                         ClientTask ctask,
                                         String sqlName,
                                         String parameter1,
                                         String parameter2)
    throws IOException, ClassNotFoundException {
        return executeQuery(
                scon,ctask,sqlName,new String[] {parameter1, parameter2}, true);
    }


    /**
     * Execute a named query on the server with a given array of parameters.
     * @param scon The server connection.
     * @param sqlName The name of the SQL statement stored on the server.
     * @param parameters An array of parameters for a prepared statement.
     *        May be null, indicating no parameters.
     * @param withMetaData If true, metadata (column names) are included and
     *        may be retrieved using ResultSet.getMetaData()
     * @return The ResultSet created by the query; null if none.
     *         (SQLException in the log.)
     */
    public static ResultSet executeQuery(ServerConnection scon,
                                         ClientTask ctask,
                                         String sqlName,
                                         String[] parameters,
                                         boolean withMetaData )
                                throws IOException {
               
        writer = scon.startMessage(T_SQL_QUERY);
        writer.writeAttribute(A_NAME, sqlName);
        if (withMetaData) {
            writer.writeAttribute(A_REQUEST_METADATA, "YES");
        }
        ctask.addStandardAttributes(writer);
        if (parameters != null) {
            for (int i=0; i < parameters.length; i++) {
                writer.startElement(T_PARAMETER);
                writer.writeContent(parameters[i]);
                writer.endElement();
            }
        }
        writer.endElement();
        writer.close();
        scon.connect();
        
        Element reply = scon.receiveMessage();
        //Log.print("received "+reply.getNodeName());
         
        if (T_RESULT_SET.equals(reply.getNodeName())) {
            return resultFromXML(reply);
        }else if(T_OK.equals(reply.getNodeName())){
            String ok = reply.getNodeName();
            return null;
        } 
        else {
            Log.quit("Sql.executeQuery: unexpected message type: "+reply.getNodeName());
            return null; // never get here
        }
    }


    /**
     * Create a ResultSet from XML "result_set" message.
     * Returns null if not a result set (for assignment to final variable)
     */
    public static ResultSet resultFromXML(Element reply) {

        if (!T_RESULT_SET.equals(reply.getNodeName())) {
            return null;
        }

        int count = Integer.parseInt(reply.getAttribute(A_COUNT));
        DiaResultSet results = new DiaResultSet(count);
        Node child = reply.getFirstChild();
        while (child != null) {
            if (child.getNodeType() == Node.ELEMENT_NODE) {
                String childName = child.getNodeName();
                assert T_ROW.equals(childName) || T_HEADING.equals(childName);
                NodeList items
                        = ((Element) child).getElementsByTagName(T_COLUMN);
                assert items.getLength() == count;
                String[] newRow = new String[count];
                for (int j = 0; j < count; j++) {
                    if ("YES".equals
                        (((Element) items.item(j)).getAttribute(A_IS_NULL)))
                    {
                        newRow[j] = null;
                    } else {
                        newRow[j] = XmlUtil.getTextFromNode(items.item(j));
                    }
                }
                if (T_ROW.equals(childName)) {
                    results.addRow(newRow);
                } else {
                    results.setHeadings(newRow);
                }
            }
            child = child.getNextSibling();
        }
        return results;
    }
    

    /**
     * Execute a named update on the server with a given array of parameters.
     * @param scon The server connection.
     * @param sqlName The name of the SQL statement stored on the server.
     * @param parameters An array of parameters for a prepared statement.
     *        May be null, indicating no parameters.
     * @return The count of affected rows, or -1 if an SQL error occurred.
     *         (SQLException in the log.)
     */
    public static int executeUpdate(ServerConnection scon,
                                         ClientTask ctask,
                                         String sqlName,
                                         String[] parameters)
    throws IOException {
        MessageWriter writer;
        writer = scon.startMessage(T_SQL_UPDATE);
        writer.writeAttribute(A_NAME, sqlName);
        ctask.addStandardAttributes(writer);
        if (parameters != null) {
            for (int i=0; i < parameters.length; i++) {
                writer.startElement(T_PARAMETER);
                writer.writeContent(parameters[i]);
                writer.endElement();
            }
        }
        writer.endElement();
        writer.close();

        Element reply = scon.receiveMessage();
        //Log.print("received "+reply.getNodeName());

        if (T_UPDATE_COUNT.equals(reply.getNodeName())) {
            return Integer.parseInt(reply.getAttribute(A_COUNT));
        } else {
            Log.quit("Sql.executeQuery: unexpected message type: "+reply.getNodeName());
            return -1; // never get here
        }
    }

    /** Convenience method to execute a named update on the server
     * with no parameters
     */
    public static int executeUpdate(ServerConnection scon,
                                         ClientTask ctask,
                                         String sqlName)
    throws IOException {
        return executeUpdate(scon, ctask,sqlName,(String[]) null);
    }

    /** Convenience method to execute a named update on the server
     * with one given parameter
     */
    public static int executeUpdate(ServerConnection scon,
                                         ClientTask ctask,
                                         String sqlName,
                                         String parameter1)
    throws IOException {
        return executeUpdate(scon, ctask,sqlName,new String[] {parameter1});
    }

    /**
     * Convenience method to execute a named update on the server
     * with two given parameters
     */
    public static int executeUpdate(ServerConnection scon,
                                         ClientTask ctask,
                                         String sqlName,
                                         String parameter1,
                                         String parameter2)
    throws IOException {
        return executeUpdate(
                scon,ctask,sqlName,new String[] {parameter1, parameter2});
    }

}
