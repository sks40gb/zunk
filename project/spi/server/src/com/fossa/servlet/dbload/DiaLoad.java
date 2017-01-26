/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fossa.servlet.dbload;

import com.fossa.servlet.command.Command_import_data;
import com.fossa.servlet.command.Mode;
import com.fossa.servlet.common.CommandLine;
import com.fossa.servlet.common.Log;
import com.fossa.servlet.common.SQLQueries;
import com.fossa.servlet.dao.DBTask;
import com.fossa.servlet.server.valueobjects.BatchHistoryData;
import com.fossa.servlet.server.valueobjects.BatchProcessHistroyData;
import com.fossa.servlet.session.UserTask;
import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import  org.apache.log4j.Logger;

/**
 *
 * @author ashish
 */
public class DiaLoad implements XrefConstants {

    String path = "";
    String importfilename = null;
    boolean debug = false;
    boolean log = false;
    boolean a_replace = false;
    boolean a_append = false;
    boolean a_split = false;
    String a_project_name = "";
    String a_volume_name = "";
    String a_image_path = "";
    String brs_filename = "";
    String img_filename = "";
    String txt_filename = "";
    boolean isL1Treatment = false;
    
    String isUnitizeOptional = "";
    String internal_volume = "";
    String volume_completion_date = "";
    private int rangeId = 0;
    private int childId = 0;
    private int volume_id = 0;
    private int batch_id = 0;
    String a_codinghost = "localhost";
    String a_codingdb = "codingdb";
    String a_codinguser = "dia";
    String a_codingpwd = "dia4ibase";
    int a_format_type = 0;
    int a_codingport = 3306;
    int a_batchspan = 2500;
    int a_l1_batchspan = 100;
    int a_span_search = -1;
    double default_search = 0.2;
    String a_format_string = "";
    private static String message = "ok";
    String[] args;
    String codingmanual = "";
    Connection connection;
    ResultSet rs = null;
    PreparedStatement pstmt = null;
    PreparedStatement pst = null;
    Statement st = null;
    HashMap brsRecordMap = new HashMap();
    private ArrayList l1DataList = new ArrayList();
    private static String FOLDER = "Folder";
    private static String SOURCE = "Source";
    private final String UPLOAD = "UPLOAD";    
    private static final String DTYG_NAME = "General Document Type";
    private static final String DTYG_TAG_NAME = "DTYG";
    
    public static Logger logger = Logger.getLogger("com.fossa.servlet.dbload");

    public DiaLoad(String args[], String codingmanual, DBTask dbTask, UserTask user) {        
        this.args = args;
        this.codingmanual = codingmanual;
        message = "";
    }

    public DiaLoad(String args[], String codingmanual) {        
        this.args = args;
        this.codingmanual = codingmanual;
        message = "";
    }

