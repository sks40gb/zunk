<%@page language="java"%>  
<%
        String count = (String) session.getAttribute("COUNT");
        String type = (String) session.getAttribute("TYPE");
        String userid = (String) session.getAttribute("ID");
        if ((count == null) || !count.equals("4") || userid.equals("") || userid == null) {
            response.sendRedirect("index.html");
        }
%>
<head><title>Add Topics </title>
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
        <table width=100% background='images/AdministratorBackground.jpg' height=73%>
            <td width=10%></td><td>
                <table width=100% bgcolor=#C1C1DD border=1 align=center><td align=center><tr>		<td valign=middle width=100% align=center height=50><font color=#333399><h2>Topics are Added Successfully</td>
            </table></td><td width=10%></td><tr><td><td align=center valign=top>
            <img src='images/back1.jpg'  onClick="window.location='http://localhost:7001/ss/AddMoreTopic.jsp'">
            <input type=image src='images/logout1.jpg'>
            <img src='images/home1.jpg' onclick="window.location='Home.jsp'">
</table><br><br></form></body>
