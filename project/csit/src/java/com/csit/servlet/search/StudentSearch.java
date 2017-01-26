/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.csit.servlet.search;

import com.csit.alert.ErrorColl;
import com.csit.jsp.JspPage;
import com.csit.sql.table.model.CourseModel;
import com.csit.sql.table.model.UserModel;
import com.csit.role.Role;
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
public class StudentSearch extends ParentServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            UserModel student = new UserModel();
            String firstName = request.getParameter(UserModel.FIRST_NAME);
            String lastName = request.getParameter(UserModel.LAST_NAME);
            if (firstName == null && lastName == null) {
                UserModel user = new UserModel();
                user.addCriteria(UserModel.TYPE, Role.STUDENT);
                request.setAttribute("userList", user.getModelList());
            } else {
                student.setFirstName(firstName);
                student.setLastName(lastName);
                int branchId = Integer.parseInt(request.getParameter(CourseModel.ID));
               // request.setAttribute("userList", Search.getStudentList(student, branchId));
            }
            request.setAttribute("searchOf", "StudentMode");
            RequestDispatcher dis = getServletContext().getRequestDispatcher(JspPage.SEARCH_RESULT);
            dis.forward(request, response);

        } catch (Exception e) {
            request.setAttribute(Attribute.ERROR, new ErrorColl(this, e));
            getServletContext().getRequestDispatcher(JspPage.ALERT_ERROR).forward(request, response);
        }
    }  
}
