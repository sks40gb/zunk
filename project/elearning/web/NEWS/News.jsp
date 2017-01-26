<%@page language="java"%>  
<%
	 String count=(String)session.getAttribute("COUNT");
	 String type=(String)session.getAttribute("TYPE");
	 String userid=(String)session.getAttribute("ID");
	 if((count==null) || !count.equals("4")||userid.equals("")||userid==null)
		{
		response.sendRedirect("../login.html");
		}
%>
<html><head>

   <meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1"><title>delTTI Autumn Courses</title></head><body alink="#bc8f8f" background="images/news.gif" bgcolor="#ffffff" link="#ff0000" text="#000063" vlink="#d2b48c">

<center>
<h2>
Courses to be included in E-Learning </h2></center>

<center>
<hr width="63%"></center>

<center>
<h3>
<font color="#bd0000">New Irish Writing</font></h3></center>

<blockquote><font size="+1">From Banville to Boland, from McGuinness to McGahern,this
course will explore new Irish writing in English. Contemporary writing
in Ireland looks both over its shoulder at the Irish Renaissance, and sideways
at trends in international writing from Europe, the US and other former-post
colonial countries. This course will explore those backwards and sidewards
looks through the study of fiction, drama and poetry. It will also explore
the relationship between contemporary writing and current-day Irish social,political
and economic concerns.</font></blockquote>

<blockquote><font size="+1">Writers to be discussed: will include John Banville,
EvanneBoland, Brian Friel, Seamus Heaney, John McGahern, Frank McGuinness,
MedbhMcGuckian, Derek Mahon, Eiléan Ní Chuilleanáin.</font>

<p><font size="+1">Fee: £IR
60&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
(Concession)</font><font size="+1"></font>

</p><p>
</p><hr width="63%"></blockquote>

<center>
<h3>
<font color="#bd0000">Commerce With the Muse:<br>
The Art of Writing Poetry</font></h3></center>

<blockquote><font size="+1">This will be a creative writing workshop Internet
style.The genre is poetry, the attitude is inclusive. People who have never
written,as well as those who have already published are invited to participate.There
will be class discussions of the poetry of published poets, as well as
of individual workshop members' work; of what is a poem and what isn't
a poem, of form and style and craft and voice. Students will be invited
to either submit their work to the instructor alone for comment, or submitit
to the group. Other students might not want to write, but join in on the
discussions about contemporary poetry. In either case, by the end ofthe
workshop students should have a better idea of the whys and whereforesof
writing poetry in the 1990s.</font>

</p><p>
</p><hr width="63%"></blockquote>

<center>
<h3>
<font color="#bd0000">Writing for Life:<br>
Business Writing<br>
How to Express One's Self Better Through Writing</font></h3></center>

<blockquote><font size="+1">Why is it that so many of us feel we can't write?
that we shy away from putting ideas down in writing? that we feel the next
guy always expresses it better? Yet, most of us spent a good part of our
formative years --from primary school to third level --writing. What is
it that we missed? Probably nothing. There is no secret to good writing,
but there are principles that many of us forgot, or indeed, never learned.
This course will be a hands-on approach to practical writing skills. Through
a variety of media, such as letters and reports, we'll explore the essentials
of good writing: formulating clear introductions and conclusions, shaping
sentences and paragraphs, using the right punctuation, and editing for
style and tone. This course can either be applied to business or personal
writing.</font>


<hr width="57%">
<center>&nbsp;</center>

<center><table background="courses_files/news.gif" bgcolor="#ffffff" border="0" cellpadding="0" cellspacing="0" cols="6" width="57%">
<tbody><tr align="center">
<td><b><font color="#cc0000"><a href="..\Home.jsp">Home</a></font></b></td>

<td><b><font color="#cc0000"><a href="..\Logout.jsp">Logout</a></font></b></td>

<td><b><font color="#cc0000"><a href="..\CourseDetails.jsp">Courses</a></font></b></td>

<td><b><font color="#cc0000"><a href="mailto:LearnigIndia@gmail.com">Mail Us</a></font></b></td>
</tr>
</tbody></table></center>

<center>&nbsp;</center>

</body></html>