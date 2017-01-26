<html>
    <head>
        <title>Update Student</title>
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

                        <form action="<%=application.getContextPath()%>/UpdateStudentPage1" method="post">
                             <input type="hidden" name="<%=UserModel.TYPE%>" value="<%=Role.STUDENT%>"/>
                            <jsp:include page="/jsp/com/user/form.jsp">
                                <jsp:param name="formTitle" value="Update Student"/>
                            </jsp:include>
                            <br>
                            <input type=reset value=RESET name=reset>
                            <input type=submit value=NEXT>
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
