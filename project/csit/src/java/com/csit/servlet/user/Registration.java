/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.csit.servlet.user;

import com.csit.alert.ErrorColl;
import com.csit.alert.Message;
import com.csit.jsp.JspPage;
import com.csit.sql.table.model.AddressDetailModel;
import com.csit.sql.table.model.ContactDetailModel;
import com.csit.sql.table.model.UserModel;
import com.csit.role.Role;
import com.csit.servlet.Attribute;
import com.csit.servlet.ParentServlet;
import java.io.IOException;
import java.sql.PreparedStatement;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author sunil
 */
public class Registration extends ParentServlet {

    PreparedStatement ps;
    ContactDetailModel contact;
    AddressDetailModel address;
    UserModel user;

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            user = new UserModel(request);
            user.setType(Role.USER);
            contact = new ContactDetailModel(request);
            address = new AddressDetailModel(request);
            insertRecords();
            RequestDispatcher dis = getServletContext().getRequestDispatcher(JspPage.ALERT_MESSAGE);
            dis.forward(request, response);

        } catch (Exception e) {
            request.setAttribute(Attribute.ERROR, new ErrorColl(this, e));
            getServletContext().getRequestDispatcher(JspPage.ALERT_ERROR).forward(request, response);
        }
    }

    private void insertRecords() throws Exception {
        user.save();
        contact.setUserId(user.getUserId());
        contact.save();
        address.setContactDetailsId(contact.getContactDetailsId());
        address.save();
        Message.setMessage("USER IS REGISTERED SUCCESSFULLY");
    }
}
