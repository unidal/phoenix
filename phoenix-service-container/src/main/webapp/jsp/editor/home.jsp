<%@ page contentType="text/html; charset=utf-8"%>
<%@ taglib prefix="a" uri="/WEB-INF/app.tld"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="w" uri="http://www.unidal.org/web/core"%>
<jsp:useBean id="ctx" type="com.dianping.service.editor.page.home.Context" scope="request" />
<jsp:useBean id="payload" type="com.dianping.service.editor.page.home.Payload" scope="request" />
<jsp:useBean id="model" type="com.dianping.service.editor.page.home.Model" scope="request" />

<a:layout>

   <link href="${model.webapp}/css/home.css" type="text/css" rel="stylesheet">

   <br>
   <br>

   <div class="blockquote">
      <div class="tabbable tabs-left">
         <ul class="nav nav-tabs">
            <c:forEach var="service" items="${model.deployment.activeServices}" varStatus="status">
               <li class="${not empty service.instances?'enabled':''} ${(service.type.name eq payload.serviceType) or (empty payload.serviceType and status.first)?'active':''}">
                  <a href="#tab${status.index}" data-toggle="tab">${service.type.name}<span class="service-indicator"></span></a>
               </li>
            </c:forEach>
         </ul>
         <div class="tab-content">
            <c:forEach var="service" items="${model.deployment.activeServices}" varStatus="status">
               <div class="tab-pane ${(service.type.name eq payload.serviceType) or (empty payload.serviceType and status.first)?'active':''}" id="tab${status.index}">
                  <c:forEach var="instance" items="${service.instances}">
                     <form method="get" class="form-horizontal">
                        <input type="hidden" name="op" value="edit">
                        <input type="hidden" name="serviceType" value="${w:htmlEncode(service.type.name)}">
                        <input type="hidden" name="id" value="${w:htmlEncode(instance.id)}">
                        <fieldset>
                           <legend>${service.type.name}<c:if test="${not (instance.id eq 'default')}">&nbsp;:&nbsp;${instance.id}</c:if></legend>
                           <c:forEach var="property" items="${instance.properties}" varStatus="status">
                              <div class="control-group">
                                 <label class="control-label" for="${ctx.nextHtmlId}">${property.name}</label>
                                 <div class="controls">
                                    <input type="text" id="${ctx.currentHtmlId}" name="properties.${property.name}" value="${property.value}" class="input-xlarge">
                                 </div>
                              </div>
                           </c:forEach>
                           <div class="control-group">
                              <div class="controls">
                                 <button type="submit" name="save" class="btn btn-primary">Save</button>
   
                                 <br> <br>
                                 <div class="well">
                                    <h3>title here</h3>
                                    <p>description here description here description here description here</p>
                                 </div>
                              </div>
                           </div>
                        </fieldset>
                     </form>
                  </c:forEach>
               </div>
            </c:forEach>
         </div>
      </div>
   </div>

</a:layout>