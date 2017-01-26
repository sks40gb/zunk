package server;

//import client.MessageMap;
import client.Global;
import client.ServerConnection;
import common.Log;
import common.msg.MessageWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
//import common.msg.MessageReader;
//import common.msg.MessageWriter;

import java.sql.SQLException;
import java.sql.Statement;
//import java.util.Map;
import org.w3c.dom.Element;


/**
 * Handler for changing batch boundaries.
 * Read the message with attributes and call <code>BatchIo.batchBoundary</code>
 * to perform the task.
 */
public class Handler_validation extends Handler {


    /**
     * This class cannot be instantiated.
     */
    public Handler_validation() {}

    public void run (ServerTask task, Element action) throws SQLException,IOException {
        
       Connection con;
       Statement st;
       boolean requestedMetaData = true;
       
       
        System.out.println("inside Handler_validation-------------------------------");
        System.out.println("inside Handler_validation-------------------------------"+action);
        
//          int getVolumeId = getInt(action, A_VOLUME_ID);
//          String getUserInput = action.getAttribute(A_USER_INPUT);
//          
//          System.out.println("getVolumeId................"+getVolumeId);
//          System.out.println("getUserInput................"+getUserInput);
//         
//          
//         MessageWriter writer = task.getMessageWriter();
//         writer.startElement(T_VALID);
//         if (getVolumeId != 0) {
//            writer.writeAttribute(A_VOLUME_ID, getVolumeId);
//            writer.writeAttribute(A_USER_INPUT,"From server"+getUserInput);
//        }
//        writer.endElement();
//        writer.close();         
            
        //BatchIO.batchBoundary(task, givenBatchId, givenChildId, givenDelta);
        con=task.getConnection();
        st=con.createStatement();
        String sql="SELECT function_name,description,error_message,status,user_input FROM validation_functions WHERE validation_id=1";
        st.executeQuery(sql); 
        ResultSet result = st.getResultSet();
        //ResultSetMetaData rsmd = result.getMetaData();
        writeXmlFromResult(task,result,requestedMetaData);
    }
    
    public static void writeXmlFromResult(ServerTask task, ResultSet rs, boolean requestedMetaData)
    throws SQLException, IOException {
   
        ResultSetMetaData rsmd = rs.getMetaData();
        int columnCount = rsmd.getColumnCount();

        MessageWriter writer = task.getMessageWriter();        
        writer.startElement(T_RESULT_SET);
        writer.writeAttribute(A_COUNT, Integer.toString(columnCount));
        if (requestedMetaData) {
            writer.startElement(T_HEADING);
            for (int j = 1; j <= columnCount; j++) {
                writer.startElement(T_COLUMN);
                writer.writeContent(rsmd.getColumnName(j));
                writer.endElement();
            }
            writer.endElement();
        }
        int count = 0;
        while (rs.next()) {
            System.out.println("xml ing row " + count++ );
            writer.startElement(T_ROW);
            for (int j = 1; j <= columnCount; j++) {
                writer.startElement(T_COLUMN);
                String value = rs.getString(j);
                if (rs.wasNull()) {
                    writer.writeAttribute(A_IS_NULL, "YES");
                } else {
                    
                    writer.writeContent(value);
                }
                writer.endElement();
            }
            writer.endElement();
        }
        writer.endElement();
        writer.close();
    }

    // get integer attribute, or zero if empty string
    private int getInt(Element action, String attribute) {
        String attributeString = action.getAttribute(attribute);
        return (attributeString.length() == 0 ? 0 : Integer.parseInt(attributeString));
    }
}
