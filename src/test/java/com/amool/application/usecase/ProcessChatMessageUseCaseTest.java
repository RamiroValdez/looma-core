package com.amool.application.usecase;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import com.amool.application.port.out.ChatAIPort;
import com.amool.application.port.out.ChatConversationPort;
import com.amool.application.usecases.ProcessChatMessageUseCase;
import com.amool.domain.model.ChatMessage;

public class ProcessChatMessageUseCaseTest {

    private ProcessChatMessageUseCase processChatMessageUseCase;
    private ChatConversationPort chatConversationPort;
    private ChatAIPort chatAIPort;

    private static final Long USER_ID = 1L;
    private static final Long CHAPTER_ID = 100L;
    private static final String USER_MESSAGE = "¿Cómo puedo mejorar este diálogo?";
    private static final String CHAPTER_CONTENT = "Contenido del capítulo de prueba";
    private static final String AI_RESPONSE = "Aquí tienes algunos consejos para mejorar el diálogo...";

    @BeforeEach
    public void setUp() {
        chatConversationPort = Mockito.mock(ChatConversationPort.class);
        chatAIPort = Mockito.mock(ChatAIPort.class);
        processChatMessageUseCase = new ProcessChatMessageUseCase(chatConversationPort, chatAIPort);
    }

    // ==================== Tests de Gestión de Contexto ====================

    @Test
    public void when_ExecuteWithChapterContent_ThenSaveChapterContext() {
        // Given: Una conversación sin historial y con contenido de capítulo
        givenEmptyConversation();
        givenValidChapterContent();

        // When: Se procesa un mensaje con contenido de capítulo
        whenExecuteWithChapterContent();

        // Then: El contexto del capítulo debe guardarse
        thenChapterContextShouldBeSaved();
    }

    @Test
    public void when_ExecuteWithNullChapterContent_ThenDoNotSaveChapterContext() {
        // Given: Una conversación sin historial
        givenEmptyConversation();

        // When: Se procesa un mensaje sin contenido de capítulo
        whenExecuteWithoutChapterContent();

        // Then: El contexto del capítulo no debe guardarse
        thenChapterContextShouldNotBeSaved();
    }

    @Test
    public void when_ExecuteWithEmptyChapterContent_ThenDoNotSaveChapterContext() {
        // Given: Una conversación sin historial
        givenEmptyConversation();

        // When: Se procesa un mensaje con contenido vacío
        whenExecuteWithEmptyChapterContent();

        // Then: El contexto del capítulo no debe guardarse
        thenChapterContextShouldNotBeSaved();
    }

    // ==================== Tests de Gestión de Mensajes ====================

    @Test
    public void when_Execute_ThenSaveUserMessage() {
        // Given: Una conversación sin historial
        givenEmptyConversation();
        givenValidChapterContent();

        // When: Se procesa un mensaje del usuario
        whenExecuteWithChapterContent();

        // Then: El mensaje del usuario debe guardarse correctamente
        thenUserMessageShouldBeSaved();
    }

    @Test
    public void when_Execute_ThenSaveAssistantMessage() {
        // Given: Una conversación sin historial
        givenEmptyConversation();
        givenValidChapterContent();

        // When: Se procesa un mensaje y se genera una respuesta
        ChatMessage result = whenExecuteWithChapterContent();

        // Then: La respuesta del asistente debe guardarse
        thenAssistantMessageShouldBeSaved(result);
    }

    @Test
    public void when_Execute_ThenReturnAssistantMessage() {
        // Given: Una conversación sin historial
        givenEmptyConversation();
        givenValidChapterContent();

        // When: Se procesa un mensaje
        ChatMessage result = whenExecuteWithChapterContent();

        // Then: Debe retornar el mensaje del asistente
        thenShouldReturnAssistantMessage(result);
    }

    // ==================== Tests de Gestión de Historial ====================

    @Test
    public void when_ConversationHistoryExists_ThenIncludeInPrompt() {
        // Given: Una conversación con historial previo de 4 mensajes
        givenConversationWithHistory(4);
        givenValidChapterContent();

        // When: Se procesa un nuevo mensaje
        whenExecuteWithChapterContent();

        // Then: El historial debe incluirse en el prompt (3 mensajes, excluyendo el último)
        thenHistoryShouldBeIncludedInPrompt(3);
    }

    @Test
    public void when_NoConversationHistory_ThenGenerateWithEmptyHistory() {
        // Given: Una conversación sin historial
        givenEmptyConversation();
        givenValidChapterContent();

        // When: Se procesa un mensaje
        whenExecuteWithChapterContent();

        // Then: El historial debe estar vacío
        thenHistoryShouldBeEmpty();
    }

    @Test
    public void when_HistoryExceedsMaxMessages_ThenLimitToLast5Messages() {
        // Given: Una conversación con más de 5 mensajes en el historial
        givenConversationWithHistory(8);

        // When: Se procesa un nuevo mensaje
        whenExecuteWithoutChapterContent();

        // Then: El historial debe limitarse a máximo 5 mensajes
        thenHistoryShouldBeLimitedToMaxMessages(5);
    }

