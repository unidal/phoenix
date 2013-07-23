package com.dianping.phoenix.service.visitor;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.unidal.lookup.ComponentTestCase;

import com.dianping.phoenix.agent.resource.entity.App;
import com.dianping.phoenix.agent.resource.entity.Container;
import com.dianping.phoenix.agent.resource.entity.Domain;
import com.dianping.phoenix.agent.resource.entity.Host;
import com.dianping.phoenix.agent.resource.entity.Lib;
import com.dianping.phoenix.agent.resource.entity.Resource;
import com.dianping.phoenix.console.page.home.Payload;
import com.dianping.phoenix.service.resource.ResourceManager;
import com.dianping.phoenix.service.visitor.resource.JarFilterStrategy;

public class FilterStrategyTest extends ComponentTestCase {

	@Test
	public void test() throws Exception {
		ResourceManager resourceManager = lookup(ResourceManager.class);
		Resource resource = resourceManager.getResource();
		Domain domain = new Domain();
		domain.setName("helloworld");
		resource.getProducts().get("PhoenixTest").getDomains().put("helloworld", domain);

		int max = 5;

		for (int idx = 0; idx < max; idx++) {
			Host host = new Host();
			Container container = new Container();
			App app = new App();
			Lib lib = new Lib();
			lib.setArtifactId("test" + idx);
			lib.setVersion(String.valueOf(idx));
			app.addLib(lib);

			lib = new Lib();
			lib.setArtifactId("test" + (idx + 1));
			lib.setVersion(String.valueOf(idx + 1));
			app.addLib(lib);

			lib = new Lib();
			lib.setArtifactId("test" + (idx + 2));
			lib.setVersion(String.valueOf(idx + 2));
			app.addLib(lib);

			container.addApp(app);
			host.setIp("127.0.0." + idx);
			host.setContainer(container);
			domain.addHost(host);
		}

		Payload payload = new Payload();
		List<String> dep = new ArrayList<String>();
		List<String> opr = new ArrayList<String>();
		List<String> ver = new ArrayList<String>();
		List<String> joints = new ArrayList<String>();

		for (int idx = 0; idx <= 2; idx++) {
			dep.add("test" + idx);
			opr.add("=");
			ver.add(String.valueOf(idx));
		}

		joints.add("or");
		joints.add("or");

		for (Host host : domain.getHosts().values()) {
			StringBuilder str = new StringBuilder(1000);
			for(Lib lib : host.getContainer().getApps().get(0).getLibs()){
				str.append(lib.getArtifactId() + "=" + lib.getVersion() + " ");
			}
			System.out.println(host.getIp() + "=[" + str.toString()+ "]");
		}

		payload.setDependencies(dep);
		payload.setOperators(opr);
		payload.setVersions(ver);
		payload.setJoints(joints);

		for (int idx = 0; idx < dep.size(); idx++) {
			System.out.println(dep.get(idx) + opr.get(idx) + ver.get(idx));
		}
		long begin = System.currentTimeMillis();
		JarFilterStrategy strategy = new JarFilterStrategy(resource, payload);
		System.out.println(System.currentTimeMillis() - begin);

		System.out.println(strategy.getStrategy());
	}

}
