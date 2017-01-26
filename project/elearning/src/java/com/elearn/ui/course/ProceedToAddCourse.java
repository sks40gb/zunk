package com.elearn.ui.course;

//Proceed AddCourse.jsp and AddTopic.java
import com.elearn.db.connection.ElearnConnection;
import com.elearn.util.DateFormat;
import javax.servlet.http.*;
import javax.servlet.*;
import java.io.*;
import java.sql.*;

public class ProceedToAddCourse extends HttpServlet {

    public Connection con;
    PreparedStatement ps1, ps2;
    PrintWriter pw;
    String coursecategory, course, courseid, coursespec, Sdd, Smm, Syyyy, Fdd, Fmm, Fyyyy, duration;
    String startdate, finishdate;
    int count = 0;

    public void init() {
        try {
            con = ElearnConnection.getConnection();
            ps1 = con.prepareStatement("select catid from course_category where category=?");
            ps2 = con.prepareStatement("insert into course (course_id,course,course_start_date,course_finish_date,course_duration,course_spec,catid) values(?,?,?,?,?,?,?)");
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

                //Statement st=con.createStatement();
                ps1.setString(1, coursecategory);		 //select from course_category
                ResultSet rs = ps1.executeQuery();

                ps2.setString(1, courseid);
                ps2.setString(2, course);
                ps2.setString(3, startdate);
                ps2.setString(4, finishdate);
                ps2.setString(5, duration);
                ps2.setString(6, coursespec);

                HttpSession ses = req.getSession();
                ses.setAttribute("courseid", courseid);

                String catid = "";
                while (rs.next()) {
                    catid = rs.getString(1);
                }
                ps2.setString(7, catid);
                //ps2.setString(8,"4");
                int i = ps2.executeUpdate();				 //insert into course
            }

            pw.print("<head><title>Add Topics </title>");
            pw.print("<meta http-equiv='Content-Type' content='text/html; charset=iso-8859-1'>");
            pw.print("</head><body bgcolor=#C1C1DD>");
            pw.print("<OBJECT classid='clsid:D27CDB6E-AE6D-11cf-96B8-444553540000' codebase='http://download.macromedia.com/pub/shockwave/cabs/flash/swflash.cab#version=5,0,0,0' WIDTH='752' HEIGHT='50'>");
            pw.print("<PARAM NAME=movie VALUE='css/starmessage1.swf'> <PARAM NAME=quality VALUE='Best'> ");
            pw.print("<PARAM NAME=wmode VALUE='opaque'><PARAM NAME=bgcolor VALUE='#000000'>");
            pw.print("<EMBED src='css/starmessage1.swf' quality='Best' wmode='opaque' bgcolor='#000000'");
            pw.print("WIDTH='700' HEIGHT='50' TYPE='application/x-shockwave-flash' ");
            pw.print("PLUGINSPAGE='http://www.macromedia.com/shockwave/download/index.cgi?P1_Prod_Version=ShockwaveFlash'></EMBED></OBJECT><hr>");
            pw.print("<link rel='stylesheet' href='css/css_input.css'><table width=100% background='images/AdministratorBackground.jpg' height=100%>");
            pw.print("<td width=10%></td><td>");
            pw.print("<form action=ProceedToAddTopic method=post >");
            pw.print("<center><h2><u><font color=#CCCCFF>Add Topic Details</u></h1>	");
            pw.print("<table width=80% bgcolor=#C1C1DD border=6><td align=center><tr><td>Topic</td><td><input type=text name=topic size=30 name=topic></td></tr>");
            pw.print("<tr><td>Path</td><td><input type=text name=path size=30 name=topic></td></tr>");
            pw.print("<tr><td>Live Talk Date</td><td>");

            pw.print("<select name='Ldd'>");
            pw.print("<option value='01'>01</option><option value='02'>02</option><option ");
            pw.print("value='03'>03</option><option value='04'>04</option><option ");
            pw.print("value='05'>05</option><option value='06'>06</option><option ");
            pw.print("value='07'>07</option><option value='08'>08</option><option ");
            pw.print("value='09'>09</option><option value='10'>10</option><option");
            pw.print("value='11'>11</option><option value='12'>12</option><option");
            pw.print("value='13'>13</option><option value='14'>14</option><option");
            pw.print("value='15'>15</option><option value='16'>16</option><option");
            pw.print("value='17'>17</option><option value='18'>18</option><option");
            pw.print("value='19'>19</option><option value='20'>20</option><option");
            pw.print("value='21'>21</option><option value='22'>22</option><option");
            pw.print("value='23'>23</option><option value='24'>24</option><option");
            pw.print("value='25'>25</option><option value='26'>26</option><option");
            pw.print("value='27'>27</option><option value='28'>28</option><option");
            pw.print("value='29'>29</option><option value='30'>30</option>");
            pw.print("<option value='31'>31</option></select>");


            pw.print("<select name='Lmm'>");
            pw.print("<option value='01'>Jan</option><option value='02'>Feb</option><option ");
            pw.print("value='03'>Mar</option><option value='04'>Apr</option><option ");
            pw.print("value='05'>May</option><option value='06'>Jun</option><option ");
            pw.print("value='07'>Jul</option><option value='08'>Aug</option><option ");
            pw.print("value='09'>Sep</option><option value='10'>Oct</option><option ");
            pw.print("value='11'>Nov</option><option value='12'>Dec</option></select>");


            pw.print("<select name='Lyyyy'>");
            pw.print("	<option value='1957'>1957</option><option ");
            pw.print("value='1958'>1958</option><option value='1959'>1959</option><option ");
            pw.print("value='1960'>1960</option><option value='1961'>1961</option><option ");
            pw.print("value='1962'>1962</option><option value='1963'>1963</option><option ");
            pw.print("value='1964'>1964</option><option value='1965'>1965</option><option ");
            pw.print("value='1966'>1966</option><option value='1967'>1967</option><option ");
            pw.print("value='1968'>1968</option><option value='1969'>1969</option><option ");
            pw.print("value='1970'>1970</option><option value='1971'>1971</option><option ");
            pw.print("value='1972'>1972</option><option value='1973'>1973</option><option ");
            pw.print("value='1974'>1974</option><option value='1975'>1975</option><option ");
            pw.print("value='1976'>1976</option><option value='1977'>1977</option><option ");
            pw.print("value='1978'>1978</option><option value='1979'>1979</option><option ");
            pw.print("value='1980'>1980</option><option value='1981'>1981</option><option ");
            pw.print("value='1982'>1982</option><option value='1983'>1983</option><option ");
            pw.print("value='1984'>1984</option><option value='1985'>1985</option><option ");
            pw.print("value='1986'>1986</option><option value='1987'>1987</option><option ");
            pw.print("value='1988'>1988</option><option value='1989'>1989</option><option ");
            pw.print("value='1990'>1990</option><option value='1991'>1991</option><option ");
            pw.print("value='1992'>1992</option><option value='1993'>1993</option><option ");
            pw.print("value='1994'>1994</option><option value='1995'>1995</option><option ");
            pw.print("value='1996'>1996</option><option value='1997'>1997</option><option ");
            pw.print("value='1998'>1998</option><option value='1999'>1999</option><option ");
            pw.print("value='2000'>2000</option><option value='2001'>2001</option><option ");
            pw.print("value='2002'>2002</option><option value='2003'>2003</option><option ");
            pw.print("value='2004'>2004</option><option value='2005'>2005</option><option ");
            pw.print("value='2006'>2006</option><option value='2007'>2007</option><option ");
            pw.print("value='2008'>2008</option></select>");


            pw.print("</td></tr><tr><td>Duration</td><td><input type=text size=4 name=duration>&nbsp;days</td></tr>");
            pw.print("</table></td><td width=10%></td><tr><td><td align=center>");
            //pw.print("<input type='image' src='images/addtopic.bmp' name=btn value='addtopic'>");
            //pw.print("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
            pw.print("<input type=submit name=btn class='but' value='AddTopic'>");
            pw.print("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
            pw.print("<input type=submit class='but' name=btn value='AddMoreTopic'>");
            //pw.print("<input type='image' src='images/add_more_topic.bmp' name=btn value='moretopic'>	");
            pw.print("</table><br><br></form></body></form>");
            pw.print("</body>");
            count++;
        } catch (Exception e) {
            pw.print(e);
            pw.print("<body bgcolor=#B9CCEE><br><br><br><br><link rel='stylesheet' href='css/css_input.css'>");
            pw.print("<center><table border=10 width=90%><tr><td  height=40% bgcolor=#9185C9 width=90%>");
            pw.print("<center><h2><font color=#CCFFFF><br>required inputs are not in proper format<br>or<br> ");
            pw.print("course already exist");
            pw.print("</font><br></font></h1><center><input type=button onClick=history.back() class='but' value=back>");
            pw.print("<br></td></tr></table></center></body>");
        //e.printStackTrace(out);
        }
    }  //doPost */

