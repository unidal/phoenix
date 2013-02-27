<%@ page contentType="text/html; charset=utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<jsp:useBean id="model" type="com.dianping.phoenix.console.page.version.Model" scope="request"/>
<table class="table table-striped table-condensed lion">
	<tbody>
		<c:forEach var="deliverable" items="${model.deliverables}">
			<tr>
				<td width="120">${deliverable.warVersion}</td>
				<td>
					${deliverable.description}
					<span class="pull-right hide btn_container">
						<button version="${deliverable.id}" class="btn btn-mini2 pull-right" name="btn_del" type="button">删除</button>
					</span>
				</td>
			</tr>
		</c:forEach>
	</tbody>
</table>