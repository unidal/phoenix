<%@ page contentType="text/html; charset=utf-8"%>
<%@ taglib prefix="a" uri="http://www.dianping.com/phoenix/console"%>
<%@ taglib prefix="res" uri="http://www.unidal.org/webres"%>
<jsp:useBean id="ctx"
	type="com.dianping.phoenix.console.page.home.Context" scope="request" />
<jsp:useBean id="payload"
	type="com.dianping.phoenix.console.page.home.Payload" scope="request" />
<jsp:useBean id="model"
	type="com.dianping.phoenix.console.page.home.Model" scope="request" />

<a:layout>
	<ul class="breadcrumb">
		<li><a href="${model.webapp}/console/home">Home</a><span class="divider">/</span></li>
		<li class="active">About</li>
	</ul>

	<div class="row-fluid">
		<div class="hero-unit">
			<div class="row-fluid">
				<div class="span9">
					<h1>Phoenix@DP</h1>
					<p class="text-info">架构JAR包发布工具</p>
					<p class="text-info">统一替换架构产品的JAR包，在此过程中确保一系列的安全，包括正确性验证，失败回滚等</p>
					<p>
						Designed by 
						<span class="label label-inverse">老吴</span>
						<span class="label label-success">Figo</span>
						<span class="label label-important">向彬</span>
						<span class="label label-warning">老马</span>
						<span class="label label-info">Danson</span>
						<span class="label label-info">song</span>
						<span class="label label-info">克柱</span>
					</p>
					<p>
						Hackthon by 
						<span class="label label-inverse">老吴</span>
						<span class="label label-warning">老马</span>
						<span class="label label-info">尤勇</span>
						<span class="label label-important">一鸣</span>
					</p>
				</div>
				<div class="span3">
					<img width="200px" src="${model.webapp}/img/phoenix2.png">
				</div>
			</div>
		</div>
	</div>
	<div class="row-fluid">
		<div class="span4">
			<h2>快速迭代、敢于尝试</h2>
			<p>加速平台架构产品开发进程</p>
		</div>
		<!--/span-->
		<div class="span4">
			<h2>拥抱变化、及时响应</h2>
			<p>缩短架构产品到稳定周期</p>
		</div>
		<!--/span-->
		<div class="span4">
			<h2>敏捷开发、敏捷发布</h2>
			<p>整个点评业务也会更加稳定，线上故障越来越少</p>
		</div>
		<!--/span-->
	</div>
	<!--/row-->

</a:layout>
