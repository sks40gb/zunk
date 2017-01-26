<html>
    <head>
        <title>Display-Comments</title>
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
                        <center>
                                <%
                                Html html = new Html();
                                CommentModel c = new CommentModel();
                                //  c.addCriteria("subject", "suni");
                                List<CommentModel> commentList = c.getModelList();
                                for (CommentModel comment : commentList) {
                                     UserModel __user = new UserModel();
                                    __user.setUserId(comment.getUserId());
                                    __user.getModel();
                               %>
                                  <table align="center" style="background-color:<%=html.getRandomColor()%>"  class="formTable">
                                <tr>
                                    <td colspan="2"><div class="heading">COMMENT BY : <%=__user.getUserName()%><div style="float:right" class="heading"><%=DateFormatter.convertDateToString(comment.getCommentDate())%></div></div></td>
                                </tr>                                
                                <tr>
                                    <td>SUBJECT</td>
                                    <td><input type=text value="<%=comment.getSubject()%>" disabled></td>
                                </tr>
                                <tr>
                                    <td>COMMENT</td>
                                    <td>
                                        <TEXTAREA value="" rows="5" cols="50" disabled><%=comment.getComment()%></TEXTAREA>
                                    </td>
                                </tr>
                            </table>
                                <% } 
                             %>
                        </center>
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
