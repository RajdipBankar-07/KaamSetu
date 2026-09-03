package com.kaamsetu;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * 🌾 KaamSetu (कामसेतू) - Main Spring Boot Application
 * Modular Monolith Backend for Rural & Village Local Jobs Marketplace
 */
@SpringBootApplication
@EnableJpaAuditing
public class KaamSetuApplication {

    public static void main(String[] args) {
        SpringApplication.run(KaamSetuApplication.class, args);
    }
}
