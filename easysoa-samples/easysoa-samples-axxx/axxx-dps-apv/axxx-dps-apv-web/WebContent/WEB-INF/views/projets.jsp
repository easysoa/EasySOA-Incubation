<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<%@ include file="/static/html/header.html" %>

<%@ include file="/static/html/menu.html" %>

		<div class="columns-1" id="content-wrapper">
			<div class="lfr-column" id="column-1">
				<div class="lfr-portlet-column" id="layout-column_column-1">
					<div id="p_p_id_56_INSTANCE_zZ7Y_" class="portlet-boundary portlet-boundary_56_  portlet-journal-content">
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
														<b><span style="color: maroon;">Nouveau projet</span></b>
													</div>

													<a href="/apv/projet/newProjet?tdrId=${tdrId}">Créer un nouveau projet</a>

													<p style="text-align: justify;">&nbsp;</p>
													<p>
														<span style="font-size: large;"> </span>
													</p>
													<div>
														<b><span style="color: maroon;">Tous les projets</span></b>
													</div>
													<p>
														<c:if  test="${!empty projets}">
														<table class="list">
														<colgroup>
														 	<col width="200px">
														 	<col width="300px">
														  	<col width="170px">
														   	<col width="230px">
														   	<col width="50px">
														   	<col width="50px">
														   	<!--<col width="50px">-->
														</colgroup>
														<tbody>
															<tr>
															    <th>Type lieu</th>
															    <th>Période</th>
															    <th>Département</th>
															    <th>status</th>															    
															    <th>&nbsp;</th>
															    <th>&nbsp;</th>
															</tr>
															<c:forEach items="${projets}" var="projet">
																<!-- TODO alternate line colors -->
															    <tr class="odd">
															        <td>${projet.typeLieu}</td>
															        <td>${projet.periode}</td>
															        <td>${projet.departement}</td>
															        <td>${projet.status}</td>
																	<td><a href="/apv/projet/details/${projet.id}">Details</a></td>
															        <td><a href="/apv/projet/delete/${projet.id}">Delete</a></td>
															    </tr>
															</c:forEach>
														</tbody>
														</table>
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

		</div>

<%@ include file="/static/html/footer.html" %> 
