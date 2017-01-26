package com.core.io;

import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * It is a special class from java.io package which is neither a input stream nor a output stream 
 * (because it can do both). It is directly a subclass of Object class.
 * 
 * Random access file is a special kind of file in Java which allows non-sequential or random access to any location 
 * in file. This means you don't need to start from 1st line if you want to read line number 10, you can directly go 
 * to line 10 and read. It's similar to array data structure, Just like you can access any element in array by index 
 * you can read any content from file by using file pointer.
 * 
 * @author Sunil
 */
public class RandomAccessFileApp {

    public static void main(String args[]) {

        String data = "KitKat (4.4 - 4.4.2)";
        writeToRandomAccessFile("sample.store", 100, data);
        System.out.println("String written into RandomAccessFile from Java Program : " + data);

        String fromFile = readFromRandomAccessFile("sample.store", 100);
        System.out.println("String read from RandomAccessFile in Java : " + fromFile);

    }

    /*
     * Utility method to read from RandomAccessFile in Java
     */
    public static String readFromRandomAccessFile(String file, int position) {
        String record = null;
        try {
            RandomAccessFile fileStore = new RandomAccessFile(file, "rw");

            // moves file pointer to position specified
            fileStore.seek(position);

            // reading String from RandomAccessFile
            record = fileStore.readUTF();

            fileStore.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return record;
    }

    /*
    * Utility method for writing into RandomAccessFile in Java
     */
    public static void writeToRandomAccessFile(String file, int position, String record) {
        try {
            RandomAccessFile fileStore = new RandomAccessFile(file, "rw");

            // moves file pointer to position specified
            fileStore.seek(position);

            // writing String to RandomAccessFile
            fileStore.writeUTF(record);

            fileStore.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

//javarevisited.blogspot.com/2015/02/randomaccessfile-example-in-java-read-write-String.html#ixzz46H7NYd8M
}
