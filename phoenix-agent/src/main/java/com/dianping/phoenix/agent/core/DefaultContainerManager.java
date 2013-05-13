package com.dianping.phoenix.agent.core;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.unidal.lookup.ContainerHolder;
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
import com.site.lookup.annotation.Inject;

public class DefaultContainerManager extends ContainerHolder implements ContainerManager {
	public final static Logger logger = Logger.getLogger(DefaultContainerManager.class);

	@Inject
	private ConfigManager config;

	@Override
	public void attachContainerLoader(String domain, String version) throws Exception {

		List<File> serverXmlList = config.getServerXmlFileList();

		if (serverXmlList.size() == 0) {
			throw new RuntimeException("Container config not found!");
		}

		for (File serverXml : serverXmlList) {
			File kernelDocBase = new File(String.format(config.getKernelDocBasePattern(), domain, version));
			String domainDocBasePattern = String.format(config.getDomainDocBaseFeaturePattern(), domain);
			attachPhoenixContextLoader(serverXml, domainDocBasePattern, config.getLoaderClass(), kernelDocBase);
		}
	}

	/**
	 * Add phoenix &lt;Loader&gt; to matched &lt;Context&gt;
	 * 
	 * @param serverXml
	 *            server.xml to process
	 * @param docBasePattern
	 *            the pattern to find &lt;Context&gt; whose docBase attribute
	 *            matches <code>docBasePattern</code>, see
	 *            {@link #isDocBaseMatch(String, String)}
	 * @param loaderClass
	 *            full class name of phoenix loader
	 * @param kernelDocBase
	 *            phoenix loader's docBase
	 * @throws Exception
	 */
	private void attachPhoenixContextLoader(File serverXml, String docBasePattern, String loaderClass,
			File kernelDocBase) throws Exception {
		logger.info(String.format("try to add <Loader> whose docBase matches %s in %s", docBasePattern,
				serverXml.getAbsolutePath()));

		DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		Document doc = builder.parse(serverXml);
		NodeList ctxList = doc.getElementsByTagName("Context");

		int ctxFound = 0;
		for (int i = 0; i < ctxList.getLength(); i++) {
			Element ctx = (Element) ctxList.item(i);
			String docBase = ctx.getAttribute("docBase");
			if (isDocBaseMatch(docBase, docBasePattern)) {
				ctxFound++;
				logger.info(String.format("found matched <Context docBase=\"%s\">", docBase));
				int loaderCnt = ((Element) ctx).getElementsByTagName("Loader").getLength();
				if (loaderCnt == 0) {
					logger.info("no <Loader> found, add it");
					Element loader = doc.createElement("Loader");
					loader.setAttribute("className", loaderClass);
					loader.setAttribute("kernelDocBase", kernelDocBase.getAbsolutePath());
					ctx.appendChild(loader);
					writeDocument(serverXml, doc);
				} else {
					logger.info("<Loader> already exists, won't add");
				}
				break;
			}
		}
		logger.info(String.format("found %d matched <Context>", ctxFound));
	}

	@Override
	public void detachContainerLoader(String domain) throws Exception {
		List<File> serverXmlList = config.getServerXmlFileList();
		if (serverXmlList.size() == 0) {
			throw new RuntimeException("Container config not found!");
		}
		for (File serverXml : serverXmlList) {
			detachPhoenixContextLoader(serverXml, String.format(config.getDomainDocBaseFeaturePattern(), domain));
		}
	}

	/**
	 * Remove phoenix &lt;Loader&gt; to matched &lt;Context&gt;
	 * 
	 * @param serverXml
	 * @param docBasePattern
	 * @throws Exception
	 */
	private void detachPhoenixContextLoader(File serverXml, String docBasePattern) throws Exception {
		logger.info(String.format("try to remove <Loader> whose docBase matches %s", docBasePattern));

		DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		Document doc = builder.parse(serverXml);
		NodeList ctxList = doc.getElementsByTagName("Context");
		for (int i = 0; i < ctxList.getLength(); i++) {
			Element ctx = (Element) ctxList.item(i);
			String docBase = ctx.getAttribute("docBase");
			if (isDocBaseMatch(docBase, docBasePattern)) {
				logger.info(String.format("found matched <Context docBase=\"%s\">", docBase));
				NodeList loaderList = ((Element) ctx).getElementsByTagName("Loader");
				if (loaderList.getLength() > 0) {
					logger.info("<Loader> found, remove it");
					for (int j = 0; j < loaderList.getLength(); j++) {
						ctx.removeChild(loaderList.item(j));
					}
					writeDocument(serverXml, doc);
				} else {
					logger.info("no <Loader> found, won't remove");
				}
				return;
			}
		}
	}

	private String findAgentVersion() {
		String version = "N/A";
		InputStream in = null;
		try {
			File classesDir = new File(this.getClass().getClassLoader().getResource("").getPath());
			File pomProperties = new File(classesDir.getParentFile(),
					"META-INF/maven/com.dianping.platform/phoenix-agent/pom.properties");
			// won't exist in eclipse environment
			if (pomProperties.exists()) {
				Properties props = new Properties();
				in = new FileInputStream(pomProperties);
				props.load(in);
				version = props.getProperty("version");
			}
		} catch (Exception e) {
			logger.error("error get agent version from pom.properties", e);
		} finally {
			IOUtils.closeQuietly(in);
		}
		return version;
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
	 * Whether the value of &lt;Context&gt;'s docBase attribute matches
	 * specified <code>docBasePattern</code>
	 * 
	 * @param docBase
	 *            the docBase
	 * @param docBaseSuffix
	 *            the docBasePattern
	 * @return
	 */
	public boolean isDocBaseMatch(String docBase, String docBaseSuffix) {
		docBase = trimAndRemoveTailingSlash(docBase);
		docBaseSuffix = trimAndRemoveTailingSlash(docBaseSuffix);
		return docBase.endsWith(docBaseSuffix);
	}

	@Override
	public Response reportContainerStatus() throws Exception {
		Response res = new Response();
		List<File> serverXmlList = config.getServerXmlFileList();
		for (File serverXml : serverXmlList) {
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

		res.setVersion(findAgentVersion());

		return res;
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

	private String trimAndRemoveTailingSlash(String docBase) {
		docBase = docBase.trim();
		while (docBase.endsWith("/") || docBase.endsWith("\\")) {
			docBase = docBase.substring(0, docBase.length() - 1);
		}
		return docBase;
	}

	/**
	 * Write <code>doc</code> to <code>serverXml</code>
	 * 
	 * @param serverXml
	 * @param doc
	 * @throws Exception
	 */
	private void writeDocument(File serverXml, Document doc) throws Exception {
		Transformer transformer = TransformerFactory.newInstance().newTransformer();
		DOMSource source = new DOMSource(doc);
		FileOutputStream fout = new FileOutputStream(serverXml);
		StreamResult result = new StreamResult(fout);
		transformer.transform(source, result);
	}
}
