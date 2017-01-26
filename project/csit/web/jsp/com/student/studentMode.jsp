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
                        <form action="<%=application.getContextPath()%>/StudentMode" method="post">
                        <%
                         UserModel user_ = new UserModel();
                         user_.addCriteria(UserModel.TYPE, Role.STUDENT);
                         if(user_.getModelList() == null || user_.getModelList().size() < 1){
                             %>No Student<%
                             }else{
                             %><input type="hidden" name="mode" value="<%=Mode.UPDATE%>"/>
                            <table align="center"class="formTable">
                                <input type=hidden name="mode" value=<%= request.getParameter("mode")%>>
                                <tr>
                                    <td>USER NAME</td>
                                    <td>
                                        <select name="<%=UserModel.USER_NAME%>">
                                        <%
                                        UserModel user = new UserModel();
                                        user.addCriteria(UserModel.TYPE, Role.STUDENT);
                                        for(Object obj : user.getModelList()){
                                             UserModel _u = (UserModel)obj;
                                             %>
                                             <option value="<%=_u.getUserName()%>"><%=_u.getUserName()%></option>
                                             <%
                                            }
                                        %>
                                    </select>
                                    </td>
                                </tr>

                            </table>
                            <br>
                            <input type=reset value=RESET name=reset>
                            <input type=submit value="SUBMIT"><%
                             }
                        %>
                            
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