/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sun.hibernate.Dao;

import com.sun.hibernate.domain.Domain;
import com.sun.util.ClassUtil;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Example;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author Sunil
 */
public class BaseHibernateDAO<T extends Domain> extends IBaseHibernateDAO {

    private static Logger log = Logger.getLogger(BaseHibernateDAO.class);

    /**
     * Insert new records into the table.
     * @param instance
     */
    public void save(T instance) {
        log.debug("saving " + instance.getClass().getSimpleName() + " instance");
        try {
            getSession().save(instance);
            log.debug("save successful");
        } catch (Exception re) {
            log.error("save failed", re);         
        } finally {
            if (getSession() != null) {
                closeSession();
            }
        }
    }

    /**
     * Save of Update of Type Domain
     * @param instance - Instance to be updated
     */
    public void saveOrUpdate(T instance) {
        log.debug("saving " + instance.getClass().getSimpleName() + " instance");
        try {
            getCurrentSession().saveOrUpdate(instance);
            getCurrentSession().flush();
            log.debug("save successful");
        } catch (RuntimeException re) {
            log.error("save failed", re);
            throw re;
        }
    }

    /**
     * Synchronize the DB and instance Domain.
     * Update should be used to save the data when the session does not contain an
     * already persistent instance with the same identifier.
     * @param instance
     */
    public void update(T instance) {
        log.debug("updating " + instance.getClass().getSimpleName() + " instance");
        try {
            getCurrentSession().update(instance);
            getCurrentSession().flush();
            log.debug("updated successful");
        } catch (RuntimeException re) {
            log.error("update failed", re);
            throw re;
        }
    }

    /**
     * Synchronize the DB and instance Domain.
     * Merge should be used to save the modificatiions at any time without knowing about the state of a session.
     * @param instance
     */
    public void merge(T instance) {
        log.debug("merging " + instance.getClass().getSimpleName() + " instance");
        try {
            getCurrentSession().merge(instance);
            getCurrentSession().flush();
            log.debug("merge successful");
        } catch (RuntimeException re) {
            log.error("merge failed", re);
            throw re;
        }
    }

    /**
     * Remove the instance from the database
     * @param instance
     */
    public void delete(T instance) {
        log.debug("deleting " + instance.getClass().getSimpleName() + " instance");
        try {
            getSession().delete(instance);
            log.debug("delete successful");
        } catch (RuntimeException re) {
            log.error("delete failed", re);
            throw re;
        }
    }

    /**
     * Get all the records from the database.
     * @param domainClass
     * @return
     */
    public List<T> findAll(Class<? extends Domain> domainClass) {
        log.debug("finding all " + domainClass + " instance");
        try {
            Criteria criteria = getSession().createCriteria(domainClass);
            return criteria.list();
        } catch (RuntimeException re) {
            log.error("findAll is failed", re);
            throw re;
        }
    }

    /**
     * Get the List of Domain by matching a property of Domain and value.
     * @param propertyName - Property of Domain <T>
     * @param value - value
     * @param domainClass - Domain class
     * @return - List of Type Domain
     */
    public List<T> findByProperty(String propertyName, Object value, Class<? extends Domain> domainClass) {
        log.debug("finding " + domainClass + " instance with property: " + propertyName + ", value: " + value);
        try {
            Criteria criteria = getCurrentSession().createCriteria(domainClass);
            criteria.add(Restrictions.eq(propertyName, value));
            return criteria.list();
        } catch (RuntimeException re) {
            log.error("find by property name failed", re);
            throw re;
        }
    }

    /**
     * Get the List of Domain by matching a property of Domain and value.
     * @param propertyName - Property of Domain <T>
     * @param value - value
     * @param instance - Domain instance
     * @return - List of Type Domain
     */
    public List<T> findByProperty(String propertyName, Object value, T instance) {
        return findByProperty(propertyName, value, instance.getClass());
    }

