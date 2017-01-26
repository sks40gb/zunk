/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sun.struts.action;

import com.sun.hibernate.Dao.UserDAO;
import com.sun.hibernate.domain.User;
import com.sun.struts.form.UserRegistrationForm;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

public class UserRegistrationAction extends Action {

    public ActionForward execute(
            ActionMapping mapping,
            ActionForm form,
            HttpServletRequest request,
            HttpServletResponse response)
            throws Exception {

        if (isCancelled(request)) {
            return mapping.findForward("welcome");
        }else{
            UserRegistrationForm registrationForm = (UserRegistrationForm)form;
            User user = new User();
            user.setTitle(registrationForm.getFirstName());
            user.setRoles(null);
            new UserDAO().saveOrUpdate(user);
        }

        return mapping.findForward("success");
    }
}
