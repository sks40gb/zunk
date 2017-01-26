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
   String courseid=request.getParameter("courseid");

   con=com.elearn.db.connection.ElearnConnection.getConnection();
//...............................................................................

	 PreparedStatement ps1=con.prepareStatement("select course from course where course_id =?");  
	 ps1.setString(1,courseid); 
	 ResultSet rs1=ps1.executeQuery();
	 if(rs1.next())
	{
	 String course=rs1.getString(1);
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

<table width=100% background='images/AdministratorBackground.jpg' height=300 border=0>
<tr>
<form action="SessionInavalidate" method=post>	
<td align=left height=20 valign=top><pre><h2><font color=#00CCFF><blockquote><blockquote><blockquote><blockquote>Course :</font> <font color=#FF0000><%=course%></font></h2></blockquote></blockquote></blockquote></blockquote><center>
		<table border=1 width=90%>
		<tr><th><font color=#00CCFF >Referenced Books</font></th><th><font color=#00CCFF>Author</th></tr>

		<%       		 
	    PreparedStatement ps2=con.prepareStatement("select bookname,author from ebooks where course_id=?");
	    ps2.setString(1,courseid);
	    ResultSet rs2=ps2.executeQuery();
	    while(rs2.next())
			{
			%>
			<tr>
			<td><font color=#DDF7FF size=3><li><%=rs2.getString(1)%></td>
			<td><font color=#DDF7FF size=3><%=rs2.getString(2)%></td>
			</td>
			</tr>
			<%
			} //while closed
			%>
		</table></center>
		<%	 
	}//if closed
}catch(Exception e){out.println("Error------"+e);} 
%>
</td>
<tr><td align=center valign=top height=20><br>
<input type="image" src="images\logout1.jpg" name="addtopic">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<img src="images\myhome.jpg" onClick="window.location.href='Home.jsp'">
</td></tr>

</table><br><br></form></body>	