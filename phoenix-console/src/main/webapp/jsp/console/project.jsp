<%@ page contentType="text/html; charset=utf-8"%>
<%@ taglib prefix="a" uri="http://www.dianping.com/phoenix/console"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="w" uri="http://www.unidal.org/web/core"%>
<%@ taglib prefix="res" uri="http://www.unidal.org/webres"%>
<jsp:useBean id="ctx" type="com.dianping.phoenix.console.page.home.Context" scope="request" />
<jsp:useBean id="payload" type="com.dianping.phoenix.console.page.home.Payload" scope="request" />
<jsp:useBean id="model" type="com.dianping.phoenix.console.page.home.Model" scope="request" />

<a:layout>

	<c:set var="project" value="${model.project}"/>
	<div class="container-fluid">
		<div class="row-fluid">
			<div class="span12">
				<table class="table table-bordered table-condensed nohover">
					<tbody>
						<tr>
							<th width="80">Project</th>
							<td width="200">${project.name}</td>
							<th width="80">Owner</th>
							<td width="200">${project.owner}</td>
							<th width="80">Description</th>
							<td>${project.description}</td>
						</tr>
					</tbody>
				</table>
			</div>
		</div>
		
		<form>
		<input type="hidden" name="project" value="${payload.project}">
		<div class="row-fluid">
			<div class="span3">
				<div class="row-fluid">
					<div class="span12 thumbnail" style="height:280px;overflow-y: auto;">
						<table class="table table-striped table-condensed lion">
							<thead>
								<tr>
									<th><input type="checkbox" id="all-machine-check"/> Machine</th>
									<th>Kernel</th>
									<th>App</th>
								</tr>
							</thead>
							<tbody>
								<c:forEach var="host" items="${project.hosts}" varStatus="status">
									<tr>
										<td>
											${w:showCheckbox('host', host, payload.hosts, 'ip', 'ip')}
											<c:if test="${host.status=='up'}">
											   <div class="z6 a-f-e" title="可用"></div>
											</c:if>
											<c:if test="${host.status=='down'}">
											   <div class="u6 a-f-e" title="不可用"></div>
											</c:if>
										</td>
										<td>1.0</td>
										<td>2.0.3</td>
									</tr>
								</c:forEach>
							</tbody>
						</table>
					</div>
				</div>
			</div>
			<div class="span9">
				<h3>Kernel policy</h3>
				<div class="row-fluid">
					<div class="span12">
						<table class="table table-bordered table-condensed lion nohover">
							<tbody>
								<tr>
									<th width="15%">Kernel Version</th>
									<td>
										<select name="plan.version">
											${w:showOptions(model.versions, payload.plan.version, 'version', 'version')}
										</select>
									</td>
								</tr>
							</tbody>
						</table>
					</div>
				</div>

				<h3>Deploy policy</h3>
				<input type="hidden" name="op" value="deploy"/>
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
										<input type="radio" id="plan.abortOnError-1" name="plan.abortOnError" value="true" ${payload.plan.abortOnError==true?'checked':''}><label for="plan.abortOnError-1">终断后续发布</label>
									</td>
									<td>
										<input type="radio" id="plan.abortOnError-2" name="plan.abortOnError" value="false" ${payload.plan.abortOnError==false?'checked':''}><label for="plan.abortOnError-2">继续后续发布</label>
									</td>
								</tr>
							</tbody>
						</table>
					</div>
				</div>
				<br />
				<div class="row-fluid">
					<button type="submit" name="deploy" value="Deploy" class="btn btn-primary">Deploy</button>
					<c:if test="${model.rolling}">
					   &nbsp;&nbsp;<button type="submit" name="watch" value="Watch" class="btn btn-primary">Watch</button>
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
			<div class="span12 thumbnail" style="height:225px;overflow-y: auto;">
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

    <res:useJs value="${res.js.local.project_js}" target="project-js" />
    <res:jsSlot id="project-js" />
</a:layout>