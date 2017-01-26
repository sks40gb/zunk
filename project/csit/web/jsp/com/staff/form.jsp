<%@include file="/jsp/com/init.jsp" %>
<input name="<%=StaffModel.ID%>" type="hidden" value="${staff.staffId}">
<table align="center"  class="formTable">
    <th width="100%" colspan="3">
        ${param.formTitle}
    </th>
    <tr>
        <td>POST</td>
        <td><input type=text  name="<%=StaffModel.POST%>" value="${staff.post}"></td>
    </tr>

    <tr>
        <td>DEPARTMENT</td>
        <td><input type=text  name="<%=StaffModel.DEPARTMENT%>" value="${staff.department}"></td>
    </tr>
    <tr>
        <td>QAULIFICATION</td>
        <td><input type=text  name="<%=StaffModel.QUALIFICATION%>" value="${staff.qualification}"></td>
    </tr>

    <tr>
        <td>TYPE</td>
        <td>
        <select name="<%=StaffModel.TYPE%>" value="${staff.type}">
            <option>STAFF</option>
            <option>FACULTY</option>
            <option>HOD</option>
        </select>
    </tr>

    <tr>
        <td>JOINING DATE</td>
        <td>
            <%
        try {
            request.setAttribute("j_date", DateFormatter.convertDateToString(((StaffModel) request.getAttribute("staff")).getJoiningDate()));
        } catch (Exception e) {
            request.setAttribute("j_date", "");
        }
            %>
            <input type="Text" id="date" name="<%=StaffModel.JOINING_DATE%>" value = "${j_date}" readonly>
            <a href="javascript:NewCssCal('date','yyyymmdd')">
                <img src="<%= application.getContextPath()%>/images/cal/cal.gif" width="16" height="16" alt="Pick a date">
            </a>
        </td>
    </tr>

</table>