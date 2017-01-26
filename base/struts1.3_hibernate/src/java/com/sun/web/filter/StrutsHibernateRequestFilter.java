/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sun.web.filter;

import com.sun.hibernate.Dao.IBaseHibernateDAO;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.hibernate.StaleObjectStateException;
import org.apache.struts.action.ActionServlet;

/**
 *
 * @author Administrator
 */
public class StrutsHibernateRequestFilter extends ActionServlet {

    IBaseHibernateDAO hibernateDAO = new IBaseHibernateDAO();

    @Override
    protected void process(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        try {
            hibernateDAO.getCurrentSession().beginTransaction();
            // Call the next filter (continue request processing)
            //chain.doFilter(request, response);
            super.process(request, response);
            // Commit and cleanup
            hibernateDAO.getCurrentSession().getTransaction().commit();
        } catch (StaleObjectStateException staleEx) {
            System.out.println("INSDIE CATCH ERROR STATE OBJECT STATE EXCEPTION");
            // Rollback, close everything, possibly compensate for any permanent changes
            // during the conversation, and finally restart business conversation. Maybe
            // give the user of the application a chance to merge some of his work with
            // fresh data... what you do here depends on your applications design.
            throw staleEx;
        } catch (Throwable ex) {
            System.out.println("INSDIE CATCH ERROR Throwable " + ex);
            // Rollback only
            ex.printStackTrace();
            try {
                if (hibernateDAO.getCurrentSession().getTransaction().isActive()) {
                    System.out.println("Trying to rollback database transaction after exception");
                    hibernateDAO.getCurrentSession().getTransaction().rollback();
                }
            } catch (Throwable rbEx) {
                System.err.println("Could not rollback transaction after exception! " + rbEx);
            }
            throw new ServletException(ex);
        }
    }
}
