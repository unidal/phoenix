package com.dianping.phoenix.bootstrap;

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
import org.apache.tomcat.util.http.mapper.Mapper;

public class Jboss4StandardContext extends StandardContext {
	private static final long serialVersionUID = 1L;

	private StandardContext m_standardContext;

	private ServletContext m_servletContext;

	private StandardContext m_subContext;

	private boolean m_isFinished;

	public Jboss4StandardContext(StandardContext context) {
		m_standardContext = context;
		m_servletContext = m_standardContext.getServletContext();
	}

	@Override
	public void addApplicationListener(String listener) {
		dualInvoke(listener);
		m_standardContext.addApplicationListener(listener);
	}

	@Override
	public void addApplicationParameter(ApplicationParameter parameter) {
		dualInvoke(parameter);
		m_standardContext.addApplicationParameter(parameter);
	}

	@Override
	public void addChild(Container child) {
		dualInvoke(child);
		m_standardContext.addChild(child);
	}

	@Override
	public void addConstraint(SecurityConstraint constraint) {
		dualInvoke(constraint);
		m_standardContext.addConstraint(constraint);
	}

	@Override
	public void addContainerListener(ContainerListener listener) {
		dualInvoke(listener);

		m_standardContext.addContainerListener(listener);
	}

	@Override
	public void addErrorPage(ErrorPage errorPage) {
		dualInvoke(errorPage);
		m_standardContext.addErrorPage(errorPage);
	}

	@Override
	public void addFilterDef(FilterDef filterDef) {
		dualInvoke(filterDef);
		m_standardContext.addFilterDef(filterDef);
	}

	@Override
	public void addFilterMap(FilterMap filterMap) {
		dualInvoke(filterMap);
		m_standardContext.addFilterMap(filterMap);
	}

	@Override
	public void addInstanceListener(String listener) {
		dualInvoke(listener);
		m_standardContext.addInstanceListener(listener);
	}

	@Override
	public void addJspMapping(String pattern) {
		dualInvoke(pattern);
		m_standardContext.addJspMapping(pattern);
	}

	@Override
	public void addLifecycleListener(LifecycleListener listener) {
		dualInvoke(listener);

		m_standardContext.addLifecycleListener(listener);
	}

	@Override
	public void addLocaleEncodingMappingParameter(String locale, String encoding) {
		dualInvoke(locale, encoding);
		m_standardContext.addLocaleEncodingMappingParameter(locale, encoding);
	}

	@Override
	public void addMessageDestination(MessageDestination md) {
		dualInvoke(md);
		m_standardContext.addMessageDestination(md);
	}

	@Override
	public void addMessageDestinationRef(MessageDestinationRef mdr) {
		dualInvoke(mdr);
		m_standardContext.addMessageDestinationRef(mdr);
	}

	@Override
	public void addMimeMapping(String extension, String mimeType) {
		dualInvoke(extension, mimeType);
		m_standardContext.addMimeMapping(extension, mimeType);
	}

	@Override
	public void addNotificationListener(NotificationListener listener, NotificationFilter filter, Object object)
	      throws IllegalArgumentException {

		m_standardContext.addNotificationListener(listener, filter, object);
	}

	@Override
	public void addParameter(String name, String value) {
		dualInvoke(name, value);
		m_standardContext.addParameter(name, value);
	}

	@Override
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		dualInvoke(listener);

