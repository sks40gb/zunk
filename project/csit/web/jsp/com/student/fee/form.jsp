<%@include file="/jsp/com/init.jsp" %>
<table align="center"  class="formTable">
    <tr>
        <td>Student</td>
        <td>
            <select name="<%=FeeModel.STUDENT_ID%>">
                <%
         UserModel _user = new UserModel();
         _user.addCriteria(UserModel.TYPE, Role.STUDENT);
        for (Object obj : _user.getModelList()) {
            UserModel user = (UserModel)obj;
            StudentModel student =  new StudentModel();
            student.getModel();
            student.addCriteria(StudentModel.USER_ID, user.getUserId());
                %><option value="<%= student.getStudentId()%>"><%= user.getUserName()%></option><%
        }
                %>
            </select>
        </td>
    </tr>
    <tr>
        <td>Semester</td>
        <td>
            <select name="<%=FeeModel.SEM_ID%>">
                <%=Html.getSemesters()%>
            </select>
        </td>
    </tr>
    <tr>
        <td>Due Date</td>
        <td>
            <input type="Text" id="date" name="<%=FeeModel.DUE_DATE%>">
            <a href="javascript:NewCssCal('date','yyyymmdd')">
                <img src="<%= application.getContextPath()%>/images/cal/cal.gif" width="16" height="16" alt="Pick a date">
            </a>
        </td>
    </tr>
    <tr>
        <td>Deposite Fee</td>
        <td>
            <input type="text" name="<%=FeeModel.AMOUNT%>"/>
        </td>
    </tr>
    <tr>
        <td>Late Fee</td>
        <td>
            <input type="text" name="<%=FeeModel.LATE_FEE%>"/>
        </td>
    </tr>
</table>