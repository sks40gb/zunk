<html>
    <head>
        <title>Login</title>
        <%@include file="/jsp/com/init.jsp" %>
    </head>

    <body>
        <center>

            <table class="container">
                <tr>
                    <td id="column1" colspan="2">
                        <%@include file="/jsp/com/header.jsp" %>
                    </td>
                </tr>
                <tr>
                    <td id="column2" class="column-twenty">
                        <%@include file="/jsp/com/left.jsp" %>
                    </td>
                    <td id="column3" class="column-eighty">

                        <form method="post" action="<%=application.getContextPath()%>/Login">

                            <table align="center" class="formTable">
                                <th width="100%" colspan="3">
                                    Login
                                </th>
                                <tr>
                                    <td>USERNAME</td>

                                    <td><input type="textfield" name="<%=UserModel.USER_NAME%>"></td>
                                </tr>
                                <tr>
                                    <td>PASSWORD</td>
                                    <td><input type="password" name="<%=UserModel.PASSWORD%>"></td>
                                </tr>

                                <tr>
                                    <td></td>
                                    <td><input type="submit" value="Login">&nbsp;<input type="reset"></td>
                                </tr>

                            </table>

                        </form>

                    </td>
                </tr>
                <tr>
                    <td id="column4" colspan="2">
                        <%@include file="/jsp/com/footer.jsp" %>
                    </td>
                </tr>

            </table>
        </center>
    </body>
</html>
