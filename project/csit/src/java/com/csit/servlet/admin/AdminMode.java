/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.csit.servlet.admin;

import com.csit.alert.ErrorColl;
import com.csit.jsp.JspPage;
import com.csit.sql.table.model.AddressDetailModel;
import com.csit.sql.table.model.ContactDetailModel;
import com.csit.sql.table.model.UserModel;
import com.csit.mode.Mode;
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
 * @author sunil
 */
public class AdminMode extends ParentServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");        
        try {
            String mode = request.getParameter("mode");
            String userName = request.getParameter(UserModel.USER_NAME);           
            if (Mode.UPDATE.equals(mode)) {

                UserModel user = new UserModel();
                user.addCriteria(UserModel.USER_NAME, userName);
                user.addCriteria(UserModel.TYPE, Role.ADMIN);
                user.getModel();                

                ContactDetailModel contact = new ContactDetailModel();
                contact.addCriteria(ContactDetailModel.USER_ID, user.getUserId());
                contact.getModel();

                AddressDetailModel address = new AddressDetailModel();
                address.addCriteria(AddressDetailModel.CONTACT_DETAILS_ID, contact.getContactDetailsId());
                address.getModel();
               
                user.setDisabled(true);
                request.setAttribute("user", user);
                request.setAttribute("contact", contact);
                request.setAttribute("address", address);
                request.setAttribute("mode", Mode.UPDATE);
                RequestDispatcher dis = getServletContext().getRequestDispatcher(JspPage.ADMIN_ADD_UPDATE);
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
