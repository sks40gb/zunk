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
   PreparedStatement ps1=con.prepareStatement("select std_exam_status from student_record where std_id=?");
   ps1.setString(1,userid);
   ResultSet rs1=ps1.executeQuery();
   String status="";
   while(rs1.next())
	   {
	   status=rs1.getString(1);
	   }

   if(status==null|| !status.equals("yes"))
	   {
		%>
		<html>
     <head><title>E-Learning</title>	
	 
	 </head>
	 <body bgcolor="#EFDCDF">
      <br>        
      <font face="Times New Roman" size="7" color="#FF0000">E</font>
      <font face="Times New Roman" size="7" color="#0099FF"><i>-L</i></font>
      <b><font face="Times New Roman" size="6" color="#0099FF">earning</font></b>    
      <hr>
	  <form action=SessionInavalidate method=post>
      <table width=100% background='images/AdministratorBackground.jpg' height=73%>	
	  <td width=10%></td><td>	
		<table width=100% bgcolor=#C1C1DD border=1 align=center><td align=center><tr>		<td valign=middle width=100% align=center height=50><font color=#333399><h2>You Have Not Registered For Exam</td>
      </table></td><td width=10%></td><tr><td><td align=center valign=top>
      <img src=images/back.jpg onClick=history.back()>
      <input type=image src=images/logout.jpg>      
      </table><br><br></form></body>	

	  <script>
		alert("You Have Not Registered For Exam");
		window.location.href='Home.jsp'
		</script>
      </html>		
		<%
		//response.sendRedirect("login.html");
	   }	
else{
	%>
	<html>
     <head><title>Invalid User</title>
	  <script>
		function bool()
		{
		history.forward();
		}
	 </script>
	 </head>

<body bgcolor="#EFDCDF" onload="bool()">
      <br>        
      <font face="Times New Roman" size="7" color="#FF0000">E</font>
      <font face="Times New Roman" size="7" color="#0099FF"><i>-L</i></font>
      <b><font face="Times New Roman" size="6" color="#0099FF">earning</font></b>    
      <hr>
      <br>
      <br>
      <b><font face="Times New Roman" size="5" color="#FF0000"><center>
          Welcome to E-Learning Online Exam.To start exam</b></font>
      <b><a href="Nextques.jsp">Click Here</a></b>
  <br>
  <br>
	<%
  /* java.util.Date dt=new java.util.Date();
   long LDate = dt.getTime();
   java.sql.Date sqldate=new java.sql.Date(LDate);*/
   ps=con.prepareStatement("update student_record set std_exam_status='Appeared',std_exam_date=sysdate where std_id=?");
   //ps.setDate(1,sqldate);     
   ps.setString(1,userid);
   ps.executeUpdate();
   session.setAttribute("N","1"); //................
	
   String courseid="";
   PreparedStatement ps3=con.prepareStatement("select std_course_id from student_record where std_id=?");
   ps3.setString(1,userid);
   ResultSet rs3=ps3.executeQuery();
   while(rs3.next())
	 {
	 courseid=rs3.getString(1);
	 }
   
   PreparedStatement ps4=con.prepareStatement("select exam_que_numbers from course where course_id =?");
   ps4.setString(1,courseid);
   ResultSet rs4=ps4.executeQuery();
   while(rs4.next())
	  {
	  String NO_OF_QUES=rs4.getString(1);
      session.setAttribute("NO_OF_QUES",NO_OF_QUES);		   
	  }
  }//close else
	con.close(); 
  }catch(Exception e){
					//out.println("<h1>"+e);
					} 
%>
</center>
</body>          
</html>