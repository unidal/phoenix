/**
 * Project: phoenix-load-balancer
 * 
 * File Created at 2013-10-16
 * 
 */
package com.dianping.phoenix.lb.dao;

import java.util.List;

import com.dianping.phoenix.lb.model.configure.entity.Template;

/**
 * @author Leo Liang
 * 
 */
public interface TemplateDao {

    Template find(String templateName);

    boolean add(Template template);

    void update(Template template);

    List<Template> list();

    void delete(String templateName);

}
