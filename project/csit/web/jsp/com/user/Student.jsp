<html>
    <head>
        <title>Registration</title>
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

                    <!---------------------  FIRST PAGE RECORDS ------------------------------->                                  
                    <form action="<%=application.getContextPath()%>/Registration" method="post">
                        <table align="center">
                            <tr>

                                <td>ENROLLMENT NUMBER</td>
                                <td><input type="text"  size="25" name="enrollmentnumber"></td>
                            </tr>
                            <tr>
                                <td></td>
                                <td><input type="submit" value="submit">&nbsp;<input type="reset"></td>
                            </tr>
                            <tr>
                        </table>
                    </form>

                </td>
                <tr>

                    <td id="column4" colspan="2">
                        <%@include file="/jsp/com/footer.jsp" %>
                    </td>
                </tr>

            </table>
        </center>
    </body>
</html>

