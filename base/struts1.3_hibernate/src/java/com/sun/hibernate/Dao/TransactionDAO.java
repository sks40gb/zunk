/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sun.hibernate.Dao;

import com.sun.hibernate.domain.Domain;
import com.sun.hibernate.domain.Transaction;
import java.util.List;
import org.hibernate.Query;

/**
 *
 * @author shanmugam
 */
public class TransactionDAO extends BaseHibernateDAO {
    public static final String TRANSACTION_ID = "transactionId";
    public static final String TRANSACTION_DATE = "transactionDate";
    public static final String TRANSACTION_AMT = "transactionAmt";
    public static final String REFERENCE_NUMBER = "referenceNumber";
    
    UserDAO userDAO = new UserDAO();
   
    public Integer findSizeUserProperty(String propertyName, Integer value, Class<? extends Domain> domainClass) {
        try {
            String queryString = "select COUNT(*) from " + domainClass.getSimpleName() + " as model where model." + propertyName + "= ?";
            Query queryObject = getSession().createQuery(queryString);
            queryObject.setParameter(0, value);
            Object totalSize = queryObject.uniqueResult();
            return null!=totalSize?Integer.parseInt(totalSize.toString()):0;
        } catch (RuntimeException re) {
            throw re;
        }
    }
    public List<Transaction> findByUserProperty(String propertyName, Integer value, Class<? extends Domain> domainClass,Integer pageNo,Integer pageSize) {
        try {
            String queryString = "from " + domainClass.getSimpleName() + " as model where model." + propertyName + "= ?";
            Query queryObject = getSession().createQuery(queryString);
            queryObject.setParameter(0, value);
            queryObject.setFirstResult(pageSize*(pageNo-1)).setMaxResults(pageSize);
            return queryObject.list();
        } catch (RuntimeException re) {
            throw re;
        }
    }
   
    public List<Transaction> findBySearchValue(String id, String searchValue,Integer pageNo,Integer pageSize) {
        try {
           String[] properties = {TRANSACTION_ID ,TRANSACTION_DATE ,TRANSACTION_AMT ,REFERENCE_NUMBER};
           StringBuilder queryBuilder = new StringBuilder("from Transaction where user.userId = '"+id+"' and");
           boolean isFirst = true;
           queryBuilder.append(" (");
            for (String property : properties) {
               if(isFirst){
                    queryBuilder.append(property+" like '%"+searchValue+"%'");
                    isFirst = false;
                }else{
                    queryBuilder.append(" or "+property+" like '%"+searchValue+"%'");
                }
            }
            queryBuilder.append(" )");
            Query queryObject = getSession().createQuery(queryBuilder.toString());
            queryObject.setFirstResult(pageSize*(pageNo-1)).setMaxResults(pageSize);
            return queryObject.list();
        } catch (RuntimeException re) {
           throw re;
        }
    }   
}
