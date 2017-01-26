/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fossa.servlet.server.valueobjects;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Timestamp;
import java.sql.Connection;
import java.sql.PreparedStatement;
import org.apache.log4j.Logger;
/**
 *
 * @author anurag
 */
/** class used to make batch status entry in the corresponding history table */

public class BatchProcessHistroyData
{
   private int history_process_id;
   private int batch_id;
   private int volume_id;           
   private String process;           
   private String is_ready;           
   private String in_use;           
   private String in_queue;           
   private Timestamp start_time;           
   private Timestamp end_time;           
   private int started_by;           
   private int ended_by;   
   private int assigned_to;           
   private Timestamp queued_time;           
   private int queued_to;           
   private Timestamp assigned_time;

   private static Logger logger = Logger.getLogger("com.fossa.servlet.server.valueobjects");
   
   public Timestamp getAssigned_time()
   {
      return assigned_time;
   }

   public void setAssigned_time(Timestamp assigned_time)
   {
      this.assigned_time = assigned_time;
   }

   public int getAssigned_to()
   {
      return assigned_to;
   }

   public void setAssigned_to(int assigned_to)
   {
      this.assigned_to = assigned_to;
   }

   public int getBatch_id()
   {
      return batch_id;
   }

   public void setBatch_id(int batch_id)
   {
      this.batch_id = batch_id;
   }

   public Timestamp getEnd_time()
   {
      return end_time;
   }

   public void setEnd_time(Timestamp end_time)
   {
      this.end_time = end_time;
   }

   public int getEnded_by()
   {
      return ended_by;
   }

   public void setEnded_by(int ended_by)
   {
      this.ended_by = ended_by;
   }

   public int getHistory_process_id()
   {
      return history_process_id;
   }

   public void setHistory_process_id(int history_process_id)
   {
      this.history_process_id = history_process_id;
   }

   public String getIn_queue()
   {
      return in_queue;
   }

   public void setIn_queue(String in_queue)
   {
      this.in_queue = in_queue;
   }

   public String getIn_use()
   {
      return in_use;
   }

   public void setIn_use(String in_use)
   {
      this.in_use = in_use;
   }

   public String getIs_ready()
   {
      return is_ready;
   }

   public void setIs_ready(String is_ready)
   {
      this.is_ready = is_ready;
   }

   public String getProcess()
   {
      return process;
   }

   public void setProcess(String process)
   {
      this.process = process;
   }

   public Timestamp getQueued_time()
   {
      return queued_time;
   }

   public void setQueued_time(Timestamp queued_time)
   {
      this.queued_time = queued_time;
   }

   public int getQueued_to()
   {
      return queued_to;
   }

   public void setQueued_to(int queued_to)
   {
      this.queued_to = queued_to;
   }

   public Timestamp getStart_time()
   {
      return start_time;
   }

   public void setStart_time(Timestamp start_time)
   {
      this.start_time = start_time;
   }

   public int getStarted_by()
   {
      return started_by;
   }

   public void setStarted_by(int started_by)
   {
      this.started_by = started_by;
   }

   public int getVolume_id()
   {
      return volume_id;
   }

   public void setVolume_id(int volume_id)
   {
      this.volume_id = volume_id;
   }
   
   public void insertIntoHistoryTable(Connection con) {
        try {   
            String sql = "INSERT INTO history_process (" +
                    "batch_id, " +
                    "volume_id, " +
                    "process, " +                    
                    "is_ready, " +
                    "in_use, " +
                    "in_queue, " +
                    "start_time, " +
                    "end_time, " +
                    "started_by, " +
                    "ended_by," +
                    "assigned_to," +
                    "queued_time," +
                    "queued_to," +
                    "assigned_time" +
                    ") VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
            
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, batch_id);
            ps.setInt(2, volume_id);
            ps.setString(3, process);            
            ps.setString(4, is_ready);
            ps.setString(5, in_use);
            ps.setString(6, in_queue);
            ps.setTimestamp(7, start_time);
            ps.setTimestamp(8, end_time);
            ps.setInt(9, started_by);
            ps.setInt(10, ended_by);
            ps.setInt(11, assigned_to);
            ps.setTimestamp(12,queued_time);
            ps.setInt(13, queued_to);
            ps.setTimestamp(14, assigned_time);
            ps.executeUpdate();
            ps.close();
        } catch (Exception e) {
            logger.error("Exception while inserting into process history." + e);
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            logger.error(sw.toString());
        }
    }
}
