<!-- MapTest3.jsp -->

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="u" uri="/WEB-INF/util.tld" %>

<u:map var="prefMap" scope="session"
    lang="${param.lang}" version="${param.version}"/>

<p>User Preferences:
<ul>
    <li>Language: ${prefMap.lang}</li>
    <li>Version: ${prefMap.version}</li>
</ul>

<form method="post" action="MapTest2.jsp">
    <p> <input type="submit" value="Change">
</form>
