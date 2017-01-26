package com.elearn.ui.exam;


//Add New Examination

import com.elearn.db.connection.ElearnConnection;
import com.elearn.util.DateFormat;
import javax.servlet.http.*;
import javax.servlet.*;
import java.io.*;
import java.sql.*;

public class AddNewExam extends HttpServlet
{
	public Connection con;
	PreparedStatement ps1;
	PrintWriter pw;	
	String courseid;	
	String NoOfQuestions;
	
	public void init()
	{		
		try
		   {			
			con=ElearnConnection.getConnection();
			ps1=con.prepareStatement("update course set exam_que_numbers=?,course_exam_date=? where course_id=?");			
		    }
		   catch(Exception e){e.printStackTrace();}
	 }      

	public  void doPost(HttpServletRequest req, HttpServletResponse res)throws ServletException,IOException
	{
		try
		{	
			res.setContentType("text/html");
			pw=res.getWriter();	
			
			boolean b=readValues(req);
			if(!b)
			{
			 //pw.println("<h3><font color=blue>Required Inputs are Insufficient </font></h3><br><form><input type=button value='Back' onClick='history.back()'></form>");
			 pw.print("<body background='images/AdministratorBackground.jpg'><br><br><br><br>");
			 pw.print("<center><table border=10 width=90%><tr><td  height=10% bgcolor=#9185C9 width=90%>");
			 pw.print("<h1><font color=#CCFFFF><center><br>");
			 pw.print("<br></font> <font color=white>Required Inputs are Insufficient </font>");
			 pw.print("<font color=#CCFFFF><br>");
			 pw.print("</font><br></font></h1><center>");
			 pw.print("<img src='images/backbutton1.bmp'onClick=history.back()>");
			 pw.print("<br><br></td></tr></table></center></body>");
				return;
			}
			
			String day=req.getParameter("DD");
			String month=req.getParameter("MM");
			String year=req.getParameter("YYYY");
			String date= DateFormat.getConvertedDate(day, month, year);
            
			Statement stmt=con.createStatement();
			ResultSet rs=stmt.executeQuery("select course_id from course");

			int isCourse=0;
			while(rs.next())
				{
				String notNull=rs.getString(1);
				if(notNull==null)
					notNull="";

				if(notNull.equals(courseid))
					{
					isCourse=1;
					break;
					}
				isCourse=0;
				}
				
			if(isCourse==0)
				res.sendRedirect("Error.jsp");
            //............................................
			HttpSession ses=req.getSession();
			ses.setAttribute("C_id",courseid);
			ps1.setInt(1,Integer.parseInt(NoOfQuestions));		
			ps1.setString(2,date);
			ps1.setString(3,courseid);
			int x=ps1.executeUpdate();			
			res.sendRedirect("AddQuestions.jsp");
            //............................................

		}
		catch(Exception e)
		{	
		//pw.print(e);
		pw.print("<body background='images/AdministratorBackground.jpg'><br><br><br><br>");
		pw.print("<center><table border=10 width=90%><tr><td  height=20% bgcolor=#9185C9 width=90%>");
		pw.print("<h1><font color=#CCFFFF><center>");		
		pw.print("<font color=#CCFFFF><br>required inputs are not in proper format ");
		pw.print("</font><br></font></h1><center><input type=button onClick=history.back() value=back>");
		pw.print("<br></td></tr></table></center></body>");
		//e.printStackTrace(out);
		}  
	}  //doPost
	public boolean readValues(HttpServletRequest req)throws Exception
	{   
		String dd,mm,yyyy;

		courseid=req.getParameter("courseid");	
		dd=req.getParameter("DD");
		mm=req.getParameter("MM");
		      yyyy=req.getParameter("YYYY");
		NoOfQuestions=req.getParameter("NoOfQuestions");		

		if((courseid==null) || courseid.equals("")||(dd==null)||dd.equals("")||(mm==null)||mm.equals("")||(yyyy==null)||yyyy.equals("")||(NoOfQuestions==null)||NoOfQuestions.equals("") )
			return false;
		else
			return true;
	} 
} //main