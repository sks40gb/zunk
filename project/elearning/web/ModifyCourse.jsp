<%@page language="java"%>  
<%@ page import="java.sql.*;" %>
<%
 String count=(String)session.getAttribute("COUNT");
 String type=(String)session.getAttribute("TYPE");
 if((count==null) || !count.equals("4"))
	{
	response.sendRedirect("login.html");
	}
else
	{
		if(type.equalsIgnoreCase("Admin"))
		{		

		PreparedStatement ps1;
		try
			{
			Connection con=com.elearn.db.connection.ElearnConnection.getConnection();
			ps1=con.prepareStatement("select course_id,course from course where course like ?");		
			ps1.setString(1,"%");
			ResultSet rs1=ps1.executeQuery();		
			%>


<head><title>E-Learning</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
</head><body bgcolor=#C1C1DD>
<OBJECT classid="clsid:D27CDB6E-AE6D-11cf-96B8-444553540000" codebase="http://download.macromedia.com/pub/shockwave/cabs/flash/swflash.cab#version=5,0,0,0"
WIDTH="752" HEIGHT="50">
<PARAM NAME=movie VALUE="css\starmessage1.swf"> <PARAM NAME=quality VALUE="Best"> 
<PARAM NAME=wmode VALUE="opaque"><PARAM NAME=bgcolor VALUE="#000000">
<EMBED src="css\starmessage1.swf" quality="Best" wmode="opaque" bgcolor="#000000"
WIDTH="700" HEIGHT="50" TYPE="application/x-shockwave-flash" 
PLUGINSPAGE="http://www.macromedia.com/shockwave/download/index.cgi?P1_Prod_Version=ShockwaveFlash"></EMBED></OBJECT><hr>

<table width=100% background='images/AdministratorBackground.jpg' height=79% >	
<td valign=top width=100%>
<form action='ModifyCourse2.jsp'><br><br>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
&nbsp;&nbsp;&nbsp;<font color=#D7DFFF size=5>Update Course<br>
<table width=80% bgcolor=#C1C1DD border=6 align=center valign=top>
	<tr>		
	<td>Course</td>
	<td>
	<select name="courseid">
<%	
	while(rs1.next())
		{
		%>
		<option value="<%= rs1.getString(1)%>"><%= rs1.getString(2)%></option>
		<%
		}
		%>	

	
	</td>
	<td><input type=image src=images\update.jpg></td>
</tr>	
</table>
</form>

<br><br>
<table align=center>
<tr><td align=center valign=top>
<img src=images/back1.jpg onClick='history.back()'></td>
<td width=30></td><td>
<img src=images/home1.jpg onClick="window.location='Home.jsp'"></td>
</table>
</table><br><br></form></body>	
<%

   }catch(Exception e){e.printStackTrace();}
	    }
	else
	   {
%>
		<body bgcolor=#B9CCEE><br><br><br><br>
		<center><table border=10 width=90%><tr><td  height=20% bgcolor=#9185C9 width=90%>
		<h2><font color=#CCFFFF><center><br>
		Only For Administrator Access<br></font></h2><center>
		<img src='images/backbutton1.bmp' onClick=history.back()>
		<br></td></tr></table></center></body>
<%
		}
	}
%>