package jsputils.el;

import javax.servlet.jsp.JspContext;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.el.ELException;
import javax.servlet.jsp.el.ExpressionEvaluator;
import javax.servlet.jsp.el.FunctionMapper;
import javax.servlet.jsp.el.VariableResolver;

public class ELUtils {
    public static Object evaluate(String expression,
            Object expectedType, JspContext jspContext)
            throws JspException {
        return evaluate(expression, expectedType,
            jspContext, null);
    }

    public static Object evaluate(String expression,
            Object expectedType, JspContext jspContext,
            FunctionMapper functionMapper)
            throws JspException {
        try {
            ExpressionEvaluator evaluator
                = jspContext.getExpressionEvaluator();
            VariableResolver variableResolver
                = jspContext.getVariableResolver();
            return evaluator.evaluate(
                expression, getClass(expectedType),
                variableResolver, functionMapper);
        } catch (ELException x) {
            throw new JspException(x);
        }
    }

    public static Class getClass(Object expectedType)
            throws JspException {
        if (expectedType instanceof Class)
            return (Class) expectedType;
        String strType = expectedType.toString();
        try {
            return Class.forName(strType);
        } catch (ClassNotFoundException x) {
            throw new JspException(x);
        }
    }

}
