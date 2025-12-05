package com.amool.application.usecases;

import com.amool.application.port.out.ChatAIPort;
import com.amool.application.port.out.ChatConversationPort;
import com.amool.domain.model.ChatMessage;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class ProcessChatMessage {

    private static final int MAX_CONTEXT_LENGTH = 4000;
    private static final int MAX_HISTORY_MESSAGES = 5;

    private final ChatConversationPort chatConversationPort;
    private final ChatAIPort chatAIPort;

    public ProcessChatMessage(ChatConversationPort chatConversationPort, ChatAIPort chatAIPort) {
        this.chatConversationPort = chatConversationPort;
        this.chatAIPort = chatAIPort;
    }

    public List<ChatMessage> execute(Long userId, Long chapterId, String message, String chapterContent) {
        if (chapterContent != null && !chapterContent.trim().isEmpty()) {
            chatConversationPort.saveChapterContext(userId, chapterId, chapterContent);
        }

        ChatMessage userMessage = new ChatMessage(userId, chapterId, message, true);
        chatConversationPort.saveMessage(userMessage);

        List<ChatMessage> conversationHistory = chatConversationPort.getConversation(userId, chapterId);

        String context = chatConversationPort.getChapterContext(userId, chapterId);
        String limitedContext = limitContext(context);

        List<String> historyForPrompt = buildConversationHistory(conversationHistory);

        String assistantResponse = generateResponse(limitedContext, historyForPrompt, message);

        String[] segments = assistantResponse.split("---");
        List<ChatMessage> responseMessages = new ArrayList<>();
        for (String segment : segments) {
            String cleanSegment = segment.trim();
            if (!cleanSegment.isEmpty() && !cleanSegment.equals("---")) {
                ChatMessage assistantMessage = new ChatMessage(userId, chapterId, cleanSegment, false);
                chatConversationPort.saveMessage(assistantMessage);
                responseMessages.add(assistantMessage);
            }
        }

        return responseMessages;
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
        if (conversation == null || conversation.isEmpty()) {
            return List.of();
        }

        int endIndex = Math.max(0, conversation.size() - 1);

        return conversation.subList(0, endIndex).stream()
            .sorted(Comparator.comparing(ChatMessage::getTimestamp))
            .limit(MAX_HISTORY_MESSAGES)
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
            Si el mensaje del usuario requiere ideas, sugerencias o desarrollo narrativo, utiliza el contenido del capítulo y el historial de mensajes como referencia.
            Si el mensaje del usuario es una despedida, saludo, agradecimiento o indica que no necesita más ideas, responde SOLO con una frase breve y NO continúes la historia ni generes contenido adicional.
            Si tu respuesta tiene más de una idea o párrafo, SEPARA cada parte usando exactamente tres guiones (---) en una línea aparte. No uses ningún otro separador.
            """.formatted(
                context.isEmpty() ? "(Sin contexto aún)" : context,
                conversationHistoryText.isEmpty() ? "(No hay historial previo)" : conversationHistoryText,
                userMessage
            );

        return chatAIPort.generateResponse(fullPrompt, context, historyForPrompt);
    }
}
