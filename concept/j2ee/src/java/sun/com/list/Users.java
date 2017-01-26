/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sun.com.list;

import java.util.ArrayList;
import java.util.List;
import sun.com.entity.User;

/**
 *
 * @author sunil
 */
public class Users {

    public static void main(String[] args) {
        double a = 81;
        double b = 20;
        System.out.println("--> " + b/a);

    }
    private static List<User> users;

    public static List<User> getUsers() {
        return users;
    }

    public static void setUsers(List<User> users) {
        Users.users = users;
    }

    public static void addUser(User user){
        if(users == null){
            users = new ArrayList<User>();
        }
        users.add(user);
    }
    
}
