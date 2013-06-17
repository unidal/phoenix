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

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import com.dianping.phoenix.router.model.entity.F5Rule;

public class F5RuleWrapper implements Rule {
    private List<Pattern> patternList = new ArrayList<Pattern>();
    private String        targetUrlPattern;

    public F5RuleWrapper(F5Rule f5Rule, String targetUrlPattern) {
        for (String pattern : f5Rule.getPatterns()) {
            patternList.add(Pattern.compile(pattern));
        }
        this.targetUrlPattern = targetUrlPattern;
    }

    public boolean match(String path) {
        for (Pattern pattern : patternList) {
            if (pattern.matcher(path).matches()) {
                return true;
            }
        }
        return false;
    }

    public String map(String path) {
        return String.format(targetUrlPattern, path);
    }
}
