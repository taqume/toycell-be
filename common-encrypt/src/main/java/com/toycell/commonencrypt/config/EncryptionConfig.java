package com.toycell.commonencrypt.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = "com.toycell.commonencrypt")
public class EncryptionConfig {
    // This configuration enables auto-scanning of encryption components
}
