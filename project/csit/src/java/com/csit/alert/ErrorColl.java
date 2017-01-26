/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.csit.alert;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author sunil
 */
public class ErrorColl {

    private String errorMessage;
    private String exception;
    private List<String> errorList;

    public ErrorColl() {
    }

    public ErrorColl(Object object,String errorMessage) {
        this.errorMessage = object.getClass() + "   :  <br>" + errorMessage;
    }
    public ErrorColl(Object object, Exception e) {
        exception = e.toString();
        e.printStackTrace();
    }

    public String getErrorMessage() {
        return errorMessage == null ? "" : errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        exception = null;
    }

    public String getException() {
        return exception == null ? "" : exception;
    }

    public void setException(Exception e) {
        if (e != null) {
            errorMessage = null;
            exception = e.toString();
        }
    }

    public List<String> getErrorList() {
        return errorList == null ?  new ArrayList<String>() : errorList;
    }

    public void setErrorList(List<String> errorList) {
        this.errorList = errorList;
    }
}
