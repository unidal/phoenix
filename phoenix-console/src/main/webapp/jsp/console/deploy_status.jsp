<%@ page contentType="text/plain; charset=utf-8" trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<jsp:useBean id="ctx" type="com.dianping.phoenix.console.page.deploy2.Context" scope="request" />
<jsp:useBean id="payload" type="com.dianping.phoenix.console.page.deploy2.Payload" scope="request" />
<jsp:useBean id="model" type="com.dianping.phoenix.console.page.deploy2.Model" scope="request" />
{
"status":"${model.status}", 
"offset":${model.offset}, 
"content": "${model.quotedLog}",
"hosts": [ 
<c:forEach var="plan" items="${model.hostPlans}" varStatus="s1">{
"index": ${plan.index},
"host": "${plan.host}",
"status": [<c:forEach var="status" items="${plan.statuses}" varStatus="s2">
"${status}"<c:if test="${not s2.last}">,</c:if>
</c:forEach>]}<c:if test="${not s1.last}">,</c:if>
</c:forEach>]
}