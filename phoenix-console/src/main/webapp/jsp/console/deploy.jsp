<%@ page contentType="text/html; charset=utf-8"%>
<%@ taglib prefix="a" uri="http://www.dianping.com/phoenix/console"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="res" uri="http://www.unidal.org/webres"%>
<jsp:useBean id="ctx"
	type="com.dianping.phoenix.console.page.deploy.Context" scope="request" />
<jsp:useBean id="payload"
	type="com.dianping.phoenix.console.page.deploy.Payload" scope="request" />
<jsp:useBean id="model"
	type="com.dianping.phoenix.console.page.deploy.Model" scope="request" />

<a:layout>
	<div class="row-fluid">
		<div class="span4">
			<div class="page-header">
				<strong style="font-size: medium;">shop-web</strong>：[<font color="blue">1.0.0.0</font>, 方式：1->1->1->1, 错误：终断后续发布]
			</div>
			<div class="row-fluid">
				<div class="span12 thumbnail" style="height: 440px; overflow: auto;">
					<table class="table table-condensed">
						<thead>
							<tr>
								<th width="90">Machine</th>
								<th>Progress</th>
							</tr>
						</thead>
						<tbody>
							<!-- 
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
							 -->
							<tr>
								<td>192.168.8.40</td>
								<td>
									<div class="progress">
										<div class="bar" style="width: 60%;"></div>
									</div>
								</td>
							</tr>
							<tr>
								<td>192.168.8.41</td>
								<td>
									<div class="progress progress-danger">
										<div class="bar" style="width: 20%;"></div>
									</div>
								</td>
							</tr>
							<tr>
								<td>192.168.8.42</td>
								<td>
									<div class="progress">
										<div class="bar" style="width: 0%;"></div>
									</div>
								</td>
							</tr>
							<tr>
								<td>192.168.8.43</td>
								<td>
									<div class="progress">
										<div class="bar" style="width: 0%;"></div>
									</div>
								</td>
							</tr>
							<tr>
								<td>192.168.8.44</td>
								<td>
									<div class="progress">
										<div class="bar" style="width: 0%;"></div>
									</div>
								</td>
							</tr>
							<tr>
								<td>192.168.8.45</td>
								<td>
									<div class="progress">
										<div class="bar" style="width: 0%;"></div>
									</div>
								</td>
							</tr>
							<tr>
								<td>192.168.8.46</td>
								<td>
									<div class="progress">
										<div class="bar" style="width: 0%;"></div>
									</div>
								</td>
							</tr>
							<tr>
								<td>192.168.8.47</td>
								<td>
									<div class="progress">
										<div class="bar" style="width: 0%;"></div>
									</div>
								</td>
							</tr>
							<tr>
								<td>192.168.8.48</td>
								<td>
									<div class="progress">
										<div class="bar" style="width: 0%;"></div>
									</div>
								</td>
							</tr>
							<tr>
								<td>192.168.8.49</td>
								<td>
									<div class="progress">
										<div class="bar" style="width: 0%;"></div>
									</div>
								</td>
							</tr>
							<tr>
								<td>192.168.8.50</td>
								<td>
									<div class="progress">
										<div class="bar" style="width: 0%;"></div>
									</div>
								</td>
							</tr>
						</tbody>
					</table>
				</div>
			</div>
			<div class="row" style="margin-top: 5px;">
				<p class="pull-right">
					<span class="label label-todo">pending&nbsp;&nbsp;&nbsp;</span> <span
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
					style="height: 508px; line-height: 20px; overflow: auto;">
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
</a:layout>