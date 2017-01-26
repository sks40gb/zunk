
<html>
    <head>
        <title>Course</title>
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
                        <form action="<%=application.getContextPath()%>/CourseMode" method="post">
                            <input type="hidden" name="<%=UserModel.TYPE%>" value="<%=Role.ADMIN%>"/>
                            <table align="center"  class="formTable">
                                <th colspan="3">
                                    Select Course
                                </th>
                                <input type=hidden name="mode" value=<%= request.getParameter("mode")%>>
                                <tr><td>COURSE</td>
                                    <td>
                                        <select name="<%=CourseModel.ID%>">
                                            <%
                                            List<CourseModel> courseList = new CourseModel().getModelList();
                                            for (CourseModel course : courseList) {
                                            %>
                                            <option value="<%=course.getCourseId()%>"><%=course.getName()%></option>
                                            <%
                                              }
                                            %>
                                        </select>
                                    </td>
                                </tr>

                            </table>
                            <br>
                            <input type=reset value=RESET name=reset>
                            <input type=submit value=<%= mode%>>
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




