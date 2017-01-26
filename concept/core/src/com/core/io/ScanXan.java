package com.core.io;

import java.io.*;
import java.util.Scanner;

public class ScanXan {
    public static void main(String[] args) throws IOException {

        Scanner s = null;

        try {
            s = new Scanner(new BufferedReader(new FileReader("D:" + File.separator + "transaction.txt")));

            while (s.hasNextDouble()) {
                System.out.println(s.nextDouble());
            }
        } finally {
            if (s != null) {
                s.close();
            }
        }
    }
}