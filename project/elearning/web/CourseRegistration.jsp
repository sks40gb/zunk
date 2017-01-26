    <%@page language="java"%>
	<%@ page import="java.sql.*;" %>
	<%
	 String count=(String)session.getAttribute("COUNT");
	 String type=(String)session.getAttribute("TYPE");
	 String userid=(String)session.getAttribute("ID");
	 if((count==null) || !count.equals("4")||userid.equals("")||userid==null)
		{
		response.sendRedirect("login.html");
		}

    try{			
			Connection con=com.elearn.db.connection.ElearnConnection.getConnection();
			PreparedStatement ps1=con.prepareStatement("select course_id,course from course where course like ?");  
			ps1.setString(1,"%"); 
			ResultSet rs1=ps1.executeQuery();
		%>
  
	  <html>
      <head><title>E-Learning</title>
      <META HTTP-EQUIV="refresh" content="90000;URL=InsertExam.jsp">
	  </head>
	  <body bgcolor="#000080" background='images/AdministratorBackground.jpg'>
	  <blockquote>
      <br>        
      <font face="Times New Roman" size="7" color="#FF0000">Online</font>
      <font face="Times New Roman" size="7" color="#0099FF"><i>L</i></font>
      <b><font face="Times New Roman" size="6" color="#0099FF">earning</font></b> 
	  <img src='images/title_back3.jpg' width=100% height=10>
		<br>
	  <center><font color=#66FFFF size=6 face="Times New Roman">Course Registration Form</font><br>
	  <form action='CourseRegistration' method=post>
	  <table width=80% border=10 bgcolor=#CCCCFF >	  
	   <tr>
	  <td width=40% >
	  Course
	  </td>
	  <td>	 
		
		
	 <select name="courseid">
	 <%
	 while(rs1.next())
	   {
		%>		
		<option value= "<%= rs1.getString(1)%>"><%= rs1.getString(2)%> </option>
		<%
	   }
		%>	

	  </td>
	  </tr>
	  </table>	 	  
	  <center><br><br>
	  <input type=image src=images/submit.jpg> 
	  <img src=images/back1.jpg onClick='history.back()'>
	  <img src=images/home1.jpg onClick=window.location.href='Home.jsp'>
	  </center>
	  </form>
	  </body>
	  </html>
	  
	<%
		}catch(Exception e){}
	%>