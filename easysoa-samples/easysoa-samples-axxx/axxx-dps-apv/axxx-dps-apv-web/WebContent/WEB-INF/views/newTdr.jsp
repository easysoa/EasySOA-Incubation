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

<h2>New Tdr</h2>
 
 
    <!--@XmlElement(nillable=false, required=true)
	private String identifiantClientPivotal;
    @XmlElement(nillable=false, required=true)
	private TypeStructure typeStructure; // enum ex. Association nat.
    @XmlElement(nillable=false, required=true)
	private String nomStructure;
    @XmlElement(nillable=false, required=true)
	private int anciennete;
    @XmlElement(nillable=false, required=true)
	private String telephone;
	private String email;
	@XmlElement(nillable=false, required=true)
	private String adresse;
	@XmlElement(nillable=false, required=true)
	private String ville;
    @XmlElement(nillable=false, required=true)
	private String cp;
    @XmlElement(nillable=false, required=true)
	private String apeNaf; //. ex. 512E TODO remove
    @XmlElement(nillable=false, required=true)
	private String sirenSiret; // rule -->
 
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

</body>
</html>