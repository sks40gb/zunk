/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fossa.servlet.command;

import com.fossa.servlet.common.CommonLogger;
import org.w3c.dom.Element;
import com.fossa.servlet.dao.DBTask;
import com.fossa.servlet.common.msg.MessageWriter;
import com.fossa.servlet.session.UserTask;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * This class returns all validation functions records for a field.
 * @author sunil
 */
public class Command_request_ouput_format implements Command {


    private int volumeId;

    public String execute(Element action, UserTask user, DBTask dbTask, MessageWriter writer) {
        try {
            volumeId = Integer.parseInt(action.getAttribute(A_VOLUME_ID));

            Connection con = dbTask.getConnection();
            Statement st = dbTask.getStatement();
            PreparedStatement ps = null;
            String query;     
            
            query = "select volume_id FROM ovp_sequence WHERE volume_id = ? ";
            ps = con.prepareStatement(query);
            ps.setInt(1, volumeId);
            ps.executeQuery();
            ResultSet rs = ps.getResultSet();
           
            if(rs.next()){
                
                 query = "select " +
                            "OS.projectfields_id, " +
                            "OS.ovp_field_id," +
                            "coalesce(OVP.field_name,PF.field_name) as field_name, " +
                            "OS.separator," +
                            "OS.sequence," +
                            "OS.format, " +
                            "OS.suppress," +
                            "OS.type " +
                         "FROM ovp_sequence OS " +
                         "LEFT OUTER JOIN projectfields PF ON OS.projectfields_id = PF.projectfields_id  " +
                         "LEFT OUTER JOIN ovp_field OVP ON OVP.ovp_field_id = OS.ovp_field_id  " +
                         "WHERE volume_id = ? ORDER BY OS.sequence";
            }else{
                query = "SELECT " +
                            "projectfields_id, " +
                            "null, " +
                            "field_name, " +
                            "'|', " +
                            "sequence, " +
                            "null, " +
                            "'false', " +
                            "'PROJECT_FIELD' " +
                         "FROM projectfields " +
                         "WHERE project_id = " +
                                 "(SELECT project_id FROM volume WHERE volume_id = ?) " +
                         "ORDER BY sequence";
            }
            
           
            ps = con.prepareStatement(query);
            ps.setInt(1, volumeId);
            ps.executeQuery();
            rs = ps.getResultSet();            
            
            Command_sql_query.writeXmlFromResult(user, rs, writer);
            rs.close();

        } catch (Exception exc) {
            CommonLogger.printExceptions(this, "Exception while requesting output format data.", exc);
        }
        return null;
    }

    public boolean isReadOnly() {
        return true;
    }
}

