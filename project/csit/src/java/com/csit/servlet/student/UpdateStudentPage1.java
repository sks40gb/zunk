/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.csit.servlet.student;

import com.csit.alert.ErrorColl;
import com.csit.jsp.JspPage;
import com.csit.sql.table.model.AddressDetailModel;
import com.csit.sql.table.model.ContactDetailModel;
import com.csit.sql.table.model.StudentModel;
import com.csit.sql.table.model.UserModel;
import com.csit.role.Role;
import com.csit.servlet.Attribute;
import com.csit.servlet.ParentServlet;
import java.io.IOException;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 *
 * @author abhilasha
 */
public class UpdateStudentPage1 extends ParentServlet {
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
         try {

            HttpSession session = request.getSession();
            UserModel user = new UserModel(request);
            user.setType(Role.STUDENT);
            ContactDetailModel contact = new ContactDetailModel(request);
            AddressDetailModel address = new AddressDetailModel(request);            
            session.setAttribute("user", user);
            session.setAttribute("contact", contact);
            session.setAttribute("address", address);
            //session.setAttribute("student", StudentModel.getModelByUserId(user.getId()));
            StudentModel student = new StudentModel();
            student.addCriteria(StudentModel.USER_ID, user.getUserId());
            student.getModel();            
            request.setAttribute("student", student);
            RequestDispatcher dis = getServletContext().getRequestDispatcher(JspPage.STUDENT_UPDATE_PAGE_2);
            dis.forward(request, response);
        } catch (Exception e) {
            request.setAttribute(Attribute.ERROR, new ErrorColl(this,e));
            getServletContext().getRequestDispatcher(JspPage.ALERT_ERROR).forward(request, response);
        }
    }
 

}
