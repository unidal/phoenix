package com.dianping.kernel.inspect.page.description;

import static com.dianping.kernel.Constants.PHOENIX_WEBAPP_DESCRIPTOR_ALL;
import static com.dianping.kernel.Constants.PHOENIX_WEBAPP_DESCRIPTOR_APP;
import static com.dianping.kernel.Constants.PHOENIX_WEBAPP_DESCRIPTOR_DEFAULT;
import static com.dianping.kernel.Constants.PHOENIX_WEBAPP_DESCRIPTOR_KERNEL;

import java.io.IOException;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import org.apache.catalina.core.StandardContext;

import com.dianping.kernel.inspect.InspectPage;
import org.unidal.lookup.annotation.Inject;
import org.unidal.web.mvc.PageHandler;
import org.unidal.web.mvc.annotation.InboundActionMeta;
import org.unidal.web.mvc.annotation.OutboundActionMeta;
import org.unidal.web.mvc.annotation.PayloadMeta;

public class Handler implements PageHandler<Context> {
	@Inject
	private JspViewer m_jspViewer;

	private DescriptorModel m_defaultModel;

	private DescriptorModel m_appModel;

	private DescriptorModel m_kernelModel;

	private DescriptorModel m_allModel;

	@Override
	@PayloadMeta(Payload.class)
	@InboundActionMeta(name = "desc")
	public void handleInbound(Context ctx) throws ServletException, IOException {
		// display only, no action here
	}

	@Override
	@OutboundActionMeta(name = "desc")
	public void handleOutbound(Context ctx) throws ServletException, IOException {
		Model model = new Model(ctx);
		ServletContext sc = ctx.getServletContext();
		StandardContext defaultDescriptor = (StandardContext) sc.getAttribute(PHOENIX_WEBAPP_DESCRIPTOR_DEFAULT);
		StandardContext appDescriptor = (StandardContext) sc.getAttribute(PHOENIX_WEBAPP_DESCRIPTOR_APP);
		StandardContext kernelDescriptor = (StandardContext) sc.getAttribute(PHOENIX_WEBAPP_DESCRIPTOR_KERNEL);
		StandardContext allDescriptor = (StandardContext) sc.getAttribute(PHOENIX_WEBAPP_DESCRIPTOR_ALL);

		if (m_defaultModel == null && defaultDescriptor != null) {
			m_defaultModel = new DescriptorModel(defaultDescriptor);
		}

		if (m_appModel == null && appDescriptor != null) {
			m_appModel = new DescriptorModel(appDescriptor);
		}

		if (m_kernelModel == null && kernelDescriptor != null) {
			m_kernelModel = new DescriptorModel(kernelDescriptor);
		}

		if (m_allModel == null && allDescriptor != null) {
			m_allModel = new DescriptorModel(allDescriptor);
		}

		model.setAction(Action.VIEW);
		model.setPage(InspectPage.DESCRIPTION);
		model.setDefaultModel(m_defaultModel);
		model.setAppModel(m_appModel);
		model.setKernelModel(m_kernelModel);
		model.setAllModel(m_allModel);

		m_jspViewer.view(ctx, model);
	}
}
