<%@ page contentType="text/html; charset=utf-8"%>
<%@ taglib prefix="a" uri="http://www.dianping.com/phoenix/console"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="res" uri="http://www.unidal.org/webres"%>
<jsp:useBean id="ctx" type="com.dianping.phoenix.console.page.deploy.Context" scope="request" />
<jsp:useBean id="payload" type="com.dianping.phoenix.console.page.deploy.Payload" scope="request" />
<jsp:useBean id="model" type="com.dianping.phoenix.console.page.deploy.Model" scope="request" />

<a:layout>
	
	<div class="row-fluid">
		<div class="span4">
			<div class="page-header">
				<strong style="font-size: medium;">${model.deploy.domain}</strong>：
				[<font color="blue">${model.deploy.version}</font>, 方式：1->1->1->1, 错误：终断后续发布], 结果：[<strong><span id="deploy_status">${model.deploy.status}</span></strong>]
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
							<c:forEach var="entry" items="${model.deploy.hosts}">
								<c:set var="host" value="${entry.value}"/>
								<tr id="${host.ip}" data-offset="${host.offset}"">
									<td>${host.ip}</td>
									<td>
                                        <div class="progress">
                                            <div class="bar" style="width: ${host.progress}%;">
                                                <div style="width: 250px;color: #000000;">${host.currentStep}</div>
                                            </div>
                                        </div>
									</td>
								</tr>
							</c:forEach>
						</tbody>
					</table>
				</div>
			</div>
			<div class="row" style="margin-top: 5px;">
				<p class="pull-right">
					<span class="label label-todo">pending&nbsp;&nbsp;&nbsp;</span> <span class="label label-doing">doing&nbsp;&nbsp;</span> <span
						class="label label-success">success</span> <span class="label label-failed">failed&nbsp;</span>
				</p>
			</div>

			<div id="result" style="display: none"></div>
		</div>
		<div class="span8">
			<div class="row-fluid">
				<div class="page-header">
					<h4>Deployment Details</h4>
				</div>
				<c:forEach var="entry" items="${model.deploy.hosts}" varStatus="status">
					<c:set var="host" value="${entry.value}"/>
					<div id="log-${host.ip}" data-spy="scroll" data-offset="0" style="height: 508px; line-height: 20px; overflow: auto;"
						 class="terminal-like<c:if test="${status.index > 0}"> hide</c:if>">
						<c:forEach var="segment" items="${host.segments}">
							<span class="terminal-like">${segment.encodedText}</span>
						</c:forEach>
					</div>
				</c:forEach>
			</div>
		</div>
	</div>

	<res:useJs value="${res.js.local.deploy_js}" target="deploy-js" />
	<res:useCss value='${res.css.local.deploy_css}' target="head-css" />
	<res:jsSlot id="deploy-js" />
	<res:cssSlot id="deploy-css" />
</a:layout>