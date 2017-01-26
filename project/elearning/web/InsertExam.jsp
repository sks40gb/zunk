<%@ page language="java"%>
<%@ page import="java.sql.*;" %>
<%
 String answer="";
   try{
   
   Connection con=com.elearn.db.connection.ElearnConnection.getConnection();
   PreparedStatement ps1=con.prepareStatement("insert into std_result_info values(?,?,?,?,?)");
 
	 
   String UserAns=request.getParameter("R");
   if(UserAns==null)
	   UserAns="";
   if(request.getParameter("queid")==null)
	   response.sendRedirect("Nextques.jsp");

   ps1.setString(1,request.getParameter("queid"));
   ps1.setString(2,(String)session.getAttribute("ID"));
   ps1.setString(3,UserAns);	
   
   PreparedStatement ps2=con.prepareStatement("select answer from examination where question_id=?");
   ps2.setString(1,request.getParameter("queid"));
  
   ResultSet rs2=ps2.executeQuery();
   while(rs2.next())
	   {
	   answer=rs2.getString(1);
	   }
   ps1.setString(4,answer);


   if((request.getParameter("R"))==null||(request.getParameter("R")).equals(""))
	   ps1.setString(5,"NotAttempt");
   else
	   ps1.setString(5,"Attempt");	

   int x=ps1.executeUpdate();

   con.close();
	

	 //......................................................................
    Class.forName("sun.jdbc.odbc.JdbcOdbcDriver");
    con=DriverManager.getConnection("jdbc:odbc:elearn","scott","tiger");
    int check=0;
	PreparedStatement ps6=con.prepareStatement("select status from std_result_info  where std_id=? and status='Attempt'");
	ps6.setString(1,(String)session.getAttribute("ID"));
	ResultSet rs6=ps6.executeQuery();
	while(rs6.next())
		{
		 check++;
		}
	if(check>=(Integer.parseInt((String)session.getAttribute("NO_OF_QUES"))))
	   {%>
	   <script>alert("......Exam completed.......")</script>
	   <%
	    response.sendRedirect("ExamResult.jsp");
	   }
	 con.close();
  //..........................................................................




   if(Integer.parseInt((String)session.getAttribute("N"))>(Integer.parseInt((String)session.getAttribute("NO_OF_QUES"))))//.........no. of Questions
	   throw new java.sql.SQLException();

   response.sendRedirect("Nextques.jsp");

    }catch(java.sql.SQLException e)
		  {%>
		   
		  <html>
		  <head><title>E-Learning</title>
		  <META HTTP-EQUIV="refresh" content="18000;URL=InsertExam.jsp">

	 <script>
		function bool()
		{
		history.forward();
		}
	 </script>

		  </head>
		  <body bgcolor="#000080" onload="">
	 
		  <br>        
		  <font face="Times New Roman" size="7" color="#FF0000">E</font>
	      <font face="Times New Roman" size="7" color="#0099FF"><i>-L</i></font>
		  <b><font face="Times New Roman" size="6" color="#0099FF">earning</font></b>    
		  <hr>	  	  
		  <form name=form2 action=Nextques1.jsp>
		  <h2><center><font color=#FF9933>Following Question will again be repeated<h2><center>
		  <table width=100% border=1 align=center valign=top  bgcolor=#CCCCFF>
	  
          <%try{
				
				Class.forName("sun.jdbc.odbc.JdbcOdbcDriver");
				Connection con=DriverManager.getConnection("jdbc:odbc:elearn","scott","tiger");		
				

				PreparedStatement ps5=con.prepareStatement("select question_id from std_result_info  where status=?");
				ps5.setString(1,"NotAttempt");
				ResultSet rs5=ps5.executeQuery();
				while(rs5.next())
					{
					 String QID=rs5.getString(1);
					 PreparedStatement ps4=con.prepareStatement("select question_id,question_desc from examination where question_id=?");
					 ps4.setString(1,QID);
					 ResultSet rs4=ps4.executeQuery();
					 while(rs4.next())			  
						{%>
						<tr><td height=25><input type=radio name=R1 value=<%=rs4.getString(1)%>>&nbsp;&nbsp;<%=rs4.getString(2)%></td></tr>
						<%}
					 }
				%>
				<tr><td><input type=image src=images/go.jpg>&nbsp;&nbsp;
				<img src=images/reset.jpg onClick='window.form2.reset()'>
				<img src=images/quitexam.jpg onClick="window.location.href='ExamResult.jsp'">
				</table>
				</form>
				<%
				con.close();
				//session.setAttribute("NN","1");
		       } //second catch closed
		  catch(Exception ee){out.print("<h1>"+ee);}
		  }
  
%>

