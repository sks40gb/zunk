<%@include file="/jsp/com/init.jsp" %>

<%
        CourseModel course = (CourseModel) request.getAttribute("course");
        CourseCategoryModel category = new CourseCategoryModel();
        List<CourseCategoryModel> courseCatList = category.getModelList();

        if (course == null) {
            course = new CourseModel();
        }

%>

<table align="center"  class="formTable">
    <input type="hidden" name="<%=CourseModel.ID%>" value="<%=course.getCourseId()%>"/>
    <th colspan="3">
        ${param.subTitle}
    </th>
    <tr>
        <td>COURSE</td>
        <td><input type=text name="<%=CourseModel.COURSE%>" value="<%=course.getName()%>" ></td>
    </tr>

    <tr>
        <td>DURATION</td>

        <td>
        <select name="<%=CourseModel.DURATION%>" value="<%=course.getDuration()%>">
            <option>2 Semesters</option>
            <option>4 Semesters</option>
            <option>6 Semesters</option>
            <option>8 Semesters</option>
            <option>10 Semesters</option>
            <option>12 Semesters</option>
            <option>1 Year</option>
            <option>2 Years</option>
            <option>3 Years</option>
            <option>4 Years</option>
            <option>As per Rule</option>
        </select>
    </tr>
    <tr>
        <td>SEAT</td>
        <td><input type=text name="<%=CourseModel.SEAT%>" value="<%=course.getSeat()%>"></td>
    </tr>
<tr>
        <td> ADMISSION FEE</td>
        <td><input type=text name="<%=CourseModel.COURSEFEE%>" value="<%=course.getCourseFee()%>"></td>
    </tr>

    <tr>
        <td>ELIGIBILITY</td>
        <td><input type=text name="<%=CourseModel.ELIGIBILITY%>" value="<%=course.getEligibility()%>"></td>
    </tr>

    <tr><td>COURSE CATEGORY</td>
        <td>
            <select name="<%=CourseModel.COURSE_CATEGORY_ID%>" value="123">
                <%
        for (CourseCategoryModel cat : courseCatList) {
                %>
                <option value="<%=cat.getCourseCategoryId()%>"><%=cat.getName()%></option>
                <%
        }
                %>
            </select>
        </td>
    </tr>
</table>