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
<head>
<title></title>
<frameset rows="17%,83%">
<frame name="TOP" src="CourseDetailsTOP.jsp">
<frameset cols="17%,83%">
<frame name="LEFT" src="CourseDetailsLEFT.jsp">
<frame name="RIGHT" src="CourseDetailsRIGHT.jsp">
</frameset>
</head>
<body>
</body>
</html>