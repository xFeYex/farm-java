package org.trpg.farming.lease.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(basePackages = "org.trpg.farming.lease")
@Slf4j
public class LeaseExceptionHandler {

    @ExceptionHandler(BizException.class)
    public Result<Void> handleBizException(BizException ex) {
        log.warn("租赁域业务异常: {}", ex.getMessage());
        return Result.fail(400, ex.getMessage());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public Result<Void> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
        log.warn("租赁域请求体读取失败: {}", ex.getMessage());
        return Result.fail(400, "请求体不能为空或格式错误");
    }

    @ExceptionHandler(Exception.class)
    public Result<Void> handleException(Exception ex) {
        log.error("租赁域系统异常", ex);
        return Result.fail(500, ex.getMessage() == null ? "租赁域服务暂时不可用，请稍后再试" : ex.getMessage());
    }
}
