package com.elearn.ui.course;

//Proceed AddCourse.jsp and AddTopic.java
import com.elearn.db.connection.ElearnConnection;
import javax.servlet.http.*;
import javax.servlet.*;
import java.io.*;
import java.sql.*;

public class DeleteCourse extends HttpServlet {

    public Connection con;
    PreparedStatement ps1;
    PrintWriter pw;
    String courseid;

    public void init() {
        try {
            con = ElearnConnection.getConnection();
            ps1 = con.prepareStatement("delete from course where course_id=?");
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
                pw.print("<body background='images/AdministratorBackground.jpg'><br><br><br><br>");
                pw.print("<center><table border=10% width=90% height=20><tr><td  height=20% bgcolor=#9185C9 width=90%>");
                pw.print("<h1><font color=#CCFFFF><center><br>");
                pw.print("<br></font> <font color=white><h2>Enter course id then press delete button</font>");
                pw.print("<font color=#CCFFFF><br>");
                pw.print("</font><br></font></h1><center>");
                pw.print("<img src='images/backbutton1.bmp'onClick='http://localhost:7001/ss/DeleteCourse.jsp'>");
                pw.print("<br><br></td></tr></table></center></body>");
                return;
            }
            ps1.setString(1, courseid);
            int i = ps1.executeUpdate();				 //delete from course

            pw.print("<head><title>Add Topics </title>");
            pw.print("<meta http-equiv='Content-Type' content='text/html; charset=iso-8859-1'>");
            pw.print("</head><body bgcolor=#C1C1DD>");
            pw.print("<OBJECT classid='clsid:D27CDB6E-AE6D-11cf-96B8-444553540000' ");
            pw.print("codebase='http://download.macromedia.com/pub/shockwave/cabs/flash/swflash.c");
            pw.print("ab#version=5,0,0,0'");
            pw.print("WIDTH='752' HEIGHT='50'>");
            pw.print("<PARAM NAME=movie VALUE='css/starmessage1.swf'> <PARAM NAME=quality ");
            pw.print("VALUE='Best'> ");
            pw.print("<PARAM NAME=wmode VALUE='opaque'><PARAM NAME=bgcolor VALUE='#000000'>");
            pw.print("<EMBED src='css/starmessage1.swf' quality='Best' wmode='opaque'");
            pw.print("bgcolor='#000000'");
            pw.print("WIDTH='700' HEIGHT='50' TYPE='application/x-shockwave-flash' ");
            pw.print("PLUGINSPAGE='http://www.macromedia.com/shockwave/download/index.cgi?P1_Prod");
            pw.print("_Version=ShockwaveFlash'></EMBED></OBJECT><hr>");
            pw.print("<form action=SessionInavalidate method=post>");
            pw.print("<table width=100% background='images/AdministratorBackground.jpg' ");
            pw.print("height=73%>");
            pw.print("<td width=10%></td><td>");
            pw.print("<table width=100% bgcolor=#C1C1DD border=1 align=center><td");
            pw.print("align=center><tr><td valign=middle width=100% align=center ");
            pw.print("height=50><font color=#333399><h2>Course Deleted Successfully</td>");
            pw.print("</table></td><td width=10%></td><tr><td><td align=center valign=top>");
            pw.print("<img src='images/back1.jpg' onClick=history.back()>");
            pw.print("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
            pw.print("<img src='images/home1.jpg' onClick=window.location.href='Home.jsp'>");
            pw.print("</table><br><br></form></body>");
        } catch (Exception e) {
            pw.print(e);
            pw.print("<body bgcolor=#B9CCEE><br><br><br><br>");
            pw.print("<center><table border=10 width=90%><tr><td  height=40% bgcolor=#9185C9 width=90%>");
            pw.print("<center><h2><font color=#CCFFFF><br>course id <font color=red>" + courseid + " </font>does not exist)");
            pw.print("</font><br></font></h1><center><input type=button onClick=history.back() value=back>");
            pw.print("<br></td></tr></table></center></body>");

        }
    }  //doPost */

    public boolean readValues(HttpServletRequest req) throws Exception {

        courseid = req.getParameter("courseid");
        if ((courseid == null) || courseid.equals("")) {
            return false;
        } else {
            return true;
        }
    }
} //main
	