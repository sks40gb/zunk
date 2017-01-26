/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fossa.servlet.command;

import com.fossa.servlet.common.CommonLogger;
import com.fossa.servlet.common.ExportData;
import com.fossa.servlet.common.Log;
import com.fossa.servlet.common.ProjectFieldsData;
import com.fossa.servlet.common.SQLQueries;
import com.fossa.servlet.common.StatusConstants;
import com.fossa.servlet.common.msg.MessageWriter;
import com.fossa.servlet.dao.DBTask;
import com.fossa.servlet.dbload.XrefConstants;
import com.fossa.servlet.script.Formatter;
import com.fossa.servlet.script.ValidationReport;
import com.fossa.servlet.script.Validator;
import com.fossa.servlet.writer.DiaExportFromGui;
import com.fossa.servlet.session.UserTask;
import com.fossa.servlet.threadpool.ThreadPool;
import com.fossa.servlet.writer.PageissueData;
import com.fossa.servlet.writer.RunStats;
import com.fossa.servlet.writer.ValueData;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Date;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.w3c.dom.Element;

/**
 * This class exports output data
 * @author Bala
 */
public class Command_export_data implements Command ,Job {

    private PreparedStatement pst = null;
    private Connection connection = null;
    private ProjectFieldsData[] projectfields = null;
    private static PageissueData[] pageissues;
    private String projectName = null;
    private boolean isUnitizeOnly = false;
    private boolean isQCBatchesAllowed = false;
    private int project_id = 0;
    private int volume_id = 0;
    private DBTask dbTask = null;
    private int startBatchNumber = 0;
    private int endBatchNumber = 0;
    private int batchStatus = 0;
    private ResultSet getValuesResultSet = null;
    private ExportData data = null;
    private RunStats stats;
    public static MessageWriter writer;
    public static UserTask user = null;
    private int exportDetailsId = 0;
    private int outputValidationId = 0;
    private int tsearchExportDetailsId = 0;
    private String pathPrefix = null;
    private String userName = null;
    private String fileName = null;
    private String validationResult = "PASS";
    private Element action;
    private String FOLDER_NAME = null;
    private static final String OVP_TABLE_NAME = "output_validations_details";
    private static final String EXPORT_DETAILS_TABLE_NAME = "export_details";
    private static final String TSEARCH_EXPORT_DETAILS_TABLE_NAME = "tsearch_export_details";
    private static final String OVP_TABLE_ID = "output_validations_details_id";
    private static final String EXPORT_DETAILS_TABLE_ID = "export_details_id";
    private static final String TSEARCH_EXPORT_DETAILS_TABLE_ID = "tsearch_export_details_id";
    private int serverTaskQueueId = 0;  
    private int userId =0;
    private static String contextPath;
    private int servertaskqueue_id;
     
     
    public Command_export_data(int serverTaskQueueId,UserTask user){
      this.serverTaskQueueId = serverTaskQueueId;
      Command_export_data.user = user;            
    }
    
    public Command_export_data(){}
    
    public String execute(Element action, UserTask user, DBTask dbTask, MessageWriter writer) {
        try {
            Connection con = dbTask.getConnection();
            this.action = action;
            Command_export_data.user = user;
            Command_export_data.writer = writer;            

            System.out.println("Command_export_data.writer    : " + Command_export_data.writer.toString());

            this.dbTask = dbTask;
            ThreadPool pool = new ThreadPool(1);
            Statement getQueueTask = dbTask.getStatement();
            ResultSet rs = getQueueTask.executeQuery("select * from servertaskqueue where status = 'progress'");
            int count =0;
            while(rs.next()){ 
                servertaskqueue_id = rs.getInt(1);
                count++;
//                if(count <=2){
//                  pool.assign(this);
//                // t.start();
//                  pool.complete();   
//                  
//                  PreparedStatement updateServerTaskQueueStatus = con.prepareStatement("Update servertaskqueue set status = ? where servertaskqueue_id = ?");
//                  updateServerTaskQueueStatus.setString(1, "complete");
//                  updateServerTaskQueueStatus.setInt(2, servertaskqueue_id);
//                  updateServerTaskQueueStatus.executeUpdate();                  
//                }else{
//                    count = 0;
//                    try {                        
//                        Thread.sleep(1000);
//                        
//                       } catch (InterruptedException e) {
//                       }
//                }             
            }


            // Create a new Thread and start to run so that it can continue without effecting the client application.
            // User can perform other task without waiting for completion of this export operation.
            // This export operation will be handled by the server.
            
            System.out.println("All tasks are done.");
           
        } catch (SQLException ex) {
            Logger.getLogger(Command_export_data.class.getName()).log(Level.SEVERE, null, ex);
        }
         return null;

//        this.dbTask = dbTask;
//
//        // Create a new Thread and start to run so that it can continue without effecting the client application.
//        // User can perform other task without waiting for completion of this export operation.
//        // This export operation will be handled by the server.
//        Thread t = new Thread(this);
//        t.start();
//
//        return null;

    }
    
