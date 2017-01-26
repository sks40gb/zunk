package jsputils.tags;

import jsputils.el.ELUtils;

import javax.servlet.jsp.JspException;

public class EvalTag extends VarTagSupport {
    private String strExpr;
    private Object varType;

    public EvalTag() {
        varType = Object.class;
    }

    public void setExpr(String expr) throws JspException {
        strExpr = expr;
    }

    public void setType(Object type) throws JspException {
        varType = type;
    }

    protected Object evaluate(String expression,
            Object expectedType) throws JspException {
        return ELUtils.evaluate(
            expression, expectedType, getJspContext());
    }

    public void doTag() throws JspException {
        if (strExpr == null)
            strExpr = invokeBody();
        Object value = evaluate(strExpr, varType);
        boolean exported = export(value);
        if (!exported && value != null)
            write(value.toString());
    }

}
