/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package export;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

/**
 *
 * @author sunil
 */
public abstract class XlsReaderWriter<T extends XlsReaderWriter> {
    
    String xlsFilePath =null;
    private String[] sheets = null;
    
    protected XlsReaderWriter() {}
    
    protected XlsReaderWriter(String xlsFilePath) {
        this.xlsFilePath = xlsFilePath;  
        try {
            this.sheets = parseSheetsInFile(xlsFilePath);
        } catch (Exception e){
            e.printStackTrace();
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
    
    /**
     * 
     * @param sheetNumber
     * @return
     */
    public abstract  ArrayList<?> getRecordsForSheet(int sheetNumber) throws FileNotFoundException, IOException;
    
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
    
    /**
     * 
     * @param xlsFilePath
     * @param sheetName
     * @param feedbackList
     */
    public abstract  void write(String xlsFilePath, String sheetName, List list,int count,List selectedTallyList) throws FileNotFoundException, IOException;
    
     /**
     * Get all sheet name in Excel file and store in an array.
     * @param path
     * @return
     * @throws FileNotFoundException
     * @throws IOException
     */
    protected String[] parseSheetsInFile(String path)
            throws FileNotFoundException, IOException {        
        if(path == null){
            path = xlsFilePath;
        }
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
    
}
