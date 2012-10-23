package com.dianping.kernel.console.page.classpath;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.servlet.ServletException;

import com.dianping.kernel.console.ConsolePage;
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

	private List<Artifact> buildArtifacts() {
		ClassLoader loader = getClass().getClassLoader();

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
					}
				}
			}

			return artifacts;
		} else {
			throw new RuntimeException("Not supported classloader: " + loader.getClass());
		}
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
		List<Artifact> artifacts = buildArtifacts();

		Collections.sort(artifacts, new Comparator<Artifact>() {
			@Override
			public int compare(Artifact a1, Artifact a2) {
				return a1.toString().compareTo(a2.toString());
			}
		});

		model.setArtifacts(artifacts);
		model.setAction(Action.VIEW);
		model.setPage(ConsolePage.CLASSPATH);
		m_jspViewer.view(ctx, model);
	}
}
