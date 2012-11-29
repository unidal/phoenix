<%@ page contentType="text/plain; charset=utf-8" trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<jsp:useBean id="ctx" type="com.dianping.phoenix.console.page.deploy.Context" scope="request" />
<jsp:useBean id="payload" type="com.dianping.phoenix.console.page.deploy.Payload" scope="request" />
<jsp:useBean id="model" type="com.dianping.phoenix.console.page.deploy.Model" scope="request" />
<<<<<<< HEAD
{
"status":"${model.deploy.status}", 
"hosts": [ 
<c:forEach var="entry" items="${model.deploy.hosts}" varStatus="status">
<c:set var="host" value="${entry.value}"/>{
"offset": ${host.offset},
"progress": ${host.progress},
"step": "${host.currentStep}",
"log": "${host.log}"
}<c:if test="${not status.last}">,</c:if>
</c:forEach>]
}
=======

<c:if test="${payload.count == 3}">
    {
        "status":"doing",
        "hosts": [
            {
                "host": "192.168.66.131",
                "offset": 1,
                "progress": 10,
                "step": "stop container",
                "status": "doing",
                "log": "stop jboss..."
            },
            {
                "host": "192.168.66.132",
                "offset": 2,
                "progress": 20,
                "step": "fetch kernel",
                "status": "doing",
                "log": "fetch kernel from remote git repository..."
            },
            {
                "host": "192.168.66.133",
                "offset": 0,
                "progress": 0,
                "step": "",
                "status": "pending",
                "log": ""
            },
            {
                "host": "192.168.66.134",
                "offset": 0,
                "progress": 0,
                "step": "",
                "status": "pending",
                "log": ""
            }
        ]
    }
</c:if>

<c:if test="${payload.count == 6}">
    {
        "status":"doing",
        "hosts": [
            {
                "host": "192.168.66.131",
                "offset": 2,
                "progress": 20,
                "step": "fetch kernel",
                "status": "doing",
                "log": "fetch kernel from remote git repository..."
            },
            {
                "host": "192.168.66.132",
                "offset": 3,
                "progress": 30,
                "step": "update kernel",
                "status": "doing",
                "log": "update kernel to local repository..."
            },
            {
                "host": "192.168.66.133",
                "offset": 0,
                "progress": 0,
                "step": "",
                "status": "pending",
                "log": ""
            },
            {
                "host": "192.168.66.134",
                "offset": 0,
                "progress": 0,
                "step": "",
                "status": "pending",
                "log": ""
            }
        ]
    }
</c:if>

<c:if test="${payload.count == 9}">
    {
        "status":"doing",
        "hosts": [
            {
                "host": "192.168.66.131",
                "offset": 3,
                "progress": 30,
                "step": "update kernel",
                "status": "doing",
                "log": "update kernel to local repository..."
            },
            {
                "host": "192.168.66.132",
                "offset": 4,
                "progress": 50,
                "step": "start jboss",
                "status": "doing",
                "log": "start jboss..."
            },
            {
                "host": "192.168.66.133",
                "offset": 0,
                "progress": 0,
                "step": "",
                "status": "pending",
                "log": ""
            },
            {
                "host": "192.168.66.134",
                "offset": 0,
                "progress": 0,
                "step": "",
                "status": "pending",
                "log": ""
            }
        ]
    }
</c:if>


<c:if test="${payload.count == 12}">
    {
        "status":"doing",
        "hosts": [
            {
                "host": "192.168.66.131",
                "offset": 4,
                "progress": 50,
                "step": "start jboss",
                "status": "doing",
                "log": "start jboss..."
            },
            {
                "host": "192.168.66.132",
                "offset": 5,
                "progress": 70,
                "step": "qa auto test",
                "status": "doing",
                "log": "start qa auto test for project quality..."
            },
            {
                "host": "192.168.66.133",
                "offset": 0,
                "progress": 0,
                "step": "",
                "status": "pending",
                "log": ""
            },
            {
                "host": "192.168.66.134",
                "offset": 0,
                "progress": 0,
                "step": "",
                "status": "pending",
                "log": ""
            }
        ]
    }
</c:if>

<c:if test="${payload.count == 15}">
    {
        "status":"doing",
        "hosts": [
            {
                "host": "192.168.66.131",
                "offset": 5,
                "progress": 70,
                "step": "qa auto test",
                "status": "doing",
                "log": "start qa auto test for project quality..."
            },
            {
                "host": "192.168.66.132",
                "offset": 6,
                "progress": 80,
                "step": "swith to online",
                "status": "doing",
                "log": "modify f5 to swith the project to online environment..."
            },
            {
                "host": "192.168.66.133",
                "offset": 0,
                "progress": 0,
                "step": "",
                "status": "pending",
                "log": ""
            },
            {
                "host": "192.168.66.134",
                "offset": 0,
                "progress": 0,
                "step": "",
                "status": "pending",
                "log": ""
            }
        ]
    }
</c:if>

<c:if test="${payload.count == 18}">
    {
        "status":"doing",
        "hosts": [
            {
                "host": "192.168.66.131",
                "offset": 6,
                "progress": 80,
                "step": "swith to online",
                "status": "doing",
                "log": "modify f5 to swith the project to online environment..."
            },
            {
                "host": "192.168.66.132",
                "offset": 7,
                "progress": 100,
                "step": "commit",
                "status": "success",
                "log": "complete to deploy new kernel..."
            },
            {
                "host": "192.168.66.133",
                "offset": 0,
                "progress": 0,
                "step": "",
                "status": "pending",
                "log": ""
            },
            {
                "host": "192.168.66.134",
                "offset": 0,
                "progress": 0,
                "step": "",
                "status": "pending",
                "log": ""
            }
        ]
    }
</c:if>

<c:if test="${payload.count == 21}">
    {
        "status":"doing",
        "hosts": [
            {
                "host": "192.168.66.131",
                "offset": 7,
                "progress": 80,
                "step": "commit",
                "status": "failed",
                "log": "failed to deploy new kernel..."
            },
            {
                "host": "192.168.66.133",
                "offset": 0,
                "progress": 0,
                "step": "",
                "status": "pending",
                "log": ""
            },
            {
                "host": "192.168.66.134",
                "offset": 0,
                "progress": 0,
                "step": "",
                "status": "pending",
                "log": ""
            }
        ]
    }
</c:if>
>>>>>>> d85463f406c564cba9c6fdce345284be1a505806
