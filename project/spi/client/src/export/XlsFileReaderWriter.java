/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package export;

/**
 *
 * @author sunil
 */
import valueobjects.Feedback;
import valueobjects.FileError;
import common.msg.MessageConstants;
import java.awt.Component;
import java.io.FileNotFoundException;
import java.util.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFClientAnchor;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFPatriarch;
import org.apache.poi.hssf.usermodel.HSSFPicture;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class XlsFileReaderWriter {

    private String xlsFilePath;
    private int resolution = MessageConstants.RES_HIGH;
    private String[] sheets = null;
    String filePath;
    String sheetName;
    List<Feedback> feedbackList;
    HSSFWorkbook workBook;

    public static void main(String[] args) throws FileNotFoundException, IOException {

        XlsFileReaderWriter filereader = new XlsFileReaderWriter("/home/sunil/Desktop/test.xls");
        try {
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public XlsFileReaderWriter() {
    }
    
    /*
     * this construtor is used while reading the excel file
     * @param xlsFilePath - path of the excel sheet going to be read.
     */
    public XlsFileReaderWriter(String xlsFilePath) throws FileNotFoundException, IOException {
        this.xlsFilePath = xlsFilePath;
        this.sheets = parseSheetsInFile(xlsFilePath);
    }


    /*
     * It returns no of rows in excel sheet.
     * @param sheetNumber - excel sheet whose rows going to be count.
     */ 
    public int getRows(int sheetNumber) throws FileNotFoundException, IOException {
        POIFSFileSystem poif_fs = new POIFSFileSystem(new FileInputStream(this.xlsFilePath));
        HSSFWorkbook workbook = new HSSFWorkbook(poif_fs);
        HSSFSheet sheet = workbook.getSheet(sheets[sheetNumber]);
        return sheet.getLastRowNum();
    }
    
    /*
     * it validate the excel file. It validates -
     *  1. column name
     *  2. column values of batch_id, job_id, docName, tagName
     *  @param sheetNumber - Sheet Number 
     */ 
    public ArrayList<FileError> validateXlsSheetFile(int sheetNumber) throws FileNotFoundException, IOException {

        Object sheetHeading[] = getHeadings(sheetNumber);
        ArrayList<FileError> errorList = new ArrayList<FileError>();
        String error = null;
        String errorType = null;
        
        //checking no of columns of the existing excel sheet
        if (sheetHeading.length != Feedback.totolColumns) {
            errorType = "Invalid Report";
            error = "The report is missing mandatory columns.";
            errorList.add(new FileError(errorType, error));
            return errorList;
        }
        
        //matching the column name
        if (!((isHeadMatch(sheetHeading[0], Feedback.BATCH_ID)) &&
                (isHeadMatch(sheetHeading[1], Feedback.BOX_ID)) &&
                (isHeadMatch(sheetHeading[2], Feedback.DOC_N)) &&
                (isHeadMatch(sheetHeading[3], Feedback.TAG_NAME)) &&
                (isHeadMatch(sheetHeading[4], Feedback.TAG_VALUE)) &&
                (isHeadMatch(sheetHeading[5], Feedback.CORRECT_TAG_VALUE)) &&
                (isHeadMatch(sheetHeading[6], Feedback.AUDIT_PROBLEM_DESC_CODE)) &&
                (isHeadMatch(sheetHeading[7], Feedback.PROB_TEXT)))) {
            errorType = "Invalid Report";
            error = "Column Name does not match.";
            errorList.add(new FileError(errorType, error));
            return errorList;
        }

        // validating column of axcel sheet 
        POIFSFileSystem poif_fs = new POIFSFileSystem(new FileInputStream(this.xlsFilePath));
        HSSFWorkbook workbook = new HSSFWorkbook(poif_fs);
        HSSFSheet sheet = workbook.getSheet(sheets[sheetNumber]);
        HSSFRow row; 
        Object cellValue;
        boolean failure = false;

        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            row = sheet.getRow(i);
            if (row != null) {
                try {

                    if ((cellValue = row.getCell((short) 0)) == null || (cellValue = row.getCell((short) 0)).toString().trim().toString().trim().equals("")) {
                        failure = true;
                        errorType = "Invalid BATCHID Value.";
                        error = "Either BATCH ID column has blank value or is not available on row . Please check the Excel Report File.";
                        errorList.add(new FileError(errorType, error));
                    }

                    if ((cellValue = row.getCell((short) 1)) == null || (cellValue = row.getCell((short) 1)).toString().trim().equals("")) {
                        failure = true;
                        errorType = "Missing JOBNAME on Excel File.";
                        error = "Either BOX ID column has blank value or is not available on row . Please check the Excel Report File.";
                        errorList.add(new FileError(errorType, error));
                    }


                    if ((cellValue = row.getCell((short) 2)) == null || (cellValue = row.getCell((short) 2)).toString().trim().equals("")) {
                        failure = true;
                        errorType = "Invalid DOCN Value";
                        error = "DOCN Value  does not exists.";
                        errorList.add(new FileError(errorType, error));
                    }

                    if ((cellValue = row.getCell((short) 3)) == null || (cellValue = row.getCell((short) 3)).toString().trim().equals("")) {
                        failure = true;
                        errorType = "Invalid TAGNAME";
                        error = "TAGNAME has no corresponding value or does not exist.";
                        errorList.add(new FileError(errorType, error));
                    }

//               if((cellValue = row.getCell((short)4)) == null || (cellValue = row.getCell((short)4)).toString().trim().equals(""))
//                {
//                    failure = true;
//                    errorType = "Invalid TAGNAME";
//                    error = "TAGNAME has no corresponding value or does not exist.";
//                    errorList.add(new FileError(errorType, error));                    
//                }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            if (failure) {
                return errorList;
            }
        }
        return null;
    }

    /*
     *comparing the header of the excel sheet with standard column header name.
     *@param head1 - standard header
     *@param head2 - header to be matched
     */   
    private boolean isHeadMatch(Object head1, String head2) {

        if (((String) head1).trim().equalsIgnoreCase(head2)) {
            return true;
        } else {
            return false;
        }
    }
    
    /*
     *getting the all headings of the Excel Sheet
     *@param sheetNumber - Sheet Number
     */  
    public Object[] getHeadings(int sheetNumber) throws FileNotFoundException, IOException {
        ArrayList<String> heading = new ArrayList<String>();
        POIFSFileSystem poif_fs = new POIFSFileSystem(new FileInputStream(this.xlsFilePath));
        HSSFWorkbook workbook = new HSSFWorkbook(poif_fs);
        HSSFSheet sheet = workbook.getSheet(sheets[sheetNumber]);
        HSSFRow row;
        row = sheet.getRow(0);
        if (row != null) {            
            try {
                Iterator itr = row.cellIterator();

                while (itr.hasNext()) {                   
                    heading.add(itr.next().toString());
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        }//end for rows

        return heading.toArray();
    }


    /*
     * if the excel sheet have the proper data in required format,
     * then return the record in recordList,else return the error
     * type and error in errorList.
     * @param sheetNumer - excel sheet number
     */
    public ArrayList<Feedback> getRecordsForSheet(int sheetNumber) throws FileNotFoundException, IOException {
        Feedback feedback = null;
        ArrayList<Feedback> recordList = new ArrayList<Feedback>();
        ArrayList<String> errorList = new ArrayList<String>();
        POIFSFileSystem poif_fs = new POIFSFileSystem(new FileInputStream(this.xlsFilePath));
        HSSFWorkbook workbook = new HSSFWorkbook(poif_fs);
        HSSFSheet sheet = workbook.getSheet(sheets[sheetNumber]);
        HSSFRow row;
        Object cellValue;
        boolean failure = false;

        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            row = sheet.getRow(i);
            if (row != null) {
                //xlsFileRecordList.add(new Feedback(row));            
                try {
                    //Iterator itr = row.cellIterator();
                    feedback = new Feedback();

                    if ((cellValue = row.getCell((short) 0)) != null) {                        
                        
                        feedback.setBatchId((cellValue.toString().split("\\.")[0]));
                    }
                    if ((cellValue = row.getCell((short) 1)) != null) {
                        feedback.setBoxId(cellValue.toString());
                    }

                    if ((cellValue = row.getCell((short) 2)) != null) {
                        feedback.setDocNumber(cellValue.toString());
                    }

                    if ((cellValue = row.getCell((short) 3)) != null) {
                        feedback.setTagName(cellValue.toString());
                    }


                    if ((cellValue = row.getCell((short) 4)) != null) {
                        feedback.setTagValue(cellValue.toString());
                    }

                    if ((cellValue = row.getCell((short) 5)) != null) {
                        feedback.setCorrectTagValue(cellValue.toString());
                    }

                    if ((cellValue = row.getCell((short) 6)) != null) {
                        feedback.setAuditProblem(cellValue.toString());
                    }

                    if ((cellValue = row.getCell((short) 7)) != null) {
                        feedback.setProcText(cellValue.toString());
                    }

                    recordList.add(feedback);                    
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }//end for rows

        }
        return recordList;
    }

    private void parseFileForSheet(File path, String sheetname)
            throws FileNotFoundException, IOException {
        POIFSFileSystem poif_fs = new POIFSFileSystem(new FileInputStream(path));
        HSSFWorkbook workbook = new HSSFWorkbook(poif_fs);
        HSSFSheet sheet = workbook.getSheet(sheetname);
        HSSFRow row;
        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            row = sheet.getRow(i);
            if (row != null) {
                //xlsFileRecordList.add(new Feedback(row));
                //list.add(parseRow(row));
            }
        }//end for rows

    }//end parseFile
    

    /**
     * Get all sheet name in Excel file and store in an array.
     * @param path
     * @return
     * @throws FileNotFoundException
     * @throws IOException
     */
    private String[] parseSheetsInFile(String path)
            throws FileNotFoundException, IOException {

        List<String> list = new ArrayList<String>();
        String[] array;
        POIFSFileSystem poif_fs = new POIFSFileSystem(new FileInputStream(path));
        HSSFWorkbook workbook = new HSSFWorkbook(poif_fs);

        int numberSheets = workbook.getNumberOfSheets();

        for (int i = 0; i < numberSheets; i++) {
            list.add(workbook.getSheetName(i));
        }//end for

        array = new String[list.size()];

        return list.toArray(array);
    }//end parseSheetsInFile   

    
    
    
    /*
     *Writing the date to the excel sheet
     *@param xlsFilePath  - Excel sheet file path where file is to be written.
     *@param sheetName - Main sheet name
     *@param feedbackList - List having all the records to be written in Excel sheet.
     */    
    public void writeXlsFile(String xlsFilePath, String sheetName, List feedbackList) throws FileNotFoundException, IOException {
        this.sheetName = sheetName;
        this.feedbackList = feedbackList;
        this.xlsFilePath = xlsFilePath;
        //List list = new ArrayList();
        
        //header for the  main sheet
        String[] headerList = {
            "REPORT DATE",
            "Batch ID",
            "BOXID",
            "DOCN",
            "TAG NAME",
            "TAG VALUE",
            "Correct TAG Value",
            "Audit Problem Desc Code",
            "Prob Text",
            "VALIDITY",
            "COMMENTS",
            "IMAGES",
            "XEROX COMMENTS",
            "SAMPLING USED",
            "COVERED BY QA SAMPLING REPORT",
            "QA LEVEL",
            "PROOFREADER",
            "REMARKS",
            "SHIFT",
            "DTYS",
            "CODER",
            "CHECKER",
            "LISTING",
            "TALLY"
        };
       
        int rowSize = headerList.length;
        workBook = new HSSFWorkbook();
        HSSFSheet sheet = workBook.createSheet(sheetName);
        
        //create a style for this header columns
        HSSFCellStyle columnHeaderStyle = workBook.createCellStyle();
        columnHeaderStyle.setFillBackgroundColor(HSSFColor.BLUE_GREY.index);
        columnHeaderStyle.setFillForegroundColor(HSSFColor.BLUE_GREY.index);
        
        //setting font style 
        HSSFFont font = workBook.createFont();
        font.setColor(HSSFFont.COLOR_RED);
        columnHeaderStyle.setFont(font);

        //creating Headers for the sheet
        HSSFRow columnHeaderRow = sheet.createRow((short) 0);        
        
        HSSFCell colHeading;
        for (int i = 0; i < rowSize; i++) {
            colHeading = columnHeaderRow.createCell((short) i);
            colHeading.setCellStyle(columnHeaderStyle);
            HSSFRichTextString txt = new HSSFRichTextString(headerList[i]);
            colHeading.setCellValue(txt);
        }

        //int nextRowNumber = 1;
        HSSFCell cell;
        HSSFRichTextString txt;
        Feedback feedback;
        String value = "";
        for (int i = 1; i <= feedbackList.size(); i++) {
            try {
                feedback = (Feedback) feedbackList.get((i - 1));
                HSSFRow row = sheet.createRow((short) i);
                
                java.util.Date dt = new java.util.Date();
                
                //report date
                cell = row.createCell((short) 0);
                Calendar calendar = new GregorianCalendar();
                Date date = calendar.getTime();
                DateFormat format1 = new SimpleDateFormat( "dd/MM/yyyy" );
                txt = new HSSFRichTextString(format1.format( date ));
               
                
                cell.setCellValue(txt);

                //batch id
                cell = row.createCell((short) 1);
                txt = new HSSFRichTextString(feedback.getBatchId());
                cell.setCellValue(txt);

                //box id
                cell = row.createCell((short) 2);
                txt = new HSSFRichTextString(feedback.getBoxId());
                cell.setCellValue(txt);

                //docN
                cell = row.createCell((short) 3);
                txt = new HSSFRichTextString(feedback.getDocNumber());
                cell.setCellValue(txt);

                //tag name
                cell = row.createCell((short) 4);
                txt = new HSSFRichTextString(feedback.getTagName());
                cell.setCellValue(txt);

                //tag value
                cell = row.createCell((short) 5);
                txt = new HSSFRichTextString(feedback.getTagValue());
                cell.setCellValue(txt);

                //correct tag value
                cell = row.createCell((short) 6);
                txt = new HSSFRichTextString(feedback.getCorrectTagValue());
                cell.setCellValue(txt);

                //audit problem
                cell = row.createCell((short) 7);
                txt = new HSSFRichTextString(feedback.getAuditProblem());
                cell.setCellValue(txt);

                //proc text
                cell = row.createCell((short) 8);
                txt = new HSSFRichTextString(feedback.getProcText());
                cell.setCellValue(txt);

                //validity
                cell = row.createCell((short) 9);
                txt = new HSSFRichTextString(feedback.getValidity());
                cell.setCellValue(txt);

                //comments
                cell = row.createCell((short) 10);
                txt = new HSSFRichTextString(feedback.getComments());
                cell.setCellValue(txt);

                //images                    
                cell = row.createCell((short) 11);
                txt = new HSSFRichTextString(feedback.getImageList().toString());
                cell.setCellValue(txt);

                //xerox comments
                cell = row.createCell((short) 12);
                txt = new HSSFRichTextString("");
                cell.setCellValue(txt);

                //sampling used
                cell = row.createCell((short) 13);
                txt = new HSSFRichTextString("");
                cell.setCellValue(txt);

                //covered by QA sampling record
                cell = row.createCell((short) 14);
                txt = new HSSFRichTextString("");
                cell.setCellValue(txt);

                //QA level
                cell = row.createCell((short) 15);
                txt = new HSSFRichTextString("");
                cell.setCellValue(txt);

                //proof reader
                cell = row.createCell((short) 16);
                txt = new HSSFRichTextString("");
                cell.setCellValue(txt);

                //remarks
                cell = row.createCell((short) 17);
                txt = new HSSFRichTextString("");
                cell.setCellValue(txt);

                //shift
                cell = row.createCell((short) 18);
                txt = new HSSFRichTextString("");
                cell.setCellValue(txt);

                //dtys
                cell = row.createCell((short) 19);
                txt = new HSSFRichTextString("");
                cell.setCellValue(txt);

                //coder
                cell = row.createCell((short) 20);    
                value = (feedback.isCoderSelected() ? feedback.getCoder() : "");                                
                txt = new HSSFRichTextString(value);
                cell.setCellValue(txt);

                //checker
                cell = row.createCell((short) 21);
                value = (feedback.isCheckerSelected() ? feedback.getChecker() : "");
                txt = new HSSFRichTextString(value);
                cell.setCellValue(txt);

                //listing 
                cell = row.createCell((short) 22);
                value = (feedback.isListingSelected() ? feedback.getListing() : "");
                txt = new HSSFRichTextString(value);
                cell.setCellValue(txt);

                //tally
                cell = row.createCell((short) 23);
                value = (feedback.isTallySelected() ? feedback.getTally() : "");
                txt = new HSSFRichTextString(value);
                cell.setCellValue(txt);

                //qa
                cell = row.createCell((short) 24);
                value = (feedback.isQaSelected() ? feedback.getQa() : "");
                txt = new HSSFRichTextString(value);
                cell.setCellValue(txt);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        FileOutputStream fileOut = new FileOutputStream(new File(xlsFilePath));
       
        //Making column autosize according to the data size.        
        sheet.autoSizeColumn((short) 0);
        sheet.autoSizeColumn((short) 1);
        sheet.autoSizeColumn((short) 2);
        sheet.autoSizeColumn((short) 3);
        sheet.autoSizeColumn((short) 4);
        sheet.autoSizeColumn((short) 5);
        sheet.autoSizeColumn((short) 6);
        sheet.autoSizeColumn((short) 7);
        sheet.autoSizeColumn((short) 8);
        sheet.autoSizeColumn((short) 9);
        sheet.autoSizeColumn((short) 10);
        sheet.autoSizeColumn((short) 11);
        sheet.autoSizeColumn((short) 12);
        sheet.autoSizeColumn((short) 13);
        sheet.autoSizeColumn((short) 14);
        sheet.autoSizeColumn((short) 15);
        sheet.autoSizeColumn((short) 16);
        sheet.autoSizeColumn((short) 17);
        sheet.autoSizeColumn((short) 18);
        sheet.autoSizeColumn((short) 19);
        sheet.autoSizeColumn((short) 20);
        sheet.autoSizeColumn((short) 21);
        sheet.autoSizeColumn((short) 22);
        sheet.autoSizeColumn((short) 23);
        
        //write image file in different sheets.
        writeAllImages();

        workBook.write(fileOut);
        fileOut.close();        
        System.out.println("--------------------> file writing is accomplished successfully");

    }

    /*
     * It writes the all selected images to the excel sheet.
     * Check for the dublicates files and make it correction by using the set.
     * Name of the sheet will be the same as the image name.
     */   
    private void writeAllImages() throws FileNotFoundException, IOException {
        Component viewer = new          
         Component  () {};        
            int offset = 0;
        String imageName = "DefAUlt";
        Set<String> imagesSet = new TreeSet<String>();
        for (Feedback fb : feedbackList) {
            if (fb.getSelectedImageList() != null) {
                for (String filename : fb.getSelectedImageList()) {

                    imagesSet.add(filename);
                }
            }
        }
        for (String filename : imagesSet) {           
            if (filename != null) {
                //get image data in bytes format from the server.
                byte[] imageData = client.Global.theImageConnection.obtainImage(viewer, filename, offset, resolution,"");                
                StringTokenizer tokens = new StringTokenizer(filename, "/,\\");
                while (tokens.hasMoreElements()) {
                    imageName = tokens.nextToken();
                }
                writeImage(imageData, imageName);
            }
        }
    }
    
    /*
     * writing individual file
     * @param imageData - the image data in bytes format.
     * @param sheetName - name of the sheet where  the image data going to be 
     * written.
     */    
    private void writeImage(byte[] imageData, String sheetName) throws FileNotFoundException, IOException {

        HSSFSheet sheet = workBook.createSheet(sheetName);
        HSSFPatriarch patriarch = sheet.createDrawingPatriarch();
        HSSFClientAnchor anchor = new HSSFClientAnchor(0, 0, 0, 255, (short) 1, 1, (short) 15, 50);
        anchor.setAnchorType(5);
        int pictureIndex = workBook.addPicture(imageData, HSSFWorkbook.PICTURE_TYPE_JPEG);
        HSSFPicture picture = patriarch.createPicture(anchor, pictureIndex);
        picture.setAnchor(anchor);
    }
}

