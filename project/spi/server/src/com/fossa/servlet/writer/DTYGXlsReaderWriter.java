/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fossa.servlet.writer;

import com.fossa.servlet.common.DTYGFieldsData;
import com.fossa.servlet.writer.XlsReaderWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JProgressBar;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

/**
 *
 * @author sunil
 */
public class DTYGXlsReaderWriter extends XlsReaderWriter {

    private final String BATCH_ID = "batchID";
    private String xlsFilePath;
    private String[] sheets = null;
    private JProgressBar progressBar = null;
    String filePath;
    String sheetName;
    //List<Feedback> dtygList;
    List<DTYGFieldsData> dtygList;
    HSSFWorkbook workBook;

    private DTYGXlsReaderWriter() {
        super();
    }

    /**
     * The access spcefier is private because it should initated by using the 
     * reader and writer method depending upon the requirement. 
     * @param filePath - path for the file.
     * @throws java.io.FileNotFoundException
     * @throws java.io.IOException
     */
    private DTYGXlsReaderWriter(String filePath) throws FileNotFoundException, IOException {
        super(filePath);
        this.xlsFilePath = filePath;
        this.sheets = parseSheetsInFile(filePath);
    }

    /**
     * It return the DTYGXlsReaderWriter object for the file reading operation.
     * @param filePath - path for the file.
     * @return - object of DTYGXlsReaderWriter class.
     * @throws java.io.FileNotFoundException
     * @throws java.io.IOException
     */
    public static DTYGXlsReaderWriter reader(String filePath) throws FileNotFoundException, IOException {
        return new DTYGXlsReaderWriter(filePath);
    }

    /**
     * It return the DTYGXlsReaderWriter object for the file writing operations.
     * @return
     */
    public static DTYGXlsReaderWriter writer() {
        return new DTYGXlsReaderWriter();
    }

    /**
     * Get the all records a particular sheet of Excelsheet.
     * @param sheetNumber - sheet number to read.
     * @param progressBar - progress bar
     * @return 
     * @throws java.io.FileNotFoundException
     * @throws java.io.IOException
     */
    public ArrayList<DTYGFieldsData> getRecordsForSheet(int sheetNumber, JProgressBar progressBar) throws FileNotFoundException, IOException {
        this.progressBar = progressBar;
        return getRecordsForSheet(sheetNumber);
    }

