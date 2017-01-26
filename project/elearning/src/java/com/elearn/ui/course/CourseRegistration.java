package com.elearn.ui.course;

//Examination Registration
import com.elearn.db.connection.ElearnConnection;
import com.elearn.util.DateFormat;
import javax.servlet.http.*;
import javax.servlet.*;
import java.io.*;
import java.sql.*;

public class CourseRegistration extends HttpServlet {

    public Connection con;
    PreparedStatement ps1, ps2, ps3, ps4, ps5, ps6;
    PrintWriter pw;
    String userid;

    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        try {
            res.setContentType("text/html");
            pw = res.getWriter();
            con = ElearnConnection.getConnection();

            String courseid = req.getParameter("courseid");
            HttpSession ses = req.getSession();
            userid = (String) ses.getAttribute("ID");
            ps1 = con.prepareStatement("select * from student_record where std_id=?");
            ps1.setString(1, userid);
            ResultSet rs1 = ps1.executeQuery();
            if (rs1.next()) {
                ps4 = con.prepareStatement("update student_record set std_reg_date=?,std_course_id=?,std_exam_status=? where std_id=?");
                ps4.setString(1, DateFormat.getCurrentDate());
                ps4.setString(2, courseid);
                ps4.setString(3, "yes");
                ps4.setString(4, userid);
                int x = ps4.executeUpdate();
            } else {
                ps5 = con.prepareStatement("insert into student_record (std_id,std_reg_date,std_course_id,std_exam_status) values(?,?,?,?)");
                ps5.setString(1, userid);
                ps5.setString(2, DateFormat.getCurrentDate());
                ps5.setString(3, courseid);
                ps5.setString(4, "yes");
                int x = ps5.executeUpdate();
            }

            ps6 = con.prepareStatement("delete from std_result_info where std_id=?");
            ps6.setString(1, userid);
            int x = ps6.executeUpdate();
            con.close();

            pw.print("<body bgcolor=#B9CCEE><br><br><br><br>");
            pw.print("<center><table border=10% width=90% height=20><tr><td  height=15% bgcolor=#9185C9 width=90%>");
            pw.print("<h1><font color=#CCFFFF><center>");
            pw.print("<br></font> <font color=white><h2>You have Registered Successfully</font>");
            pw.print("<font color=#CCFFFF><br>");
            pw.print("</font><br></font></h1><center>");
            pw.print("<img src='images/back1.jpg' onClick='history.back()'>");
            pw.print("<img src='images/home1.jpg' onClick=window.location='Home.jsp'><br></td>");
            pw.print("<br><br></td></tr></table></center></body>");

        } catch (Exception e) {
            pw.print("<body bgcolor=#B9CCEE><br><br><br><br>");
            pw.print("<center><table border=10 width=90%><tr><td  height=40% bgcolor=#9185C9 width=90%>");
            pw.print("<center><h2><font color=#CCFFFF><br>Required inputs are not in proper format ");
            pw.print("</font><br></font></h1><center><input type=button onClick=history.back() value=back>");
            pw.print("<br></td></tr></table></center></body>");

        }
    }  //doPost */
} //main
	