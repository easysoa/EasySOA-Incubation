<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<html>
<head>
    <title>Projets</title>
</head>
<body>
 
<h2>Nouveau projet</h2>
 
<form:form method="post" action="add" commandName="projet">
 
    <table>
    <tr>
        <td><form:label path="typeLieu">Type lieu</form:label></td>
        <td><form:input path="typeLieu" /></td>
    </tr>
    <tr>
        <td colspan="2">
            <input type="submit" value="Nouveau projet"/>
        </td>
    </tr>
</table> 
</form:form>
 
     
<h2>Tous les projets</h2>

<c:if  test="${!empty projets}">
<table class="data">
<tr>
    <th>Type lieu</th>
    <th>...</th>
    <th>&nbsp;</th>
</tr>
<c:forEach items="${projets}" var="projet">
    <tr>
        <td>${projet.typeLieu}</td>
        <td>...</td>
        <td><a href="delete/${projet.id}">delete</a></td>
    </tr>
</c:forEach>
</table>
</c:if>
 
</body>
</html>