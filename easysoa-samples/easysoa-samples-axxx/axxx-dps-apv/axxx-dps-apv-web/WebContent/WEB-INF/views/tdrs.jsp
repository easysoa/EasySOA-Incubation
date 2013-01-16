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
													<!--<p style="text-align: justify;">&nbsp;</p>
													<p>
														<span style="font-size: large;"> </span>
													</p>
													<div>
														<b><span style="color: maroon;">New Tdr</span></b>
													</div>
													<p>
														<form:form method="post" action="add" commandName="tdr" cssClass="uni-form">
														    <form:errors path="*" cssClass="errorblock" element="div" />
														    <table class="data">
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
													</p>

													<p style="text-align: justify;">&nbsp;</p>
													<p>
														<span style="font-size: large;"> </span>
													</p>-->
													<div>
														<b><span style="color: maroon;">All Tdrs</span></b>
													</div>
													<p>
														<c:if  test="${!empty tdrs}">
														<table class="list">
														<colgroup>
														 	<col width="200px">
														 	<col width="300px">
														  	<col width="120px">
														   	<col width="230px">
														   	<col width="50px">
														   	<col width="50px">
														   	<col width="50px">
														</colgroup>
														<tbody>
															<tr>
															    <th>Nom Structure</th>
															    <th>Type Structure</th>
															    <th>Partenaire depuis</th>
															    <th>Status</th>
															    <th>Projets</th>
															    <th>&nbsp;</th>
															    <th>&nbsp;</th>
															</tr>
															<c:forEach items="${tdrs}" var="tdr"  varStatus="loopStatus">
  															<tr class="${loopStatus.index % 2 == 0 ? 'even' : 'odd'}">
															        <td>${tdr.nomStructure}</td>
															        <!-- td>{tdr.lastname}, {tdr.firstname} </td -->
															        <td>${tdr.typeStructure}</td>
															        <td>${tdr.tdrTdb.partenaireDepuis}</td>
															        <td>${tdr.tdrTdb.status}</td>
															        <td><a href="/apv/projet/list?tdrId=${tdr.id}">projets</a></td>
															        <td><a href="/apv/tdr/details/${tdr.id}">Details</a></td>
															        <td>
															        <c:if test="${tdr.tdrTdb.status == 'created'}">
															        	<a href="/apv/tdr/delete/${tdr.id}">delete</a>
															        </c:if>
															        </td>
															    </tr>
															</c:forEach>
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