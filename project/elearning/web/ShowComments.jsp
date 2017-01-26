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
		try{		
			Connection con=com.elearn.db.connection.ElearnConnection.getConnection();
			 PreparedStatement ps1=con.prepareStatement("select user_id,subject,comment_t,send_date from user_comment where comment_t like ?");  
			ps1.setString(1,"%"); 
			ResultSet rs1=ps1.executeQuery();
		%>
<head><title>Add Topics </title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
</head><body bgcolor=#C1C1DD>

<table align=center border=4 width=90% background='images/AdministratorBackground.jpg' height=17% bordercolor=#660066>	
	<td width=10% bgcolor=#330000 align=center><img src=images\world.gif></td>
	<td width=80% align=center><h1><font color=#D7CAF7> </font><font color=#FF3366>E</font><font color=#D7CAF7>-Learning</font></td>
	<td width=10% bgcolor=#330000 align=center><img src=images\world.gif></td>
</table>
<hr width=90%>

<table width=90% border=4 align=center background='images/AdministratorBackground.jpg' height=73% bordercolor=#660066>	
	<td valign=top>
<font color=#FFFFFF>

<blockquote><br>

<%
while(rs1.next())
	{
	String x1=rs1.getString(1);
	String x2=rs1.getString(2);
	String x3=rs1.getString(3);
	java.util.Date x4=rs1.getDate(4);
	%>
	<pre><font color=#FF0033 size=5><b><%=x1%> :</b></font>                                                     <%=x4%><br><font color=#00CCFF size=4><%=x2%> : </font><font color=#CCFFFF><%=x3%></font>

	<%
	}
	%>

</blockquote></table><br><br></form></body>	


	<%
			}catch(Exception e){}
	    } // if-2 closed
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