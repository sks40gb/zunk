<%-- 
    Document   : index
    Created on : 19 Feb, 2009, 12:05:29 PM
    Author     : sunil
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>JSP Page</title>
    </head>
    <body>
        <h1>Hello World!</h1><br>
        <h2><a href="<%= application.getContextPath()%>/jsp/login.jsp" >Login</a></h2><br>
        <h2><a href="<%= application.getContextPath()%>/jsp/ajax/ajaxSample.jsp" >Ajax</a></h2><br>
        <h2><a href="<%= application.getContextPath()%>/jsp/progressBar.jsp" >Progress Bar</a></h2><br>
        <h2><a href="<%= application.getContextPath()%>/jsp/parent.jsp" >Inlude File</a></h2><br>
    </body>
</html>
