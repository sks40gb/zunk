package com.sun.hibernate.Dao;
// Generated Jan 10, 2010 11:27:38 AM by Hibernate Tools 3.2.1.GA

import com.sun.hibernate.domain.GenericCode;
import java.util.List;
import org.hibernate.Query;

/**
 * UserDetails generated by hbm2java
 */
public class GenericCodeDAO extends BaseHibernateDAO{
     public List<GenericCode> findByCodeTypeId(Integer id) {
       List list = null;
        try {
            String queryString = "";
            queryString = "from GenericCode where codetypeid='" + id + "'";
            Query queryObject = getSession().createQuery(queryString);
            list = queryObject.list();
            return list;
        } catch (RuntimeException re) {
            re.printStackTrace();
            throw re;
        } finally {
            closeSession();
        }
    }
}
