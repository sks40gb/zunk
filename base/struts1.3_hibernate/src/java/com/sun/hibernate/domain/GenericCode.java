/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.sun.hibernate.domain;

/**
 *
 * @author shanmugam
 */
public class GenericCode extends  Domain {
     private Integer genericCodeId;
     private Integer codetypeid;
     private String code;
     private String codedesc;
     private String field1;
     private String field2;
     private String field3;
     private String field4;
     private String field5;
     private String field6;
     private String field7;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getCodedesc() {
        return codedesc;
    }

    public void setCodedesc(String codedesc) {
        this.codedesc = codedesc;
    }

    public Integer getCodetypeid() {
        return codetypeid;
    }

    public void setCodetypeid(Integer codetypeid) {
        this.codetypeid = codetypeid;
    }

    public String getField1() {
        return field1;
    }

    public void setField1(String field1) {
        this.field1 = field1;
    }

    public String getField2() {
        return field2;
    }

    public void setField2(String field2) {
        this.field2 = field2;
    }

    public String getField3() {
        return field3;
    }

    public void setField3(String field3) {
        this.field3 = field3;
    }

    public String getField4() {
        return field4;
    }

    public void setField4(String field4) {
        this.field4 = field4;
    }

    public String getField5() {
        return field5;
    }

    public void setField5(String field5) {
        this.field5 = field5;
    }

    public String getField6() {
        return field6;
    }

    public void setField6(String field6) {
        this.field6 = field6;
    }

    public String getField7() {
        return field7;
    }

    public void setField7(String field7) {
        this.field7 = field7;
    }

    public Integer getGenericCodeId() {
        return genericCodeId;
    }

    public void setGenericCodeId(Integer genericCodeId) {
        this.genericCodeId = genericCodeId;
    }
     public GenericCode(){

     }
}
