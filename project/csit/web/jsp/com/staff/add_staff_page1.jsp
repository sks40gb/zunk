<html>
    <head>
        <title>Add Staff</title>
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

                        <form action="<%=application.getContextPath() + JspPage.STAFF_ADD_PAGE_2%>" method="post">
                            <%
                                session.removeAttribute("user");
                                session.removeAttribute("contact");
                                session.removeAttribute("address");
                            %>
                            <input type="hidden" name="<%=UserModel.TYPE%>" value=" STAFF"/>
                            <jsp:include page="/jsp/com/user/form.jsp">
                                <jsp:param name="formTitle" value="Add Faculty"/>
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
