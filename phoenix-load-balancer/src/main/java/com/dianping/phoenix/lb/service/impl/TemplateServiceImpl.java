/**
 * Project: phoenix-load-balancer
 * 
 * File Created at 2013-10-17
 * 
 */
package com.dianping.phoenix.lb.service.impl;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.dianping.phoenix.lb.dao.TemplateDao;
import com.dianping.phoenix.lb.exception.BizException;
import com.dianping.phoenix.lb.model.configure.entity.Template;
import com.dianping.phoenix.lb.service.ConcurrentControlTemplate;
import com.dianping.phoenix.lb.service.TemplateService;

/**
 * @author Leo Liang
 * 
 */
public class TemplateServiceImpl extends ConcurrentControlTemplate implements TemplateService {

    @Autowired
    private TemplateDao templateDao;

    /*
     * (non-Javadoc)
     * 
     * @see com.dianping.phoenix.lb.service.TemplateService#listTemplates()
     */
    @Override
    public List<Template> listTemplates() {
        try {
            return read(new ReadOperation<List<Template>>() {

                @Override
                public List<Template> doRead() throws BizException {
                    return templateDao.list();
                }
            });
        } catch (BizException e) {
            // ignore
            return null;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.dianping.phoenix.lb.service.TemplateService#findTemplate(java.lang
     * .String)
     */
    @Override
    public Template findTemplate(final String templateName) throws BizException {
        return read(new ReadOperation<Template>() {

            @Override
            public Template doRead() throws BizException {
                if (StringUtils.isBlank(templateName)) {
                    throw new BizException("Argument(templateName) must not be empty/null.");
                }
                return templateDao.find(templateName);
            }
        });

    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.dianping.phoenix.lb.service.TemplateService#addTemplate(java.lang
     * .String, java.lang.String)
     */
    @Override
    public void addTemplate(final String templateName, final String content) throws BizException {
        if (StringUtils.isBlank(templateName) || StringUtils.isBlank(content)) {
            throw new BizException("Argument(templateName or content) must not be empty/null.");
        }

        final Template newTemplate = new Template(templateName);
        newTemplate.setContent(content);

        write(new WriteOperation<Void>() {

            @Override
            public Void doWrite() throws BizException {
                if (templateDao.find(templateName) != null) {
                    throw new BizException(String.format("Template(%s) already exists.", templateName));
                }

                try {
                    templateDao.add(newTemplate);
                } catch (IOException e) {
                    throw new BizException(e);
                }

                return null;

            }
        });

    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.dianping.phoenix.lb.service.TemplateService#deleteTemplate(java.lang
     * .String)
     */
    @Override
    public void deleteTemplate(final String templateName) {
        try {
            write(new WriteOperation<Void>() {

                @Override
                public Void doWrite() throws BizException {
                    try {
                        templateDao.delete(templateName);
                    } catch (IOException e) {
                        throw new BizException(e);
                    }
                    return null;
                }
            });
        } catch (BizException e) {
            // ignore
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.dianping.phoenix.lb.service.TemplateService#modifyTemplate(java.lang
     * .String, java.lang.String)
     */
    @Override
    public void modifyTemplate(String templateName, String content) throws BizException {
        Template template = templateDao.find(templateName);
        if (template != null) {
            template.setContent(content);
            template.setLastModifiedDate(new Date());
            try {
                templateDao.update(template);
            } catch (IOException e) {
                throw new BizException(e);
            }
        }
    }

}
