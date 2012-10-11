package com.dianping.phoenix.bootstrap;

import java.io.IOException;
import java.net.URL;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;

import com.dianping.phoenix.bootstrap.DefaultClasspathBuilder.VersionComparator;
import com.dianping.phoenix.bootstrap.server.DevModeWebappProvider;

public class DefaultClasspathBuilderTest {
	private void checkVersion(String v2, String v1) {
		VersionComparator comparator = new VersionComparator();

		Assert.assertTrue(String.format("Not backcompatible to upgrade version from %s to %s!", v2, v1),
		      comparator.compare(v1, v2) > 0);
	}

	@Test
	public void testBuild() throws IOException {
		DefaultClasspathBuilder builder = new DefaultClasspathBuilder();
		WebappProvider kernelProvider = new DevModeWebappProvider("../phoenix-kernel", "phoenix-kernel");
		WebappProvider appProvider = new DevModeWebappProvider("../phoenix-samples/sample-app1", "sample-app1");
		List<URL> urls = builder.build(kernelProvider, appProvider);
		StringBuilder sb = new StringBuilder(8192);

		for (URL url : urls) {
			String path = url.getPath();
			int index = path.indexOf("phoenix-kernel");

			if (index < 0) {
				index = path.indexOf("sample-app1");
			}

			if (index > 0) {
				sb.append(path.substring(index)).append("\n");
			} else {
				sb.append(path).append("\n");
			}
		}

		Assert.assertEquals("sample-app1/target/classes/\n" + //
		      "sample-app1/target/sample-app1/WEB-INF/lib/ant-1.7.1.jar\n" + //
		      "sample-app1/target/sample-app1/WEB-INF/lib/ant-launcher-1.7.1.jar\n" + //
		      "sample-app1/target/sample-app1/WEB-INF/lib/antlr-2.7.7.jar\n" + //
		      "sample-app1/target/sample-app1/WEB-INF/lib/antlr-3.1.3.jar\n" + //
		      "sample-app1/target/sample-app1/WEB-INF/lib/antlr-runtime-3.1.3.jar\n" + //
		      "sample-app1/target/sample-app1/WEB-INF/lib/aspectjrt-1.5.3.jar\n" + //
		      "sample-app1/target/sample-app1/WEB-INF/lib/avalon-framework-4.1.3.jar\n" + //
		      "sample-app1/target/sample-app1/WEB-INF/lib/c3p0-0.9.1.2.jar\n" + //
		      "phoenix-kernel/target/phoenix-kernel/WEB-INF/lib/cat-core-0.4.1.jar\n" + //
		      "sample-app1/target/sample-app1/WEB-INF/lib/commons-dbcp-1.2.2.jar\n" + //
		      "sample-app1/target/sample-app1/WEB-INF/lib/commons-io-2.0.1.jar\n" + //
		      "sample-app1/target/sample-app1/WEB-INF/lib/commons-lang-2.3.jar\n" + //
		      "sample-app1/target/sample-app1/WEB-INF/lib/commons-logging-1.1.jar\n" + //
		      "sample-app1/target/sample-app1/WEB-INF/lib/commons-logging-api-1.1.jar\n" + //
		      "sample-app1/target/sample-app1/WEB-INF/lib/commons-pool-1.3.jar\n" + //
		      "sample-app1/target/sample-app1/WEB-INF/lib/dom4j-1.6.1.jar\n" + //
		      "sample-app1/target/sample-app1/WEB-INF/lib/dpsf-net-1.6.0.jar\n" + //
		      "sample-app1/target/sample-app1/WEB-INF/lib/google-collections-1.0.jar\n" + //
		      "sample-app1/target/sample-app1/WEB-INF/lib/groovy-all-1.6.4.jar\n" + //
		      "sample-app1/target/sample-app1/WEB-INF/lib/hawk-client-0.6.7.jar\n" + //
		      "sample-app1/target/sample-app1/WEB-INF/lib/hawk-common-0.1.2.jar\n" + //
		      "sample-app1/target/sample-app1/WEB-INF/lib/hessian-3.1.5.jar\n" + //
		      "sample-app1/target/sample-app1/WEB-INF/lib/ibatis-2.3.4.726.jar\n" + //
		      "sample-app1/target/sample-app1/WEB-INF/lib/jackson-core-asl-1.9.4.jar\n" + //
		      "sample-app1/target/sample-app1/WEB-INF/lib/jackson-mapper-asl-1.9.4.jar\n" + //
		      "sample-app1/target/sample-app1/WEB-INF/lib/jackson-xc-1.9.4.jar\n" + //
		      "sample-app1/target/sample-app1/WEB-INF/lib/jaxen-1.1.1.jar\n" + //
		      "sample-app1/target/sample-app1/WEB-INF/lib/jdom-1.0.jar\n" + //
		      "sample-app1/target/sample-app1/WEB-INF/lib/jline-0.9.94.jar\n" + //
		      "sample-app1/target/sample-app1/WEB-INF/lib/junit-4.8.1.jar\n" + //
		      "sample-app1/target/sample-app1/WEB-INF/lib/libthrift-0.4.0.jar\n" + //
		      "sample-app1/target/sample-app1/WEB-INF/lib/lion-client-0.2.2.jar\n" + //
		      "sample-app1/target/sample-app1/WEB-INF/lib/lion-dev-1.0.0.jar\n" + //
		      "sample-app1/target/sample-app1/WEB-INF/lib/log4j-1.2.14.jar\n" + //
		      "sample-app1/target/sample-app1/WEB-INF/lib/logkit-1.0.1.jar\n" + //
		      "phoenix-kernel/target/phoenix-kernel/WEB-INF/lib/lookup-1.1.4.jar\n" + //
		      "sample-app1/target/sample-app1/WEB-INF/lib/mysql-connector-java-5.1.6.jar\n" + //
		      "sample-app1/target/sample-app1/WEB-INF/lib/netty-3.2.7.Final.jar\n" + //
		      "sample-app1/target/sample-app1/WEB-INF/lib/plexus-classworlds-2.2.2.jar\n" + //
		      "sample-app1/target/sample-app1/WEB-INF/lib/plexus-container-default-1.5.5.jar\n" + //
		      "sample-app1/target/sample-app1/WEB-INF/lib/plexus-utils-1.4.5.jar\n" + //
		      "sample-app1/target/sample-app1/WEB-INF/lib/protobuf-java-2.3.0.jar\n" + //
		      "sample-app1/target/sample-app1/WEB-INF/lib/slf4j-api-1.4.3.jar\n" + //
		      "sample-app1/target/sample-app1/WEB-INF/lib/slf4j-log4j12-1.4.3.jar\n" + //
		      "sample-app1/target/sample-app1/WEB-INF/lib/spring-2.5.6.jar\n" + //
		      "sample-app1/target/sample-app1/WEB-INF/lib/stringtemplate-3.2.jar\n" + //
		      "sample-app1/target/sample-app1/WEB-INF/lib/velocity-dep-1.4.jar\n" + //
		      "sample-app1/target/sample-app1/WEB-INF/lib/xalan-2.6.0.jar\n" + //
		      "sample-app1/target/sample-app1/WEB-INF/lib/xbean-reflect-3.4.jar\n" + //
		      "sample-app1/target/sample-app1/WEB-INF/lib/xercesImpl-2.6.2.jar\n" + //
		      "sample-app1/target/sample-app1/WEB-INF/lib/xmlParserAPIs-2.6.2.jar\n" + //
		      "sample-app1/target/sample-app1/WEB-INF/lib/xom-1.0.jar\n" + //
		      "sample-app1/target/sample-app1/WEB-INF/lib/xpp3_min-1.1.4c.jar\n" + //
		      "sample-app1/target/sample-app1/WEB-INF/lib/xstream-1.3.1.jar\n" + //
		      "sample-app1/target/sample-app1/WEB-INF/lib/zookeeper-3.3.2.jar\n" + //
		      "phoenix-kernel/target/classes/\n", sb.toString());
	}

	@Test
	public void testVersionCompatible() {
		checkVersion("3.2.7-Final", "3.2.7");
		checkVersion("3.2.7", "3.2.7.Final");
		checkVersion("3.2.6", "3.2.7.Final");
		checkVersion("1.6-rc1", "1.6-rc2");
		checkVersion("1.5-rc2", "1.6-rc1");
		checkVersion("1.6-rc1", "1.6");
		checkVersion("1.6-beta", "1.6");
		checkVersion("1.6-alpha", "1.6");
		checkVersion("1.5", "1.6-SNAPSHOT");
		checkVersion("1.5-SNAPSHOT", "1.6-SNAPSHOT");
		checkVersion("1.6-SNAPSHOT", "1.6-rc1");
		checkVersion("1.6-SNAPSHOT", "1.6.1");
		checkVersion("1.6-SNAPSHOT", "1.6");
		checkVersion("1.6-1", "1.6");
		checkVersion("1.5.3-1", "1.6");
		checkVersion("1.5.3.1", "1.6");
		checkVersion("1.5.3", "1.6");
		checkVersion("1.5", "1.5.3");
		checkVersion("1.5.2", "1.5.3");
		checkVersion("1.6.0", "1.7.1");
	}
}
