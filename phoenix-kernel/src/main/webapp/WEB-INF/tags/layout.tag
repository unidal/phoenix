<%@ tag isELIgnored="false"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<jsp:useBean id="navBar" class="com.dianping.kernel.inspect.view.NavigationBar" scope="page" />

<html>
<head>
<title>Phoenix Inspection - ${model.page.description}</title>
<meta http-equiv="content-type" content="text/html; charset=utf-8" />
<link href="${model.webapp}/css/layout.css" type="text/css" rel="stylesheet">
</head>

<body>
	<ul class="tabs">
		<c:forEach var="page" items="${navBar.visiblePages}">
			<c:if test="${page.standalone}">
				<li ${model.page.name== page.name ? 'class="selected"' : ''}><a href="${model.webapp}/${page.moduleName}/${page.path}">${page.title}</a></li>
			</c:if>
			<c:if test="${not page.standalone and model.page.name == page.name}">
				<li class="selected">${page.title}</li>
			</c:if>
		</c:forEach>
	</ul>

	<jsp:doBody />
</body>
</html>
