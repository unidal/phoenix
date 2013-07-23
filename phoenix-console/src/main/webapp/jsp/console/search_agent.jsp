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
		<li><a href="${model.webapp}/console/home?type=phoenix-agent">Agent</a><span class="divider">/</span></li>
		<li class="active">Search</li>
	</ul>
	<div class="modal" style="position: inherit; margin: auto; margin-bottom: 100px; margin-top: 20px; width: 900px;">
		<div class="modal-header">
			<h3>Set Query Condition</h3>
		</div>
		<div class="modal-body" style="min-height: 150px;">
			<form class="form-inline" id="queryform" style="margin: 5% 15% 5% 15%;">
				<input type="hidden" name="op" value="home" />
				<input type="hidden" name="type" value="phoenix-agent">
				<span class="badge badge-success" style="font-size: 12pt; vertical-align: middle; padding: 8px 8px 8px 8px; background-color: #999;">Agent Version:</span>
				<input id="operator" name="agentoperator" type="hidden" value="=">
				&nbsp;
				<div class="btn-group agent-opt" data-toggle="buttons-radio" for="operator">
					<button type="button" class="btn">&lt;</button>
					<button type="button" class="btn active">=</button>
					<button type="button" class="btn">&gt;</button>
				</div>
				&nbsp;
				<select name="agentversion">
					<c:forEach var="version" items="${model.agentVersions}">
						<option>${version}</option>
					</c:forEach>
				</select>
				&nbsp;
				<input type="submit" class="btn btn-primary" value="Search">
			</form>
		</div>
	</div>
	<res:useJs value="${res.js.local['search-agent.js']}" target="search-js" />
	<res:jsSlot id="search-js" />
</a:layout>