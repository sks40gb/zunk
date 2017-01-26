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
<head><title>E-learining-Commments</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
</head><body bgcolor=#C1C1DD>

<table align=center border=4 width=80% background='images/AdministratorBackground.jpg' height=17% bordercolor=#660066>	
	<td width=90% align=center><h1><font color=#FF3366>E</font><font color=#D7CAF7>-Learning</font></td>
	<td width=10% bgcolor=#330000 align=center><img src=images\world.gif></td>
</table>
<hr width=80%>

<table width=80% border=4 align=center background='images/AdministratorBackground.jpg' height=73% bordercolor=#660066>	
	<td width=10%>
<font color=#FFFFFF>

<%//.....................send comments.......................................%>
<form action="SendComments" method="post"> 
<blockquote><blockquote>
<center>
 <table align=left border=0><tr>
	<td><font color=#D7CAF7><h4>Subject</h4></font></td>
    <td><input name="subject" type="text" id="subject" size="49" style="background-color: rgb(0,0,0); color: rgb(255,255,255);"></td>
    </tr>
     <tr> 
     <tr> 
     <td><font color=#D7CAF7><h4>Comments</h4></font></td>
    <td><textarea name="comments" cols="60" rows="7" style="background-color: rgb(0,0,0); color: rgb(255,255,255);  font-family: Arial; font-size: 12px" wrap=virtual></textarea></td></tr><br>
	<tr>
	<td></td>

	<td align=center height=40 valign=bottom><input type=image src="images/submit.jpg">
   <img src="images/reset.jpg" onClick="window.form.reset()">
	</td>
	</tr>
</table>
</table>
<br>
</center>
<br><br></form></body>	