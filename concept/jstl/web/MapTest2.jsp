<!-- MapTest2.jsp -->

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="u" uri="/WEB-INF/util.tld" %>

<c:if test="${prefMap == null}">
    <u:map var="prefMap" scope="session"
        lang="${defaultLang}" version="${defaultVersion}"/>
</c:if>

<form method="post" action="MapTest3.jsp">

    <p>Language: <br>
    <select name="lang" size="1">
        <c:forEach var="lang" items="${langMap}">
            <c:set var="selected" value=""/>
            <c:if test="${lang.key == prefMap.lang}">
                <c:set var="selected" value="selected"/>
            </c:if>
            <option value="${lang.key}" ${selected}>
                ${lang.value}
            </option>
        </c:forEach>
    </select>

    <p>Version: <br>
    <c:forEach var="version" items="${versionMap}">
        <c:set var="checked" value=""/>
        <c:if test="${version.key == prefMap.version}">
            <c:set var="checked" value="checked"/>
        </c:if>
        <input type="radio" name="version" ${checked}
            value="${version.key}"> ${version.value} <br>
    </c:forEach>

    <p> <input type="submit" value="Save">

</form>
