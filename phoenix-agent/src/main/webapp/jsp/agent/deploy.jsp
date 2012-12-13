<%@ page contentType="text/html; charset=utf-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="w" uri="http://www.unidal.org/web/core"%>
<jsp:useBean id="ctx" type="com.dianping.phoenix.agent.page.deploy.Context" scope="request"/>
<jsp:useBean id="payload" type="com.dianping.phoenix.agent.page.deploy.Payload" scope="request"/>
<jsp:useBean id="model" type="com.dianping.phoenix.agent.page.deploy.Model" scope="request"/>
<c:if test="${not empty ctx.errors}">
{
	"status" : "error",
	"message" : "<w:errors bundle="/META-INF/error.properties"><w:error code="*"/></w:errors>"
}
</c:if>
<c:if test="${empty ctx.errors}">
${model.responseInJson}
</c:if>
