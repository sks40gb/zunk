<!-- SplitTest.jsp -->

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<p>fn:split -
<c:set var="str" value="a1/a2//b//c1/c2/c3"/>
<c:set var="delim" value="//"/>
<c:set var="array" value="${fn:split(str, delim)}"/>
<c:forEach var="token" items="${array}">
    [<c:out value="${token}"/>]
</c:forEach>
