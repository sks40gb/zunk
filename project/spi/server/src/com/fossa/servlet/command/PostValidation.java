/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fossa.servlet.command;

import bsh.Interpreter;
import com.fossa.servlet.common.CommonLogger;
import com.fossa.servlet.common.SQLQueries;
import com.fossa.servlet.dao.FossaDataSource;
import com.fossa.servlet.server.valueobjects.ChildData;
import com.fossa.servlet.server.valueobjects.FieldData;
import com.fossa.servlet.server.valueobjects.ProjectData;
import com.fossa.servlet.server.valueobjects.FunctionData;
import com.fossa.servlet.server.valueobjects.VolumeData;
import com.fossa.servlet.server.valueobjects.PostValidationReportsData;
import com.fossa.servlet.session.UserTask;
import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

/**
 * This class handles the requests for reading/writing the excel sheets for PVR
 * @author sunil
 */
public class PostValidation {

    private static final String PASS = "PASS";
    private static final String FAIL = "FAIL";
    private static final String PVR = "post validation report";
    private int volumeId;
    private int projectId;
    private int postValidationId;
    private String projectName;
    private String volumeName;
    private String fileName;
    private String userName;
    private String runDate;
    private String IdString;
    public static final String BLANK = "";
    private Object error = null;
    private String validationFunctions;
    private ProjectData projectData;
    private VolumeData volumeData;
    private PreparedStatement pstmt = null;
    private ResultSet rs = null;
    private static Connection connection;
    private Set<PostValidationReportsData> postValidationDataSet;
    private static Logger logger = Logger.getLogger("com.fossa.servlet.command");

    /**
     * @param volumeId - Volume Id
     * @param projectId - Project Id
     * @param IdString - String having the Fileds Ids and corresponging Projects Ids.
     */
    public PostValidation(int volumeId, int projectId, int postValidationId, String IdString) {
        try {
            connection = FossaDataSource.getConnection();
        } catch (Exception e) {
            CommonLogger.printExceptions(this, "Exception while establishing a data source connection" , e);
        }
        this.volumeId = volumeId;
        this.projectId = projectId;
        this.IdString = IdString;
        this.postValidationId = postValidationId;
        // fill required Project Data
        fillRecordToProjectData();
        // fill required Volume Data
        fillRecordToVolumeData();
        getProjectVolumeAndFileName();
    }

    public void getProjectVolumeAndFileName() {
        try {
            pstmt = connection.prepareStatement(SQLQueries.SEL_NAMES_PVR);
            pstmt.setInt(1, postValidationId);
            pstmt.executeQuery();
            rs = pstmt.getResultSet();
            while (rs.next()) {
                projectName = rs.getString(1);
                volumeName = rs.getString(2);
                fileName = rs.getString(3);
                userName = rs.getString(4);
                runDate = rs.getString(5);
            }
        } catch (Exception e) {
            CommonLogger.printExceptions(this, "Exception while getting project & volume name for PVR", e);
        }
    }

    public static Connection getConnection() {
        return connection;
    }

