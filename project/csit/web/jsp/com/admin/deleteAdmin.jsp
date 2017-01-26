<html>
    <head>
        <title>Delete Admin</title>
        <%@include file="/jsp/com/init.jsp" %>
    </head>

    <body>
        <center>
            <%
            String mode = request.getParameter("mode");
            if (mode == null) {
            mode = Mode.ADD;
            }
            session.setAttribute("mode", mode);
            %>
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
                        <form action="<%=application.getContextPath()%>/DeleteAdmin" method="post">
                            <input type="hidden" name="<%=UserModel.TYPE%>" value="<%=Role.ADMIN%>"/>
                            <table align="center"  class="formTable">
                                <input type=hidden name="mode" value=<%= request.getParameter("mode")%>>
                                <tr>
                                    <td>USER NAME</td>
                                    <td><input type=text name="<%=UserModel.USER_NAME%>"></td>
                                </tr>

                            </table>
                            <br>
                            <input type=reset value=RESET name=reset>
                            <input type=submit value="DELETE">
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