    /**
     * Get all the Field Record of a the project.
     * Put the Field Record to an Array <code>projectfields</code>.
     */
    private void getProjectfields() {        
        ArrayList fieldsList = new ArrayList();
        try {
            Statement st = connection.createStatement();
            ResultSet rs = null;
            String query = "";
            if (isFormattedVolume()) {
                query = "select " +
                        // "OS.projectfields_id, " +
                        // "OS.ovp_field_id," +
                        "coalesce(PF.projectfields_id, -1) as Id, " +
                        "OS.sequence," +
                        "coalesce(OVP.field_name,PF.field_name) as field_name, " +
                        "coalesce(PF.field_type,'text') as field_type, " +
                        "coalesce(PF.tag_name, OVP.field_name) as tag_name " +
                        //                            "OS.separator," +
                        //                            "OS.format, " +
                        //                            "OS.suppress," +
                        //                            "OS.type " +
                        "FROM ovp_sequence OS " +
                        "LEFT OUTER JOIN projectfields PF ON OS.projectfields_id = PF.projectfields_id  " +
                        "LEFT OUTER JOIN ovp_field OVP ON OVP.ovp_field_id = OS.ovp_field_id  " +
                        "WHERE volume_id = " + volume_id + " AND OS.suppress != 'true' ORDER BY OS.sequence ";

            } else {
                query = "select " +
                        //"projectfields_id, " +
                        "P.project_id, " +
                        "sequence, " +
                        "field_name, " +
                        "field_type, " +
                        //"field_size, " +
                        //"repeated, " +
                        //"required, " +
                        //"default_value, " +
                        //"min_value, " +
                        //"max_value, " +
                        //"tablespec_id, " +
                        //"table_mandatory, " +
                        //"mask, " +
                        //"valid_chars, " +
                        //"invalid_chars, " +
                        //"charset, " +
                        //"type_field, " +
                        //"type_value, " +
                        //"unitize, " +
                        //"spell_check, " +
                        //"field_level, " +
                        //"minimum_size, " +
                        "tag_name " +
                        "from projectfields inner join project P on P.project_id = projectfields.project_id where P.project_id = " + project_id + " order by sequence";
            }
            rs = st.executeQuery(query);
            while (rs.next()) {
                ProjectFieldsData fields = new ProjectFieldsData();
                fields.projectfieldsId = rs.getInt(1);
                fields.sequence = rs.getInt(2);
                fields.fieldName = rs.getString(3);
                fields.fieldType = rs.getString(4);
                fields.tagName = rs.getString(5);

//                fields.projectfieldsId = rs.getInt(1);
//                fields.projectId = rs.getInt(2);
//                fields.sequence = rs.getInt(3);
//                fields.fieldName = rs.getString(4);                
//                fields.tagName = rs.getString(24);
//                fields.fieldType = rs.getString(5);                
//                fields.fieldSize = rs.getInt(6);
//                fields.repeated = rs.getString(7);              
//                fields.required = rs.getString(8);
//                fields.defaultValue = rs.getString(9);
//                fields.minValue = rs.getString(10);
//                fields.maxValue = rs.getString(11);
//                fields.tablespecId = rs.getInt(12);
//                fields.tableMandatory = rs.getString(13);
//                fields.mask = rs.getString(14);
//                fields.validChars = rs.getString(15);
//                fields.invalidChars = rs.getString(16);
//                fields.charset = rs.getString(17);
//                fields.typeField = rs.getString(18);
//                fields.typeValue = rs.getString(19);
//                fields.unitize = rs.getString(20);
//                fields.spellCheck = rs.getString(21);
//                fields.minimumSize = rs.getInt(23);
                fieldsList.add(fields);
            }
            projectfields = (ProjectFieldsData[]) fieldsList.toArray(new ProjectFieldsData[fieldsList.size()]);
            rs.close();
        } catch (SQLException e) {
            CommonLogger.printExceptions(this, "SQLException caught during fetching project fields.", e);
        }
        if (projectfields.length > 0) {
            if (isUnitizeOnly) {
                // don't need values if we're exporting unitization, only
                getOffsetValue();
            } else {
                readValues();
            }
        }
    }

    private void readValues() {
        try {            
            
            CallableStatement getExportDataCStatement = connection.prepareCall("{ call Sproc_getExportData(?,?,?,?,?) }");
            getExportDataCStatement.setInt(1, project_id);
            getExportDataCStatement.setInt(2, volume_id);
            getExportDataCStatement.setInt(3, startBatchNumber);
            getExportDataCStatement.setInt(4, endBatchNumber);
            getExportDataCStatement.setInt(5, batchStatus);
            getValuesResultSet = getExportDataCStatement.executeQuery();
            
            getOffsetValue();

        } catch (SQLException ex) {
            CommonLogger.printExceptions(this, "SQLException caught during fetching the field values.", ex);
        }
    }

    private void getOffsetValue() {
        try {            
            Statement st = connection.createStatement();
            ResultSet rs = st.executeQuery("select top 1 page_id from page where offset > 0 and volume_id = " + volume_id);
            int offset = rs.next() ? 1 : 0;            
            PreparedStatement ps = connection.prepareStatement("select P.seq, P.bates_number, P.path, P.filename, P.boundary_flag, P.offset, P.rotate, C.lft, C.rgt, P.file_type, V.volume_name, CASE B.status WHEN 'Unitize' Then 1 WHEN 'UQC' Then 2 WHEN 'UComplete' Then 3 WHEN 'UBatched' Then 4 WHEN 'Coding' Then 5 WHEN 'CodingQC' Then 6 WHEN 'QCComplete' Then 7 WHEN 'QA' Then 8 WHEN 'QAComplete' Then 9 Else 0 END as 'B.status - 0', R.lft, R.rgt, PI.sequence, PI.issue_name, B.status, B.batch_number, beginP.bates_number, endP.bates_number, P.group_one_path, P.group_one_filename, P.document_number, V.original_volume_name  from page P inner join child C on C.child_id = P.child_id inner join range R on (C.range_id = R.range_id)  inner join batch B on (C.batch_id = B.batch_id) inner join volume V on V.volume_id = B.volume_id  inner join page beginP on (beginP.seq = R.lft and beginP.volume_id = V.volume_id)  inner join page endP on (endP.seq = R.rgt and endP.volume_id = V.volume_id)  left join pageissue PI on (P.page_id = PI.page_id) where  B.volume_id = ? and B.batch_number  between ? and ? and dbo.fn_GetStatusNumber(b.status) - 0 >= ? ORDER by seq , PI.sequence");
            ps.setInt(1, volume_id);
            ps.setInt(2, startBatchNumber);
            ps.setInt(3, endBatchNumber);
            ps.setInt(4, batchStatus);
            ResultSet getPageDataResultSet = ps.executeQuery();
            st.close();
            readPagesEntry(getPageDataResultSet, offset);
        } catch (SQLException ex) {
            CommonLogger.printExceptions(this, "SQLException caught during fetching the offset values.", ex);
        }
    }

