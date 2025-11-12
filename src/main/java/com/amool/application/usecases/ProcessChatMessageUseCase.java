package com.amool.application.usecases;

import com.amool.application.port.out.ChatAIPort;
import com.amool.application.port.out.ChatConversationPort;
import com.amool.domain.model.ChatMessage;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class ProcessChatMessageUseCase {

    private static final int MAX_CONTEXT_LENGTH = 4000;
    private static final int MAX_HISTORY_MESSAGES = 5;

    private final ChatConversationPort chatConversationPort;
    private final ChatAIPort chatAIPort;

    public ProcessChatMessageUseCase(ChatConversationPort chatConversationPort, ChatAIPort chatAIPort) {
        this.chatConversationPort = chatConversationPort;
        this.chatAIPort = chatAIPort;
    }

    public ChatMessage execute(Long userId, Long chapterId, String message, String chapterContent) {
        // Guardar contexto del capítulo si se proporciona
        if (chapterContent != null && !chapterContent.trim().isEmpty()) {
            chatConversationPort.saveChapterContext(userId, chapterId, chapterContent);
        }

        // Guardar mensaje del usuario
        ChatMessage userMessage = new ChatMessage(userId, chapterId, message, true);
        chatConversationPort.saveMessage(userMessage);

        // Obtener historial de conversación
        List<ChatMessage> conversationHistory = chatConversationPort.getConversation(userId, chapterId);

        // Obtener contexto del capítulo
        String context = chatConversationPort.getChapterContext(userId, chapterId);
        String limitedContext = limitContext(context);

        // Preparar historial para el prompt (excluyendo el mensaje actual)
        List<String> historyForPrompt = buildConversationHistory(conversationHistory);

        // Generar respuesta del asistente
        String assistantResponse = generateResponse(limitedContext, historyForPrompt, message);

        // Guardar respuesta del asistente
        ChatMessage assistantMessage = new ChatMessage(userId, chapterId, assistantResponse, false);
        chatConversationPort.saveMessage(assistantMessage);

        return assistantMessage;
    }

    private String limitContext(String context) {
        if (context == null || context.isEmpty()) {
            return "";
        }
        return context.length() > MAX_CONTEXT_LENGTH
            ? context.substring(0, MAX_CONTEXT_LENGTH) + "..."
            : context;
    }

    private List<String> buildConversationHistory(List<ChatMessage> conversation) {
        if (conversation.isEmpty()) {
            return List.of();
        }

        // Excluir el último mensaje (que es el mensaje actual del usuario)
        int endIndex = Math.max(0, conversation.size() - 1);

        return conversation.subList(0, endIndex).stream()
            .sorted(Comparator.comparing(ChatMessage::getTimestamp))
            .skip(Math.max(0, endIndex - MAX_HISTORY_MESSAGES))
            .map(msg -> String.format("%s: %s",
                msg.isUserMessage() ? "Usuario" : "Asistente",
                msg.getContent()))
            .collect(Collectors.toList());
    }

    private String generateResponse(String context, List<String> historyForPrompt, String userMessage) {
        String conversationHistoryText = String.join("\n\n", historyForPrompt);

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
                context.isEmpty() ? "(Sin contexto aún)" : context,
                conversationHistoryText.isEmpty() ? "(No hay historial previo)" : conversationHistoryText,
                userMessage
            );

        return chatAIPort.generateResponse(fullPrompt, context, historyForPrompt);
    }
}

