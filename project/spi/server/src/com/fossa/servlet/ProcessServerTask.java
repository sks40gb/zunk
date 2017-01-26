/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.fossa.servlet;

import com.fossa.servlet.command.Command_export_data;
import com.fossa.servlet.dao.DBTask;
import com.fossa.servlet.session.UserTask;
import com.fossa.servlet.threadpool.ThreadPool;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServlet;

/**
 *
 * @author balab
 */
public class ProcessServerTask extends HttpServlet{
    Connection con = null;       
    private static Logger logger = Logger.getLogger("com.fossa.servlet");
    
    /**
     * This method is invoked when this class is initiliazed.
     */
    @Override
    public void init() {
        System.out.println("aaaaaaaaaaaaaaaaa");
        DBTask dbTask = new DBTask();
        UserTask task = new UserTask();
        //dbTask.setAutoCommit(false); //The commit is done inside table.load(con) method
        con = dbTask.getConnection();    
       // for(;;){
             createThreadPool(dbTask,task);
       //      try {
                 System.out.println("ccccccccccccccccc");
       //         Thread.sleep(100000);
      //      } catch (InterruptedException e) {
      //      }
      // }
            
    }
  
    private void createThreadPool(DBTask dbTask,UserTask task){
        try {
            System.out.println("bbbbbbbbbbbbbbbbbbbb");
            ThreadPool pool = new ThreadPool(5);
            Statement getQueueTask = dbTask.getStatement();
            ResultSet rs = getQueueTask.executeQuery("select * from servertaskqueue where status = 'progress'");
            int count = 0;
            while (rs.next()) {
                int servertaskqueue_id = rs.getInt(1);
                count++;
//                if (count <= 2) {
//                    pool.assign(new Command_export_data(servertaskqueue_id,task));                    
//                    pool.complete();
//                    
//                    PreparedStatement updateServerTaskQueueStatus = con.prepareStatement("Update servertaskqueue set status = ? where servertaskqueue_id = ?");
//                    updateServerTaskQueueStatus.setString(1, "complete");
//                    updateServerTaskQueueStatus.setInt(2, servertaskqueue_id);
//                    updateServerTaskQueueStatus.executeUpdate();
//                    if(count == 2){
//                     break;
//                    }
//                } 
            }
        } catch (SQLException ex) {
            Logger.getLogger(ProcessServerTask.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