    public boolean readValues(HttpServletRequest req) throws Exception {
        coursecategory = req.getParameter("coursecategory");
        course = req.getParameter("course");
        courseid = req.getParameter("courseid");
        coursespec = req.getParameter("coursespec");
        Sdd = req.getParameter("Sdd");
        Smm = req.getParameter("Smm");
        Syyyy = req.getParameter("Syyyy");
        startdate = DateFormat.getConvertedDate(Sdd, Smm, Syyyy);
        Fdd = req.getParameter("Fdd");
        Fmm = req.getParameter("Fmm");
        Fyyyy = req.getParameter("Fyyyy");
        finishdate = DateFormat.getConvertedDate(Fdd, Fmm, Fyyyy);
        duration = req.getParameter("duration");

        if ((coursecategory == null) || coursecategory.equals("") || (course == null) || course.equals("") || (courseid == null) || courseid.equals("") || (Sdd == null) || Sdd.equals("") || (Smm == null) || Smm.equals("") || (Syyyy == null) || Syyyy.equals("") || (Fdd == null) || Fdd.equals("") || (Fmm == null) || Fmm.equals("") || (Fyyyy == null) || Fyyyy.equals("") || (duration == null) || duration.equals("")) {
            return false;
        } else {
            return true;
        }
    }
} //main
	