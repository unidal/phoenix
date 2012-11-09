<%@ page contentType="text/html; charset=utf-8"%>
<%@ taglib prefix="a" uri="/WEB-INF/app.tld"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="w" uri="http://www.unidal.org/web/core"%>
<jsp:useBean id="ctx" type="com.dianping.kernel.inspect.page.home.Context" scope="request" />
<jsp:useBean id="payload" type="com.dianping.kernel.inspect.page.home.Payload" scope="request" />
<jsp:useBean id="model" type="com.dianping.kernel.inspect.page.home.Model" scope="request" />

<a:layout>
	<c:set var="app" value="${model.applicationModel}" />

	<table border="1">
		<tr>
			<td>System Started</td>
			<td>${app.startTime}</td>
		</tr>
		<tr>
			<td>Running for</td>
			<td>${app.runningFor}</td>
		</tr>
		<tr>
			<td>Total Physical Memory</td>
			<td>${w:format(app.os.totalPhysicalMemory/1024/1024,'0.00')} MB</td>
		</tr>
		<tr>
			<td>Free Physical Memory</td>
			<td>${w:format(app.os.freePhysicalMemory/1024/1024,'0.00')} MB</td>
		</tr>
		<tr>
			<td>Total Swap Space</td>
			<td>${w:format(app.os.totalSwapSpace/1024/1024,'0.00')} MB</td>
		</tr>
		<tr>
			<td>Free Swap Space</td>
			<td>${w:format(app.os.freeSwapSpace/1024/1024,'0.00')} MB</td>
		</tr>
		<tr>
			<td>Max JVM Memory</td>
			<td>${w:format(app.maxMemory/1024/1024,'0.00')} MB</td>
		</tr>
		<tr>
			<td>Total JVM Memory</td>
			<td>${w:format(app.totalMemory/1024/1024,'0.00')} MB</td>
		</tr>
		<tr>
			<td>Used JVM Memory</td>
			<td>${w:format(app.usedMemory/1024/1024,'0.00')} MB (${w:format(app.usedMemoryPercentage,'0.00%')})</td>
		</tr>
	</table>
</a:layout>
