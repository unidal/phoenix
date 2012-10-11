<%@ tag trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="res" uri="http://www.unidal.org/webres"%>

<res:bean id="res"/>
<!DOCTYPE html>
<html lang="en">

<head>
<title>Egret - ${model.page.description}</title>
<meta charset="utf-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<meta name="description" content="">
<meta name="author" content="">
<res:cssSlot id="head-css"/>
<res:jsSlot id="head-js"/>
<res:useCss value='${res.css.local.bootstrap_css}' target="head-css"/>
<res:useCss value='${res.css.local.body_css}' target="head-css"/>
</head>

<body>
	<div class="navbar navbar-inverse navbar-fixed-top">
		<div class="navbar-inner">
			<div class="container-fluid">
				<a class="btn btn-navbar" data-toggle="collapse" data-target=".nav-collapse"> <span class="icon-bar"></span> <span
					class="icon-bar"></span> <span class="icon-bar"></span>
				</a> <a class="brand" href="/egret/console">Egret</a>
				<div class="nav-collapse collapse">
					<ul class="nav">
						<li><a href="${model.webapp}/${page.moduleName}console">Home</a></li>
						<li><a href="${model.webapp}/${page.moduleName}console?op=about">About</a></li>
					</ul>
				</div>
				<!--/.nav-collapse -->
			</div>
		</div>
	</div>

	<div class="container-fluid">
		<jsp:doBody />

		<hr>

		<footer>
			<p> Egret@Dianping 2012</p>
		</footer>

	</div>
	<!--/.fluid-container-->

	<res:useJs value="${res.js.local['jquery-1.8.1.min.js']}" target="bottom-js"/>
	<res:useJs value="${res.js.local.bootstrap_js}" target="bottom-js"/>
	<res:jsSlot id="bottom-js"/>
</body>
</html>
