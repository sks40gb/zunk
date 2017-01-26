package com.elearn.ui.topic;

//Proceed to delete course topic 
import com.elearn.db.connection.ElearnConnection;
import javax.servlet.http.*;
import javax.servlet.*;
import java.io.*;
import java.sql.*;

public class DeleteTopics extends HttpServlet {

    public Connection con;
    PreparedStatement ps1, ps2;
    PrintWriter pw;
    String courseid, course;

    public void init() {
        try {
            con = ElearnConnection.getConnection();
            ps1 = con.prepareStatement("select course from course where course_id=?");
            ps2 = con.prepareStatement("select topicid,course_topic from topic_details where course_id=?");
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
                pw.print("<img src='images/backbutton1.bmp'onClick=history.back()>");
                pw.print("<br><br></td></tr></table></center></body>");
                return;
            }
            ps1.setString(1, courseid);
            ResultSet rs1 = ps1.executeQuery();
            while (rs1.next()) {
                course = rs1.getString(1);     //select course from course
            }

            ps2.setString(1, courseid);
            ResultSet rs2 = ps2.executeQuery(); //select topics from topic details

            pw.print("<head><title>Add Topics </title>");
            pw.print("<meta http-equiv='Content-Type' content='text/html; charset=iso-8859-1'>");
            pw.print("</head><body bgcolor=#C1C1DD>");
            pw.print("<OBJECT classid='clsid:D27CDB6E-AE6D-11cf-96B8-444553540000' codebase='http://download.macromedia.com/pub/shockwave/cabs/flash/swflash.cab#version=5,0,0,0'");
            pw.print("WIDTH='752' HEIGHT='50'>");
            pw.print("<PARAM NAME=movie VALUE='css/starmessage1.swf'> <PARAM NAME=quality VALUE='Best'> ");
            pw.print("<PARAM NAME=wmode VALUE='opaque'><PARAM NAME=bgcolor VALUE='#000000'>");
            pw.print("<EMBED src='css/starmessage1.swf' quality='Best' wmode='opaque' bgcolor='#000000'");
            pw.print("WIDTH='700' HEIGHT='50' TYPE='application/x-shockwave-flash'");
            pw.print("PLUGINSPAGE='http://www.macromedia.com/shockwave/download/index.cgi?P1_Prod_Version=ShockwaveFlash'></EMBED></OBJECT><hr>");

            pw.print("<table width=100% background='images/AdministratorBackground.jpg' height=79% >");
            pw.print("<td valign=top width=100%>");
            pw.print("<form action=ProceedToDeleteTopics method=post name=form1><br><br>");
            pw.print("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
            pw.print("&nbsp;&nbsp;&nbsp;<font color=#D7DFFF size=5>Course <font color=red>" + course + "<br>");
            pw.print("<table width=80% bgcolor=#C1C1DD border=6 align=center valign=top>");
            pw.print("<thead><tr><th></th><th>Topic Id</th><th>Topics</th></tr></thead><tbody>");

            while (rs2.next()) {
                String topicid = rs2.getString(1);
                pw.print("<tr><td><input type=checkbox name=C value=" + topicid + "></td>");
                pw.print("<td>" + topicid + "</td>");
                pw.print("<td>" + rs2.getString(2) + "</td>");
                pw.print("</tr>");
            }

            pw.print("</tbody></table><br><br><center>");
            pw.print("<input type=image src=images/delete.jpg><img src=images/reset.jpg onClick='window.form1.reset()'>");
            pw.print("<img src=images/myhome.jpg onClick=window.location.href='Home.jsp'>");
            pw.print("</table></form></center><br><br></form></body>	");
        } catch (Exception e) {
            pw.print("<body bgcolor=#B9CCEE><br><br><br><br>");
            pw.print("<center><table border=10 width=90%><tr><td  height=40% bgcolor=#9185C9 width=90%>");
            pw.print("<center><h2><font color=#CCFFFF><br>course id <font color=red>" + courseid + "  </font>does not exist");
            pw.print("</font><br></font></h1><center><input type=button onClick=history.back() value=back>");
            pw.print("<br></td></tr></table></center></body>");
        //e.printStackTrace(out);
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
	