<html>
    <head>
        <title>Courses</title>
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
                        <table  class="formTable" width="80%">
                         <th colspan="3">
                             Course : <%= request.getParameter("name") %>
                        </th>
                        <tr class="heading">
                            <td width="40%">SUBJECT</td>
                            <td width="60%">DESCRIPTION</td>
                        </tr>
                        
                            <%
                            SubjectModel subject = new SubjectModel();
                            subject.addCriteria(CourseModel.ID, request.getParameter("courseId"));
                            Html html = new Html();
                            for(Object _subject : subject.getModelList()){
                                SubjectModel s = (SubjectModel)_subject;
                                %>
                                <tr style ="background-color:<%= html.getRandomColor()%>">
                                    <td><%=s.getName()%></td>
                                    <td><%=s.getDescription()%></td>
                                 </tr>
                                <%
                             }
                            %>
                         </table>
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
