<%@ page language="java"%>
<%
	session.invalidate();
	response.sendRedirect("index.html");
	%>
