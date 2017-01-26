/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fossa.servlet;

import com.fossa.servlet.command.Command_export_data;
import java.io.IOException;
import javax.servlet.GenericServlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import org.quartz.CronExpression;
import org.quartz.CronTrigger;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerFactory;
import org.quartz.impl.StdSchedulerFactory;

/**
 *
 * @author balab
 */
public class SchedulerServlet extends GenericServlet {

    /** 
     * Constant to represent property for the cron expression. 
     */
// private static final String CRON_EXPRESSION = "0 0/15 * * * ?";  

 private static final String CRON_EXPRESSION = "0 0/2 * * * ?";  
   
 public void init(ServletConfig servletConfig) throws ServletException {  
   
 super.init(servletConfig);  
   ServletContext con = getServletContext();
 // System.out.println("  con.getRealPath  "+  con.getRealPath("/"));
 // The Quartz Scheduler  
 Scheduler scheduler = null;  
  
        try {
            Command_export_data.setContextPath(con.getRealPath("/"));
            // Initiate a Schedule Factory  
            SchedulerFactory schedulerFactory = new StdSchedulerFactory();
            // Retrieve a scheduler from schedule factory  
            scheduler = schedulerFactory.getScheduler();
            // Initiate JobDetail with job name, job group and  
            // executable job class  
            JobDetail jobDetail = new JobDetail("RetryJob",
                    "RetryGroup", Command_export_data.class);
            // Initiate CronTrigger with its name and group name  
            CronTrigger cronTrigger = new CronTrigger("cronTrigger",
                    "triggerGroup");
            // setup CronExpression  
            CronExpression cexp = new CronExpression(CRON_EXPRESSION);
            // Assign the CronExpression to CronTrigger  
            cronTrigger.setCronExpression(cexp);
            // schedule a job with JobDetail and Trigger  
            scheduler.scheduleJob(jobDetail, cronTrigger);

            // start the scheduler  
            scheduler.start();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void service(ServletRequest serveletRequest, ServletResponse servletResponse)
            throws ServletException, IOException {
    }    

  
}  
