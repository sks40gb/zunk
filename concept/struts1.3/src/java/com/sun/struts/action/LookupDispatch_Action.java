
package com.sun.struts.action;

import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.actions.LookupDispatchAction;

/**
 *
 * @author Sunil
 */
public class LookupDispatch_Action extends LookupDispatchAction{

    public Map getKeyMethodMap(){
        Map map = new HashMap();
        map.put("action.lookup.create","create");
        map.put("action.lookup.display","display");
        return map;
    }
    
    public ActionForward create(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res){
        return mapping.findForward("success");
    }
    
    public ActionForward display(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res){
        return mapping.findForward("success");
    }
    
}
