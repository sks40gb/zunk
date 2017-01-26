/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package client;

import common.msg.MessageWriter;
import java.io.IOException;
import java.sql.ResultSet;
import org.w3c.dom.Element;

/**
 * ClientTask to get the Listing QC field 
 */
public class TaskGetListingQCField extends ClientTask {
   
    /** Project Id */
    private int project_id;    
    /** Volume Id */
    private int volume_id;    
    /* Status (Listing, ListingQC, Tally, TallyQC etc) */
    private String whichStatus;
    /** The server connection */
    private ServerConnection scon = Global.theServerConnection;
 
    /**
     * Instantiate the object with following parameter
     * @param project_id Poject Id
     * @param volume_id  Volume Id
     * @param whichStatus Status select the process whether it is Listing, Tally etc..
     */
    public TaskGetListingQCField(int project_id,int volume_id,String whichStatus){
       this.project_id = project_id;
       this.volume_id = volume_id;    
       this.whichStatus = whichStatus;
    }

    //Requsets sent to the server in xml format.     
    public void run() throws IOException {
        MessageWriter writer = scon.startMessage(T_GET_LISTING_QC_FIELD);
        writer.writeAttribute(A_PROJECT_ID, project_id);
        writer.writeAttribute(A_VOLUME_ID, volume_id);
        writer.writeAttribute(A_STATUS,whichStatus);        
        writer.endElement();
        writer.close(); 
        //get the reply from the server and set result.   
        Element reply = scon.receiveMessage();
        final ResultSet rsObj = Sql.resultFromXML(reply);

        setResult(rsObj);
                 
    }

}


