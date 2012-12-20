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
		String requestUri = ((HttpServletRequest) request).getRequestURI();

		if (requestUri.startsWith("/jsp/inspect/")) {
			RequestDispatcher dispatcher = m_config.getServletContext().getRequestDispatcher("jsp");

			if (dispatcher != null) {
				dispatcher.forward(request, response);
			}
		}
	}

	@Override
	public void init(FilterConfig config) throws ServletException {
		m_config = config;
	}
}
