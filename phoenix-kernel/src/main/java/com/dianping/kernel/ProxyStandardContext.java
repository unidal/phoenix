package com.dianping.kernel;

import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;

import javax.management.ListenerNotFoundException;
import javax.management.MBeanNotificationInfo;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.NotificationFilter;
import javax.management.NotificationListener;
import javax.management.ObjectName;
import javax.naming.directory.DirContext;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import org.apache.AnnotationProcessor;
import org.apache.catalina.Cluster;
import org.apache.catalina.Container;
import org.apache.catalina.ContainerListener;
import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.LifecycleListener;
import org.apache.catalina.Loader;
import org.apache.catalina.Manager;
import org.apache.catalina.Pipeline;
import org.apache.catalina.Realm;
import org.apache.catalina.Valve;
import org.apache.catalina.Wrapper;
import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.Response;
import org.apache.catalina.core.NamingContextListener;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.deploy.ApplicationParameter;
import org.apache.catalina.deploy.ErrorPage;
import org.apache.catalina.deploy.FilterDef;
import org.apache.catalina.deploy.FilterMap;
import org.apache.catalina.deploy.LoginConfig;
import org.apache.catalina.deploy.MessageDestination;
import org.apache.catalina.deploy.MessageDestinationRef;
import org.apache.catalina.deploy.NamingResources;
import org.apache.catalina.deploy.SecurityConstraint;
import org.apache.catalina.util.CharsetMapper;
import org.apache.juli.logging.Log;
import org.apache.tomcat.util.http.mapper.Mapper;

import com.dianping.phoenix.Constants;

public class ProxyStandardContext extends StandardContext{
	
	private StandardContext m_standardContext;
	private ServletContext  m_servletContext;
	private StandardContext m_subContext;
	private boolean m_isFinished = false;
	
	private Log log;
	
	public ProxyStandardContext(StandardContext context,Log log ){
		this.m_standardContext = context;
		this.m_servletContext = this.m_standardContext.getServletContext();
		this.log = log;
	}
	
	public void finished(boolean isFinished){
		this.m_isFinished = isFinished;
	}
	
	private void invoke(Object... args ){
		if(this.m_isFinished){
			return;
		}
		StackTraceElement[] trace = Thread.currentThread().getStackTrace();
		StackTraceElement e = trace[2];
		String methodName = e.getMethodName();
		
		if(methodName.equals("setPublicId")||methodName.equals("addWatchedResource")){
			if(this.m_servletContext.getAttribute(Constants.PHOENIX_WEB_DESCRIPTION_DEFAULT) == null){
				this.m_subContext = new StandardContext();
				this.m_servletContext.setAttribute(Constants.PHOENIX_WEB_DESCRIPTION_DEFAULT, this.m_subContext);
			}else if(this.m_servletContext.getAttribute(Constants.PHOENIX_WEB_DESCRIPTION_APP) == null){
				this.m_subContext = new StandardContext();
				this.m_servletContext.setAttribute(Constants.PHOENIX_WEB_DESCRIPTION_APP, this.m_subContext);
			}else if(this.m_servletContext.getAttribute(Constants.PHOENIX_WEB_DESCRIPTION_KERNEL) == null){
				this.m_subContext = new StandardContext();
				this.m_servletContext.setAttribute(Constants.PHOENIX_WEB_DESCRIPTION_KERNEL, this.m_subContext);
			}else{
				return;
			}
			this.m_subContext.setDocBase(this.m_standardContext.getDocBase());
		}
		
		Method[] methods = StandardContext.class.getDeclaredMethods();
		Method method = null;
		for(Method m : methods ){
			Class<?>[]parameterTypes = null;
			if(m.getName().equals(methodName )
					&&(parameterTypes = m.getParameterTypes() ).length == args.length ){
				int i = 0;
				for(Object arg : args ){
					if(!parameterTypes[i++].isInstance(arg ) ){
						break;
					}
				}
				method = m;
				break;
			}
		}
		if(method != null ){
			if(!method.isAccessible() ){
				method.setAccessible(true);
			}
			try {
				method.invoke(this.m_subContext, args);
			} catch (Exception ex ) {
				log.warn("Error whern ProxyStandardContext invoke method:"+methodName
						+";  "+ex.getMessage(), ex);
			} 
		}
	}
	

	@Override
	public AnnotationProcessor getAnnotationProcessor() {
		return this.m_standardContext.getAnnotationProcessor();
	}


	@Override
	public void setAnnotationProcessor(AnnotationProcessor annotationProcessor) {
		invoke(annotationProcessor);
		this.m_standardContext.setAnnotationProcessor(annotationProcessor);
	}


	@Override
	public String getEncodedPath() {
		
		return this.m_standardContext.getEncodedPath();
	}


	@Override
	public void setName(String name) {
		invoke(name);
		this.m_standardContext.setName(name);
	}


	@Override
	public boolean isCachingAllowed() {
		
		return this.m_standardContext.isCachingAllowed();
	}


	@Override
	public void setCachingAllowed(boolean cachingAllowed) {
		invoke(cachingAllowed);
		this.m_standardContext.setCachingAllowed(cachingAllowed);
	}


	@Override
	public void setCaseSensitive(boolean caseSensitive) {
		invoke(caseSensitive);
		this.m_standardContext.setCaseSensitive(caseSensitive);
	}


	@Override
	public boolean isCaseSensitive() {
		
		return this.m_standardContext.isCaseSensitive();
	}


	@Override
	public void setAllowLinking(boolean allowLinking) {
		invoke(allowLinking);
		this.m_standardContext.setAllowLinking(allowLinking);
	}


	@Override
	public boolean isAllowLinking() {
		
		return this.m_standardContext.isAllowLinking();
	}


