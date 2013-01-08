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
										<p><h1>TDR precomptes</h1></p>
												<div class="list">	
													<p>
														<c:if  test="${!empty tdrPrecomptes}">
														<table class="list" >
														<colgroup>
														 	<col width="200px">
														 	<col width="400px">
														  	<col width="120px">
														   	<col width="230px">
														   	<col width="50px">
														</colgroup>														
														<tbody>
															<tr>
															    <th>Nom Structure</th>
															    <th>Ville</th>
															    <th></th>
															    <th></th>
															    <th></th>
															</tr>
															<c:forEach items="${tdrPrecomptes}" var="tdr">
															<!-- TODO alternate colors -->
														    <tr class="odd APPROVED">
														        <td>${tdr.nomStructure}</td>
														        <td>${tdr.ville}</td>
														        <td><a href="/apv/tdrprecompte/details/${tdr.id}">Details</a></td>
														        <td>...</td>
														        <td><a href="/apv/tdrprecompte/delete/${tdr.id}">delete</a></td>
														    </tr>
															</c:forEach>
														</tbody>
														</table>
														</c:if>													
													</p>
												</div>
												<p>&nbsp;</p>
												<p><a href="/apv/tdrprecompte/newTdr">(TEST SEULEMENT) Créer une nouvelle TDR</a></p>
												<p>&nbsp;</p>
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