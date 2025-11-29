package org.shanling.simplemanage.config.encrypt;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.io.IOException;

/**
 * 加密过滤器
 *
 * @author shanling
 */
@Slf4j
public class CryptoFilter implements Filter {
    
    private final ApiDecryptProperties properties;
    private final ScriptRsaKeyProvider keyProvider;
    private final RequestMappingHandlerMapping handlerMapping;
    private final HandlerExceptionResolver exceptionResolver;

    public CryptoFilter(ApiDecryptProperties properties, 
                       ScriptRsaKeyProvider keyProvider,
                       RequestMappingHandlerMapping handlerMapping,
                       HandlerExceptionResolver exceptionResolver) {
        this.properties = properties;
        this.keyProvider = keyProvider;
        this.handlerMapping = handlerMapping;
        this.exceptionResolver = exceptionResolver;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) 
            throws IOException, ServletException {
        
        HttpServletRequest servletRequest = (HttpServletRequest) request;
        HttpServletResponse servletResponse = (HttpServletResponse) response;
        
        // 获取加密注解
        ApiEncrypt apiEncrypt = getApiEncryptAnnotation(servletRequest);
        
        // 如果没有加密注解，直接放行
        if (apiEncrypt == null) {
            chain.doFilter(request, response);
            return;
        }
        
        boolean responseFlag = apiEncrypt.response();
        ServletRequest requestWrapper = null;
        ServletResponse responseWrapper = null;
        EncryptResponseBodyWrapper responseBodyWrapper = null;

        // 获取密钥（从数据库或配置文件）
        String requestPrivateKey = getRequestPrivateKey();
        String responsePublicKey = getResponsePublicKey();

        // 验证密钥是否可用
        if (StrUtil.isBlank(requestPrivateKey) || StrUtil.isBlank(responsePublicKey)) {
            log.error("未配置加密密钥，请在数据库中配置script_rsa_key或在application.yml中配置默认密钥");
            exceptionResolver.resolveException(
                servletRequest, servletResponse, null,
                new RuntimeException("加密密钥未配置"));
            return;
        }

        // 是否为 put 或者 post 请求
        if (HttpMethod.PUT.matches(servletRequest.getMethod()) || 
            HttpMethod.POST.matches(servletRequest.getMethod())) {
            
            // 是否存在加密标头
            String headerValue = servletRequest.getHeader(properties.getHeaderFlag());
            if (StrUtil.isNotBlank(headerValue)) {
                try {
                    // 请求解密
                    requestWrapper = new DecryptRequestBodyWrapper(servletRequest, requestPrivateKey, properties.getHeaderFlag());
                } catch (Exception e) {
                    log.error("请求解密失败", e);
                    exceptionResolver.resolveException(
                        servletRequest, servletResponse, null,
                        new RuntimeException("请求解密失败: " + e.getMessage()));
                    return;
                }
            } else {
                // 有 @ApiEncrypt 注解但没有加密标头，拒绝访问
                log.warn("接口要求加密但请求未加密: {}", servletRequest.getRequestURI());
                exceptionResolver.resolveException(
                    servletRequest, servletResponse, null,
                    new RuntimeException("请求必须加密"));
                return;
            }
        }

        // 判断是否响应加密
        if (responseFlag) {
            responseBodyWrapper = new EncryptResponseBodyWrapper(servletResponse);
            responseWrapper = responseBodyWrapper;
        }

        chain.doFilter(
            ObjectUtil.defaultIfNull(requestWrapper, request),
            ObjectUtil.defaultIfNull(responseWrapper, response));

        if (responseFlag && responseBodyWrapper != null) {
            servletResponse.reset();
            // 对原始内容加密
            String encryptContent = responseBodyWrapper.getEncryptContent(
                servletResponse, responsePublicKey, properties.getHeaderFlag());
            // 对加密后的内容写出
            servletResponse.getWriter().write(encryptContent);
        }
    }

    /**
     * 获取 ApiEncrypt 注解
     */
    private ApiEncrypt getApiEncryptAnnotation(HttpServletRequest servletRequest) {
        try {
            HandlerExecutionChain mappingHandler = handlerMapping.getHandler(servletRequest);
            if (ObjectUtil.isNotNull(mappingHandler)) {
                Object handler = mappingHandler.getHandler();
                if (handler instanceof HandlerMethod handlerMethod) {
                    return handlerMethod.getMethodAnnotation(ApiEncrypt.class);
                }
            }
        } catch (Exception e) {
            log.debug("获取ApiEncrypt注解失败", e);
        }
        return null;
    }

    /**
     * 获取请求解密私钥
     * 优先使用数据库密钥，如果未找到则使用配置文件默认密钥
     */
    private String getRequestPrivateKey() {
        String dbKey = keyProvider.getRequestPrivateKey();
        if (StrUtil.isNotBlank(dbKey)) {
            return dbKey;
        }
        return properties.getPrivateKey();
    }

    /**
     * 获取响应加密公钥
     * 优先使用数据库密钥，如果未找到则使用配置文件默认密钥
     */
    private String getResponsePublicKey() {
        String dbKey = keyProvider.getResponsePublicKey();
        if (StrUtil.isNotBlank(dbKey)) {
            return dbKey;
        }
        return properties.getPublicKey();
    }

    @Override
    public void destroy() {
    }
}
