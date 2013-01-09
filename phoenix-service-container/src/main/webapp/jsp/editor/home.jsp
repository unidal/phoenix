<%@ page contentType="text/html; charset=utf-8" %>
<%@ taglib prefix="a" uri="/WEB-INF/app.tld"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="w" uri="http://www.unidal.org/web/core"%>
<jsp:useBean id="ctx" type="com.dianping.service.editor.page.home.Context" scope="request"/>
<jsp:useBean id="payload" type="com.dianping.service.editor.page.home.Payload" scope="request"/>
<jsp:useBean id="model" type="com.dianping.service.editor.page.home.Model" scope="request"/>

<a:layout>

<br><br>
<table class="table">
<c:forEach var="service" items="${model.deployment.activeServices}">
	<c:forEach var="property" items="${service.properties}" varStatus="status">
		<tr>
			<c:if test="${status.first}">
				<td rowspan="${w:size(service.properties)}">${service.type.name}(${service.alias})</td>
			</c:if>
			<td>${property.name}</td>
			<td><input type="text" name="property.${property.name}" value="${property.value}" size="40"/></td>
		</tr>
	</c:forEach>
</c:forEach>
</table>

</a:layout>