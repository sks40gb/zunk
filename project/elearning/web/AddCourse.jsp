<%@page language="java"%>  
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
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<HTML>
<HEAD>
	<head> 
		<title>Add Couses</title>
		<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
	</head>
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
<table width=100% background='images/AdministratorBackground.jpg' height=100%>	
<td width=10%></td>
		<td>
		<form action=ProceedToAddCourse method=post name=form1>   
		<center><h2><u><font color=#00CCFF>Add New Course</u></h1>		
		<table width=95% bgcolor=#C1C1DD border=6>
		<td align=center>
		
		<tr><td width=30%>Course Category </td>
		

		<td width=50%>
		<select name=coursecategory>
		<option>Technical</option>
		<option>Management</option>
		<option>Public Sector</option>
		<option>Others</option>
		</td>
		</tr>

		<tr>
		<td>Course</td><td><input type=text name=course size=30></td>
		</tr>
		<tr>
		<td>Course ID</td><td><input type=text name=courseid></td>
		</tr>
		<tr>
		<td>Course Spec</td><td><input type=text name=coursespec></td>
		</tr>
		<tr>
		<td width=35%>Start Date</td>
		<td width=35%>


<select name="Sdd">
<option value="01">01</option><option value="02">02</option><option value="03">03</option><option value="04">04</option><option value="05">05</option><option value="06">06</option><option value="07">07</option><option value="08">08</option><option value="09">09</option><option value="10">10</option><option value="11">11</option><option value="12">12</option><option value="13">13</option><option value="14">14</option><option value="15">15</option><option value="16">16</option><option value="17">17</option><option value="18">18</option><option value="19">19</option><option value="20">20</option><option value="21">21</option><option value="22">22</option><option value="23">23</option><option value="24">24</option><option value="25">25</option><option value="26">26</option><option value="27">27</option><option value="28">28</option><option value="29">29</option><option value="30">30</option><option value="31">31</option></select>


<select name="Smm">
<option value="01">Jan</option><option value="02">Feb</option><option value="03">Mar</option><option value="04">Apr</option><option value="05">May</option><option value="06">Jun</option><option value="07">Jul</option><option value="08">Aug</option><option value="09">Sep</option><option value="10">Oct</option><option value="11">Nov</option><option value="12">Dec</option></select>


<select name="Syyyy">
	<option value="1957">1957</option><option value="1958">1958</option><option value="1959">1959</option><option value="1960">1960</option><option value="1961">1961</option><option value="1962">1962</option><option value="1963">1963</option><option value="1964">1964</option><option value="1965">1965</option><option value="1966">1966</option><option value="1967">1967</option><option value="1968">1968</option><option value="1969">1969</option><option value="1970">1970</option><option value="1971">1971</option><option value="1972">1972</option><option value="1973">1973</option><option value="1974">1974</option><option value="1975">1975</option><option value="1976">1976</option><option value="1977">1977</option><option value="1978">1978</option><option value="1979">1979</option><option value="1980">1980</option><option value="1981">1981</option><option value="1982">1982</option><option value="1983">1983</option><option value="1984">1984</option><option value="1985">1985</option><option value="1986">1986</option><option value="1987">1987</option><option value="1988">1988</option><option value="1989">1989</option><option value="1990">1990</option><option value="1991">1991</option><option value="1992">1992</option><option value="1993">1993</option><option value="1994">1994</option><option value="1995">1995</option><option value="1996">1996</option><option value="1997">1997</option><option value="1998">1998</option><option value="1999">1999</option><option value="2000">2000</option><option value="2001">2001</option><option value="2002">2002</option><option value="2003">2003</option><option value="2004">2004</option><option value="2005">2005</option><option value="2006">2006</option><option value="2007">2007</option><option value="2008">2008</option></select>

		</td>	
		</tr>
		<tr>
		<td>Finish Date</td>
		<td>
			


		<select name="Fdd">
<option value="01">01</option><option value="02">02</option><option value="03">03</option><option value="04">04</option><option value="05">05</option><option value="06">06</option><option value="07">07</option><option value="08">08</option><option value="09">09</option><option value="10">10</option><option value="11">11</option><option value="12">12</option><option value="13">13</option><option value="14">14</option><option value="15">15</option><option value="16">16</option><option value="17">17</option><option value="18">18</option><option value="19">19</option><option value="20">20</option><option value="21">21</option><option value="22">22</option><option value="23">23</option><option value="24">24</option><option value="25">25</option><option value="26">26</option><option value="27">27</option><option value="28">28</option><option value="29">29</option><option value="30">30</option><option value="31">31</option></select>


<select name="Fmm">
<option value="01">Jan</option><option value="02">Feb</option><option value="03">Mar</option><option value="04">Apr</option><option value="05">May</option><option value="06">Jun</option><option value="07">Jul</option><option value="08">Aug</option><option value="09">Sep</option><option value="10">Oct</option><option value="11">Nov</option><option value="12">Dec</option></select>


<select name="Fyyyy">
	<option value="1957">1957</option><option value="1958">1958</option><option value="1959">1959</option><option value="1960">1960</option><option value="1961">1961</option><option value="1962">1962</option><option value="1963">1963</option><option value="1964">1964</option><option value="1965">1965</option><option value="1966">1966</option><option value="1967">1967</option><option value="1968">1968</option><option value="1969">1969</option><option value="1970">1970</option><option value="1971">1971</option><option value="1972">1972</option><option value="1973">1973</option><option value="1974">1974</option><option value="1975">1975</option><option value="1976">1976</option><option value="1977">1977</option><option value="1978">1978</option><option value="1979">1979</option><option value="1980">1980</option><option value="1981">1981</option><option value="1982">1982</option><option value="1983">1983</option><option value="1984">1984</option><option value="1985">1985</option><option value="1986">1986</option><option value="1987">1987</option><option value="1988">1988</option><option value="1989">1989</option><option value="1990">1990</option><option value="1991">1991</option><option value="1992">1992</option><option value="1993">1993</option><option value="1994">1994</option><option value="1995">1995</option><option value="1996">1996</option><option value="1997">1997</option><option value="1998">1998</option><option value="1999">1999</option><option value="2000">2000</option><option value="2001">2001</option><option value="2002">2002</option><option value="2003">2003</option><option value="2004">2004</option><option value="2005">2005</option><option value="2006">2006</option><option value="2007">2007</option><option value="2008">2008</option></select>

		</td>	
		</tr>

		<tr>
		<td>Duration</td><td><input type=text size=4 name=duration>&nbsp;days</td>
		</tr>
		</table>
		</td>  
		<td width=10%></td><tr><td><td align=center>
		<input type=image src=images\addcourse.jpg>		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		<img src=images\reset.jpg onClick='window.form1.reset()'></td>
		</table>
		<br><br>			
		</form></body>	
		<%
	    }
	else
	   {
		%>
		<body bgcolor=#B9CCEE><br><br><br><br>
		<center><table border=10 width=90%><tr><td  height=40% bgcolor=#9185C9 width=90%>
		<h1><font color=#CCFFFF><center><br>
		Only For Administrator Access<br></font></h1><center>
		<img src='images/backbutton1.bmp'onClick=history.back()>
		<br></td></tr></table></center></body>
<%
		}
	}
%>
