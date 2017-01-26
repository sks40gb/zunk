package jsputils.tags;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.JspFragment;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import java.io.IOException;

public class IfTag extends SimpleTagSupport {
    private boolean testAttr;
    private JspFragment trueAttr;
    private JspFragment falseAttr;
    private JspFragment errorAttr;

    public void setTest(boolean test) {
        testAttr = test;
    }

    public void setTRUE(JspFragment fragment) {
        trueAttr = fragment;
    }

    public void setFALSE(JspFragment fragment) {
        falseAttr = fragment;
    }

    public void setERROR(JspFragment fragment) {
        errorAttr = fragment;
    }

    public void doTag() throws JspException, IOException {
        try {
            if (testAttr) {
                if (trueAttr != null)
                    trueAttr.invoke(null);
            } else {
                if (falseAttr != null)
                    falseAttr.invoke(null);
            }
        } catch (Exception x) {
            if (errorAttr != null)
                errorAttr.invoke(null);
            else
                throw new JspException(x);
        }
    }

}