    // ==================== Tests de Gestión de Contexto del Capítulo ====================

    @Test
    public void when_ContextExceedsMaxLength_ThenLimitContext() {
        // Given: Un contexto que excede el límite máximo
        givenEmptyConversation();
        givenLongChapterContext(5000);

        // When: Se procesa un mensaje
        whenExecuteWithoutChapterContent();

        // Then: El contexto debe limitarse a 4000 caracteres
        thenContextShouldBeLimited(4000);
    }

    @Test
    public void when_ContextIsNull_ThenUseEmptyContext() {
        // Given: Un contexto null
        givenEmptyConversation();
        givenNullChapterContext();

        // When: Se procesa un mensaje
        whenExecuteWithoutChapterContent();

        // Then: Debe usarse un contexto vacío
        thenContextShouldBeEmpty();
    }

    // ==================== Tests de Generación de Prompts ====================

    @Test
    public void when_GenerateResponse_ThenPromptContainsAllElements() {
        // Given: Una conversación configurada con contexto
        givenEmptyConversation();
        givenValidChapterContent();

        // When: Se genera una respuesta
        whenExecuteWithChapterContent();

        // Then: El prompt debe contener todos los elementos requeridos
        thenPromptShouldContainAllElements();
    }

    // ==================== Tests de Interacción con Ports ====================

    @Test
    public void when_GetConversation_ThenRetrieveFromPort() {
        // Given: Una conversación sin historial
        givenEmptyConversation();

        // When: Se ejecuta el caso de uso
        whenExecuteWithoutChapterContent();

        // Then: Debe recuperarse la conversación del port
        thenConversationShouldBeRetrieved();
    }

    @Test
    public void when_GetChapterContext_ThenRetrieveFromPort() {
        // Given: Una conversación con contexto
        givenEmptyConversation();
        givenValidChapterContent();

        // When: Se ejecuta el caso de uso
        whenExecuteWithoutChapterContent();

        // Then: Debe recuperarse el contexto del port
        thenChapterContextShouldBeRetrieved();
    }

    // ==================== Given Methods ====================

    private void givenEmptyConversation() {
        when(chatConversationPort.getConversation(USER_ID, CHAPTER_ID)).thenReturn(List.of());
        when(chatConversationPort.getChapterContext(USER_ID, CHAPTER_ID)).thenReturn("");
        when(chatAIPort.generateResponse(anyString(), anyString(), anyList())).thenReturn(AI_RESPONSE);
    }

    private void givenValidChapterContent() {
        when(chatConversationPort.getChapterContext(USER_ID, CHAPTER_ID)).thenReturn(CHAPTER_CONTENT);
    }

    private void givenNullChapterContext() {
        when(chatConversationPort.getChapterContext(USER_ID, CHAPTER_ID)).thenReturn(null);
    }

    private void givenLongChapterContext(int length) {
        String longContext = "A".repeat(length);
        when(chatConversationPort.getChapterContext(USER_ID, CHAPTER_ID)).thenReturn(longContext);
    }

    private void givenConversationWithHistory(int numberOfMessages) {
        List<ChatMessage> history = new ArrayList<>();
        for (int i = 0; i < numberOfMessages; i++) {
            boolean isUserMessage = i % 2 == 0;
            String content = isUserMessage ? "Mensaje " + i : "Respuesta " + i;
            history.add(createChatMessage(USER_ID, CHAPTER_ID, content, isUserMessage,
                LocalDateTime.now().minusMinutes(numberOfMessages - i)));
        }
        when(chatConversationPort.getConversation(USER_ID, CHAPTER_ID)).thenReturn(history);
        when(chatAIPort.generateResponse(anyString(), anyString(), anyList())).thenReturn(AI_RESPONSE);
    }

    // ==================== When Methods ====================

    private ChatMessage whenExecuteWithChapterContent() {
        return processChatMessageUseCase.execute(USER_ID, CHAPTER_ID, USER_MESSAGE, CHAPTER_CONTENT);
    }

    private ChatMessage whenExecuteWithoutChapterContent() {
        return processChatMessageUseCase.execute(USER_ID, CHAPTER_ID, USER_MESSAGE, null);
    }

    private ChatMessage whenExecuteWithEmptyChapterContent() {
        return processChatMessageUseCase.execute(USER_ID, CHAPTER_ID, USER_MESSAGE, "   ");
    }

    // ==================== Then Methods ====================

    private void thenChapterContextShouldBeSaved() {
        verify(chatConversationPort, times(1)).saveChapterContext(USER_ID, CHAPTER_ID, CHAPTER_CONTENT);
    }

    private void thenChapterContextShouldNotBeSaved() {
        verify(chatConversationPort, never()).saveChapterContext(eq(USER_ID), eq(CHAPTER_ID), anyString());
    }

