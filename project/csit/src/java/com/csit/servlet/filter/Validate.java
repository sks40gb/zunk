/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.csit.servlet.filter;

import com.avi.util.DateFormatter;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.servlet.ServletRequest;

/**
 *
 * @author sunil
 */
public class Validate {

    private ServletRequest request;
    private Map<String, String[]> inputFieldMap;
    private List<String> errorList;

    public Validate(ServletRequest request) {
        this.request = request;
        init();
    }

    private void init() {
        inputFieldMap = new TreeMap<String, String[]>();
        for (Enumeration en = request.getParameterNames(); en.hasMoreElements();) {
            String name = (String) en.nextElement();
            String values[] = request.getParameterValues(name);
            inputFieldMap.put(name, values);
        }
    }

    public List<String> run() {
        for (String key : inputFieldMap.keySet()) {
            Rule[] rules = ValidationField.getValidateFieldMap().get(key);
            if (rules != null) {
                for (Rule r : rules) {
                    switch (r) {
                        case NULL:
                            checkNull(key, inputFieldMap.get(key));
                            break;
                        case EMAIL:
                            checkEmail(key, inputFieldMap.get(key));
                            break;
                        case DATE:
                            checkDate(key, inputFieldMap.get(key));
                            break;
                        case INTEGER:
                            checkInteger(key, inputFieldMap.get(key));
                            break;
                        case DOUBLE:
                            checkDouble(key, inputFieldMap.get(key));
                            break;
                        case FLOAT:
                            checkFloat(key, inputFieldMap.get(key));
                            break;
                    }
                }
            }

        }
        return errorList;
    }

    private void checkEmail(String name, String[] values) {
        for (String value : values) {
            checkEmail(name, value);
        }
    }

    private void checkEmail(String name, String email) {
        //String email = "xyz@hotmail.com";
        Pattern p = Pattern.compile(".+@.+\\.[a-z]+");
        Matcher m = p.matcher(email);
        boolean matchFound = m.matches();
        if (matchFound) {
            //System.out.println("Valid Email Id.");
        } else {
            addError("Invalid Email : " + email);
        }
    }

    private void checkDate(String name, String[] values) {
        for (String value : values) {
            if (DateFormatter.isValidDate(value)) {
                //System.out.println("Valid Date Id.");
            } else {
                addError("Invalid Date : " + value);
            }
        }
    }

    private void checkNull(String name, String[] values) {
        for (String value : values) {
            if (value == null || value.trim().equals("")) {
                addError(name + " is Required ");
            }
        }
    }

    private void checkInteger(String name, String[] values) {
        for (String value : values) {
            try {
                Integer.parseInt(value);
            } catch (NumberFormatException e) {
                addError(name + " should be Integer");
            }
        }
    }

    private void checkDouble(String key, String[] values) {
        for (String value : values) {
            try {
                Double.parseDouble(value);
            } catch (NumberFormatException e) {
                addError(key + " should be Double value");
            }
        }
    }

    private void checkFloat(String key, String[] values) {
        for (String value : values) {
            try {
                Float.parseFloat(value);
            } catch (NumberFormatException e) {
                addError(key + " should be Float value");
            }
        }
    }

    public void addError(String error) {
        if (errorList == null) {
            errorList = new ArrayList<String>();
        }
        errorList.add(error);
    }
}



















