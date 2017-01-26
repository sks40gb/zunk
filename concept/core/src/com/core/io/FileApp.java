package com.core.io;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

/**
 *
 * @author sunil
 */
public class FileApp {

    File file = new File("/home/sunil/Desktop/sunil.txt");

    public static void main(String[] args) {
        new FileApp().fileOperation();
    }
    
    
    public void fileOperation() {
        //file check
        if (file.exists()) {                                                    //1        
            System.out.println("=================> FILE FOUND ");
        } else {
            try {
                //Create new file if doesn,t exists.
                file.createNewFile();                                           //2
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        
        if(file.isDirectory()){                                                 //3
            System.out.println("-----------------> IT IS A DIR");
        }else if(file.isFile()){                                                //4    
            System.out.println("-----------------> IT IS A FILE");
        }     
       
        //-------------------------------------------------------
        try{
        Reader rd = new FileReader(file);
        char c[] = new char[100];
        rd.read(c);
        int i = 0;
        while((i =rd.read()) != -1){
            System.out.print((char)i);
        }
        //------------------------------------------------------

        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
