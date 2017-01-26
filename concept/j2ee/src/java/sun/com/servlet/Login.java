/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sun.com.servlet;

import java.io.PrintWriter;
import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import sun.com.entity.User;
import sun.com.list.Users;

/**
 *
 * @author sunil
 */
public class Login extends HttpServlet{

    public void doPost(HttpServletRequest request, HttpServletResponse response){
        try{
            PrintWriter out = response.getWriter(); 
            response.setContentType("text/html");

            String initConfigValue = getServletConfig().getInitParameter("info");
            String userName = request.getParameter("userName");
            String password = request.getParameter("password");

            User user = new User();
            user.setFirstName(userName);
            user.setPassword(password);

            Users.addUser(user);

            request.setAttribute("user", user);
            request.getSession().setAttribute("user", user);

            RequestDispatcher rd = getServletContext().getRequestDispatcher("/jsp/loginSuccess.jsp");
            rd.forward(request, response);

            
//            out.print("Username : " + initConfigValue); 
//            out.print("<br>");
//            out.print("Username : " + userName);
//            out.print("<br>");
//            out.print("Password : " + password);



            
        }catch(Exception e){
            e.printStackTrace();
        }
    }

}
