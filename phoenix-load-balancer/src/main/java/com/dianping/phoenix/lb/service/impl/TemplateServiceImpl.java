/**
 * Project: phoenix-load-balancer
 * 
 * File Created at 2013-10-17
 * 
 */
package com.dianping.phoenix.lb.service.impl;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.dianping.phoenix.lb.constant.MessageID;
import com.dianping.phoenix.lb.dao.TemplateDao;
import com.dianping.phoenix.lb.exception.BizException;
import com.dianping.phoenix.lb.model.configure.entity.Template;
import com.dianping.phoenix.lb.service.ConcurrentControlServiceTemplate;
import com.dianping.phoenix.lb.service.TemplateService;
import com.dianping.phoenix.lb.utils.ExceptionUtils;

/**
 * @author Leo Liang
 * 
 */
public class TemplateServiceImpl extends ConcurrentControlServiceTemplate implements TemplateService {

    @Autowired
    private TemplateDao templateDao;

    /**
     * @param templateDao
     */
    public TemplateServiceImpl(TemplateDao templateDao) {
        super();
        this.templateDao = templateDao;
    }

    /**
     * @param templateDao
     *            the templateDao to set
     */
    public void setTemplateDao(TemplateDao templateDao) {
        this.templateDao = templateDao;
    }

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
                public List<Template> doRead() {
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
        if (StringUtils.isBlank(templateName)) {
            ExceptionUtils.throwBizException(MessageID.TEMPLATE_NAME_EMPTY);
        }

        return read(new ReadOperation<Template>() {

            @Override
            public Template doRead() throws BizException {
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
        if (StringUtils.isBlank(templateName)) {
            ExceptionUtils.throwBizException(MessageID.TEMPLATE_NAME_EMPTY);
        }

        if (StringUtils.isBlank(content)) {
            ExceptionUtils.throwBizException(MessageID.TEMPLATE_CONTENT_EMPTY);
        }

        final Template newTemplate = new Template(templateName);
        newTemplate.setContent(content);

        write(new WriteOperation<Void>() {

            @Override
            public Void doWrite() throws Exception {
                if (templateDao.find(templateName) != null) {
                    ExceptionUtils.throwBizException(MessageID.TEMPLATE_ALREADY_EXISTS, templateName);
                }

                templateDao.add(newTemplate);

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
    public void deleteTemplate(final String templateName) throws BizException {
        if (StringUtils.isBlank(templateName)) {
            ExceptionUtils.throwBizException(MessageID.TEMPLATE_NAME_EMPTY);
        }

        try {
            write(new WriteOperation<Void>() {

                @Override
                public Void doWrite() throws Exception {
                    templateDao.delete(templateName);
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
    public void modifyTemplate(final String templateName, final String content) throws BizException {
        if (StringUtils.isBlank(templateName)) {
            ExceptionUtils.throwBizException(MessageID.TEMPLATE_NAME_EMPTY);
        }

        if (StringUtils.isBlank(content)) {
            ExceptionUtils.throwBizException(MessageID.TEMPLATE_CONTENT_EMPTY);
        }

        write(new WriteOperation<Void>() {

            @Override
            public Void doWrite() throws Exception {
                Template template = templateDao.find(templateName);
                if (template != null) {
                    template.setContent(content);
                    templateDao.update(template);
                } else {
                    ExceptionUtils.throwBizException(MessageID.TEMPLATE_NOT_EXISTS, templateName);
                }
                return null;
            }
        });
    }
}
