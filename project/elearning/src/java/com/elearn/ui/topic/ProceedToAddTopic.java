package com.elearn.ui.topic;

//Proceed AddCourse.jsp and AddTopic.java
import com.elearn.db.connection.ElearnConnection;
import com.elearn.util.DateFormat;
import javax.servlet.http.*;
import javax.servlet.*;
import java.io.*;
import java.sql.*;

public class ProceedToAddTopic extends HttpServlet {

    public Connection con;
    PreparedStatement ps1,ps2;
    PrintWriter pw;
    String topic, path, Ldd, Lmm, Lyyyy, duration, courseid;
    String livedate, temp;

    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        try {

            con = ElearnConnection.getConnection();
            ps1 = con.prepareStatement("insert into topic_details(" +
                    "topicid," +
                    "course_id," +
                    "course_topic," +
                    "path," +
                    "live_talk_date," +
                    "topic_duration" +
                    ") values(?,?,?,?,?,?)");

            res.setContentType("text/html");
            pw = res.getWriter();
            boolean b = readValues(req);
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

            HttpSession ses = req.getSession();
            courseid = (String) ses.getAttribute("courseid");
            
            int topicid = -1;
            ps2 = con.prepareStatement("select topicid from topic_details");
            ResultSet rs2 = ps2.executeQuery();
            while(rs2.next()) {
                topicid = rs2.getInt(1);
            }
            topicid++;
            
            ps1.setInt(1, topicid);
            ps1.setString(2, courseid);
            ps1.setString(3, topic);
            ps1.setString(4, path);
            ps1.setString(5, livedate);
            ps1.setString(6, duration);

            int i = ps1.executeUpdate();				 //insert into course

            String NextPage = req.getParameter("btn");
            if (NextPage.equals("AddMoreTopic")) {
                res.sendRedirect("AddMoreTopic.jsp");
            } else {
                res.sendRedirect("AddTopicResult.jsp");
            }
        } catch (Exception e) {
            pw.print(e);
            pw.print("<body bgcolor=#B9CCEE><br><br><br><br>");
            pw.print("<center><table border=10 width=90%><tr><td  height=40% bgcolor=#9185C9 width=90%>");
            pw.print("<center><h2><font color=#CCFFFF><br>required inputs are not in proper format ");
            pw.print("</font><br></font></h1><center><input type=button onClick=history.back() value=back>");
            pw.print("<br></td></tr></table></center></body>");
        //e.printStackTrace(out);
        }
    }  //doPost */

    public boolean readValues(HttpServletRequest req) throws Exception {

        topic = req.getParameter("topic");
        path = req.getParameter("path");
        Ldd = req.getParameter("Ldd");
        Lmm = req.getParameter("Lmm");
        Lyyyy = req.getParameter("Lyyyy");
        livedate = DateFormat.getConvertedDate(Ldd, Lmm, Lyyyy);
        duration = req.getParameter("duration");

        if ((topic == null) || topic.equals("") || (duration == null) || duration.equals("") || (Ldd == null) || Ldd.equals("") || (Lmm == null) || Lmm.equals("") || (Lyyyy == null) || Lyyyy.equals("")) {
            return false;
        } else {
            return true;
        }
    }
} //main
	