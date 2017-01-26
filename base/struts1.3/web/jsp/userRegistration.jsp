<%@taglib tagdir="/WEB-INF/tags" prefix="sun" %>
<h1>User Registration</h1>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<html:errors/>
<html:form action="userRegistration" styleClass="form_container">
    <table>
        <tr>
            <td>
                <bean:message key="userRegistration.firstName" />*
            </td>
            <td>
                <html:text property="firstName" />
            </td>
        </tr>
        <td>
            <bean:message key="userRegistration.lastName" />*
        </td>
        <td>
            <html:text property="lastName" />
        </td>
        <tr>
            <td>
                <bean:message key="userRegistration.userName" />*
            </td>
            <td>
                <html:text property="userName" />
            </td>
        </tr>
        <tr>
            <td>
                <bean:message key="userRegistration.email" />*
            </td>
            <td>
                <html:text property="email" />
            </td>
        </tr>
        <tr>
            <td>
                <bean:message key="userRegistration.phone" />
            </td>
            <td>
                <html:text property="phone" />
            </td>
        </tr>
        <tr>
            <td>
                <bean:message key="userRegistration.fax" />
            </td>
            <td>
                <html:text property="fax" />
            </td>
        </tr>
        <tr>
            <td>
                <bean:message key="userRegistration.password" />*
            </td>
            <td>
                <html:password property="password" />
            </td>
        </tr>
        <tr>
            <td>
                <bean:message key="userRegistration.password" />*
            </td>
            <td>
                <html:password property="passwordCheck" />
            </td>
        </tr>
        <tr>
            <td>
                <html:submit />
            </td>
            <td>
                <html:cancel />
            </td>
        </tr>    
    </table>
</html:form>