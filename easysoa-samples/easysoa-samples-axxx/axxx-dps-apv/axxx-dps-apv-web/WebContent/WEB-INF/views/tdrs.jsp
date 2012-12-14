<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<html>
<head>
    <title>Tdrs</title>
</head>
<body>
 
<h2>New Tdr</h2>
 
<form:form method="post" action="add" commandName="tdr">
 
    <table>
    <tr>
        <td><form:label path="nomStructure">Nom structure</form:label></td>
        <td><form:input path="nomStructure" /></td>
    </tr>
    <tr>
        <td colspan="2">
            <input type="submit" value="Nouvelle tdr"/>
        </td>
    </tr>
</table> 
</form:form>
 
     
<h2>All Tdrs</h2>

<c:if  test="${!empty tdrs}">
<table class="data">
<tr>
    <th>Nom Structure</th>
    <th>Type Structure</th>
    <th>&nbsp;</th>
</tr>
<c:forEach items="${tdrs}" var="tdr">
    <tr>
        <td>${tdr.nomStructure}</td>
        <!-- td>{tdr.lastname}, {tdr.firstname} </td -->
        <td>...</td>
        <td><a href="delete/${tdr.id}">delete</a></td>
    </tr>
</c:forEach>
</table>
</c:if>
 
</body>
</html>