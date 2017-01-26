/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.csit.servlet.course;

import com.csit.alert.ErrorColl;
import com.csit.alert.Message;
import com.csit.jsp.JspPage;
import com.csit.sql.table.model.CourseCategoryModel;
import com.csit.servlet.Attribute;
import com.csit.servlet.ParentServlet;
import java.io.IOException;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author sunil
 */
public class AddCourseCategory extends ParentServlet {
   
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            CourseCategoryModel course = new CourseCategoryModel(request);
            course.save();
            Message.setMessage("COURSE CATEGORY HAS BEEN ADDED SUCCESSFULLY");
            RequestDispatcher dis = getServletContext().getRequestDispatcher(JspPage.ALERT_MESSAGE);
            dis.forward(request, response);

        } catch (Exception e) {
            request.setAttribute(Attribute.ERROR, new ErrorColl(this,e));
            getServletContext().getRequestDispatcher(JspPage.ALERT_ERROR).forward(request, response);
        }
    }
}
