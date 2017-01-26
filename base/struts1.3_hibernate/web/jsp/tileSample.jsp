<%@ page language="java" %>
<%@ taglib uri="/WEB-INF/tld/struts-tiles.tld" prefix="tiles" %>

<tiles:insert page="/tiles/template.jsp" flush="true">
   <tiles:put name="title" type="string" value="Welcome" />
   <tiles:put name="header" value="/tiles/top.jsp" />
   <tiles:put name="menu" value="/tiles/left.jsp" />
   <tiles:put name="body" value="/tiles/content.jsp" />
   <tiles:put name="bottom" value="/tiles/bottom.jsp" />
</tiles:insert>
