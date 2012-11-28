<%@ page contentType="text/plain; charset=utf-8" trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<jsp:useBean id="ctx" type="com.dianping.phoenix.console.page.deploy.Context" scope="request" />
<jsp:useBean id="payload" type="com.dianping.phoenix.console.page.deploy.Payload" scope="request" />
<jsp:useBean id="model" type="com.dianping.phoenix.console.page.deploy.Model" scope="request" />
{
"status":"${model.deploy.status}", 
"hosts": [ 
<c:forEach var="entry" items="${model.deploy.hosts}" varStatus="status">
<c:set var="host" value="${entry.value}"/>{
"offset": ${host.offset},
"progress": ${host.progress},
"step": "${host.currentStep}",
"log": "${host.log}"
}<c:if test="${not status.last}">,</c:if>
</c:forEach>]
}
