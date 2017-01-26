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

                        <form method="post" action="<%=application.getContextPath()%>/Registration">
                           
                           <!---------------------  FRIST PAGE RECORDS ------------------------------->
                            <%
                            UserModel user = (UserModel) request.getAttribute("user");
                            ContactDetailModel contact = (ContactDetailModel) request.getAttribute("contact");
                            AddressDetailModel address = (AddressDetailModel) request.getAttribute("address");
                            %>
                            <table align="center">
                                <tr>
                                    <td>POST: </td>

                                    <td><input type="textfield" name="post"></td>
                                </tr>
                                <tr>
                                    <td>PAYMENT ID : </td>
                                    <td><input type="textfield" name="payment_id"></td>
                                </tr>

                                <tr>
                                    <td></td>
                                    <td><input type="submit" value="submit">&nbsp;<input type="reset"></td>
                                </tr>

                            </table>

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
