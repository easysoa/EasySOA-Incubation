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
														<b><span style="color: maroon;">Projet details</span></b>
													</div>
													<p>
														<c:if test="${!empty projet}">
														<form:form method="post" commandName="projet" cssClass="uni-form">
														    <form:errors path="*" cssClass="errorblock" element="div" />
															<table class="data">
																<colgroup> <!-- 1000 -->
																	<col width="250px">
																	<col width="150px">
																	<col width="100px">
																	<col width="250px">
																	<col width="150px">
																	<col width="100px">
																</colgroup>
																<tbody>
																	<!-- Projet ID -->
																	<form:hidden path="id"/>
																	<!-- TDR ID -->
																	<form:hidden path="tdr.id" />
																	<tr>
																    	<td><form:label path="status">Status</form:label></td>
																		<td><form:hidden path="status"  />${projet.status}</td>
															        	<td><form:errors path="status" cssClass="error" /></td>
																    	<td><form:label path="typeLieu">Type lieu</form:label></td>
																		<td><form:hidden path="typeLieu" />${projet.typeLieu}</td>
															        	<td><form:errors path="typeLieu" cssClass="error" /></td>
																	</tr>
																	<tr>
																    	<td><form:label path="periode">P�riode</form:label></td>
																		<td><form:hidden path="periode" />${projet.periode}</td>
															        	<td><form:errors path="periode" cssClass="error" /></td>
																    	<td><form:label path="departement">D�partement</form:label></td>
																		<td><form:hidden path="departement" />${projet.departement}</td>
															        	<td><form:errors path="departement" cssClass="error" /></td>
																	</tr>																	
																	<tr>
																    	<td><form:label path="enfantsBenefs.nbBeneficiaires">Enfants : nombre de b�n�ficiaires</form:label></td>
																		<td><form:input path="enfantsBenefs.nbBeneficiaires" /></td>
															        	<td><form:errors path="enfantsBenefs.nbBeneficiaires" cssClass="error" /></td>
																    	<td><form:label path="enfantsBenefs.montantApv">Montant Apv</form:label></td>
																		<td><form:input path="enfantsBenefs.montantApv" /></td>
															        	<td><form:errors path="enfantsBenefs.montantApv" cssClass="error" /></td>															        
															        </tr>
																	<tr>
																    	<td><form:label path="jeunesBenefs.nbBeneficiaires">Jeunes : nombre de b�n�ficiaires</form:label></td>
																		<td><form:input path="jeunesBenefs.nbBeneficiaires" /></td>
															        	<td><form:errors path="jeunesBenefs.nbBeneficiaires" cssClass="error" /></td>
																    	<td><form:label path="jeunesBenefs.montantApv">Montant Apv</form:label></td>
																		<td><form:input path="jeunesBenefs.montantApv" /></td>
															        	<td><form:errors path="jeunesBenefs.montantApv" cssClass="error" /></td>															        
															        </tr>
															    	<tr>
																    	<td><form:label path="adultesIsolesBenefs.nbBeneficiaires">Adultes isol�s : nombre de b�n�ficiaires</form:label></td>
																		<td><form:input path="adultesIsolesBenefs.nbBeneficiaires" /></td>
															        	<td><form:errors path="adultesIsolesBenefs.nbBeneficiaires" cssClass="error" /></td>
																    	<td><form:label path="adultesIsolesBenefs.montantApv">Montant Apv</form:label></td>
																		<td><form:input path="adultesIsolesBenefs.montantApv" /></td>
															        	<td><form:errors path="adultesIsolesBenefs.montantApv" cssClass="error" /></td>															        
															        </tr>
															    	<tr>
																    	<td><form:label path="seniorsBenefs.nbBeneficiaires">S�niors : nombre de b�n�ficiaires</form:label></td>
																		<td><form:input path="seniorsBenefs.nbBeneficiaires" /></td>
															        	<td><form:errors path="seniorsBenefs.nbBeneficiaires" cssClass="error" /></td>
																    	<td><form:label path="seniorsBenefs.montantApv">Montant Apv</form:label></td>
																		<td><form:input path="seniorsBenefs.montantApv" /></td>
															        	<td><form:errors path="seniorsBenefs.montantApv" cssClass="error" /></td>															        
															        </tr>
																	<tr><td colspan="6">&nbsp;</td></tr>															        
															    	<tr>
															        	<td colspan="3">
															            	<input type="submit" value="Sauver" onclick="saveSubmit(this.form);"/>
															        	</td>
															        	<td colspan="3">
															        		<c:choose>
																        		<c:when test="${projet.totalBenefs.nbBeneficiaires > 0 && projet.totalBenefs.montantApv > 0 && projet.status == 'created'}">
																        			<input type="submit" value="Approuver" onclick="approveSubmit(this.form);"/>
																        		</c:when>
																        		<c:when test="${projet.status == 'approved'}">
																        			&nbsp;
																        		</c:when>																        		
																        		<c:otherwise>
																        			Les conditions ne sont pas r�unies, l'approbation du projet n'est pas possible.
																        			<br/>
																        			<input type="submit" value="Approuver" onclick="approveSubmit(this.form);" disabled="true"/>
																        		</c:otherwise>
															        		</c:choose>
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
			<a href="/apv/projet/list.html?trdId=${projet.tdr.id}">Retour � la liste des projets</a>
		</div>

<%@ include file="/static/html/footer.html" %>