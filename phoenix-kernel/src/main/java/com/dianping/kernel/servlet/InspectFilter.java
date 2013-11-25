package com.dianping.kernel.servlet;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

public class InspectFilter implements Filter {
	private FilterConfig m_config;

	@Override
	public void destroy() {
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
			ServletException {
		HttpServletRequest req = (HttpServletRequest) request;
		String contextPath = req.getContextPath();
		String requestUri = req.getRequestURI();
		boolean matched = false;

		if (contextPath == null || contextPath.length() == 0 || contextPath.equals("/")) {
			matched = requestUri.startsWith("/jsp/inspect/") || requestUri.startsWith("//jsp/inspect/");
		} else {
			matched = requestUri.substring(contextPath.length()).startsWith("/jsp/inspect/");
		}

		if (matched) {
			RequestDispatcher dispatcher = m_config.getServletContext().getNamedDispatcher("jsp");

			if (dispatcher != null) {
				dispatcher.forward(request, response);
			} else {
				chain.doFilter(request, response);
			}
		} else {
			chain.doFilter(request, response);
		}
	}

	@Override
	public void init(FilterConfig config) throws ServletException {
		m_config = config;
	}
}
