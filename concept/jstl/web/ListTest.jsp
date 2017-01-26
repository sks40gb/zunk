<!-- ListTest.jsp -->

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="u" uri="/WEB-INF/util.tld" %>

<u:list var="services">
    <u:item> E-Mail </u:item>
    <u:item> Web Hosting </u:item>
    <u:item> E-Commerce </u:item>
</u:list>

<form method="post" action="ListTest2.jsp">
    <p> Services: <br>
    <c:forEach var="index" begin="${0}"
            end="${fn:length(services)-1}">
        <input type="checkbox" checked
            name="selected" value="${index}">
        <c:out value="${services[index]}"/>
        <input type="hidden" name="service${index}"
            value="${services[index]}"> <br>
    </c:forEach>
    <p> <input type="submit" value="Select">
</form>