    private void readPagesEntry(ResultSet rs, int offsetAdjustment) {        
        ArrayList pageList = new ArrayList();
        try {
            while (rs.next()) {
                PageissueData pages = new PageissueData();
                pages.seq = rs.getInt(1);
                pages.batesNumber = rs.getString(2);
                pages.path = rs.getString(3);
                pages.filename = rs.getString(4);
                pages.boundaryFlag = rs.getString(5);
                pages.offset = rs.getInt(6) + offsetAdjustment;
                pages.rotate = rs.getInt(7);
                pages.lft = rs.getInt(8);
                pages.rgt = rs.getInt(9);
                pages.fileType = rs.getInt(10);
                pages.volumeName = rs.getString(11);
                pages.rangeLft = rs.getInt(13);
                pages.rangeRgt = rs.getInt(14);
                pages.sequence = rs.getInt(15);
                pages.issueName = rs.getString(16);
                pages.status = rs.getString(17);
                pages.batchNumber = rs.getInt(18);
                pages.beginBatesNumber = rs.getString(19);
                pages.endBatesNumber = rs.getString(20);
                pages.groupOnePath = rs.getString(21);
                pages.groupOneFileName = rs.getString(22);
                pages.documentNumber = rs.getString(23);
                pages.originalVolume = rs.getString(24);
                pageList.add(pages);
            //Log.print("(ExportPage.readPagesEntry) " + pages.batesNumber);
            }
            rs.close();

        } catch (SQLException e) {
            Log.quit("(ExportPage.ReadPagesEntry) Sql error " + e);
        }
        pageissues = (PageissueData[]) pageList.toArray(new PageissueData[pageList.size()]);

        // All of the data has been downloaded from the server -- start the export.
        int xrefFormat = 0;
        if (Boolean.parseBoolean(data.isDoculex)) {
            xrefFormat = XrefConstants.DOCULEX;
        } else if (Boolean.parseBoolean(data.isOpticon)) {
            xrefFormat = XrefConstants.OPTICON;
        } else if (Boolean.parseBoolean(data.isSummation)) {
            xrefFormat = XrefConstants.SUMMATION;
        } else if (Boolean.parseBoolean(data.isBRS)) {
            xrefFormat = XrefConstants.BRS;
        } else {
            xrefFormat = XrefConstants.LFP;
        }

        if (data.isTSearchExport.equals("true")) {
            FOLDER_NAME = "$TSearchExport";
        } else {
            FOLDER_NAME = "$Export";
        }
        
        pathPrefix = contextPath + File.separator + "UPLOAD" + File.separator + FOLDER_NAME + File.separator + projectName + "_" + data.volume_name + File.separator;
        File file = new File(pathPrefix);
        if (!file.exists()) {
            file.mkdirs();
        }

        System.out.println("");
        
        String txtFile = projectName + "_" + data.volume_name + ".txt";
        String lfpFile = projectName + "_" + data.volume_name + ".lfp";
        String logFile = projectName + "_" + data.volume_name + ".log";
        String fileNames[] = {txtFile, lfpFile, logFile};
      
        data.data_filename = pathPrefix + fileNames[0];
        data.lfp_filename = pathPrefix + fileNames[1];
        data.log_filename = pathPrefix + fileNames[2];
        
        System.out.println(""+ data.data_filename);
        System.out.println(""+ data.lfp_filename);
        System.out.println(""+ data.log_filename);
       
        String pathFile[] = {data.data_filename, data.lfp_filename, data.log_filename};
        stats = new RunStats();
        DiaExportFromGui export = new DiaExportFromGui(projectfields, getValueDataList(getValuesResultSet), pageissues, data, stats, xrefFormat);

        Validator.resetReportList();
        export.setConnection(connection);
        boolean haltExport = false;
        validationResult = "PASS";
        System.out.println("data.doValidation #####################################################################--> " + data.doValidation);
        if (data.doValidation.equalsIgnoreCase("true")) {
            //execute validation
            insertIntoExportDetails(OVP_TABLE_NAME);
            haltExport = export.executeScript(new Validator(connection));
            if (Validator.hasError()) {
                validationResult = "FAIL";
                System.out.println("-------------> FAIL" );
            } else {
                validationResult = "PASS";
                System.out.println("-------------> PASS" );
            }
            updateExportDetails(OVP_TABLE_NAME);
        }  
        if (validationResult.equals("PASS")) {            
        
            if (data.isTSearchExport.equals("true")) {
                //execure formatter
                export.executeScript(new Formatter(connection));
                //export files.
                insertIntoExportDetails(TSEARCH_EXPORT_DETAILS_TABLE_NAME);
                export.run();
                Zip zipFiles = new Zip(pathPrefix, fileNames, pathFile, data.project_name, data.volume_name);
                updateExportDetails(TSEARCH_EXPORT_DETAILS_TABLE_NAME);
            } else {
                //execure formatter
                System.out.println("aaaaaaaaaaa");
                
                    export.executeScript(new Formatter(connection));
                    //export files.
                    insertIntoExportDetails(EXPORT_DETAILS_TABLE_NAME);
                    export.run();
                    Zip zipFiles = new Zip(pathPrefix, fileNames, pathFile, data.project_name, data.volume_name);
                    updateExportDetails(EXPORT_DETAILS_TABLE_NAME);
                
            }
        }
        //Validator.getReportList();
        writeReportXML();

    }