	@Override
	public void setCacheTTL(int cacheTTL) {
		invoke(cacheTTL);
		this.m_standardContext.setCacheTTL(cacheTTL);
	}


	@Override
	public int getCacheTTL() {
		
		return this.m_standardContext.getCacheTTL();
	}


	@Override
	public int getCacheMaxSize() {
		
		return this.m_standardContext.getCacheMaxSize();
	}


	@Override
	public void setCacheMaxSize(int cacheMaxSize) {
		invoke(cacheMaxSize);
		this.m_standardContext.setCacheMaxSize(cacheMaxSize);
	}


	@Override
	public int getCacheObjectMaxSize() {
		
		return this.m_standardContext.getCacheObjectMaxSize();
	}


	@Override
	public void setCacheObjectMaxSize(int cacheObjectMaxSize) {
		invoke(cacheObjectMaxSize);
		this.m_standardContext.setCacheObjectMaxSize(cacheObjectMaxSize);
	}


	@Override
	public boolean getDelegate() {
		
		return this.m_standardContext.getDelegate();
	}


	@Override
	public void setDelegate(boolean delegate) {
		invoke(delegate);
		this.m_standardContext.setDelegate(delegate);
	}


	@Override
	public boolean isUseNaming() {
		
		return this.m_standardContext.isUseNaming();
	}


	@Override
	public void setUseNaming(boolean useNaming) {
		invoke(useNaming);
		this.m_standardContext.setUseNaming(useNaming);
	}


	@Override
	public boolean isFilesystemBased() {
		
		return this.m_standardContext.isFilesystemBased();
	}


	@Override
	public Object[] getApplicationEventListeners() {
		
		return this.m_standardContext.getApplicationEventListeners();
	}


	@Override
	public void setApplicationEventListeners(Object[] listeners) {
		invoke(listeners);
		this.m_standardContext.setApplicationEventListeners(listeners);
	}


	@Override
	public Object[] getApplicationLifecycleListeners() {
		
		return this.m_standardContext.getApplicationLifecycleListeners();
	}


	@Override
	public void setApplicationLifecycleListeners(Object[] listeners) {
		invoke(listeners);
		this.m_standardContext.setApplicationLifecycleListeners(listeners);
	}


	@Override
	public boolean getAntiJARLocking() {
		
		return this.m_standardContext.getAntiJARLocking();
	}


	@Override
	public boolean getAntiResourceLocking() {
		
		return this.m_standardContext.getAntiResourceLocking();
	}


	@Override
	public void setAntiJARLocking(boolean antiJARLocking) {
		invoke(antiJARLocking);
		this.m_standardContext.setAntiJARLocking(antiJARLocking);
	}


	@Override
	public void setAntiResourceLocking(boolean antiResourceLocking) {
		invoke(antiResourceLocking);
		this.m_standardContext.setAntiResourceLocking(antiResourceLocking);
	}


	@Override
	public boolean getAvailable() {
		
		return this.m_standardContext.getAvailable();
	}


	@Override
	public void setAvailable(boolean available) {
		invoke(available);
		this.m_standardContext.setAvailable(available);
	}


	@Override
	public CharsetMapper getCharsetMapper() {
		
		return this.m_standardContext.getCharsetMapper();
	}


	@Override
	public void setCharsetMapper(CharsetMapper mapper) {
		invoke(mapper);
		this.m_standardContext.setCharsetMapper(mapper);
	}


	@Override
	public String getConfigFile() {
		
		return this.m_standardContext.getConfigFile();
	}


	@Override
	public void setConfigFile(String configFile) {
		invoke(configFile);
		this.m_standardContext.setConfigFile(configFile);
	}


	@Override
	public boolean getConfigured() {
		
		return this.m_standardContext.getConfigured();
	}


	@Override
	public void setConfigured(boolean configured) {
		invoke(configured);
		this.m_standardContext.setConfigured(configured);
	}


	@Override
	public boolean getCookies() {
		
		return this.m_standardContext.getCookies();
	}


	@Override
	public void setCookies(boolean cookies) {
		invoke(cookies);
		this.m_standardContext.setCookies(cookies);
	}


	@Override
	public boolean getUseHttpOnly() {
		
		return this.m_standardContext.getUseHttpOnly();
	}


	@Override
	public void setUseHttpOnly(boolean useHttpOnly) {
		invoke(useHttpOnly);
		this.m_standardContext.setUseHttpOnly(useHttpOnly);
	}


	@Override
	public String getSessionCookieDomain() {
		
		return this.m_standardContext.getSessionCookieDomain();
	}


	@Override
	public void setSessionCookieDomain(String sessionCookieDomain) {
		invoke(sessionCookieDomain);
		this.m_standardContext.setSessionCookieDomain(sessionCookieDomain);
	}


	@Override
	public String getSessionCookiePath() {
		
		return this.m_standardContext.getSessionCookiePath();
	}


	@Override
	public void setSessionCookiePath(String sessionCookiePath) {
		invoke(sessionCookiePath);
		this.m_standardContext.setSessionCookiePath(sessionCookiePath);
	}


	@Override
	public String getSessionCookieName() {
		
		return this.m_standardContext.getSessionCookieName();
	}


	@Override
	public void setSessionCookieName(String sessionCookieName) {
		invoke(sessionCookieName);
		this.m_standardContext.setSessionCookieName(sessionCookieName);
	}


	@Override
	public boolean getCrossContext() {
		
		return this.m_standardContext.getCrossContext();
	}


	@Override
	public void setCrossContext(boolean crossContext) {
		invoke(crossContext);
		this.m_standardContext.setCrossContext(crossContext);
	}


	@Override
	public String getDefaultContextXml() {
		
		return this.m_standardContext.getDefaultContextXml();
	}


