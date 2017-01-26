package com.hibernate.dao;

import com.hibernate.base.BaseHibernateDAO;
import com.hibernate.domain.ex.inheritance.User;
import java.util.List;
import org.hibernate.Criteria;
import org.hibernate.Hibernate;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.Order;
import org.hibernate.transform.Transformers;

/**
 *
 * @author sunil
 */
public class UserDAO extends BaseHibernateDAO<User> {

    public UserDAO() {
        super(User.class);
    }

    public List<User> getAllUser() {
        SQLQuery query = getCurrentSession().createSQLQuery("SELECT user_name as userName from User");
        query.addScalar("userName", Hibernate.STRING);
        query.setResultTransformer(Transformers.aliasToBean(User.class));
        return query.list();
    }

     public List<User> getAllUserOrderByUserName() {
        Criteria criteria = getCurrentSession().createCriteria(User.class);
        criteria.addOrder(Order.asc("userName"));
        return criteria.list();
    }

}
