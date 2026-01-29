package com.codingshuttle.lovable_clone.Entity;

import com.codingshuttle.lovable_clone.Entity.enums.MessageRole;

import java.time.Instant;

public class ChatMessage {

    Long id;
    ChatSession chatSession;

    String content;

    MessageRole role;

    String toolCalls; //JSON array of tools called

    Integer tokenUsed;

    Instant createdAt;
}
