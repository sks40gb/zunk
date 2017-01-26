<%@ page language="java"%>
<%@ page import="java.sql.*;" %>
<%
PreparedStatement ps=null;
Connection con=null;
try{
   String count=(String)session.getAttribute("COUNT");
   String type=(String)session.getAttribute("TYPE");
   String userid=(String)session.getAttribute("ID");   
   if((count==null) || !count.equals("4")||userid.equals("")||userid==null)
		{
		response.sendRedirect("login.html");
		}

   con=com.elearn.db.connection.ElearnConnection.getConnection();

 /*  PreparedStatement ps1=con.prepareStatement("select std_exam_status from student_record where std_id=?");
   ps1.setString(1,userid);
   ResultSet rs1=ps1.executeQuery();
   String status="";
   while(rs1.next())*/
//................................................................................
   
	String catid=request.getParameter("H");
	String category="";
	if(catid.equals("1"))
		category="Technical";
	else if(catid.equals("2"))
		category="Management";
	else if(catid.equals("3"))
		category="Public Sector";
	else
		category="Other Catagories";
%>


<head><title>E-Learning</title></head>
<html><body bgcolor=#E1DEFE text=#330066>
<font color=#290DCA size=6><center><pre><h1><%=category%></h1></pre></center></font>
<font color=#FFFFFF size=4>
<center><table width=90% border=0>
<tr><th></th><th align=left><h2><font color=#290DCA>Course</h2></font></th><th align=left><h3><font color=#290DCA>Start Date<font size=2> (yyyy/mm/dd)</h3></font></th></tr>

<%
   String course="";
   String courseid="";
  // String startdate="";
   PreparedStatement ps1=con.prepareStatement("select course_id,course,course_start_date from course where catid=?");
   ps1.setString(1,catid);
   ResultSet rs1=ps1.executeQuery();  
   int i=1;
   while(rs1.next())
	   {
		courseid=rs1.getString(1);
		course=rs1.getString(2);
		//=new java.sql.Date();
	    java.sql.Date startdate=rs1.getDate(3);       
%>
		<tr>
		<td><font color=#333399 size=4 width=1%><%=i%>.</font></td>
		<td><font color=#FFFFFF size=4><a href="#<%=courseid%>"><%=course%></a></font></td>
		<td><font color=#330066 size=3><%=startdate%></a></font></td>
		</tr>
<%
		i++;
	    }
%>

</font>
</table>
</center>
<br><br><br><br><br><br><br><br><br>
<font color=#290DCA size=4><center><pre><h2><b>Course Topics</b></h2></pre></center></font>
<blockquote>

<%  
   PreparedStatement ps2=con.prepareStatement("select course_id,course from course where catid=?");
   ps2.setString(1,catid);
   ResultSet rs2=ps2.executeQuery();   
   i=1;
   while(rs2.next())
	   {
		courseid=rs2.getString(1);
	    course=rs2.getString(2); 
		%>
		<font color=#290DCA><h3><a name="<%=courseid%>"><%=i%>. <u><%=course%></u></a></h3></font>
		<blockquote><font size=4>
		<%
		String topic="";
		PreparedStatement ps3=con.prepareStatement("select course_topic from topic_details where course_id=?");
		ps3.setString(1,courseid);
		ResultSet rs3=ps3.executeQuery();		
		while(rs3.next())
		   {
			topic=rs3.getString(1);
			%>
			* <%=topic%><br>
			<%
			}
		%>
		</font></blockquote>
		<%
		i++;
	    }
 }catch(Exception e){out.println("Error------"+e);} 
		%>
</html>

