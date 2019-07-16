package com.core.io;

import java.io.*;

public class DataInputStreamApp {

    public static void main(String[] args) {
        String filename = "data-out.dat";
        String message = "Hi,您好!";

        // Write primitives to an output file
        try {
            DataOutputStream out = new DataOutputStream(new FileOutputStream(filename));
//            DataOutputStream out =
//                    new DataOutputStream(
//                    new BufferedOutputStream(
//                    new FileOutputStream(filename)));
            out.writeByte(127);
            out.writeShort(0xFFFF);  // -1
            out.writeInt(0xABCD);
            out.writeLong(0x1234);  // JDK 7 syntax
            out.writeFloat(11.22f);
            out.writeDouble(55.66);
            out.writeBoolean(true);
            out.writeBoolean(false);
            for (int i = 0; i < message.length(); i++) {
                out.writeChar(message.charAt(i));
            }
            out.writeChars(message);
            out.writeBytes(message);
            out.flush();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        // Read raw bytes and print in Hex
        try {
            BufferedInputStream in = new BufferedInputStream(new FileInputStream(filename));
            int inByte;
            while ((inByte = in.read()) != -1) {
                System.out.printf("%02X ", inByte);   // Print Hex codes
            }
            System.out.printf("%n%n");
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        // Read primitives
        try {
            DataInputStream in =new DataInputStream(new FileInputStream(filename));
            System.out.println("byte:    " + in.readByte());
            System.out.println("short:   " + in.readShort());
            System.out.println("int:     " + in.readInt());
            System.out.println("long:    " + in.readLong());
            System.out.println("float:   " + in.readFloat());
            System.out.println("double:  " + in.readDouble());
            System.out.println("boolean: " + in.readBoolean());
            System.out.println("boolean: " + in.readBoolean());

            System.out.print("char:    ");
            for (int i = 0; i < message.length(); i++) {
                System.out.print(in.readChar());
            }
            System.out.println();

            System.out.print("chars:   ");
            for (int i = 0; i < message.length(); i++) {
                System.out.print(in.readChar());
            }
            System.out.println();

            System.out.print("bytes:   ");
            for (int i = 0; i < message.length(); i++) {
                System.out.print((char) in.readByte());
            }
            System.out.println();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}