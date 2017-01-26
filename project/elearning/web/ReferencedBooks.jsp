<%@ page language="java"%>
<%@ page import="java.sql.*;" %>
<%
   Connection con=null;
   
   String count=(String)session.getAttribute("COUNT");
   String type=(String)session.getAttribute("TYPE");
   String userid=(String)session.getAttribute("ID");   
   if((count==null) || !count.equals("4")||userid.equals("")||userid==null)
		{
		response.sendRedirect("login.html");
		}
try{
   con=com.elearn.db.connection.ElearnConnection.getConnection();
//...............................................................................

	 PreparedStatement ps=con.prepareStatement("select course_id,course from course where course like ?");  
	 ps.setString(1,"%"); 
	 ResultSet rs=ps.executeQuery();
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

<table width=100% background='images/AdministratorBackground.jpg' height=100% border=0>	
<td width=10%></td><td valign=top height=10>
<form action="ReferencedBooksShow.jsp" >		
<br>
<center><h2><u><font color=#00CCFF>Referenced Books</u></h1>		

<table width=80% bgcolor=#C1C1DD border=6><td align=center>

<tr>
<td>Course</td>
<td>

<select name="courseid">
		<%
		while(rs.next())
		   {
			%>		
			<option value= "<%= rs.getString(1)%>"><%= rs.getString(2)%> </option>
			<%
		   }
			%>	
</td></tr>
</table>

</td><td width=10%></td><tr><td><td align=center valign=top height=20><br>
<input type="image" src="images\show.jpg" name="addtopic">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<img src="images\myhome.jpg" onClick="window.location.href='Home.jsp'">
<tr>
<% 
}catch(Exception e){} 
%>

</td><td></td>
</tr>
</table><br><br></form></body>	