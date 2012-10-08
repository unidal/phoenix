package com.dianping.kernel;

import java.io.File;
import java.io.FileInputStream;

import org.apache.catalina.core.StandardContext;
import org.apache.catalina.startup.ContextConfig;
import org.apache.catalina.startup.WebRuleSet;
import org.apache.tomcat.util.digester.Digester;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXParseException;

import com.dianping.phoenix.bootstrap.Tomcat6WebappLoader;

public class Tomcat6WebappRegistry {
	public void register(Tomcat6WebappLoader loader) throws Exception {
		WebRuleSet webRuleSet = loader.getFieldValue(ContextConfig.class, "webRuleSet", null);
		File webXml = new File(loader.getKernelWarRoot(), "WEB-INF/web.xml");
		InputSource source = new InputSource(new FileInputStream(webXml));
		StandardContext ctx = (StandardContext) loader.getContainer();
		Digester digester = ContextConfig.createWebXmlDigester(ctx.getXmlNamespaceAware(), ctx.getXmlValidation());

		ctx.setReplaceWelcomeFiles(true);
		digester.setClassLoader(loader.getWebappClassLoader());
		digester.setUseContextClassLoader(false);
		digester.push(ctx);
		digester.setErrorHandler(new ContextErrorHandler());

		try {
			digester.parse(source);
		} catch (Exception e) {
			throw new RuntimeException(String.format("Error when parsing %s!", webXml), e);
		} finally {
			webRuleSet.recycle();
			digester.reset();
			source.getByteStream().close();
		}
	}

	protected static class ContextErrorHandler implements ErrorHandler {
		public void error(SAXParseException exception) {
			exception.printStackTrace();
		}

		public void fatalError(SAXParseException exception) {
			exception.printStackTrace();
		}

		public void warning(SAXParseException exception) {
			exception.printStackTrace();
		}
	}
}
