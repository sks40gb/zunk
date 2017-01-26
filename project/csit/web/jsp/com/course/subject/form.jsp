<%@include file="/jsp/com/init.jsp" %>

<%
        CourseModel course = new CourseModel();
        List<CourseModel> courseList = course.getModelList();

%>

<table align="center"  class="formTable">
    <input type="hidden" name="<%=SubjectModel.ID%>" value="${subject.subjectId}"/>
    <th colspan="3">
        ${param.subTitle}
    </th>
    <tr>
        <input type="hidden" name="<%=SubjectModel.ID%>"/>
        <td>DEPARTMENT</td>
        <td><input type=text name="<%=SubjectModel.NAME%>" value="${subject.name}"></td>
    </tr>
    <tr>
        <td>DESCRIPTION</td>
        <td><input type=text name="<%=SubjectModel.DESCRIPTION%>" value="${subject.description}"></td>
    </tr>
    <tr><td>COURSE</td>
        <td>          
            <select name="<%=SubjectModel.COURSE_ID%>">
                <%
        for (CourseModel _course : courseList) {
                %>
                <option value="<%=_course.getCourseId()%>"><%=_course.getName()%></option>
                <%
        }
                %>
            </select>
        </td>
    </tr>
</table>