package com.dianping.phoenix.softbalance.action;

import org.springframework.stereotype.Component;

import com.opensymphony.xwork2.ActionSupport;

/**
 * @author wukezhu
 */
@Component("loginAction")
public class LoginAction extends ActionSupport {
    private static final long   serialVersionUID = -1084994778030229218L;

    @Override
    public String execute() throws Exception {
        LOG.info("execute");
        return SUCCESS;
    }

    @Override
    public void validate() {
        LOG.info("validate");
        super.validate();
    }

}
