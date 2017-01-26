/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sun.struts.action;

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
        }

        return mapping.findForward("success");
    }
}
