/**
 * Project: phoenix-load-balancer
 * 
 * File Created at 2013-10-17
 * 
 */
package com.dianping.phoenix.lb.service;

import java.util.List;

import com.dianping.phoenix.lb.exception.BizException;
import com.dianping.phoenix.lb.model.configure.entity.Template;

/**
 * @author Leo Liang
 * 
 */
public interface TemplateService {
    List<Template> listTemplates();

    Template findTemplate(String templateName) throws BizException;

    void addTemplate(String templateName, String content) throws BizException;

    void deleteTemplate(String templateName) throws BizException;

    void modifyTemplate(String templateName, String content) throws BizException;
}
