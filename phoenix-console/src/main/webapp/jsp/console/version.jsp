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
		        </div>
			</div>
		</div>
		<div class="row-fluid">
			<div class="span5 thumbnail" style="height:200px;overflow-y: auto;">
				<table class="table table-striped table-condensed lion">
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
					<tbody>
						<c:forEach var="version" items="${model.versions}">
							<tr class="version_row">
								<td>${version.version}</td>
								<td>
									${version.description} <font color="white">${version.id}</font>
									<span class="pull-right hide btn_container">
										<button version="${version.version}" class="btn btn-mini2 pull-right" name="btn_del" type="button">删除</button>
									</span>
								</td>
							</tr>
						</c:forEach>
					</tbody>
				</table>
			</div>
			<div class="span7 thumbnail" style="height:200px;overflow-y: auto;">
				<table class="table table-striped table-condensed lion">
					<thead>
						<tr>
							<th>Group Id</th>
							<th>Artifact Id</th>
							<th>Version</th>
						</tr>
					</thead>
					<tbody>
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
		
		<div class="row-fluid">
			<div class="span12 thumbnail">
				<form method="get" class="no-vertial-margin">
				<input type="hidden" name="op" value="add">
				Version：<input type="text" name="version" value="${payload.version}" class="no-vertial-margin">&nbsp;&nbsp;&nbsp;
				Description：<input type="text" name="desc" value="${payload.description}" class="input-xxlarge no-vertial-margin">
				<button type="submit" class="btn btn-primary">&nbsp;创建&nbsp; </button>
				</form>
			</div>
		</div>
		<br />
		<div class="terminal-like" style="height: 200px;overflow-y: auto;">
			Git pull from remote repository<br/>
			Pull succeed<br/>
			Download phoenix.war from maven repository<br/>
			Download succeed<br/>
			Clear the local phoenix working directory<br/>
			Decompress phoenix.war to local working directory<br/>
			Commit working directory to master branch<br/>
			Commit succeed<br/>
			Create a tag and push it to remote repository<br/>
			Succeed!<br/>
			滚动条....<br/>
			滚动条....<br/>
			滚动条....<br/>
			滚动条....<br/>
			滚动条....<br/>
		</div>
	</div>
	<res:useJs value="${res.js.local.version_js}" target="version-js" />
	<res:jsSlot id="version-js" />
</a:layout>