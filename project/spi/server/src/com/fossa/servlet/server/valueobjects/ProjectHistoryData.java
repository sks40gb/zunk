/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fossa.servlet.server.valueobjects;

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
 * @author anurag
 */
public class ProjectHistoryData
{  
   private int history_project_id;   
   private int project_id;
   private int active;
   private String project_name;
   private String level_field_name;
   private int high_coding_batch;
   private int high_unitize_batch;
   private int high_volume;
   private int lft;
   private int rgt;
   private int split_documents;     
   
   private static Logger logger = Logger.getLogger("com.fossa.servlet.server.valueobjects");
   public ProjectHistoryData()
   {
   }

   public ProjectHistoryData(Connection con, int projectId)
   {
      init(con, projectId);
   }

   public int getActive()
   {
      return active;
   }

   public void setActive(int active)
   {
      this.active = active;
   }

   public int getHigh_coding_batch()
   {
      return high_coding_batch;
   }

   public void setHigh_coding_batch(int high_coding_batch)
   {
      this.high_coding_batch = high_coding_batch;
   }

   public int getHigh_unitize_batch()
   {
      return high_unitize_batch;
   }

   public void setHigh_unitize_batch(int high_unitize_batch)
   {
      this.high_unitize_batch = high_unitize_batch;
   }

   public int getHigh_volume()
   {
      return high_volume;
   }

   public void setHigh_volume(int high_volume)
   {
      this.high_volume = high_volume;
   }

   public int getHistory_project_id()
   {
      return history_project_id;
   }

   public void setHistory_project_id(int history_project_id)
   {
      this.history_project_id = history_project_id;
   }

   public String getLevel_field_name()
   {
      return level_field_name;
   }

   public void setLevel_field_name(String level_field_name)
   {
      this.level_field_name = level_field_name;
   }

   public int getLft()
   {
      return lft;
   }

   public void setLft(int lft)
   {
      this.lft = lft;
   }

   public int getProject_id()
   {
      return project_id;
   }

   public void setProject_id(int project_id)
   {
      this.project_id = project_id;
   }

   public String getProject_name()
   {
      return project_name;
   }

   public void setProject_name(String project_name)
   {
      this.project_name = project_name;
   }

   public int getRgt()
   {
      return rgt;
   }

   public void setRgt(int rgt)
   {
      this.rgt = rgt;
   }

   public int getSplit_documents()
   {
      return split_documents;
   }

   public void setSplit_documents(int split_documents)
   {
      this.split_documents = split_documents;
   }

   

   
   public void init(Connection con, int projectId)
   {
      try {        
         String sql = "SELECT " +                 
                 "project_id, " +
                 "active, " +
                 "project_name, " +
                 "level_field_name, " +
                 "high_coding_batch," +
                 "high_unitize_batch, " +
                 "high_volume, " +
                 "lft, " +
                 "rgt, " +
                 "split_documents " +                 
                 "FROM project WHERE project_id = ? ";

         PreparedStatement ps = con.prepareStatement(sql);
         ps.setInt(1, projectId);
         ResultSet rs = ps.executeQuery();
         if (rs.next()) {            
            project_id = rs.getInt(1);
            active = rs.getInt(2);
            project_name = rs.getString(3);
            level_field_name = rs.getString(4);
            high_coding_batch = rs.getInt(5);
            high_unitize_batch = rs.getInt(6);
            high_volume = rs.getInt(7);
            lft = rs.getInt(8);
            rgt = rs.getInt(9);
            split_documents = rs.getInt(10);            
         }
      } catch (Exception e) {
         logger.error("Exception while getting the project details in Project Histroy." + e);
         StringWriter sw = new StringWriter();
         e.printStackTrace(new PrintWriter(sw));
         logger.error(sw.toString());
      }      
   }
   
   
   public void insertIntoHistoryTable(Connection con, int h_user_id, String mode)
   {
      try {
         String sql = "INSERT INTO history_project (" +
                "project_id, " +
                 "active, " +
                 "project_name, " +
                 "level_field_name, " +
                 "high_coding_batch," +
                 "high_unitize_batch, " +
                 "high_volume, " +
                 "lft, " +
                 "rgt, " +
                 "split_documents," + 
                 "users_id," +
                 "mode," +
                 "date" +
                 ") VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?)";

         PreparedStatement ps = con.prepareStatement(sql);
         ps.setInt(1,project_id);
         ps.setInt(2,active);
         ps.setString(3,project_name);
         ps.setString(4,level_field_name);
         ps.setInt(5,high_coding_batch);
         ps.setInt(6,high_unitize_batch);
         ps.setInt(7,high_volume);
         ps.setInt(8,lft);
         ps.setInt(9,rgt);
         ps.setInt(10,split_documents);
         ps.setInt(11,h_user_id);
         ps.setString(12,mode);
         ps.setTimestamp(13,new Timestamp(new Date().getTime()));

         ps.executeUpdate();
         ps.close();
      } catch (Exception e) {
         logger.error("Exception while saving the project details in Project Histroy." + e);
         StringWriter sw = new StringWriter();
         e.printStackTrace(new PrintWriter(sw));
         logger.error(sw.toString());
      }
   }
    
}

