package com.amool.application.usecases;

import com.amool.application.port.out.ChatConversationPort;
import com.amool.domain.model.ChatMessage;

import java.util.List;

public class GetChatConversation {

    private final ChatConversationPort chatConversationPort;

    public GetChatConversation(ChatConversationPort chatConversationPort) {
        this.chatConversationPort = chatConversationPort;
    }

    public List<ChatMessage> execute(Long userId, Long chapterId) {
        return chatConversationPort.getConversation(userId, chapterId);
    }
}

