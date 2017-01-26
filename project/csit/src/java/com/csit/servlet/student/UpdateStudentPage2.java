/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.csit.servlet.student;

import com.csit.alert.ErrorColl;
import com.csit.alert.Message;
import com.csit.jsp.JspPage;
import com.csit.sql.table.model.AddressDetailModel;
import com.csit.sql.table.model.ContactDetailModel;
import com.csit.sql.table.model.StudentModel;
import com.csit.sql.table.model.UserModel;
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
 * @author Admin
 */
public class UpdateStudentPage2 extends ParentServlet {
   
    private ContactDetailModel contact;
    private AddressDetailModel address;
    private UserModel user;
    private StudentModel student;

  protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            HttpSession session = request.getSession();
            user = (UserModel)session.getAttribute("user");
            contact = (ContactDetailModel)session.getAttribute("contact");
            address = (AddressDetailModel)session.getAttribute("address");
            student = new StudentModel(request);                       
            
            updateRecords();
            RequestDispatcher dis = getServletContext().getRequestDispatcher(JspPage.ALERT_MESSAGE);
            dis.forward(request, response);

        } catch (Exception e) {
            request.setAttribute(Attribute.ERROR, new ErrorColl(this,e));
            getServletContext().getRequestDispatcher(JspPage.ALERT_ERROR).forward(request, response);
        }
    }

    private void updateRecords() throws Exception {
        user.update();
        contact.setUserId(user.getUserId());
        contact.update();
        address.setContactDetailsId(contact.getContactDetailsId());
        address.update();
        student.setUserId(user.getUserId());
        student.update();
        Message.setMessage("RECORD HAS BEEN UPDATED SUCCESSFULLY");
    }   

}
