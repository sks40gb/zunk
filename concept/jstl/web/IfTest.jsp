<!-- IfTest.jsp -->

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="u" uri="/WEB-INF/util.tld" %>

<html>
<body>
<form method="post">

<c:set var="paramsProvided"
    value="${!empty param.unitPrice and !empty param.quantity}"/>

<u:if test="${paramsProvided}">
    <jsp:attribute name="TRUE">
        <c:set var="price"
            value="${param.unitPrice * param.quantity}"/>
        <p> Price: ${price}
    </jsp:attribute>
    <jsp:attribute name="FALSE">
        <p> Please fill out the form
    </jsp:attribute>
    <jsp:attribute name="ERROR">
        <p> Number format error
    </jsp:attribute>
</u:if>

<p> Unit Price:
<input type="text" name="unitPrice" size="10"
    value="<c:out value='${param.unitPrice}'/>">
<p> Quantity:
<input type="text" name="quantity" size="10"
    value="<c:out value='${param.quantity}'/>">
<p> <input type="submit" value="Calculate Price">

</form>
</body>
</html>
