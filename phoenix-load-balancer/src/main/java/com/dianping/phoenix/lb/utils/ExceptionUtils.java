/**
 * Project: phoenix-load-balancer
 * 
 * File Created at Oct 28, 2013
 * 
 */
package com.dianping.phoenix.lb.utils;

import org.apache.log4j.Logger;

import com.dianping.phoenix.lb.constant.MessageID;
import com.dianping.phoenix.lb.exception.BizException;

/**
 * @author Leo Liang
 * 
 */
public class ExceptionUtils {
    private static final Logger log = Logger.getLogger(ExceptionUtils.class);

    public static void logAndRethrowBizException(Throwable e, MessageID messageId, Object... args) throws BizException {
        String msg = MessageUtils.getMessage(messageId, args);
        log.error(msg, e);
        throw new BizException(messageId, e, args);
    }

    public static void logAndRethrowBizException(Throwable e) throws BizException {
        log.error(e);
        throw new BizException(e);
    }

    public static void throwBizException(MessageID messageId, Object... args) throws BizException {
        throw new BizException(messageId, args);
    }
}
