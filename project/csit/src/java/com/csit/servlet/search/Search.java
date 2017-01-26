/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.csit.servlet.search;

import com.avi.sql.table.CriteriaType;
import com.csit.alert.ErrorColl;
import com.csit.jsp.JspPage;
import com.csit.servlet.Attribute;
import com.csit.servlet.ParentServlet;
import com.csit.sql.table.model.UserModel;
import java.io.IOException;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author sunil
 */
public class Search  extends ParentServlet {

   public static final String FIRST_NAME = "FIRSTnAME29093023";
   public static final String LAST_NAME = "LASTnAME29093023";
   public static final String BRANCH_NAME = "BRANCH29093023";
   public static final String USER_TYPE = "USER_TYPE29093023";
   public static final String STAFF_TYPE = "STAFF_TYPE29093023";


   @Override 
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {           
            String userType = request.getParameter(USER_TYPE);
            String firstName = request.getParameter(FIRST_NAME);
            String lastName = request.getParameter(LAST_NAME);            

            UserModel user = new UserModel();
            user.addCriteria(UserModel.FIRST_NAME, firstName, CriteriaType.LIKE);
            user.addCriteria(UserModel.LAST_NAME, lastName, CriteriaType.LIKE);
            if(userType != null && !userType.equals("ALL")){
                user.addCriteria(UserModel.TYPE, userType);
            }
            request.setAttribute("userList", user.getModelList());            
            RequestDispatcher dis = getServletContext().getRequestDispatcher(JspPage.SEARCH_RESULT);
            dis.forward(request, response);

        } catch (Exception e) {
            request.setAttribute(Attribute.ERROR, new ErrorColl(this, e));
            getServletContext().getRequestDispatcher(JspPage.ALERT_ERROR).forward(request, response);
        }
    }

}