    /*
     *Writing the date to the excel sheet    
     */
    public void writeReportToExcelSheet(UserTask user) {
        if (postValidationDataSet == null) {
            return;
        }
        String sheetName = PVR;
        //String xlsFilePath = "/home/sunil/Desktop/zzzz.xls";
        String xlsFilePath = user.getContextPath() + File.separator +
                "UPLOAD" + File.separator +
                projectName + File.separator +
                volumeName + File.separator +
                fileName;

        //create the directories if it does not exits.
        int fslash = xlsFilePath.lastIndexOf("/");
        int bslash = xlsFilePath.lastIndexOf("\\");
        int mark = fslash > bslash ? fslash : bslash;
        String dirs = xlsFilePath.substring(0, mark);
        File fDir = new File(dirs);
        fDir.mkdirs();

        //header for the  main sheet
        String[] headerList = {
            "Post Validaton Id",
            "Batch Number",
            "Document Number",
            "Project Fields",
            "Validaton Functons",
            "Result",
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
        HSSFRichTextString runDate_v = new HSSFRichTextString(runDate);
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
        HSSFRichTextString volume_Name = new HSSFRichTextString(volumeName);
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
        for (PostValidationReportsData pvd : postValidationDataSet) {
            try {

                HSSFRow row = sheet.createRow((short) i);

                //PostValidaton Id
                cell = row.createCell((short) 0);
                txt = new HSSFRichTextString(new Integer(postValidationId).toString());
                cell.setCellValue(txt);

                //Batch Number
                cell = row.createCell((short) 1);
                txt = new HSSFRichTextString((new Integer(pvd.getBatchNumber())).toString());
                cell.setCellValue(txt);

                //Document Number
                cell = row.createCell((short) 2);
                txt = new HSSFRichTextString(pvd.getDocumentNumber(volumeId));
                cell.setCellValue(txt);

                //Project Fields Name
                cell = row.createCell((short) 3);
                txt = new HSSFRichTextString(pvd.getFieldsName());
                cell.setCellValue(txt);

                //Validaton Functons Name
                cell = row.createCell((short) 4);
                txt = new HSSFRichTextString(pvd.getFunctionName());
                cell.setCellValue(txt);

                //Status
                cell = row.createCell((short) 5);
                txt = new HSSFRichTextString(pvd.getStatus());
                cell.setCellValue(txt);

            } catch (Exception e) {
                logger.error("Exception while wriritng report in a xls sheet" + e);
                StringWriter sw = new StringWriter();
                e.printStackTrace(new PrintWriter(sw));
                logger.error(sw.toString());
            }
            i++;
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
            sheet.autoSizeColumn((short) 8);
            workBook.write(fileOut);
            fileOut.close();
        } catch (Exception e) {
             logger.error("Exception while writing report in a xls sheet" + e);
             StringWriter sw = new StringWriter();
             e.printStackTrace(new PrintWriter(sw));
             logger.error(sw.toString());
        }
    }

    /**
     * Get the required data related to the project and fill to the projectData
     * Put the parameter value for the field.
     */
    private void fillRecordToProjectData() {
        projectData = new ProjectData(projectId);
        //split the Fields to separate individual Field
        String[] fieldIdWithFunctionIds = IdString.split("#");
        int fieldId;
        String functionIds[];
        //Iterate over all Fields
        for (String individual : fieldIdWithFunctionIds) {
            //get Field Id
            fieldId = new Integer(individual.split("-")[0]);
            //get function Id
            functionIds = individual.split("-")[1].split(",");
            FieldData fieldData = new FieldData(new Integer(fieldId));
            //Iterate over all Functions for the Field
            for (String functionId : functionIds) {
                int validation_mapping_master_id = -1;
                int fun_id = new Integer(functionId);
                FunctionData validation = new FunctionData(fun_id);
                try {
                    // set the parameter for the validation function according to
                    // projectfields_id
                    String parameter = "";
                    pstmt = connection.prepareStatement(SQLQueries.SEL_VALIDATION_MAPPING_MASTER_PVR);
                    pstmt.setInt(1, fun_id);
                    pstmt.executeQuery();
                    rs = pstmt.getResultSet();
                    if (rs.next()) {
                        validation_mapping_master_id = rs.getInt(1);
                    }

                    pstmt = connection.prepareStatement(SQLQueries.SEL_VALIDATION_MAPPING_DETAILS_PVR);
                    pstmt.setInt(1, fieldId);
                    pstmt.setInt(2, validation_mapping_master_id);
                    pstmt.executeQuery();
                    rs = pstmt.getResultSet();
                    if (rs.next()) {
                        parameter = rs.getString(1);
                        validation.setParameter(parameter);
                    }
                } catch (Exception e) {
                    CommonLogger.printExceptions(this, "Exception while filling record to project data" , e);
                }
                fieldData.putInValidationMap(fun_id, validation);
            }
            //put the Field to Project Data.
            projectData.putInFieldMap(fieldData.getFieldName(), fieldData);
        }
    }

    /**
     * Writing PVR to DB
     */
    public void writeReportToDB() {
        try {
            //insert the data to the post_validation_details table
            for (PostValidationReportsData pvd : postValidationDataSet) {
                pstmt = connection.prepareStatement(SQLQueries.INS_POST_VALIDATION_DETAILS_PVR);
                pstmt.setInt(1, pvd.getPostValidatonId());
                pstmt.setInt(2, pvd.getBatchId());
                pstmt.setInt(3, pvd.getChildId());
                pstmt.setInt(4, pvd.getProjectFieldsId());
                pstmt.setInt(5, pvd.getValidationFuncitonsMasterId());
                pstmt.setString(6, pvd.getStatus());
                pstmt.executeUpdate();
            }
            pstmt = connection.prepareStatement(SQLQueries.UPD_POST_VALIDATION_DETAILS_PVR);
            pstmt.setInt(1, postValidationId);
            pstmt.executeUpdate();
        } catch (Exception e) {
            logger.error("Exception while saving post validations report" + e);
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            logger.error(sw.toString());
        }

    }

    /**
     * Get required data for volume for PVR
     */
    private void fillRecordToVolumeData() {
        volumeData = new VolumeData(volumeId, projectData);
    }

    /**
     * Running script and get the status whether it is PASS or FAIL.
     * Generate the report
     */
    public void runScript() {
        //postValidationDataList = new ArrayList<PostValidationDetailsData>();
        postValidationDataSet = new HashSet<PostValidationReportsData>();

        //iterate over each child for the volume
        for (ChildData childMap : volumeData.getChildMap().values()) {
            hasValidationFailed(childMap, false);
        }

        for (PostValidationReportsData pvd : postValidationDataSet) {
            try {
                String function_name = null;
                //pstmt = connection.prepareStatement("SELECT batch_name FROM batch WHERE batch_id = ? ");
                pstmt = connection.prepareStatement(SQLQueries.SEL_VALIDATION_FUNCTIONS_MASTER_PVR);
                pstmt.setInt(1, pvd.getValidationFuncitonsMasterId());
                pstmt.executeQuery();
                rs = pstmt.getResultSet();
                while (rs.next()) {
                    function_name = rs.getString(1);
                }
            } catch (Exception e) {
                logger.error("Exception while run script in PVR" + e);
                StringWriter sw = new StringWriter();
                e.printStackTrace(new PrintWriter(sw));
                logger.error(sw.toString());
            }
        }

    }

    /**
     * Checking functions for the child is working or not.
     * @param childMap - Child Record
     * @param unitize - is Unitize ?
     * @return 
     */
    private String hasValidationFailed(ChildData childMap, boolean unitize) {

        int batchId = childMap.getBatchId();
        int childId = childMap.getChildId();
        
        validationFunctions = " private String message = null;" +
                " private char[] charArray;" +
                " private String[] tokens =null;" +
                " java.util.HashMap projectFieldsMap = new java.util.HashMap();" +
                " private String codedValue=null;" +
                " private String allParameters=null;" +
                " private String [] tokenArray ;" +
                " public String execute(String methodName, String input, String errorMessage,String value, String recordTokens, String allParametersValues) {" +
                " if(recordTokens != null) { " +
                "    tokens = recordTokens.split(\"\\\\|\");" +
                "    for(String token : tokens){ " +
                "      if(token.split(\"★\").length > 1){" +
                "         projectFieldsMap.put(token.split(\"★\")[0],token.split(\"★\")[1]); " +
                "      } " +
                "      else { " +
                "         projectFieldsMap.put(token.split(\"★\")[0],\"\");" +
                "      }" +
                "     } " +
                " } " +
                " codedValue = input;" +
                " allParameters = allParametersValues; " +
                " message = this.invokeMethod( methodName, new Object [] {errorMessage,value} ); " +
                " return message;" +
                " }";

        if (!unitize) {
            String allFieldsValue = "";
            boolean isFirstTime = true;
            Map<String, FieldData> fieldMap = childMap.getFieldMap();
            for (String key : fieldMap.keySet()) {
                String name = key;
                String value = childMap.getFieldMap().get(key).getFieldValue();
                // the format of allFieldsValue is --> name ★ value | name ★ value | name ★ value                
                allFieldsValue = ((isFirstTime) ? (name + "★" + value) : (allFieldsValue + "|" + name + "★" + value));
                isFirstTime = false;
            }
            allFieldsValue = allFieldsValue.replace("\\", "\\\\");
            for (String key : fieldMap.keySet()) {
                String name = key;
                String value = childMap.getFieldMap().get(key).getFieldValue();

                //setting the field_id;
                int fieldId = childMap.getFieldMap().get(key).getFieldId();
                String fieldName = childMap.getFieldMap().get(key).getFieldName();

                List<FunctionData> validationList = fieldMap.get(name).getValidationList();

                //saparate all input in inputList                
                if (validationList != null) {
                    for (FunctionData validation : validationList) {

                        int validationFuncitonsMasterId = validation.getFunctionId();
                        String functionName = validation.getFunctionName();
                        String functionBody = validation.getFunctionBody();
                        String parameter = validation.getParameter().replace("\\", "\\\\");
                        String errorMessage = validation.getErrorMessage().replace("\\", "\\\\");                        
                        String allParameters = "";
                        Interpreter interpreter = new Interpreter();
                        try {
                            String scriptFileContent = validationFunctions + "\n" + functionBody;
                            String paramArray[] = parameter.split(",");
                            if (null != value) {
                                StringTokenizer tokens = null;
                                //if field name is AUTHOR,FIRSTNAME,LASTNAME,MIDDLEINITIAL,CC,RE - then 
                                //input will be saparated by ( ; & ,) else only by ( ; )
                                if (name.equalsIgnoreCase("AUTHOR") |
                                        name.equalsIgnoreCase("FIRSTNAME") |
                                        name.equalsIgnoreCase("LASTNAME") |
                                        name.equalsIgnoreCase("MIDDLEINITIAL") |
                                        name.equalsIgnoreCase("CC") |
                                        name.equalsIgnoreCase("RE")) {
                                    //tokens = new StringTokenizer(value, ";,/");
                                    tokens = new StringTokenizer(value, ";,");
                                } //if the field type is not name, the input will be saparated by "," only .
                                else {
                                    tokens = new StringTokenizer(value, ";");
                                }
                                boolean isNext = true;
                                String inputValue = null;
                                if (tokens.countTokens() > 0) {
                                    while (tokens.hasMoreTokens() && isNext) {
                                        inputValue = (String) tokens.nextToken().trim();
                                        //to handle the backword slash (\) . used for handling the specail character.
                                        inputValue = inputValue.replace("\\", "\\\\");

                                        // if there is a parameter having value -""  set default param.
                                        if (paramArray.length == 1 && ((String) paramArray[0]).equals("")) {
                                            paramArray[0] = "DeFaUlTvAlUe1f2dd15sddTdklKLL";
                                        }
                                        for (int j = 0; j < paramArray.length; j++) {
                                            if (paramArray[j].equals(BLANK)) {
                                                continue;
                                            }
                                            allParameters = ((j == 0) ? paramArray[0] : allParameters + ":" + paramArray[j]);
                                        }
                                        allParameters = allParameters.replace("\\", "\\\\");
                                        for (int j = 0; j < paramArray.length; j++) {
                                            //ignore the input if there is empty space in between two comma(,).
                                            if (paramArray[j].equals(BLANK)) {
                                                continue;
                                            }
                                            String param = paramArray[j];
                                            try {
                                                error = interpreter.eval(scriptFileContent + "execute(\"" + functionName + "\",\"" + inputValue + "\",\"" + errorMessage + "\",\"" + param + "\",\"" + allFieldsValue + "\",\"" + allParameters + "\")");


                                            } catch (Exception e) {
                                                logger.error("Exception while in PVR" + e);
                                                StringWriter sw = new StringWriter();
                                                e.printStackTrace(new PrintWriter(sw));
                                                logger.error(sw.toString());
                                            }

                                            if (error != null) {
                                                //isNext = false;
                                                break;
                                            }
                                        }
                                    }
                                } else {
                                    inputValue = BLANK;
                                    // if there is a parameter having value -""  set default param.
                                    if (paramArray.length == 1 && ((String) paramArray[0]).equals(BLANK)) {
                                        paramArray[0] = "DeFaUlTvAlUe1f2dd15sddTdklKLL";
                                    }

                                    for (int j = 0; j < paramArray.length; j++) {
                                        if (paramArray[j].equals(BLANK)) {
                                            continue;

                                        }
                                        allParameters = ((j == 0) ? paramArray[0] : allParameters + ":" + paramArray[j]);
                                    }
                                    allParameters.replace("\\", "\\\\");

                                    for (int j = 0; j < paramArray.length; j++) {
                                        //ignore the input if there is empty space in between two comma(,).
                                        if (paramArray[j].equals(BLANK)) {
                                            continue;
                                        }
                                        String param = paramArray[j];
                                        param.replace("\\", "\\\\");
                                        error = interpreter.eval(scriptFileContent + "execute(\"" + functionName + "\",\"" + inputValue + "\",\"" + errorMessage + "\",\"" + param + "\",\"" + allFieldsValue + "\",\"" + allParameters + "\")");


                                        if (error != null) {
                                            //isNext = false;                                            
                                            break;
                                        }
                                    }
                                }
                            }

                        } catch (Exception e) {
                            CommonLogger.printExceptions(this, "Exception while handling the PVR" , e);
                        }

                        PostValidationReportsData pvd = new PostValidationReportsData();
                        pvd.setBatchId(batchId);
                        pvd.setChildId(childId);
                        pvd.setProjectFieldsId(fieldId);
                        pvd.setFieldsName(fieldName);
                        pvd.setValidationFuncitonsMasterId(validationFuncitonsMasterId);
                        pvd.setFunctionName(functionName);
                        String status = (error == null ? PASS : FAIL);
                        pvd.setStatus(status);
                        postValidationDataSet.add(pvd);
                    }
                }
            }
        }
        return null;
    }
}