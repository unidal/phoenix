package com.dianping.kernel.inspect.page.description;

import com.dianping.kernel.inspect.InspectPage;
import org.unidal.web.mvc.ViewModel;

public class Model extends ViewModel<InspectPage, Action, Context> {
	private DescriptorModel m_defaultModel;

	private DescriptorModel m_appModel;

	private DescriptorModel m_kernelModel;

	private DescriptorModel m_allModel;

	public Model(Context ctx) {
		super(ctx);
	}

	public DescriptorModel getAllModel() {
		return m_allModel;
	}

	public DescriptorModel getAppModel() {
		return m_appModel;
	}

	@Override
	public Action getDefaultAction() {
		return Action.VIEW;
	}

	public DescriptorModel getDefaultModel() {
		return m_defaultModel;
	}

	public DescriptorModel getKernelModel() {
		return m_kernelModel;
	}

	public void setAllModel(DescriptorModel allModel) {
		m_allModel = allModel;

		if (allModel != null) {
			m_allModel.setModel(this);
		}
	}

	public void setAppModel(DescriptorModel appModel) {
		m_appModel = appModel;
	}

	public void setDefaultModel(DescriptorModel defaultModel) {
		m_defaultModel = defaultModel;
	}

	public void setKernelModel(DescriptorModel kernelModel) {
		m_kernelModel = kernelModel;
	}
}
