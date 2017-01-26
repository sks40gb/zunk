/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.csit.servlet.filter;

import com.csit.alert.ErrorColl;
import com.csit.jsp.JspPage;
import com.csit.servlet.Attribute;
import java.io.IOException;
import java.util.List;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

/**
 *
 * @author sunil
 */
public class ApplicationFilter implements Filter {

    private FilterConfig filterConfig = null;

    public void init(FilterConfig filterConfig)
            throws ServletException {
        this.filterConfig = filterConfig;
    }

    public void destroy() {

        this.filterConfig = null;
    }

    public void doFilter(ServletRequest request,
            ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        List<String> list = new Validate(request).run();
        if (list != null) {
            ErrorColl error = new ErrorColl();
            error.setErrorList(list);
            request.setAttribute(Attribute.ERROR,error);
            filterConfig.getServletContext().getRequestDispatcher(JspPage.ALERT_ERROR).forward(request, response);
            return;

        }
        //System.out.println(request);
        chain.doFilter(request, response);
    }
}
 











