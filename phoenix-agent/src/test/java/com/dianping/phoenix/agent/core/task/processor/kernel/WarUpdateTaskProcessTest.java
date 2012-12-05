package com.dianping.phoenix.agent.core.task.processor.kernel;

import static org.mockito.Mockito.mock;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.dianping.phoenix.agent.core.TestUtil;
import com.dianping.phoenix.agent.core.event.AbstractEventTracker;
import com.dianping.phoenix.agent.core.event.EventTracker;
import com.dianping.phoenix.agent.core.event.LifecycleEvent;
import com.dianping.phoenix.agent.core.task.processor.kernel.DeployTask;
import com.dianping.phoenix.agent.core.task.processor.kernel.DeployTaskProcessor;
import com.dianping.phoenix.agent.core.tx.FileBasedTransactionManager;
import com.dianping.phoenix.agent.core.tx.Transaction;
import com.dianping.phoenix.agent.core.tx.TransactionId;
import com.dianping.phoenix.agent.core.tx.TransactionManager;

public class WarUpdateTaskProcessTest {

	@Test
	public void testLog() throws IOException {
		DeployTaskProcessor processor = new DeployTaskProcessor();
		TransactionManager txLog = new FileBasedTransactionManager();
		TransactionId txId = TestUtil.generateTxId(1L);
		EventTracker eventTracker = new AbstractEventTracker() {

			@Override
			protected void onLifecycleEvent(LifecycleEvent event) {
				System.out.println(event.getStatus());
			}

		};
		Transaction tx = new Transaction(mock(DeployTask.class), txId, eventTracker);
		processor.submit(tx);
		char[] cbuf = new char[4096];
		txLog.getLogReader(txId, 0).read(cbuf);
		System.out.println(new String(cbuf));
		System.in.read();
	}

	public static void main(String[] args) throws Exception {
		//<Loader className="com.dianping.phoenix.bootstrap.Jboss4WebappLoader" kernelDocBase="/Users/marsqing/Projects/phoenix/misc/target/data/webapps/phoenix-kernel/" />
		File svrXml = new File("/data/server.xml");
		DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		Document doc = builder.parse(svrXml);
		NodeList ctxList = doc.getElementsByTagName("Context");
		for (int i = 0; i < ctxList.getLength(); i++) {
			Node ctx = ctxList.item(i);
			String docBase = ctx.getAttributes().getNamedItem("docBase").getNodeValue();
			if ("/data/webapps/user-service/current/".equals(docBase)) {
				
				if(ctx instanceof Element) {
					int loaderCnt = ((Element)ctx).getElementsByTagName("Loader").getLength();
					if(loaderCnt == 0) {
						Element loader = doc.createElement("Loader");
						loader.setAttribute("className", "com.dianping.phoenix.bootstrap.Jboss4WebappLoader");
						loader.setAttribute("kernelDocBase", "/data/webapps/phoenix-kernel/current/");
						ctx.appendChild(loader);
					}
				}
			}
		}

		Transformer transformer = TransformerFactory.newInstance().newTransformer();

		DOMSource source = new DOMSource(doc);
		FileOutputStream fout = new FileOutputStream(svrXml);
		StreamResult result = new StreamResult(fout);
		transformer.transform(source, result);

	}

}
