<%@ page contentType="text/html; charset=utf-8" %>
<jsp:useBean id="ctx" type="com.dianping.phoenix.dev.agent.page.home.Context" scope="request"/>
<jsp:useBean id="payload" type="com.dianping.phoenix.dev.agent.page.home.Payload" scope="request"/>
<jsp:useBean id="model" type="com.dianping.phoenix.dev.agent.page.home.Model" scope="request"/>

View of home page under agent
<jsp:getProperty name="payload" property="projectName" />