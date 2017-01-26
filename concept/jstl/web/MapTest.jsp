<!-- MapTest.jsp -->

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="u" uri="/WEB-INF/util.tld" %>

<u:map var="langMap" scope="application"
    en="English" de="Deutsch" fr="Fran�ais"
    it="Italiano" es="Espa�ol"/>
<c:set var="defaultLang" scope="application" value="en"/>

<u:map var="versionMap" scope="application"
    html="HTML" java="Java" flash="Flash"/>
<c:set var="defaultVersion" scope="application" value="html"/>

<c:redirect url="MapTest2.jsp"/>
