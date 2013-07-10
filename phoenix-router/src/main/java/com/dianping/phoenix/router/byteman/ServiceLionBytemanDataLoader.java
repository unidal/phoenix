/**
 * Project: phoenix-router
 * 
 * File Created at 2013-6-6
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
package com.dianping.phoenix.router.byteman;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Pattern;

import com.dianping.phoenix.ResourceUtils;

/**
 * @author Leo Liang
 * 
 */
public class ServiceLionBytemanDataLoader {
    public Map<String, String> loadServices(String p) {
        Pattern pattern = Pattern.compile(p);
        Collection<String> resources = ResourceUtils.getResources(pattern);
        Map<String, String> res = new HashMap<String, String>();
        InputStream input = null;

        for (String resource : resources) {
            try {
                input = this.getClass().getResourceAsStream("/" + resource);
                if (input != null) {
                    Properties prop = new Properties();
                    prop.loadFromXML(input);
                    for (String key : prop.stringPropertyNames()) {
                        res.put(key, prop.getProperty(key));
                        System.out.println(String.format("[ServiceLion] add %s = %s", key, prop.getProperty(key)));
                    }
                }
            } catch (Exception e) {

            } finally {
                if (input != null) {
                    try {
                        input.close();
                    } catch (IOException e) {

                    }
                    input = null;
                }
            }
        }
        return res;
    }
}
