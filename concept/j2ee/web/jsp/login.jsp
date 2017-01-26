<%-- 
    Document   : login
    Created on : 19 Feb, 2009, 12:22:11 PM
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
        <center> 
            <font size="6" color="green">Login</font>
        <form method="post" action="login.do">
            <table>
                <tr>
                    <td>User Name</td>
                    <td><input type="text" name="userName"></td>
                </tr>
                <tr>
                    <td>Password </td>
                    <td><input type="password" name="password"/></td>
                </tr>
            </table>

            <input type="submit" value="submit"/>
            <input type="reset" value="reset"/>
        </form>
        </center>
    </body>
</html>
