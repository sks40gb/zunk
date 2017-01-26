package com.elearn.ui.exam;


//Add New Examination
import com.elearn.db.connection.ElearnConnection;
import javax.servlet.http.*;
import javax.servlet.*;
import java.io.*;
import java.sql.*;

public class AddQuestions extends HttpServlet {

    public Connection con;
    PreparedStatement ps1;
    PrintWriter pw;
    String Question, Option1, Option2, Option3, Option4, Answer;

    public void init() {
        try {
            con = ElearnConnection.getConnection();
            ps1 = con.prepareStatement("insert into examination " +
                    "(question_id," +
                    "course_id," +
                    "question_desc," +
                    "option1," +
                    "option2," +
                    "option3," +
                    "option4," +
                    "answer" +
                    ") values(exam_increment.nextval,?,?,?,?,?,?,?)");
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
                pw.print("<center><table border=10 width=90%><tr><td  height=15% bgcolor=#9185C9 width=90%>");
                pw.print("<h2><font color=#CCFFFF><center><br>");
                pw.print("<br></font> <font color=white>Required Inputs are Insufficient </font>");
                pw.print("<font color=#CCFFFF><br>");
                pw.print("</font><br></font></h2><center>");
                pw.print("<img src='images/backbutton1.bmp'onClick=history.back()>");
                pw.print("<br><br></td></tr></table></center></body>");
                return;
            }

            if (!Answer.equals(Option1) && !Answer.equals(Option2) && !Answer.equals(Option3) && !Answer.equals(Option4)) {
                pw.print("<body background='images/AdministratorBackground.jpg'><br><br><br><br>");
                pw.print("<center><table border=10 width=90%><tr><td  height=20% bgcolor=#9185C9 width=90%>");
                pw.print("<h1><font color=#CCFFFF><center><br>");
                pw.print("<br></font> <font color=white>Answer must be one of four options</font>");
                pw.print("<font color=#CCFFFF><br>");
                pw.print("</font><br></font></h1><center>");
                pw.print("<img src='images/backbutton1.bmp'onClick=history.back()>");
                pw.print("<br><br></td></tr></table></center></body>");
                return;
            }


            //.................................
            HttpSession ses = req.getSession();
            String courseid = (String) ses.getAttribute("C_id");
            ps1.setString(1, courseid);
            ps1.setString(2, Question);
            ps1.setString(3, Option1);
            ps1.setString(4, Option2);
            ps1.setString(5, Option3);
            ps1.setString(6, Option4);
            ps1.setString(7, Answer);
            int x = ps1.executeUpdate();

            res.sendRedirect("AddQuestions.jsp");

        //....................................

        } catch (Exception e) {
            //pw.print(e);
            pw.print("<body background='images/AdministratorBackground.jpg'><br><br><br><br>");
            pw.print("<center><table border=10 width=90%><tr><td  height=40% bgcolor=#9185C9 width=90%>");
            pw.print("<center><h1><font color=#CCFFFF><br>required inputs are not in proper format ");
            pw.print("</font><br></font></h1><center><input type=button onClick=history.back() value=back>");
            pw.print("<br></td></tr></table></center></body>");
        //e.printStackTrace(out);
        }
    }  //doPost

    public boolean readValues(HttpServletRequest req) throws Exception {
        Question = req.getParameter("Question");
        Option1 = req.getParameter("Option1");
        Option2 = req.getParameter("Option2");
        Option3 = req.getParameter("Option3");
        Option4 = req.getParameter("Option4");
        Answer = req.getParameter("Answer");

        if ((Question == null) || Question.equals("") || (Option1 == null) || Option1.equals("") || (Option2 == null) || Option2.equals("") || (Option3 == null) || Option3.equals("") || (Option4 == null) || Option4.equals("") || (Answer == null) || Answer.equals("")) {
            return false;
        } else {
            return true;
        }
    }
} //main