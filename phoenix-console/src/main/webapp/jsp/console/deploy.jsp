<%@ page contentType="text/html; charset=utf-8"%>
<%@ taglib prefix="a" uri="/WEB-INF/app.tld"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="res" uri="http://www.unidal.org/webres"%>
<jsp:useBean id="ctx"
	type="com.dianping.phoenix.console.page.deploy.Context" scope="request" />
<jsp:useBean id="payload"
	type="com.dianping.phoenix.console.page.deploy.Payload" scope="request" />
<jsp:useBean id="model"
	type="com.dianping.phoenix.console.page.deploy.Model" scope="request" />

<a:body>
	<div class="row-fluid">
		<div class="span4">
			<div class="page-header">
				<h4>Plan : ${payload.plan}</h4>
			</div>
			<table class="table table-striped table-bordered table-condensed">
				<thead>
					<tr>
						<th width="20%">Remote Hosts</th>
						<th>Progress</th>
					</tr>
				</thead>
				<tbody>
					<c:forEach var="plan" items="${model.hostPlans}">
						<tr>
							<td>${plan.host}</td>
							<td>
								<div id="p_${plan.index}" class="progress progress-striped">
									<c:forEach var="step" items="${plan.steps}" varStatus="it">
										<div id="b_${plan.index}_${step.step}"
											class="bar bar-${plan.statuses[step.step]}"
											style="width: ${step.weight}%" rel="tooltip"
											title="${plan.statuses[step.step]}"></div>
									</c:forEach>
								</div>
							</td>
						</tr>
					</c:forEach>
				</tbody>
			</table>

			<div class="row">
				<p class="pull-right">
					<span class="label label-todo">todo&nbsp;&nbsp;&nbsp;</span> <span
						class="label label-doing">doing&nbsp;&nbsp;</span> <span
						class="label label-success">success</span> <span
						class="label label-warning">warning</span> <span
						class="label label-failed">failed&nbsp;</span>
				</p>
			</div>

			<div id="result" style="display: none"></div>
		</div>
		<div class="span8">
			<div class="row-fluid">
				<div class="page-header">
					<h4>Remote Deploy Logs</h4>
				</div>
				<div id="status" data-spy="scroll" data-offset="0"
					class="terminal-like"
					style="height: 500px; line-height: 20px; overflow: auto;">
					<span id="offset--1" class="terminal-like">
						${model.quotedLog} </span>
				</div>
			</div>
		</div>
	</div>

	<input type="hidden" id="offset" name="offset" value="${model.offset}">
	<input type="hidden" id="plan" name="plan" value="${payload.plan}">

	<res:useJs value="${res.js.local.deploy_js}" target="deploy-js" />
	<res:useJs value="${res.js.local.TypingText_js}" target="deploy-js" />
	<res:jsSlot id="deploy-js" />
	<res:useCss value='${res.css.local.deploy_css}' target="head-css" />
	<res:cssSlot id="deploy-css" />
</a:body>