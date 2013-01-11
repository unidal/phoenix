package com.dianping.service.editor.page.home;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;

import org.unidal.lookup.annotation.Inject;
import org.unidal.web.mvc.PageHandler;
import org.unidal.web.mvc.annotation.InboundActionMeta;
import org.unidal.web.mvc.annotation.OutboundActionMeta;
import org.unidal.web.mvc.annotation.PayloadMeta;

import com.dianping.service.deployment.entity.DeploymentModel;
import com.dianping.service.deployment.entity.PropertyModel;
import com.dianping.service.deployment.entity.ServiceModel;
import com.dianping.service.editor.EditorPage;
import com.dianping.service.editor.model.ModelBuilder;
import com.dianping.service.editor.model.ServiceAccessor;

public class Handler implements PageHandler<Context> {
	@Inject
	private JspViewer m_jspViewer;

	@Inject
	private ModelBuilder m_builder;

	@Inject
	private ServiceAccessor m_accessor;

	private ServiceModel findService(DeploymentModel deployment, String serviceType, String alias) {
		for (ServiceModel service : deployment.getActiveServices()) {
			if (service.getType().getName().equals(serviceType) && service.getAlias().equals(alias)) {
				return service;
			}
		}

		return null;
	}

	@Override
	@PayloadMeta(Payload.class)
	@InboundActionMeta(name = "home")
	public void handleInbound(Context ctx) throws ServletException, IOException {
		Payload payload = ctx.getPayload();

		switch (payload.getAction()) {
		case EDIT:
			if (m_accessor.updateProperties(payload.getServiceType(), payload.getAlias(), payload.getProperties())) {
				ctx.redirect(EditorPage.HOME, "serviceType=" + payload.getServiceType());
				return;
			}

			// TODO error handling
			break;
		}
	}

	@Override
	@OutboundActionMeta(name = "home")
	public void handleOutbound(Context ctx) throws ServletException, IOException {
		Model model = new Model(ctx);
		Payload payload = ctx.getPayload();
		DeploymentModel deployment = new DeploymentModel();

		model.setAction(Action.VIEW);
		model.setPage(EditorPage.HOME);
		deployment.accept(m_builder);

		switch (payload.getAction()) {
		case EDIT:
			Map<String, String> properties = payload.getProperties();
			String serviceType = payload.getServiceType();
			String alias = payload.getAlias();
			ServiceModel service = findService(deployment, serviceType, alias);

			if (service != null) {
				for (Map.Entry<String, String> e : properties.entrySet()) {
					updateProperty(service, e.getKey(), e.getValue());
				}
			}

			break;
		case VIEW:
		}

		model.setDeployment(deployment);
		m_jspViewer.view(ctx, model);
	}

	private void updateProperty(ServiceModel service, String name, String value) {
		for (PropertyModel property : service.getProperties()) {
			if (property.getName().equals(name)) {
				property.setValue(value);
			}
		}
	}
}
