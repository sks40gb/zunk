package com.elearn.ui.topic;


//Proceed To Delete Topics
import com.elearn.db.connection.ElearnConnection;
import javax.servlet.http.*;
import javax.servlet.*;
import java.io.*;
import java.sql.*;

public class ProceedToDeleteTopics extends HttpServlet {

    public Connection con;
    PreparedStatement ps1;
    PrintWriter pw;
    String courseid;

    public void init() {
        try {
            con = ElearnConnection.getConnection();
            ps1 = con.prepareStatement("delete from topic_details where topicid=?");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        try {
            res.setContentType("text/html");
            pw = res.getWriter();

            String C[] = req.getParameterValues("C");
            for (int i = 0; i < (C.length); i++) {
                ps1.setString(1, C[i]);
                int j = ps1.executeUpdate();				 //delete from course
            }
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
            pw.print("height=50><font color=#333399><h2>Topics Deleted Successfully</td>");
            pw.print("</table></td><td width=10%></td><tr><td><td align=center valign=top>");
            pw.print("<img src='images/back1.jpg' onClick=history.back()>");
            pw.print("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
            pw.print("<img src='images/home1.jpg' onClick=window.location.href='Home.jsp'>");
            pw.print("</table><br><br></form></body>");
        } catch (Exception e) {
            pw.print("<body bgcolor=#B9CCEE><br><br><br><br>");
            pw.print("<link rel='stylesheet' href='css/css_input.css'>");
            pw.print("<center><table border=10 width=90%><tr><td  height=40% bgcolor=#9185C9 width=90%>");
            pw.print("<center><h2><font color=#CCFFFF><br>Select appropriate Topics to delete");
            pw.print("</font><br></font></h1><center><input type=button class='but' onClick=history.back() value=back>");
            pw.print("<br></td></tr></table></center></body>");
        //e.printStackTrace(out);
        }
    }  //doPost */
} //main
	