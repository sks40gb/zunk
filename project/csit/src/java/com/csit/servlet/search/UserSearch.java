/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.csit.servlet.search;

import com.csit.alert.ErrorColl;
import com.csit.jsp.JspPage;
import com.csit.sql.table.model.UserModel;
import com.csit.servlet.Attribute;
import com.csit.servlet.ParentServlet;
import java.io.IOException;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Admin
 */
public class UserSearch extends ParentServlet {
   
  protected void processRequest(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        try {
            UserModel user = new UserModel();
            user.setFirstName(request.getParameter(UserModel.FIRST_NAME));
            user.setLastName(request.getParameter(UserModel.LAST_NAME));
            if(request.getParameter("all") != null && request.getParameter("all").equals("1")){
            //    request.setAttribute("userList", Search.getAllUserList(user));
            }else{
             //   request.setAttribute("userList", Search.getUserList(user));
            }
            request.setAttribute("searchOf", "StaffMode");
            RequestDispatcher dis = getServletContext().getRequestDispatcher(JspPage.SEARCH_RESULT);
            dis.forward(request, response);
        } catch (Exception e) {
            request.setAttribute(Attribute.ERROR, new ErrorColl(this,e));
            getServletContext().getRequestDispatcher(JspPage.ALERT_ERROR).forward(request, response);
        }
    }
}
