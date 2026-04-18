package org.trpg.farming.production.common;

/**
 * 这里放一个很轻量的业务异常，够当前模块做参数和权限提示了。
 */
public class BizException extends RuntimeException {

    public BizException(String message) {
        super(message);
    }
}
