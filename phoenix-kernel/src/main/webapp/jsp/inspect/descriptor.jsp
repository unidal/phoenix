<%@ page contentType="text/html; charset=utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<jsp:useBean id="ctx" type="com.dianping.kernel.inspect.page.descriptor.Context" scope="request" />
<jsp:useBean id="payload" type="com.dianping.kernel.inspect.page.descriptor.Payload" scope="request" />
<jsp:useBean id="model" type="com.dianping.kernel.inspect.page.descriptor.Model" scope="request" />

<c:set var="kernel" value="${model.kernelModel}" />
<c:set var="app" value="${model.appModel}" />
<c:set var="all" value="${model.allModel}" />

<h2>Welcome Files</h2>
<table border="1">
	<tr>
		<th>Name</th>
	</tr>
	<c:forEach var="welcomeFile" items="${all.welcomeFiles}">
		<tr>
			<td>${welcomeFile}</td>
		</tr>
	</c:forEach>
</table>

<h2>Listener</h2>
<table border="1">
	<tr>
		<th>Listener Class</th>
	</tr>
	<c:forEach var="listener" items="${all.listeners}">
		<tr>
			<td><label title="index: ${listener.value}">${listener.key}</label></td>
		</tr>
	</c:forEach>
</table>

<h2>Servlet</h2>
<table border="1">
	<tr>
		<th>Name</th>
		<th>Servlet Class</th>
		<th>URL Pattern</th>
	</tr>
	<c:forEach var="servlet" items="${all.servlets}">
		<c:set var="pattern" value="${servlet.key}" />
		<c:set var="wrapper" value="${servlet.value.key}" />
		<c:set var="initParameters" value="${servlet.value.value}" />
		<tr>
			<td><label title="
loadOnStartup: ${wrapper.loadOnStartup}
init-parameters: ${initParameters}
">${not empty wrapper.jspFile ? wrapper.jspFile : wrapper.name}</label></td>
			<td>${wrapper.servletClass}</td>
			<td>${pattern}</td>
		</tr>
	</c:forEach>
</table>

<h2>Filter</h2>
<table border="1">
	<tr>
		<th>Name</th>
		<th>Filter Class</th>
		<th>URL Pattern</th>
	</tr>
	<c:forEach var="filter" items="${all.filters}">
		<c:set var="def" value="${filter.key}" />
		<c:set var="map" value="${filter.value}" />
		<tr>
			<td><label
				title="
matchAllServletNames: ${map.matchAllServletNames}
matchAllUrlPatterns: ${map.matchAllUrlPatterns}
init-parameters: ${def.parameterMap}
">${def.filterName}</label></td>
			<td>${def.filterClass}</td>
			<td><c:forEach var="pattern" items="${map.URLPatterns}">
					${pattern}<br>
				</c:forEach></td>
		</tr>
	</c:forEach>
</table>