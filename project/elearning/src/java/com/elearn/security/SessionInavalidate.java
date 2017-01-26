package com.elearn.security;

import javax.servlet.http.*;
import javax.servlet.*;

public class SessionInavalidate  extends HttpServlet
{		  

	public  void doPost(HttpServletRequest req, HttpServletResponse res)
	{
		try
		{
		HttpSession ses=req.getSession();	
		ses.invalidate();
		res.sendRedirect("index.html");
		}
		catch(Exception e)
		{}
	}
}



