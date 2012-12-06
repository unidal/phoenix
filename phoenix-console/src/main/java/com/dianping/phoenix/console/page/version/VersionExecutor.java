package com.dianping.phoenix.console.page.version;

import org.unidal.helper.Threads.Task;

import com.dianping.phoenix.service.DefaultStatusReporter;
import com.dianping.phoenix.service.StatusReporter;

public class VersionExecutor implements Task {

	private boolean m_active;

	private VersionContext m_context;

	private VersionManager m_manager;

	public VersionExecutor(VersionContext context, VersionManager manager) {
		m_context = context;
		m_manager = manager;
		m_active = true;
	}

	@Override
	public String getName() {
		return getClass().getSimpleName();
	}

	@Override
	public void run() {
		if (m_active) {
			try {

				m_manager.submitVersion(m_context);
				m_manager.updateVersionSuccessed(m_context.getVersionId());
			} catch (VersionException e) {
				e.printStackTrace();
				try {
					m_manager.removeVersion(m_context.getVersionId());
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			m_manager
			.getReporter()
			.categoryLog(
					DefaultStatusReporter.VERSION_LOG,
					m_context.getVersion(),
					String.format(
							"Create version(%s) >>>>>>>>>>>>>>>DONE<<<<<<<<<<<<<<<<<",
							m_context.getVersion()));
			
			try {
				Thread.sleep(30 * 1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			m_manager.clearVersion(m_context.getVersion());

			
		}
	}

	@Override
	public void shutdown() {
		m_active = false;
	}

}
