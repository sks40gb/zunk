/*
 * ValidationFieldsFetch.java
 *
 * Created on October 19, 2007, 12:43 PM
 */
package client;

import common.Log;
import common.ValidationData;
import common.msg.MessageWriter;
import java.io.IOException;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * ClientTask to get all validation for the field of a project.
 * @author bmurali
 */
public class TaskDisplayAdvanceValidation extends ClientTask {

    /** The server connection */
    final private ServerConnection scon = Global.theServerConnection;
    /** Project Id */
    private int projectId;
    /** Field Id */
    private int fieldId;

    /** Creates a new instance of validationFieldsFetch with following parameters      
     * @param projectId  Project Id
     * @param fieldId    Field Id
     */
    public TaskDisplayAdvanceValidation(int projectId, int fieldId) {
        this.projectId = projectId;
        this.fieldId = fieldId;
    }

    /**
     * Write the message with attributes and set the result.
     */
    @Override
    public void run() throws IOException {
        try {
            MessageWriter writer;
            writer = scon.startMessage(T_ADVANCE_VALIDATION);
            writer.writeAttribute(A_PROJECT_ID, projectId);
            writer.writeAttribute(A_FIELD_ID, fieldId);
            writer.endElement();
            writer.close();

            Element reply = scon.receiveMessage();
            Log.print("received " + reply.getNodeName());
            
            //get the data for the validation from the results
            //1. get the headings for the validation
            //2, get the values from the each rows
            int row = 0;
            int j = 0;
            NodeList resultSetNodes = reply.getElementsByTagName("heading");
            NodeList headings = resultSetNodes.item(0).getChildNodes();
            NodeList rowList = reply.getElementsByTagName("row");
            row = rowList.getLength();
            NodeList columnValue = rowList.item(j).getChildNodes();

            String[] head = new String[headings.getLength()];
            String[] rowValue = new String[(rowList.getLength() * columnValue.getLength())];
            
            //get the heading from the node and put in a array <code>head</head>
            for (int i = 0; i < headings.getLength(); i++) {
                head[i] = headings.item(i).getTextContent();
            }

            int k = 0;
            //get the row values
            for (j = 0; j < row; j++) {
                columnValue = rowList.item(j).getChildNodes();
                for (int i = 0; i < columnValue.getLength(); i++) {
                    rowValue[i] = columnValue.item(i).getTextContent();
                    rowValue[k] = rowValue[i];
                    k++;
                }
            }

            // store the result so the callback can get it
            ValidationData obj = new ValidationData(rowValue, row);
            setResult(obj);

        } catch (FailException e) {
            Log.print("Validate batch failure: " + e.getMessage());
            throw e;
        }
    }
}