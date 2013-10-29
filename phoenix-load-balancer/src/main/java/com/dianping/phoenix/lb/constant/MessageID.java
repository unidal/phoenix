/**
 * Project: phoenix-load-balancer
 * 
 * File Created at Oct 28, 2013
 * 
 */
package com.dianping.phoenix.lb.constant;

/**
 * @author Leo Liang
 * 
 */
public enum MessageID {
    TEMPLATE_SAVE_FAIL("template_save_fail"), //
    TEMPLATE_ALREADY_EXISTS("template_already_exists"), //
    TEMPLATE_NAME_EMPTY("template_name_empty"), //
    TEMPLATE_CONTENT_EMPTY("template_content_empty"), //
    TEMPLATE_NOT_EXISTS("template_not_exists"), //
    //
    STRATEGY_SAVE_FAIL("strategy_save_fail"), //
    //
    VIRTUALSERVER_ALREADY_EXISTS("vs_already_exists"), //
    VIRTUALSERVER_SAVE_FAIL("vs_save_fail"), //
    VIRTUALSERVER_CONCURRENT_MOD("vs_concurrent_mod"), //
    VIRTUALSERVER_NOT_EXISTS("vs_not_exists"), //
    VIRTUALSERVER_DEL_FAIL("vs_del_fail"), //
    VIRTUALSERVER_NAME_EMPTY("vs_name_empty"), //
    VIRTUALSERVER_DEFAULTPOOL_NOT_EXISTS("vs_defaultpool_not_exists"), //
    VIRTUALSERVER_TEMPLATE_NOT_EXISTS("vs_template_not_exists"), //
    VIRTUALSERVER_DIRECTIVE_TYPE_NOT_SUPPORT("vs_directive_type_not_support"), //
    VIRTUALSERVER_STRATEGY_NOT_SUPPORT("vs_strategy_not_support"), //

    ;

    private String messageId;

    private MessageID(String messageId) {
        this.messageId = messageId;
    }

    public String messageId() {
        return this.messageId;
    }

}
