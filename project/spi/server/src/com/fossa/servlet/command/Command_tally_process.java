/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fossa.servlet.command;

import com.fossa.servlet.common.Log;
import com.fossa.servlet.common.msg.MessageWriter;
import com.fossa.servlet.common.msg.XmlUtil;
import com.fossa.servlet.dao.DBTask;
import com.fossa.servlet.session.UserTask;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Command Class to Create Tally Group for Given Volume.
 * @author sunil
 */
public class Command_tally_process implements Command {

    private String value;
    ArrayList valueList = new ArrayList();

    public String execute(Element action, UserTask user, DBTask dbTask, MessageWriter writer) {

        CallableStatement cs = null;
        Statement statement = null;
        ResultSet rs = null;
        Connection connection = null;
        String fieldName = null;
        String tallyType = null;
        String samplingValue = null;
        int project_id;
        int volume_id;
        String allFieldsName = "";
        String allTallyType = "";
        String allSamplingValue = "";

        project_id = Integer.parseInt(action.getAttribute(A_PROJECT_ID));
        volume_id = Integer.parseInt(action.getAttribute(A_VOLUME_ID));
        Node firstChild = action.getFirstChild();
        while (firstChild != null && firstChild.getNodeType() != Node.ELEMENT_NODE) {
            firstChild = firstChild.getNextSibling();
        }
        if (firstChild != null) {
            try {
                Node child = action.getFirstChild();
                child = child.getNextSibling();
                while (child != null) {
                    if (child.getNodeType() == Node.ELEMENT_NODE && E_FIELD_NAME.equals(child.getNodeName())) {

                        value = XmlUtil.getTextFromNode(child);
                        valueList.add(value);
                    }
                    child = child.getNextSibling();
                }
            } catch (Throwable t) {
                logger.error("Exception while reading the XMLReader." + t);
                StringWriter sw = new StringWriter();
                t.printStackTrace(new PrintWriter(sw));
                logger.error(sw.toString());
                Log.quit(t);
            }
        }

        try {
            connection = dbTask.getConnection();
            cs = connection.prepareCall("{call sproc_CreateTallyGroups(?,?,?,?,?)}");

            for (int i = 0; valueList.size() > i; i++) {
                //  Node fieldNode = fieldNodeRows.item(i);
                //  Node child = fieldNode.getFirstChild();
                //    System.out.println("connection created ---rams"+fieldNode.getNodeName());
                String getListValue = (String) valueList.get(i);
                String[] splitListValue = getListValue.split("-");
                fieldName = splitListValue[0];                
                // get all field name and store in a string.
                // it is used while running the query.
                allFieldsName += (i == 0) ? ("'" + fieldName + "'") : (" , '" + fieldName + "'");
                
                 
                tallyType = splitListValue[1];   
                allTallyType += (i == 0) ? ("'" + tallyType + "'") : (" , '" + tallyType + "'");
                
                samplingValue = splitListValue[2];    
                allSamplingValue += (i == 0) ? ("'" + samplingValue + "'") : (" , '" + samplingValue + "'");  
                cs.setString(1, fieldName);
                cs.setString(2, samplingValue);
                cs.setString(3, tallyType);
                cs.setInt(4, project_id);
                cs.setInt(5, volume_id);
                cs.execute();    
            }
            
            cs.close();

            statement = connection.createStatement();

            String sqlString = " SELECT DISTINCT PF.projectfields_id, PF.field_name," +
                    " TTM.tally_name, TDG.dictionary_group_number FROM tally_mapping TM " +
                    " INNER JOIN tally_dictionary TD ON TD.tally_mapping_id = TM.tally_mapping_id " +
                    " INNER JOIN tally_dictionary_group TDG ON TDG.tally_dictionary_group_id = TD.tally_dictionary_group_id " +
                    " INNER JOIN projectfields PF ON PF.projectfields_id = TM.project_field_id " +
                    " INNER JOIN tally_type_master TTM ON TTM.tally_type_master_id = TM.tally_type_master_id " +
                    " WHERE PF.project_id = " + project_id + " AND PF.field_name IN ( " + allFieldsName + ")";

            rs = statement.executeQuery(sqlString);

            writer.startElement(T_TALLY_PROCESS);//start main element

            String userSessionId = user.getFossaSessionId();
            writer.writeAttribute(A_FOSSAID, userSessionId);
            while (rs.next()) {
                writer.startElement(T_ROW);//start row
                //field id
                System.out.println("tally group details" + rs.getInt(1));
                //System.out.println("tally group details"+rs.getInt(1));
                writer.startElement(T_COLUMN);//start column
                writer.writeContent(rs.getInt(1));
                writer.endElement();//end column

                //field name
                writer.startElement(T_COLUMN);//start column
                writer.writeContent(rs.getString(2));
                writer.endElement();//end column

                //tally name
                writer.startElement(T_COLUMN);//start column
                writer.writeContent(rs.getString(3));
                writer.endElement();//end column

                //group number
                writer.startElement(T_COLUMN);//start column
                writer.writeContent(rs.getInt(4));
                writer.endElement();//end column

                writer.endElement();// end row

            }
            writer.endElement();//end main element

            statement.close();
            connection.commit();
        //rs.close();
        } catch (IOException ex) {
            logger.error("Exception while fetching the image path for the selected volume." + ex);
            StringWriter sw = new StringWriter();
            ex.printStackTrace(new PrintWriter(sw));
            logger.error(sw.toString());
        } catch (SQLException ex) {
            logger.error("Exception while fetching the image path for the selected volume." + ex);
            StringWriter sw = new StringWriter();
            ex.printStackTrace(new PrintWriter(sw));
            logger.error(sw.toString());
        }
        return null;
    }

    public boolean isReadOnly() {
        return true;
    }
}
