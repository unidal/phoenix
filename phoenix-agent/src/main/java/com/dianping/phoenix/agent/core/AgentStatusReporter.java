package com.dianping.phoenix.agent.core;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FilenameFilter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.log4j.Logger;
import org.unidal.lookup.ContainerHolder;
import org.unidal.lookup.annotation.Inject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.dianping.cat.configuration.NetworkInterfaceManager;
import com.dianping.phoenix.agent.core.shell.ScriptExecutor;
import com.dianping.phoenix.agent.response.entity.Container;
import com.dianping.phoenix.agent.response.entity.Domain;
import com.dianping.phoenix.agent.response.entity.Kernel;
import com.dianping.phoenix.agent.response.entity.Lib;
import com.dianping.phoenix.agent.response.entity.Response;
import com.dianping.phoenix.agent.response.entity.War;
import com.dianping.phoenix.agent.util.Artifact;
import com.dianping.phoenix.agent.util.ArtifactResolver;
import com.dianping.phoenix.configure.ConfigManager;

public class AgentStatusReporter extends ContainerHolder {
	
	private final static Logger logger = Logger.getLogger(AgentStatusReporter.class);

	@Inject
	private ConfigManager config;

	public AgentStatusReporter() {
	}

	public Response report() throws Exception {
		Response res = new Response();
		File serverXml = config.getServerXml();
		if (serverXml != null && serverXml.exists()) {
			DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			Document doc = builder.parse(serverXml);
			NodeList ctxList = doc.getElementsByTagName("Context");
			// for each <Context>
			for (int i = 0; i < ctxList.getLength(); i++) {
				Element ctx = (Element) ctxList.item(i);
				String strDomainDocBase = ctx.getAttribute("docBase");
				NodeList loaderList = ctx.getElementsByTagName("Loader");
				String strKernelDocBase = null;
				if (loaderList.getLength() > 0) {
					Element loader = (Element) loaderList.item(0);
					strKernelDocBase = loader.getAttribute("kernelDocBase");
				}

				// kernel info
				War kernelWar = reportWar(strKernelDocBase);
				Kernel kernel = new Kernel();
				kernel.setWar(kernelWar);

				// domain info
				Domain domain = new Domain();
				War domainWar = reportWar(strDomainDocBase);
				domain.setKernel(kernel);
				domain.setWar(domainWar);

				res.addDomain(domain);
			}
		}

		Container container = new Container();
		container.setInstallPath(config.getContainerInstallPath());
		container.setName(config.getContainerType().toString().toLowerCase());
		container.setStatus(getStatus());
		// TODO
		container.setVersion("");
		res.setContainer(container);
		res.setIp(NetworkInterfaceManager.INSTANCE.getLocalHostAddress());

		return res;
	}

	private String getStatus() {
		String status = "unknown";
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		File scriptFile = config.getAgentStatusScriptFile();
		if (!scriptFile.isFile()) {
			logger.warn("agent status script file not found");
		} else {
			try {
				StringBuilder sb = new StringBuilder();
				sb.append(scriptFile.getAbsolutePath());
				sb.append(String.format(" -e \"%s\" ", config.getEnv()));
				sb.append(String.format(" -b \"%s\" ", config.getContainerInstallPath()));
				sb.append(String.format(" -c \"%s\" ", config.getContainerType()));
				ScriptExecutor scriptExecutor = lookup(ScriptExecutor.class);
				int exitCode = scriptExecutor.exec(sb.toString(), out, out);
				status = exitCode == 0 ? "up" : "down";
				logger.info(out.toString());
			} catch (Exception e) {
				logger.warn(String.format("error get container status, set to %s", status), e);
			}
		}
		return status;
	}

	/**
	 * Get full basic infos of war <code>warDocBase</code>
	 * 
	 * @param warDocBase
	 *            war docBase
	 * @return
	 */
	private War reportWar(File warDocBase) {

		if (warDocBase == null || !warDocBase.exists()) {
			return null;
		}

		War war = new War();
		ArtifactResolver resolver = new ArtifactResolver();
		Artifact artifactWar = resolver.resolve(warDocBase);

		if (artifactWar == null) {
			return null;
		} else {
			war.setName(artifactWar.getArtifactId());
			war.setVersion(artifactWar.getVersion());

			File libDir = new File(warDocBase, "WEB-INF/lib");
			File[] libs = libDir.listFiles(new FilenameFilter() {

				@Override
				public boolean accept(File dir, String name) {
					return name.endsWith(".jar");
				}
			});

			for (int i = 0; i < libs.length; i++) {
				Artifact artifactLibJar = resolver.resolve(libs[i]);
				Lib lib = new Lib();
				lib.setArtifactId(artifactLibJar.getArtifactId());
				lib.setGroupId(artifactLibJar.getGroupId());
				lib.setVersion(artifactLibJar.getVersion());
				war.addLib(lib);
			}

			return war;
		}
	}

	public War reportWar(String strWarDocBase) {
		if (strWarDocBase == null) {
			return null;
		} else {
			return reportWar(new File(strWarDocBase));
		}
	}

}