	@Override
	public void setDefaultContextXml(String defaultContextXml) {
		invoke(defaultContextXml);
		this.m_standardContext.setDefaultContextXml(defaultContextXml);
	}


	@Override
	public String getDefaultWebXml() {
		
		return this.m_standardContext.getDefaultWebXml();
	}


	@Override
	public void setDefaultWebXml(String defaultWebXml) {
		invoke(defaultWebXml);
		this.m_standardContext.setDefaultWebXml(defaultWebXml);
	}


	@Override
	public long getStartupTime() {
		
		return this.m_standardContext.getStartupTime();
	}


	@Override
	public void setStartupTime(long startupTime) {
		invoke(startupTime);
		this.m_standardContext.setStartupTime(startupTime);
	}


	@Override
	public long getTldScanTime() {
		
		return this.m_standardContext.getTldScanTime();
	}


	@Override
	public void setTldScanTime(long tldScanTime) {
		invoke(tldScanTime);
		this.m_standardContext.setTldScanTime(tldScanTime);
	}


	@Override
	public String getDisplayName() {
		
		return this.m_standardContext.getDisplayName();
	}


	@Override
	public String getAltDDName() {
		
		return this.m_standardContext.getAltDDName();
	}


	@Override
	public void setAltDDName(String altDDName) {
		invoke(altDDName);
		this.m_standardContext.setAltDDName(altDDName);
	}


	@Override
	public String getCompilerClasspath() {
		
		return this.m_standardContext.getCompilerClasspath();
	}


	@Override
	public void setCompilerClasspath(String compilerClasspath) {
		invoke(compilerClasspath);
		this.m_standardContext.setCompilerClasspath(compilerClasspath);
	}


	@Override
	public void setDisplayName(String displayName) {
		invoke(displayName);
		this.m_standardContext.setDisplayName(displayName);
	}


	@Override
	public boolean getDistributable() {
		
		return this.m_standardContext.getDistributable();
	}


	@Override
	public void setDistributable(boolean distributable) {
		invoke(distributable);
		this.m_standardContext.setDistributable(distributable);
	}


	@Override
	public String getDocBase() {
		
		return this.m_standardContext.getDocBase();
	}


	@Override
	public void setDocBase(String docBase) {
		invoke(docBase);
		this.m_standardContext.setDocBase(docBase);
	}


	@Override
	public boolean isLazy() {
		
		return this.m_standardContext.isLazy();
	}


	@Override
	public void setLazy(boolean lazy) {
		invoke(lazy);
		this.m_standardContext.setLazy(lazy);
	}


	@Override
	public String getInfo() {
		
		return this.m_standardContext.getInfo();
	}


	@Override
	public String getEngineName() {
		
		return this.m_standardContext.getEngineName();
	}


	@Override
	public void setEngineName(String engineName) {
		invoke(engineName);
		this.m_standardContext.setEngineName(engineName);
	}


	@Override
	public String getJ2EEApplication() {
		
		return this.m_standardContext.getJ2EEApplication();
	}


	@Override
	public void setJ2EEApplication(String j2eeApplication) {
		invoke(j2eeApplication);
		this.m_standardContext.setJ2EEApplication(j2eeApplication);
	}


	@Override
	public String getJ2EEServer() {
		
		return this.m_standardContext.getJ2EEServer();
	}


	@Override
	public void setJ2EEServer(String j2eeServer) {
		invoke(j2eeServer);
		this.m_standardContext.setJ2EEServer(j2eeServer);
	}


	@Override
	public synchronized void setLoader(Loader loader) {
		invoke(loader);
		this.m_standardContext.setLoader(loader);
	}


	@Override
	public boolean getIgnoreAnnotations() {
		
		return this.m_standardContext.getIgnoreAnnotations();
	}


	@Override
	public void setIgnoreAnnotations(boolean ignoreAnnotations) {
		invoke(ignoreAnnotations);
		this.m_standardContext.setIgnoreAnnotations(ignoreAnnotations);
	}


	@Override
	public LoginConfig getLoginConfig() {
		
		return this.m_standardContext.getLoginConfig();
	}


	@Override
	public void setLoginConfig(LoginConfig config) {
		invoke(config);
		this.m_standardContext.setLoginConfig(config);
	}


	@Override
	public Mapper getMapper() {
		
		return this.m_standardContext.getMapper();
	}


	@Override
	public NamingResources getNamingResources() {
		
		return this.m_standardContext.getNamingResources();
	}


	@Override
	public void setNamingResources(NamingResources namingResources) {
		invoke(namingResources);
		this.m_standardContext.setNamingResources(namingResources);
	}


	@Override
	public String getPath() {
		
		return this.m_standardContext.getPath();
	}


	@Override
	public void setPath(String path) {
		invoke(path);
		this.m_standardContext.setPath(path);
	}


	@Override
	public String getPublicId() {
		
		return this.m_standardContext.getPublicId();
	}
	
	@Override
	public void setPublicId(String publicId) {
		invoke(publicId);
		this.m_standardContext.setPublicId(publicId);
	}


	@Override
	public boolean getReloadable() {
		
		return this.m_standardContext.getReloadable();
	}


	@Override
	public boolean getOverride() {
		
		return this.m_standardContext.getOverride();
	}


	@Override
	public String getOriginalDocBase() {
		
		return this.m_standardContext.getOriginalDocBase();
	}


	@Override
	public void setOriginalDocBase(String docBase) {
		invoke(docBase);
		this.m_standardContext.setOriginalDocBase(docBase);
	}


	@Override
	public ClassLoader getParentClassLoader() {
		
		return this.m_standardContext.getParentClassLoader();
	}


