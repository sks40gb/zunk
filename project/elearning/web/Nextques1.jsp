<%@ page language="java"%>
<%@ page import="java.sql.*;" %>
<%
	
//..............................................................................
  String count=(String)session.getAttribute("COUNT");
  String type=(String)session.getAttribute("TYPE");
  if((count==null) || !count.equals("4"))
	{
	response.sendRedirect("login.html");
	}
//..............................................................................

   PreparedStatement ps2=null;
   Connection con=null;
   ResultSet rs2=null;
    String s="";
	String s1="";
	String s2="";
	String s3="";
	String s4="";
	String ans="";
	String queid="";
	int n=0;
   try{
   
   con=com.elearn.db.connection.ElearnConnection.getConnection();
   ps2=con.prepareStatement("select question_desc,option1,option2,option3,option4,question_id from examination where question_id=?",ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_READ_ONLY);
   
   // n=Integer.parseInt((String)session.getAttribute("NN"));
    String userid=(String)session.getAttribute("ID");        

    String QID=request.getParameter("R1");

    ps2.setInt(1,Integer.parseInt(QID));
    rs2=ps2.executeQuery();  
	rs2.next();
	s=rs2.getString(1);
	s1=rs2.getString(2);
	s2=rs2.getString(3);
	s3=rs2.getString(4);
	s4=rs2.getString(5);
	queid=rs2.getString(6);
	con.close();
  }
  catch(java.lang.NumberFormatException nfe)
	{
		%>
		<script>
		alert("select one question or quit Exam")
		history.back()
		</script>
		<%
	}
  catch(Exception e){out.println("Error------"+e);} 
%>

	  <html>
      <head><title>E-Learning</title>   	  
		
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
      <font face="Times New Roman" size="7" color="#FF0000">Online</font>
      <font face="Times New Roman" size="7" color="#0099FF"><i>L</i></font>
      <b><font face="Times New Roman" size="6" color="#0099FF">earning</font></b>    
      <hr>	  	  
	  <form name=form1 action='InsertExam1.jsp'>
	  <table width=88% border=0 align=center valign=top height=250 bgcolor=#CCCCFF>
	  <thead>
	  <tr><th></th>	  
	  <th colspan=4 height=60 align=left>Q.  <%= s%>
	  </th></tr></thead> <tbody>
	  <tr>
	  <td width=15%></td>
	  <td><input type=radio name=R value=<%=s1%>></td>
	  <td width=80% height=40>[A] <%=s1%> </td>
	  </tr>
	  <tr>
	  <td></td>
	  <td><input type=radio name=R value=<%=s2%>></td>
	  <td width=86% height=40>[B] <%=s2%> </td>
	  </tr>
	  <tr>
	  <td></td>
	  <td><input type=radio name=R value=<%=s3%>></td>
	  <td width=86% height=40>[C] <%=s3%> </td>
	  </tr>
	  <tr>
	  <td></td>
	  <td><input type=radio name=R value=<%=s4%>></td>
	  <td width=86% height=40>[D] <%=s4%> </td>
	  </tr>	
	  <tr><input type=hidden name=queid value=<%=queid%>>
	  <tr></tr>
	  <td></td><td></td>
	  <td><input type=image src='images/go.jpg'>
	  &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
	  <img src='images/reset.jpg' onClick='window.form1.reset()'>
	   &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
	  <img src=images/quitexam.jpg onClick="window.location.href='ExamResult.jsp'">
	  </td>
	  </tr><tr></tr>
	  </tbody>
	  </table></form><hr>
	  <script>
		  function banner()
		  {
		  window.form1.submit()
		  }
	      id=setTimeout("banner()",1000*10);  // 10 seconds
	 </script>
	 </body>
	 </html>
<%
/*int col=rs2.getRow();
n++;
if(col==0)
	n=1;
String ss=""+n+"";
session.setAttribute("NN",ss);*/
%>



