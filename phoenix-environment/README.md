**Maven依赖**
	&lt;!-- requestId项目的通用容器(基于ThreadLocal) --&gt;
	&lt;dependency&gt;
		&lt;groupId&gt;com.dianping.platform&lt;/groupId&gt;
    	&lt;artifactId&gt;phoenix-environment&lt;/artifactId&gt;
    	&lt;version&gt;0.1.0&lt;/version&gt;
    &lt;/dependency&gt;
    &lt;!-- 如果使用filter,则需要依赖servlet --&gt;
    &lt;dependency&gt;
        &lt;groupId&gt;javax.servlet&lt;/groupId&gt;
        &lt;artifactId&gt;servlet-api&lt;/artifactId&gt;
        &lt;version&gt;2.5&lt;/version&gt;
    &lt;/dependency&gt;


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
