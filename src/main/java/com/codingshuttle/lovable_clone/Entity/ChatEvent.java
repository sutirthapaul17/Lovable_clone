package com.codingshuttle.lovable_clone.Entity;


import com.codingshuttle.lovable_clone.Entity.enums.ChatEventType;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name = "chat_events")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level=AccessLevel.PRIVATE)
public class ChatEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    ChatMessage chatMessage;

    @Enumerated(EnumType.STRING)
    ChatEventType type;


    @Column(nullable = false)
    Integer sequenceOrder;

    @Column(columnDefinition = "TEXT")
    String content;

    String filePath;

    @Column(columnDefinition = "TEXT")
    String metaData;
}
