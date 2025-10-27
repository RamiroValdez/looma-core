package com.amool.application.usecases;

import com.amool.domain.model.ChatMessage;
import java.util.List;

public interface ProcessChatMessageUseCase {
    ChatMessage processMessage(Long userId, Long chapterId, String message, String chapterContent);
    List<ChatMessage> getConversation(Long userId, Long chapterId);
}
