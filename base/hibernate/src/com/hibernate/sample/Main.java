package com.hibernate.sample;

import com.hibernate.dao.UserDAO;
import com.hibernate.base.HibernateSessionFactory;
import com.hibernate.domain.ex.inheritance.User;
import java.util.List;

/**
 *
 * @author sunil
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {        
        UserDAO userDAO = new UserDAO();
        userDAO.getCurrentSession().beginTransaction();
//        print(userDAO.getAllUser());
        print(userDAO.getAllUserOrderByUserName());

//        TwoWheeler twoWheeler = new TwoWheeler();
//        twoWheeler.setHandleType("Straight");
//        twoWheeler.setModel("FZ-S");
//        userDAO.saveOrUpdate(twoWheeler);
//
////        userDAO.getCurrentSession userDAO.getCurrentSession().beginTransaction();
//         userDAO.getCurrentSession().getTransaction().commit();
        HibernateSessionFactory.closeSession();

    }

    public static void print(List<User> users){
        for(User user : users){
            System.out.println(user.getUserName());
        }
    }

}
