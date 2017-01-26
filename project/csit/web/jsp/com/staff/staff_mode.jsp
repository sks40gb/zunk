<html>
    <head>
        <title>Registration</title>
        <%@include file="/jsp/com/init.jsp" %>
    </head>

    <body>
        <center>
            <%            
                session.setAttribute("mode", Mode.UPDATE);
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
                        <form action="<%=application.getContextPath() + JspPage.STAFF_UPDATE_PAGE_1%>" method="post">
                            <input type="hidden" name="mode" value="<%=Mode.UPDATE%>"/>
                            <table align="center"class="formTable">
                                <input type=hidden name="mode" value=<%= request.getParameter("mode")%>>
                                <tr>
                                    <td>USER NAME</td>
                                    <td>
                                        <select name="<%=UserModel.ID%>">
                                        <%
                                        UserModel user = new UserModel();
                                        user.addCriteria(UserModel.TYPE, Role.STAFF);
                                        for(Object obj : user.getModelList()){
                                             UserModel _u = (UserModel)obj;
                                             %>
                                             <option value="<%=_u.getUserId()%>"><%=_u.getUserName()%></option>
                                             <%
                                            }
                                        %>
                                    </select>
                                    </td>
                                </tr>

                            </table>
                            <br>
                            <input type=reset value=RESET name=reset>
                            <input type=submit value="SUBMIT">
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