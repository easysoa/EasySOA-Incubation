<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<html>
<head>
    <title>Tdrs</title>
    <style>
.error {
	color: #ff0000;
}
 
.errorblock {
	color: #000;
	background-color: #ffEEEE;
	border: 3px solid #ff0000;
	padding: 8px;
	margin: 16px;
}
	</style>
</head>
<body>
 
<h2>New Tdr</h2>
 
<form:form method="post" action="add" commandName="tdr">
    <form:errors path="*" cssClass="errorblock" element="div" />
    <table>
    <tr>
        <td><form:label path="nomStructure">Nom structure</form:label></td>
        <td><form:input path="nomStructure" /></td>
        <td><form:errors path="nomStructure" cssClass="error" /></td>
    </tr>
    <tr>
        <td><form:label path="tdrTdb.partenaireDepuis">Tableau de bord - partenaire depuis</form:label></td>
        <td><form:input path="tdrTdb.partenaireDepuis" /></td>
        <td><form:errors path="tdrTdb.partenaireDepuis" cssClass="error" /></td>
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
    <th>Partenaire depuis</th>
    <th>Projets</th>
    <th>&nbsp;</th>
</tr>
<c:forEach items="${tdrs}" var="tdr">
    <tr>
        <td>${tdr.nomStructure}</td>
        <!-- td>{tdr.lastname}, {tdr.firstname} </td -->
        <td>${tdr.tdrTdb.partenaireDepuis}</td>
        <td><a href="../projet/list?tdrId=${tdr.id}">projets</a></td>
        <td>...</td>
        <td><a href="delete/${tdr.id}">delete</a></td>
    </tr>
</c:forEach>
</table>
</c:if>
 
</body>
</html>