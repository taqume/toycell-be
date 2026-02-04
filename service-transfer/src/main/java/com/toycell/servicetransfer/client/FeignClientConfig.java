package com.toycell.servicetransfer.client;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Configuration
@Slf4j
public class FeignClientConfig {

    @Bean
    public RequestInterceptor requestInterceptor() {
        return new RequestInterceptor() {
            @Override
            public void apply(RequestTemplate template) {
                // JWT token'ı diğer servislere ilet
                ServletRequestAttributes attributes = 
                        (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
                
                if (attributes != null) {
                    String authorizationHeader = attributes.getRequest().getHeader("Authorization");
                    if (authorizationHeader != null) {
                        template.header("Authorization", authorizationHeader);
                        log.debug("Forwarding Authorization header to: {}", template.url());
                    }
                }
            }
        };
    }
}
