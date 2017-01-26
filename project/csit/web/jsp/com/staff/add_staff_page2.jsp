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
                        <% session.setAttribute("user", new UserModel(request));%>
                        <% session.setAttribute("contact", new ContactDetailModel(request));%>
                        <% session.setAttribute("address", new AddressDetailModel(request));%>
                        <form action="<%=application.getContextPath()%>/AddStaff" method="post">
                            <jsp:include page="form.jsp">
                                <jsp:param name="formTitle" value="Add Faculty"/>
                            </jsp:include>
                            <br>
                            <input type=reset value=RESET name=reset>
                            <input type=submit value=ADD>
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
