<!-- EvalTest.jsp -->

<%@ taglib prefix="u" uri="/WEB-INF/util.tld" %>

<u:eval expr="${initParam.tags_db_dataSource}" var="db"/>
${db}

<u:eval expr="${initParam.tags_db_dataSource}"/>

<u:eval>${initParam.tags_db_dataSource}</u:eval>
