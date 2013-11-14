<!DOCTYPE HTML>
<%@ page contentType="text/html; charset=utf-8"%>

<%@ taglib prefix="a" uri="http://www.dianping.com/phoenix/console"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="w" uri="http://www.unidal.org/web/core"%>
<%@ taglib prefix="res" uri="http://www.unidal.org/webres"%>

<jsp:useBean id="ctx" type="com.dianping.phoenix.console.page.home.Context" scope="request" />
<jsp:useBean id="payload" type="com.dianping.phoenix.console.page.home.Payload" scope="request" />
<jsp:useBean id="model" type="com.dianping.phoenix.console.page.home.Model" scope="request" />

<a:layout>
	<res:useCss value="${res.css.local['DT_bootstrap.css']}" target="head-css" />

	<ul class="breadcrumb">
		<li><a href="${model.webapp}/console/home">Home</a><span class="divider">/</span></li>
		<c:choose>
			<c:when test="${payload.type=='phoenix-agent'}">
				<li class="active">Agent</li>
			</c:when>
			<c:otherwise>
				<li class="active">Kernel</li>
			</c:otherwise>
		</c:choose>
	</ul>
	<div class="alert alert-success" style="display: none;">
		<button type="button" class="close" data-dismiss="alert">×</button>
		<strong>Query Condition: </strong>&emsp;<span id="queryInfo"></span>
	</div>
	<input type="hidden" id="payload_dependencies" value="${payload.dependencies}">
	<input type="hidden" id="payload_operators" value="${payload.operators}">
	<input type="hidden" id="payload_versions" value="${payload.versions}">
	<input type="hidden" id="payload_joints" value="${payload.joints}">
	<input type="hidden" id="payload_agentversion" value="${payload.agentVersion}">
	<input type="hidden" id="payload_agentoperator" value="${payload.agentOperator}">
	<div class="container-fluid">
		<div class="row-fluid">
			<div class="span12">
				<ul class="nav nav-tabs" id="myTab">
					<c:set var="flag" scope="page" value="true" />
					<c:set var="totalActive" scope="page" value="0" />
					<c:set var="totalInactive" scope="page" value="0" />
					<c:forEach var="product" items="${model.products}">
						<c:set var="totalActive" scope="page" value="${totalActive + product.productActiveCount}" />
						<c:set var="totalInactive" scope="page" value="${totalInactive + product.productInactiveCount}" />
						<c:choose>
							<c:when test="${flag}">
								<li class="active bl"><a href="#${product.name}">${product.name}: ${product.productActiveCount}/${product.productInactiveCount + product.productActiveCount}</a></li>
								<c:set var="flag" scope="page" value="false" />
							</c:when>
							<c:otherwise>
								<li class="bl"><a href="#${product.name}">${product.name}: ${product.productActiveCount}/${product.productInactiveCount + product.productActiveCount}</a></li>
							</c:otherwise>
						</c:choose>
					</c:forEach>
					<li class="pull-right"><span class="badge badge-success">${totalActive}</span> | <span class="badge badge-important">${totalInactive}</span></li>
				</ul>
			</div>
		</div>
		
		<div class="tab-content">
			<c:set var="flag" scope="page" value="true" />
			<c:forEach var="product" items="${model.products}">
				<c:choose>
					<c:when test="${flag}">
						<div class="tab-pane active row-fluid" id="${product.name}">
							<c:set var="flag" scope="page" value="false" />
					</c:when>
					<c:otherwise>
						<div class="tab-pane row-fluid" id="${product.name}">
					</c:otherwise>
				</c:choose>
				<div>
					<table class="table table-bordered table-striped table-condensed">
						<thead>
							<tr>
								<th>Project</th>
								<th><span class="badge badge-success">${product.productActiveCount}</span></th>
								<th><span class="badge badge-important">${product.productInactiveCount}</span></th>
								<th>Owner</th>
								<th>Kernel Versions</th>
								<th>App Versions</th>
							</tr>
						</thead>
						<tbody>
							<c:forEach var="domain" items="${product.domains}">
								<tr>
									<td><a class="toProject" href="?op=project&type=${payload.plan.warType.name}&project=${domain.value.name}">${domain.value.name}</a></td>
									<td><span class="badge badge-success">${domain.value.activeCount}</span></td>
									<td><span class="badge badge-important">${domain.value.inactiveCount}</span></td>
									<td><c:choose>
											<c:when test="${fn:length(domain.value.owners) eq 0}">N/A</c:when>
											<c:otherwise>
												<c:forEach var="owner" items="${domain.value.owners}">
													${owner}&nbsp;
													</c:forEach>
											</c:otherwise>
										</c:choose></td>
									<td><c:choose>
											<c:when test="${fn:length(domain.value.kernelVersions) eq 0}">N/A</c:when>
											<c:otherwise>
												<c:forEach var="kerVersion" items="${domain.value.kernelVersions}">
													${kerVersion}&nbsp;
													</c:forEach>
											</c:otherwise>
										</c:choose></td>
									<td><c:choose>
											<c:when test="${fn:length(domain.value.appVersions) eq 0}">N/A</c:when>
											<c:otherwise>
												<c:forEach var="appVersion" items="${domain.value.appVersions}">
													${appVersion}&nbsp;
													</c:forEach>
											</c:otherwise>
										</c:choose></td>
								</tr>
							</c:forEach>
						</tbody>
					</table>
				</div>
		</div>
		</c:forEach>
	</div>
	</div>
	<res:useJs value="${res.js.local['jquery.dataTables.min.js']}" target="datatable-js" />
	<res:useJs value="${res.js.local['DT_bootstrap.js']}" target="b-datatable-js" />
	<res:useJs value="${res.js.local['console-home.js']}" target="home-js" />
	<res:jsSlot id="datatable-js" />
	<res:jsSlot id="b-datatable-js" />
	<res:jsSlot id="home-js" />
</a:layout>