<head><title>E-Learning</title>
<%@page language="java"%>  
<%@ page import="java.sql.*;" %>
<%
	 String count=(String)session.getAttribute("COUNT");
	 String type=(String)session.getAttribute("TYPE");
	 String userid=(String)session.getAttribute("ID");
	 if((count==null) || !count.equals("4")||userid.equals("")||userid==null)
		{
		response.sendRedirect("login.html");
		}

		PreparedStatement ps1,ps2;
		try
			{			
			Connection con=com.elearn.db.connection.ElearnConnection.getConnection();
//............................................................................
			String courseid="";
			ps1=con.prepareStatement("select std_course_id from student_record where std_id=?");	
			ps1.setString(1,userid);
			ResultSet rs1=ps1.executeQuery();
			while(rs1.next())
				courseid=rs1.getString(1);
//............................................................................
			%>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
</head><body bgcolor=#C1C1DD>
<OBJECT classid="clsid:D27CDB6E-AE6D-11cf-96B8-444553540000" codebase="http://download.macromedia.com/pub/shockwave/cabs/flash/swflash.cab#version=5,0,0,0"
WIDTH="752" HEIGHT="50">
<PARAM NAME=movie VALUE="css\starmessage1.swf"> <PARAM NAME=quality VALUE="Best"> 
<PARAM NAME=wmode VALUE="opaque"><PARAM NAME=bgcolor VALUE="#000000">
<EMBED src="css\starmessage1.swf" quality="Best" wmode="opaque" bgcolor="#000000"
WIDTH="700" HEIGHT="50" TYPE="application/x-shockwave-flash" 
PLUGINSPAGE="http://www.macromedia.com/shockwave/download/index.cgi?P1_Prod_Version=ShockwaveFlash"></EMBED></OBJECT><hr>
<form action=SessionInavalidate method=post>
<table width=100% background='images/AdministratorBackground.jpg'  border=0>	
<tr><th height=1% valign=top><font size=6 color=#59BDFF><pre>Question Bank</pre></th></tr>
<tr><th valign=top height=1%><font size=5 color=#91D2FF><pre>Course : <font size=4 color=#E80000><%=courseid%></pre></th></tr>
<%
//...............................................................................
	int i=1;
	String question="";
	ps2=con.prepareStatement("select question from questionbank where course_id=?");	
	ps2.setString(1,courseid);
	ResultSet rs2=ps2.executeQuery();
	while(rs2.next())
		{
		question=rs2.getString(1);
//...............................................................................
%>
		<tr><td height=1%><blockquote><font color=white><%=i%>. <%=question%></td></tr>
<%		
		i++;
		}

	}catch(Exception e){out.print(e);}
%>
<td></td>
</table></td><td width=10%><br><br><center>
<img src='images/back1.jpg' onClick=history.back()>
<input type=image src='images/logout1.jpg'>
<img src='images/home1.jpg' onclick="window.location='Home.jsp'">
</center>
</table><br><br></form></body>	