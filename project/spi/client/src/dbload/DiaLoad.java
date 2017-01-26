/* $Header: /home/common/cvsarea/ibase/dia/src/dbload/DiaLoad.java,v 1.17.2.1 2006/08/23 19:04:52 nancy Exp $ */
//package dbload;

package dbload;


import dbload.XrefFileReader.XrefException;
import dbload.DbWriter.DbWriterException;
import common.Log;


public class DiaLoad implements XrefConstants {


    String importfilename   = null;
    boolean debug           = false;
    boolean log             = false;
    String a_project_name   = "";
    String a_volume_name    = "";
    String a_image_path     = "";
    int a_format_type       = 0;
    String a_codinghost     = "localhost";
    int a_codingport        = 3306;
    String a_codingdb       = "codingdb";
    int a_batchspan         = 2500;
    int a_span_search       = -1;
    double default_search      = 0.2;
    String a_codinguser     = "dia";
    String a_codingpwd      = "dia4ibase";
    boolean a_replace       = false;
    boolean a_append        = false;
    boolean a_split         = false;

    String a_format_string  = "";
    

    private static String message = "ok";
    String[] args;

    public DiaLoad (String args[]) {
        //Log.print("(DiaLoad)");
        this.args = args;
        message = "";
    }

//    public static void main (String args[]) {
//        DiaLoad load = new DiaLoad(args);
//        load.run();
//    }

    public void run() {
	//DiaLoad dia = new DiaLoad();
	boolean xrefError = false;

        for (int i = 0; i < args.length; i++) {
            Log.print("(DiaLoad) arg " + args[i]);
        }
	loadParameters( args );
	//if (dia.loadParameters( args )) {
                       
        //Log.print("Start DiaLoad");
        
        // Build XREF Data
        if (importfilename != null ) {
            
            Log.print("Creating XREF table entries");

            try {
                Xref[] result =  XrefFileReader.read(importfilename, a_format_type);
                if (result == null) {
                    // no input data
                    message = "Error in input file or filename.";
                    return;
                }
                for (int i = 0; i < result.length; i++) {
                    Xref theXref = result[i];

                    // Debug print only
                    if (theXref instanceof ImageXref) {
                        ImageXref data = (ImageXref) result[i];
                        if (debug) Log.write(data.bates+"||"+data.boundary+"||"+data.offset+"||"+data.volume+"||"+data.path+"||"+data.fileName+"||"+data.fileType+"||"+data.rotation+"||");
                
                    } else { 
                        IssueXref data = (IssueXref) result[i];
                        if (debug) Log.write(data.bates+"||"+data.issueName);
                    }
                }
        
                try {
        
                    DbWriter db = new DbWriter(a_codingdb, a_codinghost
                                               , a_codingport, a_codinguser, a_codingpwd
                                               , a_replace, a_append, a_split, message);
                    if ( debug ) db.debugOn();
                    if ( log ) db.loggingOn();
                    
                    
                    db.write( result, a_project_name, a_volume_name
                              , a_batchspan, a_span_search, a_image_path);
                } catch (DbWriterException e) {
                     System.out.println("DB Error: "+e); 
                     if (debug | log ) Log.write("DB Error: "+e);
                     message = "DB Error: "+e;
                     return;
                }
            } catch (XrefException e) {
                String[] errors = e.getErrors();
                xrefError = true;
                for (int i = 0; i < errors.length; i++) {
                    System.out.println(i+": ERROR: "+errors[i]); 
                    if (debug | log) Log.write(i+": ERROR: "+errors[i]);
                    message = message + "\n" + (i+1) +": ERROR: "+errors[i];
                    return;
                }
            }
            Log.print( "Successful XREF Creation");
        } else {
            Log.print( "No XREF Creation Selected");
        }
        //}
        Log.print("Exit DiaLoad");
    }

    public String getStats() {
        if (message.equals("")) {
            message = "ok";
        }
        return message;
    }

   private boolean loadParameters( String args[]) {
   
       String commandLineSpec = "[--log] [--debug] [--replace ] [--append] [--split ]"+
	                        " [ {-i | --imagepath [=] }  value ]  {-p | --project [=] } value " +
				" [{--codingdb [=]} value] [{--codingport [=]} value] [{--codinguser [=]} value] " +
				" [{--codingpwd [=]} value] " +
				" [{--codinghost [=]} value] " +
                                " [ {-b | --batchspan [=] }  value ] " +
                                " [ {-s | --spansearch [=] }  value ] " +
				" {-v | --volume [=]} value {-t | --type [=]} value [{ -x | --xref  [=] } importfile]";

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

           if (this.debug || this.log) Log.write ("Syntax Error:  DiaLoad missing arguments");
           if (this.debug || this.log) Log.write ("   Correct syntax:\n" );
           if (this.debug || this.log) Log.write ("     DiaLoad "+commandLineSpec);
           if (this.debug || this.log) Log.write ("Command Line Error - proper syntax: "+commandLineSpec ); 

  	   System.out.println("Syntax Error:  DiaLoad missing arguments");
  	   System.out.println("   Correct syntax:\n");
  	   System.out.println("     DiaLoad "+commandLineSpec);
	   System.out.println("Command Line Error - proper syntax: "+commandLineSpec );
  	   return false;
         }
  
  
  
  
  
