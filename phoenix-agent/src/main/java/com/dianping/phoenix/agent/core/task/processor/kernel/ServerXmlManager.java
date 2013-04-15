package com.dianping.phoenix.agent.core.task.processor.kernel;

import java.io.File;
import java.io.FileOutputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class ServerXmlManager {

	private final static Logger logger = Logger.getLogger(ServerXmlManager.class);

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

	private String trimAndRemoveTailingSlash(String docBase) {
		docBase = docBase.trim();
		while (docBase.endsWith("/") || docBase.endsWith("\\")) {
			docBase = docBase.substring(0, docBase.length() - 1);
		}
		return docBase;
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
	public void attachPhoenixContextLoader(File serverXml, String docBasePattern, String loaderClass,
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

	/**
	 * Remove phoenix &lt;Loader&gt; to matched &lt;Context&gt;
	 * 
	 * @param serverXml
	 * @param docBasePattern
	 * @throws Exception
	 */
	public void detachPhoenixContextLoader(File serverXml, String docBasePattern) throws Exception {
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
