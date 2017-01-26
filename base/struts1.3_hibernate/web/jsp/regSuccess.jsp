<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<html>
    <head>
        <title>
            User Registration Was Successful!
        </title>
    </head>
    <body>
        <h1>User Registration Was Successful!</h1>
    </body>
    <table>
        <tr>
            <td>
                <bean:message key="userRegistration.firstName" />
            </td>
            <td>
                <bean:write name="user" property="firstName" />
            </td>
        </tr>
        <tr>
            <td>
                <bean:message key="userRegistration.lastName" />
            </td>
            <td>
                <bean:write name="user" property="lastName" />
            </td>
        </tr>
        <tr>
            <td>
                <bean:message key="userRegistration.email" />
            </td>
            <td>
                <bean:write name="user" property="email" />
            </td>
        </tr>
    </table>
</html>
