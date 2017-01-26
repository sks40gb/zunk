<html>
    <head>
        <title>Registration</title>
        <%@include file="/jsp/com/init.jsp" %>
    </head>

    <body>
        <center>
            <%
                List<UserModel> userList = (ArrayList<UserModel>) request.getAttribute("userList");
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
                            <input type="hidden" name="<%=UserModel.TYPE%>" value="<%=Role.STUDENT%>"/>
                            <table align="center" class="formTable">
                                <tr class="heading">
                                    <td>FIRST NAME</td>
                                    <td>MIDDLE NAME</td>
                                    <td>LAST NAME</td>
                                    <td>TYPE</td>                                  
                                </tr>
                                <%
                                for (UserModel usr : userList) {
                                %>
                                    <tr class="<%=Html.getCssClassName()%>">
                                        <form action="<%=application.getContextPath()%>/<%=request.getAttribute("searchOf")%>" method="post">
                                            <input type="hidden" name="mode" value="<%=Mode.UPDATE%>">
                                            <input type="hidden" name="<%=UserModel.USER_NAME%>" value="<%=usr.getUserName()%>">
                                            <td><%=usr.getFirstName()%></td>
                                            <td><%=usr.getMiddleName()%></td>
                                            <td><%=usr.getLastName()%></td>
                                            <td><%=usr.getType()%></td>
                                            <!-- <td><input type="submit" value="VIEW"></td>-->
                                        </form>
                                    </tr>
                                <%
                                }
                                %>

                            </table>
                            <br>                            
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