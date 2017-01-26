<%@page language="java"%>  
<%@ page import="java.sql.*;" %>
<%
String courseid=request.getParameter("courseid");
 String count=(String)session.getAttribute("COUNT");
 String type=(String)session.getAttribute("TYPE");
 //HttpSession ses=req.getSession();
 session.setAttribute("courseid",courseid);

 if((count==null) || !count.equals("4"))
	{
	response.sendRedirect("login.html");
	}
	%>


		<body bgcolor=#B9CCEE><br><br><br><br>
		<center><table border=10 width=90%><tr><td  height=20% bgcolor=#9185C9 width=90%>
		<h2><font color=#CCFFFF><center><br>
		Course doen't exist<br></font></h2><center>
		<img src='images/backbutton1.bmp' onClick=history.back()>
		<br></td></tr></table></center></body>