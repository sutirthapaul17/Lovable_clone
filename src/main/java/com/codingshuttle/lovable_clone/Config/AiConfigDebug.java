package com.codingshuttle.lovable_clone.Config;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.model.openai.autoconfigure.OpenAiChatProperties;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class AiConfigDebug {

    private final OpenAiChatModel chatModel;
    private final OpenAiChatProperties properties;

    @Value("${spring.ai.openai.base-url:NOT_FOUND}")
    private String baseUrl;

    @Value("${spring.ai.openai.timeout:NOT_FOUND}")
    private String timeout;

    @Value("${spring.ai.openai.api-key:NOT_FOUND}")
    private String apiKey;

    @PostConstruct
    public void debugEnvironment() {
        System.out.println("========== ENVIRONMENT ==========");
        System.out.println("BASE URL = " + baseUrl);
        System.out.println("TIMEOUT  = " + timeout);
        System.out.println("API KEY  = " +
                (apiKey.equals("NOT_FOUND") ? "NOT_FOUND" : "PRESENT"));
        System.out.println("=================================");
    }

}
