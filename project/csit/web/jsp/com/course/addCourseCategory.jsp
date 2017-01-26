<html>
    <head>
        <title>Add Course Category</title>
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

                        <form action="<%=application.getContextPath()%>/AddCourseCategory" method="post">
                            <table align="center"  class="formTable">
                                <th colspan="3">
                                    Add Course Category
                                </th>
                                <tr>
                                    <input type="hidden" name="<%=CourseCategoryModel.ID%>"/>
                                    <td>CATEGORY</td>
                                    <td><input type=text name="<%=CourseCategoryModel.NAME%>"></td>
                                </tr>
                            </table>
                            <input type=reset value=RESET name=reset>
                            <input type=submit value="ADD">
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