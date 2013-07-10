package com.dianping.phoenix.dev.core.tools.remedy;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Collection;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.NameFileFilter;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public enum PomRemedy {

	INSTANCE;
	
	private static Logger log = Logger.getLogger(PomRemedy.class);

	public void remedyPomIn(final File dirToScan) throws Exception {

		log.debug(String.format("scanning %s for all pom.xml under top 2 level directory",
				dirToScan.getAbsolutePath()));
		IOFileFilter fileFilter = new NameFileFilter("pom.xml");
		IOFileFilter dirFilter = new IOFileFilter() {

			@Override
			public boolean accept(File dir, String name) {
				return dir.getParentFile().equals(dirToScan) || dir.getParentFile().getParentFile().equals(dirToScan);
			}

			@Override
			public boolean accept(File file) {
				return file.getParentFile().equals(dirToScan) || file.getParentFile().getParentFile().equals(dirToScan);
			}
		};

		Collection<File> poms = FileUtils.listFiles(dirToScan, fileFilter, dirFilter);
		for (File pom : poms) {
			log.debug("scanning " + pom.getAbsolutePath());
			Document doc = remedy(pom);
			writeDocument(new FileOutputStream(pom), doc);
		}
	}

	Document remedy(File pom) throws Exception {
		DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		Document doc = builder.parse(pom);

		XPath xpath = XPathFactory.newInstance().newXPath();
		XPathExpression pluginExpr = xpath.compile("/project/build/plugins/plugin[artifactId='maven-eclipse-plugin']");
		Element pluginEle = (Element) pluginExpr.evaluate(doc, XPathConstants.NODE);
		if (pluginEle == null) {
			log.debug("no maven-eclipse-plugin found, add it");
			pluginEle = createPluginElement(doc);
		}

		try {
			Node versionEle = pluginEle.getElementsByTagName("version").item(0);
			Float version = Float.parseFloat(versionEle.getTextContent().trim());
			if (version < 2.9) {
				log.debug("bump maven-eclipse-plugin's version to 2.9");
				versionEle.setTextContent("2.9");
			}
		} catch (Exception e) {
			// ignore it
		}

		XPathExpression ajdtExpr = xpath
				.compile("/project/build/plugins/plugin[artifactId='maven-eclipse-plugin']/configuration/ajdtVersion");
		Element ajdtEle = (Element) ajdtExpr.evaluate(doc, XPathConstants.NODE);
		if (ajdtEle == null) {
			createOrGetChildElement(pluginEle, "configuration");
			ajdtEle = doc.createElement("ajdtVersion");
			ajdtEle.setTextContent("none");
			pluginEle.getElementsByTagName("configuration").item(0).appendChild(ajdtEle);
			log.debug("add <ajdtVersion> to <configuration>");
		} else {
			ajdtEle.setTextContent("none");
			log.debug("change value of <ajdtVersion> to none");
		}
		
		return doc;
	}

	void writeDocument(OutputStream out, Document doc) throws Exception {
		Transformer transformer = TransformerFactory.newInstance().newTransformer();
		DOMSource source = new DOMSource(doc);
		StreamResult result = new StreamResult(out);
		transformer.transform(source, result);
		out.close();
	}

	private Element createOrGetChildElement(Element parent, String childEleName) {
		NodeList children = parent.getElementsByTagName(childEleName);
		Element child;
		if (children.getLength() == 0) {
			child = parent.getOwnerDocument().createElement(childEleName);
			parent.appendChild(child);
		} else {
			child = (Element) children.item(0);
		}
		return child;
	}

	private Element createPluginElement(Document doc) {
		Element buildEle = createOrGetChildElement(doc.getDocumentElement(), "build");
		Element pluginsEle = createOrGetChildElement(buildEle, "plugins");
		Element pluginEle = createOrGetChildElement(pluginsEle, "plugin");
		Element groupId = createOrGetChildElement(pluginEle, "groupId");
		groupId.setTextContent("org.apache.maven.plugins");
		Element artifactId = createOrGetChildElement(pluginEle, "artifactId");
		artifactId.setTextContent("maven-eclipse-plugin");
		Element version = createOrGetChildElement(pluginEle, "version");
		version.setTextContent("2.9");
		return pluginEle;
	}

}
