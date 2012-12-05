package com.dianping.phoenix.agent.core.task.processor.kernel;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class ServerXmlUtil {

	public static void attachPhoenixContextLoader(File serverXml, String docBasePattern, String loaderClass,
			File kernelDocBase) throws Exception {
		DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		Document doc = builder.parse(serverXml);
		NodeList ctxList = doc.getElementsByTagName("Context");
		for (int i = 0; i < ctxList.getLength(); i++) {
			Element ctx = (Element) ctxList.item(i);
			String docBase = ctx.getAttribute("docBase");
			if (docBase.indexOf(docBasePattern) >= 0) {
				if (ctx instanceof Element) {
					int loaderCnt = ((Element) ctx).getElementsByTagName("Loader").getLength();
					if (loaderCnt == 0) {
						Element loader = doc.createElement("Loader");
						loader.setAttribute("className", loaderClass);
						loader.setAttribute("kernelDocBase", kernelDocBase.getAbsolutePath());
						ctx.appendChild(loader);
					} else {
						return;
					}
				}
			}
		}

		writeDocument(serverXml, doc);
	}

	public static void detachPhoenixContextLoader(File serverXml, String docBasePattern)
			throws Exception {
		DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		Document doc = builder.parse(serverXml);
		NodeList ctxList = doc.getElementsByTagName("Context");
		for (int i = 0; i < ctxList.getLength(); i++) {
			Element ctx = (Element) ctxList.item(i);
			String docBase = ctx.getAttribute("docBase");
			if (docBase.indexOf(docBasePattern) >= 0) {
				if (ctx instanceof Element) {
					NodeList loaderList = ((Element) ctx).getElementsByTagName("Loader");
					for (int j = 0; j < loaderList.getLength(); j++) {
						ctx.removeChild(loaderList.item(j));
					}
				}
			}
		}

		writeDocument(serverXml, doc);
	}

	private static void writeDocument(File serverXml, Document doc) throws TransformerConfigurationException,
			TransformerFactoryConfigurationError, FileNotFoundException, TransformerException {
		Transformer transformer = TransformerFactory.newInstance().newTransformer();
		DOMSource source = new DOMSource(doc);
		FileOutputStream fout = new FileOutputStream(serverXml);
		StreamResult result = new StreamResult(fout);
		transformer.transform(source, result);
	}

}
