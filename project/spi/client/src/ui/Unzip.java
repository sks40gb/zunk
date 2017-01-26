package ui;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import javax.swing.JDialog;
import javax.swing.JOptionPane;

public class Unzip
{
   private static final String FILESEPARATOR = File.separator;
   private JDialog parent = null;   
   private boolean isUnzipped = false;
   
   public Unzip(JDialog parent,String folderPath,String fileName){
     try{     
         this.parent = parent;         
         FileInputStream fis = new FileInputStream(new File(folderPath+File.separator+fileName));          
         
         if(storeZipStream(fis, folderPath)){
            parent.setVisible(false);
            TSearchDialog tSearch = new TSearchDialog(parent,folderPath);
            tSearch.setLocationRelativeTo(null);
            tSearch.setVisible(true);
         }
         
      }catch(Exception e){
         System.err.println("--Exception raised----"+e.getMessage());
         JOptionPane.showMessageDialog(null,"Cannot read the file from server\nPlease verify the path.","File Error Message",JOptionPane.ERROR_MESSAGE);
      }
   }
   
   

   public boolean storeZipStream(InputStream inputStream, String dir) throws IOException
   {

      ZipInputStream zis = new ZipInputStream(inputStream);
      ZipEntry entry = null;
      int countEntry = 0;
      if (!dir.endsWith(FILESEPARATOR)) {
         dir += FILESEPARATOR;
      }

      // check inputStream is ZIP or not
      if ((entry = zis.getNextEntry()) != null) {
         do {
            String entryName = entry.getName();
            // Directory Entry should end with FileSeparator
            if (!entry.isDirectory()) {
               // Directory will be created while creating file with in it.
               String fileName = dir + entryName;
               createFile(zis, fileName);
               countEntry++;
            }
         }
         while ((entry = zis.getNextEntry()) != null);
         System.out.println("No of files Extracted : " + countEntry);
         isUnzipped = true;
      }
      else {
         isUnzipped = false;
         JOptionPane.showMessageDialog(null,"Given file is not a Compressed one");
         throw new IOException("Given file is not a Compressed one");
      }
      return isUnzipped;
   }

   public void createFile(InputStream is, String absoluteFileName) throws IOException
   {

      File f = new File(absoluteFileName);

      if (!f.getParentFile().exists()) {
         f.getParentFile().mkdirs();
      }
      OutputStream out = new FileOutputStream(absoluteFileName);
      byte[] buf = new byte[1024];
      int len = 0;
      while ((len = is.read(buf)) > 0) {
         out.write(buf, 0, len);
      }
      // Close the streams
      out.close();
   }
}
