package com.dianping.phoenix.console.page.deploy2;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.unidal.lookup.annotation.Inject;
import org.unidal.web.http.HttpServletResponseWrapper;

import com.dianping.phoenix.deploy.DeployContext;
import com.dianping.phoenix.deploy.DeployManager;
import com.dianping.phoenix.deploy.DeployUpdate;

public class KeepAliveViewer {
	@Inject
	private JspViewer m_jspViewer;

	@Inject
	private DeployManager m_deployManager;

	private byte[] renderChunk(Context ctx, Model model) throws ServletException, IOException {
		HttpServletRequest req = ctx.getHttpServletRequest();
		HttpServletResponse response = ctx.getHttpServletResponse();
		HttpServletResponseWrapper res = new HttpServletResponseWrapper(response, true);

		ctx.initialize(req, res); // hack
		m_jspViewer.view(ctx, model);
		ctx.initialize(req, response); // restore

		return res.getByteArray();
	}

	public void view(Context ctx, Model model) throws ServletException, IOException {
		long lastAccessTime = System.currentTimeMillis();
		HttpServletResponse res = ctx.getHttpServletResponse();

		res.setBufferSize(4096);
		res.setHeader("Transfer-Encoding", "chunked");

		ServletOutputStream out = res.getOutputStream();
		byte[] firstChunk = renderChunk(ctx, model);

		// HTML with status chuck
		out.write(firstChunk);
		res.flushBuffer();

		int deployId = ctx.getPayload().getId();
		DeployContext deployContext = new DeployContext(deployId);

		while (true) {
			DeployUpdate update = m_deployManager.poll(deployContext);

			if (update != null) {
				// status chunk
				update.writeTo(out);
				out.flush();
				lastAccessTime = System.currentTimeMillis();

				if (update.isDone()) {
					break;
				}
			} else if (System.currentTimeMillis() - lastAccessTime > 15 * 1000L) { // 15 seconds
				// avoid client idle and disconnect
				out.write(" ".getBytes());
				out.flush();
				lastAccessTime = System.currentTimeMillis();
			}
		}
	}
}
