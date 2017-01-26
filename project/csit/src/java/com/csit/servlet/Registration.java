/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.csit.servlet;

import com.csit.alert.ErrorColl;
import com.csit.alert.Message;
import com.csit.jsp.JspPage;
import com.csit.sql.table.model.AddressDetailModel;
import com.csit.sql.table.model.ContactDetailModel;
import com.csit.sql.table.model.UserModel;
import java.io.IOException;
import java.sql.PreparedStatement;
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
            contact = new ContactDetailModel(request);
            address = new AddressDetailModel(request);
            insertRecords(request, response);
            getServletContext().getRequestDispatcher(JspPage.ALERT_MESSAGE).forward(request, response);


        } catch (Exception e) {
            request.setAttribute(Attribute.ERROR, new ErrorColl(this, e));
            getServletContext().getRequestDispatcher(JspPage.ALERT_ERROR).forward(request, response);
        }
    }

    private void insertRecords(HttpServletRequest request, HttpServletResponse response) throws Exception {
        user.save();
        contact.setUserId(user.getUserId());
        contact.save();
        address.setContactDetailsId(contact.getUserId());
        address.save();
        Message.setMessage("REGISTERED SUCCESSFULLY");
    }
  
}
