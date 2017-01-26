    <%@page language="java"%>  
	<%
	 String count=(String)session.getAttribute("COUNT");
	 String type=(String)session.getAttribute("TYPE");
	 String userid=(String)session.getAttribute("ID");
	 if((count==null) || !count.equals("4")||userid.equals("")||userid==null)
		{
		response.sendRedirect("login.html");
		}
	%>	  
	  <html>
      <head><title>E-Learning</title>
      <META HTTP-EQUIV="refresh" content="90000;URL=InsertExam.jsp">
	  </head>
	  <body bgcolor="#E1DEFE">
	  <blockquote>
      <br>  
	  <img src='images/title_back3.jpg' width=100% height=10>
      <font face="Times New Roman" size="7" color="#FF0000">E-</font>
      <font face="Times New Roman" size="7" color="#0099FF"><i>L</i></font>
      <b><font face="Times New Roman" size="6" color="#0099FF">earning</font></b> 
	  <img src='images/title_back3.jpg' width=100% height=10>		 

	  </body>
	  </html>
