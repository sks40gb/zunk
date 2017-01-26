/*
 * ImageSource.java
 *
 * Created on November 30, 2007, 12:04 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.fossa.servlet.server;

import com.fossa.servlet.common.Log;
import com.fossa.servlet.common.MessageConstants;
import com.fossa.servlet.common.SQLQueries;
import com.fossa.servlet.dao.DBTask;
import com.fossa.servlet.session.UserTask;
import java.io.ByteArrayInputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author Bala
 */
/**
 * Class to provide a source for an image to be sent to the client, handling
 * any required page extraction or resolution reduction.
 */
public class ImageSource implements MessageConstants{
    
    final private static int GIF_CODE = ('G' << 8) | ('I' & 0XFF);    // GIF, "GI"
    final private static int INTEL_CODE = 'I' * 0x101;     // TIFF, little-endian "II"
    final private static int JPEG_CODE = 0xFFD8;           // JPEC, 0xFFD8
    final private static int MOTOROLA_CODE = 'M' * 0x101;  // TIFF, little-endian "MM"

    // Magic code in 3rd byte of TIFF file
    final private static int TIFF_MAGIC = 42;

    // Maximum initial size of result array
    final private static int INITIAL_ARRAY_SIZE = 256*1024;

    // Length of cell for TIFF types 1, 2, ...
    final private static int[] TIFF_TYPE_LENGTH = {1,1,2,4,8,1, 1,2,4,8,4,8};

    // TIFF tag codes
    final private static int TAG_STRIP_OFFSETS = 273;
    final private static int TAG_STRIP_BYTE_COUNTS = 279;
    final private static int TAG_TILE_OFFSETS = 324;
    final private static int TAG_TILE_BYTE_COUNTS = 325;

    // scaling parameters for {HIGH, MEDIUM, LOW, DRAFT}_RES
    final private static int[]     scalePercent = {100,100,50,50};
    final private static boolean[] halveYRes = {false,true,false,true};

    private File theFile;              // File for given source path
    private int resolution;            // resolution (RES_HIGH .. RES_DRAFT)
    private int pageOffset;            // given page number in TIFF file
    private boolean intel = false;     // true if little-endian byte order

    // The given image filed, opened for random access
    private RandomAccessFile raf = null;
    private int rafLength;
    // If non-zero, the offset of the IFD (TIFF ImageFileDirectory)
    // for the required page of a multi-page file
    int ifdOffset = 0;

    private int streamLength = 0;  // size of output stream, (so far, while splitting)
    private InputStream stream = null;   // the InputStream to be returned

    // array to collect output when splitting TIFF
    private byte[] outputArray = null;
    
    
    /** Creates a new instance of ImageSource */
    public ImageSource(UserTask task, DBTask dbTask, String fileName, int pageOffset, int resolution)throws IOException {
        
        if (fileName.charAt(0) == '*') {
            Log.print("init for database: "+fileName);
            assert pageOffset == 0;
            //System.out.println("substring of filename--------------->"+  Integer.parseInt(fileName.substring(5)));
            initializeForDatabase(dbTask, Integer.parseInt(fileName.substring(5)),resolution);
        } else {
            Log.print("init for file: "+fileName);
            initializeForFile(fileName, pageOffset, resolution);
        }
    }

    private void initializeForDatabase(DBTask dbTask,  int pageId, int resolution) throws IOException{
         try {
            Connection con = dbTask.getConnection();
            ResultSet rs = con.createStatement().executeQuery(SQLQueries.SEL_IMGSOURCE_IMAGE+pageId);
            if (! rs.next()) {
                rs.close();
                throw new FileNotFoundException("Binder image record not found: "+pageId);
            }

            // Note.  We use ByteArrayInputStream instead of Blob because
            // Blob uses it internally and we can close the ResultSet
            //Blob result = rs.getBlob(1);
            byte[] result = rs.getBytes(1);
            //streamLength = (int) result.length();
            streamLength = result.length;
            stream = new ByteArrayInputStream(result);
            rs.close();
            if (resolution == RES_HIGH) {
                //stream = result.getBinaryStream();
                //Log.print("InitializeForDatabase: "+streamLength+" "+stream);
            } else {
                Log.print("InitializeForDatabase: res="+resolution+" "+streamLength+" "+stream);
                //buildReducedResolution(stream, streamLength);
            }
        } catch (SQLException e) {
            throw new IOException(e.toString());
        }
    }

