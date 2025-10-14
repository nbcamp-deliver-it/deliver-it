package com.sparta.deliverit.global.infrastructure.config.ai;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

@Configuration
public class GeminiConfig {

    private final String AUTHORIZATION = "dummyAUTHORIZATION";
    private final String token = "dummyToken";

    @Bean
    public SimpleClientHttpRequestFactory requestFactory() {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(Duration.ofSeconds(1));
        requestFactory.setReadTimeout(Duration.ofSeconds(10)); // java.net.SocketTimeoutException: Read timed out
        return requestFactory;
    }

    @Bean
    @Qualifier("geminiClient")
    public RestTemplate geminiRestTemplate() {
        RestTemplateBuilder builder = new RestTemplateBuilder();
        return builder.requestFactory(this::requestFactory)
                .defaultHeader(AUTHORIZATION, token)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .errorHandler(new GeminiRestTemplateErrorHandler())
                .build();
    }
}
