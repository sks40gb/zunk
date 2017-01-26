
<%@include file="/jsp/com/init.jsp" %>
<%
        StudentModel student = (StudentModel) request.getAttribute("student");
        if (student == null) {
            student = new StudentModel();
        }
%>
<table align="center"  class="formTable">
    <input type="hidden" name ="<%=StudentModel.ID%>" value="<%=student.getStudentId()%>">
    <tr>
        <td>ENROLL NUMBER</td>
        <td><input type=text  name="<%=StudentModel.ENROLL_NUMBER%>" value="<%=student.getEnrollNumber()%>"></td>
    </tr>
    <tr><td>COURSE</td>
        <td>
            <select name="<%=CourseModel.ID%>">
                <%        
        for (Object object: new CourseModel().getModelList()) {
            CourseModel course = (CourseModel)object;
                %>
                <option value="<%=course.getCourseId()%>"><%=course.getName()%></option>
                <%
        }
                %>
            </select>
        </td>
    </tr>

    <tr>
        <td>ADMISSION DATE</td>
        <td>
            <%String admin_date = student.getAdminssionDate() ==  null ? "" :DateFormatter.convertDateToString(student.getAdminssionDate());%>
            <input type="Text" id="date" name="<%=StudentModel.ADMISSION_DATE%>" value="<%=admin_date%>" readonly>
            <a href="javascript:NewCssCal('date','yyyymmdd')">
                <img src="<%= application.getContextPath()%>/images/cal/cal.gif" width="16" height="16" alt="Pick a date">
            </a>
        </td>
    </tr>
</table>