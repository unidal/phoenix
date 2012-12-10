<%@ page contentType="text/html; charset=utf-8"%>
<%@ taglib prefix="a" uri="/WEB-INF/app.tld"%>
<%@ taglib prefix="res" uri="http://www.unidal.org/webres"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<jsp:useBean id="ctx" type="com.dianping.phoenix.console.page.version.Context" scope="request"/>
<jsp:useBean id="payload" type="com.dianping.phoenix.console.page.version.Payload" scope="request"/>
<jsp:useBean id="model" type="com.dianping.phoenix.console.page.version.Model" scope="request"/>

<a:layout>
	<div class="container-fluid">
		<div class="row-fluid">
			<div class="span12">
				<div class="alert fade in">
		            <a class="close" data-dismiss="alert" href="#">×</a>
		            <strong>Note：</strong> 同一时刻仅允许一个构建任务!
		            <input type="hidden" id="creating_version" value="${model.creatingVersion != null ? model.creatingVersion : ""}">
		            <input type="hidden" id="log_index" value="0">
		            <c:if test="${model.creatingVersion != null}">
		            &nbsp;&nbsp;&nbsp;<span style="color: blue;" id="creating_version_note">"${model.creatingVersion}" IS Creating...</span>
		            </c:if>
		        </div>
			</div>
		</div>
		<div class="row-fluid">
			<div class="span5 thumbnail">
				<div>
					<table class="table table-striped table-condensed lion" style="margin-bottom:0px;">
						<thead>
							<tr>
								<th width="120">Version</th>
								<th>
									Description
									<span id="del_confirm" class="pull-right hide">
										<input type="hidden" id="del_version">
										<a href="#" id="del_confirm" class="no-dec">确认</a>&nbsp;
										<a href="#" id="del_cancel" class="no-dec">取消</a>
									</span>
								</th>
							</tr>
						</thead>
					</table>
				</div>
				<div id="version_panel" style="height:200px;overflow-y: auto;">
					<table class="table table-striped table-condensed lion">
						<tbody>
							<c:forEach var="version" items="${model.versions}">
								<tr class="version_row">
									<td width="120">${version.version}</td>
									<td>
										${version.description}
										<span class="pull-right hide btn_container">
											<button version="${version.id}" class="btn btn-mini2 pull-right" name="btn_del" type="button">删除</button>
										</span>
									</td>
								</tr>
							</c:forEach>
						</tbody>
					</table>
				</div>
			</div>
			<div class="span7 thumbnail">
				<div>
					<table class="table table-striped table-condensed lion" style="margin-bottom:0px;">
						<thead>
							<tr>
								<th width="240">Group Id</th>
								<th width="190">Artifact Id</th>
								<th>Version</th>
							</tr>
						</thead>
					</table>
				</div>
				<div style="height:200px;overflow-y: auto;">
					<table class="table table-striped table-condensed lion">
						<tbody>
							<tr>
								<td width="240">com.dianping.dpsf</td>
								<td width="190">dpsf-net</td>
								<td>1.8.0</td>
							</tr>
							<tr>
								<td>com.dianping.lion</td>
								<td>lion-client</td>
								<td>0.2.2</td>
							</tr>
							<tr>
								<td>com.dianping.swallow</td>
								<td>swallow-client</td>
								<td>0.5.4</td>
							</tr>
							<tr>
								<td>com.dianping.zebra</td>
								<td>zebra-client</td>
								<td>0.1.5</td>
							</tr>
							<tr>
								<td>com.dianping.dpsf</td>
								<td>dpsf-net</td>
								<td>1.8.0</td>
							</tr>
							<tr>
								<td>com.dianping.lion</td>
								<td>lion-client</td>
								<td>0.2.2</td>
							</tr>
							<tr>
								<td>com.dianping.swallow</td>
								<td>swallow-client</td>
								<td>0.5.4</td>
							</tr>
							<tr>
								<td>com.dianping.zebra</td>
								<td>zebra-client</td>
								<td>0.1.5</td>
							</tr>
						</tbody>
					</table>
				</div>
			</div>
		</div>
		<br />
		<div class="row-fluid">
			<div class="span12 thumbnail">
				<form method="get" class="no-vertial-margin">
					<input type="hidden" name="op" value="create">
					Version：<input type="text" id="version" name="version" value="${payload.version}" class="no-vertial-margin">&nbsp;&nbsp;&nbsp;
					Description：<input type="text" id="desc" name="desc" value="${payload.description}" class="input-xxlarge no-vertial-margin">
					<c:choose>
						<c:when test="${model.creatingVersion == null}">
							<button type="submit" class="btn btn-primary" id="create_btn">&nbsp;创建&nbsp; </button>
						</c:when>
						<c:otherwise>
							<button type="submit" class="btn" id="create_btn" disabled>&nbsp;创建&nbsp; </button>
						</c:otherwise>
					</c:choose>
					&nbsp;
					<span style="color: red;" id="error_msg"></span>
				</form>
			</div>
		</div>
		<br />
		<div id="log-plane" class="terminal-like" style="height: 200px; line-height: 20px; overflow: auto;">
		</div>
	</div>
	<res:useJs value="${res.js.local.version_js}" target="version-js" />
	<res:jsSlot id="version-js" />
</a:layout>