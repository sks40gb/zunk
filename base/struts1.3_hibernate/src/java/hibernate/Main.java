/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package hibernate;

import com.sun.hibernate.Dao.LogicalOperator;
import com.sun.hibernate.Dao.QueryOperator;
import com.sun.hibernate.Dao.UserDAO;
import com.sun.hibernate.HibernateSessionFactory;
import com.sun.hibernate.domain.User;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("userId", 1);        
        List<User> users = userDAO.find(map, User.class, QueryOperator.EQUAL, LogicalOperator.OR);
        for(User user : users){
            System.out.println("ID : " + user.toString());
        }      
        HibernateSessionFactory.closeSession();

    }

}
