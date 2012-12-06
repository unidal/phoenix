<%@ tag trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="res" uri="http://www.unidal.org/webres"%>

<res:bean id="res"/>
<!DOCTYPE html>
<html lang="en">

<head>
	<title>Phoenix - ${model.page.description}</title>
	<meta charset="utf-8">
	<meta name="viewport" content="width=device-width, initial-scale=1.0">
	<meta name="description" content="">
	<link rel="shortcut icon" href="${model.webapp}/img/favicon.png">
	<res:cssSlot id="head-css"/>
	<res:jsSlot id="head-js"/>
	<res:useJs value="${res.js.local['jquery-1.8.1.min.js']}" target="head-js"/>
	<res:useJs value="${res.js.local['core.js']}" target="head-js"/>
	<res:useCss value='${res.css.local.bootstrap_css}' target="head-css"/>
	<res:useCss value="${res.js.local['bootstrap-responsive.css']}" target="head-css"/>
	<res:useCss value='${res.css.local.phoenix_css}' target="head-css"/>
	<script type="text/javascript">var contextpath = "${model.webapp}";</script>
</head>

<body data-spy="scroll" data-target=".subnav" data-offset="50">
	<div class="navbar navbar-inverse navbar-fixed-top">
		<div class="navbar-inner">
			<div class="container-fluid">
				<a class="btn btn-navbar" data-toggle="collapse" data-target=".nav-collapse"> <span class="icon-bar"></span> <span
					class="icon-bar"></span> <span class="icon-bar"></span>
				</a> 
				
				<div class="pull-right">
					<img height="39" alt="Phoenix" src="${model.webapp}/img/phoenix21.png">
				</div>
				<div class="nav-collapse collapse">
					<ul class="nav">
						<li><a href="${model.moduleUri}/home">Home</a></li>
						<li><a href="${model.moduleUri}/version">Version</a></li>
						<li><a href="${model.moduleUri}?op=about">About</a></li>
					</ul>
				</div>
				<!--/.nav-collapse -->
			</div>
		</div>
	</div>

	<div class="container-fluid" style="min-height:524px;">
		<div class="row-fluid">
			<div class="span12"><jsp:doBody /></div>
		</div>
	</div>
	<br />
	<div class="container">
		<footer><center>©2012 Dianping Phoenix Team, Mail: <a href="mailto:www@dianping.com">www@dianping.com</a></center></footer>
	</div>
	<!--/.fluid-container-->

	<res:useJs value="${res.js.local.bootstrap_js}" target="bottom-js"/>
	<res:jsSlot id="bottom-js"/>
</body>
</html>
