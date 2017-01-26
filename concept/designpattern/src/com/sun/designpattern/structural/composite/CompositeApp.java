package com.sun.designpattern.structural.composite;

import java.util.ArrayList;
import java.util.Collection;

/**
 *
 * @author Sunil
 */
public class CompositeApp {

    public static void main(String[] args) {
        File f1 = new Folder("A");
        File f2 = new Folder("B");
        File f3 = new Folder("C");
        File f4 = new Folder("B");

        f3.addFile(f1);
        f3.addFile(f2);
        f4.addFile(f3);
        f4.showAllFile();
    }
}

interface File {

    public String getName();

    public boolean isFolder();

    public void addFile(File file);

    public void showAllFile();
}

class Folder implements File {

    private String name;
    private boolean folder;
    private Collection<File> files;

    public Folder(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setFolder(boolean folder) {
        this.folder = folder;
    }

    public boolean isFolder() {
        return folder;
    }

    public Collection<File> getFiles() {
        return files;
    }

    public void addFile(File file) {
        if (files == null) {
            files = new ArrayList<File>();
        }
        files.add(file);
    }

    public void showAllFile() {
        if (files != null) {
            for (File file : files) {
                System.out.println(getName() + " File Name : " + file.getName());
                file.showAllFile();
            }
        }
    }
}