    private void thenUserMessageShouldBeSaved() {
        ArgumentCaptor<ChatMessage> messageCaptor = ArgumentCaptor.forClass(ChatMessage.class);
        verify(chatConversationPort, times(2)).saveMessage(messageCaptor.capture());

        ChatMessage userMsg = messageCaptor.getAllValues().getFirst();
        assertEquals(USER_ID, userMsg.getUserId());
        assertEquals(CHAPTER_ID, userMsg.getChapterId());
        assertEquals(USER_MESSAGE, userMsg.getContent());
        assertTrue(userMsg.isUserMessage());
        assertNotNull(userMsg.getTimestamp());
    }

    private void thenAssistantMessageShouldBeSaved(ChatMessage result) {
        ArgumentCaptor<ChatMessage> messageCaptor = ArgumentCaptor.forClass(ChatMessage.class);
        verify(chatConversationPort, times(2)).saveMessage(messageCaptor.capture());

        ChatMessage assistantMsg = messageCaptor.getAllValues().get(1);
        assertEquals(USER_ID, assistantMsg.getUserId());
        assertEquals(CHAPTER_ID, assistantMsg.getChapterId());
        assertEquals(AI_RESPONSE, assistantMsg.getContent());
        assertFalse(assistantMsg.isUserMessage());
        assertNotNull(assistantMsg.getTimestamp());
        assertEquals(assistantMsg, result);
    }

    private void thenShouldReturnAssistantMessage(ChatMessage result) {
        assertNotNull(result);
        assertEquals(AI_RESPONSE, result.getContent());
        assertFalse(result.isUserMessage());
    }

    private void thenHistoryShouldBeIncludedInPrompt(int expectedSize) {
        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<String>> historyCaptor = ArgumentCaptor.forClass(List.class);
        verify(chatAIPort).generateResponse(anyString(), anyString(), historyCaptor.capture());

        List<String> capturedHistory = historyCaptor.getValue();
        assertEquals(expectedSize, capturedHistory.size());
        assertTrue(capturedHistory.get(0).contains("Usuario:") || capturedHistory.get(0).contains("Asistente:"));
    }

    private void thenHistoryShouldBeEmpty() {
        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<String>> historyCaptor = ArgumentCaptor.forClass(List.class);
        verify(chatAIPort).generateResponse(anyString(), anyString(), historyCaptor.capture());

        assertTrue(historyCaptor.getValue().isEmpty());
    }

    private void thenHistoryShouldBeLimitedToMaxMessages(int maxMessages) {
        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<String>> historyCaptor = ArgumentCaptor.forClass(List.class);
        verify(chatAIPort).generateResponse(anyString(), anyString(), historyCaptor.capture());

        assertTrue(historyCaptor.getValue().size() <= maxMessages);
    }

    private void thenContextShouldBeLimited(int maxLength) {
        ArgumentCaptor<String> contextCaptor = ArgumentCaptor.forClass(String.class);
        verify(chatAIPort).generateResponse(anyString(), contextCaptor.capture(), anyList());

        String capturedContext = contextCaptor.getValue();
        assertTrue(capturedContext.length() <= maxLength + 3); // +3 por "..."
        assertTrue(capturedContext.endsWith("..."));
    }

    private void thenContextShouldBeEmpty() {
        ArgumentCaptor<String> contextCaptor = ArgumentCaptor.forClass(String.class);
        verify(chatAIPort).generateResponse(anyString(), contextCaptor.capture(), anyList());

        assertEquals("", contextCaptor.getValue());
    }

    private void thenPromptShouldContainAllElements() {
        ArgumentCaptor<String> promptCaptor = ArgumentCaptor.forClass(String.class);
        verify(chatAIPort).generateResponse(promptCaptor.capture(), anyString(), anyList());

        String capturedPrompt = promptCaptor.getValue();
        assertTrue(capturedPrompt.contains("asistente de escritura creativa"));
        assertTrue(capturedPrompt.contains("CONTEXTO DEL CAPÍTULO ACTUAL"));
        assertTrue(capturedPrompt.contains("HISTORIAL DE LA CONVERSACIÓN"));
        assertTrue(capturedPrompt.contains("MENSAJE ACTUAL DEL USUARIO"));
        assertTrue(capturedPrompt.contains(USER_MESSAGE));
    }

    private void thenConversationShouldBeRetrieved() {
        verify(chatConversationPort, times(1)).getConversation(USER_ID, CHAPTER_ID);
    }

    private void thenChapterContextShouldBeRetrieved() {
        verify(chatConversationPort, times(1)).getChapterContext(USER_ID, CHAPTER_ID);
    }

    // ==================== Helper Methods ====================

    private ChatMessage createChatMessage(Long userId, Long chapterId, String content,
                                         boolean isUserMessage, LocalDateTime timestamp) {
        return new ChatMessage(userId, chapterId, content, isUserMessage, timestamp);
    }
}
