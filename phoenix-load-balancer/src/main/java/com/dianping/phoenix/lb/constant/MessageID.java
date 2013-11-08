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
    STRATEGY_SAVE_FAIL("strategy_save_fail"), //
    //
    VIRTUALSERVER_ALREADY_EXISTS("vs_already_exists"), //
    VIRTUALSERVER_SAVE_FAIL("vs_save_fail"), //
    VIRTUALSERVER_CONCURRENT_MOD("vs_concurrent_mod"), //
    VIRTUALSERVER_NOT_EXISTS("vs_not_exists"), //
    VIRTUALSERVER_DEL_FAIL("vs_del_fail"), //
    VIRTUALSERVER_NAME_EMPTY("vs_name_empty"), //
    VIRTUALSERVER_TAGID_EMPTY("vs_pushid_empty"), //
    VIRTUALSERVER_DEFAULTPOOL_NOT_EXISTS("vs_defaultpool_not_exists"), //
    VIRTUALSERVER_DIRECTIVE_TYPE_NOT_SUPPORT("vs_directive_type_not_support"), //
    VIRTUALSERVER_LOCATION_NO_DOMAIN("vs_location_no_domain"), //
    VIRTUALSERVER_STRATEGY_NOT_SUPPORT("vs_strategy_not_support"), //
    VIRTUALSERVER_TAG_FAIL("vs_tag_fail"), //
    VIRTUALSERVER_TAG_LOAD_FAIL("vs_tag_load_fail"), //
    VIRTUALSERVER_TAG_NOT_FOUND("vs_tag_not_found"), //
    VIRTUALSERVER_TAG_LIST_FAIL("vs_tag_list_fail"), //

    POOL_LOWER_THAN_MINAVAIL_PCT("pool_lower_than_minavail_pct"), //
    POOL_NO_MEMBER("pool_no_member"), //

    ;

    private String messageId;

    private MessageID(String messageId) {
        this.messageId = messageId;
    }

    public String messageId() {
        return this.messageId;
    }

}