    /**
     * Get the list of value data from the resultset.
     * @param rs ResultSet
     * @return Value Data List.
     */
    private ArrayList<ValueData> getValueDataList(ResultSet rs) {
        ArrayList<ValueData> valueDataList = new ArrayList<ValueData>();
        //Map<String,ValueData> valueDataMap = new HashMap<String, ValueData>();       
        try {

            while (rs.next()) {
                ValueData vd = new ValueData();
                vd.childLft = rs.getInt(2);
                vd.projectfieldsSequence = rs.getInt(3);
                vd.sequence = rs.getInt(4);
                vd.fieldName = rs.getString(1);
                vd.value = rs.getString(5).trim();
                vd.type = rs.getString(7).trim();

                //System.out.println(vd.fieldName + "  ------  " + vd.value + "  ------   " + vd.projectfieldsSequence + " ----- " + vd.childLft + " ---- " + vd.type) ;

                valueDataList.add(vd);
            //valueDataMap.put(vd.fieldName, vd);
            }
            if (valueDataList.size() == 0) {
                ValueData vd = new ValueData();
                vd.childLft = Integer.MAX_VALUE;
            }

        } catch (SQLException e) {
            System.out.println("SQL Error: " + e);
        }
        ArrayList<ValueData> formattedList = getFormattedValueDataList(valueDataList);
        
        for(ValueData v : formattedList){
            System.out.println(v.fieldName + " ---- " + v.childLft);
        }
        
        return formattedList == null ? valueDataList : formattedList;
    //return valueDataList;
    }

