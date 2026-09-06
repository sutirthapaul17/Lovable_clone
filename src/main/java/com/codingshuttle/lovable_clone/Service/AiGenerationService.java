package com.codingshuttle.lovable_clone.Service;

import org.springframework.http.codec.ServerSentEvent;
import reactor.core.publisher.Flux;

import java.util.Optional;

public interface AiGenerationService {
    Flux<String> streamResponse(String message, Long projectId);
}
