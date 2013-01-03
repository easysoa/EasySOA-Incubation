<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<html>
<head>
    <title>tdr precompte</title>
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

TDR precomptes

<br/>

<c:if  test="${!empty tdrPrecomptes}">
<table class="data" border="1">
<tr>
    <th>Nom Structure</th>
    <th>Ville</th>
    <th></th>
    <th></th>
    <th></th>
</tr>
<c:forEach items="${tdrPrecomptes}" var="tdr">
    <tr>
        <td>${tdr.nomStructure}</td>
        <td>${tdr.ville}</td>
        <td><a href="../tdrprecompte/details/${tdr.id}">Details</a></td>
        <td>...</td>
        <td><a href="../tdrprecompte/delete/${tdr.id}">delete</a></td>
    </tr>
</c:forEach>
</table>
</c:if>

<br/>

<a href="../tdrprecompte/newTdr">(TEST SEULEMENT) Créer une nouvelle TDR</a>

</body>
</html>