package org.trpg.farming.lease.common;

/**
 * 租赁域先放一个轻量业务异常，够当前 MVP 的参数和业务规则校验使用。
 */
public class BizException extends RuntimeException {

    public BizException(String message) {
        super(message);
    }
}
