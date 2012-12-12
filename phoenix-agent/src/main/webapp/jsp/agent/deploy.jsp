<%@ page contentType="text/html; charset=utf-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="w" uri="http://www.unidal.org/web/core"%>
<jsp:useBean id="ctx" type="com.dianping.phoenix.agent.page.deploy.Context" scope="request"/>
<jsp:useBean id="payload" type="com.dianping.phoenix.agent.page.deploy.Payload" scope="request"/>
<jsp:useBean id="model" type="com.dianping.phoenix.agent.page.deploy.Model" scope="request"/>
<c:if test="${not empty ctx.errors}">
{
	"status" : "error",
	"error" : "
		<w:error code="deployId.invalid">deployId invalid</w:error>
		<w:error code="version.invalid">version invalid</w:error>
		<w:error code="duplicate_txid">duplicate txid</w:error>
		<w:error code="another_tx_runnint">another tx runnint</w:error>
	"
}
</c:if>
<c:if test="${empty ctx.errors}">
${model.responseInJson}
</c:if>
