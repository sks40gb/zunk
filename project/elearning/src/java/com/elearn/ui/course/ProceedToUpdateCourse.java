package com.elearn.ui.course;

// ProceedToUpdateCourse
import com.elearn.db.connection.ElearnConnection;
import com.elearn.util.DateFormat;
import javax.servlet.http.*;
import javax.servlet.*;
import java.io.*;
import java.sql.*;

public class ProceedToUpdateCourse extends HttpServlet {

    public Connection con;
    PreparedStatement ps1;
    PrintWriter pw;
    String course, courseid, coursespec, Sdd, Smm, Syyyy, Fdd, Fmm, Fyyyy, duration;
    String startdate, finishdate;
    int count = 0;

    public void init() {
        try {
            con = ElearnConnection.getConnection();
            ps1 = con.prepareStatement("update course set course_start_date=?,course_finish_date=?, course_duration=?,course_spec=? where course_id=?");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        try {
            res.setContentType("text/html");
            pw = res.getWriter();
            boolean b = readValues(req);
            if (true)//(count==0)
            {
                if (!b) {
                    //pw.println("<h3><font color=blue>Required Inputs are Insufficient </font></h3><br><form><input type=button value='Back' onClick='history.back()'></form>");
                    pw.print("<body bgcolor=#B9CCEE><br><br><br><br>");
                    pw.print("<center><table border=10% width=90% height=20><tr><td  height=20% bgcolor=#9185C9 width=90%>");
                    pw.print("<h1><font color=#CCFFFF><center><br>");
                    pw.print("<br></font> <font color=white><h2>Required Inputs are Insufficient </font>");
                    pw.print("<font color=#CCFFFF><br>");
                    pw.print("</font><br></font></h1><center>");
                    pw.print("<img src='images/backbutton1.bmp'onClick=history.back()>");
                    pw.print("<br><br></td></tr></table></center></body>");
                    return;
                }

                if (coursespec == null || coursespec.equals("")) {
                    coursespec = "";
                }
                ps1.setString(1, startdate);
                ps1.setString(2, finishdate);
                ps1.setString(3, duration);
                ps1.setString(4, coursespec);
                ps1.setString(5, courseid);
                int i = ps1.executeUpdate();				 //insert into course
            }
            res.sendRedirect("ProceedToUpdateCourseResult.jsp");
        } catch (Exception e) {
            pw.print("<body bgcolor=#B9CCEE><br><br><br><br>");
            pw.print("<center><table border=10 width=90%><tr><td  height=40% bgcolor=#9185C9 width=90%>");
            pw.print("<center><h2><font color=#CCFFFF><br>required inputs are not in proper format ");
            pw.print("</font><br></font></h1><center><input type=button onClick=history.back() value=back>");
            pw.print("<br></td></tr></table></center></body>");
        //e.printStackTrace(out);
        }
    }  //doPost */

    public boolean readValues(HttpServletRequest req) throws Exception {
        courseid = req.getParameter("courseid");
        coursespec = req.getParameter("coursespec");
        Sdd = req.getParameter("Sdd");
        Smm = req.getParameter("Smm");
        Syyyy = req.getParameter("Syyyy");
        startdate = DateFormat.getConvertedDate(Sdd, Smm, Syyyy);
        Fdd = req.getParameter("Fdd");
        Fmm = req.getParameter("Fmm");
        Fyyyy = req.getParameter("Fyyyy");
        finishdate = startdate = DateFormat.getConvertedDate(Fdd, Fmm, Fyyyy);
        duration = req.getParameter("duration");

        if ((Sdd == null) || Sdd.equals("") || (Smm == null) || Smm.equals("") || (Syyyy == null) || Syyyy.equals("") || (Fdd == null) || Fdd.equals("") || (Fmm == null) || Fmm.equals("") || (Fyyyy == null) || Fyyyy.equals("") || (duration == null) || duration.equals("")) {
            return false;
        } else {
            return true;
        }
    }
} //main
	