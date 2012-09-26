package com.dianping.kernel;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.apache.catalina.LifecycleListener;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.loader.WebappLoader;
import org.apache.catalina.startup.ContextConfig;
import org.apache.catalina.startup.WebRuleSet;
import org.apache.tomcat.util.digester.Digester;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXParseException;

import com.dianping.phoenix.bootstrap.Tomcat6WebappLoader;


/**
 * @author bin.miao
 *
 */
public class Tomcat6WebappRegister {
	
	private boolean xmlNamespaceAware = false;
	private boolean xmlValidation = false;
	private static Digester webDigester = null;
	private Tomcat6WebappLoader loader;
	
	/**
	 * @param loader
	 * @throws Exception 
	 */
	public void start(Tomcat6WebappLoader loader,File webXml) throws Exception{
		this.loader = loader;
		//Get StandardContext
		StandardContext context = (StandardContext)loader.getContainer();
		LifecycleListener[] listeners = context.findLifecycleListeners();
		ContextConfig config = null;
		//Get ContextConfig
		for(LifecycleListener listener : listeners){
			if(listener instanceof ContextConfig){
				config = (ContextConfig)listener;
			}
		}
		if(config == null){
			throw new TomcatRegisterException("can't get ContextConfig from Context");
		}
		//Reflect webRuleSet
		Field field = ContextConfig.class.getDeclaredField("webRuleSet");
		field.setAccessible(true);
		WebRuleSet webRuleSet = (WebRuleSet)field.get(null);
		Method recycle = WebRuleSet.class.getMethod("recycle", new Class[0]);
		//Create WebXml Source and Stream
		InputStream stream = new FileInputStream(webXml);
		InputSource source = new InputSource(webXml.toURI().toURL().toExternalForm());
		xmlNamespaceAware = context.getXmlNamespaceAware();
		xmlValidation = context.getXmlValidation();
		webDigester = config.createWebXmlDigester(xmlNamespaceAware, xmlValidation);
		
		source.setByteStream(stream);
        // Parse WebXml
        if (context instanceof StandardContext)
            ((StandardContext) context).setReplaceWelcomeFiles(true);
        webDigester.setClassLoader(this.getClass().getClassLoader());
        webDigester.setUseContextClassLoader(false);
        webDigester.push(context);
        webDigester.setErrorHandler(new ContextErrorHandler());
        try {
			webDigester.parse(source);
		} catch (Exception e) {
			throw new TomcatRegisterException("webxml parse error\r\n"+e.getMessage(),e);
		} finally {
			recycle.invoke(webRuleSet, new Object[0]);
			webDigester.reset();
			stream.close();
		}
	}
	
	protected class ContextErrorHandler
		implements ErrorHandler {

		public void error(SAXParseException exception) {
		}
		
		public void fatalError(SAXParseException exception) {
		}
		
		public void warning(SAXParseException exception) {
		}
	}

}
