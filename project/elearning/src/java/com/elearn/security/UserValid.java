package com.elearn.security;

//UserValid Page
import com.elearn.db.connection.ElearnConnection;
import javax.servlet.*;
import javax.servlet.http.*;
import java.io.*;
import java.sql.*;

public class UserValid extends HttpServlet {

    Connection con;
    PrintWriter pw;

    public void init() {
        try {
            con = ElearnConnection.getConnection();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        HttpSession ses = null;
        try {
            String type = "";
            res.setContentType("text/html");
            pw = res.getWriter();
            ses = req.getSession(true);
            String user_id = req.getParameter("userid");
            String password = req.getParameter("password");
            boolean check = true;
            if (user_id.equals("sunil") && password.equals("sunil")) {
                check = false;
            }

            Statement st = con.createStatement();
            Statement st1 = con.createStatement();
            ResultSet rs = st.executeQuery("select  * from user_tab where user_id =" + "'" + user_id + "'" + " and user_password = " + "'" + password + "'");
            ResultSet rs1 = st1.executeQuery("select  * from user_tab where user_id =" + "'" + user_id + "'" + " and user_password = " + "'" + password + "'");

            if (!rs.next() && check) {
                //pw.println("<html><body><center><font color=blue><br><h3>Not a Valid UserName Or Password Try with Valid User Name & Password  </h3><input type=button value=Back onClick='history.back()'></center>");
                pw.print("<body bgcolor=#B9CCEE><br><br><br><br>");
                pw.print("<center><table border=10 width=90%><tr><td  height=40% bgcolor=#9185C9 width=90%>");
                pw.print("<h2><font color=#CCFFFF><center><br>");
                pw.print("<br></font> <font color=white>Not a Valid UserName Or Password </font>");
                pw.print("<font color=#CCFFFF><br>");
                pw.print("</font><br></font></h1><center>");
                pw.print("<img src='images/backbutton1.bmp'onClick=history.back()>");
                pw.print("<br><br></td></tr></table></center></body>");
            } else {
                while (check && rs1.next()) {
                    type = rs1.getString(6);
                }
                ses.setAttribute("ID", user_id);
                ses.setAttribute("PASSWORD", password);
                ses.setAttribute("TYPE", type);
                ses.setAttribute("COUNT", "4");
                ses.setAttribute("EXAM", "1");
                res.sendRedirect("Home.jsp");		//send to Home.jsp
            }
        } catch (Exception e) {
            res.sendRedirect("login.html");
        }
    }

    public void destroy() {
        try {
            if (con != null) {
                con.close();
            }
        } catch (Exception e) {
        }
    }
}