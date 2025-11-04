package com.amool.application.service;

import com.amool.domain.model.ChatMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.springframework.ai.chat.client.ChatClient;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
class ChatServiceTest {

    @Mock
    ChatClient.Builder chatClientBuilder;

    ChatClient mockChatClient;

    ChatService chatService;

    @BeforeEach
    void setUp() {
        mockChatClient = mock(ChatClient.class, RETURNS_DEEP_STUBS);
        when(chatClientBuilder.build()).thenReturn(mockChatClient);

        chatService = new ChatService(chatClientBuilder);
    }

    @Test
    void processMessage_storesUserAndAssistantMessagesAndReturnsAssistantMessage() {
        when(mockChatClient.prompt().user(anyString()).call().content())
            .thenReturn("Respuesta simulada");

        Long userId = 1L;
        Long chapterId = 10L;
        ChatMessage assistant = chatService.processMessage(userId, chapterId, "Hola", "Contenido del capítulo");

        assertNotNull(assistant);
        assertFalse(assistant.isUserMessage());
        assertEquals("Respuesta simulada", assistant.getContent());

        List<ChatMessage> convo = chatService.getConversation(userId, chapterId);
        assertEquals(2, convo.size());
        assertTrue(convo.get(0).isUserMessage());
        assertFalse(convo.get(1).isUserMessage());
        assertEquals("Hola", convo.get(0).getContent());
        assertEquals("Respuesta simulada", convo.get(1).getContent());

        verify(mockChatClient, atLeastOnce()).prompt();
        verify(mockChatClient.prompt(), atLeastOnce()).user(anyString());
        verify(mockChatClient.prompt().user(anyString()), atLeastOnce()).call();
    }

    @Test
    void processMessage_whenChatClientThrows_returnsErrorMessage() {
        when(mockChatClient.prompt().user(anyString()).call())
            .thenThrow(new RuntimeException("boom"));

        Long userId = 2L;
        Long chapterId = 20L;
        ChatMessage assistant = chatService.processMessage(userId, chapterId, "Test error", null);

        assertNotNull(assistant);
        assertFalse(assistant.isUserMessage());
        assertTrue(assistant.getContent().startsWith("Lo siento, hubo un error"));
    }

    @Test
    void conversation_trimsToMaxMessages() {
        when(mockChatClient.prompt().user(anyString()).call().content())
            .thenReturn("Respuesta corta");

        Long userId = 3L;
        Long chapterId = 30L;
        int attempts = 25;
        for (int i = 0; i < attempts; i++) {
            chatService.processMessage(userId, chapterId, "Msg " + i, null);
        }

        List<ChatMessage> convo = chatService.getConversation(userId, chapterId);
        assertTrue(convo.size() <= 20);
    }
}
