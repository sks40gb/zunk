package com.core.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Sunil
 */
public class CSVReader {

    private InputStream inputStream;

    public static void main(String[] args) throws Exception {
        CSVReader csvReader = new CSVReader(new FileInputStream(new File("C:\\Users\\sunsingh\\Desktop\\fee_data.csv")));
        
        System.out.println(csvReader.calculateFee(csvReader.getData(), "C1055"));
    }

    public List<String> calculateFee(List<List<String>> data, String query){
		for(List<String> row : data){
			if(row.get(0).equals(query)){
				return row;
			}
		}
                return null;
	}
    
    public CSVReader(InputStream is) {
        this.inputStream = is;
    }

    public List<List<String>> getData() throws IOException {
        List<List<String>> list = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String line;
        int count  = 0;
        while ((line = reader.readLine()) != null) {
            List<String> parsedLine = parseLine(line);
            list.add(parsedLine);
            System.out.println(parsedLine);
            count++;
            if(count > 2){
                break;
            }
                   
        }
        return list;
    }

    /**
     * Convert CSV row to ArrayList
     *
     * @param line
     * @return
     * @throws Exception
     */
    public List<String> parseLine(String line) throws IOException {
        int pointer = 0;
        int ch = line.charAt(pointer++);
        while (ch == '\r') {
            //ignore linefeed chars wherever, particularly just before end of file
            ch = line.charAt(pointer++);
        }
        if (ch < 0) {
            return null;
        }
        List<String> store = new ArrayList<>(30);
        StringBuffer curVal = new StringBuffer();
        boolean inquotes = false;
        boolean started = false;
        while (ch >= 0) {
            if (inquotes) {
                started = true;
                if (ch == '\"') {
                    inquotes = false;
                } else {
                    curVal.append((char) ch);
                }
            } else if (ch == '\"') {
                inquotes = true;
                if (started) {
                    // if this is the second quote in a value, add a quote
                    // this is for the double quote in the middle of a value
                    curVal.append('\"');
                }
            } else if (ch == ',') {
                store.add(curVal.toString());
                curVal = new StringBuffer();
                started = false;
            } else if (ch == '\r') {
                //ignore LF characters
            } else if (ch == '\n') {
                //end of a line, break out
                break;
            } else {
                curVal.append((char) ch);
            }
            if(pointer < line.length()){
                ch = line.charAt(pointer++);                
            }else{
                ch = -1;
            }
        }
        store.add(curVal.toString());
        return store;
    }

}
