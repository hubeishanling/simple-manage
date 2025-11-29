package org.shanling.simplemanage.config.encrypt;

import jakarta.servlet.DispatcherType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

/**
 * API 加密自动配置
 *
 * @author shanling
 */
@Slf4j
@Configuration
@ConditionalOnProperty(value = "api-decrypt.enabled", havingValue = "true")
public class CryptoAutoConfiguration {

    @Bean
    public FilterRegistrationBean<CryptoFilter> cryptoFilterRegistration(
            ApiDecryptProperties properties,
            ScriptRsaKeyProvider keyProvider,
            RequestMappingHandlerMapping handlerMapping,
            HandlerExceptionResolver handlerExceptionResolver) {
        
        log.info("初始化API加密过滤器，headerFlag={}", properties.getHeaderFlag());
        
        FilterRegistrationBean<CryptoFilter> registration = new FilterRegistrationBean<>();
        registration.setDispatcherTypes(DispatcherType.REQUEST);
        registration.setFilter(new CryptoFilter(properties, keyProvider, handlerMapping, handlerExceptionResolver));
        registration.addUrlPatterns("/*");
        registration.setName("cryptoFilter");
        registration.setOrder(FilterRegistrationBean.HIGHEST_PRECEDENCE);
        
        return registration;
    }
}
