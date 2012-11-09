package com.dianping.kernel.inspect.page.classpath;

import static com.dianping.phoenix.bootstrap.AbstractCatalinaWebappLoader.PHOENIX_WEBAPP_PROVIDER_APP;
import static com.dianping.phoenix.bootstrap.AbstractCatalinaWebappLoader.PHOENIX_WEBAPP_PROVIDER_KERNEL;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import com.dianping.kernel.inspect.InspectPage;
import com.dianping.phoenix.spi.WebappProvider;
import com.site.lookup.annotation.Inject;
import com.site.web.mvc.PageHandler;
import com.site.web.mvc.annotation.InboundActionMeta;
import com.site.web.mvc.annotation.OutboundActionMeta;
import com.site.web.mvc.annotation.PayloadMeta;

public class Handler implements PageHandler<Context> {
	@Inject
	private JspViewer m_jspViewer;

	@Inject
	private ArtifactResolver m_artifactResolver;

	// cache artifacts for performance since it's unchangeable
	private List<Artifact> m_artifacts;

	private List<Artifact> m_kernelArtifacts;

	private List<Artifact> m_appArtifacts;
	
	private List<Artifact> m_containerArtifacts;

	private List<Artifact> buildArtifacts(ClassLoader loader) {
		if (loader instanceof URLClassLoader) {
			URLClassLoader ucl = (URLClassLoader) loader;
			URL[] urls = ucl.getURLs();
			List<Artifact> artifacts = new ArrayList<Artifact>(urls.length);

			for (URL url : urls) {
				String path = url.getPath();

				if (path.endsWith(".jar")) {
					int off = path.lastIndexOf(':');
					Artifact artifact = m_artifactResolver.resolve(new File(path.substring(off + 1)));

					if (artifact != null) {
						artifacts.add(artifact);
					} else {
						artifacts.add(new Artifact(path)); // something wrong?
					}
				} else {
					artifacts.add(new Artifact(path));
				}
			}

			Collections.sort(artifacts, new Comparator<Artifact>() {
				@Override
				public int compare(Artifact a1, Artifact a2) {
					return a1.compareTo(a2);
				}
			});

			return artifacts;
		} else {
			throw new RuntimeException("Not supported classloader: " + loader.getClass());
		}
	}
	
	private List<Artifact> buildContainerArtifacts(List<Artifact> artifacts,ClassLoader loader) {
		if (loader instanceof URLClassLoader) {
			URLClassLoader ucl = (URLClassLoader) loader;
			URL[] urls = ucl.getURLs();

			for (URL url : urls) {
				String path = url.getPath();

				if (path.endsWith(".jar")) {
					int off = path.lastIndexOf(':');
					Artifact artifact = m_artifactResolver.resolve(new File(path));

					if (artifact == null) {
						artifact = new Artifact(path);
					} 
					artifacts.add(artifact);
					artifact.setFromContainer();
				} else {
					Artifact artifact = new Artifact(path);
					artifacts.add(artifact);
					artifact.setFromContainer();
				}
			}

			Collections.sort(artifacts, new Comparator<Artifact>() {
				@Override
				public int compare(Artifact a1, Artifact a2) {
					return a1.compareTo(a2);
				}
			});
			if(loader.getParent() instanceof URLClassLoader){
				return buildContainerArtifacts(artifacts,loader.getParent());
			}else{
				return artifacts;
			}
			
		} else {
			throw new RuntimeException("Not supported classloader: " + loader.getClass());
		}
	}

	private List<Artifact> buildArtifacts(WebappProvider provider) {
		List<Artifact> artifacts = new ArrayList<Artifact>();

		for (File file : provider.getClasspathEntries()) {
			String path = file.getPath();

			if (path.endsWith(".jar")) {
				int off = path.lastIndexOf(':');
				Artifact artifact = m_artifactResolver.resolve(new File(path.substring(off + 1)));

				if (artifact != null) {
					artifacts.add(artifact);
				} else {
					artifacts.add(new Artifact(path)); // something wrong?
				}
			} else {
				artifacts.add(new Artifact(path));
			}
		}

		return artifacts;
	}

	@Override
	@PayloadMeta(Payload.class)
	@InboundActionMeta(name = "classpath")
	public void handleInbound(Context ctx) throws ServletException, IOException {
		// display only, no action here
	}

	@Override
	@OutboundActionMeta(name = "classpath")
	public void handleOutbound(Context ctx) throws ServletException, IOException {
		Model model = new Model(ctx);
		ServletContext servletContext = ctx.getServletContext();
		WebappProvider kernelProvider = (WebappProvider) servletContext.getAttribute(PHOENIX_WEBAPP_PROVIDER_KERNEL);
		WebappProvider appProvider = (WebappProvider) servletContext.getAttribute(PHOENIX_WEBAPP_PROVIDER_APP);
		boolean mixedMode = kernelProvider != null && appProvider != null;

		if (m_artifacts == null) {
			m_artifacts = buildArtifacts(getClass().getClassLoader());
		}

		model.setMixedMode(mixedMode);
		model.setAction(Action.VIEW);
		model.setPage(InspectPage.CLASSPATH);
		model.setArtifacts(m_artifacts);

		if (mixedMode) {
			if (m_kernelArtifacts == null || m_appArtifacts == null) {
				m_kernelArtifacts = buildArtifacts(kernelProvider);
				m_appArtifacts = buildArtifacts(appProvider);
				m_containerArtifacts = buildContainerArtifacts(new ArrayList<Artifact>(),getClass().getClassLoader().getParent());
				m_artifacts.addAll(m_containerArtifacts);
			}

			model.setKernelArtifacts(m_kernelArtifacts);
			model.setAppArtifacts(m_appArtifacts);
			model.setContainerArtifacts(m_containerArtifacts);
		}

		m_jspViewer.view(ctx, model);
	}
}
