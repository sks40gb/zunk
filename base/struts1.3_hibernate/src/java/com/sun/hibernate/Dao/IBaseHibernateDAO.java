/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sun.hibernate.Dao;

import com.sun.hibernate.HibernateSessionFactory;
import org.hibernate.Session;

/**
 *
 * @author shanmugam
 */
public class IBaseHibernateDAO {

    public Session getSession() {
        return this.getCurrentSession();
    }

    public void closeSession() {
        HibernateSessionFactory.closeSession();
    }

    public Session getCurrentSession() {
        return HibernateSessionFactory.getSessionFactory().getCurrentSession();
    }

    public Session getSessionConsoleApp() {
        return HibernateSessionFactory.getSession();
    }
}
