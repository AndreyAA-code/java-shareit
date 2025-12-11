package ru.practicum.gateway.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.RestTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RestTemplateConfig {

    private final String serverHost;

    public RestTemplateConfig(@Value("${server.host}") String serverHost) {
        this.serverHost = serverHost;
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    public String getServerHost() {
        return serverHost;
    }
}