<%@ page language="java"%>
<%@ page import="java.sql.*;" %>
<%
 String count=(String)session.getAttribute("COUNT"); 
 String userid=(String)session.getAttribute("ID");
 if((count==null) || !count.equals("4")||userid.equals("")||userid==null)
	{
	response.sendRedirect("index.html");
	}
  //String userid="sks";	
  %>


<BODY BGCOLOR="white">
<FONT size="+1" ID="FrameHeadingFont">
<B>Topics </B></FONT>
<BR><BR>
</FONT>

<%
try
  {   
   Connection con=com.elearn.db.connection.ElearnConnection.getConnection();
//..........................................................................
	String courseid="";
	PreparedStatement ps1=con.prepareStatement("select std_course_id from student_record where std_id=?");
	ps1.setString(1,userid);
	ResultSet rs1=ps1.executeQuery();
	if(!rs1.next())
		response.sendRedirect("Home.jsp");
	else
		courseid=rs1.getString(1);
//............................................................................
	String catid="";
	PreparedStatement ps3=con.prepareStatement("select catid from course where course_id=?");
	ps3.setString(1,courseid);
	ResultSet rs3=ps3.executeQuery();
	rs3.next();
	catid=rs3.getString(1);
//............................................................................
	String category="";
	PreparedStatement ps4=con.prepareStatement("select category from course_category where catid=?");
	ps4.setString(1,catid);
	ResultSet rs4=ps4.executeQuery();
	rs4.next();
	category=rs4.getString(1);

//............................................................................
	//String CourseTopic="";
	//String path="";
    PreparedStatement ps2=con.prepareStatement("select course_topic,path from topic_details where course_id=?");
	ps2.setString(1,courseid);
	ResultSet rs2=ps2.executeQuery();
	while(rs2.next())
		{
		String CourseTopic=rs2.getString(1);
		String path=rs2.getString(2);
		if(path==null)
			path="";
		%> 
		<A HREF="course/<%=category%>/<%=courseid%>/<%=path%>/<%=CourseTopic%>.html"  target="RIGHT"><%=CourseTopic%></A><br>
		<%
		 }
  }catch(Exception e){}

		%>

</BODY>
</HTML>
