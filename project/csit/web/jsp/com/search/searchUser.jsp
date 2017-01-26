<html>
    <head>
        <title>Registration</title>
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
                        <form action="<%=application.getContextPath()%>/Search" method="post">
                            <table align="center"  class="formTable">
                                <input type=hidden name="all" value=<%= request.getParameter("all")%>>
                                <tr>
                                    <td>FIRST NAME</td>
                                    <td><input type=text  name="<%=Search.FIRST_NAME%>"></td>
                                </tr>
                                <tr>
                                    <td>LAST NAME</td>
                                    <td><input type=text  name="<%=Search.LAST_NAME%>"></td>
                                </tr>
                                <tr>
                                    <td>TYPE</td>
                                    <td>
                                        <select name="<%=Search.USER_TYPE%>">
                                            <option><%=Role.ADMIN%>
                                            <option><%=Role.STAFF%>
                                            <option><%=Role.STUDENT%>
                                            <option><%=Role.USER%>
                                            <option>ALL
                                        </select>
                                    </td>
                                </tr>
                            </table>
                            <br>
                            <input type=reset value=RESET name=reset>
                            <input type=submit value="SEARCH">
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