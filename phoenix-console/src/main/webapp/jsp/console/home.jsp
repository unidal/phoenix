<%@ page contentType="text/html; charset=utf-8"%>
<%@ taglib prefix="a" uri="/WEB-INF/app.tld"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="res" uri="http://www.unidal.org/webres"%>
<jsp:useBean id="ctx"
	type="com.dianping.phoenix.console.page.home.Context" scope="request" />
<jsp:useBean id="payload"
	type="com.dianping.phoenix.console.page.home.Payload" scope="request" />
<jsp:useBean id="model"
	type="com.dianping.phoenix.console.page.home.Model" scope="request" />

<a:layout>
	<!-- 
	<div class="container-fluid">
		<div class="row-fluid">
			<div class="span12 thumbnail">
				<label class="help-inline" for="search-project">Project：</label>
				<input id="search-project" type="text" style="margin-bottom: 0px;">
				<button type="submit" class="btn">Search</button>
			</div>
		</div>
	</div>
	<br />
	 -->
	
	<div class="container-fluid">
		<div class="row-fluid">
			<div class="span12">
				<ul class="nav nav-tabs">
					<li class="active">
						<a href="#">主站</a>
					</li>
					<li>
						<a href="#">团购</a>
					</li>
					<li>
						<a href="#">手机</a>
					</li>
					<li>
						<a href="#">其他</a>
					</li>
				</ul>
			</div>
		</div>
		<div class="row-fluid">
			<div class="span12" style="height:518px;overflow-y: auto;">
				<table class="table table-bordered table-striped table-condensed table-page">
					<thead>
						<tr>
							<th width="35">S</th>
							<th width="220">Project</th>
							<th width="120">Owner</th>
							<th width="150">Machine(Active/Inactive)</th>
							<th>Kernel Versions</th>
							<th>App Versions</th>
						</tr>
					</thead>
					<tbody>
						<tr>
							<td><img src="${model.webapp}/img/green.gif"></td>
							<td><a href="?op=project&name=shop-web">group-web</a></td>
							<td>jianbin.xu</td>
							<td>8/0</td>
							<td>1.2, 1.5, 1.6</td>
							<td>2.0.0, 2.0.1</td>
						</tr>
						<tr>
							<td><img src="${model.webapp}/img/green.gif"></td>
							<td><a href="#">group-web</a></td>
							<td>jianbin.xu</td>
							<td>8/0</td>
							<td>1.2, 1.5, 1.6</td>
							<td>2.0.0, 2.0.1</td>
						</tr>
						<tr>
							<td><img src="${model.webapp}/img/yellow.gif"></td>
							<td><a href="#">group-web</a></td>
							<td>jianbin.xu</td>
							<td>8/0</td>
							<td>1.2, 1.5, 1.6</td>
							<td>2.0.0, 2.0.1</td>
						</tr>
						<tr>
							<td><img src="${model.webapp}/img/blue.png"></td>
							<td><a href="#">group-web</a></td>
							<td>jianbin.xu</td>
							<td>8/0</td>
							<td>1.2, 1.5, 1.6</td>
							<td>2.0.0, 2.0.1</td>
						</tr>
						<tr>
							<td><img src="${model.webapp}/img/green.gif"></td>
							<td><a href="?op=project&name=shop-web">group-web</a></td>
							<td>jianbin.xu</td>
							<td>8/<font color="red">1</font></td>
							<td>1.2, 1.5, 1.6</td>
							<td>2.0.0, 2.0.1</td>
						</tr>
						<tr>
							<td><img src="${model.webapp}/img/green.gif"></td>
							<td><a href="#">group-web</a></td>
							<td>jianbin.xu</td>
							<td>8/0</td>
							<td>1.2, 1.5, 1.6</td>
							<td>2.0.0, 2.0.1</td>
						</tr>
						<tr>
							<td><img src="${model.webapp}/img/yellow.gif"></td>
							<td><a href="#">group-web</a></td>
							<td>jianbin.xu</td>
							<td>8/0</td>
							<td>1.2, 1.5, 1.6</td>
							<td>2.0.0, 2.0.1</td>
						</tr>
						<tr>
							<td><img src="${model.webapp}/img/blue.png"></td>
							<td><a href="#">group-web</a></td>
							<td>jianbin.xu</td>
							<td>8/0</td>
							<td>1.2, 1.5, 1.6</td>
							<td>2.0.0, 2.0.1</td>
						</tr>
						<tr>
							<td><img src="${model.webapp}/img/green.gif"></td>
							<td><a href="?op=project&name=shop-web">group-web</a></td>
							<td>jianbin.xu</td>
							<td>8/0</td>
							<td>1.2, 1.5, 1.6</td>
							<td>2.0.0, 2.0.1</td>
						</tr>
						<tr>
							<td><img src="${model.webapp}/img/green.gif"></td>
							<td><a href="#">group-web</a></td>
							<td>jianbin.xu</td>
							<td>8/0</td>
							<td>1.2, 1.5, 1.6</td>
							<td>2.0.0, 2.0.1</td>
						</tr>
						<tr>
							<td><img src="${model.webapp}/img/yellow.gif"></td>
							<td><a href="#">group-web</a></td>
							<td>jianbin.xu</td>
							<td>8/0</td>
							<td>1.2, 1.5, 1.6</td>
							<td>2.0.0, 2.0.1</td>
						</tr>
						<tr>
							<td><img src="${model.webapp}/img/green.gif"></td>
							<td><a href="?op=project&name=shop-web">group-web</a></td>
							<td>jianbin.xu</td>
							<td>8/0</td>
							<td>1.2, 1.5, 1.6</td>
							<td>2.0.0, 2.0.1</td>
						</tr>
						<tr>
							<td><img src="${model.webapp}/img/green.gif"></td>
							<td><a href="#">group-web</a></td>
							<td>jianbin.xu</td>
							<td>8/0</td>
							<td>1.2, 1.5, 1.6</td>
							<td>2.0.0, 2.0.1</td>
						</tr>
						<tr>
							<td><img src="${model.webapp}/img/yellow.gif"></td>
							<td><a href="#">group-web</a></td>
							<td>jianbin.xu</td>
							<td>8/0</td>
							<td>1.2, 1.5, 1.6</td>
							<td>2.0.0, 2.0.1</td>
						</tr>
						<tr>
							<td><img src="${model.webapp}/img/red.png"></td>
							<td><a href="#">group-web</a></td>
							<td>jianbin.xu</td>
							<td>8/0</td>
							<td>1.2, 1.5, 1.6</td>
							<td>2.0.0, 2.0.1</td>
						</tr>
					</tbody>
				</table>
				<div class="pagination">
					<input type="hidden" name="pageNo" value="${paginater.pageNumber}"/>
					<ul>
						<li><a href="#" onclick="jump2Page(1, this);return false;">首页</a></li>
						<li><a href="#" onclick="jump2Page(1, this);return false;">前一页</a></li>
						<li class="active"><a href="#" onclick="jump2Page(1, this);return false;">1</a></li>
						<li><a href="#" onclick="jump2Page(2, this);return false;">2</a></li>
						<li><a href="#" onclick="jump2Page(3, this);return false;">3</a></li>
					    <li><a href="#" onclick="jump2Page(2, this);return false;">后一页</a></li>
					    <li><a href="#" onclick="jump2Page(3, this);return false;">尾页</a></li>
					    <li><a href="javascript: return false;">共3页 | 39条 | 每页15条</a></li>
					</ul>
				</div>
			</div>
		</div>
		
		<!-- 
		<div class="row-fluid">
			<div class="span2 thumbnail" style="height:518px;overflow-y: auto;">
				<table class="table table-striped table-condensed">
					<thead>
						<tr>
							<th>Running Task</th>
						</tr>
					</thead>
					<tbody>
						<tr>
							<td>
								<a href="#">192.168.8.40</a><br/>
								stoping jboss
							</td>
						</tr>
						<tr>
							<td>
								<a href="#">192.168.8.41</a><br/>
								starting jboss
							</td>
						</tr>
						<tr>
							<td>
								<a href="#">192.168.8.42</a><br/>
								pending
							</td>
						</tr>
						<tr>
							<td>
								<a href="#">192.168.8.43</a><br/>
								pending
							</td>
						</tr>
						<tr>
							<td>
								<a href="#">192.168.8.40</a><br/>
								stoping jboss
							</td>
						</tr>
						<tr>
							<td>
								<a href="#">192.168.8.41</a><br/>
								starting jboss
							</td>
						</tr>
						<tr>
							<td>
								<a href="#">192.168.8.42</a><br/>
								pending
							</td>
						</tr>
						<tr>
							<td>
								<a href="#">192.168.8.43</a><br/>
								pending
							</td>
						</tr>
						<tr>
							<td>
								<a href="#">192.168.8.40</a><br/>
								stoping jboss
							</td>
						</tr>
						<tr>
							<td>
								<a href="#">192.168.8.41</a><br/>
								starting jboss
							</td>
						</tr>
						<tr>
							<td>
								<a href="#">192.168.8.42</a><br/>
								pending
							</td>
						</tr>
						<tr>
							<td>
								<a href="#">192.168.8.43</a><br/>
								pending
							</td>
						</tr>
						<tr>
							<td>
								<a href="#">192.168.8.40</a><br/>
								stoping jboss
							</td>
						</tr>
						<tr>
							<td>
								<a href="#">192.168.8.41</a><br/>
								starting jboss
							</td>
						</tr>
						<tr>
							<td>
								<a href="#">192.168.8.42</a><br/>
								pending
							</td>
						</tr>
						<tr>
							<td>
								<a href="#">192.168.8.43</a><br/>
								pending
							</td>
						</tr>
						<tr>
							<td>
								<a href="#">192.168.8.40</a><br/>
								stoping jboss
							</td>
						</tr>
						<tr>
							<td>
								<a href="#">192.168.8.41</a><br/>
								starting jboss
							</td>
						</tr>
						<tr>
							<td>
								<a href="#">192.168.8.42</a><br/>
								pending
							</td>
						</tr>
						<tr>
							<td>
								<a href="#">192.168.8.43</a><br/>
								pending
							</td>
						</tr>
						<tr>
							<td>
								<a href="#">192.168.8.40</a><br/>
								stoping jboss
							</td>
						</tr>
						<tr>
							<td>
								<a href="#">192.168.8.41</a><br/>
								starting jboss
							</td>
						</tr>
						<tr>
							<td>
								<a href="#">192.168.8.42</a><br/>
								pending
							</td>
						</tr>
						<tr>
							<td>
								<a href="#">192.168.8.43</a><br/>
								pending
							</td>
						</tr>
						<tr>
							<td>
								<a href="#">192.168.8.40</a><br/>
								stoping jboss
							</td>
						</tr>
						<tr>
							<td>
								<a href="#">192.168.8.41</a><br/>
								starting jboss
							</td>
						</tr>
						<tr>
							<td>
								<a href="#">192.168.8.42</a><br/>
								pending
							</td>
						</tr>
						<tr>
							<td>
								<a href="#">192.168.8.43</a><br/>
								pending
							</td>
						</tr>
					</tbody>
				</table>
			</div>
			<div class="span3 thumbnail" style="height:518px;">
				<table class="table table-striped table-condensed table-page">
					<thead>
						<tr>
							<th>
								Project
							</th>
						</tr>
					</thead>
					<tbody>
						<tr>
							<td>
								<input type="radio" name="project_sel">
								<a href="?op=project&name=shop-web">shop-web</a>
							</td>
						</tr>
						<tr>
							<td>
								<input type="radio" name="project_sel">
								<a href="#">grop-web</a>
							</td>
						</tr>
						<tr>
							<td>
								<input type="radio" name="project_sel">
								<a href="#">group-service</a>
							</td>
						</tr>
						<tr>
							<td>
								<input type="radio" name="project_sel">
								<a href="#">user-web</a>
							</td>
						</tr>
						<tr>
							<td>
								<input type="radio" name="project_sel">
								<a href="#">user-service</a>
							</td>
						</tr>
						<tr>
							<td>
								<input type="radio" name="project_sel">
								<a href="#">user-admin-web</a>
							</td>
						</tr>
						<tr>
							<td>
								<input type="radio" name="project_sel">
								<a href="#">shoppic-web</a>
							</td>
						</tr>
						<tr>
							<td>
								<input type="radio" name="project_sel">
								<a href="#">shoppic-service</a>
							</td>
						</tr>
						<tr>
							<td>
								<input type="radio" name="project_sel">
								<a href="#">usercard-web</a>
							</td>
						</tr>
						<tr>
							<td>
								<input type="radio" name="project_sel">
								<a href="#">usercard-service</a>
							</td>
						</tr>
						<tr>
							<td>
								<input type="radio" name="project_sel">
								<a href="#">mobile-membercard-admin-biz</a>
							</td>
						</tr>
						<tr>
							<td>
								<input type="radio" name="project_sel">
								<a href="#">mobile-membercard-admin-web</a>
							</td>
						</tr>
						<tr>
							<td>
								<input type="radio" name="project_sel">
								<a href="#">mobile-membercard-bc-biz</a>
							</td>
						</tr>
						<tr>
							<td>
								<input type="radio" name="project_sel">
								<a href="#">mobile-membercard-bc-web</a>
							</td>
						</tr>
						<tr>
							<td>
								<input type="radio" name="project_sel">
								<a href="#">user-web-admin-site-map</a>
							</td>
						</tr>
					</tbody>
				</table>
				<div class="pagination" style="height: 25px;">
					<input type="hidden" name="pageNo" value="1"/>
					<ul>
						<li><a href="#" onclick="jump2Page(1, this);return false;">首页</a></li>
						<li class="active"><a href="#" onclick="jump2Page(1, this);return false;">1</a></li>
						<li><a href="#" onclick="jump2Page(2, this);return false;">2</a></li>
					    <li><a href="#" onclick="jump2Page(2, this);return false;">尾页</a></li>
					</ul>
				</div>
			</div>
			<div class="span2 thumbnail" style="height:518px;overflow-y: auto;">
				<table class="table table-striped table-condensed">
					<thead>
						<tr>
							<th>Machine</th>
							<th>Kernel</th>
						</tr>
					</thead>
					<tbody>
						<tr>
							<td>
								<input type="radio" name="machine_sel">
								192.168.8.40<div class="z6 a-f-e" title="在线"></div>
							</td>
							<td>1.0</td>
						</tr>
						<tr>
							<td>
								<input type="radio" name="machine_sel">
								192.168.8.41<div class="u6 a-f-e" title="忙碌"></div>
							</td>
							<td>1.0</td>
						</tr>
						<tr>
							<td>
								<input type="radio" name="machine_sel">
								192.168.8.42<div class="z6 a-f-e" title="在线"></div>
							</td>
							<td>1.0</td>
						</tr>
						<tr>
							<td>
								<input type="radio" name="machine_sel">
								192.168.8.43<div class="z6 a-f-e" title="在线"></div>
							</td>
							<td>1.0</td>
						</tr>
						<tr>
							<td>
								<input type="radio" name="machine_sel">
								192.168.8.40<div class="z6 a-f-e" title="在线"></div>
							</td>
							<td>1.0</td>
						</tr>
						<tr>
							<td>
								<input type="radio" name="machine_sel">
								192.168.8.41<div class="z6 a-f-e" title="在线"></div>
							</td>
							<td>1.0</td>
						</tr>
						<tr>
							<td>
								<input type="radio" name="machine_sel">
								192.168.8.42<div class="z6 a-f-e" title="在线"></div>
							</td>
							<td>1.0</td>
						</tr>
						<tr>
							<td>
								<input type="radio" name="machine_sel">
								192.168.8.43<div class="z6 a-f-e" title="在线"></div>
							</td>
							<td>1.0</td>
						</tr>
						<tr>
							<td>
								<input type="radio" name="machine_sel">
								192.168.8.40<div class="z6 a-f-e" title="在线"></div>
							</td>
							<td>1.0</td>
						</tr>
						<tr>
							<td>
								<input type="radio" name="machine_sel">
								192.168.8.41<div class="z6 a-f-e" title="在线"></div>
							</td>
							<td>1.0</td>
						</tr>
						<tr>
							<td>
								<input type="radio" name="machine_sel">
								192.168.8.42<div class="z6 a-f-e" title="在线"></div>
							</td>
							<td>1.0</td>
						</tr>
						<tr>
							<td>
								<input type="radio" name="machine_sel">
								192.168.8.43<div class="z6 a-f-e" title="在线"></div>
							</td>
							<td>1.0</td>
						</tr>
						<tr>
							<td>
								<input type="radio" name="machine_sel">
								192.168.8.40<div class="z6 a-f-e" title="在线"></div>
							</td>
							<td>1.0</td>
						</tr>
						<tr>
							<td>
								<input type="radio" name="machine_sel">
								192.168.8.41<div class="z6 a-f-e" title="在线"></div>
							</td>
							<td>1.0</td>
						</tr>
						<tr>
							<td>
								<input type="radio" name="machine_sel">
								192.168.8.42<div class="z6 a-f-e" title="在线"></div>
							</td>
							<td>1.0</td>
						</tr>
						<tr>
							<td>
								<input type="radio" name="machine_sel">
								192.168.8.43<div class="z6 a-f-e" title="在线"></div>
							</td>
							<td>1.0</td>
						</tr>
						<tr>
							<td>
								<input type="radio" name="machine_sel">
								192.168.8.40<div class="z6 a-f-e" title="在线"></div>
							</td>
							<td>1.0</td>
						</tr>
						<tr>
							<td>
								<input type="radio" name="machine_sel">
								192.168.8.41<div class="z6 a-f-e" title="在线"></div>
							</td>
							<td>1.0</td>
						</tr>
						<tr>
							<td>
								<input type="radio" name="machine_sel">
								192.168.8.42<div class="z6 a-f-e" title="在线"></div>
							</td>
							<td>1.0</td>
						</tr>
						<tr>
							<td>
								<input type="radio" name="machine_sel">
								192.168.8.43<div class="z6 a-f-e" title="在线"></div>
							</td>
							<td>1.0</td>
						</tr>
					</tbody>
				</table>
			</div>
			<div class="span5 thumbnail" style="height:518px;overflow-y: auto;">
				<table class="table table-striped table-condensed">
					<thead>
						<tr>
							<th>Group</th>
							<th>Artifact</th>
							<th>Kernel</th>
							<th>App</th>
						</tr>
					</thead>
					<tbody>
						<tr>
							<td>opensymphony</td>
							<td>sitemesh</td>
							<td>1.0</td>
							<td>1.1</td>
						</tr>
						<tr>
							<td>org.apache.struts</td>
							<td>struts2-core</td>
							<td>2.1.8</td>
							<td>2.2.0</td>
						</tr>
						<tr>
							<td>org.apache.struts</td>
							<td>struts2-json-plugin</td>
							<td>2.1.8</td>
							<td>2.2.0</td>
						</tr>
						<tr>
							<td>org.springframework</td>
							<td>spring-web</td>
							<td>2.5.6</td>
							<td>3.0</td>
						</tr>
						<tr>
							<td>opensymphony</td>
							<td>sitemesh</td>
							<td>1.0</td>
							<td>1.1</td>
						</tr>
						<tr>
							<td>org.apache.struts</td>
							<td>struts2-core</td>
							<td>2.1.8</td>
							<td>2.2.0</td>
						</tr>
						<tr>
							<td>org.apache.struts</td>
							<td>struts2-json-plugin</td>
							<td>2.1.8</td>
							<td>2.2.0</td>
						</tr>
						<tr>
							<td>org.springframework</td>
							<td>spring-web</td>
							<td>2.5.6</td>
							<td>3.0</td>
						</tr>
						<tr>
							<td>opensymphony</td>
							<td>sitemesh</td>
							<td>1.0</td>
							<td>1.1</td>
						</tr>
						<tr>
							<td>org.apache.struts</td>
							<td>struts2-core</td>
							<td>2.1.8</td>
							<td>2.2.0</td>
						</tr>
						<tr>
							<td>org.apache.struts</td>
							<td>struts2-json-plugin</td>
							<td>2.1.8</td>
							<td>2.2.0</td>
						</tr>
						<tr>
							<td>org.springframework</td>
							<td>spring-web</td>
							<td>2.5.6</td>
							<td>3.0</td>
						</tr>
						<tr>
							<td>opensymphony</td>
							<td>sitemesh</td>
							<td>1.0</td>
							<td>1.1</td>
						</tr>
						<tr>
							<td>org.apache.struts</td>
							<td>struts2-core</td>
							<td>2.1.8</td>
							<td>2.2.0</td>
						</tr>
						<tr>
							<td>org.apache.struts</td>
							<td>struts2-json-plugin</td>
							<td>2.1.8</td>
							<td>2.2.0</td>
						</tr>
						<tr>
							<td>org.springframework</td>
							<td>spring-web</td>
							<td>2.5.6</td>
							<td>3.0</td>
						</tr>
						<tr>
							<td>opensymphony</td>
							<td>sitemesh</td>
							<td>1.0</td>
							<td>1.1</td>
						</tr>
						<tr>
							<td>org.apache.struts</td>
							<td>struts2-core</td>
							<td>2.1.8</td>
							<td>2.2.0</td>
						</tr>
						<tr>
							<td>org.apache.struts</td>
							<td>struts2-json-plugin</td>
							<td>2.1.8</td>
							<td>2.2.0</td>
						</tr>
						<tr>
							<td>org.springframework</td>
							<td>spring-web</td>
							<td>2.5.6</td>
							<td>3.0</td>
						</tr>
						<tr>
							<td>opensymphony</td>
							<td>sitemesh</td>
							<td>1.0</td>
							<td>1.1</td>
						</tr>
						<tr>
							<td>org.apache.struts</td>
							<td>struts2-core</td>
							<td>2.1.8</td>
							<td>2.2.0</td>
						</tr>
						<tr>
							<td>org.apache.struts</td>
							<td>struts2-json-plugin</td>
							<td>2.1.8</td>
							<td>2.2.0</td>
						</tr>
						<tr>
							<td>org.springframework</td>
							<td>spring-web</td>
							<td>2.5.6</td>
							<td>3.0</td>
						</tr>
					</tbody>
				</table>
			</div>
		</div>
	</div>
	 -->
	
	<!-- 
	<p></p>
	<div class="row-fluid">
		<div class="row-fluid">
			<form class="form-search" action="?op=home">
				<div class="input-append">
					<input type="text" name="keyword" value="${payload.keyword}"
						class="search-query input-large" placeholder="jar-version">
					<button type="submit" class="btn">Search</button>
				</div>
			</form>
		</div>
		<div class="row-fluid">
			<table class="table table-striped table-hover">
				<caption></caption>
				<thead>
					<tr>
						<th width="10%">Project</th>
						<th width="10%">Owner</th>
						<th width="20%">Hosts</th>
						<th>DependencyJars</th>
					</tr>
				</thead>
				<tbody>
					<c:forEach var="project" items="${model.projects}">
						<tr>
							<td><a href="?op=project&name=${project.name}">${project.name}</a></td>
							<td>${project.owner}</td>
							<td>${project.hosts}</td>
							<td>${project.dependencyJars}</td>
						</tr>
					</c:forEach>
				</tbody>
			</table>
		</div>
	</div>
	 -->
</a:layout>