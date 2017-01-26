/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sun.hibernate.Dao;

import com.sun.constant.CommonConstant;
import com.sun.constant.Status;
import com.sun.hibernate.domain.Role.Roles;
import com.sun.hibernate.domain.User;
import java.util.ArrayList;
import java.util.List;
import org.hibernate.HibernateException;
import org.hibernate.Query;

/**
 *
 * @author shanmugam
 */
public class UserDAO extends BaseHibernateDAO<User> {
    public static final String STATUS = "status";
    public static final String ID = "id";
    public static final String COMPANY_NAME = "companyName";
    public static final String CITY = "city";
    public static final String STATE = "state";
    public static final String ZIP = "zip";
    public static final String FIRST_NAME = "firstName";
    public static final String LAST_NAME = "lastName";
    public static final String EMAIL = "email";

    public List findByStatus(Status status) {
        return findByProperty(STATUS, status.toString(), User.class);
    }

    public List<User> findByStatusAndRole(Status status, Roles role) throws Exception {
        List<User> searchList = new ArrayList<User>();
        StringBuilder queryBuilder = new StringBuilder(" from User where");
        if(!status.toString().equals(Status.ALL.toString())){
            queryBuilder.append(" "+STATUS+" = '"+status+"'");
        }else{
            queryBuilder.append(" ("+STATUS+" = '"+Status.PENDING.toString()+"' or "+STATUS+" = '"+Status.APPROVED.toString()+"')");
        }
        Query queryObject = getSession().createQuery(queryBuilder.toString());
        List<User> list = queryObject.list();
        for (User user : list) {
            if (user.hasRole(role)) {
                searchList.add(user);
            }
        }
        return searchList;
    }

    public List<User> findUser(String searchValue,String status) throws HibernateException{
        String[] properties = {ID, COMPANY_NAME, CITY, STATE, ZIP, FIRST_NAME, LAST_NAME, EMAIL};
        StringBuilder queryBuilder = new StringBuilder(" from User where");
        if(!status.trim().equals(CommonConstant.ALL)){
            queryBuilder.append(" "+STATUS+" = '"+status+"'");
        }else{
            queryBuilder.append(" ("+STATUS+" = '"+Status.PENDING.toString()+"' or "+STATUS+" = '"+Status.APPROVED.toString()+"')");
        }
        queryBuilder.append(" and (");
        boolean isFirst = true;
        for(String property : properties){
            if(isFirst){
                queryBuilder.append(property+" like '%"+searchValue+"%'");
                isFirst = false;
            }else{
                queryBuilder.append(" or "+property+" like '%"+searchValue+"%'");
            }
        }
        queryBuilder.append(")");
        Query queryObject = getSession().createQuery(queryBuilder.toString());
        return queryObject.list();
    }
}