    public void run() {
        //DiaLoad dia = new DiaLoad();
        boolean xrefError = false;
        int coding_manual_id = 0;        
        for (int i = 0; i < args.length; i++) {
            Log.print("(DiaLoad) arg " + args[i]);
        }        
        loadParameters(args);

        // Build XREF Data       
        if (importfilename != null) {

            Log.print("Creating XREF table entries");
            try {                
                int spliting = 0;
                if(codingmanual.length() != 0){
                        if(codingmanual.startsWith("http")){
                            spliting =codingmanual.lastIndexOf("/", codingmanual.length());
                        }else{
                            spliting =codingmanual.lastIndexOf("\\", codingmanual.length());
                        }
                        String fileName = codingmanual.substring(spliting + 1, codingmanual.length());
                         System.out.println("codingmanual=========" + codingmanual); 
                        File f11= new File(codingmanual);
                        if(f11.exists()){
                            System.out.println("===========111111111");                    
                        }else{
                            System.out.println("=========222222222");
                        }


                        String replacedFileName = "";
                        int index = fileName.indexOf(".");
                        String subString = fileName.substring(++index);
                        if (subString.matches("TXT")) {
                            subString = "txt";
                            replacedFileName = fileName.replaceFirst(".TXT", ".txt");
                        } else if (subString.matches("PDF")) {
                            subString = "pdf";
                            replacedFileName = fileName.replaceFirst(".PDF", ".pdf");
                        } else if (subString.matches("DOC")) {
                            subString = "doc";
                            replacedFileName = fileName.replaceFirst(".DOC", ".doc");
                        } else {
                            replacedFileName = fileName;
                        }
                        String[] fname = replacedFileName.split(" ");
                        String fileNoSpace = "";
                        String ff = "%20";
                        for (int i = 0; i < fname.length; i++) {
                            if (i == fname.length - 1) {
                                fileNoSpace = fileNoSpace + fname[i];
                            } else {
                                fileNoSpace = fileNoSpace + fname[i] + ff;
                            }

                        }                
                        path = codingmanual.substring(0, spliting+1) + fileNoSpace;
                        if (logger.isInfoEnabled()) {
                            logger.info("file path " + path);
                        }
        //check codingManual file is there or not               
                        if (fileNoSpace.trim().length() != 0) {                    
                                connection = Command_import_data.connection;                        
                                pst = connection.prepareStatement("SELECT coding_manual_id FROM coding_manual WHERE project_name = ?");                        
                                pst.setString(1, a_project_name);
                                pst.executeQuery();
                                rs = pst.getResultSet();
                                while (rs.next()) {
                                    coding_manual_id = rs.getInt(1);
                                }
                                if (coding_manual_id == 0) {
                                    pstmt = connection.prepareStatement("INSERT INTO coding_manual (project_name,codingManual_fileName,codingManual_filepath) VALUES(?,?,?)");                            
                                    pstmt.setString(1, a_project_name);
                                    pstmt.setString(2, fileNoSpace);
                                    pstmt.setString(3, path);
                                    pstmt.executeUpdate();
                                } else {
                                    pstmt = connection.prepareStatement("UPDATE coding_manual SET project_name = ?,codingManual_fileName = ?,codingManual_filepath = ? WHERE project_name =?");                            
                                    pstmt.setString(1, a_project_name);
                                    pstmt.setString(2, fileNoSpace);
                                    pstmt.setString(3, path);
                                    pstmt.setString(4, a_project_name);
                                    pstmt.executeUpdate();
                                }
                                pstmt.close();
                                pst.close();
                            } else {
                                message = "Coding manual not found in the specified path.\n ' " + path + "'";
                                return; 
                            }
                }
                

                Xref[] result = null;
                if (a_format_type == BRS) {
                    result = XrefFileReader.read(brs_filename, img_filename, txt_filename, a_format_type);
                    l1DataList = XrefFileReader.l1List;
                    brsRecordMap = XrefFileReader.brsMap;
                } else {
                    result = XrefFileReader.read(importfilename, a_format_type);
                }

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
                        if (debug) {
                            Log.write(data.bates + "||" + data.boundary + "||" + data.offset + "||" + data.volume + "||" + data.path + "||" + data.fileName + "||" + data.fileType + "||" + data.rotation + "||");
                        }
                    } else {
                        IssueXref data = (IssueXref) result[i];
                        if (debug) {
                            Log.write(data.bates + "||" + data.issueName);
                        }
                    }
                }

                try {
                    DbWriter db = new DbWriter(a_codingdb, a_codinghost, a_codingport, a_codinguser, a_codingpwd, a_replace, a_append, a_split, message);
                    if (debug) {
                        db.debugOn();
                    }
                    if (log) {
                        db.loggingOn();
                    }
                    int contextpathlength = a_image_path.indexOf(UPLOAD);
                    String imgpath = a_image_path.substring(contextpathlength + UPLOAD.length() + 1);
                    Command_import_data.progressCount = 3;
                    
                    String tokens[] = a_image_path.split(":");
                        String type ="";
                        String ip ="";
                        String port ="";
                        String path ="";
                        String serverIP_port="";
                        
                    if(tokens.length >3){                        
                         type = tokens[0];
                         ip = tokens[1];
                         port = tokens[2];
                         
                         String matcher = ip+":"+port+":";
                         int position = a_image_path.lastIndexOf(matcher) + matcher.length();
                         path = a_image_path.substring(position);
                        serverIP_port = type+":"+ip+":"+port;
                        
                    }else{
                          type = tokens[0];
                          path = a_image_path.substring(4);
                          serverIP_port = type;                          
                    }
                    
                    
                    db.write(result, a_project_name, a_volume_name, a_batchspan, a_span_search, path,serverIP_port, isUnitizeOptional, internal_volume,volume_completion_date);
                    Command_import_data.progressCount = 10;

                    //inseting data into project_l1 table and batch table                   
                    int page_id = 0;
                    int parent_id = 0;
                    connection = Command_import_data.connection;
                    st = new DBTask().getStatement();

                    //added for L1 process
                    int project_id = -1;
                    int volumeId = 0;
                    int batch_number = 0;
                    int spanSizeForL1 = 0;
                    int pageSeq = 0;
                    int lastRgt = 0;

                    pst = connection.prepareStatement("SELECT project_id FROM project where project_name =?");
                    pst.setString(1, this.a_project_name);
                    pst.executeQuery();
                    rs = pst.getResultSet();
                    while (rs.next()) {
                        project_id = rs.getInt(1);
                    }
                    
                    ResultSet ressetWork = st.executeQuery("select max(rgt) from project WITH(HOLDLOCK) where project_id = " + project_id);
                    if (ressetWork.next()) {
                        pageSeq = ressetWork.getInt(1);
                    }

                    pst = connection.prepareStatement("SELECT volume_id FROM volume where project_id=?");
                    pst.setInt(1, project_id);
                    pst.executeQuery();
                    rs = pst.getResultSet();
                    while (rs.next()) {
                        volumeId = rs.getInt(1);
                    }
                    //query to get batch_number
                    pst = connection.prepareStatement("SELECT max(batch_number) FROM batch where volume_id =?");
                    pst.setInt(1, volumeId);
                    pst.executeQuery();
                    rs = pst.getResultSet();
                    while (rs.next()) {
                        batch_number = rs.getInt(1);
                    }
                    if (l1DataList.size() <= 100) {
                        spanSizeForL1 = 1;
                    } else {
                        spanSizeForL1 = (int) l1DataList.size() / 100;
                    }

                    int count = 0;
                    int batchCount = 1;
                    boolean flag = false;
                    for (Object object : l1DataList) {
                        flag = true;
                        count++;
                        pageSeq = pageSeq + 20;
                        L1Xref l1xref = (L1Xref) object;
                        pst = connection.prepareStatement("SELECT page_id, volume_id FROM page WHERE bates_number = ?");
                        pst.setString(1, l1xref.bates_number);
                        //pst.setInt(2,volumeId);
                        pst.executeQuery();
                        rs = pst.getResultSet();
                        while (rs.next()) {
                            page_id = rs.getInt(1);
                            volume_id = rs.getInt(2);
                        }
                        //if it is source 
                        if (l1xref.type.equals("Source") && count <= 1) {
                            batch_number++;
                            insertBatch(st, volume_id,/*lft*/ pageSeq,/*rgt*/ 0, batch_number,/*activegroup*/ 0,/*treatment level*/ "L1");
                            ResultSet rs = st.executeQuery("select top 1 batch_id from batch order by batch_id DESC");
                            if (rs.next()) {
                                batch_id = rs.getInt(1);
                                insertRange(st, pageSeq, lastRgt);
                                insertChild(st, pageSeq, lastRgt);
                                //rs = st.executeQuery("select last_insert_id()");
                                rs = st.executeQuery("select top 1 child_id from child order by child_id DESC");
                                if (rs.next()) {
                                    childId = rs.getInt(1);
                                }
                            }
                        } else if (l1xref.type.equals("Source") && count > a_l1_batchspan) {
                            int checkCount = (a_l1_batchspan * batchCount);
                            for (int i = 1; i < a_l1_batchspan; i++) {
                                if (count == checkCount + i) {
                                    batch_number++;
                                    batchCount++;
                                    updateBatchRgt(st, batch_id, lastRgt);
                                    insertBatch(st, volume_id,/*lft*/ pageSeq,/*rgt*/ 0, batch_number,/*activegroup*/ 0,/* treatment level*/ "L1");
                                    ResultSet rs = st.executeQuery("select top 1 batch_id from batch order by batch_id desc");
                                    if (rs.next()) {
                                        batch_id = rs.getInt(1);
                                        insertRange(st, pageSeq, lastRgt);
                                        insertChild(st, pageSeq, lastRgt);
                                                                    
                                    }
                                    break;
                                }
                            }
                        } else if (l1xref.type.equals("Source") || (l1xref.type.equals("Folder"))) {
                            // first page of child                            
                            Command_import_data.progressCount = (count * 85) / l1DataList.size() + 10;
                            if (l1xref.type.equals("Source") || (l1xref.type.equals("Folder"))) {
                                insertRange(st, pageSeq, lastRgt);
                            }
                            insertChild(st, pageSeq, lastRgt);
                        }

                        if (page_id != 0 && volume_id != 0) {

                            pstmt = connection.prepareStatement("INSERT INTO project_l1 (page_id, type, filename, parent_id, volume_id,seq,original_flag,boundary_flag,boundary,child_id,bates_number,group_one_path,group_one_filename,offset,path) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
                            pstmt.setInt(1, page_id);
                            pstmt.setString(2, l1xref.type);
                            pstmt.setString(3, l1xref.l1_file_name);

                            if (l1xref.type.equals(SOURCE)) {
                                pstmt.setInt(4, 0);
                            } else {
                                pstmt.setInt(4, parent_id);
                            }
                            pstmt.setInt(5, volume_id);
                            pstmt.setInt(6, pageSeq);
                            if (l1xref.type.equals("Source")) {
                                pstmt.setString(7, "D");
                                pstmt.setString(8, "D");
                                pstmt.setString(9, "RANGE");
                            } else if (l1xref.type.equals("Folder")) {
                                pstmt.setString(7, "C");
                                pstmt.setString(8, "C");
                                pstmt.setString(9, "CHILD");
                            }
                            pstmt.setInt(10, childId);
                            pstmt.setString(11, l1xref.bates_number);
                            pstmt.setString(12, " ");
                            pstmt.setString(13, " ");
                            pstmt.setInt(14, 0);
                            pstmt.setString(15, l1xref.group_one_path);
                            pstmt.executeUpdate();
                            lastRgt = pageSeq;
                            if (l1xref.type.equals(SOURCE)) {
                                pst = connection.prepareStatement("SELECT project_l1_id FROM project_l1");
                                pst.executeQuery();
                                rs = pst.getResultSet();
                                while (rs.next()) {
                                    parent_id = rs.getInt(1);
                                }
                            }
                        }
                    }
                    updateBatchRgt(st, batch_id, lastRgt);
                    updateRangeRgt(st, lastRgt);
                    updateChildRgt(st, lastRgt);

                    // In case of L1 process
                    // Check if you have a field name
                    // else create one for the project. The field name is 'Document Type General' and the tag_name is 'DTYG'
                    // now get the field name for the newly created project
                    // loop through the child_idList and for each BATES number, find the DTYG in BRSMap
                    // Manually verify whether the DTYG is right in BRS file.
                    if (flag) {
                        String field_name = DTYG_NAME;
                        pst = connection.prepareStatement("SELECT field_name FROM projectfields where project_id = ? AND tag_name = ?");
                        pst.setInt(1, project_id);
                        pst.setString(2, "DTYG");
                        pst.executeQuery();
                        rs = pst.getResultSet();
                        if (rs.next()) {
                            field_name = rs.getString(1);
                        } else {
                            //verify the sequence of other fields that has already been added
                            int sequence = 1;
                            Statement getDeqStatement = connection.createStatement();
                            ResultSet getSeqResultSet = getDeqStatement.executeQuery("select max(sequence) from projectfields " +
                                                                                                   "where project_id = " + project_id);
                            if(getSeqResultSet.next()){
                               sequence = getSeqResultSet.getInt(1) + 1;
                            }
                            pstmt = connection.prepareStatement("INSERT INTO projectfields (project_id, field_name, tag_name, field_type, field_size, unitize, spell_check,sequence,field_level,validation_functions_group_id, description,table_mandatory) values(?,?,?,?,?,?,?,?,?,?,?,?)");
                            pstmt.setInt(1, project_id);
                            pstmt.setString(2, field_name);
                            pstmt.setString(3, DTYG_TAG_NAME);
                            pstmt.setString(4, "text");
                            pstmt.setString(5, "100");
                            pstmt.setString(6, "No");
                            pstmt.setString(7, "No");
                            pstmt.setInt(8, sequence);
                            pstmt.setInt(9, 0);
                            pstmt.setInt(10, 17);
                            pstmt.setString(11, "General Document Type. Please refer Coding Manual.");
                            pstmt.setString(12, "No");
                            pstmt.executeUpdate();
                        }
                    }                    
                    int child_id = -1;
                    String bates_number = null;
                    String original_flag = null;
                    String boundary = null;
                    pst = connection.prepareStatement("select distinct(child_id), bates_number, original_flag, boundary from page where volume_id = ? AND (boundary='RANGE' OR boundary='CHILD')");
                    pst.setInt(1, volume_id);
                    pst.executeQuery();
                    rs = pst.getResultSet();
                    while (rs.next()) {

                        child_id = rs.getInt(1);
                        bates_number = rs.getString(2);
                        original_flag = rs.getString(3);
                        boundary = rs.getString(4);

                        if (brsRecordMap.get(bates_number) != null) {
                            pstmt = connection.prepareStatement("INSERT INTO value (child_id, field_name, sequence, value) values(?,?,?,?)");
                            pstmt.setInt(1, child_id);
                            pstmt.setString(2, "General Document Type");
                            pstmt.setInt(3, 0);
                            pstmt.setString(4, (String) brsRecordMap.get(bates_number));
                            pstmt.executeUpdate();
                        }
                    }
                } catch (Exception e) {
                    logger.error("Exception during inserting project fields and child in import." + e);
                    StringWriter sw = new StringWriter();
                    e.printStackTrace(new PrintWriter(sw));
                    logger.error(sw.toString());

                    //for debug only
                    e.printStackTrace();
                    if (debug | log) {
                        Log.write("DB Error: " + e);
                    }
                    message = "DB Error: " + e;
                    return;
                }
            } catch (Exception e) {
                logger.error("Exception in import." + e);
                StringWriter sw = new StringWriter();
                e.printStackTrace(new PrintWriter(sw));
                logger.error(sw.toString());
                return;
            }
            Command_import_data.progressCount = 100;
            Log.print("Successful XREF Creation");
        } else {
            Command_import_data.progressCount = 0;
            Log.print("No XREF Creation Selected");
        }
        Log.print("Exit DiaLoad");
    }

    public String getStats() {
        if (message.equals("")) {
            message = "ok";
        }
        return message;
    }

    private boolean loadParameters(String args[]) {           
        String commandLineSpec = "[--log] [--debug] [--replace ] [--append] [--split ]" +
                " [ {-i | --imagepath [=] }  value ]  {-p | --project [=] } value " +
                " [{--codingdb [=]} value] [{--codingport [=]} value] [{--codinguser [=]} value] " +
                " [{--codingpwd [=]} value] " +
                " [{--codinghost [=]} value] " +
                " [ {-b | --batchspan [=] }  value ] " +
                " [ {-e | --l1_batchspan [=] }  value ] " +
                " [ {-s | --spansearch [=] }  value ] " +
                " {-v | --volume [=]} value {-t | --type [=]} value [{ -x | --xref  [=] } importfile]" +
                "[{ -w | --brsfile  [=] } brsfile]" +
                "[{ -y | --imgfile  [=] } imgfile]" +
                "[{ -z | --txtfile  [=] } txtfile]" +
                "[{ -l | --level  [=] } level]" +
                "[{ -u | --unitize  [=] } unitize]" +
                "[{ -n | --internal  [=] } internal]" +
                "[{ -c | --volumecompletion  [=] } volumecompletion]";

        CommandLine cl = new CommandLine(commandLineSpec);


        if (!cl.parse(args)) {
            return false;
        } else {
            String cmd_buffer = new String();
            cmd_buffer = "";

            // check for no arguments
            int ii = args.length;
            if (ii > 0) {
                int jj = 0;
                while (ii > jj) {
                    cmd_buffer = cmd_buffer + args[jj];
                    cmd_buffer = cmd_buffer + "  ";
                    jj++;
                }

            } else {
                if (this.debug || this.log) {
                    Log.write("Syntax Error:  DiaLoad missing arguments");
                }
                if (this.debug || this.log) {
                    Log.write("   Correct syntax:\n");
                }
                if (this.debug || this.log) {
                    Log.write("     DiaLoad " + commandLineSpec);
                }
                if (this.debug || this.log) {
                    Log.write("Command Line Error - proper syntax: " + commandLineSpec);
                }

                System.out.println("Syntax Error:  DiaLoad missing arguments");
                System.out.println("   Correct syntax:\n");
                System.out.println("     DiaLoad " + commandLineSpec);
                System.out.println("Command Line Error - proper syntax: " + commandLineSpec);
                return false;
            }

            if (cl.getOption("x") != null) {
                this.importfilename = cl.getOption("x");
            }


            if (cl.getOption("w") != null) {
                this.brs_filename = cl.getOption("w");
            }
            if (cl.getOption("y") != null) {
                this.img_filename = cl.getOption("y");
            }
            if (cl.getOption("z") != null) {
                this.txt_filename = cl.getOption("z");
            }
            //this line is added to get the treatment level
            if (cl.getOption("t") != null) {
                this.isL1Treatment = true;
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
                return false;
            }
            if (cl.getOption("t") != null) {
                Log.write("Option:" + cl.getOption("t"));
                if ("LFP".equals(cl.getOption("t"))) {
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
            
            if (cl.getOption("u") != null) {
                this.isUnitizeOptional = cl.getOption("u");
            }
            if (cl.getOption("n") != null) {
                this.internal_volume = cl.getOption("n");
            }
            if (cl.getOption("c") != null) {
                this.volume_completion_date = cl.getOption("c");
            }
            if (cl.getOption("i") != null) {
                this.a_image_path = cl.getOption("i");
            }

            // Number of pages to put in a batch.  Try to start each batch with a document.
            if (cl.getOption("b") != null) {
                try {
                    Integer i_batchspan = new Integer(cl.getOption("b"));
                    this.a_batchspan = i_batchspan.intValue();
                } catch (NumberFormatException e) {
                    Log.write("Invalid batch span value:" + cl.getOption("b") + " must be numeric");                    
                    logger.error("Exception during getting batch span value" + e);
                    StringWriter sw = new StringWriter();
                    e.printStackTrace(new PrintWriter(sw));
                    logger.error(sw.toString());
                    return false;
                }
            }
            if (cl.getOption("e") != null) {
                try {
                    Integer i_batchspan = new Integer(cl.getOption("e"));
                    this.a_l1_batchspan = i_batchspan.intValue();
                } catch (NumberFormatException e) {
                    Log.write("Invalid batch span value:" + cl.getOption("b") + " must be numeric");
                    logger.error("Exception during getting batch span value" + e);
                    StringWriter sw = new StringWriter();
                    e.printStackTrace(new PrintWriter(sw));
                    logger.error(sw.toString());
                    return false;
                }

            }

            // Number of pages to search beyond batchspan for a document to begin the next batch.
            if (cl.getOption("s") != null) {
                try {
                    Integer i_span_search = new Integer(cl.getOption("s"));
                    this.a_span_search = i_span_search.intValue();
                } catch (NumberFormatException e) {
                    Log.write("Invalid span search value:" + cl.getOption("s") + " must be numeric");
                    logger.error("Exception during getting span search value" + e);
                    StringWriter sw = new StringWriter();
                    e.printStackTrace(new PrintWriter(sw));
                    logger.error(sw.toString());
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
                if (this.debug || this.log) {
                    Log.write("Syntax Error:  Import file name required");
                }
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
                } catch (NumberFormatException e) {
                    Log.write("Invalid Coding Port:" + cl.getOption("codingport") + " must be numeric");
                    
                    logger.error("Invalid Coding Port:" + cl.getOption("codingport") + " must be numeric");
                    logger.error("Number Format Exception in import." + e);
                    StringWriter sw = new StringWriter();
                    e.printStackTrace(new PrintWriter(sw));
                    logger.error(sw.toString());
                    return false;
                }
            }

            if (a_span_search > a_batchspan) {
                Log.print("Warning: spansearch is greater than batchspan: " + a_span_search + " > " + a_batchspan);
            }
            if (a_batchspan > 0 && a_span_search < 0) {
                // no spansearch entered.  default to 20%
                a_span_search = (int) (a_batchspan * default_search);
                Log.print("Warning: spansearch is defaulting to " + a_span_search);

            }

            if (this.log) {
                Log.write("Cmd Buffer: " + cmd_buffer);
                Log.write("project name: " + this.a_project_name);
                Log.write("volume name: " + this.a_volume_name);
                Log.write("format type: " + this.a_format_string);
            }
         
            return true;
        }
    }

    private void insertBatch(Statement st, int volume_id, int lft, int rgt,
                             int batch_number, int activeGroup, String level) throws SQLException {
       
         if(isUnitizeOptional.equals("true")){
           st.executeUpdate("insert into batch(volume_id,lft,rgt, batch_number, status, priority, active_group, treatment_level) values(" + volume_id + "," + lft + "," + rgt + "," + batch_number + ",'Coding',0," + activeGroup + ",'L1')");
         }else{
            st.executeUpdate("insert into batch(volume_id,lft,rgt, batch_number, status, priority, active_group, treatment_level) values(" + volume_id + "," + lft + "," + rgt + "," + batch_number + ",'Unitize',0," + activeGroup + ",'L1')");
         }
        

        //insert into history_batch for audit-history

        st = connection.createStatement();
        ResultSet rs_batchId = st.executeQuery(SQLQueries.SEL_TOP_BATCH_ID);
        rs_batchId.next();
        int batchId = rs_batchId.getInt(1);

        BatchHistoryData batchData = new BatchHistoryData(connection, batchId);
        batchData.insertIntoHistoryTable(connection, Command_import_data.userId, Mode.ADD);


        BatchProcessHistroyData data = new BatchProcessHistroyData();
        data.setBatch_id(batchId);
        data.setVolume_id(volume_id);
        data.setProcess("Unitize");
        data.setIs_ready("Yes");
        data.insertIntoHistoryTable(connection);
    }

    private void insertRange(Statement st, int sequence, int lastRgt) throws SQLException {
        // first page of range
        if (rangeId > 0) {
            // not first range of volume
            // End the last range row by adding a range.rgt
            updateRangeRgt(st, lastRgt);
        }
        // Save range_id for use in updating range.rgt
        // when next/last range encountered.
        st.executeUpdate("insert into range(volume_id,lft,rgt) values(" + volume_id + "," + sequence + ",0)");
        
        rs = st.executeQuery("select top 1 range_id from range order by range_id DESC");
        if (rs.next()) {
            rangeId = rs.getInt(1);
        }
        rs.close();
        if (debug) {
            Log.write("    doing range insert: range_id: " + rangeId + "/" + sequence);
        }
    }

    private void insertChild(Statement st, int sequence, int lastRgt) throws SQLException {
        if (childId > 0) {
            // not first child of volume
            // End the last child row by adding a child.rgt
            updateChildRgt(st, lastRgt);
        }
        // Save child_id for use in updating child.rgt
        // when next/last child encountered.

        st.executeUpdate("insert into child (volume_id, lft, rgt, range_id, batch_id) values(" + volume_id + "," + sequence + ",0," + rangeId + "," + batch_id + " )");
        // get the last auto_increment generated rangeId
        rs = st.executeQuery("select top 1 child_id from child order by child_id DESC");
        if (rs.next()) {
            childId = rs.getInt(1);
        }
        if (debug) {
            Log.write("    doing child insert: child_id: " + childId + "/" + sequence);
        }
        rs.close();
    // increment the total number of children seen
    // pageCnt++;
    }

    private void updateBatchRgt(Statement st, int batch_id, int lastRgt) throws SQLException {
        st.executeUpdate("update batch  set rgt=" + lastRgt // rgt saved from last page insert
                + " where batch_id=" + batch_id); // batchId saved from last batch insert
        BatchHistoryData batchData = new BatchHistoryData(connection, batch_id);
        batchData.insertIntoHistoryTable(connection, Command_import_data.userId, Mode.EDIT);
    }

    private void updateRangeRgt(Statement st, int lastRgt) throws SQLException {
        st.executeUpdate("update range" + " set rgt=" + lastRgt // rgt saved from last page insert
                + " where range_id=" + rangeId); // rangeId saved from last range insert
    }

    private void updateChildRgt(Statement st, int lastRgt) throws SQLException {
        st.executeUpdate("update child" + " set rgt=" + lastRgt // rgt saved from last page insert
                + " where child_id=" + childId); // childId saved from last child insert
    }
}
