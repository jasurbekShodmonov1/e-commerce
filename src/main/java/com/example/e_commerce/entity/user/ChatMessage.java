package com.example.e_commerce.entity.user;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "chat_messages")
@Getter
@Setter
public class ChatMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "chat_messages_seq_gen")
    @SequenceGenerator(
            name = "chat_messages_seq_gen",
            sequenceName = "chat_messages_seq",
            allocationSize = 50
    )
    private Long id;

    @Column(name = "sender_id", nullable = false)
    private Long senderId;

    @Column(name = "recipient_id", nullable = false)
    private Long recipientId;

    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(name = "timestamp", nullable = false)
    private LocalDateTime timestamp;
}
