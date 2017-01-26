<%@ page language="java" %>
<%@ taglib uri="/WEB-INF/tld/struts-tiles.tld" prefix="tiles" %>
<html>

    <head>
        <title><tiles:getAsString name="title" ignore="true"/></title>
    </head>

    <body>

        <table border="1" cellpadding="0" cellspacing="0" width="100%" bordercolor="#000000" bgcolor="#E7FDFE">
            <tr>
                <td width="100%" colspan="2" valign="top"><tiles:insert attribute="header"/></td>
            </tr>
            <tr>
                <td width="23%"><tiles:insert attribute="menu"/></td>
                <td width="77%" valign="top" valign="top"><tiles:insert attribute="body"/></td>
            </tr>
            <tr>
                <td width="100%" colspan="2" valign="top"><tiles:insert attribute="bottom"/></td>
            </tr>
        </table>

    </body>

</html>