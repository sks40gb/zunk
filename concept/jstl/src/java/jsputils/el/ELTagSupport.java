package jsputils.el;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import javax.servlet.jsp.JspContext;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.SimpleTagSupport;

public class ELTagSupport extends SimpleTagSupport {
    protected Object evaluate(String expression,
            Object expectedType) throws JspException {
        return ELUtils.evaluate(
            expression, expectedType, getJspContext());
    }

    private Object queryPageContext(
            String name, Class expectedType) {
        try {
            String expr = "${pageContext." + name + "}";
            return evaluate(expr, expectedType);
        } catch (JspException x) {
            return null;
        }
    }

    protected HttpServletRequest getRequest() {
        Object request;
        JspContext jspContext = getJspContext();
        if (jspContext instanceof PageContext)
            request = ((PageContext) jspContext).getRequest();
        else
            request = queryPageContext("request",
                HttpServletRequest.class);
        return (HttpServletRequest) request;
    }

    protected HttpServletResponse getResponse() {
        Object response;
        JspContext jspContext = getJspContext();
        if (jspContext instanceof PageContext)
            response = ((PageContext) jspContext).getResponse();
        else
            response = queryPageContext("response",
                HttpServletResponse.class);
        return (HttpServletResponse) response;
    }

    protected HttpSession getSession() {
        Object session;
        JspContext jspContext = getJspContext();
        if (jspContext instanceof PageContext)
            session = ((PageContext) jspContext).getSession();
        else
            session = queryPageContext("session",
                HttpSession.class);
        return (HttpSession) session;
    }

    protected ServletContext getApplication() {
        Object application;
        JspContext jspContext = getJspContext();
        if (jspContext instanceof PageContext)
            application = ((PageContext) jspContext)
                .getServletContext();
        else
            application = queryPageContext("servletContext",
                ServletContext.class);
        return (ServletContext) application;
    }

}
