package com.elearn.ui.user;

//Registration Form
import com.elearn.db.connection.ElearnConnection;
import com.elearn.util.DateFormat;
import javax.servlet.http.*;
import javax.servlet.*;
import java.io.*;
import java.sql.*;

public class Registration extends HttpServlet {

    public Connection con;
    PreparedStatement ps;
    PrintWriter pw;
    String userid, userpassword, fname, mname, lname, gender, dob, email, address1, address2, city, state, country, occupation;
    String contactnumber, zipcode;

    public void init() {
        try {
            con = ElearnConnection.getConnection();
            ps = con.prepareStatement("insert into user_tab" +
                    "(user_id," +
                    "user_password," +
                    "user_first_name," +
                    "user_middle_name," +
                    "user_last_name," +
                    "user_type," +
                    "user_gender," +
                    "user_dob," +
                    "user_email," +
                    "user_address1," +
                    "user_address2," +
                    "user_city," +
                    "user_state," +
                    "user_postcode," +
                    "user_country," +
                    "user_occupation," +
                    "user_contact_number) " +
                    "values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        try {
            res.setContentType("text/html");
            pw = res.getWriter();
            boolean b = readValues(req);
            if (!b) {
                //pw.println("<h3><font color=blue>Required Inputs are Insufficient </font></h3><br><form><input type=button value='Back' onClick='history.back()'></form>");
                pw.print("<body bgcolor=#B9CCEE><br><br><br><br>");
                pw.print("<center><table border=10 width=90%><tr><td  height=40% bgcolor=#9185C9 width=90%>");
                pw.print("<h1><font color=#CCFFFF><center><br>");
                pw.print("<br></font> <font color=white>Required Inputs are Insufficient </font>");
                pw.print("<font color=#CCFFFF><br>");
                pw.print("</font><br></font></h1><center>");
                pw.print("<img src='images/backbutton1.bmp'onClick=history.back()>");
                pw.print("<br><br></td></tr></table></center></body>");
                return;
            }
            if (mname == null || mname.equals("")) {
                mname = "";
            }
            if (address2 == null || address2.equals("")) {
                address2 = "";
            }
            //Statement st=con.createStatement();
            ps.setString(1, userid);
            ps.setString(2, userpassword);
            ps.setString(3, fname);
            ps.setString(4, mname);
            ps.setString(5, lname);
            ps.setString(6, "user");
            ps.setString(7, gender);
            ps.setString(8, dob);
            ps.setString(9, email);
            ps.setString(10, address1);
            ps.setString(11, address2);
            ps.setString(12, city);
            ps.setString(13, state);
            ps.setString(14, zipcode);
            ps.setString(15, country);
            ps.setString(16, occupation);
            ps.setString(17, contactnumber);
            int i = ps.executeUpdate();
            if (i == 1) {
                pw.println("<html><head><title>Registration Successful</title></head>");
                pw.println("<body bgcolor=#E6DBFB>");
                pw.println("<link rel='stylesheet' href='css/css_input.css'>");
                pw.println("<strong><h1><center><font color=blue>E-LEARNING</font></center></h1></strong>");
                pw.println("<br><h2><font color=#660066>Registration Successful </font></h2>");
                pw.println("<font color=blue><h2> Welcome to the New Experience of E-learning");
                pw.println("</h2><h3>Your User id For Further Correspondense is <font color=red><b>" + userid + "</h3></b><br>");
                pw.println("<br><center><input type=button class='but' onClick=window.location.href='login.html' value='login'>");
                pw.println("</body></html>");
            }
        } catch (Exception e) {
            //pw.print(e);
            pw.print("<body bgcolor=#B9CCEE><br><br><br><br>");
            pw.print("<center><table border=10 width=90%><tr><td  height=40% bgcolor=#9185C9 width=90%>");
            pw.print("<h1><font color=#CCFFFF><center><br>");
            pw.print("User already exist<br></font> <font color=#FF9933>or</font>");
            pw.print("<font color=#CCFFFF><br>required inputs are not in proper format ");
            pw.print("</font><br></font></h1><center><input type=button onClick=history.back() value='back'>");
            pw.print("<br></td></tr></table></center></body>");
        //e.printStackTrace(out);
        }
    }  //doPost

    public boolean readValues(HttpServletRequest req) throws Exception {
        String dd, mm, yyyy;
        userid = req.getParameter("UserID");
        userpassword = req.getParameter("password");
        fname = req.getParameter("FirstName");
        mname = req.getParameter("MiddleName");
        lname = req.getParameter("LastName");
        gender = req.getParameter("gender");
        dd = req.getParameter("DD");
        mm = req.getParameter("MM");
        yyyy = req.getParameter("YYYY");
        dob = DateFormat.getConvertedDate(dd, mm, yyyy);
        email = req.getParameter("Email");
        address1 = req.getParameter("Address1");
        address2 = req.getParameter("Address2");
        city = req.getParameter("City");
        state = req.getParameter("State");
        zipcode = req.getParameter("Postcode");
        country = req.getParameter("Country");
        occupation = req.getParameter("Occupation");
        contactnumber = req.getParameter("ContactNumber");

        if ((userid == null) || userid.equals("") || (userpassword == null) || userpassword.equals("") || (fname == null) || fname.equals("") || (lname == null) || lname.equals("") || (gender == null) || gender.equals("") || (dob == null) || dob.equals("") || (email == null) || email.equals("") || (address1 == null) || address1.equals("") || (city == null) || city.equals("") || (state == null) || state.equals("") || (zipcode == null) || zipcode.equals("") || (country == null) || country.equals("") || (occupation == null) || occupation.equals("") || (contactnumber == null) || contactnumber.equals("")) {
            return false;
        } else {
            return true;
        }
    }
} //main
	