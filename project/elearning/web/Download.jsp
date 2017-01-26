<%@ page language="java"%>
<%@ page import="java.sql.*;" %>
<%
   Connection con=null;
   
   String count=(String)session.getAttribute("COUNT");
   String type=(String)session.getAttribute("TYPE");
   String userid=(String)session.getAttribute("ID");   
   if((count==null) || !count.equals("4")||userid.equals("")||userid==null)
		{
		response.sendRedirect("login.html");
		}
try{
     con=com.elearn.db.connection.ElearnConnection.getConnection();
//...............................................................................

	 PreparedStatement ps1=con.prepareStatement("select std_course_id,std_exam_status from student_record where std_id =?");  
	 ps1.setString(1,userid); 
	 ResultSet rs1=ps1.executeQuery();
	 if(rs1.next())
		{
		 String courseid=rs1.getString(1);
		 String status=rs1.getString(2);
		 if(status==null || !status.equals("yes"))
			 response.sendRedirect("NotCourseRegistrationResult.jsp");		 
//...............................................................................

		PreparedStatement ps2=con.prepareStatement("select catid from course where course_id =?");  
	     ps2.setString(1,courseid); 
		 ResultSet rs2=ps2.executeQuery();
		 rs2.next();			
			String catid=rs2.getString(1);
//...........................................................................
		 PreparedStatement ps3=con.prepareStatement("select category from course_category where catid =?");  
	     ps3.setString(1,catid); 
		 ResultSet rs3=ps3.executeQuery();
		 rs3.next();			
			String category=rs3.getString(1);
//..........................................................................
			String OddressOfZipFile="course/"+category+"/"+courseid+".zip";
				response.sendRedirect(OddressOfZipFile);			
	
		}//close if
		else
			response.sendRedirect("NotCourseRegistrationResult.jsp");

	 
	}catch(Exception e){out.print(e);}
		 %>
	



		


