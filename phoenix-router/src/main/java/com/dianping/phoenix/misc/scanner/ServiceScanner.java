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

import java.util.ArrayList;
import java.util.List;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

/**
 * @author Leo Liang
 * 
 */
public class ServiceScanner extends XmlScanner<String> {

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.dianping.phoenix.misc.scanner.XmlScanner#doScan(org.w3c.dom.Document)
     */
    @Override
    protected List<String> doScan(Document doc) throws Exception {
        List<String> resList = new ArrayList<String>();
        XPathFactory factory = XPathFactory.newInstance();
        XPath xpath = factory.newXPath();
        XPathExpression expr = xpath
                .compile("//bean[@class='com.dianping.dpsf.spring.ServiceRegistry']/property[@name='services']/map/entry[@key]");

        Object xmlRes = expr.evaluate(doc, XPathConstants.NODESET);
        
        NodeList nodes = (NodeList) xmlRes;
        
        for(int i = 0; i< nodes.getLength();i++){
            resList.add(nodes.item(i).getAttributes().getNamedItem("key").getNodeValue());
        }

        return resList;
    }
}
