/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.csit.servlet.comment;

import com.csit.alert.ErrorColl;
import com.csit.alert.Message;
import com.csit.jsp.JspPage;
import com.csit.sql.table.model.CommentModel;
import com.csit.servlet.Attribute;
import com.csit.servlet.ParentServlet;
import com.csit.sql.table.model.UserModel;
import java.io.IOException;
import java.util.Date;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Admin
 */
public class AddComment extends ParentServlet {

   protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            CommentModel comment = new CommentModel(request);
            UserModel user = (UserModel)request.getSession().getAttribute(com.csit.servlet.Attribute.CURRENT_USER);
            comment.setUserId(user.getUserId());
           // comment.setUserId(1);            
            comment.setCommentDate(new Date());
            comment.save();
            Message.setMessage("COMMENT HAS BEEN ADDED SUCCESSFULLY");
            RequestDispatcher dis = getServletContext().getRequestDispatcher(JspPage.ALERT_MESSAGE);
            dis.forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute(Attribute.ERROR, new ErrorColl(this,e));
            getServletContext().getRequestDispatcher(JspPage.ALERT_ERROR).forward(request, response);
        }
    }
}
