<%@ page contentType="text/html; charset=utf-8"%>
<%@ taglib prefix="a" uri="http://www.dianping.com/phoenix/console"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"  %>
<%@ taglib prefix="w" uri="http://www.unidal.org/web/core"%>
<%@ taglib prefix="res" uri="http://www.unidal.org/webres"%>
<jsp:useBean id="ctx" type="com.dianping.phoenix.console.page.home.Context" scope="request" />
<jsp:useBean id="payload" type="com.dianping.phoenix.console.page.home.Payload" scope="request" />
<jsp:useBean id="model" type="com.dianping.phoenix.console.page.home.Model" scope="request" />

<a:layout>
	<res:useCss value="${res.css.local['DT_bootstrap.css']}" target="head-css" />
	<res:useCss value="${res.css.local['overview.css']}" target="head-css" />

	<ul class="breadcrumb">
		<li><a href="${model.webapp}/console/home">Home</a><span class="divider">/</span></li>
		<c:choose>
			<c:when test="${payload.type=='phoenix-agent'}">
				<li><a class="toParent" href="${model.webapp}/console/home?type=phoenix-agent">Agent</a><span class="divider">/</span></li>
			</c:when>
			<c:otherwise>
				<li><a class="toParent" href="${model.webapp}/console/home?type=phoenix-kernel">Kernel</a><span class="divider">/</span></li>
			</c:otherwise>
		</c:choose>
		<li class="active">Project</li>
	</ul>
	<div class="alert alert-success" style="display: none;">
            <button type="button" class="close" data-dismiss="alert">×</button>
            <strong>Query Condition: </strong>&emsp;<span id="queryInfo"></span>
    </div>
	<w:errors>
       <h3>Error occurred:</h3>
	   <pre class="error">
	      <w:error code="project.hosts">Please check at least one of the hosts below.</w:error>
	      <w:error code="project.version">You need select one version, or create a new one <a href="${model.moduleUri}/version?type=${payload.plan.warType}">here</a>.</w:error>
	      <w:error code="*"><strong>\${code}</strong>: \${exception.message}</w:error>
	   </pre>
	</w:errors>
	<input type="hidden" id="payload_dependencies" value="${payload.dependencies}">
	<input type="hidden" id="payload_operators" value="${payload.operators}">
	<input type="hidden" id="payload_versions" value="${payload.versions}">
	<input type="hidden" id="payload_joints" value="${payload.joints}">
	<input type="hidden" id="payload_agentversion" value="${payload.agentVersion}">
	<input type="hidden" id="payload_agentoperator" value="${payload.agentOperator}">
	<c:set var="domain" value="${model.domain}"/>
	<div class="container-fluid">
		<div class="row-fluid">
			<div class="span12">
				<table class="table table-bordered table-condensed nohover">
					<tbody>
						<tr>
							<th width="80">Project</th>
							<td width="200">${domain.name}</td>
							<th width="80">Owner</th>
							<td width="200">
								<c:choose>
									<c:when test="${fn:length(domain.owners) eq 0}">N/A</c:when>
									<c:otherwise>
										<c:forEach var="owner" items="${domain.owners}">
										${owner}&nbsp;
										</c:forEach>
									</c:otherwise>
								</c:choose>
							</td>
							<th width="80">Description</th>
							<td>${domain.description}</td>
						</tr>
					</tbody>
				</table>
			</div>
		</div>
		
		<form>
		<input type="hidden" name="project" value="${payload.project}">
		<div class="row-fluid">
			<div class="span5">
				<div class="row-fluid">
					<div class="span12 thumbnail" style="height:400px;overflow-y: auto;">
						<table id="host-nav" class="table table-striped table-condensed lion">
							<thead>
								<tr>
									<th><input type="checkbox" id="all-machine-check"/> IP</th>
									<th>Hostname</th>
									<th>Env</th>
									<th>Kernel</th>
									<th>App</th>
								</tr>
							</thead>
							<tbody>
								<c:forEach var="host" items="${domain.hosts}" varStatus="status">
									<tr>
										<td>
											<%--  ${w:showCheckbox('host', host.value, payload.hosts, 'ip', 'ip')}--%>
											${w:showCheckbox('host', host.value, payload.hosts, 'ip', 'ip')}
											<c:if test="${host.value.phoenixAgent.status=='ok'}">
											   <div class="z6 a-f-e" title="可用"></div>
											</c:if>
											<c:if test="${host.value.phoenixAgent.status!='ok'}">
											   <div class="u6 a-f-e" title="不可用"></div>
											</c:if>
										</td>
										<td>
											${host.value.hostname}
										</td>
										<td>${host.value.env}</td>
										<c:choose>
											<c:when test="${fn:length(host.value.container.apps) eq 0}">
												<td>N/A</td>
												<td>N/A</td>
											</c:when>
											<c:otherwise>
												<c:forEach var="app" items="${host.value.container.apps}">
													<c:if test="${app.name==domain.name}">
														<td>
															<c:choose>
																<c:when test="${fn:length(app.version) eq 0}">
																	N/A
																</c:when>
																<c:otherwise>
																	${app.version}
																</c:otherwise>
															</c:choose>
														</td>
														<td>
															<c:choose>
																<c:when test="${fn:length(app.kernel.version) eq 0}">
																	N/A
																</c:when>
																<c:otherwise>
																	${app.kernel.version}
																</c:otherwise>
															</c:choose>
														</td>
													</c:if>
												</c:forEach>
											</c:otherwise>
										</c:choose>
									</tr>
								</c:forEach>
							</tbody>
						</table>
					</div>
				</div>
			</div>
			<div class="span7">
				<div class="row-fluid">
					<div class="span12">
						<table class="table table-bordered table-condensed lion nohover">
							<thead>
								<tr>
									<th width="40%">
                                       <label class="help-inline" style="padding-left: 0px;"><strong style="color:#08C;">版本号 (${payload.plan.warType})</strong></label>
                                    </th>
									<td>
										<select name="plan.version">
											${w:showOptions(model.deliverables, payload.plan.version, 'warVersion', 'warVersion')}
										</select>
									</td>
								</tr>
							</thead>
						</table>
					</div>
				</div>

				<input type="hidden" name="op" value="deploy"/>
				<input type="hidden" name="type" value="${payload.plan.warType}"/>
				<div class="row-fluid">
					<div class="span12 thumbnail">
						 <table class="table table-condensed lion nohover" style="margin:0 0 0;border-bottom:1px solid #DDD;">
						 	<thead>
							    <tr>
									<th colspan="3">
										<label class="help-inline" style="padding-left: 0px;"><strong style="color:#08C;">部署方式</strong></label>
									</th>
							    </tr>
							</thead>
							<tbody>
								<tr>
									<c:forEach var="policy" items="${model.policies}">
										<td width="190">
											${w:showRadio('plan.policy', policy, payload.plan.policy, 'id', 'description')}
										</td>
									</c:forEach>
								</tr>
							</tbody>
						</table>
						<table class="table table-condensed lion nohover" style="margin:0 0 0;border-bottom:1px solid #DDD;">
						 	<thead>
							    <tr>
									<th colspan="3">
										<label class="help-inline" style="padding-left: 0px;"><strong style="color:#08C;">错误处理</strong></label>
									</th>
							    </tr>
							</thead>
							<tbody>
								<tr>
									<td width="180">
										<input type="radio" id="${ctx.nextHtmlId}" name="plan.abortOnError" value="true" ${payload.plan.abortOnError==true?'checked':''}><label for="${ctx.currentHtmlId}">中断后续发布</label>
									</td>
									<td>
										<input type="radio" id="${ctx.nextHtmlId}" name="plan.abortOnError" value="false" ${payload.plan.abortOnError==false?'checked':''}><label for="${ctx.currentHtmlId}">继续后续发布</label>
									</td>
								</tr>
							</tbody>
						</table>
						<table class="table table-condensed lion nohover" style="margin:0 0 0;border-bottom:1px solid #DDD;">
						 	<thead>
							    <tr>
									<th colspan="3">
										<label class="help-inline" style="padding-left: 0px;"><strong style="color:#08C;">发布控制</strong></label>
									</th>
							    </tr>
							</thead>
							<tbody>
								<tr>
									<td width="180">
										<input type="radio" id="${ctx.nextHtmlId}" name="plan.autoContinue" value="false" checked onchange="disableTxt('txt_deployInterval')"> <label for="${ctx.currentHtmlId }">手动控制</label>
									</td>
									<td>
										<input type="radio" id="${ctx.nextHtmlId }" name="plan.autoContinue" value="true" onchange="enableTxt('txt_deployInterval')"> <label for="${ctx.currentHtmlId}">发布间隔/秒: </label>
										<input type="text" id="txt_deployInterval" name="plan.deployInterval" value="0">
									</td>
								</tr>
							</tbody>
						</table>
						<table class="table table-condensed lion nohover" style="margin:0 0 0;border-bottom:1px solid #DDD;">
						 	<thead>
							    <tr>
									<th colspan="3">
										<label class="help-inline" style="padding-left: 0px;"><strong style="color:#08C;">冒烟测试服务</strong></label>
									</th>
							    </tr>
							</thead>
							<tbody>
								<tr>
									<td width="180">
										<input type="radio" id="${ctx.nextHtmlId}" name="plan.skipTest" value="false" ${payload.plan.skipTest==false?'checked':''}><label for="${ctx.currentHtmlId}">打开</label>
									</td>
									<td>
										<input type="radio" id="${ctx.nextHtmlId}" name="plan.skipTest" value="true" ${payload.plan.skipTest==true?'checked':''}><label for="${ctx.currentHtmlId}">关闭</label>
									</td>
								</tr>
							</tbody>
						</table>
					</div>
				</div>
				<script type="text/javascript">
					$(document).ready(function() {
						if($("[name='plan.autoContinue'][value='true']")[0].checked){
							$("[name='plan.deployInterval']")[0].disabled = false;
						} else {
							$("[name='plan.deployInterval']")[0].disabled = true;
						}
					});
					function disableTxt(id) {
					    document.getElementById(id).disabled = true;
					}
					function enableTxt(id) {
					    document.getElementById(id).disabled = false;
					    document.getElementById(id).focus();
					}
				</script>
				<br />
				<div class="row-fluid">
					<button type="submit" name="deploy" value="Deploy" class="btn btn-primary">Deploy</button>
					<c:if test="${not empty model.activeDeployment}">
					   &nbsp;&nbsp;<a href="${model.moduleUri}/deploy?id=${model.activeDeployment.id}">Watch</a>
					</c:if>
				</div>
			</div>
		</div>
		</form>
		
		<br />
		<div class="row-fluid">
			<div class="span6" style="min-height: 0px;">
				<strong>Tips：</strong>点击上面机器所在行，针对该机器编辑依赖项规则，Disable：禁用选定的依赖包
			</div>
		</div>
		<div class="row-fluid">
			<div class="span12 thumbnail" style="height:165px; overflow-y:auto;">
				<table class="table table-striped table-condensed">
					<thead>
						<tr>
							<th>Group</th>
							<th>Artifact</th>
							<th>Kernel</th>
							<th>App</th>
							<th>Kernel-New</th>
							<th>Disable</th>
						</tr>
					</thead>
					<tbody>
						<tr>
							<td>opensymphony</td>
							<td>sitemesh</td>
							<td>1.0</td>
							<td>1.1</td>
							<td>1.1</td>
							<td><input type="checkbox"></td>
						</tr>
						<tr>
							<td>org.apache.struts</td>
							<td>struts2-core</td>
							<td>-----</td>
							<td>2.2.0</td>
							<td>-----</td>
							<td><input type="checkbox"></td>
						</tr>
						<tr>
							<td>org.apache.struts</td>
							<td>struts2-json-plugin</td>
							<td>2.1.8</td>
							<td>-----</td>
							<td>2.2.0</td>
							<td><input type="checkbox"></td>
						</tr>
						<tr>
							<td>org.springframework</td>
							<td>spring-web</td>
							<td>2.5.6</td>
							<td>3.0</td>
							<td>3.0</td>
							<td><input type="checkbox"></td>
						</tr>
						<tr>
							<td>opensymphony</td>
							<td>sitemesh</td>
							<td>1.0</td>
							<td>1.1</td>
							<td>1.1</td>
							<td><input type="checkbox"></td>
						</tr>
						<tr>
							<td>org.apache.struts</td>
							<td>struts2-core</td>
							<td>2.1.8</td>
							<td>2.2.0</td>
							<td>2.2.0</td>
							<td><input type="checkbox"></td>
						</tr>
						<tr>
							<td>org.apache.struts</td>
							<td>struts2-json-plugin</td>
							<td>2.1.8</td>
							<td>2.2.0</td>
							<td>2.2.0</td>
							<td><input type="checkbox"></td>
						</tr>
						<tr>
							<td>org.springframework</td>
							<td>spring-web</td>
							<td>2.5.6</td>
							<td>3.0</td>
							<td>3.0</td>
							<td><input type="checkbox"></td>
						</tr>
						<tr>
							<td>opensymphony</td>
							<td>sitemesh</td>
							<td>1.0</td>
							<td>1.1</td>
							<td>1.1</td>
							<td><input type="checkbox"></td>
						</tr>
						<tr>
							<td>org.apache.struts</td>
							<td>struts2-core</td>
							<td>2.1.8</td>
							<td>2.2.0</td>
							<td>2.2.0</td>
							<td><input type="checkbox"></td>
						</tr>
						<tr>
							<td>org.apache.struts</td>
							<td>struts2-json-plugin</td>
							<td>2.1.8</td>
							<td>2.2.0</td>
							<td>2.2.0</td>
							<td><input type="checkbox"></td>
						</tr>
						<tr>
							<td>org.springframework</td>
							<td>spring-web</td>
							<td>2.5.6</td>
							<td>3.0</td>
							<td>3.0</td>
							<td><input type="checkbox"></td>
						</tr>
						<tr>
							<td>opensymphony</td>
							<td>sitemesh</td>
							<td>1.0</td>
							<td>1.1</td>
							<td>1.1</td>
							<td><input type="checkbox"></td>
						</tr>
						<tr>
							<td>org.apache.struts</td>
							<td>struts2-core</td>
							<td>2.1.8</td>
							<td>2.2.0</td>
							<td>2.2.0</td>
							<td><input type="checkbox"></td>
						</tr>
						<tr>
							<td>org.apache.struts</td>
							<td>struts2-json-plugin</td>
							<td>2.1.8</td>
							<td>2.2.0</td>
							<td>2.2.0</td>
							<td><input type="checkbox"></td>
						</tr>
						<tr>
							<td>org.springframework</td>
							<td>spring-web</td>
							<td>2.5.6</td>
							<td>3.0</td>
							<td>3.0</td>
							<td><input type="checkbox"></td>
						</tr>
						<tr>
							<td>opensymphony</td>
							<td>sitemesh</td>
							<td>1.0</td>
							<td>1.1</td>
							<td>1.1</td>
							<td><input type="checkbox"></td>
						</tr>
						<tr>
							<td>org.apache.struts</td>
							<td>struts2-core</td>
							<td>2.1.8</td>
							<td>2.2.0</td>
							<td>2.2.0</td>
							<td><input type="checkbox"></td>
						</tr>
						<tr>
							<td>org.apache.struts</td>
							<td>struts2-json-plugin</td>
							<td>2.1.8</td>
							<td>2.2.0</td>
							<td>2.2.0</td>
							<td><input type="checkbox"></td>
						</tr>
						<tr>
							<td>org.springframework</td>
							<td>spring-web</td>
							<td>2.5.6</td>
							<td>3.0</td>
							<td>3.0</td>
							<td><input type="checkbox"></td>
						</tr>
						<tr>
							<td>opensymphony</td>
							<td>sitemesh</td>
							<td>1.0</td>
							<td>1.0</td>
							<td>1.1</td>
							<td><input type="checkbox"></td>
						</tr>
						<tr>
							<td>org.apache.struts</td>
							<td>struts2-core</td>
							<td>2.1.8</td>
							<td>2.2.0</td>
							<td>2.2.0</td>
							<td><input type="checkbox"></td>
						</tr>
						<tr>
							<td>org.apache.struts</td>
							<td>struts2-json-plugin</td>
							<td>2.1.8</td>
							<td>2.2.0</td>
							<td>2.2.0</td>
							<td><input type="checkbox"></td>
						</tr>
						<tr>
							<td>org.springframework</td>
							<td>spring-web</td>
							<td>2.5.6</td>
							<td>3.0</td>
							<td>3.0</td>
							<td><input type="checkbox"></td>
						</tr>
					</tbody>
				</table>
			</div>
		</div>
	</div>

	<res:useJs value="${res.js.local['jquery.dataTables.min.js']}" target="datatable-js" />
    <res:useJs value="${res.js.local.project_js}" target="project-js" />
	<res:jsSlot id="datatable-js" />
    <res:jsSlot id="project-js" />
</a:layout>