		m_standardContext.addPropertyChangeListener(listener);
	}

	@Override
	public void addRoleMapping(String role, String link) {
		dualInvoke(role, link);
		m_standardContext.addRoleMapping(role, link);
	}

	@Override
	public void addSecurityRole(String role) {
		dualInvoke(role);
		m_standardContext.addSecurityRole(role);
	}

	@Override
	public void addServletMapping(String pattern, String name) {
		dualInvoke(pattern, name);
		m_standardContext.addServletMapping(pattern, name);
	}

	@Override
	public void addServletMapping(String pattern, String name, boolean jspWildCard) {
		dualInvoke(pattern, name, jspWildCard);
		m_standardContext.addServletMapping(pattern, name, jspWildCard);
	}

	@Override
	public void addTaglib(String uri, String location) {
		dualInvoke(uri, location);
		m_standardContext.addTaglib(uri, location);
	}

	@Override
	public synchronized void addValve(Valve valve) {
		dualInvoke(valve);

		m_standardContext.addValve(valve);
	}

	@Override
	public void addWatchedResource(String name) {
		dualInvoke(name);
		m_standardContext.addWatchedResource(name);
	}

	@Override
	public void addWelcomeFile(String name) {
		dualInvoke(name);
		m_standardContext.addWelcomeFile(name);
	}

	@Override
	public void addWrapperLifecycle(String listener) {
		dualInvoke(listener);
		m_standardContext.addWrapperLifecycle(listener);
	}

	@Override
	public void addWrapperListener(String listener) {
		dualInvoke(listener);
		m_standardContext.addWrapperListener(listener);
	}

	@Override
	public void backgroundProcess() {

		m_standardContext.backgroundProcess();
	}

	@Override
	public void create() throws Exception {

		m_standardContext.create();
	}

	@Override
	public ObjectName createObjectName(String hostDomain, ObjectName parentName) throws MalformedObjectNameException {

		return m_standardContext.createObjectName(hostDomain, parentName);
	}

	@Override
	public Wrapper createWrapper() {

		return m_standardContext.createWrapper();
	}

	@Override
	public void destroy() throws Exception {

		m_standardContext.destroy();
	}

	@Override
	public boolean filterStart() {

		return m_standardContext.filterStart();
	}

	@Override
	public boolean filterStop() {

		return m_standardContext.filterStop();
	}

	@Override
	public String[] findApplicationListeners() {

		return m_standardContext.findApplicationListeners();
	}

	@Override
	public ApplicationParameter[] findApplicationParameters() {

		return m_standardContext.findApplicationParameters();
	}

	@Override
	public Container findChild(String name) {
		dualInvoke(name);

		return m_standardContext.findChild(name);
	}

	@Override
	public Container[] findChildren() {

		return m_standardContext.findChildren();
	}

	@Override
	public SecurityConstraint[] findConstraints() {

		return m_standardContext.findConstraints();
	}

	@Override
	public ContainerListener[] findContainerListeners() {

		return m_standardContext.findContainerListeners();
	}

	@Override
	public ErrorPage findErrorPage(int errorCode) {
		dualInvoke(errorCode);
		return m_standardContext.findErrorPage(errorCode);
	}

	@Override
	public ErrorPage findErrorPage(String exceptionType) {
		dualInvoke(exceptionType);
		return m_standardContext.findErrorPage(exceptionType);
	}

	@Override
	public ErrorPage[] findErrorPages() {

		return m_standardContext.findErrorPages();
	}

	@Override
	public FilterConfig findFilterConfig(String name) {
		dualInvoke(name);

		return m_standardContext.findFilterConfig(name);
	}

	@Override
	public FilterDef findFilterDef(String filterName) {
		dualInvoke(filterName);
		return m_standardContext.findFilterDef(filterName);
	}

	@Override
	public FilterDef[] findFilterDefs() {

		return m_standardContext.findFilterDefs();
	}

	@Override
	public FilterMap[] findFilterMaps() {

		return m_standardContext.findFilterMaps();
	}

	@Override
	public String[] findInstanceListeners() {

		return m_standardContext.findInstanceListeners();
	}

	@Override
	public LifecycleListener[] findLifecycleListeners() {

		return m_standardContext.findLifecycleListeners();
	}

	@Override
	public Context findMappingObject() {

		return m_standardContext.findMappingObject();
	}

	@Override
	public MessageDestination findMessageDestination(String name) {
		dualInvoke(name);
		return m_standardContext.findMessageDestination(name);
	}

	@Override
	public MessageDestinationRef findMessageDestinationRef(String name) {
		dualInvoke(name);
		return m_standardContext.findMessageDestinationRef(name);
	}

	@Override
	public MessageDestinationRef[] findMessageDestinationRefs() {

		return m_standardContext.findMessageDestinationRefs();
	}

	@Override
	public MessageDestination[] findMessageDestinations() {

		return m_standardContext.findMessageDestinations();
	}

	@Override
	public String findMimeMapping(String extension) {
		dualInvoke(extension);
		return m_standardContext.findMimeMapping(extension);
	}

	@Override
	public String[] findMimeMappings() {

		return m_standardContext.findMimeMappings();
	}

	@Override
	public String findParameter(String name) {
		dualInvoke(name);
		return m_standardContext.findParameter(name);
	}

	@Override
	public String[] findParameters() {

		return m_standardContext.findParameters();
	}

	@Override
	public String findRoleMapping(String role) {
		dualInvoke(role);
		return m_standardContext.findRoleMapping(role);
	}

	@Override
	public boolean findSecurityRole(String role) {
		dualInvoke(role);
		return m_standardContext.findSecurityRole(role);
	}

	@Override
	public String[] findSecurityRoles() {

		return m_standardContext.findSecurityRoles();
	}

	@Override
	public String findServletMapping(String pattern) {
		dualInvoke(pattern);
		return m_standardContext.findServletMapping(pattern);
	}

	@Override
	public String[] findServletMappings() {

		return m_standardContext.findServletMappings();
	}

	@Override
	public DirContext findStaticResources() {

		return m_standardContext.findStaticResources();
	}

	@Override
	public String findStatusPage(int status) {
		dualInvoke(status);
		return m_standardContext.findStatusPage(status);
	}

	@Override
	public int[] findStatusPages() {

		return m_standardContext.findStatusPages();
	}

	@Override
	public String findTaglib(String uri) {
		dualInvoke(uri);
		return m_standardContext.findTaglib(uri);
	}

	@Override
	public String[] findTaglibs() {
		return m_standardContext.findTaglibs();
	}

	@Override
	public String[] findWatchedResources() {

		return m_standardContext.findWatchedResources();
	}

	@Override
	public boolean findWelcomeFile(String name) {
		dualInvoke(name);
		return m_standardContext.findWelcomeFile(name);
	}

	@Override
	public String[] findWelcomeFiles() {

		return m_standardContext.findWelcomeFiles();
	}

	@Override
	public String[] findWrapperLifecycles() {

		return m_standardContext.findWrapperLifecycles();
	}

	@Override
	public String[] findWrapperListeners() {

		return m_standardContext.findWrapperListeners();
	}

	public void finish() {
		m_isFinished = true;
	}

	@Override
	public void fireContainerEvent(String type, Object data) {
		dualInvoke(type, data);

		m_standardContext.fireContainerEvent(type, data);
	}

	@Override
	public String getAltDDName() {

		return m_standardContext.getAltDDName();
	}

	@Override
	public AnnotationProcessor getAnnotationProcessor() {
		return m_standardContext.getAnnotationProcessor();
	}

	@Override
	public boolean getAntiJARLocking() {

		return m_standardContext.getAntiJARLocking();
	}

	@Override
	public boolean getAntiResourceLocking() {

		return m_standardContext.getAntiResourceLocking();
	}

	@Override
	public Object[] getApplicationEventListeners() {

		return m_standardContext.getApplicationEventListeners();
	}

	@Override
	public Object[] getApplicationLifecycleListeners() {

		return m_standardContext.getApplicationLifecycleListeners();
	}

	@Override
	public boolean getAvailable() {

		return m_standardContext.getAvailable();
	}

	@Override
	public int getBackgroundProcessorDelay() {

		return m_standardContext.getBackgroundProcessorDelay();
	}

	@Override
	public Valve getBasic() {

		return m_standardContext.getBasic();
	}

	@Override
	public int getCacheMaxSize() {

		return m_standardContext.getCacheMaxSize();
	}

	@Override
	public int getCacheTTL() {

		return m_standardContext.getCacheTTL();
	}

	@Override
	public CharsetMapper getCharsetMapper() {

		return m_standardContext.getCharsetMapper();
	}

	@Override
	public String getCharsetMapperClass() {

		return m_standardContext.getCharsetMapperClass();
	}

	@Override
	public ObjectName[] getChildren() {

		return m_standardContext.getChildren();
	}

	@Override
	public Cluster getCluster() {

		return m_standardContext.getCluster();
	}

	@Override
	public String getCompilerClasspath() {

		return m_standardContext.getCompilerClasspath();
	}

	@Override
	public File getConfigBase() {

		return m_standardContext.getConfigBase();
	}

	@Override
	public String getConfigFile() {

		return m_standardContext.getConfigFile();
	}

	@Override
	public boolean getConfigured() {

		return m_standardContext.getConfigured();
	}

	@Override
	public String getContainerSuffix() {

		return m_standardContext.getContainerSuffix();
	}

	@Override
	public boolean getCookies() {

		return m_standardContext.getCookies();
	}

	@Override
	public boolean getCrossContext() {

		return m_standardContext.getCrossContext();
	}

	@Override
	public String getDefaultContextXml() {

		return m_standardContext.getDefaultContextXml();
	}

	@Override
	public String getDefaultWebXml() {

		return m_standardContext.getDefaultWebXml();
	}

	@Override
	public boolean getDelegate() {

		return m_standardContext.getDelegate();
	}

	@Override
	public String getDeploymentDescriptor() {

		return m_standardContext.getDeploymentDescriptor();
	}

	@Override
	public String getDisplayName() {

		return m_standardContext.getDisplayName();
	}

	@Override
	public boolean getDistributable() {

		return m_standardContext.getDistributable();
	}

	@Override
	public String getDocBase() {

		return m_standardContext.getDocBase();
	}

	@Override
	public String getDomain() {

		return m_standardContext.getDomain();
	}

	@Override
	public String getEncodedPath() {

		return m_standardContext.getEncodedPath();
	}

	@Override
	public String getEngineName() {

		return m_standardContext.getEngineName();
	}

	@Override
	public Valve getFirst() {

		return m_standardContext.getFirst();
	}

	@Override
	public String getHostname() {

		return m_standardContext.getHostname();
	}

	@Override
	public boolean getIgnoreAnnotations() {

		return m_standardContext.getIgnoreAnnotations();
	}

	@Override
	public String getInfo() {

		return m_standardContext.getInfo();
	}

	@Override
	public String getJ2EEApplication() {

		return m_standardContext.getJ2EEApplication();
	}

	@Override
	public String getJ2EEServer() {

		return m_standardContext.getJ2EEServer();
	}

	@Override
	public String[] getJavaVMs() {

		return m_standardContext.getJavaVMs();
	}

	@Override
	public ObjectName getJmxName() {

		return m_standardContext.getJmxName();
	}

	@Override
	public Loader getLoader() {

		return m_standardContext.getLoader();
	}

	@Override
	public LoginConfig getLoginConfig() {

		return m_standardContext.getLoginConfig();
	}

	@Override
	public Manager getManager() {

		return m_standardContext.getManager();
	}

	@Override
	public Mapper getMapper() {

		return m_standardContext.getMapper();
	}

	@Override
	public Object getMappingObject() {

		return m_standardContext.getMappingObject();
	}

	@Override
	public String getName() {

		return m_standardContext.getName();
	}

	@Override
	public NamingContextListener getNamingContextListener() {

		return m_standardContext.getNamingContextListener();
	}

	@Override
	public NamingResources getNamingResources() {

		return m_standardContext.getNamingResources();
	}

	@Override
	public MBeanNotificationInfo[] getNotificationInfo() {

		return m_standardContext.getNotificationInfo();
	}

	@Override
	public String getObjectName() {

		return m_standardContext.getObjectName();
	}

	@Override
	public String getOriginalDocBase() {

		return m_standardContext.getOriginalDocBase();
	}

	@Override
	public boolean getOverride() {

		return m_standardContext.getOverride();
	}

	@Override
	public Container getParent() {

		return m_standardContext.getParent();
	}

	@Override
	public ClassLoader getParentClassLoader() {

		return m_standardContext.getParentClassLoader();
	}

	@Override
	public ObjectName getParentName() throws MalformedObjectNameException {

		return m_standardContext.getParentName();
	}

	@Override
	public String getPath() {

		return m_standardContext.getPath();
	}

	@Override
	public boolean getPaused() {

		return m_standardContext.getPaused();
	}

	@Override
	public Pipeline getPipeline() {

		return m_standardContext.getPipeline();
	}

	@Override
	public boolean getPrivileged() {

		return m_standardContext.getPrivileged();
	}

	@Override
	public long getProcessingTime() {

		return m_standardContext.getProcessingTime();
	}

	@Override
	public boolean getProcessTlds() {

		return m_standardContext.getProcessTlds();
	}

	@Override
	public String getPublicId() {

		return m_standardContext.getPublicId();
	}

	@Override
	public Realm getRealm() {

		return m_standardContext.getRealm();
	}

	@Override
	public boolean getReloadable() {

		return m_standardContext.getReloadable();
	}

	@Override
	public DirContext getResources() {

		return m_standardContext.getResources();
	}

	@Override
	public String getServer() {

		return m_standardContext.getServer();
	}

	@Override
	public ServletContext getServletContext() {

		return m_standardContext.getServletContext();
	}

	@Override
	public String[] getServlets() {

		return m_standardContext.getServlets();
	}

	@Override
	public int getSessionTimeout() {

		return m_standardContext.getSessionTimeout();
	}

	@Override
	public boolean getStartChildren() {

		return m_standardContext.getStartChildren();
	}

	@Override
	public long getStartTime() {

		return m_standardContext.getStartTime();
	}

	@Override
	public long getStartupTime() {

		return m_standardContext.getStartupTime();
	}

	@Override
	public int getState() {

		return m_standardContext.getState();
	}

	@Override
	public DirContext getStaticResources() {

		return m_standardContext.getStaticResources();
	}

	@Override
	public boolean getSwallowOutput() {

		return m_standardContext.getSwallowOutput();
	}

	@Override
	public boolean getTldNamespaceAware() {

		return m_standardContext.getTldNamespaceAware();
	}

	@Override
	public long getTldScanTime() {

		return m_standardContext.getTldScanTime();
	}

	@Override
	public boolean getTldValidation() {

		return m_standardContext.getTldValidation();
	}

	@Override
	public String getType() {

		return m_standardContext.getType();
	}

	@Override
	public long getUnloadDelay() {

		return m_standardContext.getUnloadDelay();
	}

	@Override
	public boolean getUnpackWAR() {

		return m_standardContext.getUnpackWAR();
	}

	@Override
	public ObjectName[] getValveObjectNames() {

		return m_standardContext.getValveObjectNames();
	}

	@Override
	public Valve[] getValves() {

		return m_standardContext.getValves();
	}

	@Override
	public String[] getWelcomeFiles() {

		return m_standardContext.getWelcomeFiles();
	}

	@Override
	public String getWorkDir() {

		return m_standardContext.getWorkDir();
	}

	@Override
	public String getWorkPath() {

		return m_standardContext.getWorkPath();
	}

	@Override
	public String getWrapperClass() {

		return m_standardContext.getWrapperClass();
	}

	@Override
	public boolean getXmlNamespaceAware() {

		return m_standardContext.getXmlNamespaceAware();
	}

	@Override
	public boolean getXmlValidation() {

		return m_standardContext.getXmlValidation();
	}

	@Override
	public void init() throws Exception {

		m_standardContext.init();
	}

	private void dualInvoke(Object... args) {
		if (m_isFinished) {
			return;
		}

		StackTraceElement[] trace = Thread.currentThread().getStackTrace();
		StackTraceElement element = trace[2];
		String methodName = element.getMethodName();

		if (methodName.equals("addWatchedResource")) { // to work around
			                                            // NullPointerException
			m_subContext = new StandardContext();
			m_subContext.setDocBase(m_standardContext.getDocBase());
		}

		if (methodName.equals("setPublicId")) {
			if (m_servletContext.getAttribute(Constants.PHOENIX_WEBAPP_DESCRIPTOR_DEFAULT) == null) {
				m_subContext = new StandardContext();
				m_subContext.setDocBase(m_standardContext.getDocBase());
				m_servletContext.setAttribute(Constants.PHOENIX_WEBAPP_DESCRIPTOR_DEFAULT, m_subContext);
			} else if (m_servletContext.getAttribute(Constants.PHOENIX_WEBAPP_DESCRIPTOR_APP) == null) {
				m_subContext = new StandardContext();
				m_subContext.setDocBase(m_standardContext.getDocBase());
				m_servletContext.setAttribute(Constants.PHOENIX_WEBAPP_DESCRIPTOR_APP, m_subContext);
			} else if (m_servletContext.getAttribute(Constants.PHOENIX_WEBAPP_DESCRIPTOR_KERNEL) == null) {
				m_subContext = new StandardContext();
				m_subContext.setDocBase(m_standardContext.getDocBase());
				m_servletContext.setAttribute(Constants.PHOENIX_WEBAPP_DESCRIPTOR_KERNEL, m_subContext);
			}

			m_servletContext.setAttribute(Constants.PHOENIX_WEBAPP_DESCRIPTOR_ALL, m_standardContext);
		}

		Method[] methods = StandardContext.class.getMethods();
		Method method = findMethod(methods, methodName, args);

		if (method != null) {
			try {
				method.invoke(m_subContext, args);
			} catch (Exception e) {
				getLogger().warn(String.format("Error when invoking method(%s) of %s", methodName, StandardContext.class),
				      e);
			}
		} else {
			getLogger().warn(
			      String.format("No method(%s) found with %s arguments in %s", methodName, args.length,
			            StandardContext.class));
		}
	}

	private Method findMethod(Method[] methods, String methodName, Object... args) {
		for (Method m : methods) {
			Class<?>[] parameterTypes = null;
			if (m.getName().equals(methodName) && (parameterTypes = m.getParameterTypes()).length == args.length) {
				int i = 0;
				for (Object arg : args) {
					if (arg != null && !parameterTypes[i++].isInstance(arg)) {
						break;
					}
				}

				return m;
			}
		}

		return null;
	}

	@Override
	public void invoke(Request request, Response response) throws IOException, ServletException {

		m_standardContext.invoke(request, response);
	}

	@Override
	public boolean isAllowLinking() {

		return m_standardContext.isAllowLinking();
	}

	@Override
	public boolean isCachingAllowed() {

		return m_standardContext.isCachingAllowed();
	}

	@Override
	public boolean isCaseSensitive() {

		return m_standardContext.isCaseSensitive();
	}

	@Override
	public boolean isEventProvider() {

		return m_standardContext.isEventProvider();
	}

	@Override
	public boolean isFilesystemBased() {

		return m_standardContext.isFilesystemBased();
	}

	@Override
	public boolean isLazy() {

		return m_standardContext.isLazy();
	}

	@Override
	public boolean isReplaceWelcomeFiles() {
		return m_standardContext.isReplaceWelcomeFiles();
	}

	@Override
	public boolean isSaveConfig() {
		return m_standardContext.isSaveConfig();
	}

	@Override
	public boolean isStateManageable() {
		return m_standardContext.isStateManageable();
	}

	@Override
	public boolean isStatisticsProvider() {
		return m_standardContext.isStatisticsProvider();
	}

	@Override
	public boolean isUseNaming() {

		return m_standardContext.isUseNaming();
	}

	@Override
	public boolean listenerStart() {

		return m_standardContext.listenerStart();
	}

	@Override
	public boolean listenerStop() {

		return m_standardContext.listenerStop();
	}

	@Override
	public void loadOnStartup(Container[] children) {
		dualInvoke((Object) children);

		m_standardContext.loadOnStartup(children);
	}

	@Override
	public void postDeregister() {

		m_standardContext.postDeregister();
	}

	@Override
	public void postRegister(Boolean registrationDone) {
		dualInvoke(registrationDone);

		m_standardContext.postRegister(registrationDone);
	}

	@Override
	public void preDeregister() throws Exception {

		m_standardContext.preDeregister();
	}

	@Override
	public ObjectName preRegister(MBeanServer server, ObjectName name) throws Exception {

		return m_standardContext.preRegister(server, name);
	}

	@Override
	public synchronized void reload() {

		m_standardContext.reload();
	}

	@Override
	public void removeApplicationListener(String listener) {
		dualInvoke(listener);
		m_standardContext.removeApplicationListener(listener);
	}

	@Override
	public void removeApplicationParameter(String name) {
		dualInvoke(name);
		m_standardContext.removeApplicationParameter(name);
	}

	@Override
	public void removeChild(Container child) {
		dualInvoke(name);
		m_standardContext.removeChild(child);
	}

	@Override
	public void removeConstraint(SecurityConstraint constraint) {
		dualInvoke(name);
		m_standardContext.removeConstraint(constraint);
	}

	@Override
	public void removeContainerListener(ContainerListener listener) {
		dualInvoke(listener);

		m_standardContext.removeContainerListener(listener);
	}

	@Override
	public void removeErrorPage(ErrorPage errorPage) {
		dualInvoke(errorPage);
		m_standardContext.removeErrorPage(errorPage);
	}

	@Override
	public void removeFilterDef(FilterDef filterDef) {
		dualInvoke(filterDef);
		m_standardContext.removeFilterDef(filterDef);
	}

	@Override
	public void removeFilterMap(FilterMap filterMap) {
		dualInvoke(filterMap);
		m_standardContext.removeFilterMap(filterMap);
	}

	@Override
	public void removeInstanceListener(String listener) {
		dualInvoke(listener);

		m_standardContext.removeInstanceListener(listener);
	}

	@Override
	public void removeLifecycleListener(LifecycleListener listener) {
		dualInvoke(listener);

		m_standardContext.removeLifecycleListener(listener);
	}

	@Override
	public void removeMessageDestination(String name) {
		dualInvoke(name);

		m_standardContext.removeMessageDestination(name);
	}

	@Override
	public void removeMessageDestinationRef(String name) {
		dualInvoke(name);

		m_standardContext.removeMessageDestinationRef(name);
	}

	@Override
	public void removeMimeMapping(String extension) {
		dualInvoke(extension);

		m_standardContext.removeMimeMapping(extension);
	}

	@Override
	public void removeNotificationListener(NotificationListener listener) throws ListenerNotFoundException {

		m_standardContext.removeNotificationListener(listener);
	}

	@Override
	public void removeNotificationListener(NotificationListener listener, NotificationFilter filter, Object object)
	      throws ListenerNotFoundException {

		m_standardContext.removeNotificationListener(listener, filter, object);
	}

	@Override
	public void removeParameter(String name) {
		dualInvoke(name);

		m_standardContext.removeParameter(name);
	}

	@Override
	public void removePropertyChangeListener(PropertyChangeListener listener) {
		dualInvoke(listener);

		m_standardContext.removePropertyChangeListener(listener);
	}

	@Override
	public void removeRoleMapping(String role) {
		dualInvoke(role);

		m_standardContext.removeRoleMapping(role);
	}

	@Override
	public void removeSecurityRole(String role) {
		dualInvoke(role);

		m_standardContext.removeSecurityRole(role);
	}

	@Override
	public void removeServletMapping(String pattern) {
		dualInvoke(pattern);

		m_standardContext.removeServletMapping(pattern);
	}

	@Override
	public void removeTaglib(String uri) {
		dualInvoke(uri);

		m_standardContext.removeTaglib(uri);
	}

	@Override
	public synchronized void removeValve(Valve valve) {
		dualInvoke(valve);

		m_standardContext.removeValve(valve);
	}

	@Override
	public void removeWatchedResource(String name) {
		dualInvoke(name);

		m_standardContext.removeWatchedResource(name);
	}

	@Override
	public void removeWelcomeFile(String name) {
		dualInvoke(name);

		m_standardContext.removeWelcomeFile(name);
	}

	@Override
	public void removeWrapperLifecycle(String listener) {
		dualInvoke(listener);

		m_standardContext.removeWrapperLifecycle(listener);
	}

	@Override
	public void removeWrapperListener(String listener) {
		dualInvoke(listener);

		m_standardContext.removeWrapperListener(listener);
	}

	@Override
	public boolean resourcesStart() {

		return m_standardContext.resourcesStart();
	}

	@Override
	public boolean resourcesStop() {

		return m_standardContext.resourcesStop();
	}

	@Override
	public void setAllowLinking(boolean allowLinking) {
		dualInvoke(allowLinking);
		m_standardContext.setAllowLinking(allowLinking);
	}

	@Override
	public void setAltDDName(String altDDName) {
		dualInvoke(altDDName);
		m_standardContext.setAltDDName(altDDName);
	}

	@Override
	public void setAnnotationProcessor(AnnotationProcessor annotationProcessor) {
		dualInvoke(annotationProcessor);
		m_standardContext.setAnnotationProcessor(annotationProcessor);
	}

	@Override
	public void setAntiJARLocking(boolean antiJARLocking) {
		dualInvoke(antiJARLocking);
		m_standardContext.setAntiJARLocking(antiJARLocking);
	}

	@Override
	public void setAntiResourceLocking(boolean antiResourceLocking) {
		dualInvoke(antiResourceLocking);
		m_standardContext.setAntiResourceLocking(antiResourceLocking);
	}

	@Override
	public void setApplicationEventListeners(Object[] listeners) {
		dualInvoke(listeners);
		m_standardContext.setApplicationEventListeners(listeners);
	}

	@Override
	public void setApplicationLifecycleListeners(Object[] listeners) {
		dualInvoke(listeners);
		m_standardContext.setApplicationLifecycleListeners(listeners);
	}

	@Override
	public void setAvailable(boolean available) {
		dualInvoke(available);
		m_standardContext.setAvailable(available);
	}

	@Override
	public void setBackgroundProcessorDelay(int delay) {
		dualInvoke(delay);

		m_standardContext.setBackgroundProcessorDelay(delay);
	}

	@Override
	public void setBasic(Valve valve) {
		dualInvoke(valve);

		m_standardContext.setBasic(valve);
	}

	@Override
	public void setCacheMaxSize(int cacheMaxSize) {
		dualInvoke(cacheMaxSize);
		m_standardContext.setCacheMaxSize(cacheMaxSize);
	}

	@Override
	public void setCacheTTL(int cacheTTL) {
		dualInvoke(cacheTTL);
		m_standardContext.setCacheTTL(cacheTTL);
	}

	@Override
	public void setCachingAllowed(boolean cachingAllowed) {
		dualInvoke(cachingAllowed);
		m_standardContext.setCachingAllowed(cachingAllowed);
	}

	@Override
	public void setCaseSensitive(boolean caseSensitive) {
		dualInvoke(caseSensitive);
		m_standardContext.setCaseSensitive(caseSensitive);
	}

	@Override
	public void setCharsetMapper(CharsetMapper mapper) {
		dualInvoke(mapper);
		m_standardContext.setCharsetMapper(mapper);
	}

	@Override
	public void setCharsetMapperClass(String mapper) {
		dualInvoke(mapper);
		m_standardContext.setCharsetMapperClass(mapper);
	}

	@Override
	public synchronized void setCluster(Cluster cluster) {
		dualInvoke(cluster);

		m_standardContext.setCluster(cluster);
	}

	@Override
	public void setCompilerClasspath(String compilerClasspath) {
		dualInvoke(compilerClasspath);
		m_standardContext.setCompilerClasspath(compilerClasspath);
	}

	@Override
	public void setConfigFile(String configFile) {
		dualInvoke(configFile);
		m_standardContext.setConfigFile(configFile);
	}

	@Override
	public void setConfigured(boolean configured) {
		dualInvoke(configured);
		m_standardContext.setConfigured(configured);
	}

	@Override
	public void setCookies(boolean cookies) {
		dualInvoke(cookies);
		m_standardContext.setCookies(cookies);
	}

	@Override
	public void setCrossContext(boolean crossContext) {
		dualInvoke(crossContext);
		m_standardContext.setCrossContext(crossContext);
	}

	@Override
	public void setDefaultContextXml(String defaultContextXml) {
		dualInvoke(defaultContextXml);
		m_standardContext.setDefaultContextXml(defaultContextXml);
	}

	@Override
	public void setDefaultWebXml(String defaultWebXml) {
		dualInvoke(defaultWebXml);
		m_standardContext.setDefaultWebXml(defaultWebXml);
	}

	@Override
	public void setDelegate(boolean delegate) {
		dualInvoke(delegate);
		m_standardContext.setDelegate(delegate);
	}

	@Override
	public void setDisplayName(String displayName) {
		dualInvoke(displayName);
		m_standardContext.setDisplayName(displayName);
	}

	@Override
	public void setDistributable(boolean distributable) {
		dualInvoke(distributable);
		m_standardContext.setDistributable(distributable);
	}

	@Override
	public void setDocBase(String docBase) {
		dualInvoke(docBase);
		m_standardContext.setDocBase(docBase);
	}

	@Override
	public void setDomain(String domain) {
		dualInvoke(domain);

		m_standardContext.setDomain(domain);
	}

	@Override
	public void setEngineName(String engineName) {
		dualInvoke(engineName);
		m_standardContext.setEngineName(engineName);
	}

	@Override
	public void setIgnoreAnnotations(boolean ignoreAnnotations) {
		dualInvoke(ignoreAnnotations);
		m_standardContext.setIgnoreAnnotations(ignoreAnnotations);
	}

	@Override
	public void setJ2EEApplication(String j2eeApplication) {
		dualInvoke(j2eeApplication);
		m_standardContext.setJ2EEApplication(j2eeApplication);
	}

	@Override
	public void setJ2EEServer(String j2eeServer) {
		dualInvoke(j2eeServer);
		m_standardContext.setJ2EEServer(j2eeServer);
	}

	@Override
	public String[] setJavaVMs(String[] javaVMs) {
		dualInvoke((Object) javaVMs);

		return m_standardContext.setJavaVMs(javaVMs);
	}

	@Override
	public void setLazy(boolean lazy) {
		dualInvoke(lazy);
		m_standardContext.setLazy(lazy);
	}

	@Override
	public synchronized void setLoader(Loader loader) {
		dualInvoke(loader);
		m_standardContext.setLoader(loader);
	}

	@Override
	public void setLoginConfig(LoginConfig config) {
		dualInvoke(config);
		m_standardContext.setLoginConfig(config);
	}

	@Override
	public synchronized void setManager(Manager manager) {
		dualInvoke(manager);

		m_standardContext.setManager(manager);
	}

	@Override
	public void setName(String name) {
		dualInvoke(name);
		m_standardContext.setName(name);
	}

	@Override
	public void setNamingContextListener(NamingContextListener namingContextListener) {
		dualInvoke(namingContextListener);

		m_standardContext.setNamingContextListener(namingContextListener);
	}

	@Override
	public void setNamingResources(NamingResources namingResources) {
		dualInvoke(namingResources);
		m_standardContext.setNamingResources(namingResources);
	}

	@Override
	public void setOriginalDocBase(String docBase) {
		dualInvoke(docBase);
		m_standardContext.setOriginalDocBase(docBase);
	}

	@Override
	public void setOverride(boolean override) {
		dualInvoke(override);
		m_standardContext.setOverride(override);
	}

	@Override
	public void setParent(Container container) {
		dualInvoke(container);

		m_standardContext.setParent(container);
	}

	@Override
	public void setParentClassLoader(ClassLoader parent) {
		dualInvoke(parent);

		m_standardContext.setParentClassLoader(parent);
	}

	@Override
	public void setPath(String path) {
		dualInvoke(path);
		m_standardContext.setPath(path);
	}

	@Override
	public void setPrivileged(boolean privileged) {
		dualInvoke(privileged);
		m_standardContext.setPrivileged(privileged);
	}

	@Override
	public void setProcessTlds(boolean newProcessTlds) {
		dualInvoke(newProcessTlds);

		m_standardContext.setProcessTlds(newProcessTlds);
	}

	@Override
	public void setPublicId(String publicId) {
		dualInvoke(publicId);
		m_standardContext.setPublicId(publicId);
	}

	@Override
	public synchronized void setRealm(Realm realm) {
		dualInvoke(realm);

		m_standardContext.setRealm(realm);
	}

	@Override
	public void setReloadable(boolean reloadable) {
		dualInvoke(reloadable);
		m_standardContext.setReloadable(reloadable);
	}

	@Override
	public void setReplaceWelcomeFiles(boolean replaceWelcomeFiles) {
		dualInvoke(replaceWelcomeFiles);
		m_standardContext.setReplaceWelcomeFiles(replaceWelcomeFiles);
	}

	@Override
	public synchronized void setResources(DirContext resources) {
		dualInvoke(resources);
		m_standardContext.setResources(resources);
	}

	@Override
	public void setSaveConfig(boolean saveConfig) {
		dualInvoke(saveConfig);
		m_standardContext.setSaveConfig(saveConfig);
	}

	@Override
	public String setServer(String server) {
		dualInvoke(server);

		return m_standardContext.setServer(server);
	}

	@Override
	public void setSessionTimeout(int timeout) {
		dualInvoke(timeout);
		m_standardContext.setSessionTimeout(timeout);
	}

	@Override
	public void setStartChildren(boolean startChildren) {
		dualInvoke(startChildren);

		m_standardContext.setStartChildren(startChildren);
	}

	@Override
	public void setStartupTime(long startupTime) {
		dualInvoke(startupTime);
		m_standardContext.setStartupTime(startupTime);
	}

	@Override
	public void setSwallowOutput(boolean swallowOutput) {
		dualInvoke(swallowOutput);
		m_standardContext.setSwallowOutput(swallowOutput);
	}

	@Override
	public void setTldNamespaceAware(boolean tldNamespaceAware) {
		dualInvoke(tldNamespaceAware);

		m_standardContext.setTldNamespaceAware(tldNamespaceAware);
	}

	@Override
	public void setTldScanTime(long tldScanTime) {
		dualInvoke(tldScanTime);
		m_standardContext.setTldScanTime(tldScanTime);
	}

	@Override
	public void setTldValidation(boolean tldValidation) {
		dualInvoke(tldValidation);

		m_standardContext.setTldValidation(tldValidation);
	}

	@Override
	public void setUnloadDelay(long unloadDelay) {
		dualInvoke(unloadDelay);
		m_standardContext.setUnloadDelay(unloadDelay);
	}

	@Override
	public void setUnpackWAR(boolean unpackWAR) {
		dualInvoke(unpackWAR);
		m_standardContext.setUnpackWAR(unpackWAR);
	}

	@Override
	public void setUseNaming(boolean useNaming) {
		dualInvoke(useNaming);
		m_standardContext.setUseNaming(useNaming);
	}

	@Override
	public void setWorkDir(String workDir) {
		dualInvoke(workDir);
		m_standardContext.setWorkDir(workDir);
	}

	@Override
	public void setWrapperClass(String wrapperClassName) {
		dualInvoke(wrapperClassName);
		m_standardContext.setWrapperClass(wrapperClassName);
	}

	@Override
	public void setXmlNamespaceAware(boolean webXmlNamespaceAware) {
		dualInvoke(webXmlNamespaceAware);

		m_standardContext.setXmlNamespaceAware(webXmlNamespaceAware);
	}

	@Override
	public void setXmlValidation(boolean webXmlValidation) {
		dualInvoke(webXmlValidation);

		m_standardContext.setXmlValidation(webXmlValidation);
	}

	@Override
	public synchronized void start() throws LifecycleException {

		m_standardContext.start();
	}

	@Override
	public void startRecursive() throws LifecycleException {

		m_standardContext.startRecursive();
	}

	@Override
	public synchronized void stop() throws LifecycleException {

		m_standardContext.stop();
	}

	@Override
	public String toString() {

		return m_standardContext.toString();
	}

}