	@Override
	public boolean getPrivileged() {
		
		return this.m_standardContext.getPrivileged();
	}


	@Override
	public void setPrivileged(boolean privileged) {
		invoke(privileged);
		this.m_standardContext.setPrivileged(privileged);
	}


	@Override
	public void setReloadable(boolean reloadable) {
		invoke(reloadable);
		this.m_standardContext.setReloadable(reloadable);
	}


	@Override
	public void setOverride(boolean override) {
		invoke(override);
		this.m_standardContext.setOverride(override);
	}


	@Override
	public boolean isReplaceWelcomeFiles() {
		
		return this.m_standardContext.isReplaceWelcomeFiles();
	}


	@Override
	public void setReplaceWelcomeFiles(boolean replaceWelcomeFiles) {
		invoke(replaceWelcomeFiles);
		this.m_standardContext.setReplaceWelcomeFiles(replaceWelcomeFiles);
	}


	@Override
	public ServletContext getServletContext() {
		
		return this.m_standardContext.getServletContext();
	}


	@Override
	public int getSessionTimeout() {
		
		return this.m_standardContext.getSessionTimeout();
	}


	@Override
	public void setSessionTimeout(int timeout) {
		invoke(timeout);
		this.m_standardContext.setSessionTimeout(timeout);
	}


	@Override
	public boolean getSwallowOutput() {
		
		return this.m_standardContext.getSwallowOutput();
	}


	@Override
	public void setSwallowOutput(boolean swallowOutput) {
		invoke(swallowOutput);
		this.m_standardContext.setSwallowOutput(swallowOutput);
	}


	@Override
	public long getUnloadDelay() {
		
		return this.m_standardContext.getUnloadDelay();
	}


	@Override
	public void setUnloadDelay(long unloadDelay) {
		invoke(unloadDelay);
		this.m_standardContext.setUnloadDelay(unloadDelay);
	}


	@Override
	public boolean getUnpackWAR() {
		
		return this.m_standardContext.getUnpackWAR();
	}


	@Override
	public void setUnpackWAR(boolean unpackWAR) {
		invoke(unpackWAR);
		this.m_standardContext.setUnpackWAR(unpackWAR);
	}


	@Override
	public String getWrapperClass() {
		
		return this.m_standardContext.getWrapperClass();
	}


	@Override
	public void setWrapperClass(String wrapperClassName) {
		invoke(wrapperClassName);
		this.m_standardContext.setWrapperClass(wrapperClassName);
	}


	@Override
	public synchronized void setResources(DirContext resources) {
		invoke(resources);
		this.m_standardContext.setResources(resources);
	}


	@Override
	public String getCharsetMapperClass() {
		
		return this.m_standardContext.getCharsetMapperClass();
	}


	@Override
	public void setCharsetMapperClass(String mapper) {
		invoke(mapper);
		this.m_standardContext.setCharsetMapperClass(mapper);
	}


	@Override
	public String getWorkPath() {
		
		return this.m_standardContext.getWorkPath();
	}


	@Override
	public String getWorkDir() {
		
		return this.m_standardContext.getWorkDir();
	}


	@Override
	public void setWorkDir(String workDir) {
		invoke(workDir);
		this.m_standardContext.setWorkDir(workDir);
	}


	@Override
	public boolean isSaveConfig() {
		
		return this.m_standardContext.isSaveConfig();
	}


	@Override
	public void setSaveConfig(boolean saveConfig) {
		invoke(saveConfig);
		this.m_standardContext.setSaveConfig(saveConfig);
	}


	@Override
	public boolean getClearReferencesStopThreads() {
		
		return this.m_standardContext.getClearReferencesStopThreads();
	}


	@Override
	public void setClearReferencesStopThreads(boolean clearReferencesStopThreads) {
		invoke(clearReferencesStopThreads);
		this.m_standardContext.setClearReferencesStopThreads(clearReferencesStopThreads);
	}


	@Override
	public boolean getClearReferencesStopTimerThreads() {
		
		return this.m_standardContext.getClearReferencesStopTimerThreads();
	}


	@Override
	public void setClearReferencesStopTimerThreads(
			boolean clearReferencesStopTimerThreads) {
		invoke(clearReferencesStopTimerThreads);
		this.m_standardContext.setClearReferencesStopTimerThreads(clearReferencesStopTimerThreads);
	}


	@Override
	public boolean getClearReferencesThreadLocals() {
		
		return this.m_standardContext.getClearReferencesThreadLocals();
	}


	@Override
	public void setClearReferencesThreadLocals(
			boolean clearReferencesThreadLocals) {
		invoke(clearReferencesThreadLocals);
		this.m_standardContext.setClearReferencesThreadLocals(clearReferencesThreadLocals);
	}


	@Override
	public void addApplicationListener(String listener) {
		invoke(listener);
		this.m_standardContext.addApplicationListener(listener);
	}


	@Override
	public void addApplicationParameter(ApplicationParameter parameter) {
		invoke(parameter);
		this.m_standardContext.addApplicationParameter(parameter);
	}


	@Override
	public void addChild(Container child) {
		invoke(child);
		this.m_standardContext.addChild(child);
	}


	@Override
	public void addConstraint(SecurityConstraint constraint) {
		invoke(constraint);
		this.m_standardContext.addConstraint(constraint);
	}


	@Override
	public void addErrorPage(ErrorPage errorPage) {
		invoke(errorPage);
		this.m_standardContext.addErrorPage(errorPage);
	}


	@Override
	public void addFilterDef(FilterDef filterDef) {
		invoke(filterDef);
		this.m_standardContext.addFilterDef(filterDef);
	}


	@Override
	public void addFilterMap(FilterMap filterMap) {
		invoke(filterMap);
		this.m_standardContext.addFilterMap(filterMap);
	}


