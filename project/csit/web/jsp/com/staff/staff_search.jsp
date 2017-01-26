<html>
    <head>
        <title>Registration</title>
        <%@include file="/jsp/com/init.jsp" %>
    </head>

    <body>
        <center>
            <%
                UserModel user = new UserModel();
                user.addCriteria(UserModel.TYPE, Role.STAFF);
                List<UserModel> userList = (ArrayList<UserModel>) user.getModelList();
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
                                    <td>USER NAME</td>
                                    <td>FIRST NAME</td>
                                    <td>MIDDLE NAME</td>
                                    <td>LAST NAME</td>
                                    <td>TYPE</td>
                                    <td></td> 
                                </tr>
                                <%
                                for (UserModel usr : userList) {
                                    StaffModel staff = new StaffModel();
                                    staff.addCriteria(StaffModel.USER_ID, usr.getUserId());
                                    staff.getModel();
                                %>
                                    <tr class="<%=Html.getCssClassName()%>">
                                        <form action="<%=application.getContextPath() + JspPage.STAFF_UPDATE_PAGE_1%>" method="post">
                                            <input type="hidden" name="mode" value="<%=Mode.UPDATE%>">
                                            <input type="hidden" name="<%=UserModel.ID%>" value="<%=usr.getUserId()%>">
                                            <td><%=usr.getUserName()%></td>
                                            <td><%=usr.getFirstName()%></td>
                                            <td><%=usr.getMiddleName()%></td>
                                            <td><%=usr.getLastName()%></td>
                                            <td><%=staff.getType()%></td>
                                            <td><input type="submit" value="UPDATE  "></td>
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