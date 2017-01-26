/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.util.zip;

/**
 *
 * @author Administrator
 */
import java.io.*;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarFile;
import java.util.jar.JarEntry;

class testZipFiles {

    public static void main(String[] args) throws IOException {
        File jarFile = new File("C:\\Documents and Settings\\administrator.CONGSERVER\\Desktop\\current\\xmlwrite.zip");

        for(File f : getSelectedFiles(getExtartedFiles(jarFile),"java")){
            System.out.println("F : " + f.getName());
        }
    }

    public static List<File> getSelectedFiles(List<File> files, String ext){
        List<File> selectedFiles = new ArrayList<File>();
        for(File file : files){
            if(file != null){
                if(file.getName().endsWith("." + ext)){
                    selectedFiles.add(file);
                }
            }
        }
        return selectedFiles;
    }

    public static List<File> getExtartedFiles(File jarFile) throws IOException{
        List<File> files = new ArrayList<File>();
        JarFile jar = new JarFile(jarFile);
        Enumeration enums = jar.entries();
        String destDir = jarFile.getParent();
        while (enums.hasMoreElements()) {
            JarEntry file = (JarEntry) enums.nextElement();
            File f = new File(destDir + File.separator + file.getName());
            files.add(f);
            if (file.isDirectory()) { // if its a directory, create it
                f.mkdir();
                continue;
            }
            InputStream is = jar.getInputStream(file); // get the input stream
            FileOutputStream fos = new FileOutputStream(f);
            while (is.available() > 0) {  // write contents of 'is' to 'fos'
                fos.write(is.read());
            }
            fos.close();
            is.close();
        }
        return files;
    }
}