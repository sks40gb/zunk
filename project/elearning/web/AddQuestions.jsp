<%@page language="java"%>  
<%@ page import="java.sql.*;" %>
<%
String courseid=request.getParameter("courseid");
 String count=(String)session.getAttribute("COUNT");
 String type=(String)session.getAttribute("TYPE");
 //HttpSession ses=req.getSession();
 session.setAttribute("courseid",courseid);

 if((count==null) || !count.equals("4"))
	{
	response.sendRedirect("login.html");
	}
	%>
</HEAD>
<body bgcolor=#C1C1DD>
<OBJECT classid="clsid:D27CDB6E-AE6D-11cf-96B8-444553540000" codebase="http://download.macromedia.com/pub/shockwave/cabs/flash/swflash.cab#version=5,0,0,0"
WIDTH="752" HEIGHT="50">
<PARAM NAME=movie VALUE="css\starmessage1.swf"> 
<PARAM NAME=quality VALUE="Best"> 
<PARAM NAME=wmode VALUE="opaque"> 
<PARAM NAME=bgcolor VALUE="#000000">
<EMBED src="css\starmessage1.swf" quality="Best" wmode="opaque" bgcolor="#000000"
WIDTH="700" 
HEIGHT="50" 
TYPE="application/x-shockwave-flash" 
PLUGINSPAGE="http://www.macromedia.com/shockwave/download/index.cgi?P1_Prod_Version=ShockwaveFlash">
</EMBED>
</OBJECT>
<hr>
<table width=100% background='images/AdministratorBackground.jpg' height=70% valign=top>	
<td width=10%></td>
		<td valign=top><br><br>

		<form action=AddQuestions method=post name=form1>		
		<center><h2><u><font color=#00CCFF>Add New Questions</u></h1>		
		<table width=100% bgcolor=#C1C1DD border=6 valign=top>
		<td align=center>		
				
		<tr>
		<td>Question</td><td><input type=text name=Question size=80></td>
		</tr>
		<tr>
		<td>Option 1</td><td><input type=text name=Option1 size=25></td>
		</tr>
		<tr>
		<td>Option 2</td><td><input type=text name=Option2 size=25></td>
		</tr>
		<tr>
		<td>Option 3</td><td><input type=text name=Option3 size=25></td>
		</tr>
		<tr>
		<td>Option 4</td><td><input type=text name=Option4 size=25></td>
		</tr>
		<tr>
		<td>Answer</td><td><input type=text name=Answer size=25></td>
		</tr>
		</table><br><br>
		<input type=hidden name="C_id" value=<%=courseid%> >
		<input type=image src=images/submit.jpg>
		<img src=images/reset.jpg onClick='window.form1.reset()'>		
		<img src=images/home1.jpg onClick=window.location.href='Home.jsp'>
		</form>
		<tr><td align=center>
		<td></td>		
		</td>		
		</td>  
		<td width=10%></td><tr><td></tr><tr></td>
		</table>
		<br><br>			
		</form></body>	