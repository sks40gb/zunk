/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.csit.servlet.staff;

import com.csit.alert.ErrorColl;
import com.csit.alert.Message;
import com.csit.jsp.JspPage;
import com.csit.sql.table.model.AddressDetailModel;
import com.csit.sql.table.model.ContactDetailModel;
import com.csit.sql.table.model.StaffModel;
import com.csit.sql.table.model.UserModel;
import com.csit.role.Role;
import com.csit.servlet.Attribute;
import com.csit.servlet.ParentServlet;
import java.io.IOException;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Admin
 */
public class AddStaff extends ParentServlet {

    private ContactDetailModel contact;
    private AddressDetailModel address;
    private UserModel user;
    private StaffModel staff;

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            HttpSession session = request.getSession();

            staff = new StaffModel(request);
            user = (UserModel) session.getAttribute("user");
            contact = (ContactDetailModel) session.getAttribute("contact");
            address = (AddressDetailModel) session.getAttribute("address");
            session.removeAttribute("user");
            session.removeAttribute("contact");
            session.removeAttribute("address");
            insertRecords();
            RequestDispatcher dis = getServletContext().getRequestDispatcher(JspPage.ALERT_MESSAGE);
            dis.forward(request, response);

        } catch (Exception e) {
            request.setAttribute(Attribute.ERROR, new ErrorColl(this, e));
            getServletContext().getRequestDispatcher(JspPage.ALERT_ERROR).forward(request, response);
        }
    }

    private void insertRecords() throws Exception {
        user.setType(Role.STAFF);
        user.save();
        contact.setUserId(user.getUserId());
        contact.save();
        address.setContactDetailsId(contact.getContactDetailsId());
        address.save();
        staff.setUserId(user.getUserId());
        staff.save();
        Message.setMessage("STAFF IS ADDED SUCCESSFULLY");
    }
}
