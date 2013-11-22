package com.dianping.phoenix.environment;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PhoenixEnvironmentFilter implements Filter {

    private static final Logger LOG = LoggerFactory.getLogger(PhoenixEnvironmentFilter.class);

    @Override
    public void destroy() {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        if (request instanceof HttpServletRequest && response instanceof HttpServletResponse) {
            try {
                HttpServletRequest hRequest = (HttpServletRequest) request;

                //从request中或去id
                String requestId = hRequest.getHeader(PhoenixContext.MOBILE_REQUEST_ID);
                String referRequestId = null;

                if (requestId != null) {//如果存在requestId，则说明是移动api的web端
                    referRequestId = hRequest.getHeader(PhoenixContext.MOBILE_REFER_REQUEST_ID);

                } else {//普通web端  TODO 待第二期实现
                    //requestId不存在，则生成
                    //referRequestId，异步通过pigeon去session服务器获取
                    //判断cookie中的guid是否存在，不存在则生成
                    //将所有id放入request属性，供页头使用
                    //request.setAttribute(PhoenixEnvironment.ENV, new PhoenixEnvironment());
                }

                //将id放入ThreadLocal
                if (requestId != null) {
                    PhoenixContext.getInstance().setRequestId(requestId);
                }
                if (referRequestId != null) {
                    PhoenixContext.getInstance().setReferRequestId(referRequestId);
                }

            } catch (RuntimeException e) {
                LOG.warn(e.getMessage(), e);
            }
        }

        try {
            chain.doFilter(request, response);

        } finally {
            //清除ThreadLocal
            PhoenixContext.getInstance().clear();
        }

    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }
}
