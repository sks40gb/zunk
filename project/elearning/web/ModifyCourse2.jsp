<%@page language="java"%>  
<%@ page import="java.sql.*;" %>
<%
String courseid=request.getParameter("courseid");
String course="";
PreparedStatement ps1;

 String count=(String)session.getAttribute("COUNT");
 String type=(String)session.getAttribute("TYPE");
 //HttpSession ses=req.getSession();
 session.setAttribute("courseid",courseid);

 if((count==null) || !count.equals("4"))
	{
	response.sendRedirect("login.html");
	}
else
	{
	if(type.equalsIgnoreCase("Admin"))
	{

		try
		{
		Connection con=com.elearn.db.connection.ElearnConnection.getConnection();
		ps1=con.prepareStatement("select course from course where course_id=?");	
	
		ps1.setString(1,courseid);
		ResultSet rs1=ps1.executeQuery();
		while(rs1.next())
			course=rs1.getString(1);
		}
	
		catch(Exception e){e.printStackTrace();}


		if(course.equals("")|| course==null)
			  {%>
			  <script>
			  alert('Course ID does not exist')
			  history.back()
			  </script>
			  <%			  			
	   		  }
		else
			{
			%>
			<head><title>Modify Course </title>
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
			<br><br>
			&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
			&nbsp;&nbsp;&nbsp;<font color=#D7DFFF size=5>Update Course :<font color=red><%=course%><br>
			<table width=80% bgcolor=#C1C1DD border=6 align=center valign=top>
			<tr>		
			<td width=50%><font size=4>Update Course :</font></td>		
				<form action=UpdateCourse.jsp><td width=15%><input type=image src=images\go.jpg></td>
				<input type=hidden name=courseid value=<%=courseid%>></form>

			</tr>	
			<tr>		
				<td width=50%><font size=4>Add New Topics</font></td>	
				<form action=AddMoreTopic.jsp><td width=15%><input type=image src=images\go.jpg name=courseid value=<%=courseid%>></td>
				<input type=hidden name=courseid value=<%=courseid%>></form>
				</tr>	
			<tr>		
			<td width=50%><font size=4>Delete Old Topics</font></td>	
				<form action=DeleteTopics method=post><td width=15%><input type=image src=images\go.jpg name=courseid value=<%=courseid%>></td>
				<input type=hidden name=courseid value=<%=courseid%>></form>
			</tr>			
			</table>
			<br><br>
			<table align=center>
			<tr><td align=center valign=top>
			<img src='images/back1.jpg' onClick='history.back()'></td>
			<td width=30></td><td>
			<img src='images/home1.jpg' onClick=window.location='Home.jsp'></td>
			</table>
			</table><br><br></form></body>	
			<%}%>
<%
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