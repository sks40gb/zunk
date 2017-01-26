/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.csit.servlet.course;

import com.csit.alert.ErrorColl;
import com.csit.jsp.JspPage;
import com.csit.sql.table.model.CourseModel;
import com.csit.mode.Mode;
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
public class CourseMode extends ParentServlet {
   

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try {
            String mode = request.getParameter("mode");
            int courseId = Integer.parseInt(request.getParameter(CourseModel.ID));           
            if (Mode.UPDATE.equals(mode)) {
                CourseModel course = new CourseModel();
                course.setCourseId(courseId);
                course.getModelById();
                request.setAttribute("course", course);
                RequestDispatcher dis = getServletContext().getRequestDispatcher(JspPage.COURSE_UDPATE);
                dis.forward(request, response);
            } else {
                RequestDispatcher dis = getServletContext().getRequestDispatcher(JspPage.ALERT_ERROR);
                dis.forward(request, response);
            }

        } catch (Exception e) {
            request.setAttribute(Attribute.ERROR, new ErrorColl(this,e));
            getServletContext().getRequestDispatcher(JspPage.ALERT_ERROR).forward(request, response);
        }
    }
}
