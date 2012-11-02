<%@ page contentType="text/html; charset=utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<jsp:useBean id="ctx" type="com.dianping.kernel.inspect.page.classpath.Context" scope="request" />
<jsp:useBean id="payload" type="com.dianping.kernel.inspect.page.classpath.Payload" scope="request" />
<jsp:useBean id="model" type="com.dianping.kernel.inspect.page.classpath.Model" scope="request" />

<c:set var="kernel" value="${model.kernelArtifacts}" />
<c:set var="app" value="${model.appArtifacts}" />

<table border="1">
	<tr>
		<th>Group Id</th>
		<th>Artifact Id</th>
		<th>Version (from app)</th>
		<th>Version (from kernel)</th>
	</tr>
	<c:forEach var="artifact" items="${model.artifacts}">
		<tr>
			<td>${artifact.groupId}</td>
			<td><label title="${artifact.path}">${artifact.artifactId}</label></td>
			<c:choose>
				<c:when test="${kernel[artifact.key].version eq app[artifact.key].version}">
					<td>${artifact.version}</td>
					<td>${artifact.version}</td>
				</c:when>
				<c:when test="${artifact.fromKernel}">
					<td style="color: gray">${app[artifact.key].version}</td>
					<td style="color: blue">${artifact.version}</td>
				</c:when>
				<c:otherwise>
					<td style="color: blue">${artifact.version}</td>
					<td style="color: gray">${kernel[artifact.key].version}</td>
				</c:otherwise>
			</c:choose>
		</tr>
	</c:forEach>
</table>