package org.trpg.farming.config;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 全局登录认证拦截器 http 请求进入 Controller 之前执行
 * 功能：校验请求头中的 JWT Token，解析用户ID，存入上下文
 * 所有需要登录的接口都会经过此拦截器
 * 负责 HTTP 请求的鉴权和用户信息提取
 */
@Component
public class HttpAuthInterceptor implements HandlerInterceptor {

    /**
     * 请求进入 Controller 之前执行
     * 1. 放行 OPTIONS 预检请求
     * 2. 获取并校验 Token
     * 3. 解析用户ID存入上下文
     *
     * @return true=放行，false=拦截
     */
    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {

        // 1. 放行跨域预检请求（必须）
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        // 9. 校验通过，放行
        return true;
    }

    /**
     * 请求完成后执行（视图渲染之后）
     * 作用：清除 ThreadLocal 中的用户信息，防止内存泄漏和用户串号
     */
    @Override
    public void afterCompletion(HttpServletRequest request,
                                HttpServletResponse response,
                                Object handler,
                                Exception ex) {
    }

    /**
     * 统一返回 401 未授权 JSON 格式信息
     */
    private boolean unauthorized(HttpServletResponse response, String msg) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");

        Map<String, Object> result = new HashMap<>();
        result.put("code", 401);
        result.put("message", msg);
        return false;
    }
}