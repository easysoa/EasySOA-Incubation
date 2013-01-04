<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<html>
<head>
    <title>tdr precompte details</title>
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
	
	<script type="text/javascript">
		function saveSubmit(form){
			form.action = "../save";
			form.submit();
		}
		
		function approveSubmit(form){
			form.action = "../approve";
			form.submit();
		}		
	</script>
	
</head>
<body>

TDR precompte details

<br/>

<c:if test="${!empty tdrPrecompteDetails}">
<form:form method="post" commandName="tdrPrecompteDetails">
    <form:errors path="*" cssClass="errorblock" element="div" />
	<table class="data" border="1">
		<colgroup> <!-- 1000 -->
			<col width="200px">
			<col width="200px">
			<col width="100px">
			<col width="200px">
			<col width="200px">
			<col width="100px">			      	
		</colgroup>
		<tbody>
			<form:hidden path="id" value="${tdrPrecompteDetails.id}" />
			<tr>
		    	<td><form:label path="identifiantClientPivotal">Identifiant client Pivotal</form:label></td>
				<td><form:input disabled="true" path="identifiantClientPivotal" /></td>
	        	<td><form:errors path="identifiantClientPivotal" cssClass="error" /></td>
	        	<td>&nbsp;</td>
	        	<td>&nbsp;</td>
	        	<td>&nbsp;</td>
			</tr>
			<tr>
		    	<td><form:label path="nomStructure">Nom structure</form:label></td>
				<td><form:input path="nomStructure" /></td>
	        	<td><form:errors path="nomStructure" cssClass="error" /></td>
		    	<td><form:label path="typeStructure">Type structure</form:label></td>
				<td><form:input path="typeStructure" /></td>
	        	<td><form:errors path="typeStructure" cssClass="error" /></td>
	        </tr>
			<tr>
		    	<td><form:label path="champAction">Champ action</form:label></td>
				<td><form:input path="champAction" /></td>
	        	<td><form:errors path="champAction" cssClass="error" /></td>
				<td><form:label path="sirenSiret">Siren/Siret</form:label></td>
				<td><form:input path="sirenSiret"/></td>
	        	<td><form:errors path="sirenSiret" cssClass="error" /></td>
			</tr>
			<tr>
		        <td><form:label path="email">Email</form:label></td>
				<td><form:input path="email"/></td>
	        	<td><form:errors path="email" cssClass="error" /></td>	        
		    	<td><form:label path="ville">Ville</form:label></td>
				<td><form:input path="ville"/></td>
	        	<td><form:errors path="ville" cssClass="error" /></td>	        
		    </tr>
		    <tr>
		        <td><form:label path="cp">Code postal</form:label></td>
				<td><form:input path="cp"/></td>
	        	<td><form:errors path="cp" cssClass="error" /></td>
		        <td><form:label path="adresse">Adresse</form:label></td>
				<td><form:input path="adresse"/></td>
	        	<td><form:errors path="adresse" cssClass="error" /></td>	        
		    </tr>
		    <tr>
		        <td><form:label path="telephone">Telephone</form:label></td>
				<td><form:input path="telephone"/></td>
	        	<td><form:errors path="telephone" cssClass="error" /></td>		    
		        <td><form:label path="siteWeb">Site web</form:label></td>
				<td><form:input path="siteWeb"/></td>
	        	<td><form:errors path="siteWeb" cssClass="error" /></td>	        
		    </tr>
		    <tr>
				<td><form:label path="tdrTdb.annee">Annee</form:label></td>
				<td><form:input path="tdrTdb.annee"/></td>
	        	<td><form:errors path="tdrTdb.annee" cssClass="error" /></td>
		        <td><form:label path="tdrTdb.partenaireDepuis">Partenaire depuis</form:label></td>
				<td><form:input path="tdrTdb.partenaireDepuis"/></td>
	        	<td><form:errors path="tdrTdb.partenaireDepuis" cssClass="error" /></td>
		    </tr>
		    <tr>
		        <td><form:label path="tdrTdb.dotationGlobale">Dotation globale</form:label></td>
				<td><form:hidden path="tdrTdb.dotationGlobale"/>${tdrPrecompteDetails.tdrTdb.montantDisponible}</td>
	        	<td>&nbsp;</td>
		        <td><form:label path="tdrTdb.reliquatAnneePrecedente">Reliquat annee precedente</form:label></td>
				<td><form:hidden path="tdrTdb.reliquatAnneePrecedente"/>${tdrPrecompteDetails.tdrTdb.montantDisponible}</td>
	        	<td>&nbsp;</td>		    
		    </tr>
		    <tr>
		        <td><form:label path="tdrTdb.dotationAnnuelle">Dotation anuelle</form:label></td>
				<td><form:hidden path="tdrTdb.dotationAnnuelle"/>${tdrPrecompteDetails.tdrTdb.montantDisponible}</td>
	        	<td>&nbsp;</td>
		        <td><form:label path="tdrTdb.sommeUtilisee">Somme utilisée</form:label></td>
				<td><form:hidden path="tdrTdb.sommeUtilisee"/>${tdrPrecompteDetails.tdrTdb.montantDisponible}</td>
	        	<td>&nbsp;</td>		    
		    </tr>
		    <tr>
		        <td><form:label path="tdrTdb.montantDisponible">Montant disponible</form:label></td>
				<td><form:hidden path="tdrTdb.montantDisponible"/>${tdrPrecompteDetails.tdrTdb.montantDisponible}</td>
	        	<td>&nbsp;</td>
		        <td><form:label path="tdrTdb.reliquat">Reliquat</form:label></td>
				<td><form:hidden path="tdrTdb.reliquat"/>${tdrPrecompteDetails.tdrTdb.reliquat}</td>
	        	<td>&nbsp;</td>		    
		    </tr>
		    <tr>
		        <td><form:label path="tdrTdb.nbBeneficiairesApv">Nombre bénéficiares APV</form:label></td>
				<td><form:input path="tdrTdb.nbBeneficiairesApv" disabled="true"/></td>
	        	<td><form:errors path="tdrTdb.nbBeneficiairesApv" cssClass="error" /></td>
	        	<td>&nbsp;</td>
	        	<td>&nbsp;</td>
	        	<td>&nbsp;</td>
		    </tr>
		    <tr>
		        <td><form:label path="tdrTdb.nbEnfants">Nombre enfants</form:label></td>
				<td><form:input path="tdrTdb.nbEnfants"/></td>
	        	<td><form:errors path="tdrTdb.nbEnfants" cssClass="error" /></td>
		        <td><form:label path="tdrTdb.nbJeunes">Nombre jeunes</form:label></td>
				<td><form:input path="tdrTdb.nbJeunes"/></td>
	        	<td><form:errors path="tdrTdb.nbJeunes" cssClass="error" /></td>		    </tr>
		    <tr>
		        <td><form:label path="tdrTdb.nbAdultesIsoles">Nombre adultes isolés</form:label></td>
				<td><form:input path="tdrTdb.nbAdultesIsoles"/></td>
	        	<td><form:errors path="tdrTdb.nbAdultesIsoles" cssClass="error" /></td>
		        <td><form:label path="tdrTdb.nbSeniors">Nombre séniors</form:label></td>
				<td><form:input path="tdrTdb.nbSeniors"/></td>
	        	<td><form:errors path="tdrTdb.nbSeniors" cssClass="error" /></td>
			</tr>
		    <tr>
		        <td><form:label path="tdrTdb.nbBeneficiairesPrevisionnel">Nombre bénéficiaires prévisionel</form:label></td>
				<td><form:input path="tdrTdb.nbBeneficiairesPrevisionnel"/></td>
	        	<td><form:errors path="tdrTdb.nbBeneficiairesPrevisionnel" cssClass="error" /></td>
		        <td><form:label path="tdrTdb.status">Status</form:label></td>
				<td><form:hidden path="tdrTdb.status"/>${tdrPrecompteDetails.tdrTdb.status}</td>
	        	<td><td>&nbsp;</td></td>	        
	        </tr>
	    	<tr>
	        	<td colspan="3">
	            	<input type="submit" value="Sauver" onclick="saveSubmit(this.form);"/>
	        	</td>
	        	<td colspan="3">
	        		<!-- TODO : complete the conditions to approve the TDR -->
	        		<c:choose>
		        		<c:when test="${tdrPrecompteDetails.tdrTdb.dotationAnnuelle > 0}">
		        			<input type="submit" value="Approuver" onclick="approveSubmit(this.form);"/>
		        		</c:when>
		        		<c:otherwise>
		        			<input type="submit" value="Approuver" onclick="approveSubmit(this.form);" disabled="true"/>
		        		</c:otherwise>
	        		</c:choose>
	        	</td>
	    	</tr>
		</tbody>
	</table>
</form:form>
</c:if>

<br/>
<a href="../../tdrprecompte/list.html">Back to TDR precomptes</a>
</body>
</html>