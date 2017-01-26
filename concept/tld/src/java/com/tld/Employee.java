/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.tld;

import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.JspFragment;
import javax.servlet.jsp.tagext.SimpleTagSupport;

/**
 *
 * @author sunil
 */
public class Employee extends SimpleTagSupport {
    private String name;
    private String roll;

    /**
     * Called by the container to invoke this tag. 
     * The implementation of this method is provided by the tag library developer,
     * and handles all tag processing, body iteration, etc.
     */
    @Override
    public void doTag() throws JspException {
        JspWriter out = getJspContext().getOut();

        try {            
            out.println("<font color='red'>" + name + roll +"</font>");
            JspFragment f = getJspBody();
            if (f != null) f.invoke(out);

        } catch (java.io.IOException ex) {
            throw new JspException("Error in Sample tag", ex);
        }
    }
    
    public void setName(String name) { 
        this.name = name;
    }

    public void setRoll(String roll) {
        this.roll = roll;
    }

}
