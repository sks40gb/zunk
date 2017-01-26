package com.core.io;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.SequenceInputStream;

/**
 *
 * @author Sunil
 */
public class SequenceInputStreamApp {

    public static void main(String args[]) throws IOException {
        FileInputStream fileInputStream1 = new FileInputStream("Sayings.txt");  // first source file
        FileInputStream fileInputStream2 = new FileInputStream("Morals.txt");  //second source file

        SequenceInputStream sequenceInputStream = new SequenceInputStream(fileInputStream1, fileInputStream2);
        FileOutputStream fileOutputStream = new FileOutputStream("Result.txt");        // destination file

        int temp;
        while ((temp = sequenceInputStream.read()) != -1) {
            System.out.print((char) temp); // to print at DOS prompt
            fileOutputStream.write(temp);	// to write to file
        }
        fileOutputStream.close();
        sequenceInputStream.close();
        fileInputStream1.close();
        fileInputStream2.close();
    }
}
