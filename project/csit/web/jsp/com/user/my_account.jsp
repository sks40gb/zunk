<html>
    <head>
        <title>My Account</title>
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
                    <%
                        UserModel user = (UserModel)session.getAttribute(Attribute.CURRENT_USER);

                        ContactDetailModel contact = new ContactDetailModel();
                        contact.addCriteria(UserModel.ID, user.getUserId());
                        contact.getModel();

                        AddressDetailModel address = new AddressDetailModel();
                        address.addCriteria(ContactDetailModel.ID, contact.getContactDetailsId());
                        address.getModel();

                        request.setAttribute("user", user);
                        request.setAttribute("contact", contact);
                        request.setAttribute("address", address);
                    %>

                        <form action="<%=application.getContextPath()%>/UpdateMyAccount" method="post">
                            <jsp:include page="/jsp/com/user/form.jsp">
                                <jsp:param name="formTitle" value="My Account"/>
                                <jsp:param name="readonly" value="readonly"/>
                            </jsp:include>
                            <input type="hidden" name="<%=UserModel.TYPE%>" value="${user.type}"/>
                            <br>
                            <input type=reset value=RESET name=reset>
                            <input type=submit value="UPDATE ACCOUNT">
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
