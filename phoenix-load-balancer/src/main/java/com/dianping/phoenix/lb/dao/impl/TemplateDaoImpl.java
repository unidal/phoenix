/**
 * Project: phoenix-load-balancer
 * 
 * File Created at 2013-10-18
 * 
 */
package com.dianping.phoenix.lb.dao.impl;

import java.util.List;

import com.dianping.phoenix.lb.dao.TemplateDao;
import com.dianping.phoenix.lb.exception.BizException;
import com.dianping.phoenix.lb.model.configure.entity.Template;

/**
 * @author Leo Liang
 * 
 */
public class TemplateDaoImpl extends AbstractDao implements TemplateDao {

    /*
     * (non-Javadoc)
     * 
     * @see com.dianping.phoenix.lb.dao.TemplateDao#find(java.lang.String)
     */
    @Override
    public Template find(String templateName) {
        return store.findTemplate(templateName);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.dianping.phoenix.lb.dao.TemplateDao#add(com.dianping.phoenix.lb.model
     * .configure.entity.Template)
     */
    @Override
    public void add(Template template) throws BizException {
        store.updateOrCreateTemplate(template.getName(), template);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.dianping.phoenix.lb.dao.TemplateDao#update(com.dianping.phoenix.lb
     * .model.configure.entity.Template)
     */
    @Override
    public void update(Template template) throws BizException {
        store.updateOrCreateTemplate(template.getName(), template);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.dianping.phoenix.lb.dao.TemplateDao#list()
     */
    @Override
    public List<Template> list() {
        return store.listTemplates();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.dianping.phoenix.lb.dao.TemplateDao#delete(java.lang.String)
     */
    @Override
    public void delete(String templateName) throws BizException {
        store.removeTemplate(templateName);
    }

}
