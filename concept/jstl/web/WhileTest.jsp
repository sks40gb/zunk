<!-- WhileTest.jsp -->

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="u" uri="/WEB-INF/util.tld" %>

<p>u:while -
<c:set var="str" value="a1/a2//b//c1/c2/c3"/>
<c:set var="delim" value="//"/>
<u:while test="\${fn:contains(str, delim)}">
    [<c:out value="${fn:substringBefore(str, delim)}"/>]
    <c:set var="str" value="${fn:substringAfter(str, delim)}"/>
</u:while>
[<c:out value="${str}"/>]
