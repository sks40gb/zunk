<%@include file="/jsp/com/init.jsp" %>

<table align="center" class="formTable">
    <input type="hidden" name="<%=UserModel.ID%>" value="${user.userId}"/>
    <input type="hidden" name="<%=UserModel.THEME%>" value="${user.theme}"/>
    <input type="hidden" name="<%=ContactDetailModel.ID%>" value="${contact.contactDetailsId}"/>
    <input type="hidden" name="<%=AddressDetailModel.ID%>" value="${address.addressDetailId}"/>
    <input type='hidden' name='mode' value = "${mode}"/>

    <th width="100%" colspan="3">
        ${param.formTitle}
    </th>

    <tr>
        <td>USER NAME</td>
        <td><input type=text name="<%=UserModel.USER_NAME%>" value="${user.userName}" ${param.readonly}></td>
    </tr>

    <tr>
        <td>PASSWORD</td>

        <td><input type=password name="<%=UserModel.PASSWORD%>" value = "${user.userPassword}"></td>
    </tr>
    <tr>
        <td>FIRST NAME</td>
        <td><input type=text name="<%=UserModel.FIRST_NAME%>" value = "${user.firstName}"/></td>
    </tr>

    <tr>
        <td>MIDDLE NAME</td>
        <td><input type=text name="<%=UserModel.MIDDLE_NAME%>" value = "${user.middleName}"/></td>
    </tr>

    <tr>
        <td>LAST NAME</td>
        <td><input type=text name="<%=UserModel.LAST_NAME%>" value = "${user.lastName}"/></td>
    </tr>

    <tr>
        <td>GENDER</td>
        <td>
            <select name="<%=UserModel.GENDER%>" value = "${user.gender}"/>
                <option>M</option>
                <option>F</option>
            </select>
        </td>
    </tr>
    <tr>
        <td></td>
    </tr>
    <tr>
        <td>DATE OF BIRTH</td>
        <td>
            <%
            try{
                request.setAttribute("dob", DateFormatter.convertDateToString(((UserModel)request.getAttribute("user")).getDateOfBirth()));
            }catch(Exception e){
                request.setAttribute("dob","");
                }
            %>
            <input type="Text" id="date" name="<%=UserModel.DATE_OF_BIRTH%>" value = "${dob}" readonly>
            <a href="javascript:NewCssCal('date','yyyymmdd')">
                <img src="<%= application.getContextPath()%>/images/cal/cal.gif" width="16" height="16" alt="Pick a date">
            </a>
        </td>
    </tr>

    <tr>
        <td>E-MAIL ID</td>
        <td><input type=text name="<%=ContactDetailModel.EMAIL%>" value = "${contact.emailId}"/></td>
    </tr>

    <tr>
        <td>MOBILE NUMBER</td><td><input type=text name="<%=ContactDetailModel.MOBILE_NUMBER%>" value = "${contact.mobileNumber}"/></td>
    </tr>
    <tr>
        <td>PHONE NUMBER</td><td><input type=text name="<%=ContactDetailModel.PHONE_NUMBER%>" value = "${contact.phoneNumber}"/></td>
    </tr>

    <tr>
        <td>BLOCK</td>
        <td><input type=text  name="<%=AddressDetailModel.BLOCK%>"block value = "${address.block}"/></td>
    </tr>

    <tr>
        <td>CITY</td>
        <td><input type=text  name="<%=AddressDetailModel.CITY%>" value = "${address.city}"/></td>
    </tr>

    <tr>
        <td>STATE</td>
        <td><input type=text name="<%=AddressDetailModel.STATE%>" value = "${address.state}"/></td>
    </tr>

    <tr><td>COUNTRY</td>
        <td>
            <select name="<%=AddressDetailModel.COUNTRY%>" value = "${address.country}">
                <%=Html.getCountryOptions()%>
            </select>
        </td>
    </tr>

    <tr>
        <td>POST CODE</td>
        <td><input type=text name="<%=AddressDetailModel.POST_CODE%>" value = "${address.postCode}"/></td>
    </tr>

</table>