	@Override
	public void addInstanceListener(String listener) {
		invoke(listener);
		this.m_standardContext.addInstanceListener(listener);
	}


	@Override
	public void addJspMapping(String pattern) {
		invoke(pattern);
		this.m_standardContext.addJspMapping(pattern);
	}


	@Override
	public void addLocaleEncodingMappingParameter(String locale, String encoding) {
		invoke(locale, encoding);
		this.m_standardContext.addLocaleEncodingMappingParameter(locale, encoding);
	}


	@Override
	public void addMessageDestination(MessageDestination md) {
		invoke(md);
		this.m_standardContext.addMessageDestination(md);
	}


	@Override
	public void addMessageDestinationRef(MessageDestinationRef mdr) {
		invoke(mdr);
		this.m_standardContext.addMessageDestinationRef(mdr);
	}


	@Override
	public void addMimeMapping(String extension, String mimeType) {
		invoke(extension, mimeType);
		this.m_standardContext.addMimeMapping(extension, mimeType);
	}


	@Override
	public void addParameter(String name, String value) {
		invoke(name, value);
		this.m_standardContext.addParameter(name, value);
	}


	@Override
	public void addRoleMapping(String role, String link) {
		invoke(role, link);
		this.m_standardContext.addRoleMapping(role, link);
	}


	@Override
	public void addSecurityRole(String role) {
		invoke(role);
		this.m_standardContext.addSecurityRole(role);
	}


	@Override
	public void addServletMapping(String pattern, String name) {
		invoke(pattern, name);
		this.m_standardContext.addServletMapping(pattern, name);
	}


	@Override
	public void addServletMapping(String pattern, String name,
			boolean jspWildCard) {
		invoke(pattern, name, jspWildCard);
		this.m_standardContext.addServletMapping(pattern, name, jspWildCard);
	}


	@Override
	public void addTaglib(String uri, String location) {
		invoke(uri, location);
		this.m_standardContext.addTaglib(uri, location);
	}


	@Override
	public void addWatchedResource(String name) {
		invoke(name);
		this.m_standardContext.addWatchedResource(name);
	}


	@Override
	public void addWelcomeFile(String name) {
		invoke(name);
		this.m_standardContext.addWelcomeFile(name);
	}


	@Override
	public void addWrapperLifecycle(String listener) {
		invoke(listener);
		this.m_standardContext.addWrapperLifecycle(listener);
	}


	@Override
	public void addWrapperListener(String listener) {
		invoke(listener);
		this.m_standardContext.addWrapperListener(listener);
	}


	@Override
	public Wrapper createWrapper() {
		
		return this.m_standardContext.createWrapper();
	}


	@Override
	public String[] findApplicationListeners() {
		
		return this.m_standardContext.findApplicationListeners();
	}


	@Override
	public ApplicationParameter[] findApplicationParameters() {
		
		return this.m_standardContext.findApplicationParameters();
	}


	@Override
	public SecurityConstraint[] findConstraints() {
		
		return this.m_standardContext.findConstraints();
	}


	@Override
	public ErrorPage findErrorPage(int errorCode) {
		invoke(errorCode);
		return this.m_standardContext.findErrorPage(errorCode);
	}


	@Override
	public ErrorPage findErrorPage(String exceptionType) {
		invoke(exceptionType);
		return this.m_standardContext.findErrorPage(exceptionType);
	}


	@Override
	public ErrorPage[] findErrorPages() {
		
		return this.m_standardContext.findErrorPages();
	}


	@Override
	public FilterDef findFilterDef(String filterName) {
		invoke(filterName);
		return this.m_standardContext.findFilterDef(filterName);
	}


	@Override
	public FilterDef[] findFilterDefs() {
		
		return this.m_standardContext.findFilterDefs();
	}


	@Override
	public FilterMap[] findFilterMaps() {
		
		return this.m_standardContext.findFilterMaps();
	}


	@Override
	public String[] findInstanceListeners() {
		
		return this.m_standardContext.findInstanceListeners();
	}


	@Override
	public Context findMappingObject() {
		
		return this.m_standardContext.findMappingObject();
	}


	@Override
	public MessageDestination findMessageDestination(String name) {
		invoke(name);
		return this.m_standardContext.findMessageDestination(name);
	}


	@Override
	public MessageDestination[] findMessageDestinations() {
		
		return this.m_standardContext.findMessageDestinations();
	}


	@Override
	public MessageDestinationRef findMessageDestinationRef(String name) {
		invoke(name);
		return this.m_standardContext.findMessageDestinationRef(name);
	}


	@Override
	public MessageDestinationRef[] findMessageDestinationRefs() {
		
		return this.m_standardContext.findMessageDestinationRefs();
	}


	@Override
	public String findMimeMapping(String extension) {
		invoke(extension);
		return this.m_standardContext.findMimeMapping(extension);
	}


	@Override
	public String[] findMimeMappings() {
		
		return this.m_standardContext.findMimeMappings();
	}


	@Override
	public String findParameter(String name) {
		invoke(name);
		return this.m_standardContext.findParameter(name);
	}


	@Override
	public String[] findParameters() {
		
		return this.m_standardContext.findParameters();
	}


	@Override
	public String findRoleMapping(String role) {
		invoke(role);
		return this.m_standardContext.findRoleMapping(role);
	}


	@Override
	public boolean findSecurityRole(String role) {
		invoke(role);
		return this.m_standardContext.findSecurityRole(role);
	}


	@Override
	public String[] findSecurityRoles() {
		
		return this.m_standardContext.findSecurityRoles();
	}


	@Override
	public String findServletMapping(String pattern) {
		invoke(pattern);
		return this.m_standardContext.findServletMapping(pattern);
	}


