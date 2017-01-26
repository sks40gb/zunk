/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fossa.servlet.common;

/**
 *
 * @author sunil
 */
/**
 *  Variables  required to read and save .img file .
 *
 */
public class ImgFieldsData {

    private String ID;
    private String IA;
    private String AN;
    private String IT;
    private String IV;
    
    //Bates number present in the image file 
    private String bates_number;
    private String filename;
    private String path;
    
    //Document number present in the image file 
    private String document_number;

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getIA() {
        return IA;
    }

    public void setIA(String IA) {
        this.IA = IA;
    }

    public String getAN() {
        return AN;
    }

    public void setAN(String AN) {
        this.AN = AN;
    }

    public String getIT() {
        return IT;
    }

    public void setIT(String IT) {
        this.IT = IT;
    }

    public String getIV() {
        return IV;
    }

    public void setIV(String IV) {
        this.IV = IV;
    }

    public String getBates_number() {
        return bates_number;
    }

    public void setBates_number(String bates_number) {
        this.bates_number = bates_number;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getDocument_number() {
        return document_number;
    }

    public void setDocument_number(String document_number) {
        this.document_number = document_number;
    }
}
