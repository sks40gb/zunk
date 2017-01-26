<%@page language="java"%>  
<%
 String count=(String)session.getAttribute("COUNT");
 String type=(String)session.getAttribute("TYPE");
 if((count==null) || !count.equals("4"))
	{%>
	<script>
	alert('Unauthorized Access')
	</script>
	<%
	response.sendRedirect("login.html");
	}
else
	{
		if(type.equalsIgnoreCase("Admin"))
		{
		String course=request.getParameter("course");
%>

		<head><title>E-Learning</title>
		<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
		</head><body bgcolor=#C1C1DD>
		<OBJECT classid="clsid:D27CDB6E-AE6D-11cf-96B8-444553540000" codebase="http://download.macromedia.com/pub/shockwave/cabs/flash/swflash.cab#version=5,0,0,0"
		WIDTH="752" HEIGHT="50">
		<PARAM NAME=movie VALUE="css\starmessage1.swf"> <PARAM NAME=quality VALUE="Best"> 
		<PARAM NAME=wmode VALUE="opaque"><PARAM NAME=bgcolor VALUE="#000000">
		<EMBED src="css\starmessage1.swf" quality="Best" wmode="opaque" bgcolor="#000000"
		WIDTH="700" HEIGHT="50" TYPE="applicat<u></u>ion/x-shockwave-flash" 
		PLUGINSPAGE="http://www.macromedia.com/shockwave/download/index.cgi?P1_Prod_Version=ShockwaveFlash"></EMBED></OBJECT><hr>
		<form action=SessionInavalidate method=post>
		<table width=100% background='images/AdministratorBackground.jpg' height=73%>	
		<td width=10%></td><td>	
		<table width=100% bgcolor=#C1C1DD border=1 align=center><td align=center><tr>		<td valign=middle width=100% align=center height=50><font color=#333399><h2>Course Updated successfully</td>
		</table></td><td width=10%></td><tr><td><td align=center valign=top>
		<img src=images/back.jpg onClick=history.back()>
		<input type=image src=images/logout.jpg>
		<img src=images/home.jpg onclick="window.location='Home.jsp'">
		</table><br><br></form></body>	
<%
	    }
	else
	   {
%>
	<script>
	alert('Unauthorized Access')
	</script>
	<% response.sendRedirect("login.html"); %>
	<br></td></tr></table></center></body>
<%
		}
	}
%>