	@Override
	public String[] findServletMappings() {
		
		return this.m_standardContext.findServletMappings();
	}


	@Override
	public String findStatusPage(int status) {
		invoke(status);
		return this.m_standardContext.findStatusPage(status);
	}


	@Override
	public int[] findStatusPages() {
		
		return this.m_standardContext.findStatusPages();
	}


	@Override
	public String findTaglib(String uri) {
		invoke(uri);
		return this.m_standardContext.findTaglib(uri);
	}


	@Override
	public String[] findTaglibs() {
		return this.m_standardContext.findTaglibs();
	}


	@Override
	public boolean findWelcomeFile(String name) {
		invoke(name);
		return this.m_standardContext.findWelcomeFile(name);
	}


	@Override
	public String[] findWatchedResources() {
		
		return this.m_standardContext.findWatchedResources();
	}


	@Override
	public String[] findWelcomeFiles() {
		
		return this.m_standardContext.findWelcomeFiles();
	}


	@Override
	public String[] findWrapperLifecycles() {
		
		return this.m_standardContext.findWrapperLifecycles();
	}


	@Override
	public String[] findWrapperListeners() {
		
		return this.m_standardContext.findWrapperListeners();
	}


	@Override
	public synchronized void reload() {
		
		this.m_standardContext.reload();
	}


	@Override
	public void removeApplicationListener(String listener) {
		invoke(listener);
		this.m_standardContext.removeApplicationListener(listener);
	}


	@Override
	public void removeApplicationParameter(String name) {
		invoke(name);
		this.m_standardContext.removeApplicationParameter(name);
	}


	@Override
	public void removeChild(Container child) {
		invoke(name);
		this.m_standardContext.removeChild(child);
	}


	@Override
	public void removeConstraint(SecurityConstraint constraint) {
		invoke(name);
		this.m_standardContext.removeConstraint(constraint);
	}


	@Override
	public void removeErrorPage(ErrorPage errorPage) {
		invoke(errorPage);
		this.m_standardContext.removeErrorPage(errorPage);
	}


	@Override
	public void removeFilterDef(FilterDef filterDef) {
		invoke(filterDef);
		this.m_standardContext.removeFilterDef(filterDef);
	}


	@Override
	public void removeFilterMap(FilterMap filterMap) {
		invoke(filterMap);
		this.m_standardContext.removeFilterMap(filterMap);
	}


	@Override
	public void removeInstanceListener(String listener) {
		invoke(listener);
		
		this.m_standardContext.removeInstanceListener(listener);
	}


	@Override
	public void removeMessageDestination(String name) {
		invoke(name);
		
		this.m_standardContext.removeMessageDestination(name);
	}


	@Override
	public void removeMessageDestinationRef(String name) {
		invoke(name);
		
		this.m_standardContext.removeMessageDestinationRef(name);
	}


	@Override
	public void removeMimeMapping(String extension) {
		invoke(extension);
		
		this.m_standardContext.removeMimeMapping(extension);
	}


	@Override
	public void removeParameter(String name) {
		invoke(name);
		
		this.m_standardContext.removeParameter(name);
	}


	@Override
	public void removeRoleMapping(String role) {
		invoke(role);
		
		this.m_standardContext.removeRoleMapping(role);
	}


	@Override
	public void removeSecurityRole(String role) {
		invoke(role);
		
		this.m_standardContext.removeSecurityRole(role);
	}


	@Override
	public void removeServletMapping(String pattern) {
		invoke(pattern);
		
		this.m_standardContext.removeServletMapping(pattern);
	}


	@Override
	public void removeTaglib(String uri) {
		invoke(uri);
		
		this.m_standardContext.removeTaglib(uri);
	}


	@Override
	public void removeWatchedResource(String name) {
		invoke(name);
		
		this.m_standardContext.removeWatchedResource(name);
	}


	@Override
	public void removeWelcomeFile(String name) {
		invoke(name);
		
		this.m_standardContext.removeWelcomeFile(name);
	}


	@Override
	public void removeWrapperLifecycle(String listener) {
		invoke(listener);
		
		this.m_standardContext.removeWrapperLifecycle(listener);
	}


	@Override
	public void removeWrapperListener(String listener) {
		invoke(listener);
		
		this.m_standardContext.removeWrapperListener(listener);
	}


	@Override
	public long getProcessingTime() {
		
		return this.m_standardContext.getProcessingTime();
	}


	@Override
	public boolean filterStart() {
		
		return this.m_standardContext.filterStart();
	}


	@Override
	public boolean filterStop() {
		
		return this.m_standardContext.filterStop();
	}


	@Override
	public FilterConfig findFilterConfig(String name) {
		invoke(name);
		
		return this.m_standardContext.findFilterConfig(name);
	}


	@Override
	public boolean listenerStart() {
		
		return this.m_standardContext.listenerStart();
	}


	@Override
	public boolean listenerStop() {
		
		return this.m_standardContext.listenerStop();
	}


	@Override
	public boolean resourcesStart() {
		
		return this.m_standardContext.resourcesStart();
	}


	@Override
	public boolean resourcesStop() {
		
		return this.m_standardContext.resourcesStop();
	}


	@Override
	public void loadOnStartup(Container[] children) {
		invoke(children);
		
		this.m_standardContext.loadOnStartup(children);
	}


	@Override
	public synchronized void start() throws LifecycleException {
		
		this.m_standardContext.start();
	}


	@Override
	public synchronized void stop() throws LifecycleException {
		
		this.m_standardContext.stop();
	}


	@Override
	public void destroy() throws Exception {
		
		this.m_standardContext.destroy();
	}


	@Override
	public String toString() {
		
		return this.m_standardContext.toString();
	}