         if (cl.getOption("x") != null) {
	     this.importfilename = cl.getOption("x");
	 }
  
  
         if (cl.getOption("p") != null) {
  	   this.a_project_name = cl.getOption("p");
         }
  
         if (cl.getOption("v") != null) {
  	   this.a_volume_name = cl.getOption("v");
  
         }
  

         if (cl.getOption("replace") != null) {
  	   this.a_replace = true;
  
         }

         if (cl.getOption("split") != null) {
  	   this.a_split = true;
         }

         if (cl.getOption("append") != null) {
  	   this.a_append = true;
  
         }

	 if (this.a_replace && this.a_append) {

	     Log.write("Append and Replace incompatible when used together");
	     System.out.println("Append and Replace incompatible when used together");
	     return false;
	 }



         if (cl.getOption("t") != null) {
	   Log.write( "Option:"+cl.getOption("t") );
  	   if ("LFP".equals ( cl.getOption("t") )) {
               // allow LFP for historical reasons
  	       this.a_format_type = LFP;
  	   } else {
  	       for (int i = 1; i < TYPE_NAMES.length; i++) {
                   if (TYPE_NAMES[i].equals(cl.getOption("t"))) {
                       this.a_format_type = i;
                       break;
                   }
               }
  	   }
  
         }
  
         if (cl.getOption("i") != null) {
  	   this.a_image_path = cl.getOption("i");
         }
  

         // Number of pages to put in a batch.  Try to start each batch with a document.
         if (cl.getOption("b") != null) {
	     try {
	       Integer i_batchspan = new Integer(cl.getOption("b")); 
	       this.a_batchspan = i_batchspan.intValue(); 
	     } catch ( NumberFormatException e ) {
		 Log.write("Invalid batch span value:"+cl.getOption("b")+" must be numeric");
		 System.out.println("Invalid batch span value:"+cl.getOption("b")+" must be numeric");
		 return false;
	     }

  
         }

         // Number of pages to search beyond batchspan for a document to begin the next batch.
         if (cl.getOption("s") != null) {
	     try {
	       Integer i_span_search = new Integer(cl.getOption("s")); 
	       this.a_span_search = i_span_search.intValue(); 
	     } catch ( NumberFormatException e ) {
		 Log.write("Invalid span search value:"+cl.getOption("s")+" must be numeric");
		 System.out.println("Invalid span search value:"+cl.getOption("s")+" must be numeric");
		 return false;
	     }

  
         }

         if (cl.getOption("debug") != null) {
  	    this.debug = true;
  
         }
  
         if (cl.getOption("log") != null) {
  	    this.log = true;
         }
  
	 if (this.importfilename == null) {
	     if (this.debug || this.log) Log.write ("Syntax Error:  Import file name required"); 
	     System.out.println("Syntax Error:  Import file name required");
	     return false;
	 }


         if (cl.getOption("codinghost") != null) {
  	   this.a_codinghost = cl.getOption("codinghost");
         }

         if (cl.getOption("codinguser") != null) {
  	   this.a_codinguser = cl.getOption("codinguser");
         }

         if (cl.getOption("codingpwd") != null) {
  	   this.a_codingpwd = cl.getOption("codingpwd");
         }

         if (cl.getOption("codingdb") != null) {
  	   this.a_codingdb = cl.getOption("codingdb");
         }

         if (cl.getOption("codingport") != null) {
  	   //this.a_imageport = (int) cl.getOption("codingport");
	     try {

	       Integer i_codingport = new Integer(cl.getOption("codingport")); 
	       this.a_codingport = i_codingport.intValue(); 
	     } catch ( NumberFormatException e ) {
		 Log.write("Invalid Coding Port:"+cl.getOption("codingport")+" must be numeric");
		 System.out.println("Invalid Coding Port:"+cl.getOption("codingport")+" must be numeric"); 
		 return false;
	     }
         }

         if (a_span_search > a_batchspan) {
             Log.print("Warning: spansearch is greater than batchspan: "+a_span_search
                       +" > "+a_batchspan);
         }
         if (a_batchspan > 0
             && a_span_search < 0) {
             // no spansearch entered.  default to 20%
             a_span_search = (int) (a_batchspan * default_search);
             Log.print("Warning: spansearch is defaulting to "+a_span_search);

         }

         if (this.log) {
  	   Log.write("Cmd Buffer: "+cmd_buffer);
  	   Log.write("project name: "+this.a_project_name);
  	   Log.write("volume name: "+this.a_volume_name);
  	   Log.write("format type: "+this.a_format_string);
         }
  
	 return true;
       }
   
   }

}
