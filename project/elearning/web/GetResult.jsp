    <%@ page language="java"%>
	<%@ page import="java.sql.*;" %>
	<%
   String count=(String)session.getAttribute("COUNT");
   String type=(String)session.getAttribute("TYPE");
   String userid=(String)session.getAttribute("ID");   
   if((count==null) || !count.equals("4")||userid.equals("")||userid==null)
			response.sendRedirect("index.html");
   int i=1;



   PreparedStatement ps=null;
   Connection con=null;
   try{   
   con=com.elearn.db.connection.ElearnConnection.getConnection();
//...........................................................................

PreparedStatement ps5=con.prepareStatement("select std_exam_status from student_record where std_id=?");
ps5.setString(1,userid);
ResultSet rs5=ps5.executeQuery();
rs5.next();
if(!((rs5.getString(1)).equals("Appeared")))
	 response.sendRedirect("NotAppearedInExam.jsp");
	
//............................................................................
   String QID="";
   String StudentAnswer="";
   String ExamAnswer="";
   String status="";
   PreparedStatement ps1=con.prepareStatement("select question_id,std_ans,exam_ans,status from std_result_info where std_id=?");
   ps1.setString(1,userid);
   ResultSet rs1=ps1.executeQuery();

   int result=0;
   String courseid="";
   PreparedStatement ps3=con.prepareStatement("select std_exam_result,std_course_id from student_record where std_id=?");
   ps3.setString(1,userid);
   ResultSet rs3=ps3.executeQuery();
   rs3.next();
   result=rs3.getInt(1);
   courseid=rs3.getString(2);

  int TotalMarks=0;
  PreparedStatement ps4=con.prepareStatement("select exam_que_numbers from course where course_id=?");
   ps4.setString(1,courseid);
   ResultSet rs4=ps4.executeQuery();
   rs4.next();
   TotalMarks=rs4.getInt(1);
   TotalMarks=TotalMarks*3;

//........................................................................
   %>
       <html>
      <head><title>E-Learning</title>
      <META HTTP-EQUIV="refresh" content="90000;URL=InsertExam.jsp">
		
       <script>
		function bool()
		{
		history.forward();
		}
	 </script>

	  </head>
	  <body bgcolor="#000080" onload="bool()">
	  <blockquote>
      <br>        
      <font face="Times New Roman" size="7" color="#FF0000">E</font>
      <font face="Times New Roman" size="7" color="#0099FF"><i>-L</i></font>
      <b><font face="Times New Roman" size="6" color="#0099FF">earning</font></b> 
	  <hr width=100% size=6  color=#D0CCF4>
	  <br><br>	 

	  <table width=90% border=0  height=140 bgcolor=#CCCCFF>
	  <thead>


	  <tr><th valign=top colspan=3>
	  <img src='images/title_back3.jpg' width=100% height=5>
	  </td></tr>
	  <tr><th align=center colspan=3>
	  <font color=#663399 size=6 face="Times New Roman">Result Of </font>
	  <font color=#FF0000 size=6 face="Times New Roman"><%=userid%></td></tr>
	  <tr>
	  <th colspan=3>
	  <tr>
		<th></th>
		<th colspan=3 align=right>Marks Obatained :<font color=#CC0033 size=4><%=result%></font><font size=4>/</font><font color=#CC0033 size=4><%=TotalMarks%></font></th>
	  </tr>
	  <tr>
	  <th colspan=3>
	  <img src='images/title_back3.jpg' width=100% height=5>
	  </th>
	  </tr>  
	  <tr>
	  <th align="left"><font color=#881D11 size=4>
	  Question
	  </th>
	  <th><font color=#881D11 size=4 align=center>
	  Your Ans
	  </th>
	   <th><font color=#881D11 size=4 align=center>
	  Status
	  </th>
	  <tr>
	  <tr>
	  <th colspan=3><hr>
	  </th>
	  <tr>
	  </thead>
	  <tbody>
   <%

//...........................................................................

   while(rs1.next())
	   {		
	    QID=rs1.getString(1);
		StudentAnswer=rs1.getString(2);
		ExamAnswer=rs1.getString(3);
		status=rs1.getString(4);				

		PreparedStatement ps2=con.prepareStatement("select question_desc from examination where question_id=?");
		ps2.setString(1,QID);
		ResultSet rs2=ps2.executeQuery();
		rs2.next();
		String QueDescription=rs2.getString(1);

		String WrongOrRight="";
		if(status.equals("Attempt"))
		   {
			if(StudentAnswer.equals(ExamAnswer))
				WrongOrRight="Right";
			else
				WrongOrRight="Wrong";
		   }
		 else
			 WrongOrRight=" <font color=#009900 size=4 align=center>NotAttempt</font>";			 
		//..........................................................................
		%>

	  <tr>
	  <td align=left>	  
	  <font color=#FF3333 size=4><b>Q.<%=i%> </b></font><font color=#663399 size=4 face="Times New Roman" ><%=QueDescription%></font>
	  </td>	  
	  <td valign=bottom width=15% align=center>
	  <font color=#663399 size=4 face="Times New Roman"><%=StudentAnswer%></font>
	  </td>
	  <td valign=bottom align=center>
	  <font color=#663399 size=4 face="Times New Roman" align=center><%=WrongOrRight%></font>
	  </td>
	  </tr>
	  <tr>
	  <th colspan=3><hr>
	  </th>
	  </tr>
	  
<%    i++;	
	   }
    }catch(Exception e)//{response.sendRedirect("NotCourseRegistrationResult.jsp");}
					   {}//out.print(e);
//...........................................................................
	%>

	 <tr>
	   <th valign=bottom colspan=3>
	  <img src='images/title_back3.jpg'  height=5 width=100%>
	  </th>
	
	  </tr>
	  </table>	
	  <form action='SessionInavalidate' method=post>
	  <center><br>
	  <input type=image src=images/logout1.jpg> 
	  <img src=images/home1.jpg onClick=window.location.href='Home.jsp'>
	  </center>
	  </form>
	  </body>
	  </html>