	@Override
	public File getConfigBase() {
		
		return this.m_standardContext.getConfigBase();
	}


	@Override
	public NamingContextListener getNamingContextListener() {
		
		return this.m_standardContext.getNamingContextListener();
	}


	@Override
	public void setNamingContextListener(
			NamingContextListener namingContextListener) {
		invoke(namingContextListener);
		
		this.m_standardContext.setNamingContextListener(namingContextListener);
	}


	@Override
	public boolean getPaused() {
		
		return this.m_standardContext.getPaused();
	}


	@Override
	public String getHostname() {
		
		return this.m_standardContext.getHostname();
	}


	@Override
	public String getDeploymentDescriptor() {
		
		return this.m_standardContext.getDeploymentDescriptor();
	}


	@Override
	public String[] getServlets() {
		
		return this.m_standardContext.getServlets();
	}


	@Override
	public ObjectName createObjectName(String hostDomain, ObjectName parentName )
			throws MalformedObjectNameException {
		
		return this.m_standardContext.createObjectName(hostDomain, parentName);
	}


	@Override
	public ObjectName preRegister(MBeanServer server, ObjectName name )
			throws Exception {
		
		return this.m_standardContext.preRegister(server, name);
	}


	@Override
	public void preDeregister() throws Exception {
		
		this.m_standardContext.preDeregister();
	}


	@Override
	public void init() throws Exception {
		
		this.m_standardContext.init();
	}


	@Override
	public ObjectName getParentName() throws MalformedObjectNameException {
		
		return this.m_standardContext.getParentName();
	}


	@Override
	public void create() throws Exception {
		
		this.m_standardContext.create();
	}


	@Override
	public void removeNotificationListener(NotificationListener listener,
			NotificationFilter filter, Object object )
			throws ListenerNotFoundException {
		
		this.m_standardContext.removeNotificationListener(listener, filter, object);
	}


	@Override
	public MBeanNotificationInfo[] getNotificationInfo() {
		
		return this.m_standardContext.getNotificationInfo();
	}


	@Override
	public void addNotificationListener(NotificationListener listener,
			NotificationFilter filter, Object object )
			throws IllegalArgumentException {
		
		this.m_standardContext.addNotificationListener(listener, filter, object);
	}


	@Override
	public void removeNotificationListener(NotificationListener listener )
			throws ListenerNotFoundException {
		
		this.m_standardContext.removeNotificationListener(listener);
	}


	@Override
	public DirContext getStaticResources() {
		
		return this.m_standardContext.getStaticResources();
	}


	@Override
	public DirContext findStaticResources() {
		
		return this.m_standardContext.findStaticResources();
	}


	@Override
	public String[] getWelcomeFiles() {
		
		return this.m_standardContext.getWelcomeFiles();
	}


	@Override
	public void setXmlValidation(boolean webXmlValidation) {
		invoke(webXmlValidation);
		
		this.m_standardContext.setXmlValidation(webXmlValidation);
	}


	@Override
	public boolean getXmlValidation() {
		
		return this.m_standardContext.getXmlValidation();
	}


	@Override
	public boolean getXmlNamespaceAware() {
		
		return this.m_standardContext.getXmlNamespaceAware();
	}


	@Override
	public void setXmlNamespaceAware(boolean webXmlNamespaceAware) {
		invoke(webXmlNamespaceAware);
		
		this.m_standardContext.setXmlNamespaceAware(webXmlNamespaceAware);
	}


	@Override
	public void setTldValidation(boolean tldValidation) {
		invoke(tldValidation);
		
		this.m_standardContext.setTldValidation(tldValidation);
	}


	@Override
	public boolean getTldValidation() {
		
		return this.m_standardContext.getTldValidation();
	}


	@Override
	public void setProcessTlds(boolean newProcessTlds) {
		invoke(newProcessTlds);
		
		this.m_standardContext.setProcessTlds(newProcessTlds);
	}


	@Override
	public boolean getProcessTlds() {
		
		return this.m_standardContext.getProcessTlds();
	}


	@Override
	public boolean getTldNamespaceAware() {
		
		return this.m_standardContext.getTldNamespaceAware();
	}


	@Override
	public void setTldNamespaceAware(boolean tldNamespaceAware) {
		invoke(tldNamespaceAware);
		
		this.m_standardContext.setTldNamespaceAware(tldNamespaceAware);
	}


	@Override
	public boolean isStateManageable() {
		
		return this.m_standardContext.isStateManageable();
	}


	@Override
	public void startRecursive() throws LifecycleException {
		
		this.m_standardContext.startRecursive();
	}


	@Override
	public int getState() {
		
		return this.m_standardContext.getState();
	}


	@Override
	public String getServer() {
		
		return this.m_standardContext.getServer();
	}


	@Override
	public String setServer(String server) {
		invoke(server);
		
		return this.m_standardContext.setServer(server);
	}


	@Override
	public String[] getJavaVMs() {
		
		return this.m_standardContext.getJavaVMs();
	}


	@Override
	public String[] setJavaVMs(String[] javaVMs) {
		invoke(javaVMs);
		
		return this.m_standardContext.setJavaVMs(javaVMs);
	}


	@Override
	public long getStartTime() {
		
		return this.m_standardContext.getStartTime();
	}


	@Override
	public boolean isEventProvider() {
		
		return this.m_standardContext.isEventProvider();
	}


	@Override
	public boolean isStatisticsProvider() {
		
		return this.m_standardContext.isStatisticsProvider();
	}


	@Override
	public int getBackgroundProcessorDelay() {
		
		return this.m_standardContext.getBackgroundProcessorDelay();
	}


	@Override
	public void setBackgroundProcessorDelay(int delay) {
		invoke(delay);
		
		this.m_standardContext.setBackgroundProcessorDelay(delay);
	}