    private void initializeForFile(String fileName, int pageOffset, int resolution) throws IOException{
        
        this.theFile = new File(fileName);
        this.pageOffset = pageOffset;
        this.resolution = resolution;

        // Open as a RandomAccessFile
        raf = new RandomAccessFile(theFile, "r");
        long length = raf.length();
        if (length > Integer.MAX_VALUE) {
            throw new FileNotFoundException("Image file too large: "+theFile);
        }
        rafLength = (int) length;

        // Read the first 2 bytes.  Note: order is always MOTOROLA for this read.
        int word0 = readSwapped(0, 2);
        if (word0 == GIF_CODE || word0 == JPEG_CODE) {
            // GIF or JPEG, check subPage and use as-is
            if (pageOffset > 0) {
                throw new FileNotFoundException("Page > 0 for GIF/JPEG: "+theFile);
            }
        } else {
            // It should be a TIFF file
            if (word0 == INTEL_CODE) {
                intel = true;
            } else if (word0 == MOTOROLA_CODE) {
                // OK, leave as big-endian
            } else {
                throw new FileNotFoundException("Invalid image file ("
                                   +Integer.toHexString(word0)+"): "+theFile);
            }

            // Check the TIFF magic code
            if (readSwapped(2, 2) != TIFF_MAGIC) {
                throw new FileNotFoundException("Invalid TIFF file "+theFile);
            }
            
            // It's a TIFF file, look for the IFD for desired page.
            int currentIfdOffset = readSwapped(4, 4);
            for (int i = 0; i < pageOffset && currentIfdOffset > 0; i++) {
                currentIfdOffset = findNextIfd(currentIfdOffset);
            }

            if (currentIfdOffset <= 0) {
                throw new FileNotFoundException("No such page in TIFF file "+theFile);
            }

            // If it's a multi-page tiff, save the offset
            if (pageOffset > 0 || findNextIfd(currentIfdOffset) > 0) {
                ifdOffset = currentIfdOffset;
            }
        }
    }

    /**
     * Return the length of the constructed stream.
     */
    public int getStreamLength() throws IOException {
        if (stream == null) {
            buildStream();
        }
        return streamLength;
    }
    
    /**
     * Return the constructed stream.
     */
    public InputStream getStream() throws IOException {
        if (stream == null) {
            buildStream();
        }
        return stream;
    }
    
     /**
     * Close any open files.
     */
    public void close() throws IOException {
        if (raf != null) {
            raf.close();
        }
        if (stream != null) {
            stream.close();
        }
    }
    
    
    // Construct an InputStream to return.
    private void buildStream() throws IOException {
        //resolution = 0;
        if (resolution == RES_HIGH) {
            if (ifdOffset == 0) {
                // No changes, just pass the given file as a stream
                raf.seek(0);
                stream = new FileInputStream(raf.getFD());
                //raf.close();
                streamLength = rafLength;
            } else {
                // Extract page from multi-page TIFF
                buildExtractPage();
            }
        } else { // since resolution != RES_HIGH
            // Create lower resolution image from given page
            
            raf.seek(0);
            InputStream instream = new FileInputStream(raf.getFD());
            buildReducedResolution(instream, rafLength);
        }
        raf = null;
    }
    

   // Read given number of bytes in specified byte order
    private int readSwapped(int offset, int length) throws IOException {
        int saveOffset = offset;
        if (offset + length > rafLength) {
            throw new EOFException(
                    "Image file too short ("+(offset+length)+"): "+theFile);
        }
        int delta;
        int result = 0;
        if (intel) {
            delta = -1;
            offset += length;
            offset--;
        } else {
            delta = +1;
        }

        for (int i = 0; i < length; i++) {
            raf.seek(offset);
            result <<= 8;
            int digit = raf.read();
            result |= digit & 0xFF;
            offset += delta;
        }

        return result;
    }

    // Find the next IFD.  Complain if there isn't a current one.
    // IFD consists of <# entries> + <12 bytes/entry> + <next IFD offset>
    private int findNextIfd(int offset) throws IOException {
        if (offset <= 0) {
            throw new FileNotFoundException(
                    "No page "+(pageOffset + 1)+" in "+theFile);
        }
        int dirCount = readSwapped(offset, 2);
        return readSwapped(offset + 2 + 12*dirCount, 4);
    }

