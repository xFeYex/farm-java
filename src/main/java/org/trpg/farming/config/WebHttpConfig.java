package org.trpg.farming.config;

import lombok.RequiredArgsConstructor;
import org.antlr.v4.runtime.misc.NotNull;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Spring MVC 全局配置类
 * 负责：注册拦截器、配置跨域、全局Web配置
 * 负责HTTP相关内容
 */
@Configuration
@RequiredArgsConstructor
public class WebHttpConfig implements WebMvcConfigurer {

    /**
     * 注入自定义登录拦截器
     */
    private final HttpAuthInterceptor authInterceptor;

    /**
     * 注册拦截器
     * 拦截所有接口，排除登录/注册/错误页面
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authInterceptor)
                .addPathPatterns("/**")           // 拦截所有请求
                .excludePathPatterns(             // 放行不需要登录的接口
                        "/auth/login",
                        "/auth/register",
                        "/auth/sendEmailCode",
                        "/error",
                        "/test/**" // 测试接口
                );
    }

    /**
     * 全局跨域配置（CORS）
     * 解决前端访问后端时的跨域问题
     */
    @Override
    public void addCorsMappings(@NotNull CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("*")          // 允许所有来源
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // 允许的请求方法
                .allowedHeaders("*")                 // 允许所有请求头
                .allowCredentials(true)               // 允许携带Cookie/认证信息
                .maxAge(3600);                       // 预检请求缓存时间（秒）
    }
}