    /**     
     * Get the List of Domain by matching a property of Domain and value.
     * @param properties - List of propery to be matched.
     * @param instance - Domain instance having the value for the properties.
     * @return - List of Type Domain
     * @throws NoSuchMethodException - If the propeties do not exists in the <code>instance</code>
     * @throws IllegalAccessException - If the accessor of the property is private.
     * @throws IllegalArgumentException - If the arguement doest not match.
     * @throws InvocationTargetException
     */
    public List<T> findByProperties(List<String> properties, T instance) throws NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        Map<String, Object> propertyMap = ClassUtil.getPropertyValueMap(properties, instance);
        Class<T> c = (Class<T>) instance.getClass();
        return findByProperties(propertyMap, c);
    }

    /**
     * Get the List of Domain by matching a property of Domain and value.
     * @param propertyMap - Map having the properties and values as key and value correspondingly.
     * @param domainClass - Domain Class.
     * @return - List of Type Domain
     */
    public List<T> findByProperties(Map<String, Object> propertyMap, Class<T> domainClass) {
        log.debug("find by propertes of " + domainClass + " instance");
        try {
            return find(propertyMap, domainClass, QueryOperator.EQUAL, LogicalOperator.AND);
        } catch (RuntimeException re) {
            log.error("find by propertes is failed", re);
            throw re;
        }
    }

    /**
     * Find the result
     * @param propertyMap - Map contains the property and corresponding value.
     * @param domainClass - Domain class
     * @param operator - QueryOperator LIKE, EQUAL, LT,GT etc..
     * @param lo - Logical Operator AND or OR
     * @return - List of <code>Domain</code>
     */
    public List find(Map<String, Object> propertyMap, Class<T> domainClass, QueryOperator operator, LogicalOperator lo) {
        Set<String> keys = propertyMap.keySet();
        StringBuilder buffer = new StringBuilder("FROM ");
        buffer.append(domainClass.getSimpleName());
        buffer.append(" WHERE ");
        boolean isFirst = true;
        //If the QueryOpertor is LIKE then prepand and append % to propery
        String ext = (operator == QueryOperator.LIKE) ? "%" : "";
        for (String property : keys) {
            // for accepting integer, double and other type of fields.
            String stringProperty = "str(" + property + ")";

            if (isFirst) {
                buffer.append(stringProperty + " " + operator + " :" + property);
                isFirst = false;
            } else {
                buffer.append(" " + lo.toString() + " " + stringProperty + " " + operator + " :" + property);
            }
        }
        Query queryObject = getSession().createQuery(buffer.toString());
        for (String property : keys) {
            Object value = propertyMap.get(property);
            queryObject.setParameter(property, ext + value + ext);
        }
        List list = queryObject.list();
        return (List<T>) list;
    }

    /**
     * Find the Domain object by id
     * @param id - Id
     * @param domainClass - Domain Class
     * @return - Domain object
     */
    public T findById(Integer id, Class<T> domainClass) {
        log.debug("finding " + domainClass.getSimpleName() + " instance by id " + id);
        T t = null;
        try {
            Object o = getSession().get(domainClass.getName(), id);
            if (o != null) {
                t = (T) o;
            }
            return t;
        } catch (RuntimeException re) {
            log.error("find by id failed", re);
            throw re;
        } finally {
            closeSession();
        }
    }

    /**
     * Find by Instance
     * @param domain - Domain instance
     * @param enableLike - Enable Like operator
     * @param ignoreCase - Case insensitive
     * @return - List of type Domain
     */
    public List<T> findByInstance(T domain, boolean enableLike, boolean ignoreCase) {
        Session session = getCurrentSession();
        Criteria criteria = session.createCriteria(domain.getClass());
        Example example = Example.create(domain);
        if (enableLike) {
            example.enableLike();
        }
        if (ignoreCase) {
            example.ignoreCase();
        }
        example.excludeZeroes();
        criteria.add(example);
        return criteria.list();
    }

    /**
     * Find by Instance
     * @param domain - Domain instance
     * @return - List of type Domain
     */
    public List<T> findByInstance(T domain) {
        return findByInstance(domain, false, false);
    }

    /**
     * Find by Instance
     * @param domain - Domain instance
     * @param enableLike - Enable Like operator
     * @return - List of type Domain
     */
    public List<T> findByInstance(T domain, boolean enableLike) {
        return findByInstance(domain, false, false);
    }

    /**     
     * Search by the property and value
     * @param propertyName - Porpery name of Domian
     * @param value - value for the property
     * @param domainClass - Domain class 
     * @return - List of Type T
     */
    public List<T> searchByProperty(String propertyName, String value, Class<? extends Domain> domainClass) {
        log.debug("finding " + domainClass + " instance with property: " + propertyName + ", value: " + value);
        try {
            Criteria criteria = getCurrentSession().createCriteria(domainClass);
            criteria.add(Restrictions.like(propertyName, value + "%"));
            return criteria.list();
        } catch (RuntimeException re) {
            log.error("find by property name failed", re);
            throw re;
        }
    }

    /**
     * Search by the property and value
     * @param propertyName - Porpery name of Domian
     * @param value - value for the property
     * @param instance - Instance of Domain
     * @return - List of Type T
     */
    public List<T> searchByProperty(String propertyName, String value, T instance) {
        return findByProperty(propertyName, value, instance.getClass());
    }

    /**
     * Search by properties of Domain
     * @param properties - List of properties
     * @param instance - get the properties value from.
     * @return - List of Type Domain
     * @throws NoSuchMethodException - If the property mentioned the list <code> properties </code> does not match with
     * the property of instance
     * @throws IllegalAccessException - if the propery of instance is private.
     * @throws IllegalArgumentException - if the arguement type doest not match.
     * @throws InvocationTargetException
     */
    public List<T> searchByProperties(List<String> properties, T instance) throws NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        Map<String, Object> propertyMap = ClassUtil.getPropertyValueMap(properties, instance);
        Class<T> c = (Class<T>) instance.getClass();
        return findByProperties(propertyMap, c);
    }

    /**
     * Search by properties of Domain
     * @param propertyMap - Contains the key as property and value as property value.
     * @param domainClass - Domain class
     * @return - List of Domain of Type T     
     */
    public List<T> searchByProperties(Map<String, Object> propertyMap, Class<T> domainClass) {
        return find(propertyMap, domainClass, QueryOperator.LIKE, LogicalOperator.OR);
    }

    /**
     * Execute the hibernate query which returns List of type T
     * @param queryString - Query to be executed
     * @return - List of Domain
     */
    public List<T> executeQuery(String queryString) {
        log.debug("query : " + queryString);
        try {
            Query query = getCurrentSession().createQuery(queryString);
            List list = query.list();
            if (list != null) {
                return (List<T>) list;
            } else {
                return null;
            }
        } catch (RuntimeException re) {
            log.error("query is failed", re);
            throw re;
        }
    }

    /**
     * Execute the hibernate query which returns single instance of type T
     * @param queryString - Query to be executed.
     * @return
     */
    public T executeUniqueQuery(String queryString) {
        log.debug("query : " + queryString);
        try {
            Query query = getCurrentSession().createQuery(queryString);
            Object o = query.uniqueResult();
            if (o != null) {
                return (T) o;
            } else {
                return null;
            }
        } catch (RuntimeException re) {
            log.error("query is failed", re);
            throw re;
        }
    }
}
