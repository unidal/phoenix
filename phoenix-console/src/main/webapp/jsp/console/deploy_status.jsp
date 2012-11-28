<%@ page contentType="text/plain; charset=utf-8" trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<jsp:useBean id="ctx" type="com.dianping.phoenix.console.page.deploy.Context" scope="request" />
<jsp:useBean id="payload" type="com.dianping.phoenix.console.page.deploy.Payload" scope="request" />
<jsp:useBean id="model" type="com.dianping.phoenix.console.page.deploy.Model" scope="request" />
{
"status":"${model.status}", 
"hosts": [ 
<c:forEach var="entry" items="${model.deploy.hosts}" varStatus="s1">
<c:set var="host" value="${entry.value}"/>{
"offset": 0,
"progress": 10,
"step": "stop container",
"log": "this is mock log!"
}<c:if test="${not s1.last}">,</c:if>
</c:forEach>]
}
