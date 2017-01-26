<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean"%>
<%@taglib uri="http://struts.apache.org/tags-logic" prefix="logic"%>
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html"%>
<html>
    <head>
    </head>
    <body>

        <h2>Struts - Validator Example</h2>

        <b>User Registeration Form</b>
        <br/><br/>

        <font color="red">
        <html:errors/>
        </font>

        <html:form action="/Register" onsubmit="validateUserForm1(this);">
            <html:javascript formName="userForm" />
            <table>
                <tr>
                    <td><bean:message key="label.user.username" /> : </td>
                    <td><html:text property="username" size="20"/></td>
                </tr>
                <tr>
                    <td><bean:message key="label.user.pwd" /> : </td>
                    <td><html:text property="pwd" size="20"/></td>
                </tr>
                <tr>
                    <td><bean:message key="label.user.pwd2" /> :</td>
                    <td><html:text property="pwd2" size="20"/></td>
                </tr>
                <tr>
                    <td><bean:message key="label.user.email" /> :</td>
                    <td><html:text property="email" size="20"/></td>
                </tr>
                <tr>
                    <td></td>
                    <td>
                        <html:submit>
                            <bean:message key="label.user.button.submit" />
                        </html:submit>
                    </td>
                </tr>
            </table>
        </html:form>
    </body>
</html>