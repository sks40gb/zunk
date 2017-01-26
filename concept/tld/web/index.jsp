<%--
    Document   : index
    Created on : 6 Feb, 2009, 4:45:23 PM
    Author     : sunil
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>JSP Page</title>
    </head>
    <body>
        <%@taglib uri="/WEB-INF/tld/sample.tld" prefix="e"%>
        <%@taglib tagdir="/WEB-INF/tags" prefix="sunil"%>
        <sunil:JspInput type="text" name="FIRST_NAME"/>
        <sunil:JspInput type="checkbox" name="isMale"/>
        <e:emp name="sunil"> kumar singh </e:emp>
    </body>
</html>
