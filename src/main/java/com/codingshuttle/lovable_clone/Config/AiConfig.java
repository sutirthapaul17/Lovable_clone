package com.codingshuttle.lovable_clone.Config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.http.okhttp.OpenAiHttpClientBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class AiConfig {

    @Bean
    public OpenAiHttpClientBuilderCustomizer openAiHttpClientCustomizer() {
        return builder -> {
            builder.timeout(Duration.ofMinutes(5));
        };
    }

    @Bean
    public ChatClient chatClient(ChatClient.Builder builder){
//        OpenAiChatOptions options = OpenAiChatOptions.builder()
//                .timeout(Duration.ofMinutes(5))
//                .build();
//
//        return builder
//                .defaultOptions((ChatOptions.Builder) options)
//                .defaultAdvisors(
//                        new SimpleLoggerAdvisor()
//                )
//                .build();



        return builder
                .defaultOptions(
                        OpenAiChatOptions.builder()
                                .timeout(Duration.ofMinutes(5))
                )
                .defaultAdvisors(
                        new SimpleLoggerAdvisor()
                )
                .build();
    }
}