    @Override
    /*
     * if the excel sheet have the proper data in required format,
     * then return the record in recordList,else return the error
     * type and error in errorList.
     * @param sheetNumer - excel sheet number
     */
    public ArrayList<DTYGFieldsData> getRecordsForSheet(int sheetNumber) throws FileNotFoundException, IOException {
        DTYGFieldsData dtygData = null;
        ArrayList<DTYGFieldsData> recordList = new ArrayList<DTYGFieldsData>();
        ArrayList<String> errorList = new ArrayList<String>();
        POIFSFileSystem poif_fs = new POIFSFileSystem(new FileInputStream(this.xlsFilePath));
        HSSFWorkbook workbook = new HSSFWorkbook(poif_fs);
        HSSFSheet sheet = workbook.getSheet(sheets[sheetNumber]);
        HSSFRow row;
        Object cellValue;
        boolean nowProceedToAddRecord = false;
        int totalRow = sheet.getLastRowNum();
        int incrementRowBy = totalRow / 100;
        for (int i = 1; i <= totalRow; i++) {
            if (progressBar != null) {
                int value = (50 * i) / totalRow;
                progressBar.setValue(value);

                try {
                    Thread.sleep(1);
                } catch (Exception e) {
                }
            }
            row = sheet.getRow(i);
            if (row != null) {
                //xlsFileRecordList.add(new Feedback(row));            
                try {

                    if (!nowProceedToAddRecord) {
                        if ((cellValue = row.getCell((short) 4)) != null) {
                            if (cellValue.toString().trim().equalsIgnoreCase(BATCH_ID)) {

                                //**********************************************

//                                System.out.println("0 -------> " + row.getCell((short) 0));
//                                System.out.println("1 -------> " + row.getCell((short) 1));
//                                System.out.println("2 -------> " + row.getCell((short) 2));
//                                System.out.println("3 -------> " + row.getCell((short) 3));
//                                System.out.println("4 -------> " + row.getCell((short) 4));
//                                System.out.println("5 -------> " + row.getCell((short) 5));
//                                System.out.println("6 -------> " + row.getCell((short) 6));
//                                System.out.println("7 -------> " + row.getCell((short) 7));
//                                System.out.println("8 -------> " + row.getCell((short) 8));
//                                System.out.println("9 -------> " + row.getCell((short) 9));
//                                System.out.println("10 -------> " + row.getCell((short) 10));
//                                System.out.println("11 -------> " + row.getCell((short) 11));
//                                System.out.println("12 -------> " + row.getCell((short) 12));
//                                System.out.println("13 -------> " + row.getCell((short) 13));
//                                System.out.println("14 -------> " + row.getCell((short) 14));
//                                System.out.println("15 -------> " + row.getCell((short) 15));
//                                System.out.println("16 -------> " + row.getCell((short) 16));
//                                System.out.println("17 -------> " + row.getCell((short) 17));
//                                System.out.println("18 -------> " + row.getCell((short) 18));
//                                System.out.println("19 -------> " + row.getCell((short) 19));
//                                System.out.println("20 -------> " + row.getCell((short) 20));
//                                System.out.println("21 -------> " + row.getCell((short) 21));
//                                System.out.println("22 -------> " + row.getCell((short) 22));
//                                System.out.println("23 -------> " + row.getCell((short) 23));
//                                System.out.println("24 -------> " + row.getCell((short) 24));
//                                System.out.println("25 -------> " + row.getCell((short) 25));
//                                System.out.println("26 -------> " + row.getCell((short) 26));

                                //**********************************************
                                nowProceedToAddRecord = true;
                                continue;
                            } else {
                                continue;
                            }
                        }
                    }

                    //Iterator itr = row.cellIterator();
                    dtygData = new DTYGFieldsData();

                    if ((cellValue = row.getCell((short) 0)) != null) {
                        dtygData.ID = cellValue.toString();
                    }
                    if ((cellValue = row.getCell((short) 1)) != null) {
                        dtygData.processLevel = cellValue.toString();
                    }
                    if ((cellValue = row.getCell((short) 2)) != null) {
                        dtygData.queryType = cellValue.toString();
                    }
                    if ((cellValue = row.getCell((short) 3)) != null) {
                        dtygData.collection = cellValue.toString();
                    }

                    if ((cellValue = row.getCell((short) 4)) != null) {
                        dtygData.batchID = cellValue.toString();
                    }
                    if ((cellValue = row.getCell((short) 5)) != null) {
                        dtygData.batch = cellValue.toString();
                    }
                    if ((cellValue = row.getCell((short) 6)) != null) {
                        dtygData.images = cellValue.toString();
                    }
                    if ((cellValue = row.getCell((short) 7)) != null) {
                        dtygData.docs = cellValue.toString();
                    }
                    if ((cellValue = row.getCell((short) 8)) != null) {
                        dtygData.firstPage = cellValue.toString();
                    }
                    if ((cellValue = row.getCell((short) 9)) != null) {
                        dtygData.begImage = cellValue.toString();
                    }

                    if ((cellValue = row.getCell((short) 10)) != null) {
                        dtygData.endImage = cellValue.toString();
                    }

                    if ((cellValue = row.getCell((short) 11)) != null) {
                        dtygData.specVersion = cellValue.toString();
                    }

                    if ((cellValue = row.getCell((short) 12)) != null) {
                        dtygData.dateSubmitted = cellValue.toString();
                    }

                    if ((cellValue = row.getCell((short) 13)) != null) {
                        dtygData.spiQuestions = cellValue.toString();
                    }
                    if ((cellValue = row.getCell((short) 14)) != null) {
                        dtygData.submitter = cellValue.toString();
                    }
                    if ((cellValue = row.getCell((short) 15)) != null) {
                        dtygData.genDocType = cellValue.toString();
                    }
                    if ((cellValue = row.getCell((short) 16)) != null) {
                        dtygData.specDocType = cellValue.toString();
                    }

                    if ((cellValue = row.getCell((short) 17)) != null) {
                        dtygData.field = cellValue.toString();
                    }
                    if ((cellValue = row.getCell((short) 18)) != null) {
                        dtygData.source = cellValue.toString();
                    }
                    if ((cellValue = row.getCell((short) 19)) != null) {
                        dtygData.folderInformation = cellValue.toString();
                    }
                    if ((cellValue = row.getCell((short) 20)) != null) {
                        dtygData.dateAnswered = cellValue.toString();
                    }
                    if ((cellValue = row.getCell((short) 21)) != null) {
                        dtygData.decisionBy = cellValue.toString();
                    }

                    if ((cellValue = row.getCell((short) 22)) != null) {
                        dtygData.xeroxResponses = cellValue.toString();
                    }
                    if ((cellValue = row.getCell((short) 23)) != null) {
                        dtygData.uploadBy = cellValue.toString();
                    }
                    if ((cellValue = row.getCell((short) 24)) != null) {
                        dtygData.uploadDate = cellValue.toString();
                    }

                    if (dtygData.batchID == null || dtygData.batchID.trim().equals("")) {
                        continue;
                    }

                    recordList.add(dtygData);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }//end for rows

        }
        return recordList;
    }

    /*
     *Writing the date to the excel sheet
     *@param xlsFilePath  - Excel sheet file path where file is to be written.
     *@param sheetName - Main sheet name
     *@param dtygList - List having all the records to be written in Excel sheet.
     */
    @Override
    public void write(String xlsFilePath, String sheetName, List list) throws FileNotFoundException, IOException {
        this.sheetName = sheetName;
        this.dtygList = list;
        this.xlsFilePath = xlsFilePath;
        //header for the  main sheet
        String[] headerList = {
            "BatchID",
            "BegImage",
            "EndImage",
            "Spec Version",
            "Date Submitted",
            "SPI Questions",
            "Field",
            "Xerox Responses",
            "lookup"
        };

        int rowSize = headerList.length;
        workBook = new HSSFWorkbook();
        HSSFSheet sheet = workBook.createSheet(sheetName);

        //create a style for this header columns
        HSSFCellStyle columnHeaderStyle = workBook.createCellStyle();
        //columnHeaderStyle.FillBackgroundColor(HSSFColor.BLUE_GREY.index);
        columnHeaderStyle.setFillBackgroundColor((short) 2);
        //columnHeaderStyle.FillForegroundColor(HSSFColor.BLUE_GREY.index);

        //ting font style 
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
        DTYGFieldsData dtygData;
        String value = "";
        for (int i = 1; i <= dtygList.size(); i++) {
            try {
                dtygData = (DTYGFieldsData) dtygList.get((i - 1));
                HSSFRow row = sheet.createRow((short) i);

                //batch id
                cell = row.createCell((short) 0);
                txt = new HSSFRichTextString(dtygData.batchID);
                cell.setCellValue(txt);

                //BegImage
                cell = row.createCell((short) 1);
                txt = new HSSFRichTextString(dtygData.begImage);
                cell.setCellValue(txt);

                //EndImage
                cell = row.createCell((short) 2);
                txt = new HSSFRichTextString(dtygData.endImage);
                cell.setCellValue(txt);

                //SpecVersion
                cell = row.createCell((short) 3);
                txt = new HSSFRichTextString(dtygData.specVersion);
                cell.setCellValue(txt);

                //DateSubmitted
                cell = row.createCell((short) 4);
                txt = new HSSFRichTextString(dtygData.dateSubmitted);
                cell.setCellValue(txt);

                //SpiQuestions
                cell = row.createCell((short) 5);
                txt = new HSSFRichTextString(dtygData.spiQuestions);
                cell.setCellValue(txt);

                //Field
                cell = row.createCell((short) 6);
                txt = new HSSFRichTextString(dtygData.field);
                cell.setCellValue(txt);

                //XeroxResponses
                cell = row.createCell((short) 7);
                txt = new HSSFRichTextString(dtygData.xeroxResponses);
                cell.setCellValue(txt);

                //XeroxResponses
                cell = row.createCell((short) 8);
                txt = new HSSFRichTextString(dtygData.lookup);
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
        workBook.write(fileOut);
        fileOut.close();
        System.out.println("--------------------> file writing is accomplished successfully");

    }
}
