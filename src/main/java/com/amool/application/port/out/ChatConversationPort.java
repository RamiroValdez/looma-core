package com.amool.application.port.out;

import com.amool.domain.model.ChatMessage;
import java.util.List;

public interface ChatConversationPort {
    void saveMessage(ChatMessage message);
    List<ChatMessage> getConversation(Long userId, Long chapterId);
    void saveChapterContext(Long userId, Long chapterId, String context);
    String getChapterContext(Long userId, Long chapterId);
}

