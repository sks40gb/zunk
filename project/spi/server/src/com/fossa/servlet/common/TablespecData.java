/*
 * TablespecData.java
 *
 * Created on November 21, 2007, 2:13 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.fossa.servlet.common;

import com.fossa.servlet.command.Mode;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.Date;
import org.apache.log4j.Logger;

/**
 *
 * @author Bala
 */
public class TablespecData {
    
    /** id of the tablespec row, or 0 for insert */
    public int tablespec_id;

    /** name of the table */
    public String table_name;

    /** type of tablespec: Name or Text */
    public String table_type;

    /** id of the project the tablespec belongs to, or 0 for Global */
    public int project_id;

    /** is use of the table mandatory: Optional or Required */
    public String requirement;

    /** who may update the table: CoderAdd or SuperMod */
    public String updateable;

    /** the list of values shown for this table is based on values from this table */
    public int model_tablespec_id;
    
    private static Logger logger = Logger.getLogger("com.fossa.servlet.common");
   
   /** Method used to Insert table data  into history table*/
    public void insertIntoHistoryTable(Connection con, int userId, String mode) {
        try {
            
            //if mode is delete then get the record and put in history.
            if(mode.equals(Mode.DELETE)){                
                getRecord(con);
            }
            
            String sql = "INSERT INTO history_tablespec (" +
                    "tablespec_id, " +
                    "table_name, " +
                    "table_type, " +
                    "project_id, " +
                    "requirement, " +
                    "updateable, " +
                    "model_tablespec_id, " +
                    "h_users_id, " +
                    "mode, " +
                    "date" +
                    ") VALUES (?,?,?,?,?,?,?,?,?,?)";
            
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, tablespec_id);
            ps.setString(2, table_name);
            ps.setString(3, table_type);
            ps.setInt(4, project_id);
            ps.setString(5, requirement);
            ps.setString(6, updateable);
            ps.setInt(7, model_tablespec_id);    
            ps.setInt(8, userId);
            ps.setString(9, mode);
            ps.setTimestamp(10, new Timestamp(new Date().getTime()));
            ps.executeUpdate();
            ps.close();
        } catch (Exception e) {
            logger.error("Exception while saving history for table spec." + e);
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            logger.error(sw.toString());
        }
    }
    
    /** Get all the table data for particular table*/
    public void getRecord(Connection con) {
        try{
            
             String sql = "SELECT " +
                    "tablespec_id, " +
                    "table_name, " +
                    "table_type, " +
                    "project_id, " +
                    "requirement, " +
                    "updateable, " +
                    "model_tablespec_id " +                    
                    "FROM tablespec WHERE tablespec_id = ? ";                    
            
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setInt(1, tablespec_id);            
            pst.executeQuery();            
            ResultSet rs = pst.getResultSet();
            if (rs.next()) {
                tablespec_id = rs.getInt(1);
                table_name = rs.getString(2);
                table_type = rs.getString(3);
                project_id = rs.getInt(4);
                requirement = rs.getString(5);
                updateable = rs.getString(6);
                model_tablespec_id = rs.getInt(6);                 
            }
         
        }catch(Exception e){
            logger.error("Exception while fetching records for table spec." + e);
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            logger.error(sw.toString());
        }
    }
    
    
}
