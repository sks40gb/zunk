package jsputils.tags;

import jsputils.el.PEWrapper;
import jsputils.el.FNMapper;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import java.io.IOException;

public class WhileTag extends SimpleTagSupport {
    private String strTest;

    public void setTest(String test) throws JspException {
        strTest = test;
    }

    public void doTag() throws JspException, IOException {
        PEWrapper parsedExpr = PEWrapper.getInstance(
            strTest, Boolean.class, getJspContext(),
            FNMapper.getInstance("fn"));
        while (((Boolean) parsedExpr.evaluate()).booleanValue())
            getJspBody().invoke(null);
    }

}