	@Override
	public Loader getLoader() {
		
		return this.m_standardContext.getLoader();
	}


	@Override
	public Log getLogger() {
		
		return this.m_standardContext.getLogger();
	}


	@Override
	public Manager getManager() {
		
		return this.m_standardContext.getManager();
	}


	@Override
	public synchronized void setManager(Manager manager) {
		invoke(manager);
		
		this.m_standardContext.setManager(manager);
	}


	@Override
	public Object getMappingObject() {
		
		return this.m_standardContext.getMappingObject();
	}


	@Override
	public Cluster getCluster() {
		
		return this.m_standardContext.getCluster();
	}


	@Override
	public synchronized void setCluster(Cluster cluster) {
		invoke(cluster);
		
		this.m_standardContext.setCluster(cluster);
	}


	@Override
	public String getName() {
		
		return this.m_standardContext.getName();
	}


	@Override
	public boolean getStartChildren() {
		
		return this.m_standardContext.getStartChildren();
	}


	@Override
	public void setStartChildren(boolean startChildren) {
		invoke(startChildren);
		
		this.m_standardContext.setStartChildren(startChildren);
	}


	@Override
	public Container getParent() {
		
		return this.m_standardContext.getParent();
	}


	@Override
	public void setParent(Container container) {
		invoke(container);
		
		this.m_standardContext.setParent(container);
	}


	@Override
	public void setParentClassLoader(ClassLoader parent) {
		invoke(parent);
		
		this.m_standardContext.setParentClassLoader(parent);
	}


	@Override
	public Pipeline getPipeline() {
		
		return this.m_standardContext.getPipeline();
	}


	@Override
	public Realm getRealm() {
		
		return this.m_standardContext.getRealm();
	}


	@Override
	public synchronized void setRealm(Realm realm) {
		invoke(realm);
		
		this.m_standardContext.setRealm(realm);
	}


	@Override
	public DirContext getResources() {
		
		return this.m_standardContext.getResources();
	}


	@Override
	public void addContainerListener(ContainerListener listener) {
		invoke(listener);
		
		this.m_standardContext.addContainerListener(listener);
	}


	@Override
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		invoke(listener);
		
		this.m_standardContext.addPropertyChangeListener(listener);
	}


	@Override
	public Container findChild(String name) {
		invoke(name);
		
		return this.m_standardContext.findChild(name);
	}


	@Override
	public Container[] findChildren() {
		
		return this.m_standardContext.findChildren();
	}


	@Override
	public ContainerListener[] findContainerListeners() {
		
		return this.m_standardContext.findContainerListeners();
	}


	@Override
	public void invoke(Request request, Response response ) throws IOException,
			ServletException {
		
		this.m_standardContext.invoke(request, response);
	}


	@Override
	public void removeContainerListener(ContainerListener listener) {
		invoke(listener);
		
		this.m_standardContext.removeContainerListener(listener);
	}


	@Override
	public void removePropertyChangeListener(PropertyChangeListener listener) {
		invoke(listener);
		
		this.m_standardContext.removePropertyChangeListener(listener);
	}


	@Override
	public void addLifecycleListener(LifecycleListener listener) {
		invoke(listener);
		
		this.m_standardContext.addLifecycleListener(listener);
	}


	@Override
	public LifecycleListener[] findLifecycleListeners() {
		
		return this.m_standardContext.findLifecycleListeners();
	}


	@Override
	public void removeLifecycleListener(LifecycleListener listener) {
		invoke(listener);
		
		this.m_standardContext.removeLifecycleListener(listener);
	}


	@Override
	public synchronized void addValve(Valve valve) {
		invoke(valve);
		
		this.m_standardContext.addValve(valve);
	}


	@Override
	public ObjectName[] getValveObjectNames() {
		
		return this.m_standardContext.getValveObjectNames();
	}


	@Override
	public Valve getBasic() {
		
		return this.m_standardContext.getBasic();
	}


	@Override
	public Valve getFirst() {
		
		return this.m_standardContext.getFirst();
	}


	@Override
	public Valve[] getValves() {
		
		return this.m_standardContext.getValves();
	}


	@Override
	public synchronized void removeValve(Valve valve) {
		invoke(valve);
		
		this.m_standardContext.removeValve(valve);
	}


	@Override
	public void setBasic(Valve valve) {
		invoke(valve);
		
		this.m_standardContext.setBasic(valve);
	}


	@Override
	public void backgroundProcess() {
		
		this.m_standardContext.backgroundProcess();
	}


	@Override
	public void fireContainerEvent(String type, Object data) {
		invoke(type, data);
		
		this.m_standardContext.fireContainerEvent(type, data);
	}


	@Override
	public ObjectName getJmxName() {
		
		return this.m_standardContext.getJmxName();
	}


	@Override
	public String getObjectName() {
		
		return this.m_standardContext.getObjectName();
	}


	@Override
	public String getDomain() {
		
		return this.m_standardContext.getDomain();
	}


	@Override
	public void setDomain(String domain) {
		invoke(domain);
		
		this.m_standardContext.setDomain(domain);
	}


	@Override
	public String getType() {
		
		return this.m_standardContext.getType();
	}


	@Override
	public void postRegister(Boolean registrationDone) {
		invoke(registrationDone);
		
		this.m_standardContext.postRegister(registrationDone);
	}


	@Override
	public void postDeregister() {
		
		this.m_standardContext.postDeregister();
	}


	@Override
	public ObjectName[] getChildren() {
		
		return this.m_standardContext.getChildren();
	}


	@Override
	public String getContainerSuffix() {
		
		return this.m_standardContext.getContainerSuffix();
	}

}