    private ArrayList<ValueData> getFormattedValueDataList(List<ValueData> valueDataList) {
        ArrayList<ValueData> formattedList = null;
        Deque<ValueData> formattedDeque = new ArrayDeque<ValueData>(valueDataList);
        ResultSet rs = null;
        Map<String, ValueData> valueDataMap = new HashMap<String, ValueData>();

        PreparedStatement ps = null;
        try {
            Statement st = connection.createStatement();
            String query;

            if (isFormattedVolume()) {
                formattedList = new ArrayList<ValueData>();
                query = "select " +
                        "coalesce(OVP.field_name,PF.field_name) as field_name, " +
                        "OS.separator," +
                        "OS.format, " +
                        "OS.sequence,  " +
                        "coalesce(PF.l1_information,'Source') as type " +
                        "FROM ovp_sequence OS " +
                        "LEFT OUTER JOIN projectfields PF ON OS.projectfields_id = PF.projectfields_id  " +
                        "LEFT OUTER JOIN ovp_field OVP ON OVP.ovp_field_id = OS.ovp_field_id  " +
                        "WHERE volume_id = ? AND OS.suppress != ? ORDER BY OS.sequence";


                while ((valueDataMap = getMap(formattedDeque)) != null) {

                    List<ValueData> listBuffer = new ArrayList<ValueData>();
                    ps = connection.prepareStatement(query);
                    ps.setInt(1, volume_id);
                    ps.setString(2, "true");
                    ps.executeQuery();

                    rs = ps.getResultSet();

                    //int sequence = 1;
                    boolean setDefault = true;
                    int lft = -1;
                    while (rs.next()) {

                        String name = rs.getString(1);
                        String separator = rs.getString(2);
                        String format = rs.getString(3);
                        int sequence = rs.getInt(4);
                        String type = rs.getString(5).trim().equalsIgnoreCase("Document") ? "L2" : "L1";
                        ValueData vd = new ValueData();

                        if (valueDataMap.containsKey(name)) {                            

                            vd.childLft  = valueDataMap.get(name).childLft;

                            //vd.projectfieldsSequence = valueDataMap.get(name).projectfieldsSequence;
                            vd.projectfieldsSequence = sequence;
                            vd.sequence = sequence;
                            vd.fieldName = name;
                            vd.value = getFormattedValue(name, format, valueDataMap);
                            vd.type = type;
                            
                            // the project Fields.
                            // Note : it is executed once.

                            System.out.println("vd.type : " + vd.type + "      vd.childLft : " + vd.childLft);
                            
                            if (setDefault && vd.type.equals("L2")) {
                                lft = valueDataMap.get(name).childLft;                           

                                for (String key : valueDataMap.keySet()) {
                                    valueDataMap.get(key).childLft = vd.childLft;
                                }

                                for (ValueData v : listBuffer) {
                                    v.childLft = vd.childLft;
                                }
                                
                                setDefault = false;
                            } else {
                                setDefault = true;
                            }

                        } else {
                            vd.childLft = lft;
                            vd.projectfieldsSequence = sequence;
                            vd.sequence = sequence;
                            vd.fieldName = name;
                            vd.value = getFormattedValue(name, format, valueDataMap);
                            setDefault = true;
                        }
                        listBuffer.add(vd);
                        formattedList.add(vd);
                        
                    }

                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return formattedList;
    }

    private boolean isFormattedVolume() {
        try {
            String query = "select volume_id FROM ovp_sequence WHERE volume_id = ? ";
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setInt(1, volume_id);
            ps.executeQuery();
            ResultSet rs = ps.getResultSet();

            if (rs.next()) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private Map<String, ValueData> getMap(Deque<ValueData> deque) {
        Map<String, ValueData> map = new HashMap<String, ValueData>();
        while (!deque.isEmpty()) {
            if (map.containsKey(deque.getFirst().fieldName)) {
                return map;
            } else {
                map.put(deque.getFirst().fieldName, deque.pollFirst());
            }
        }
        return null;
    }

    private String getFormattedValue(String fieldName, String format, Map<String, ValueData> valueDataMap) {
        //put the formatted value in buffer.
        StringBuilder buffer = new StringBuilder();
        //if the format is empty then return the field value without any change.
        if (format == null || format.trim().equals("")) {
            return valueDataMap.containsKey(fieldName) ? valueDataMap.get(fieldName).value : "";
        } else {
            StringTokenizer tokens = new StringTokenizer(format, "<>");
            while (tokens.hasMoreTokens()) {
                String token = tokens.nextToken();
                if (valueDataMap.containsKey(token)) {
                    buffer.append(valueDataMap.get(token).value);
                } else {
                    buffer.append(token);
                }
            }
        }
        return buffer.toString();
    }

    public boolean isReadOnly() {
        return true;
    }

    /**
     * Write the XML and send to client as reponse.
     */
    private void writeReportXML() {
        
        List<ValidationReport> reportList = Validator.getReportList();        
        try {
            //writeXmlFromList(reportList, false);
            if (reportList.size() > 0) {
                writeReportToExcelSheet(user, reportList);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Write the report and send to the client.
     * @param list Report List
     * @param requestedMetaData
     * @throws java.sql.SQLException
     * @throws java.io.IOException
     */
    public void writeXmlFromList(List<ValidationReport> list, boolean requestedMetaData)
            throws SQLException, IOException {
        String userSessionId = user.getFossaSessionId();
        int columnCount = 8;
        writer.startElement(T_RESULT_SET);
        writer.writeAttribute(A_FOSSAID, userSessionId);
        writer.writeAttribute(A_COUNT, Integer.toString(columnCount));
        if (requestedMetaData) {
            writer.startElement(T_HEADING);
            for (int j = 1; j <= columnCount; j++) {
                writer.startElement(T_COLUMN);
                writer.writeContent("COULUMN : " + j);
                writer.endElement();
            }
            writer.endElement();
        }
        int j = 0;

        writer.startElement(T_ROW);
        writeElement(writer, (exportDetailsId == 0) ? "" : Integer.toString(exportDetailsId));
        writeElement(writer, "");
        writeElement(writer, "");
        writeElement(writer, "");
        writeElement(writer, "");
        writeElement(writer, "");
        writeElement(writer, "");
        writeElement(writer, "");
        j++;
        writer.endElement();

        for (ValidationReport vr : list) {
            writer.startElement(T_ROW);
            writeElement(writer, Integer.toString(j));
            writeElement(writer, vr.getFieldName());
            writeElement(writer, vr.getBatchNumber());
            writeElement(writer, vr.getDocumentNumber());
            writeElement(writer, vr.getFieldValue());
            writeElement(writer, vr.getFunctionName());
            writeElement(writer, vr.getErrorMessage());
            writeElement(writer, vr.getErrorType());
            j++;
            writer.endElement();
        }
        writer.endElement();

    }

    private void writeElement(MessageWriter writer, String value) throws IOException {
        writer.startElement(T_COLUMN);
        if (value == null) {
            writer.writeAttribute(A_IS_NULL, "YES");
        } else {
            writer.writeContent(value);
        }
        writer.endElement();
    }

    /**
     * Insert the report details to the table.
     * This method is used before starting the validating or formatting the data.
     * @param tableName
     */
    private void insertIntoExportDetails(String tableName) {
        try {            
            PreparedStatement insertExportDetailsPrepStmt = connection.prepareStatement("INSERT INTO " + tableName +
                    "(project_id,volume_id,file_name,status,created_by,run_date) VALUES(?,?,?,?,?,?)");
            insertExportDetailsPrepStmt.setInt(1, project_id);
            insertExportDetailsPrepStmt.setInt(2, volume_id);
            insertExportDetailsPrepStmt.setString(3, "");
            insertExportDetailsPrepStmt.setString(4, "In Progress");
            insertExportDetailsPrepStmt.setInt(5, userId);
            insertExportDetailsPrepStmt.setTimestamp(6, new Timestamp(new Date().getTime()));
            insertExportDetailsPrepStmt.executeUpdate();             
            Statement getTopIdStatement = connection.createStatement();
            String id = null;
            if (tableName.equals(EXPORT_DETAILS_TABLE_NAME)) {
                id = EXPORT_DETAILS_TABLE_ID;
            } else if (tableName.equals(TSEARCH_EXPORT_DETAILS_TABLE_NAME)) {
                id = TSEARCH_EXPORT_DETAILS_TABLE_ID;
            } else {
                id = OVP_TABLE_ID;
            }
            String query = "SELECT top 1 " + id + " from " + tableName + " order by " + id + " desc";
            ResultSet getTopIdResultSet = getTopIdStatement.executeQuery(query);
            if (getTopIdResultSet.next()) {
                if (tableName.equals(EXPORT_DETAILS_TABLE_NAME)) {
                    exportDetailsId = getTopIdResultSet.getInt(1);
                } else if (tableName.equals(TSEARCH_EXPORT_DETAILS_TABLE_NAME)) {
                    tsearchExportDetailsId = getTopIdResultSet.getInt(1);
                } else {
                     
                    outputValidationId = getTopIdResultSet.getInt(1);
                    System.out.println("outputValidationId  " + outputValidationId);
                }
            }

        } catch (SQLException ex) {
            Logger.getLogger(Command_export_data.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Update the Export details after completion of export.
     * @param tableName Table name which is going to be updated.
     *        The table could be either export_details or  output_validations_details
     */
    private void updateExportDetails(String tableName) {
        try {            
            int id = 0;
            String tableId = null;
            if (tableName.equals(EXPORT_DETAILS_TABLE_NAME)) {
                tableId = EXPORT_DETAILS_TABLE_ID;
                id = exportDetailsId;
                fileName = data.project_name + "_" + data.volume_name + ".zip";
            } else if (tableName.equals(TSEARCH_EXPORT_DETAILS_TABLE_NAME)) {
                tableId = TSEARCH_EXPORT_DETAILS_TABLE_ID;
                id = tsearchExportDetailsId;
                fileName = data.project_name + "_" + data.volume_name + ".zip";
            } else {

                tableId =OVP_TABLE_ID;

                id = outputValidationId;                   
                SimpleDateFormat format = new SimpleDateFormat("yyyy_MM_dd_hh_mm");
                String strDate = format.format(new Date());
                fileName = "ovp_" + projectName + "_" + data.volume_name + "_" + strDate + ".xls";
                Statement st = connection.createStatement();
                st.executeUpdate("UPDATE output_validations_details set result = '" + validationResult + "' where  " + tableId + " = " + id);
            }
            
            PreparedStatement updateExportDetails = connection.prepareStatement("UPDATE " + tableName + " SET status = ? ," +
                    " file_name = ? " +
                    "WHERE " + tableId + " = ?");
            updateExportDetails.setString(1, "Finished");
            updateExportDetails.setString(2, fileName);
            updateExportDetails.setInt(3, id);
            updateExportDetails.executeUpdate();
        } catch (SQLException sqlexc) {
            Logger.getLogger(Command_export_data.class.getName()).log(Level.SEVERE, null, sqlexc);
        }
    }
    //method which is called by the quartz schedular, which will execute export process and OVP
   // public void run() {
    public void execute(JobExecutionContext context) throws JobExecutionException {
        try {          
             System.out.println(" execute method============");     
             
             dbTask = new DBTask();
             connection = dbTask.getConnection();
             user = new UserTask(); 

             executeServerQueueProcess(dbTask,user);

             connection.close();

            
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }


    public void executeServerQueueProcess(DBTask dbTask,UserTask userTask) {
        
            data = getExportData(dbTask, userTask);


            if (data != null) {
                try {

                    Statement st = connection.createStatement();
                    isUnitizeOnly = Boolean.parseBoolean(data.unitizeOnly);
                    isQCBatchesAllowed = Boolean.parseBoolean(data.isQCBatchAllowed);
                    projectName = data.project_name;
                    volume_id = data.volume_id;
                    startBatchNumber = data.batch_number;
                    endBatchNumber = data.end_batch_number;
                    if (isQCBatchesAllowed) {
                        batchStatus = StatusConstants.S_CODING;
                    } else {
                        batchStatus = StatusConstants.S_QCOMPLETE;
                    }

                    PreparedStatement pstmt = connection.prepareStatement(SQLQueries.SEL_USER_NAME);
                    pstmt.setInt(1, user.getUsersId());
                    ResultSet getUser = pstmt.executeQuery();
                    if (getUser.next()) {
                        userName = getUser.getString(1);
                    } else {
                        userName = "";
                    }

                    ResultSet getProjectIDResultSet = st.executeQuery("select project_id from project where project_name = '" + projectName + "'");
                    getProjectIDResultSet.next();
                    project_id = getProjectIDResultSet.getInt(1);
                    getProjectfields();


                    PreparedStatement updateServerTaskQueueStatus = connection.prepareStatement("Update servertaskqueue set status = ? where servertaskqueue_id = ?");
                    updateServerTaskQueueStatus.setString(1, "Complete");
                    updateServerTaskQueueStatus.setInt(2, servertaskqueue_id);
                    updateServerTaskQueueStatus.executeUpdate();
                    
                }catch(Exception e){
                 e.printStackTrace();   
                }
            }
        }
    
    
    //method will execute the Serverqueue Process (Export/OVP)
    private ExportData getExportData(DBTask dbTask, UserTask userTask) {
        ExportData eData = null;
        try {
            Connection con = dbTask.getConnection();
            Statement getQueueTask = dbTask.getStatement();
            //writer = getMessageWriter(multiOutStream.newStream());
            String serverIp = InetAddress.getLocalHost().toString();
            String split[] = serverIp.split("/");
            System.out.println("systemIP========" + split[1]);

            ResultSet rs1 = getQueueTask.executeQuery("select servertaskqueue_id,status,raised_by from servertaskqueue where status = 'InQueue' or status ='Progress'");
            int count = 0;
            if(rs1.next()) {
                 servertaskqueue_id = rs1.getInt(1);
                String status = rs1.getString(2);
                userId = rs1.getInt(3);

                if (status.equals("InQueue")) {
                    PreparedStatement updateServerTaskQueueToProgress = con.prepareStatement("UPDATE servertaskqueue set status =?" +
                            " where  servertaskqueue_id=?");
                    updateServerTaskQueueToProgress.setString(1, "Progress");
                    updateServerTaskQueueToProgress.setInt(2, servertaskqueue_id);
                    updateServerTaskQueueToProgress.executeUpdate();
                }
                ResultSet rs2 = getQueueTask.executeQuery("select * from servertaskqueue where status = 'Progress' and " +
                        " server_ip ='" + split[1] + "' and servertaskqueue_id =" + servertaskqueue_id);

                if (rs2.next()) {
                    // while(rs2.next()){                
                    Statement getExportData = con.createStatement();
                    ResultSet rs = getExportData.executeQuery("select * from export_data ED inner join export E " +
                            " on ED.export_name = E.export_name  where E.export_name " +
                            " = (select export_name from export_data where servertaskqueue_id=" + servertaskqueue_id +
                            ") and ED.servertaskqueue_id=" + servertaskqueue_id);
                    while (rs.next()) {
                        eData = new ExportData();
                        eData.delimiter_set_name = rs.getString(3);
                        eData.project_name = rs.getString(4);
                        eData.volume_id = rs.getInt(5);
                        eData.volume_name = rs.getString(6);
                        eData.batch_number = rs.getInt(7);
                        eData.end_batch_number = rs.getInt(8);

                        eData.isQCBatchAllowed = rs.getString(12);
                        eData.isDoculex = rs.getString(13);
                        eData.isOpticon = rs.getString(14);
                        eData.isSummation = rs.getString(15);
                        eData.isBRS = rs.getString(16);
                        eData.doValidation = rs.getString(17);
                        System.out.println("data.doValidation" + eData.doValidation);
                        eData.isTSearchExport = rs.getString(18);
                        //data.force = rs.getString(20);
                        eData.uppercase = rs.getString(21);
                        eData.uppercase_names = rs.getString(22);
                        eData.name_mask1 = rs.getString(23);
                        eData.name_mask2 = rs.getString(24);
                        eData.name_mask3 = rs.getString(25);
                        eData.name_mask4 = rs.getString(26);
                        eData.field_delimiter = rs.getString(27);
                        eData.text_qualifier = rs.getString(28);
                        eData.value_separator = rs.getString(29);
                        eData.date_format = rs.getString(30);
                        eData.missing_date = rs.getString(31);
                        eData.missing_year = rs.getString(32);
                        eData.missing_month = rs.getString(33);
                        eData.missing_day = rs.getString(34);
                        eData.missing_date_character = rs.getString(35);
                        eData.brs_format = rs.getString(36);
                    }
                }
            }
        } catch (UnknownHostException ex) {
            Logger.getLogger(Command_export_data.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(Command_export_data.class.getName()).log(Level.SEVERE, null, ex);
        }
        return eData;
    }
    
   
    /*
     *Writing the date to the excel sheet    
     */
    public void writeReportToExcelSheet(UserTask user, List<ValidationReport> list) {
        
        String sheetName = "Output Validations Report";
        SimpleDateFormat format = new SimpleDateFormat("yyyy_MM_dd_hh_mm");
        String strDate = format.format(new Date());
        String filename = "ovp_" + projectName + "_" + data.volume_name + "_" + strDate + ".xls";

        String xlsFilePath = user.getContextPath() + File.separator + "UPLOAD" + File.separator +
                "$Export" + File.separator +
                projectName + "_" +
                data.volume_name + File.separator +
                filename;

        //create the directories if it does not exits.
        int fslash = xlsFilePath.lastIndexOf("/");
        int bslash = xlsFilePath.lastIndexOf("\\");
        int mark = fslash > bslash ? fslash : bslash;
        String dirs = xlsFilePath.substring(0, mark);
        File fDir = new File(dirs);
        if (!fDir.exists()) {
            fDir.mkdirs();
        }

        //header for the  main sheet
        String[] headerList = {
            "Serial No.",
            "Field Name",
            "Batch Number",
            "Document Number",
            "Field Value",
            "Function Name",
            "Error Message",
            "Error Type"
        };

        int rowSize = headerList.length;
        HSSFWorkbook workBook = new HSSFWorkbook();
        HSSFSheet sheet = workBook.createSheet(sheetName);

        //create a style for this header columns
        HSSFCellStyle columnHeaderStyle = workBook.createCellStyle();
        columnHeaderStyle.setFillBackgroundColor((short) 2);

        //ting font style 
        HSSFFont font = workBook.createFont();
        font.setColor(HSSFFont.COLOR_RED);
        columnHeaderStyle.setFont(font);

        /* creating Headers for the sheet =================================== */
        //user header -----------             
        HSSFRow userHeaderRow = sheet.createRow((short) 0);
        //first cell for the user row.
        HSSFCell userCell1 = userHeaderRow.createCell((short) 0);
        userCell1.setCellStyle(columnHeaderStyle);
        HSSFRichTextString user_txt = new HSSFRichTextString("User Name   : ");
        userCell1.setCellValue(user_txt);
        //second cell for the user row.
        HSSFCell userCell2 = userHeaderRow.createCell((short) 1);
        HSSFRichTextString user_Name = new HSSFRichTextString(userName);
        userCell2.setCellValue(user_Name);
        //-------------------------

        //runDate header -----------             
        HSSFRow runDateHeaderRow = sheet.createRow((short) 1);
        //first cell for the runDate row.
        HSSFCell runDateCell1 = runDateHeaderRow.createCell((short) 0);
        runDateCell1.setCellStyle(columnHeaderStyle);
        HSSFRichTextString runDate_txt = new HSSFRichTextString("Run Date   : ");
        runDateCell1.setCellValue(runDate_txt);
        //second cell for the runDate row.
        HSSFCell runDateCell2 = runDateHeaderRow.createCell((short) 1);
        HSSFRichTextString runDate_v = new HSSFRichTextString(new Date().toString());
        runDateCell2.setCellValue(runDate_v);
        //-------------------------

        //project header----------
        HSSFRow projectHeaderRow = sheet.createRow((short) 2);
        //first cell for the project row
        HSSFCell projectCell1 = projectHeaderRow.createCell((short) 0);
        projectCell1.setCellStyle(columnHeaderStyle);
        HSSFRichTextString project_txt = new HSSFRichTextString("PROJECT   : ");
        projectCell1.setCellValue(project_txt);
        //second cell for the project row
        HSSFCell projectCell2 = projectHeaderRow.createCell((short) 1);
        HSSFRichTextString project_Name = new HSSFRichTextString(projectName);
        projectCell2.setCellValue(project_Name);
        //-------------------------

        //volume header -----------             
        HSSFRow volumeHeaderRow = sheet.createRow((short) 3);
        //first cell for the volume row.
        HSSFCell volumeCell1 = volumeHeaderRow.createCell((short) 0);
        volumeCell1.setCellStyle(columnHeaderStyle);
        HSSFRichTextString volume_txt = new HSSFRichTextString("VOLUME   : ");
        volumeCell1.setCellValue(volume_txt);
        //second cell for the volume row.
        HSSFCell volumeCell2 = volumeHeaderRow.createCell((short) 1);
        HSSFRichTextString volume_Name = new HSSFRichTextString(data.volume_name);
        volumeCell2.setCellValue(volume_Name);
        //======================================================================

        HSSFRow columnHeaderRow = sheet.createRow((short) 5);
        HSSFCell colHeading;
        for (int i = 0; i < rowSize; i++) {
            colHeading = columnHeaderRow.createCell((short) i);
            colHeading.setCellStyle(columnHeaderStyle);
            HSSFRichTextString txt = new HSSFRichTextString(headerList[i]);
            colHeading.setCellValue(txt);
        }

        HSSFCell cell;
        HSSFRichTextString txt;

        int i = 6;
        int j = 1;
        for (ValidationReport vd : list) {
            try {

                HSSFRow row = sheet.createRow((short) i);

                //Serial Number
                cell = row.createCell((short) 0);
                txt = new HSSFRichTextString(new Integer(j).toString());
                cell.setCellValue(txt);

                //Project Fields Name
                cell = row.createCell((short) 1);
                txt = new HSSFRichTextString(vd.getFieldName());
                cell.setCellValue(txt);

                //Batch Number
                cell = row.createCell((short) 2);
                txt = new HSSFRichTextString((new Integer(vd.getBatchNumber())).toString());
                cell.setCellValue(txt);

                //Document Number
                cell = row.createCell((short) 3);
                txt = new HSSFRichTextString(vd.getDocumentNumber());
                cell.setCellValue(txt);


                //Fields Value
                cell = row.createCell((short) 4);
                txt = new HSSFRichTextString(vd.getFieldValue());
                cell.setCellValue(txt);

                //Validaton Functons Name
                cell = row.createCell((short) 5);
                txt = new HSSFRichTextString(vd.getFunctionName());
                cell.setCellValue(txt);

                //Error Message
                cell = row.createCell((short) 6);
                txt = new HSSFRichTextString(vd.getErrorMessage());
                cell.setCellValue(txt);

                //Error Type
                cell = row.createCell((short) 7);
                txt = new HSSFRichTextString(vd.getErrorType());
                cell.setCellValue(txt);

            } catch (Exception e) {
                logger.error("Exception while wriritng report in a xls sheet" + e);
                StringWriter sw = new StringWriter();
                e.printStackTrace(new PrintWriter(sw));
                logger.error(sw.toString());
            }
            i++;
            j++;
        }

        try {
            FileOutputStream fileOut = new FileOutputStream(new File(xlsFilePath));
            //Making column autosize according to the data.        
            sheet.autoSizeColumn((short) 0);
            sheet.autoSizeColumn((short) 1);
            sheet.autoSizeColumn((short) 2);
            sheet.autoSizeColumn((short) 3);
            sheet.autoSizeColumn((short) 4);
            sheet.autoSizeColumn((short) 5);
            sheet.autoSizeColumn((short) 6);
            sheet.autoSizeColumn((short) 7);
            workBook.write(fileOut);
            fileOut.close();
        } catch (Exception e) {
            logger.error("Exception while writing report in a xls sheet" + e);
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            logger.error(sw.toString());
        }
    }

    public static void setContextPath(String contextPath) {
        Command_export_data.contextPath = contextPath;
    }

//    public String getExportPath() {
//
//        Connection con = dbTask.getConnection();
//        String path = null;
//        try {
//            Statement getQueueTask = dbTask.getStatement();
//            ResultSet _rs = getQueueTask.executeQuery("select P.project_name, V.volume_name From  Volume V INNER JOIN " +
//                    "Project P ON V.project_id = P.project_id WHERE V.volume_id = " + volume_id + "");
//            if (_rs.next()) {
//                path = _rs.getString(1) + File.separator + _rs.getString(2);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return path;
//    }    
}

