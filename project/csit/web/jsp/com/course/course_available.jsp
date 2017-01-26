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
                        <table  class="formTable">
                        <tr class="heading">
                            <td>COURSE</td>
                            <td>DURATION</td>
                            <td>MINIMUM ELIGIBILITY</td>
                            <td>SEATS</td
                            <td> FEE</td>
                            <td>ACTION</td>
                        </tr>

                        <% List<CourseCategoryModel> categoryList = new CourseCategoryModel().getModelList();
                        for(CourseCategoryModel category : categoryList){
                           %> <tr class="sub-title"><td colspan="5" align="center"><%=category.getName()%></td></tr> <%

                                CourseCategoryModel cat = new CourseCategoryModel();
                                CourseModel _course = new CourseModel();
                                _course.addCriteria(CourseModel.COURSE_CATEGORY_ID, category.getCourseCategoryId());
                                for(Object c : _course.getModelList()){
                                    CourseModel course = (CourseModel)c;
                                    %>
                                    <form action="<%=getServletContext().getContextPath() + com.csit.jsp.JspPage.COURSE_DETAILS%>">
                                        <input type="hidden" value="<%=course.getCourseId()%>" name="<%=CourseModel.ID%>"/>
                                        <input type="hidden" value="<%=course.getName()%>" name="<%=CourseModel.COURSE%>"/>
                                        <tr class="<%=Html.getCssClassName()%>">
                                            <td><%=course.getName()%></td>
                                            <td><%=course.getDuration()%></td>
                                            <td><%=course.getEligibility()%></td>
                                            <td><%=course.getSeat()%></td>
                                            <td><%=course.getCourseFee()%></td>
                                            <td><input type="submit" value="show detail"> </td>
                                        </tr>
                                    </form>
                                    <%
                                 }

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
