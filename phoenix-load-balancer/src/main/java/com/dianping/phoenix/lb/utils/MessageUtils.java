/**
 * Project: phoenix-load-balancer
 * 
 * File Created at Oct 28, 2013
 * 
 */
package com.dianping.phoenix.lb.utils;

import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import com.dianping.phoenix.lb.constant.MessageID;

/**
 * @author Leo Liang
 * 
 */
public class MessageUtils {
    private static Properties messageMapping;

    static {
        InputStream stream = null;
        try {
            stream = MessageUtils.class.getClassLoader().getResourceAsStream("message.properties");
            messageMapping = new Properties();
            messageMapping.load(stream);
        } catch (Exception e) {
            throw new RuntimeException("Init message failed.", e);
        } finally {
            IOUtils.closeQuietly(stream);
        }
    }

    public static String getMessage(MessageID messageId, Object... args) {
        String msgTemplate = messageMapping.getProperty(messageId.messageId());
        if (StringUtils.isBlank(msgTemplate)) {
            return "Unknown message!";
        } else {
            return String.format(msgTemplate, args);
        }
    }
}
