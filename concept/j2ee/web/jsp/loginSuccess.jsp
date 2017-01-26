<%-- 
    Document   : loginSuccess
    Created on : 19 Feb, 2009, 1:19:24 PM 
    Author     : sunil
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">
 
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>PASSWORD : <%= ((sun.com.entity.User)request.getAttribute("user")).getPassword() %></title>
    </head>
    <body>
        <center>
            <table>
                <tr>
                    <td>User Name : </td>
                    <td><%= request.getParameter("userName") %></td>
                </tr> 
                <tr>
                    <td>Password : </td>
                    <td><%= request.getParameter("password") %></td>
                </tr>
            </table>

            To see all users record click <a href="<%= application.getContextPath()%>/jsp/userList.jsp">here</a>

        </center>
    </body>
</html>
