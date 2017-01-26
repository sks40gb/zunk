package dbload;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;

public class DoculexReader
{

   private String fileName;
   private DataInputStream stream;
   private byte[] buffer = new byte[1000];
   final private static String hexTable = "0123456789ABCDEF";

   private DoculexReader(String fileName)
   {
      this.fileName = fileName;
   }

   final public static void main(String[] args)
   {
      (new DoculexReader(args[0])).run();
   }

   public void run()
   {
      try {
         stream = new DataInputStream(new FileInputStream(fileName));

         readFully(stream, buffer, 0, 32);

         int numberOfDataRecs = int4(buffer, 4);
         int headerLength = int2(buffer, 8);
         int recordLength = int2(buffer, 10);
         int numberOfFields = headerLength / 32 - 1;

         System.out.println("dbase version: " + buffer[0]);
         System.out.println("last write date: " + (1900 + buffer[1]) + "-" + buffer[2] + "-" + buffer[3]);
         System.out.println("numberOfDataRecs: " + numberOfDataRecs);
         System.out.println("headerLength: " + headerLength);
         System.out.println("recordLength: " + recordLength);
         System.out.println("reserved: " + dump(buffer, 12, 20));
         System.out.println("numberOfFields: " + numberOfFields);


         int expectedHeaderLength = numberOfFields * 32 + 33;
         if (headerLength != expectedHeaderLength) {
            System.out.println("ERROR expectedHeaderLength: " + expectedHeaderLength);
         }

         String[] fieldNameTable = new String[numberOfFields];
         int[] fieldLengthTable = new int[numberOfFields];
         int totalFieldLength = 0;

         for (int i = 0; i < numberOfFields; i++) {
            readFully(stream, buffer, 0, 32);

            String fieldName;
            String fieldType;
            int fieldLength;
            int decimalPlaces;

            int fieldNameEnd = 11;
            while (fieldNameEnd > 0 && buffer[fieldNameEnd - 1] == 0) {
               fieldNameEnd--;
            }
            fieldName = new String(buffer, 0, fieldNameEnd);
            fieldType = new String(buffer, 11, 1);
            fieldLength = buffer[16] & 0xFF;
            decimalPlaces = buffer[17] & 0xFF;

            fieldNameTable[i] = fieldName;
            fieldLengthTable[i] = fieldLength;
            totalFieldLength += fieldLength;

            System.out.println(i + ". fieldName=" + fieldName);
            System.out.println(i + ". fieldType=" + fieldType);
            System.out.println(i + ". reserved:" + dump(buffer, 12, 4));
            System.out.println(i + ". fieldLength=" + fieldLength);
            System.out.println(i + ". decimalPlaces=" + decimalPlaces);
            System.out.println(i + ". reserved:" + dump(buffer, 18, 14));
         }

         int[] fieldOffsetTable = new int[numberOfFields];
         fieldOffsetTable[0] = 1;
         for (int i = 1; i < numberOfFields; i++) {
            fieldOffsetTable[i] = fieldOffsetTable[i - 1] + fieldLengthTable[i - 1];
         }

         readFully(stream, buffer, 0, 1);
         if (buffer[0] != 0x0D) {
            System.out.println("ERROR expected end-of header 0D, found:" + dump(buffer, 0, 1));
         }
         System.out.println("totalFieldLength=" + totalFieldLength);

         int recordCount = 0;
         readFully(stream, buffer, 0, 1);
         StringBuffer data = new StringBuffer(0);
         while (buffer[0] != 0x1A) {
            recordCount++;
            if (' ' != (char) buffer[0]) {
               System.out.println("NON-BLANK CONTROL :" + hex(buffer, 0, 1));
            }
            readFully(stream, buffer, 1, recordLength - 1);
            data = new StringBuffer();
            for (int i = 0; i < numberOfFields; i++) {
               data.append(',');
               data.append(trimTrailing(new String(buffer, fieldOffsetTable[i], fieldLengthTable[i])));
            }
            data.deleteCharAt(0);
            readFully(stream, buffer, 0, 1);
         }
         System.out.println("recordCount=" + recordCount);
         System.out.println(recordCount + ": " + data);


      } catch (IOException e) {
         System.out.println(e.toString());
      }
   }

   private void readFully(DataInputStream stream, byte[] buf, int off, int len)
           throws IOException
   {
      stream.readFully(buf, off, len);
   //System.out.println(">>"+dump(buf,0,len));
   }

   private short int2(byte[] buf, int off)
   {
      return (short) ((buf[off + 1] << 8) | (buf[off + 0] & 0xFF));
   }

   private int int4(byte[] buf, int off)
   {
      return (int) ((buf[off + 3] << 24) | ((buf[off + 2] & 0xFF) << 16) | ((buf[off + 1] & 0xFF) << 8) | (buf[off + 0] & 0xFF));
   }

   private String hex(byte[] buf, int off, int len)
   {
      char[] cbuf = new char[2 * len];
      for (int i = 0; i < len; i++) {
         int hi = (buf[off + i] >> 4) & 0x0F;
         int lo = (buf[off + i]) & 0x0F;
         cbuf[2 * i] = hexTable.charAt(hi);
         cbuf[2 * i + 1] = hexTable.charAt(lo);
      }
      return new String(cbuf);
   }

   private String dump(byte[] buf, int off, int len)
   {
      StringBuffer sbuf = new StringBuffer((len * 9) / 4);
      for (int i = 0; i < len; i += 4) {
         sbuf.append(" ");
         sbuf.append(hex(buf, off + i, Math.min(4, len - i)));
      }
      return sbuf.toString();
   }

   private String trimTrailing(String str)
   {
      int len = str.length();
      while (len > 0 && str.charAt(len - 1) == ' ') {
         len--;
      }
      return str.substring(0, len);
   }

}
