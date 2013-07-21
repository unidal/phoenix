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
	<res:useCss value="${res.css.local['overview.css']}" target="head-css" />
	<ul class="breadcrumb">
		<li><a href="${model.webapp}/console/home">Home</a><span class="divider">/</span></li>
		<li class="active">Overview</li>
	</ul>
	<div class="container-fluid" style="min-height: 500px">
		<div class="row-fluid">
			<div class="span3">
				<div id="accordion" class="accordion">
					<c:set var="flagc" scope="page" value="true" />
					<c:forEach var="product" items="${model.products}">
						<div class="accordion-group">
							<div class="accordion-heading">
								<a class="accordion-toggle btn btn-accordion" data-toggle="collapse" data-parent="#accordion" href="#_${product.name}">${product.name}</a>
							</div>
							<c:choose>
								<c:when test="${flagc}">
									<div id="_${product.name}" class="accordion-body collapse in">
										<div class="accordion-inner">
											<ul class="nav nav-pills nav-stacked">
												<c:set var="flagl" scope="page" value="true" />
												<c:forEach var="domain" items="${product.domains}">
													<c:choose>
														<c:when test="${flagl}">
															<li class="active"><a href="#">${domain.key}</a></li>
															<c:set var="flagl" scope="page" value="false" />
														</c:when>
														<c:otherwise>
															<li><a href="#">${domain.key}</a></li>
														</c:otherwise>
													</c:choose>
												</c:forEach>
											</ul>
										</div>
									</div>
									<c:set var="flagc" scope="page" value="false" />
								</c:when>
								<c:otherwise>
									<div id="_${product.name}" class="accordion-body collapse">
										<div class="accordion-inner">
											<ul class="nav nav-pills nav-stacked">
												<c:set var="flagl" scope="page" value="true" />
												<c:forEach var="domain" items="${product.domains}">
													<c:choose>
														<c:when test="${flagl}">
															<li class="active"><a href="#">${domain.key}</a></li>
															<c:set var="flagl" scope="page" value="false" />
														</c:when>
														<c:otherwise>
															<li><a href="#">${domain.key}</a></li>
														</c:otherwise>
													</c:choose>
												</c:forEach>
											</ul>
										</div>
									</div>
								</c:otherwise>
							</c:choose>

						</div>
					</c:forEach>
				</div>
			</div>
			<div class="span9">
				<div id="inner"></div>
			</div>
		</div>
	</div>
	
	<res:useJs value="${res.js.local['jquery.dataTables.min.js']}" target="datatable-js" />
	<res:useJs value="${res.js.local['DT_bootstrap.js']}" target="dtboot-js" />
	<res:useJs value="${res.js.local['FixedColumns.js']}" target="fixcolumn-js" />
	<res:useJs value="${res.js.local['overview.js']}" target="overview-js" />
	<res:jsSlot id="datatable-js" />
	<res:jsSlot id="dtboot-js" />
	<res:jsSlot id="fixcolumn-js" />
	<res:jsSlot id="overview-js" />
</a:layout>