<%@ page contentType="text/html; charset=utf-8"%>
<%@ taglib prefix="a" uri="/WEB-INF/app.tld"%>
<%@ taglib prefix="res" uri="http://www.unidal.org/webres"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<jsp:useBean id="ctx" type="com.dianping.phoenix.console.page.home.Context" scope="request" />
<jsp:useBean id="payload" type="com.dianping.phoenix.console.page.home.Payload" scope="request" />
<jsp:useBean id="model" type="com.dianping.phoenix.console.page.home.Model" scope="request" />

<a:layout>

	<div class="container-fluid">
		<div class="row-fluid">
			<div class="span12">
				<table class="table table-bordered table-condensed nohover">
					<tbody>
						<tr>
							<th width="100">Project</th>
							<td width="200">shop-web</td>
							<th width="100">Owner</th>
							<td width="200">jianbing.xu</td>
							<th width="100">Desc</th>
							<td>图片前台web应用</td>
						</tr>
					</tbody>
				</table>
			</div>
		</div>
		
		<div class="row-fluid">
			<div class="span3">
				<!-- 
				<div class="row-fluid">
					<div class="span12" style="min-height: 0px;">
						<li class="icon-arrow-right"></li>：右侧显示选定机器上该项目的依赖包情况
					</div>
				</div>
				 -->
				<div class="row-fluid">
					<div class="span12 thumbnail" style="height:280px;overflow-y: auto;">
						<table class="table table-striped table-condensed lion">
							<thead>
								<tr>
									<th>
										<input type="checkbox" id="pc-${id}" class="category-check"/>
										Machine
									</th>
									<th>Kernel</th>
									<th>App</th>
								</tr>
							</thead>
							<tbody>
								<tr>
									<td>
										<input type="checkbox" name="machine_sel" id="machine_1">
										<label style="margin-left: 0px;vertical-align: bottom;" for="machine_1">192.168.8.40</label><div class="z6 a-f-e" title="在线"></div>
									</td>
									<td>1.0</td>
									<td>2.0.3</td>
								</tr>
								<tr>
									<td>
										<input type="checkbox" name="machine_sel" id="machine_2">
										<label style="margin-left: 0px;vertical-align: bottom;" for="machine_2">192.168.8.41</label><div class="u6 a-f-e" title="忙碌"></div>
									</td>
									<td>1.0</td>
									<td>2.0.3</td>
								</tr>
								<tr>
									<td>
										<input type="checkbox" name="machine_sel" id="machine_3">
										<label style="margin-left: 0px;vertical-align: bottom;" for="machine_3">192.168.8.42</label><div class="z6 a-f-e" title="在线"></div>
									</td>
									<td>1.0</td>
									<td>2.0.3</td>
								</tr>
								<tr>
									<td>
										<input type="checkbox" name="machine_sel" id="machine_4">
										<label style="margin-left: 0px;vertical-align: bottom;" for="machine_4">192.168.8.43</label><div class="z6 a-f-e" title="在线"></div>
									</td>
									<td>1.0</td>
									<td>2.0.3</td>
								</tr>
								<tr>
									<td>
										<input type="checkbox" name="machine_sel" id="machine_5">
										<label style="margin-left: 0px;vertical-align: bottom;" for="machine_5">192.168.8.44</label><div class="z6 a-f-e" title="在线"></div>
									</td>
									<td>1.0</td>
									<td>2.0.3</td>
								</tr>
								<tr>
									<td>
										<input type="checkbox" name="machine_sel" id="machine_6">
										<label style="margin-left: 0px;vertical-align: bottom;" for="machine_6">192.168.8.40</label><div class="z6 a-f-e" title="在线"></div>
									</td>
									<td>1.0</td>
									<td>2.0.3</td>
								</tr>
								<tr>
									<td>
										<input type="checkbox" name="machine_sel" id="machine_7">
										<label style="margin-left: 0px;vertical-align: bottom;" for="machine_7">192.168.8.40</label><div class="z6 a-f-e" title="在线"></div>
									</td>
									<td>1.0</td>
									<td>2.0.3</td>
								</tr>
								<tr>
									<td>
										<input type="checkbox" name="machine_sel" id="machine_8">
										<label style="margin-left: 0px;vertical-align: bottom;" for="machine_8">192.168.8.40</label><div class="z6 a-f-e" title="在线"></div>
									</td>
									<td>1.0</td>
									<td>2.0.3</td>
								</tr>
								<tr>
									<td>
										<input type="checkbox" name="machine_sel" id="machine_9">
										<label style="margin-left: 0px;vertical-align: bottom;" for="machine_9">192.168.8.40</label><div class="z6 a-f-e" title="在线"></div>
									</td>
									<td>1.0</td>
									<td>2.0.3</td>
								</tr>
								<tr>
									<td>
										<input type="checkbox" name="machine_sel" id="machine_10">
										192.168.8.41<div class="z6 a-f-e" title="在线"></div>
									</td>
									<td>1.0</td>
									<td>2.0.3</td>
								</tr>
								<tr>
									<td>
										<input type="checkbox" name="machine_sel" id="machine_11">
										192.168.8.42<div class="z6 a-f-e" title="在线"></div>
									</td>
									<td>1.0</td>
									<td>2.0.3</td>
								</tr>
								<tr>
									<td>
										<input type="checkbox" name="machine_sel" id="machine_12">
										192.168.8.43<div class="z6 a-f-e" title="在线"></div>
									</td>
									<td>1.0</td>
									<td>2.0.3</td>
								</tr>
								<tr>
									<td>
										<input type="checkbox" name="machine_sel" id="machine_13">
										192.168.8.40<div class="z6 a-f-e" title="在线"></div>
									</td>
									<td>1.0</td>
									<td>2.0.3</td>
								</tr>
								<tr>
									<td>
										<input type="checkbox" name="machine_sel" id="machine_14">
										192.168.8.41<div class="z6 a-f-e" title="在线"></div>
									</td>
									<td>1.0</td>
									<td>2.0.3</td>
								</tr>
								<tr>
									<td>
										<input type="checkbox" name="machine_sel" id="machine_15">
										192.168.8.42<div class="z6 a-f-e" title="在线"></div>
									</td>
									<td>1.0</td>
									<td>2.0.3</td>
								</tr>
								<tr>
									<td>
										<input type="checkbox" name="machine_sel" id="machine_16">
										192.168.8.43<div class="z6 a-f-e" title="在线"></div>
									</td>
									<td>1.0</td>
									<td>2.0.3</td>
								</tr>
								<tr>
									<td>
										<input type="checkbox" name="machine_sel" id="machine_17">
										192.168.8.40<div class="z6 a-f-e" title="在线"></div>
									</td>
									<td>1.0</td>
									<td>2.0.3</td>
								</tr>
								<tr>
									<td>
										<input type="checkbox" name="machine_sel" id="machine_18">
										192.168.8.41<div class="z6 a-f-e" title="在线"></div>
									</td>
									<td>1.0</td>
									<td>2.0.3</td>
								</tr>
								<tr>
									<td>
										<input type="checkbox" name="machine_sel" id="machine_19">
										192.168.8.42<div class="z6 a-f-e" title="在线"></div>
									</td>
									<td>1.0</td>
									<td>2.0.3</td>
								</tr>
								<tr>
									<td>
										<input type="checkbox" name="machine_sel" id="machine_20">
										192.168.8.43<div class="z6 a-f-e" title="在线"></div>
									</td>
									<td>1.0</td>
									<td>2.0.3</td>
								</tr>
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
										<select name="KernelVersion">
											<option>1.0.0</option>
											<option>1.0.1</option>
											<option>1.0.2</option>
											<option>1.0.3</option>
											<option>1.0.4</option>
											<option>1.2.0</option>
											<option>1.2.1</option>
										</select>
									</td>
								</tr>
							</tbody>
						</table>
					</div>
				</div>
			
				<h3>Deploy policy</h3>
				<form class="form-horizontal" method="get">
					<input type="hidden" name="op" value="deploy"/>
					<div class="row-fluid">
						<div class="span12 thumbnail">
							 <table class="table table-condensed lion nohover" style="margin:0 0 0;border-bottom:1px solid #DDD;">
							 	<thead>
								    <tr>
										<th colspan="3">
											<label for="pc-${id}" class="help-inline" style="padding-left: 0px;"><strong style="color:#08C;">部署方式</strong></label>
										</th>
								    </tr>
								</thead>
								<tbody>
									<tr>
										<td width="190">
											<input type="radio" id="deploy_1" name="deploy_type" checked="checked"><label for="deploy_1">1 -> 1 -> 1 -> 1 ( 每次一台 )</label>
										</td>
										<td width="190">
											<input type="radio" id="deploy_2" name="deploy_type"><label for="deploy_2">1 -> 2 -> 2 -> 2 ( 每次两台 )</label>
										</td>
										<td>
											<input type="radio" id="deploy_3" name="deploy_type"><label for="deploy_3">1 -> 2 -> 4 -> 6 ( +2递增 )</label>
										</td>
									</tr>
								</tbody>
							</table>
							<table class="table table-condensed lion nohover" style="margin:0 0 0;border-bottom:1px solid #DDD;">
							 	<thead>
								    <tr>
										<th colspan="3">
											<label for="pc-${id}" class="help-inline" style="padding-left: 0px;"><strong style="color:#08C;">错误处理</strong></label>
										</th>
								    </tr>
								</thead>
								<tbody>
									<tr>
										<td width="180">
											<input type="radio" id="error_1" name="error_type" checked="checked"><label for="error_1">终断后续发布</label>
										</td>
										<td>
											<input type="radio" id="error_2" name="error_type"><label for="error_2">继续后续发布</label>
										</td>
									</tr>
								</tbody>
							</table>
						</div>
					</div>
					<br />
					<div class="row-fluid">
						<button type="submit" class="btn btn-primary">Deploy</button>
						<!-- TODO: 需要移除 -->
						<input type="hidden" name="plan" value="pigeon1.6.1">
						<input type="hidden" name="hosts" value="127.0.0.1">
					</div>
				</form>
			</div>
		</div>
		
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
</a:layout>