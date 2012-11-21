<%@ page contentType="text/html; charset=utf-8"%>
<%@ taglib prefix="a" uri="http://www.dianping.com/phoenix/console"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="w" uri="http://www.unidal.org/web/core"%>
<jsp:useBean id="ctx" type="com.dianping.phoenix.console.page.home.Context" scope="request" />
<jsp:useBean id="payload" type="com.dianping.phoenix.console.page.home.Payload" scope="request" />
<jsp:useBean id="model" type="com.dianping.phoenix.console.page.home.Model" scope="request" />

<a:layout>
	<div class="container-fluid">
		<div class="row-fluid">
			<div class="span12">
				<ul class="nav nav-tabs">
					<li class="active"><a href="#">主站</a></li>
					<li><a href="#">团购</a></li>
					<li><a href="#">手机</a></li>
					<li><a href="#">其他</a></li>
				</ul>
			</div>
		</div>
		<div class="row-fluid">
			<div class="span12">
				<table class="table table-bordered table-striped table-condensed table-page">
					<thead>
						<tr>
							<th width="35">Status</th>
							<th width="220">Project</th>
							<th width="120">Owner</th>
							<th width="150">Machine(Active/Inactive)</th>
							<th>Kernel Versions</th>
							<th>App Versions</th>
						</tr>
					</thead>
					<tbody>
						<c:forEach var="project" items="${model.projects}">
							<tr>
								<td><img src="${model.webapp}/img/green.gif"></td>
								<td><a href="?op=project&project=${project.name}">${project.name}</a></td>
								<td>${project.owner}</td>
								<td>${w:size(project.hosts)}/0</td>
								<td>1.2, 1.5, 1.6</td>
								<td>2.0.0, 2.0.1</td>
							</tr>
						</c:forEach>
					</tbody>
				</table>
			</div>
		</div>
	</div>
</a:layout>