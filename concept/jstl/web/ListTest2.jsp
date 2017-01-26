<!-- ListTest2.jsp -->

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="u" uri="/WEB-INF/util.tld" %>

<u:list var="selectedServices">
    <c:forEach var="index" items="${paramValues.selected}">
        <c:set var="paramName" value="service${index}"/>
        <u:item value="${param[paramName]}"/>
    </c:forEach>
</u:list>

<p> Selected Services: <br>
<ul>
    <c:forEach var="service" items="${selectedServices}">
        <li><c:out value="${service}"/></li>
    </c:forEach>
</ul>
