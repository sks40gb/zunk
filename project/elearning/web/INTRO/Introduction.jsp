<%@ page language="java"%>
<%@ page import="java.sql.*;" %>
<%   
   String count=(String)session.getAttribute("COUNT");
   String userid=(String)session.getAttribute("ID");   
   if((count==null) || !count.equals("4")||userid.equals("")||userid==null)
		{
		response.sendRedirect("login.html");
		}
%>
<head><title>Add Topics </title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
</head><body bgcolor=#C1C1DD>

<table align=center border=4 width=90% background='images/AdministratorBackground.jpg' height=17% bordercolor=#660066>	
	<td width=10% bgcolor=#330000 align=center><img src=images\world.gif></td><td width=80% align=center><h1><font color=#D7CAF7>Introduction to </font><font color=#FF3366>E</font><font color=#D7CAF7>-Learning</font></td>
	<td width=10% bgcolor=#330000 align=center><img src=images\world.gif></td>
</table>
<hr width=90%>

<table width=90% border=4 align=center background='images/AdministratorBackground.jpg' height=73% bordercolor=#660066>	
	<td width=10%>
<font color=#FFFFFF>

<blockquote><blockquote>


<br>
<p>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Experience the same engaging and effective e-learning used by corporations such as McDonalds, Cisco® Systems Inc., and the US Postal Service. Technical course offerings include the core and elective courses to prepare for MCSE in Microsoft® Windows® 2000, SQL, Exchange, and Office 2000 products. The greatest benefit of this content rich, anytime anywhere technical education is that all courses map to Microsoft® Technical certification exams.</p>


<br>
<h3>Learn . . .</h3>
<ul>
<li>key features of an online learning environment.
</li><li>strengths and limitations of online learning.
</li><li>types of applications.
</li><li>instructional design components for course development.
</li><li>dimensions of interaction.
</li><li>roles of the online instructor and learner.
</li><li>learner support issues.
</li></ul>

<br>
<h3>Equipment and Software Required</h3>
<ul><li>either a PC with Windows or a Macintosh computer
</li><li>modem (56 kbps or faster) or broadband access
</li><li>Internet and World Wide Web access
</li><li>Web browser (Netscape 6.0 or Internet Explorer 5.0 or better)
</li></ul>

          

<h3>Estimated Time Commitment</h3>

<p>Approximately five to ten hours per week for hands-on activities and
e-mail discussion via WebCT conferencing tools. Weekly participation in
discussion is required to earn Continuing Education Units (CEUs) for
course completion.</p>


<h3>Asynchronous Format </h3>

<p>The course uses an asynchronous format for participation in weekly
online discussion forums and case studies. You can send and read e-mail
messages at any time, although early in the week and then later in the
week is required for group cohesion.
</p>

<h3>About the Instructor</h3>
<p>Christine Olgren is Director of the Distance Education Certificate
Program and is responsible for program management, curriculum planning,
instructional development, evaluation, and marketing. She also serves
as an instructor and student advisor. Chris has worked in the field of
distance education since 1977. As well as program management, her
experience includes instructional design, technology assessment,
training, learner support services and instruction via audio, video,
and the Internet. She has taught online since 1995 and has developed
courses for both collaborative and self-paced learning. She holds a
Ph.D. in adult education from the University of Wisconsin-Madison, with
an emphasis in adult learning and cognitive psychology. Her research
interests focus on interactive instructional strategies and the
cognitive-metacognitive-affective dimensions of learning with
technology. She recently completed a three-year grant project on
developing and evaluating learning objects for online instruction. </p>

 



</blockquote>











</table><br><br></form></body>	