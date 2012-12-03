package com.dianping.kernel.plugin;

import javax.servlet.UnavailableException;

import org.mortbay.jetty.webapp.WebXmlConfiguration;
import org.mortbay.xml.XmlParser.Node;

public class Jetty6WebXmlConfiguration extends WebXmlConfiguration{
	
	
	private WebXmlConfiguration containerConfig;
	private WebXmlConfiguration appConfig;
	private WebXmlConfiguration kernelConfig;
	
	public Jetty6WebXmlConfiguration(){
		
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -7745292974617224889L;


	@Override
	protected void initWebXmlElement(String element, Node node)
			throws Exception {
		// TODO Auto-generated method stub
		super.initWebXmlElement(element, node);
	}

	@Override
	protected void initContextParam(Node node) {
		// TODO Auto-generated method stub
		super.initContextParam(node);
	}

	@Override
	protected void initFilter(Node node) {
		// TODO Auto-generated method stub
		super.initFilter(node);
	}

	@Override
	protected void initFilterMapping(Node node) {
		// TODO Auto-generated method stub
		super.initFilterMapping(node);
	}

	@Override
	protected void initServlet(Node node) {
		// TODO Auto-generated method stub
		super.initServlet(node);
	}

	@Override
	protected void initServletMapping(Node node) {
		// TODO Auto-generated method stub
		super.initServletMapping(node);
	}

	@Override
	protected void initListener(Node node) {
		// TODO Auto-generated method stub
		super.initListener(node);
	}


	@Override
	protected void initWelcomeFileList(Node node) {
		// TODO Auto-generated method stub
		super.initWelcomeFileList(node);
	}


	@Override
	protected void initialize(Node config) throws ClassNotFoundException,
			UnavailableException {
		// TODO Auto-generated method stub
		super.initialize(config);
	}


}
