<!DOCTYPE HTML>
<%@ page contentType="text/html; charset=utf-8"%>

<%@ taglib prefix="a" uri="http://www.dianping.com/phoenix/console"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="w" uri="http://www.unidal.org/web/core"%>
<%@ taglib prefix="res" uri="http://www.unidal.org/webres"%>

<jsp:useBean id="ctx" type="com.dianping.phoenix.console.page.home.Context" scope="request" />
<jsp:useBean id="payload" type="com.dianping.phoenix.console.page.home.Payload" scope="request" />
<jsp:useBean id="model" type="com.dianping.phoenix.console.page.home.Model" scope="request" />

<a:layout>
	<ul class="breadcrumb">
		<li><a href="${model.webapp}/console/home">Home</a><span class="divider">/</span></li>
		<li><a href="${model.webapp}/console/home">Kernel</a><span class="divider">/</span></li>
		<li class="active">Search</li>
	</ul>
	<w:errors>
		<h3>Error occurred:</h3>
		<pre class="error">
	      <w:error code="search.error">Please check your query form.</w:error>
	      <w:error code="*">
				<strong>\${code}</strong>: \${exception.message}</w:error>
	   </pre>
	</w:errors>
	<res:useCss value="${res.css.local['search-jar.css']}" target="head-css" />
	<div class="container-fluid">
		<div class="row-fluid span10" style="margin: 0 0 -20px 8%;">
			<div class="well">
				<form class="form-inline" id="queryform">
					<input type="hidden" name="op" value="home" />
					<input type="hidden" name="type" value="phoenix-kernel" />
					<div id="query1" style="margin: 10px 10px 0 10px">
						<a id="qadd" href="#" style="margin: 0 3px 0 13px; text-decoration: none">
							<img src="${model.webapp}/img/plus.png" style="width: 16px">
						</a>
						<a id="qdel" href="#" style="margin: 0 21px 0 5px; text-decoration: none">
							<img src="${model.webapp}/img/minus.png" style="width: 16px">
						</a>
						<input id="dep1" name="dependency" class="dependency" autocomplete="off" type="text" placeholder="Type or select artifactId" data-provide="typeahead">
						<input id="op1" name="operator" type="hidden" value="=">
						&nbsp;
						<div class="btn-group jar-opt" data-toggle="buttons-radio" for="op1">
							<button type="button" class="btn">&lt;</button>
							<button type="button" class="btn active">=</button>
							<button type="button" class="btn">&gt;</button>
						</div>
						&nbsp;
						<input id="ver1" name="version" type="text" placeholder="Input version info">
						&emsp;&emsp;
						<input class="btn btn-primary" type="submit" value="Search">
					</div>
				</form>
			</div>
			<div class="well" style="height: 400px; overflow: scroll;">
				<ul class="iul">
					<c:forEach var="lib" items="${model.libs}">
						<li class="ili"><a class="btn btn-primary btn-list">${lib}</a></li>
					</c:forEach>
				</ul>
			</div>
		</div>
	</div>
	<res:useJs value="${res.js.local['search-jar.js']}" target="search-js" />
	<res:jsSlot id="search-js" />
</a:layout>