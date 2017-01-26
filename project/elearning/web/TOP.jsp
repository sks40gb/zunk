 <%@ page language="java"%>
<%@ page import="java.sql.*;" %> 
<%
 String count=(String)session.getAttribute("COUNT");
 String type=(String)session.getAttribute("TYPE");
 String userid=(String)session.getAttribute("ID");
 if((count==null) || !count.equals("4")||userid.equals("")||userid==null)
	{
	response.sendRedirect("index.html");
	}

try
  {   
   Connection con=com.elearn.db.connection.ElearnConnection.getConnection();
//..........................................................................
	String courseid="";
	PreparedStatement ps1=con.prepareStatement("select std_course_id from student_record where std_id=?");
	ps1.setString(1,userid);
	ResultSet rs1=ps1.executeQuery();	
	rs1.next();
	courseid=rs1.getString(1);
//...........................................................................

	PreparedStatement ps2=con.prepareStatement("select course from course where course_id=?");
	ps2.setString(1,courseid);
	ResultSet rs2=ps2.executeQuery();	
	rs2.next();
	String course=rs2.getString(1);
	%>
<body bgcolor=#A4D1FF>
<style>A:hover {background: yellow;}</style>
<table border=0 width=100%><td width=10%><a href="Home.jsp" target="_parent">Home</a></td><td width=80% align=center><U><b><font color=#663366 size=6 align=top>Course </font></U></b>
<font color=#663399>:</font><U><font color=#FF0033 size=6><%=course%></font> </center></font></U></td><td width=10%></td></table>
<hr width=100% size=4 color=#663366>
<%		 
  }catch(Exception e){}

		%>
</body>
</html>
