package com.dianping.maven.plugin.tools.remedy;

import java.io.File;
import java.io.FileOutputStream;
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
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class PomRemedy {

	public static void main(String[] args) throws Exception {
		args = new String[] { "/Users/marsqing/Projects/tmp/phoenix-maven-tmp" };
		if (args.length != 1) {
			System.out.println("usage: dir");
			return;
		}

		final File dirToScan = new File(args[0]);
		System.out.println(String.format("scanning %s for all pom.xml under top 2 level directory",
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
			System.out.println("scanning " + pom.getAbsolutePath());
			remedy(pom);
		}
	}

	public static void remedy(File pom) throws Exception {
		DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		Document doc = builder.parse(pom);

		XPath xpath = XPathFactory.newInstance().newXPath();
		XPathExpression pluginExpr = xpath.compile("//plugin[artifactId='maven-eclipse-plugin']");
		Element pluginEle = (Element) pluginExpr.evaluate(doc, XPathConstants.NODE);
		if (pluginEle != null) {
			try {
				Node versionEle = pluginEle.getElementsByTagName("version").item(0);
				Float version = Float.parseFloat(versionEle.getTextContent().trim());
				if (version < 2.9) {
					System.out.println("bump maven-eclipse-plugin's version to 2.9");
					versionEle.setTextContent("2.9");
				}
			} catch (Exception e) {
				// ignore it
			}

			XPathExpression ajdtExpr = xpath
					.compile("//plugin[artifactId='maven-eclipse-plugin']/configuration/ajdtVersion");
			Element ajdtEle = (Element) ajdtExpr.evaluate(doc, XPathConstants.NODE);
			if (ajdtEle == null) {
				if (pluginEle.getElementsByTagName("configuration").getLength() == 0) {
					pluginEle.appendChild(doc.createElement("configuration"));
				}
				ajdtEle = doc.createElement("ajdtVersion");
				ajdtEle.setTextContent("none");
				pluginEle.getElementsByTagName("configuration").item(0).appendChild(ajdtEle);
				System.out.println("add <ajdtVersion> to <configuration>");
			} else {
				ajdtEle.setTextContent("none");
				System.out.println("change value of <ajdtVersion> to none");
			}
		}

		Transformer transformer = TransformerFactory.newInstance().newTransformer();
		DOMSource source = new DOMSource(doc);
		FileOutputStream fout = new FileOutputStream(pom);
		StreamResult result = new StreamResult(fout);
		transformer.transform(source, result);
	}

}
