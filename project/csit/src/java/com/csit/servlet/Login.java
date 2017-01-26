package com.csit.servlet;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
import com.csit.alert.ErrorColl;
import com.csit.jsp.JspPage;
import com.csit.sql.table.model.UserModel;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/*

@author hcl
 */
public class Login extends ParentServlet {
    protected void processRequest(HttpServletRequest request, HttpServletResponse response) {
        try {

           UserModel user = new UserModel(request);
           user.addCriteria(UserModel.USER_NAME);
           user.addCriteria(UserModel.PASSWORD);
           user.getModel();
           
           if(user != null && user.getType() != null){
                request.getSession().setAttribute(Attribute.CURRENT_USER, user);
                response.sendRedirect(request.getContextPath() + JspPage.ALERT_MESSAGE);
            } else {
                //user invalid
                request.setAttribute(Attribute.ERROR, new ErrorColl(this, "Login Failed"));
                getServletContext().getRequestDispatcher(JspPage.ALERT_ERROR).forward(request, response);
            }       

        } catch (Exception e) {            
            request.setAttribute(Attribute.ERROR, new ErrorColl(this, e));
            try {
                getServletContext().getRequestDispatcher(JspPage.ALERT_ERROR).forward(request, response);
            } catch (ServletException ex) {
                Logger.getLogger(Login.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(Login.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
