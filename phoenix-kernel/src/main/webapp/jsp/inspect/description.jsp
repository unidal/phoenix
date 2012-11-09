<%@ page contentType="text/html; charset=utf-8"%>
<%@ taglib prefix="a" uri="/WEB-INF/app.tld"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<jsp:useBean id="ctx" type="com.dianping.kernel.inspect.page.description.Context" scope="request" />
<jsp:useBean id="payload" type="com.dianping.kernel.inspect.page.description.Payload" scope="request" />
<jsp:useBean id="model" type="com.dianping.kernel.inspect.page.description.Model" scope="request" />

<a:layout>

<c:set var="kernel" value="${model.kernelModel}" />
<c:set var="app" value="${model.appModel}" />
<c:set var="all" value="${model.allModel}" />

<h2>Welcome Files</h2>
<table border="1">
	<tr>
		<th>Name</th>
		<th>From Container</th>
		<th>From App</th>
		<th>From Phoenix</th>
	</tr>
	<c:forEach var="welcomeFile" items="${all.welcomeFiles}">
		<tr>
			<td>${welcomeFile.key}</td>
			<td align="center">
				<c:if test="${welcomeFile.fromWhere==1}">
			       <input type='checkbox' disabled checked />
				</c:if>
			</td>
			<td align="center">
				<c:if test="${welcomeFile.fromWhere==2}">
			       <input type='checkbox' disabled checked />
				</c:if>
			</td>
			<td align="center">
				<c:if test="${welcomeFile.fromWhere==3}">
			       <input type='checkbox' disabled checked />
				</c:if>
			</td>
		</tr>
	</c:forEach>
</table>

<h2>Context Parameter</h2>
<table border="1">
	<tr>
		<th>Name</th>
		<th>Value</th>
		<th>From Container</th>
		<th>From App</th>
		<th>From Phoenix</th>
	</tr>
	<c:forEach var="parameter" items="${all.parameters}">
		<tr>
			<td>${parameter.key}</td>
			<td>${parameter.value}</td>
			<td align="center">
				<c:if test="${parameter.fromWhere==1}">
			       <input type='checkbox' disabled checked />
				</c:if>
			</td>
			<td align="center">
				<c:if test="${parameter.fromWhere==2}">
			       <input type='checkbox' disabled checked />
				</c:if>
			</td>
			<td align="center">
				<c:if test="${parameter.fromWhere==3}">
			       <input type='checkbox' disabled checked />
				</c:if>
			</td>
		</tr>
	</c:forEach>
</table>

<h2>Listener</h2>
<table border="1">
	<tr>
		<th>Listener Class</th>
		<th>From Container</th>
		<th>From App</th>
		<th>From Phoenix</th>
	</tr>
	<c:forEach var="listener" items="${all.listeners}">
		<tr>
			<td><label title="index: ${listener.value}">${listener.key}</label></td>
			<td align="center">
				<c:if test="${listener.fromWhere==1}">
			       <input type='checkbox' disabled checked />
				</c:if>
			</td>
			<td align="center">
				<c:if test="${listener.fromWhere==2}">
			       <input type='checkbox' disabled checked />
				</c:if>
			</td>
			<td align="center">
				<c:if test="${listener.fromWhere==3}">
			       <input type='checkbox' disabled checked />
				</c:if>
			</td>
		</tr>
	</c:forEach>
</table>

<h2>Servlet</h2>
<table border="1">
	<tr>
		<th>Name</th>
		<th>Servlet Class</th>
		<th>URL Pattern</th>
		<th>From Container</th>
		<th>From App</th>
		<th>From Phoenix</th>
		
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
			<td align="center">
				<c:if test="${servlet.value.fromWhere==1}">
			       <input type='checkbox' disabled checked />
				</c:if>
			</td>
			<td align="center">
				<c:if test="${servlet.value.fromWhere==2}">
			       <input type='checkbox' disabled checked />
				</c:if>
			</td>
			<td align="center">
				<c:if test="${servlet.value.fromWhere==3}">
			       <input type='checkbox' disabled checked />
				</c:if>
			</td>
			
		</tr>
	</c:forEach>
</table>

<h2>Filter</h2>
<table border="1">
	<tr>
		<th>Name</th>
		<th>Filter Class</th>
		<th>URL Pattern</th>
		<th>From Container</th>
		<th>From App</th>
		<th>From Phoenix</th>
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
			<td align="center">
				<c:if test="${filter.fromWhere==1}">
			       <input type='checkbox' disabled checked />
				</c:if>
			</td>
			<td align="center">
				<c:if test="${filter.fromWhere==2}">
			       <input type='checkbox' disabled checked />
				</c:if>
			</td>
			<td align="center">
				<c:if test="${filter.fromWhere==3}">
			       <input type='checkbox' disabled checked />
				</c:if>
			</td>
		</tr>
	</c:forEach>
</table>

</a:layout>