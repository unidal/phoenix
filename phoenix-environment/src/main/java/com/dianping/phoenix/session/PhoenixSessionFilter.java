package com.dianping.phoenix.session;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class PhoenixSessionFilter implements Filter {
	@Override
	public void destroy() {
	}

	private void doFilter(HttpServletRequest req, HttpServletResponse res, FilterChain chain) throws IOException,
	      ServletException {

	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
	      ServletException {
		if (request instanceof HttpServletRequest && response instanceof HttpServletResponse) {
			doFilter((HttpServletRequest) request, (HttpServletResponse) response, chain);
		}
	}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		// nothing to configure
	}

	static enum ValveHandler {
		REQUEST_ID {
			@Override
			public void handle(HttpServletRequest req, HttpServletResponse res, FilterChain chain) throws IOException,
			      ServletException {
				// TODO Auto-generated method stub

			}
		};

		public abstract void handle(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
		      throws IOException, ServletException;
	}
}
