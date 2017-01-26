/* $Header: /home/common/cvsarea/ibase/dia/src/install/ZipDateUtil.java,v 1.1 2003/10/03 02:41:58 weaston Exp $ */
 
package install;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;



/**
 * Extension to ZipFile, with method to unzip all entries.
 */ 

public class ZipDateUtil extends ZipFile {

    public ZipDateUtil(String name) throws IOException {
        super(name);
    }

    /** 
     * Unzip a zip file and adjust dates in a corresponding directory tree
     * @param dir The directory in which files have dates repaired.
     */

    public void fixDates (String dir) throws IOException {

        byte[] buffer1 = new byte[16384];
        byte[] buffer2 = new byte[16384];

        File dirFile = new File(dir);
        if (! dirFile.exists()) {
            throw new ZipException("Target does not exist");
        } else if (! dirFile.isDirectory()) {
            throw new ZipException("Target is not a directory");
        }

        Enumeration zipEntries = this.entries();

        while (zipEntries.hasMoreElements()) {
            StringBuffer buf = new StringBuffer();
            ZipEntry entry = (ZipEntry) zipEntries.nextElement();

            String entryName = entry.toString();
            if (entryName.endsWith("/")) {
                entryName = entryName.substring(0,entryName.length() - 1);
            }

            if (! entry.isDirectory()) {
            
                File target = new File(dirFile, entryName);
                if (target.isFile() && target.lastModified() != entry.getTime()) {

                    InputStream zipInStream
                            = this.getInputStream(entry);
                    InputStream fileInStream = new FileInputStream(target);
                    int count1 = zipInStream.read(buffer1);
                    int count2 = fileInStream.read(buffer2);
                    int offset1 = 0;
                    int offset2 = 0;
                    lp: while (count1 >= 0 && count2 >= 0) {
                        if (count1 == 0) {
                            count1 = zipInStream.read(buffer1);
                            offset1 = 0;
                        }
                        if (count2 == 0) {
                            count2 = fileInStream.read(buffer2);
                            offset2 = 0;
                        }
                        int count = Math.min(count1, count2);
                        if (count < 0) {
                            break lp;
                        }
                        for (int i = 0; i < count; i++) {
                            if (buffer1[offset1 + i] != buffer2[offset2 + i]) {
                                break lp;
                            }
                        }
                        count1 -= count;
                        count2 -= count;
                        offset1 += count;
                        offset2 += count;
                    }
                    zipInStream.close();
                    fileInStream.close();
                    if (count1 < 0 && count2 < 0) {
                        target.setLastModified(entry.getTime());
                    }
                }
            }
        }
        this.close();
    }

    /**
     * Main program.
     * usage: java install.ZipDateUtil zipfile target
     */
    public static void main(String[] args) {
    
        if (args.length != 2) {
            System.err.println("usage: java install.ZipDateUtil <zipfile> <target>");
            System.exit(1);
        }

        try {
            ZipDateUtil zzz = new ZipDateUtil(args[0]);
            zzz.fixDates(args[1]);
        } catch (IOException e) {
            System.err.println(e.toString());
            System.exit(1);
        }
    }
}
