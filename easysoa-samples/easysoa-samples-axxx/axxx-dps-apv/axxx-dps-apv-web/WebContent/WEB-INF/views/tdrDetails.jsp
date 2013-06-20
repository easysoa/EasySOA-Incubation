<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<%@ include file="/static/html/header.html" %>

<%@ include file="/static/html/menu.html" %>

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

		<div class="columns-1" id="content-wrapper">
			<div class="lfr-column" id="column-1">
				<div class="lfr-portlet-column" id="layout-column_column-1">
					<div id="p_p_id_56_INSTANCE_zZ7Y_" class="portlet-boundary portlet-boundary_56_  portlet-journal-content">
						<!--<a id="p_56_INSTANCE_zZ7Y"></a>-->
						<div class="portlet" id="portlet-wrapper-56_INSTANCE_zZ7Y">
							<!--<div class="portlet-topper">
								<span class="portlet-title"> <span><img
										class="icon" src="navigation-accueil_fichiers/spacer.png"
										alt="Web Content Display" title="Web Content Display"
										style="background-image: url('/html/icons/.sprite.png'); background-position: 50% -208px; background-repeat: no-repeat; height: 16px; width: 16px;"></span>
									Web Content Display
								</span>
								<div class="portlet-icons" id="portlet-small-icon-bar_56_INSTANCE_zZ7Y"></div>
							</div>-->
							<div class="portlet-content">
								<div class="portlet-content-container" style="">
									<div>
										<div class="journal-content-article"
											id="article_10138_11246_1.0">
											<div class="article full">
												<div class="title"></div>
												<div class="text">
													<p style="text-align: justify;">&nbsp;</p>
													<p>
														<span style="font-size: large;"> </span>
													</p>
													<div>
														<b><span style="color: maroon;">TDR details</span></b>
													</div>
													<p>
														<c:if test="${!empty tdr}">
														<form:form method="post" commandName="tdr" action="../save" cssClass="uni-form">
														    <form:errors path="*" cssClass="errorblock" element="div" />
															<table class="data">
																<colgroup> <!-- 1000 -->
																	<col width="200px">
																	<col width="200px">
																	<col width="100px">
																	<col width="200px">
																	<col width="200px">
																	<col width="100px">			      	
																</colgroup>
																<tbody>
																	<form:hidden path="id" value="${tdr.id}" />
																	<tr>
																    	<td><form:label path="identifiantClientPivotal">Identifiant client Pivotal</form:label></td>
																		<td><form:hidden path="identifiantClientPivotal" />${tdr.identifiantClientPivotal}</td>																		
															        	<td><form:errors path="identifiantClientPivotal" cssClass="error" /></td>
															        	<td>&nbsp;</td>
															        	<td>&nbsp;</td>
															        	<td>&nbsp;</td>
																	</tr>
																	<tr><td colspan="6">&nbsp;</td></tr>																	
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
																	<tr><td colspan="6">&nbsp;</td></tr>																    
																    <tr>
                                                                        <td><form:label path="tdrTdb.dotationAnnuelle">Dotation anuelle</form:label></td>
                                                                        <td><form:input path="tdrTdb.dotationAnnuelle"/></td>
                                                                        <td>&nbsp;</td>
                                                                        <td><form:label path="tdrTdb.nbBeneficiairesPrevisionnel">Nb bénéficiaires prévisionel</form:label></td>
                                                                        <td><form:hidden path="tdrTdb.nbBeneficiairesPrevisionnel"/>${tdr.tdrTdb.nbBeneficiairesPrevisionnel}</td>
                                                                        <td><form:errors path="tdrTdb.nbBeneficiairesPrevisionnel" cssClass="error" /></td>
																    </tr>
																    <tr>
                                                                        <td><form:label path="tdrTdb.reliquatAnneePrecedente">Reliquat annee precedente</form:label></td>
                                                                        <td><form:hidden path="tdrTdb.reliquatAnneePrecedente"/>${tdr.tdrTdb.reliquatAnneePrecedente}</td>
                                                                        <td>&nbsp;</td>     
                                                                        <td><form:label path="tdrTdb.dotationGlobale">Dotation globale</form:label></td>
                                                                        <td><form:hidden path="tdrTdb.dotationGlobale"/>${tdr.tdrTdb.dotationGlobale}</td>
                                                                        <td>&nbsp;</td>    
																    </tr>
																    <tr>
                                                                        <td><form:label path="tdrTdb.sommeUtilisee">Somme utilisée</form:label></td>
                                                                        <td><form:hidden path="tdrTdb.sommeUtilisee"/>${tdr.tdrTdb.sommeUtilisee}</td>
                                                                        <td>&nbsp;</td>
																        <td><form:label path="tdrTdb.montantDisponible">Montant disponible</form:label></td>
																		<td><form:hidden path="tdrTdb.montantDisponible"/>${tdr.tdrTdb.montantDisponible}</td>
															        	<td>&nbsp;</td>	    
																    </tr>
																    <tr>
                                                                    <tr><td colspan="6">&nbsp;</td></tr>
																        <td><form:label path="tdrTdb.nbBeneficiairesApv">Nombre bénéficiaires APV</form:label></td>
																		<td><form:hidden path="tdrTdb.nbBeneficiairesApv"/>${tdr.tdrTdb.nbBeneficiairesApv}</td><!-- disabled="true" -->
															        	<td><form:errors path="tdrTdb.nbBeneficiairesApv" cssClass="error" /></td>
															        	<td>&nbsp;</td>
															        	<td>&nbsp;</td>
															        	<td>&nbsp;</td>
																    </tr>													    
																    <tr>
																        <td><form:label path="tdrTdb.nbEnfants">Nombre enfants</form:label></td>
																		<td><form:hidden path="tdrTdb.nbEnfants"/>${tdr.tdrTdb.nbEnfants}</td>
															        	<td><form:errors path="tdrTdb.nbEnfants" cssClass="error" /></td>
																        <td><form:label path="tdrTdb.nbJeunes">Nombre jeunes</form:label></td>
																		<td><form:hidden path="tdrTdb.nbJeunes"/>${tdr.tdrTdb.nbJeunes}</td>
															        	<td><form:errors path="tdrTdb.nbJeunes" cssClass="error" /></td>
															        </tr>
																    <tr>
																        <td><form:label path="tdrTdb.nbAdultesIsoles">Nombre adultes isolés</form:label></td>
																		<td><form:hidden path="tdrTdb.nbAdultesIsoles"/>${tdr.tdrTdb.nbAdultesIsoles}</td>
															        	<td><form:errors path="tdrTdb.nbAdultesIsoles" cssClass="error" /></td>
																        <td><form:label path="tdrTdb.nbSeniors">Nombre séniors</form:label></td>
																		<td><form:hidden path="tdrTdb.nbSeniors"/>${tdr.tdrTdb.nbSeniors}</td>
															        	<td><form:errors path="tdrTdb.nbSeniors" cssClass="error" /></td>
																	</tr>
																    <tr>
																        <td><form:label path="tdrTdb.status">Status</form:label></td>
																		<td><form:hidden path="tdrTdb.status"/>${tdr.tdrTdb.status}</td>
															        	<td><td>&nbsp;</td></td>	        
															        </tr>
																	<tr><td colspan="6">&nbsp;</td></tr>															        
															    	<tr>
															        	<td colspan="6">
															            	<input type="submit" value="Sauver"/>
															        	</td>
															    	</tr>
																</tbody>
															</table>
														</form:form>
														</c:if>
													</p>
												</div>
											</div>
										</div>
									</div>
								</div>
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>
		<div id="footer">
			<a href="/apv/tdr/list.html">Retour vers la liste des TDR</a>
		</div>

<%@ include file="/static/html/footer.html" %>