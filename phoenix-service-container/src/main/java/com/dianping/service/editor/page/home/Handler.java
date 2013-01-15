package com.dianping.service.editor.page.home;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;

import org.unidal.helper.Reflects;
import org.unidal.lookup.annotation.Inject;
import org.unidal.web.mvc.PageHandler;
import org.unidal.web.mvc.annotation.InboundActionMeta;
import org.unidal.web.mvc.annotation.OutboundActionMeta;
import org.unidal.web.mvc.annotation.PayloadMeta;

import com.dianping.service.deployment.entity.DeploymentModel;
import com.dianping.service.deployment.entity.InstanceModel;
import com.dianping.service.deployment.entity.PropertyModel;
import com.dianping.service.deployment.entity.ServiceModel;
import com.dianping.service.editor.EditorPage;
import com.dianping.service.editor.model.ServiceAccessor;

public class Handler implements PageHandler<Context> {
	@Inject
	private JspViewer m_jspViewer;

	@Inject
	private ServiceAccessor m_accessor;

	@Override
	@PayloadMeta(Payload.class)
	@InboundActionMeta(name = "home")
	public void handleInbound(Context ctx) throws ServletException, IOException {
		Payload payload = ctx.getPayload();

		if (!ctx.hasErrors()) {
			switch (payload.getAction()) {
			case EDIT:
				try {
					m_accessor.updateProperties(payload.getServiceType(), payload.getId(), payload.getProperties());
					ctx.redirect(EditorPage.HOME, "serviceType=" + payload.getServiceType() + "&id=" + payload.getId());
					return;
				} catch (Exception e) {
					ctx.addError("editor.updateProperties", e);
				}

				break;
			}
		}
	}

	@Override
	@OutboundActionMeta(name = "home")
	public void handleOutbound(Context ctx) throws ServletException, IOException {
		Model model = new Model(ctx);
		Payload payload = ctx.getPayload();
		DeploymentModel deployment = m_accessor.buildDeployment();

		model.setAction(Action.VIEW);
		model.setPage(EditorPage.HOME);

		switch (payload.getAction()) {
		case EDIT:
			Map<String, String> properties = payload.getProperties();
			Class<?> serviceType = Reflects.forClass().getClass(payload.getServiceType());
			ServiceModel service = deployment.findService(serviceType);
			InstanceModel instance = service == null ? null : service.findInstance(payload.getId());

			if (instance != null) {
				for (Map.Entry<String, String> e : properties.entrySet()) {
					updateProperty(instance, e.getKey(), e.getValue());
				}
			}

			break;
		case VIEW:
			break;
		}

		model.setDeployment(deployment);
		m_jspViewer.view(ctx, model);
	}

	private void updateProperty(InstanceModel instance, String name, String value) {
		for (PropertyModel property : instance.getProperties()) {
			if (property.getName().equals(name)) {
				property.setValue(value);
			}
		}
	}
}
