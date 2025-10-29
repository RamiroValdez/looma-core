package com.amool.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessage {
    private Long userId;
    private Long chapterId;
    private String content;
    private boolean isUserMessage;
    private LocalDateTime timestamp;

    public ChatMessage(Long userId, Long chapterId, String content, boolean isUserMessage) {
        this.userId = userId;
        this.chapterId = chapterId;
        this.content = content;
        this.isUserMessage = isUserMessage;
        this.timestamp = LocalDateTime.now();
    }
}
