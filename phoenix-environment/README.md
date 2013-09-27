**Maven依赖**
	<!-- requestId项目的通用容器(基于ThreadLocal) -->
	<dependency>
		<groupId>com.dianping.platform</groupId>
    	<artifactId>phoenix-environment</artifactId>
    	<version>0.1.0</version>
    </dependency>
    <!-- 如果使用filter,则需要依赖servlet -->
    <dependency>
        <groupId>javax.servlet</groupId>
        <artifactId>servlet-api</artifactId>
        <version>2.5</version>
    </dependency>


**通用容器的使用**: 
	存放和获取requestId
		PhoenixContext.getInstance().getRequestId();
        PhoenixContext.getInstance().setRequestId("...");
	存放和获取referRequestId
		PhoenixContext.getInstance().getReferRequestId();
        PhoenixContext.getInstance().setReferRequestId("...");

**Filter的使用**：
目前第一期，filter的功能是获取header中的pragma-page-id和pragma-prev-page-id，作为requestId和referRequestId存到容器里。
    <filter>
        <filter-name>phoenixFilter</filter-name>
        <filter-class>com.dianping.phoenix.environment.PhoenixEnvironmentFilter</filter-class>
    </filter>
    <filter-mapping>
        <filter-name>phoenixFilter</filter-name>
        <url-pattern>*.html</url-pattern><!--filter的pattern，应该以实际需求为准-->
    </filter-mapping>
