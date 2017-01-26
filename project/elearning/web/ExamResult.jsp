    <%@ page language="java"%>
	<%@ page import="java.sql.*;" %>
	<%
	  String userid=(String)session.getAttribute("ID");
	  if(userid.equals("")||userid==null)
			response.sendRedirect("index.html");
	  
   //........................update result....................................


   PreparedStatement ps=null;
   Connection con=null;
   try{     
   con=com.elearn.db.connection.ElearnConnection.getConnection();
   PreparedStatement ps1=con.prepareStatement("select question_id,std_ans,exam_ans,status from std_result_info where std_id=?");
   ps1.setString(1,userid);
   ResultSet rs1=ps1.executeQuery();
	
   int result=0;
   while(rs1.next())
	   {		
	    String StudentAnswer=rs1.getString(2);
		String ExamAnswer=rs1.getString(3);
		String status=rs1.getString(4);
		if(status.equals("Attempt"))
			{
			if(StudentAnswer.equals(ExamAnswer))
				result=result+3;
			else
				result=result-1;			
			}
	   }
	
	con.close();
	
	
    Class.forName("sun.jdbc.odbc.JdbcOdbcDriver");
    con=DriverManager.getConnection("jdbc:odbc:elearn","scott","tiger");
	PreparedStatement ps2=con.prepareStatement("update student_record set std_exam_result=?  where std_id=?");
	ps2.setInt(1,result);
	ps2.setString(2,userid);
	int x=ps2.executeUpdate();

   }catch(Exception e){out.print(e);}


	//................................................................................



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
	  <hr width=630 size=6  color=#D0CCF4>
	  <br><br>
      <hr width=635>		  	  
	  <table width=630 border=0 align=center valign=top height=140 bgcolor=#CCCCFF>
	  <tr><td valign=top>
	  <img src='images/title_back3.jpg' width=630 height=17>
	  </td></tr>
	  <tr>
	  <td align=center>
	  <font color=#663399 size=100 face="Times New Roman">Examination Completed</font>
	  </td>
	  </tr>
	  <tr><td valign=bottom>
	  <img src='images/title_back3.jpg' width=630 height=17>
	  </td>
	  </tr>
	  </table>
	  <hr width=635>
	  <form action=SessionInavalidate method=post>
	  <center><br>
	  <input type=image src='images/logout1.jpg'> 
	  <img src=images/getresult.jpg onClick=window.location.href='GetResult.jsp'>
	  <img src=images/home1.jpg onClick=window.location.href='Home.jsp'>
	  </center>
	  </form>
	  </body>
	  </html>