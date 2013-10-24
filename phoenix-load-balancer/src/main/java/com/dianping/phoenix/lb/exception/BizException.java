/**
 * Project: phoenix-load-balancer
 * 
 * File Created at 2013-10-11
 * 
 */
package com.dianping.phoenix.lb.exception;

/**
 * @author Leo Liang
 * 
 */
public class BizException extends Exception {
    static final long serialVersionUID = -3387516993124229443L;

    public BizException() {
        super();
    }

    public BizException(String message) {
        super(message);
    }

    public BizException(String message, Throwable cause) {
        super(message, cause);
    }

    public BizException(Throwable cause) {
        super(cause);
    }
}
