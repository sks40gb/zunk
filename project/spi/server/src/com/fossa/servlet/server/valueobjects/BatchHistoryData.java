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
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Date;
import org.apache.log4j.Logger;

/**
 *
 * @author anurag
 */
/**
 * class used to make an batch  entry in history table whenever an
 * import is happening
 */
public class BatchHistoryData
{

   private int history_batch_id;
   private int batch_id;
   private int volume_id;
   private int lft;  
   private int rgt;
   private int batch_number;
   private String status;
   private int priority; 
   private int active_group;
   private int group_level;  
   private String treatment_level;  //Batch belongs to L1 or L2
   private int sub_process;
   private static Logger logger = Logger.getLogger("com.fossa.servlet.server.valueobjects");

   public BatchHistoryData()
   {
   }

   public BatchHistoryData(Connection con, int batch_id)
   {
      init(con, batch_id);
   }

   
   
   
   public int getActive_group()
   {
      return active_group;
   }

   public void setActive_group(int active_group)
   {
      this.active_group = active_group;
   }

   public int getBatch_id()
   {
      return batch_id;
   }

   public void setBatch_id(int batch_id)
   {
      this.batch_id = batch_id;
   }

   public int getBatch_number()
   {
      return batch_number;
   }

   public void setBatch_number(int batch_number)
   {
      this.batch_number = batch_number;
   }

   public int getGroup_level()
   {
      return group_level;
   }

   public void setGroup_level(int group_level)
   {
      this.group_level = group_level;
   }

   public int getHistory_batch_id()
   {
      return history_batch_id;
   }

   public void setHistory_batch_id(int history_batch_id)
   {
      this.history_batch_id = history_batch_id;
   }

   public int getLft()
   {
      return lft;
   }

   public void setLft(int lft)
   {
      this.lft = lft;
   }

   public int getPriority()
   {
      return priority;
   }

   public void setPriority(int priority)
   {
      this.priority = priority;
   }

   public int getRgt()
   {
      return rgt;
   }

   public void setRgt(int rgt)
   {
      this.rgt = rgt;
   }

   public String getStatus()
   {
      return status;
   }

   public void setStatus(String status)
   {
      this.status = status;
   }

   public String getTreatment_level()
   {
      return treatment_level;
   }

   public void setTreatment_level(String treatment_level)
   {
      this.treatment_level = treatment_level;
   }

   public int getVolume_id()
   {
      return volume_id;
   }

   public void setVolume_id(int volume_id)
   {
      this.volume_id = volume_id;
   }
   
   
   public void init(Connection con, int batchId)
   {
      try {
          
         String sql = "SELECT " +
                 "batch_id, " +
                 "volume_id, " +
                 "lft, " +
                 "rgt, " +
                 "batch_number, " +
                 "status, " +
                 "priority, " +
                 "active_group, " +
                 "group_level, " +
                 "treatment_level "+                
                 " FROM batch WHERE batch_id = ? ";

         PreparedStatement ps = con.prepareStatement(sql);
         ps.setInt(1, batchId);
         ResultSet rs = ps.executeQuery();
         if (rs.next()) {
            batch_id = rs.getInt(1);
            volume_id = rs.getInt(2);
            lft = rs.getInt(3);
            rgt = rs.getInt(4);
            batch_number = rs.getInt(5);
            status = rs.getString(6);
            priority = rs.getInt(7);
            active_group = rs.getInt(8);
            group_level = rs.getInt(9);
            treatment_level = rs.getString(10);
         }
      } catch (Exception e) {
         logger.error("Exception while getting the batch details." + e);
         StringWriter sw = new StringWriter();
         e.printStackTrace(new PrintWriter(sw));
         logger.error(sw.toString());
      }      
   }
   
   
   public void insertIntoHistoryTable(Connection con, int h_user_id, String mode)
   {
      try {
          
          ResultSet getSubProcess = null;
          Statement st = con.createStatement();
//          if(status.equals("ModifyErrors")){
//                    getSubProcess = st.executeQuery("select sub_process from batch where batch_id="+batch_id);
//                
//                 if(getSubProcess.next()){
//                 sub_process = getSubProcess.getInt(1);                     
//                 }else{
//                  sub_process = 0;
//                 }
//                }
         String sql = "INSERT INTO history_batch (" +
                 "batch_id, " +
                 "volume_id, " +
                 "lft, " +
                 "rgt, " +
                 "batch_number, " +
                 "status, " +
                 "priority, " +
                 "active_group, " +
                 "group_level, " +
                 "treatment_level," +
                 "h_user_id," +
                 "mode," +
                 "date" +
                // "sub_process " +
                 ") VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?)";

         PreparedStatement ps = con.prepareStatement(sql);
         ps.setInt(1, batch_id);
         ps.setInt(2, volume_id);
         ps.setInt(3, lft);
         ps.setInt(4, rgt);
         ps.setInt(5, batch_number);
         ps.setString(6, status);
         ps.setInt(7, priority);
         ps.setInt(8, active_group);
         ps.setInt(9, group_level);
         ps.setString(10, treatment_level);
         ps.setInt(11, h_user_id);
         ps.setString(12, mode);
         ps.setTimestamp(13, new Timestamp(new Date().getTime()));
         //ps.setInt(14, sub_process); 
         ps.executeUpdate();
         ps.close();
      } catch (Exception e) {
         logger.error("Exception while inserting into batch history." + e);
         StringWriter sw = new StringWriter();
         e.printStackTrace(new PrintWriter(sw));
         logger.error(sw.toString());
      }
   }
    
}

