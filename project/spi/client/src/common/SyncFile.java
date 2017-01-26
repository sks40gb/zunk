/* $Header: /home/common/cvsarea/ibase/dia/src/common/SyncFile.java,v 1.3 2003/11/14 16:48:10 weaston Exp $ */
package common;

import common.Log;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

final public class SyncFile
{

   /**
     * The block length for the rsync algorithm
     */
   final public static int BLOCK_LENGTH = 600;
   private long timestamp;
   private int length;
   private byte[] buffer;

   // for toString()

   private String dirName = null;
   private String fileName = null;
   private MessageDigest theDigest = null;

   /**
     * Create a new SyncFile with the specified length.  
     */
   public SyncFile(long timestamp, int length)
   {
      init(length);
      this.timestamp = timestamp;
   }

   /**
     * Create a new SyncFile and load the given file.
     * If file does not exist, assume a zero-length file.
     */
   public SyncFile(String dirName, String fileName) throws IOException
   {
      this.dirName = dirName;
      this.fileName = fileName;

      File theFile = new File(dirName, fileName);
      long fileLength = theFile.length();
      // Note: fileLength returns 0 if file does not exist
      init((int) fileLength);
      timestamp = theFile.lastModified();

      if (fileLength != 0) {
         InputStream instream = new FileInputStream(theFile);
         int readLength = instream.read(buffer);
         if (readLength != fileLength) {
            throw new IOException("wrong length read: expected " + fileLength + " got " + readLength);
         }
         instream.close();
      }
   }

   private void init(int length)
   {
      this.length = length;
      buffer = new byte[length + BLOCK_LENGTH];
      Arrays.fill(buffer, length, buffer.length, (byte) 0);
   }

   /**
     * Write the SyncFile content to the given file.
     */
   public void write(String dirName, String fileName) throws IOException
   {

      File theFile = new File(dirName, fileName);

      OutputStream outstream = new FileOutputStream(theFile);
      outstream.write(buffer, 0, length);
      outstream.close();
      theFile.setLastModified(timestamp);
   }

   /**
     * Get length of file.
     */
   public int getLength()
   {
      return length;
   }

   /**
     * Get modification time of file.
     */
   public long getTimestamp()
   {
      return timestamp;
   }

   /**
     * Get number of blocks in SyncFile.
     */
   public int getBlockCount()
   {
      return (length + (BLOCK_LENGTH - 1)) / BLOCK_LENGTH;
   }

   /**
     * Get byte at given position.
     */
   public byte get(int pos)
   {
      return buffer[pos];
   }

   /**
     * Get array of bytes from specified position in SyncFile buffer.
     */
   public byte[] get(int pos, int length)
   {
      byte[] result = new byte[length];
      System.arraycopy(buffer, pos, result, 0, length);
      return result;
   }

   /**
     * Copy sequence of bytes to another SyncFile.
     */
   public void copy(int srcPos, SyncFile dest, int destPos, int length)
   {
      System.arraycopy(buffer, srcPos, dest.buffer, destPos, length);
   }

   /**
     * Store array of bytes in specified position in SyncFile buffer.
     */
   public void store(byte[] src, int destPos)
   {
      System.arraycopy(src, 0, buffer, destPos, src.length);
   }

   /**
     * Compute the fast checksum (signature) for a block.
     * The algorithm from Tridgell's thesis is used.
     */
   public int fastSum(int offset)
   {
      int sum1 = 0;
      int sum2 = 0;
      int pos = offset;
      for (int coeff = BLOCK_LENGTH; coeff > 0; coeff--) {
         sum1 += buffer[pos];
         sum2 += coeff * buffer[pos];
         pos++;
      }
      return (sum1 << 16) + (sum2 & 0xFFFF);
   }

   /**
     * Compute the message digest for the whole file.
     */
   public byte[] strongSum()
   {
      return strongSum(0, length);
   }

   /**
     * Compute the message digest for a block.
     */
   public byte[] strongSum(int offset)
   {
      return strongSum(offset, BLOCK_LENGTH);
   }

   /**
     * Compute the message digest for the designates region of the buffer.
     */
   private byte[] strongSum(int offset, int length)
   {

      if (theDigest == null) {
         try {
            theDigest = MessageDigest.getInstance("MD5");
         } catch (NoSuchAlgorithmException e) {
            Log.quit(e);
         }
      }
      theDigest.reset();
      theDigest.update(buffer, offset, length);
      return theDigest.digest();
   }

   public String toString()
   {
      return "SyncFile[" + dirName + "," + fileName + "," + length + "]";
   }

}