   // Extract one page from a multi-page TIFF
    private void buildExtractPage() throws IOException {
        // Initialize array to collect output
        outputArray = new byte[Math.min(INITIAL_ARRAY_SIZE, rafLength)];
        streamLength = 0;

        // Create new TIFF header
        copyBytes(0, 0, 4);                            // copy heading
        putSwapped(8, 4, 4);                           // IFD offset = 8
        int ifdLength = readSwapped(ifdOffset, 2);
        copyBytes(ifdOffset, 8, 2 + ifdLength * 12);   // copy IFD
        putSwapped(0, streamLength, 4);                // next IFD offset = 0

        int stripByteCountsPosition = 0;
        int stripOffsetsPosition = 0;
        int numberOfStrips = 0;
        int stripByteCountLength = 0;
        int stripOffsetLength = 0;

        // loop through (copied) directory entries
        int dirPosition = 10;
        for (int k = 0; k < ifdLength; k++) {
            int tag = getSwapped(dirPosition, 2);
            int type = getSwapped(dirPosition + 2, 2);
            int typeLength = TIFF_TYPE_LENGTH[type - 1];
            int count = getSwapped(dirPosition + 4, 4);
            int valueOffset = getSwapped(dirPosition + 8, 4);
            int byteCount = count * typeLength;
            int savePosition = 0;
            //System.out.println(k+": tag="+tag
            //                   +" type="+type
            //                   +" typeLength="+typeLength
            //                   +" count="+count
            //                   +" valueOffset="+valueOffset
            //                   +" byteCount="+byteCount
            //                   +" streamLength="+streamLength);
            if (byteCount > 4) {
                // not immediate data, copy the values
                savePosition = streamLength;
                putSwapped(streamLength, dirPosition + 8, 4);
                copyBytes(valueOffset, streamLength, byteCount);
            } else {
                // immediate data, leave value(s) in place, save position
                savePosition = dirPosition + 8;
            }

            // if this entry related to strips, collect strip information
            switch (tag) {
            case TAG_STRIP_OFFSETS:
            case TAG_TILE_OFFSETS:
                stripOffsetsPosition = savePosition;
                stripOffsetLength = typeLength;
                numberOfStrips = count;
                break;
            case TAG_STRIP_BYTE_COUNTS:
            case TAG_TILE_BYTE_COUNTS:
                stripByteCountsPosition = savePosition;
                stripByteCountLength = typeLength;
                if (count != numberOfStrips) {
                    throw new IOException(
                            "Strip counts/offsets mismatch: "+theFile);
                }
                break;
            default: 
            }

            dirPosition += 12;
        }

        // sanity check that image (strip) information was found
        if (stripByteCountsPosition == 0 || stripOffsetsPosition == 0) {
            throw new EOFException(
                    "No image count/data: "+theFile);
        }

        // copy the strip data
        for (int i = 0; i < numberOfStrips; i++) {
            int holdStreamLength = streamLength;
            copyBytes(getSwapped(stripOffsetsPosition, stripOffsetLength),
                 streamLength,
                 getSwapped(stripByteCountsPosition, stripByteCountLength));
            putSwapped(holdStreamLength, stripOffsetsPosition, stripOffsetLength);
            stripByteCountsPosition += stripByteCountLength;
            stripOffsetsPosition += stripOffsetLength;
        }

        raf.close();
        stream = new ByteArrayInputStream(outputArray, 0, streamLength);
        outputArray = null;
    }
    
    
    private void buildReducedResolution(InputStream instream, int inlength) throws IOException {
        // reduce resolution and create an input stream
        long startTime = System.currentTimeMillis();
        com.acordex.vtj.ScaleTIFF myScaleTiff
                    = new com.acordex.vtj.ScaleTIFF();
        String message = myScaleTiff.ScaleTIFF(
                  instream,
                  inlength,
                  /* page => */ pageOffset + 1,
                  /* newRes => */ 0,
                  scalePercent[resolution],
                  halveYRes[resolution],
                  /* outRaf => */ null);
        //Log.print("ImageSource: res="+resolution+" ms-to-scale="
        //          +(System.currentTimeMillis() - startTime));
        // get result
        if (message != null) {
            Log.print("ImageSource: message="+message);
        }
        com.acordex.vtj.SeekableArrayOutStream saos
                            = myScaleTiff.GetOutputImage();
        if (saos == null) {
            Log.print("ImageSource: Failed scaling image");
            streamLength = -1;
        } else {
            streamLength = saos.GetOutputSize();
            stream = new ByteArrayInputStream(saos.GetBuffer(), 0, streamLength);
        }
    }

    // Copy a sequence of bytes from the given file to the output 
    private void copyBytes(int offset, int position, int length)
    throws IOException {
        if (offset + length > rafLength) {
            throw new EOFException(
                    "Image file too short ("+(offset+length)+"): "+theFile);
        }
        int newSize = position + length;
        if (newSize > outputArray.length) {
            expandArray(newSize);
        }
        raf.seek(offset);
        raf.readFully(outputArray, position, length);
        streamLength = Math.max(newSize, streamLength);

    }
    
    // Add a number to the output, swapping if required
    private void putSwapped(int value, int position, int length) {
        int delta;
        int result = 0;
        if (intel) {
            delta = +1;
        } else {
            delta = -1;
            position += length;
            position--;
        }

        for (int i = 0; i < length; i++) {
            putByte(value & 0xFF, position);
            value >>= 8;
            position += delta;
        }
    }

    // Get given number of bytes from the output array in specified byte order
    private int getSwapped(int offset, int length) throws IOException {
        if (offset + length > streamLength) {
            Log.quit("Output array too short ("+(offset+length)+")");
        }
        int delta;
        int result = 0;
        if (intel) {
            delta = -1;
            offset += length;
            offset--;
        } else {
            delta = +1;
        }

        for (int i = 0; i < length; i++) {
            result <<= 8;
            result |= outputArray[offset] & 0xFF;
            offset += delta;
        }

        return result;
    }
   
    
    // Add a single byte to the output.
    private void putByte(int value, int position) {
        if (position >= outputArray.length) {
            expandArray(position + 1);
        }
        outputArray[position] = (byte) value;
        streamLength = Math.max(position + 1, streamLength);
    }
    
    // Increase array size, if required
    private void expandArray(int position) {
        if (position > outputArray.length) {
            byte[] newOutputArray = new byte[Math.max(outputArray.length * 2, position)];
            System.arraycopy(outputArray, 0,newOutputArray, 0, outputArray.length);
            outputArray = newOutputArray;
        }
    }
    
}
