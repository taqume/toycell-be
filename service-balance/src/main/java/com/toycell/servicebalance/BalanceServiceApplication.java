package com.toycell.servicebalance;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
@ComponentScan(basePackages = {"com.toycell.servicebalance", "com.toycell.commonexception"})
public class BalanceServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(BalanceServiceApplication.class, args);
    }
}
