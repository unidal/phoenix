/**
 * Project: phoenix-maven-plugin
 * 
 * File Created at 2013-6-23
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
package com.dianping.maven.plugin.phoenix.web;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletResponse;

import com.dianping.maven.plugin.phoenix.web.MojoDataWebUI.DataTransmitter;
import com.google.gson.Gson;

/**
 * TODO Comment of BaseMojoDataServlet
 * 
 * @author Leo Liang
 * 
 */
public class BaseMojoDataServlet<T, R> extends HttpServlet {
    private static final long       serialVersionUID = 7623019869591944565L;
    protected DataTransmitter<T, R> dataTransmitter;
    private Gson                    gson             = new Gson();

    public BaseMojoDataServlet(DataTransmitter<T, R> dataTransmitter) {
        this.dataTransmitter = dataTransmitter;
    }

    protected void respError(HttpServletResponse resp, String errorMsg) throws IOException {
        resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        Map<String, String> resMap = new HashMap<String, String>();
        resMap.put("errMsg", errorMsg);
        resp.getOutputStream().write(gson.toJson(resMap).getBytes());
        resp.getOutputStream().flush();
    }

    protected void respSuccess(HttpServletResponse resp, Map<String, Object> resData) throws IOException {
        resp.setStatus(HttpServletResponse.SC_OK);
        resp.getOutputStream().write(gson.toJson(resData).getBytes());
        resp.getOutputStream().flush();
    }
}
