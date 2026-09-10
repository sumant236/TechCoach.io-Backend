package com.techcoach.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

/**
 * Defines global Spring bean configurations and instantiates shared dependencies for the application context.
 */
@Configuration
public class AppConfig {

    // Instantiates a modern, synchronous HTTP client for communicating with external services (e.g., AI APIs)
    @Bean
    public RestClient restClient() {
        return RestClient.create();
    }
}
