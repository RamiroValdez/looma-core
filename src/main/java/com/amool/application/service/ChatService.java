package com.amool.application.service;

import com.amool.application.usecases.ProcessChatMessageUseCase;
import com.amool.domain.model.ChatMessage;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class ChatService implements ProcessChatMessageUseCase {
    private final Map<String, List<ChatMessage>> conversations = new ConcurrentHashMap<>();
    private final Map<String, String> chapterContexts = new ConcurrentHashMap<>();
    private static final int MAX_MESSAGES = 20;
    private static final int MAX_CONTEXT_LENGTH = 4000;
    private static final int MAX_HISTORY_MESSAGES = 5;
    
    private final ChatClient chatClient;
    
    @Autowired
    public ChatService(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
    }

    @Override
    public ChatMessage processMessage(Long userId, Long chapterId, String message, String chapterContent) {
        String convKey = getConversationKey(userId, chapterId);
        
        if (chapterContent != null && !chapterContent.trim().isEmpty()) {
            chapterContexts.put(convKey, chapterContent);
        }

        List<ChatMessage> conversation = conversations.computeIfAbsent(convKey, k -> new ArrayList<>());
        
        ChatMessage userMessage = new ChatMessage(userId, chapterId, message, true);
        conversation.add(userMessage);
        
        String assistantResponse = generateResponse(convKey, message);
        ChatMessage assistantMessage = new ChatMessage(userId, chapterId, assistantResponse, false);
        conversation.add(assistantMessage);
        
        if (conversation.size() > MAX_MESSAGES) {
            List<ChatMessage> limited = new ArrayList<>(
                conversation.subList(conversation.size() - MAX_MESSAGES, conversation.size())
            );
            conversations.put(convKey, limited);
        }
        
        return assistantMessage;
    }

    @Override
    public List<ChatMessage> getConversation(Long userId, Long chapterId) {
        return conversations.getOrDefault(
            getConversationKey(userId, chapterId), 
            Collections.emptyList()
        );
    }

    private String generateResponse(String convKey, String userMessage) {
    String context = chapterContexts.getOrDefault(convKey, "");
    
    List<ChatMessage> conversationHistory = conversations.getOrDefault(convKey, Collections.emptyList());
    List<ChatMessage> historyForPrompt = new ArrayList<>(conversationHistory);
    
    if (!historyForPrompt.isEmpty()) {
        historyForPrompt.remove(historyForPrompt.size() - 1);
    }
    
    String conversationHistoryText = historyForPrompt.stream()
        .sorted(Comparator.comparing(ChatMessage::getTimestamp))
        .limit(MAX_HISTORY_MESSAGES)
        .map(msg -> String.format("%s: %s", 
            msg.isUserMessage() ? "Usuario" : "Asistente", 
            msg.getContent()))
        .collect(Collectors.joining("\n\n"));
    
    String limitedContext = context.length() > MAX_CONTEXT_LENGTH 
        ? context.substring(0, MAX_CONTEXT_LENGTH) + "..." 
        : context;
    
    try {
        String fullPrompt = """
            Eres un asistente de escritura creativa que ayuda a los escritores a mejorar sus historias.
            
            CONTEXTO DEL CAPÍTULO ACTUAL:
            %s
            
            HISTORIAL DE LA CONVERSACIÓN:
            %s
            
            MENSAJE ACTUAL DEL USUARIO:
            %s
            
            Por favor, proporciona una respuesta útil, creativa y detallada en el idioma del usuario.
            """.formatted(
                limitedContext.isEmpty() ? "(Sin contexto aún)" : limitedContext,
                conversationHistoryText.isEmpty() ? "(No hay historial previo)" : conversationHistoryText,
                userMessage
            );
            
        return chatClient.prompt()
            .user(fullPrompt)
            .call()
            .content();
    } catch (Exception e) {
        e.printStackTrace();
        return "Lo siento, hubo un error al generar la respuesta. Por favor, inténtalo de nuevo más tarde.";
    }
}

    private String getConversationKey(Long userId, Long chapterId) {
        return userId + "_" + chapterId;
    }
}
