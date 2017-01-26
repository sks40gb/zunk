<%-- 
    Document   : login
    Created on : Jul 18, 2012, 3:49:08 PM
    Author     : Sunil
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>JSP Page</title>
    </head>
    <body>
        <H3 style="color: red">${message}</H3>
        <form action="login.action" method="post">
            <table>
                <tr>
                    <td>User Name</td>
                    <td><input name="userName"/></td>
                </tr>
                <tr>
                    <td>Password</td>
                    <td><input name="password"/></td>
                </tr>
                <tr>
                    <td>&nbsp;</td>
                    <td><input type="submit" value="Login"/></td>
                </tr>    
            </table>
        </form>
    </body>
</html>
