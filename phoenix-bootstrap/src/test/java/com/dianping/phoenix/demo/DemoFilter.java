package com.dianping.phoenix.demo;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

public class DemoFilter implements Filter {
	private static int s_index;

	private String m_id;

	public DemoFilter() {
		m_id = "DemoFilter" + s_index++;
	}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		m_id = filterConfig.getFilterName();
		System.out.println(m_id + " Init");
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
	      ServletException {
		System.out.println(m_id + " before");
		chain.doFilter(request, response);
		System.out.println(m_id + " after");
	}

	@Override
	public void destroy() {
		System.out.println(m_id + " destory");
	}
}
