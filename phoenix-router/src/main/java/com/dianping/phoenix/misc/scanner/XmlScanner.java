/**
 * Project: phoenix-router
 * 
 * File Created at 2013-4-12
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
package com.dianping.phoenix.misc.scanner;

import java.io.File;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;

/**
 * TODO Comment of XMLScanner
 * 
 * @author Leo Liang
 * 
 */
public abstract class XmlScanner<T> implements Scanner<T> {
    private static Logger log = Logger.getLogger(XmlScanner.class);

    /*
     * (non-Javadoc)
     * 
     * @see com.dianping.phoenix.misc.scanner.Scanner#scan(java.lang.String)
     */
    @Override
    public List<T> scan(File file) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            return doScan(builder.parse(file));
        } catch (Exception e) {
            log.error(String.format("Parse file(%s) failed.", file), e);
            throw new RuntimeException(e);
        }
    }

    protected abstract List<T> doScan(Document doc) throws Exception;
}
