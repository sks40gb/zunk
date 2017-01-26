<%-- 
    Document   : JspControlTag
    Created on : 8 Jun, 2009, 6:56:59 PM
    Author     : sunil
--%>

<%@tag description="put the tag description here" pageEncoding="UTF-8"%>
<%@tag body-content="scriptless" %>
<%-- The list of normal or fragment attributes can be specified here: --%>
<%@attribute name="label"%>
<%@attribute name="name" required="true"%>
<%@attribute name="style"%>

<% if(label == null){        
    StringBuilder buffer = new StringBuilder(name.replace("_", " "));
    label = buffer.toString();
}
%> 
 <tr>
     <td><%=label%> : </td>
     <td> 
        <select style="border:solid 2px red;padding:1px;">
            <jsp:doBody/>
        </select>
      </td>
 </tr>
 