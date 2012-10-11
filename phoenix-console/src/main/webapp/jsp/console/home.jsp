<%@ page contentType="text/html; charset=utf-8"%>
<%@ taglib prefix="a" uri="/WEB-INF/app.tld"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="res" uri="http://www.unidal.org/webres"%>
<jsp:useBean id="ctx"
	type="com.dianping.phoenix.console.page.home.Context" scope="request" />
<jsp:useBean id="payload"
	type="com.dianping.phoenix.console.page.home.Payload" scope="request" />
<jsp:useBean id="model"
	type="com.dianping.phoenix.console.page.home.Model" scope="request" />

<a:body>

	<div class="row-fluid">
		<div class="row-fluid">
			<!-- project search form -->
			<form class="form-search" action="?op=home">
				<div class="input-append">
					<input type="text" name="keyword" value="${payload.keyword}"
						class="search-query input-large" placeholder="jar-version">
					<button type="submit" class="btn">Search</button>
				</div>
			</form>
		</div>
		<!-- project list -->
		<div class="row-fluid">
			<table class="table table-striped table-hover">
				<caption></caption>
				<thead>
					<tr>
						<th width="10%">Project</th>
						<th width="10%">Owner</th>
						<th width="20%">Hosts</th>
						<th>DependencyJars</th>
					</tr>
				</thead>
				<tbody>
					<c:forEach var="project" items="${model.projects}">
						<tr>
							<td><a href="?op=project&name=${project.name}">${project.name}</a></td>
							<td>${project.owner}</td>
							<td>${project.hosts}</td>
							<td>${project.dependencyJars}</td>
						</tr>
					</c:forEach>
				</tbody>
			</table>
		</div>
		<!--/row-->
	</div>
	<!--/row-->

</a:body>
