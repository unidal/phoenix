/**
 * Project: phoenix-router
 * 
 * File Created at 2013-6-17
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
package com.dianping.phoenix.router.filter.request;

import java.util.regex.Pattern;

import com.dianping.phoenix.router.model.entity.UrlRewriteRule;

public class UrlRewriteRuleWrapper implements Rule {

    private String  pathRegex;
    private Pattern pathPattern;
    private String  pathTarget;

    public UrlRewriteRuleWrapper(UrlRewriteRule rule) {
        pathRegex = rule.getMaskUrl();
        pathPattern = Pattern.compile(pathRegex);
        pathTarget = rule.getRealUrl();
    }

    @Override
    public boolean match(String path) {
        return pathPattern.matcher(path).matches();
    }

    @Override
    public String map(String path) {
        return path.replaceAll(pathRegex, pathTarget);
    }

}