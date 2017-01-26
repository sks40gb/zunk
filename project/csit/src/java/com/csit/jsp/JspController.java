/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.csit.jsp;

import java.io.IOException;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author sunil
 */
public class JspController {

    private HttpServletRequest request;
    private HttpServletResponse response;

    public JspController(HttpServletRequest request, HttpServletResponse response) {
        this.request = request;
        this.response = response;
    }

    public void forward(String jsp) throws ServletException, IOException{
        RequestDispatcher dis = request.getRequestDispatcher(request.getContextPath() + "/" + JspPage.ALERT_MESSAGE);
        dis.forward(request, response);
    }


}
