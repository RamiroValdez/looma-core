package com.amool.adapters.out.chat;

import com.amool.application.port.out.ChatConversationPort;
import com.amool.domain.model.ChatMessage;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class InMemoryChatConversationAdapter implements ChatConversationPort {
    
    private static final int MAX_MESSAGES = 20;
    private final Map<String, List<ChatMessage>> conversations = new ConcurrentHashMap<>();
    private final Map<String, String> chapterContexts = new ConcurrentHashMap<>();

    @Override
    public void saveMessage(ChatMessage message) {
        String key = getConversationKey(message.getUserId(), message.getChapterId());
        List<ChatMessage> conversation = conversations.computeIfAbsent(key, k -> new ArrayList<>());
        conversation.add(message);
        
        if (conversation.size() > MAX_MESSAGES) {
            List<ChatMessage> limited = new ArrayList<>(
                conversation.subList(conversation.size() - MAX_MESSAGES, conversation.size())
            );
            conversations.put(key, limited);
        }
    }

    @Override
    public List<ChatMessage> getConversation(Long userId, Long chapterId) {
        String key = getConversationKey(userId, chapterId);
        return conversations.getOrDefault(key, Collections.emptyList());
    }

    @Override
    public void saveChapterContext(Long userId, Long chapterId, String context) {
        String key = getConversationKey(userId, chapterId);
        chapterContexts.put(key, context);
    }

    @Override
    public String getChapterContext(Long userId, Long chapterId) {
        String key = getConversationKey(userId, chapterId);
        return chapterContexts.getOrDefault(key, "");
    }

    private String getConversationKey(Long userId, Long chapterId) {
        return userId + "_" + chapterId;
    }
}

