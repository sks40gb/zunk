package com.elearn.ui.admin;

import javax.servlet.http.*;
import java.io.PrintWriter;
public class AddAdmin extends HttpServlet
{
	public void doGet(HttpServletRequest req,HttpServletResponse res) 
	{		
		try{
			res.setContentType("text/html");
			PrintWriter pw = res.getWriter();
			pw.print("<h1>sunil kumar singh</h1>");	
		}catch(Exception e){}
	}
}