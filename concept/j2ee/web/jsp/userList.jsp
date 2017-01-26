<%-- 
    Document   : BeanExamples
    Created on : 20 Feb, 2009, 12:45:17 PM
 import sun.com.list.Users;
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
        <style>
            sun{
                font:12px red;
                color:white;
                padding:2px;
                border:solid 1px red;
                background-image:url('<%=application.getContextPath()%>/images/background/bg.jpeg');
            }
        </style>
        <%@page import="sun.com.list.*,sun.com.entity.User,java.util.List,java.util.ArrayList" %>

        Current UserName : <jsp:getProperty name="user" property="firstName"/>

        <%
            for (User user : Users.getUsers()) {
        %>
        <hr>
        <sun>UserName</sun> : <%=user.getFirstName()%><br>
        <sun>Password</sun> : <%=user.getPassword()%><br>
        <%
            }
        %>

        <hr>
        <%=application.getContextPath()%>/images/background/bg.jpeg'
        <a href="<%= application.getContextPath()%>/jsp/login.jsp">Login here</a>
    </body>
</html>
