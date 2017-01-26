<%@ page language="java"%>
<%@ page import="java.sql.*;" %>
<%
   String count=(String)session.getAttribute("COUNT");
   String type=(String)session.getAttribute("TYPE");
   String userid=(String)session.getAttribute("ID");   
   if((count==null) || !count.equals("4")||userid.equals("")||userid==null)
		{
		response.sendRedirect("login.html");
		}
		%>
		<body bgcolor=#B9CCEE><br><br><br><br>
		<center><table border=10 width=90%><tr><td  height=40% bgcolor=#9185C9 width=90%>
		<h2><font color=#CCFFFF><center><br>
		You have not appeared in Examination<br></font></h2><center>
		<img src='images/backbutton1.bmp'onClick=history.back()>
		<br></td></tr></table></center></body>