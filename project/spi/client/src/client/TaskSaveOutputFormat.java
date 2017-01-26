/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package client;

import common.msg.MessageWriter;
import java.io.IOException;
import java.util.List;
import org.w3c.dom.Element;
import valueobjects.FieldFormatData;

/*
 * Task Class to Save output Format
 */
public class TaskSaveOutputFormat extends ClientTask {

    /** Server connection */
    final private ServerConnection scon = Global.theServerConnection;
    private List<FieldFormatData> list;
    private MessageWriter writer;

    /**
     * Create the instance of this class and remember the parameter
     * @param queryData - query records
     */
    public TaskSaveOutputFormat(List<FieldFormatData> list) {
        this.list = list;
    }

    /**
     * Write the message with attributes and set the result.
     * Message should be in XML format. 
     */
    @Override
    public void run() throws IOException {

        writer = scon.startMessage(T_SAVE_OUTPUT_FORMAT);
        writer.writeAttribute(A_COUNT, 9);
        encode(writer);
        writer.endElement();
        writer.close();

        Element reply = scon.receiveMessage();

    }

    private void encode(MessageWriter writer) throws IOException {

        for (int i = 0; i < list.size(); i++) {
            writer.startElement(T_ROW);
            /*  
            1. project_field_id       
            2. ovp_fields_id   
            3. field_name  
            4. separator   
            5. sequence    
            6. format      
            7. suppress
            8. type
            9. volume_id*/
            
            writeColumn(list.get(i).getProjectFieldId());
            writeColumn(list.get(i).getOvpFieldId());
            writeColumn(list.get(i).getName());
            writeColumn(list.get(i).getSeparator());
            writeColumn(list.get(i).getSequence());
            writeColumn(list.get(i).getFormat());
            writeColumn(list.get(i).isSuppress());
            writeColumn(list.get(i).getType());            
            writeColumn(list.get(i).getVolumeId());            
            
            writer.endElement();
        }
    }

    private void writeColumn(String value) throws IOException {
        writer.startElement(T_COLUMN);
        if(value == null){
            writer.writeAttribute(A_IS_NULL, "YES");
        }else{
            writer.writeContent(value);
        }
        writer.endElement();
    }
    
    private void writeColumn(int value) throws IOException {
       writeColumn(Integer.toString(value));
    }
    
    private void writeColumn(boolean value) throws IOException {
        writeColumn(Boolean.toString(value));
    }
}
    
