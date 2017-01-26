<%@page language="java"%>  
<%
 String count=(String)session.getAttribute("COUNT");
 String type=(String)session.getAttribute("TYPE");
 String userid=(String)session.getAttribute("ID");
 if((count==null) || !count.equals("4")||userid.equals("")||userid==null)
	{
	response.sendRedirect("index.html");	
	}
%>
<body bgcolor=#A4D1FF background='images/AdministratorBackground.jpg'>
<center><h1><U><font color=#00CCFF align=top>E-Learning</font>
<font color=#663399>:</font><U><font color=#FF0033>Courses</font> </center></font>
</body>
</html>
