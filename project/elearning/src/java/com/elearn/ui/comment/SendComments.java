package com.elearn.ui.comment;

import com.elearn.db.connection.ElearnConnection;
import com.elearn.util.DateFormat;
import javax.servlet.http.*;
import javax.servlet.*;
import java.io.*;
import java.sql.*;

public class SendComments  extends HttpServlet
{
	PreparedStatement ps1,ps2;
	PrintWriter pw;
	String subject,comments;		
	
	public  void doPost(HttpServletRequest req, HttpServletResponse res)throws   ServletException,IOException
	{
		try
		{				
			Connection con=ElearnConnection.getConnection();
			ps1=con.prepareStatement("insert into user_comment(user_id, subject, comment_t, send_date) values(?,?,?,?)");
			res.setContentType("text/html");
			pw=res.getWriter();
			boolean b=readValues(req);
			if(true)
				{ 
				if(!b)
				{
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
					
					HttpSession ses=req.getSession();
				    String userid=(String)ses.getAttribute("ID");					

					ps1.setString(1,userid);
					ps1.setString(2,subject);
					ps1.setString(3,comments);
                    ps1.setString(4,DateFormat.getCurrentDate());
					
					/*java.util.Date dt=new java.util.Date();
					long LDate=dt.getTime();
					java.sql.Date sqldt=new java.sql.Date(LDate);				
					ps1.setDate(4,sqldt); */								

					int i=ps1.executeUpdate();				 //insert into comment
					con.close();
			}			
			res.sendRedirect("SendCommentsThanks.jsp");
			}

		catch(Exception e)
		{
		pw.print("<h1>"+e+"</h1>");
		pw.print("<body bgcolor=#B9CCEE><br><br><br><br>");
		pw.print("<center><table border=10 width=90%><tr><td  height=20% bgcolor=#9185C9 width=90%>");		
		pw.print("<center><h2><font color=#CCFFFF><br>required inputs are not in proper format ");
		pw.print("</font><br></font></h1><center><input type=button onClick=history.back() value=back>");
		pw.print("<br></td></tr></table></center></body>");
		
		}  
	}  //doPost */

	

	public boolean readValues(HttpServletRequest req)throws Exception
	{   
		subject=req.getParameter("subject");
		comments=req.getParameter("comments");		
		
		if((subject==null) || subject.equals("") ||(comments==null) || comments.equals(""))
			return false;
		else
			return true;
	} 
	
} //main
	