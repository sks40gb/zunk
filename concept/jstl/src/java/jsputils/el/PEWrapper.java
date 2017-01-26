package jsputils.el;

import javax.servlet.jsp.JspContext;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.el.ELException;
import javax.servlet.jsp.el.Expression;
import javax.servlet.jsp.el.ExpressionEvaluator;
import javax.servlet.jsp.el.FunctionMapper;
import javax.servlet.jsp.el.VariableResolver;

public class PEWrapper {
    private Expression parsedExpression;
    private JspContext jspContext;

    private PEWrapper(Expression parsedExpression,
            JspContext jspContext) {
        this.parsedExpression = parsedExpression;
        this.jspContext = jspContext;
    }

    public static PEWrapper getInstance(String expression,
            Object expectedType, JspContext jspContext,
            FunctionMapper functionMapper)
            throws JspException {
        try {
            ExpressionEvaluator evaluator
                = jspContext.getExpressionEvaluator();
            Expression parsedExpression
                = evaluator.parseExpression(expression,
                    ELUtils.getClass(expectedType),
                    functionMapper);
            return new PEWrapper(parsedExpression, jspContext);
        } catch (ELException x) {
            throw new JspException(x);
        }
    }

    public Object evaluate() throws JspException {
        try {
            VariableResolver variableResolver
                = jspContext.getVariableResolver();
            return parsedExpression.evaluate(
                variableResolver);
        } catch (ELException x) {
            throw new JspException(x);
        }
    }

    public static Object evaluate(PEWrapper pew)
            throws JspException {
        return pew.evaluate();
    }

}
