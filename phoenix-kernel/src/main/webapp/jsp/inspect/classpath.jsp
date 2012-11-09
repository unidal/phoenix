<%@ page contentType="text/html; charset=utf-8"%>
<%@ taglib prefix="a" uri="/WEB-INF/app.tld"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<jsp:useBean id="ctx" type="com.dianping.kernel.inspect.page.classpath.Context" scope="request" />
<jsp:useBean id="payload" type="com.dianping.kernel.inspect.page.classpath.Payload" scope="request" />
<jsp:useBean id="model" type="com.dianping.kernel.inspect.page.classpath.Model" scope="request" />

<a:layout>

<c:set var="kernel" value="${model.kernelArtifacts}" />
<c:set var="app" value="${model.appArtifacts}" />
<c:set var="container" value="${model.containerArtifacts}" />
<table border="1">
	<tr>
		<th>Group Id</th>
		<th>Artifact Id</th>
		<th>Version (From Container)</th>
		<th>Version (From App)</th>
		<th>Version (From Phoenix)</th>
	</tr>
	<c:forEach var="artifact" items="${model.artifacts}">
		<tr>
			<td>${artifact.groupId}</td>
			<td><label title="${artifact.path}">${artifact.artifactId}</label></td>
			<c:choose>
				<c:when test="${artifact.fromContainer}">
					<td style="color: blue">${artifact.version}</td>
					<td style="color: gray">${app[artifact.key].version}</td>
					<td style="color: gray">${kernel[artifact.key].version}</td>
				</c:when>
				<c:when test="${kernel[artifact.key].version eq app[artifact.key].version}">
					<td>${container[artifact.key].version}</td>
					<td>${artifact.version}</td>
					<td>${artifact.version}</td>
				</c:when>
				
				<c:when test="${artifact.fromKernel}">
					<td style="color: gray">${container[artifact.key].version}</td>
					<td style="color: gray">${app[artifact.key].version}</td>
					<td style="color: blue">${artifact.version}</td>
				</c:when>
				<c:otherwise>
					<td style="color: gray">${container[artifact.key].version}</td>
					<td style="color: blue">${artifact.version}</td>
					<td style="color: gray">${kernel[artifact.key].version}</td>
				</c:otherwise>
			</c:choose>
		</tr>
	</c:forEach>
</table>

</a:layout>
