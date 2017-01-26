<html>
    <head>
        <title>Registration</title>
        <%@include file="/jsp/com/init.jsp" %>
         <%@page import="com.csit.alert.*" %>
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

                        <div class="exception">                           
                            <ul>

                            <%
                             ErrorColl  errorObject = request.getAttribute(Attribute.ERROR) == null ? new ErrorColl() : (ErrorColl)request.getAttribute(Attribute.ERROR);
                             List<String> errorList = errorObject.getErrorList();

                            for(String error : errorList){
                              %>
                              <li><%= error%></li>
                              <%
                                }

                            %>
                            </ul>

                            <%=errorObject.getErrorMessage()%>
                            <%=errorObject.getException()%>
                            ${pageContext.exception}

                        </div>
                        <FORM><INPUT TYPE="button" VALUE="Back" onClick="history.go(-1);return true;"></FORM>
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

