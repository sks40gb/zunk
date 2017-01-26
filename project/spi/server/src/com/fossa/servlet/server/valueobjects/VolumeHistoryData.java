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
public class VolumeHistoryData
{  
   private int history_volume_id;   
   private int volume_id;
   private String volume_name;
   private String original_volume_name;
   private int project_id;
   private int sequence;
   private int lft;
   private int rgt;
   private String image_path;
   private Timestamp vol_received_date;
   private Timestamp vol_start_date;  
   private Timestamp vol_completed_date;     
   private Timestamp vol_Date_Xmitted;   
   private String subcon ;
  
   private static Logger logger = Logger.getLogger("com.fossa.servlet.server.valueobjects");
   
   public VolumeHistoryData()
   {
   }

   public VolumeHistoryData(Connection con, int volume_id)
   {
      init(con, volume_id);
   }

   public int getHistory_volume_id()
   {
      return history_volume_id;
   }

   public void setHistory_volume_id(int history_volume_id)
   {
      this.history_volume_id = history_volume_id;
   }

   public String getImage_path()
   {
      return image_path;
   }

   public void setImage_path(String image_path)
   {
      this.image_path = image_path;
   }

   public int getLft()
   {
      return lft;
   }

   public void setLft(int lft)
   {
      this.lft = lft;
   }

   public String getOriginal_volume_name()
   {
      return original_volume_name;
   }

   public void setOriginal_volume_name(String original_volume_name)
   {
      this.original_volume_name = original_volume_name;
   }

   public int getProject_id()
   {
      return project_id;
   }

   public void setProject_id(int project_id)
   {
      this.project_id = project_id;
   }

   public int getRgt()
   {
      return rgt;
   }

   public void setRgt(int rgt)
   {
      this.rgt = rgt;
   }

   public int getSequence()
   {
      return sequence;
   }

   public void setSequence(int sequence)
   {
      this.sequence = sequence;
   }

   public String getSubcon()
   {
      return subcon;
   }

   public void setSubcon(String subcon)
   {
      this.subcon = subcon;
   }

   public Timestamp getVol_Date_Xmitted()
   {
      return vol_Date_Xmitted;
   }

   public void setVol_Date_Xmitted(Timestamp vol_Date_Xmitted)
   {
      this.vol_Date_Xmitted = vol_Date_Xmitted;
   }

   public Timestamp getVol_completed_date()
   {
      return vol_completed_date;
   }

   public void setVol_completed_date(Timestamp vol_completed_date)
   {
      this.vol_completed_date = vol_completed_date;
   }

   public Timestamp getVol_received_date()
   {
      return vol_received_date;
   }

   public void setVol_received_date(Timestamp vol_received_date)
   {
      this.vol_received_date = vol_received_date;
   }

   public Timestamp getVol_start_date()
   {
      return vol_start_date;
   }

   public void setVol_start_date(Timestamp vol_start_date)
   {
      this.vol_start_date = vol_start_date;
   }

   public int getVolume_id()
   {
      return volume_id;
   }

   public void setVolume_id(int volume_id)
   {
      this.volume_id = volume_id;
   }

   public String getVolume_name()
   {
      return volume_name;
   }

   public void setVolume_name(String volume_name)
   {
      this.volume_name = volume_name;
   }

   
   public void init(Connection con, int volumeId)
   {
      try {        
         String sql = "SELECT " +                 
                 "volume_id, " +
                 "volume_name, " +
                 "original_volume_name, " +
                 "project_id, " +
                 "sequence," +
                 "lft, " +
                 "rgt, " +
                 "image_path, " +
                 "vol_received_date, " +
                 "vol_start_date, " +
                 "vol_completed_date, " +
                 "vol_Date_Xmitted, " +
                 "subcon " +
                 "FROM volume WHERE volume_id = ? ";

         PreparedStatement ps = con.prepareStatement(sql);
         ps.setInt(1, volumeId);
         ResultSet rs = ps.executeQuery();
         if (rs.next()) {            
            volume_id = rs.getInt(1);
            volume_name = rs.getString(2);
            original_volume_name = rs.getString(3);
            project_id = rs.getInt(4);
            sequence = rs.getInt(5);
            lft = rs.getInt(6);
            rgt = rs.getInt(7);
            image_path = rs.getString(8);
            vol_received_date = rs.getTimestamp(9);
            vol_start_date = rs.getTimestamp(10);
            vol_completed_date = rs.getTimestamp(11);
            vol_Date_Xmitted = rs.getTimestamp(12);
            subcon = rs.getString(13);
         }
      } catch (Exception e) {
         logger.error("Exception while getting the volume details in Volume History." + e);
         StringWriter sw = new StringWriter();
         e.printStackTrace(new PrintWriter(sw));
         logger.error(sw.toString());
      }      
   }
   
   
   public void insertIntoHistoryTable(Connection con, int h_user_id, String mode)
   {
      try {
         String sql = "INSERT INTO history_volume (" +
                 "volume_id, " +
                 "volume_name, " +
                 "original_volume_name, " +
                 "project_id, " +
                 "sequence," +
                 "lft, " +
                 "rgt, " +
                 "image_path, " +
                 "vol_received_date, " +
                 "vol_start_date, " +
                 "vol_completed_date, " +
                 "vol_Date_Xmitted, " +
                 "subcon," +
                 "users_id," +
                 "mode," +
                 "date" +
                 ") VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

         PreparedStatement ps = con.prepareStatement(sql);
         ps.setInt(1, volume_id);
         ps.setString(2,volume_name);
         ps.setString(3,original_volume_name);
         ps.setInt(4,project_id);
         ps.setInt(5, sequence);
         ps.setInt(6, lft);
         ps.setInt(7, rgt);         
         ps.setString(8,image_path);         
         ps.setTimestamp(9,vol_received_date);
         ps.setTimestamp(10,vol_start_date);
         ps.setTimestamp(11,vol_completed_date);
         ps.setTimestamp(12,vol_Date_Xmitted);
         ps.setString(13,subcon);
         ps.setInt(14,h_user_id);         
         ps.setString(15,mode);
         ps.setTimestamp(16, new Timestamp(new Date().getTime()));

         ps.executeUpdate();
         ps.close();
      } catch (Exception e) {
         logger.error("Exception while saving the volume details in Volume History." + e);
         StringWriter sw = new StringWriter();
         e.printStackTrace(new PrintWriter(sw));
         logger.error(sw.toString());
      }
   }
    
}

