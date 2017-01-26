/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.fossa.servlet.dbload;

import com.fossa.servlet.common.CommandLine;
import com.fossa.servlet.common.Log;
import com.fossa.servlet.common.PopulateData;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.apache.log4j.Logger;

/**
 *
 * @author ashish
 */
public class DiaPopulate {

    private static PopulateData data = null;

    String importFilename   = null;
    boolean debug           = false;
    static String a_codinghost     = "localhost";
    static int a_codingport        = 3306;
    static String a_codingdb       = "codingdb";
    static String a_codinguser     = "dia";
    static String a_codingpwd      = "dia4ibase";
    
    /* HashMap of field_name & field_type for the given project */
    Map fieldType;
    ArrayList fields;

    private static String message = "ok";
    String[] args;
    private static Logger logger = Logger.getLogger("com.fossa.servlet.dbload");

    public DiaPopulate (PopulateData data) {
        Log.print("(DiaImportData)");
        this.data = data;
        message = "";
    }

   /* public static void main (String args[]) {
	loadParameters(args); // loads data
        DiaPopulate load = new DiaPopulate(data);
        load.run();
    }*/
    
    public void setDatabase(String database) {
        a_codingdb = database;
    }

  public void run() {
        Log.print("Start DiaImportData");
        
        // read and parse data
        if (data.dataFilename != null ) {
            HashMap map;

            BrsReader brsReader = null;
            TxtReader reader = null;
            if (data.brs_format.equals("Yes")) {
                brsReader = new BrsReader(data, message);
            } else {
                reader = new TxtReader(data, message);
            }
            try {
                DataWriter writer = new DataWriter(a_codingdb, a_codinghost
                                           , a_codingport, a_codinguser, a_codingpwd
                                           , message, data);
                fields = writer.loadFieldList(data.project_name);
                fieldType = writer.loadFieldType(data.project_name);
                Map rowMap = null;
                if (reader != null) {
                    rowMap = reader.read(data.dataFilename, fields);
                } else {
                    rowMap = brsReader.read(data.dataFilename, fields);
                }
                writer.write(rowMap, data.volume_name, data.force.equals("Yes") ? true : false
                             , fieldType, message);
            } catch (Exception e) {
                 logger.error("Exception during DiaPopulate run() execution" + e);
                 StringWriter sw = new StringWriter();
                 e.printStackTrace(new PrintWriter(sw));
                 logger.error(sw.toString());
                 message = "DB Error: "+e;
                 return;
            }
            Log.print( "Successful Import Data");
        }
        Log.print("Exit DiaImportData");
    }

    public String getStats() {
        if (message.equals("")) {
            message = "ok";
        }
        return message;
    }

   private static boolean loadParameters( String args[]) {
   
       String commandLineSpec = "[--log] [--debug] [--replace ] "+
	                        " {-p | --project [=] } value " +
				" [{--codingdb [=]} value] [{--codingport [=]} value] [{--codinguser [=]} value] " +
				" [{--codingpwd [=]} value] " +
				" [{--codinghost [=]} value] " +
                                " {-v | --volume [=]} value [{ -d | --datafile  [=] } datafile]";

       CommandLine cl = new CommandLine(commandLineSpec);


      if (!cl.parse(args) ) {
	  return false;
      } else {
         String cmd_buffer = new String();
         cmd_buffer = "";
     
         // check for no arguments
         int ii = args.length;
         if (ii > 0) {
  	 int jj = 0;
  	 while (ii > jj) {
  	     cmd_buffer = cmd_buffer+args[jj];
  	     cmd_buffer = cmd_buffer+"  ";
  	     jj++;
  
  	  }
  
         } else {

           Log.write ("Syntax Error:  DiaLoad missing arguments");
           Log.write ("   Correct syntax:\n" );
           Log.write ("     DiaLoad "+commandLineSpec);
           Log.write ("Command Line Error - proper syntax: "+commandLineSpec ); 

  	   System.out.println("Syntax Error:  DiaLoad missing arguments");
  	   System.out.println("   Correct syntax:\n");
  	   System.out.println("     DiaLoad "+commandLineSpec);
	   System.out.println("Command Line Error - proper syntax: "+commandLineSpec );
  	   return false;
         }
  
  
         if (cl.getOption("d") != null) {
  	   data.dataFilename = cl.getOption("d");
         }
  
         if (cl.getOption("p") != null) {
  	   data.project_name = cl.getOption("p");
         }
  
         if (cl.getOption("v") != null) {
  	   data.volume_name = cl.getOption("v");
  
         }
  
         if (cl.getOption("replace") != null) {
  	   data.force = "Yes";
         } else {
             data.force = "No";
         }

	 if (data.dataFilename == null) {
	     Log.write ("Syntax Error:  Input file name required"); 
	     System.out.println("Syntax Error:  Input file name required");
	     return false;
	 }


         if (cl.getOption("codinghost") != null) {
  	   a_codinghost = cl.getOption("codinghost");
         }

         if (cl.getOption("codinguser") != null) {
  	   a_codinguser = cl.getOption("codinguser");
         }

         if (cl.getOption("codingpwd") != null) {
  	   a_codingpwd = cl.getOption("codingpwd");
         }

         if (cl.getOption("codingdb") != null) {
  	   a_codingdb = cl.getOption("codingdb");
         }

         if (cl.getOption("codingport") != null) {
	     try {

	       Integer i_codingport = new Integer(cl.getOption("codingport")); 
	       a_codingport = i_codingport.intValue(); 
	     } catch ( NumberFormatException e ) {
		 Log.write("Invalid Coding Port:"+cl.getOption("codingport")+" must be numeric");
		 logger.error("Exception while getting coding port" + e);
                 StringWriter sw = new StringWriter();
                 e.printStackTrace(new PrintWriter(sw));
                 logger.error(sw.toString());
		 return false;
	     }
         }

         Log.write("Cmd Buffer: "+cmd_buffer);
  	 Log.write("project name: "+data.project_name);
  	 Log.write("volume name: "+data.volume_name);
  
	 return true;
       }
   
   }

}
