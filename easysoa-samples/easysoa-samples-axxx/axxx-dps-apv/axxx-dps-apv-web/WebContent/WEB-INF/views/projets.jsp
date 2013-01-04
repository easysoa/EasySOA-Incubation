<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<%@ include file="/static/html/header.html" %>

<%@ include file="/static/html/menu.html" %>

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
														<b><span style="color: maroon;">Nouveau projet</span></b>
													</div>
													<p>
														<form:form method="post" action="add" commandName="projet" cssClass="uni-form">
														    <table>
														    <form:hidden path="tdr" value="${projet.tdr}" />
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
													</p>

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
														<tbody>
															<tr>
															    <th>Type lieu</th>
															    <th>...</th>
															    <th>&nbsp;</th>
															</tr>
															<c:forEach items="${projets}" var="projet">
																<!-- TODO alternate line colors -->
															    <tr class="odd">
															        <td>${projet.typeLieu}</td>
															        <td>...</td>
															        <td><a href="delete/${projet.id}">delete</a></td>
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
