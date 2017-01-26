package com.core.misc;

import java.util.Scanner;

/**
 *
 * @author ssi150
 */
public class MiscTest {

    public static void main(String[] args) {
        readFromConsole();

    }

    public static void readFromConsole() {
        Scanner scanner = new Scanner(System.in);
        if(scanner.hasNext()){
            System.out.println("THis is what you type : " + scanner.next());
        }

    }
}
