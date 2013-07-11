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
	<meta name="description" content="Phoenix Console">
	<link rel="shortcut icon" href="${model.webapp}/img/favicon.png">
	<res:cssSlot id="head-css"/>
	<res:jsSlot id="head-js"/>
	<res:useJs value="${res.js.local['jquery-1.8.1.min.js']}" target="head-js"/>
	<res:useJs value="${res.js.local['core.js']}" target="head-js"/>
	<res:useCss value="${res.css.local['bootstrap.min.css']}" target="head-css"/>
	<res:useCss value="${res.css.local['bootstrap-responsive.min.css']}" target="head-css"/>
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
					<img style="height: 39px" alt="Phoenix" src="${model.webapp}/img/phoenix21.png">
				</div>
				<div class="nav-collapse collapse">
					<ul class="nav nav-pills">
                       <li class="dropdown" id="menu1">
                         <a class="dropdown-toggle" data-toggle="dropdown" data-target="#" href="${model.moduleUri}/home">
                           Kernel
                           <b class="caret"></b>
                         </a>
                         <ul class="dropdown-menu">
                           <li><a href="${model.moduleUri}/home">Rollout</a></li>
                           <li><a href="${model.moduleUri}/version">Version</a></li>
                         </ul>
                       </li>
                       <li class="dropdown" id="menu1">
                         <a class="dropdown-toggle" data-toggle="dropdown" data-target="#" href="${model.moduleUri}/home?type=phoenix-agent">
                           Agent
                           <b class="caret"></b>
                         </a>
                         <ul class="dropdown-menu">
                           <li><a href="${model.moduleUri}/home?type=phoenix-agent">Rollout</a></li>
                           <li><a href="${model.moduleUri}/version?type=phoenix-agent">Version</a></li>
                         </ul>
                       </li>
					   <li><a href="${model.moduleUri}/home?op=about">About</a></li>
					</ul>
				</div>
				<!--/.nav-collapse -->
			</div>
		</div>
	</div>

	<div class="container-fluid" style="height:90%">
		<div class="row-fluid">
			<div class="span12"><jsp:doBody /></div>
		</div>
	</div>
	<br />
	<div class="container">
		<footer style="margin-bottom:0px;clear:both;text-align:center;"><center>&copy;2012 Dianping Phoenix Team, Mail: <a href="mailto:www@dianping.com">www@dianping.com</a></center></footer>
	</div>
	<!--/.fluid-container-->

	<res:useJs value="${res.js.local['bootstrap.min.js']}" target="bottom-js"/>
	<res:jsSlot id="bottom-js"/>
</body>
</html>
