<%-- 
    Document   : init
    Created on : 10 Mar, 2009, 3:52:43 PM
    Author     : sunil
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
"http://www.w3.org/TR/html4/loose.dtd">
<%@page import="java.util.*" %>
<%@page import="com.csit.jsp.*" %>
<%@page import="com.csit.sql.table.model.*" %>
<%@page import="com.csit.role.*" %>
<%@page import="com.csit.util.*" %>   
<%@page import="com.csit.mode.Mode" %>
<%@page import="com.csit.servlet.Attribute"%>
<%@page import="com.csit.servlet.search.Search"%>

<link rel="stylesheet" type="text/css" href="<%= application.getContextPath()%>/css/application.css" />
<link rel="stylesheet" type="text/css" href="<%= application.getContextPath()%>/css/layout.css" />
<link rel="stylesheet" type="text/css" href="<%= application.getContextPath()%>/css/table.css" />
<link rel="stylesheet" type="text/css" href="<%= application.getContextPath()%>/css/form.css" />
<link rel="stylesheet" type="text/css" href="<%= application.getContextPath()%>/css/menus.css" />
<link rel="stylesheet" type="text/css" href="<%= application.getContextPath()%>/css/alert.css" />
<link rel="stylesheet" type="text/css" href="<%= application.getContextPath()%>/css/theme/<%=session.getAttribute(Attribute.CURRENT_USER) == null ? Theme.DEFAULT_THEME : ((UserModel) session.getAttribute(Attribute.CURRENT_USER)).getTheme()%>.css" />
<link rel="stylesheet" type="text/css" href="<%= application.getContextPath()%>/css/clock.css" />
<link rel="stylesheet" type="text/css" href="<%= application.getContextPath()%>/css/rounded-box.css" />
<link rel="stylesheet" type="text/css" href="<%= application.getContextPath()%>/css/test.css" />

<script type="text/javascript" src="<%= application.getContextPath()%>/js/date_time_picker/datetimepicker.js"></script>
<script type="text/javascript" src="<%= application.getContextPath()%>/js/rounded-box.js"></script>
<!--<script type="text/javascript" src="<%= application.getContextPath()%>/js/clock/LCD_Clock.js"></script>-->
