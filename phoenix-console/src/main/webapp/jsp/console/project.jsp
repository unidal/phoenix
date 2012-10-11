<%@ page contentType="text/html; charset=utf-8"%>
<%@ taglib prefix="a" uri="/WEB-INF/app.tld"%>
<%@ taglib prefix="res" uri="http://www.unidal.org/webres"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<jsp:useBean id="ctx" type="com.dianping.phoenix.console.page.home.Context" scope="request" />
<jsp:useBean id="payload" type="com.dianping.phoenix.console.page.home.Payload" scope="request" />
<jsp:useBean id="model" type="com.dianping.phoenix.console.page.home.Model" scope="request" />

<a:body>

	<div class="row-fluid">
			<h2>${model.project.name}</h2>
			<table class="table table-striped table-bordered">
			<tbody>
				<tr>
					<th width="15%">Owner</th>
					<td>${model.project.owner}</td>
				</tr>
				<tr>
					<th>Dependency</th>
					<td>
							<c:forEach var="jar" items="${model.project.dependencyJars}">
								${jar}&nbsp;&nbsp;
							</c:forEach>
					</td>
				</tr>
			</tbody>
		</table>
	</div>

	<form class="form-horizontal" method="get">
		<input type="hidden" name="op" value="deploy"/>
		<h2>Host List</h2>
				<div class="row-fluid">
			<table class="table table-striped table-bordered table-condensed">
				<thead>
					<tr>
						<th width="15%">IP</th>
						<th>Operation</th>
					</tr>
				</thead>
				<tbody>
					<c:forEach var="ip" items="${model.project.hosts}">
						<tr>
							<td>${ip}</td>
							<td><input type="checkbox" name="hosts" value="${ip}"></td>
						</tr>
					</c:forEach>
				</tbody>
			</table>
		</div>

		<div class="row-fluid">
			<select name="plan">
				<c:forEach var="plan" items="${model.deployPlans}">
					<option>${plan}</option>
				</c:forEach>
			</select>
			<button type="submit" class="btn btn-primary">Deploy</button>
		</div>
	</form>
</a:body>