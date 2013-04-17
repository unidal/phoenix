/**
 * Project: phoenix-router
 * 
 * File Created at 2013-4-17
 * $Id$
 * 
 * Copyright 2010 dianping.com.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * Dianping Company. ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with dianping.com.
 */
package com.dianping.phoenix.router;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;

/**
 * @author Leo Liang
 * 
 */
public class ResponseFilter implements Filter {
    private static final List<String>  ACCEPTED_CONTENT_TYPE = Arrays.asList(new String[] { "text/html" });
    private static final List<Integer> ACCEPTED_STATUS_CODE  = Arrays.asList(new Integer[] { HttpServletResponse.SC_OK });
    private static final String        SCRIPT_NAME           = "phoenix-router.js";
    private static final byte[]        RESPONSE_APPEND_TEXT  = String.format("<script src=\"%s\"></script>",
                                                                     SCRIPT_NAME).getBytes();

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
            ServletException {
        if (response instanceof HttpServletResponse && request instanceof HttpServletRequest) {
            if (StringUtils.equalsIgnoreCase(SCRIPT_NAME, ((HttpServletRequest) request).getRequestURI())) {
                IOUtils.copy(this.getClass().getResourceAsStream(SCRIPT_NAME), response.getOutputStream());
                return;
            }

            StatusAwareServletResponse statusAwareServletResponse = new StatusAwareServletResponse(
                    (HttpServletResponse) response);

            chain.doFilter(request, statusAwareServletResponse);

            if (ACCEPTED_STATUS_CODE.contains(statusAwareServletResponse.getStatus())
                    && ACCEPTED_CONTENT_TYPE
                            .contains(StringUtils.lowerCase(statusAwareServletResponse.getContentType()))) {
                statusAwareServletResponse.getOutputStream().write(RESPONSE_APPEND_TEXT);
            }

        } else {
            chain.doFilter(request, response);
        }

    }

    private static class StatusAwareServletResponse extends HttpServletResponseWrapper {

        private int httpStatus = SC_OK;

        public StatusAwareServletResponse(HttpServletResponse response) {
            super(response);

        }

        @Override
        public void setStatus(int sc) {
            httpStatus = sc;
            super.setStatus(sc);
        }

        @Override
        public void setStatus(int sc, String sm) {
            httpStatus = sc;
            super.setStatus(sc, sm);
        }

        public int getStatus() {
            return httpStatus;
        }

        @Override
        public void sendError(int sc) throws IOException {
            this.httpStatus = sc;
            super.sendError(sc);
        }

        @Override
        public void sendError(int sc, String msg) throws IOException {
            this.httpStatus = sc;
            super.sendError(sc, msg);
        }

        @Override
        public void sendRedirect(String location) throws IOException {
            this.httpStatus = SC_MOVED_TEMPORARILY;
            super.sendRedirect(location);
        }
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void destroy() {
    }

}
