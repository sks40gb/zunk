/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.csit.servlet.admin;

import com.csit.alert.ErrorColl;
import com.csit.alert.Message;
import com.csit.jsp.JspPage;
import com.csit.mode.Mode;
import com.csit.sql.table.model.AddressDetailModel;
import com.csit.sql.table.model.ContactDetailModel;
import com.csit.sql.table.model.UserModel;
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
public class AddUpdateAdmin extends ParentServlet {

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
            if(Mode.UPDATE.equals(request.getParameter("mode"))){
                updateRecords(request);
            }else{
                insertRecords();
            }
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
        Message.setMessage("ADMIN HAS BEEN ADDED SUCCESSFULLY");
    }

    private void updateRecords(HttpServletRequest request) throws IllegalArgumentException, Exception {      
        /** set ids */
        user.setUserId(Integer.parseInt(request.getParameter(UserModel.ID)));
        contact.setContactDetailsId(Integer.parseInt(request.getParameter(ContactDetailModel.ID)));
        address.setAddressDetailId(Integer.parseInt(request.getParameter(AddressDetailModel.ID)));

        /** update record */      
        user.update();
        contact.update();
        address.update();

        Message.setMessage("RECORD HAS BEEN UPDATED SUCCESSFULLY");
    }
}
