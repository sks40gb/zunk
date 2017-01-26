<%-- 
    Document   : JspControlTag
    Created on : 8 Jun, 2009, 6:56:59 PM
    Author     : sunil
--%>

<%@tag description="put the tag description here" pageEncoding="UTF-8"%>

<%-- The list of normal or fragment attributes can be specified here: --%>
<%@attribute name="type" required="true"%>
<%@attribute name="value"%>
<%@attribute name="label"%>
<%@attribute name="name" required="true"%>
<%@attribute name="style"%>

<% if(label == null){        
    StringBuilder buffer = new StringBuilder(name.replace("_", " "));
    label = buffer.toString();
}
   %>

<%
if("submit".equalsIgnoreCase(type.trim()) || "reset".equalsIgnoreCase(type.trim())){
     %>
     <input type="${type}" value="${value}" style="border:solid 2px red;padding:4px;">
     <%
    }else{
    %>
        <tr><td><%=label%> : </td> <td> <input type="${type}" value="${value}" style="border:solid 2px red;padding:4px;"></td></tr>
    <%
  }
%>