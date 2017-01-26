package com.core.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;

/**
 *
 * @author sunil
 */
public class FileReaderApp {

    public static void main(String[] args) {

        String filePath = "/home/sunil/Desktop/thoghts";
        new FileReaderApp().readLFPFile(filePath);

    }

    public void readLFPFile(String filePath) {
        File file = new File(filePath);
        try {
            FileInputStream fis = new FileInputStream(file);
            int aval = fis.available();
            
            FileReader fileReader = new FileReader(file);
            BufferedReader br = new BufferedReader(fileReader);
            int progressValue = aval / 100;
            String line = null;
            while (br.ready()) {
                line = br.readLine();
                
                aval = aval - line.getBytes().length - 2;
                int value = (200 - aval / progressValue) / 2;
                
                System.out.println(value + "-----> " + line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
