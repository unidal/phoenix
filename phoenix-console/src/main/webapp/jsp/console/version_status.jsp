<%@ page contentType="text/plain; charset=utf-8" trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<jsp:useBean id="ctx" type="com.dianping.phoenix.console.page.version.Context" scope="request" />
<jsp:useBean id="payload" type="com.dianping.phoenix.console.page.version.Payload" scope="request" />
<jsp:useBean id="model" type="com.dianping.phoenix.console.page.version.Model" scope="request" />
{
	"version":"${model.creatingVersion}", 
	"index":${model.index},
	"log":"${model.log}"
}
