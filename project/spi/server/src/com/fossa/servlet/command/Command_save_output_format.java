/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fossa.servlet.command;

import com.fossa.servlet.common.CommonLogger;
import com.fossa.servlet.common.msg.MessageWriter;
import com.fossa.servlet.common.msg.XmlUtil;
import com.fossa.servlet.dao.DBTask;
import com.fossa.servlet.exception.ServerFailException;
import com.fossa.servlet.session.UserTask;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * This class handles in saving the ListingQC process
 * @author bmurali
 */
public class Command_save_output_format implements Command {

    private Connection connection;
    private PreparedStatement pstmt = null;
    private PreparedStatement pstmt_1 = null;
    
    public String execute(Element action, UserTask user, DBTask dbTask, MessageWriter writer) {
        try {

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

            List<String[]> list = listFromXML(action);


            connection = dbTask.getConnection();
            //Delete the old record of volume from the ovp 
            pstmt = connection.prepareStatement("DELETE FROM ovp_sequence WHERE volume_id = ?");
            assert list.get(0) != null : "FIELD LIST CANNOT BE EMPTY";
            pstmt.setString(1, list.get(0)[8]);
            pstmt.executeUpdate();
            
            pstmt = connection.prepareStatement("INSERT INTO ovp_sequence (projectfields_id,ovp_field_id,separator,sequence,format, suppress,type,volume_id) " +
                    "VALUES (?,?,?,?,?,?,?,?) ");                
            pstmt_1 = connection.prepareStatement("INSERT INTO ovp_field (field_name) VALUES (?)");                
            
            Statement st = connection.createStatement();
            ResultSet result = null;
            
            for (String rec[] : list) {
                //insert the new OVP field into the ovp_file
                
                if(rec[7].equals("OVP_FIELD")){
                    pstmt_1.setString(1,rec[2]);
                    pstmt_1.executeUpdate();
                    result = st.executeQuery("SELECT TOP 1 ovp_field_id  FROM ovp_field ORDER BY ovp_field_id desc");
                    if(result.next()){
                        rec[1] = result.getString(1);
                    }
                }
                
                pstmt.setString(1,rec[0]);
                pstmt.setString(2,rec[1]);
                pstmt.setString(3,rec[3]);
                pstmt.setString(4,rec[4]);
                pstmt.setString(5,rec[5]);
                pstmt.setString(6,rec[6]);
                pstmt.setString(7,rec[7]);
                pstmt.setString(8,rec[8]);
                
                pstmt.executeUpdate();
            }

        } catch (SQLException ex) {
            CommonLogger.printExceptions(this, "SQLException while updating the listing qc.", ex);
        } catch (ServerFailException exc) {
            CommonLogger.printExceptions(this, "ServerFailException while updating the listing qc.", exc);
            return exc.getMessage();
        }
        return null;
    }

    public boolean isReadOnly() {
        return true;
    }
    
    /**
     * Method to Get Value fron XML
     * 
     * @param reply  //Element
     * @return List //Document List
     */
    public static List listFromXML(Element reply) {
        List<String[]> list = new ArrayList<String[]>();
        if (!T_SAVE_OUTPUT_FORMAT.equals(reply.getNodeName())) {
            return null;
        }

        int count = Integer.parseInt(reply.getAttribute(A_COUNT));
        String columns[] = new String[count];

        Node child = reply.getFirstChild();
        while (child != null) {
            if (child.getNodeType() == Node.ELEMENT_NODE) {
                String childName = child.getNodeName();
                assert T_ROW.equals(childName) || T_HEADING.equals(childName);
                NodeList items = ((Element) child).getElementsByTagName(T_COLUMN);
                assert items.getLength() == count;
                String[] newRow = new String[count];
                for (int j = 0; j < count; j++) {
                    if ("YES".equals(((Element) items.item(j)).getAttribute(A_IS_NULL))) {
                        newRow[j] = null;
                    } else {
                        newRow[j] = XmlUtil.getTextFromNode(items.item(j));
                    }
                }
                if (T_ROW.equals(childName)) {
                    list.add(newRow);
                }
            }
            child = child.getNextSibling();
        }
        return list;
    }
}
