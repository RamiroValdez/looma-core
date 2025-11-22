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


    @Test
    public void when_ExecuteWithChapterContent_ThenSaveChapterContext() {
        givenEmptyConversation();
        givenValidChapterContent();

        whenExecuteWithChapterContent();

        thenChapterContextShouldBeSaved();
    }

    @Test
    public void when_ExecuteWithNullChapterContent_ThenDoNotSaveChapterContext() {
        givenEmptyConversation();

        whenExecuteWithoutChapterContent();

        thenChapterContextShouldNotBeSaved();
    }

    @Test
    public void when_ExecuteWithEmptyChapterContent_ThenDoNotSaveChapterContext() {
        givenEmptyConversation();

        whenExecuteWithEmptyChapterContent();

        thenChapterContextShouldNotBeSaved();
    }


    @Test
    public void when_Execute_ThenSaveUserMessage() {
        givenEmptyConversation();
        givenValidChapterContent();

        whenExecuteWithChapterContent();

        thenUserMessageShouldBeSaved();
    }

    @Test
    public void when_Execute_ThenSaveAssistantMessage() {
        givenEmptyConversation();
        givenValidChapterContent();

        List<ChatMessage> result = whenExecuteWithChapterContent();

        thenAssistantMessageShouldBeSaved(result);
    }

    @Test
    public void when_Execute_ThenReturnAssistantMessage() {
        givenEmptyConversation();
        givenValidChapterContent();

        List<ChatMessage> result = whenExecuteWithChapterContent();

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertFalse(result.get(0).isUserMessage());
    }


    @Test
    public void when_ConversationHistoryExists_ThenIncludeInPrompt() {
        givenConversationWithHistory(4);
        givenValidChapterContent();

        whenExecuteWithChapterContent();

        thenHistoryShouldBeIncludedInPrompt(3);
    }

    @Test
    public void when_NoConversationHistory_ThenGenerateWithEmptyHistory() {
        givenEmptyConversation();
        givenValidChapterContent();

        whenExecuteWithChapterContent();

        thenHistoryShouldBeEmpty();
    }

    @Test
    public void when_HistoryExceedsMaxMessages_ThenLimitToLast5Messages() {
        givenConversationWithHistory(8);

        whenExecuteWithoutChapterContent();

        thenHistoryShouldBeLimitedToMaxMessages(5);
    }


    @Test
    public void when_ContextExceedsMaxLength_ThenLimitContext() {
        givenEmptyConversation();
        givenLongChapterContext(5000);

        whenExecuteWithoutChapterContent();

        thenContextShouldBeLimited(4000);
    }

    @Test
    public void when_ContextIsNull_ThenUseEmptyContext() {
        givenEmptyConversation();
        givenNullChapterContext();

        whenExecuteWithoutChapterContent();

        thenContextShouldBeEmpty();
    }


    @Test
    public void when_GenerateResponse_ThenPromptContainsAllElements() {
        givenEmptyConversation();
        givenValidChapterContent();

        whenExecuteWithChapterContent();

        thenPromptShouldContainAllElements();
    }


    @Test
    public void when_GetConversation_ThenRetrieveFromPort() {
        givenEmptyConversation();

        whenExecuteWithoutChapterContent();

        thenConversationShouldBeRetrieved();
    }

    @Test
    public void when_GetChapterContext_ThenRetrieveFromPort() {
        givenEmptyConversation();
        givenValidChapterContent();

        whenExecuteWithoutChapterContent();

        thenChapterContextShouldBeRetrieved();
    }


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


    private List<ChatMessage> whenExecuteWithChapterContent() {
        return processChatMessageUseCase.execute(USER_ID, CHAPTER_ID, USER_MESSAGE, CHAPTER_CONTENT);
    }

    private List<ChatMessage> whenExecuteWithoutChapterContent() {
        return processChatMessageUseCase.execute(USER_ID, CHAPTER_ID, USER_MESSAGE, null);
    }

    private List<ChatMessage> whenExecuteWithEmptyChapterContent() {
        return processChatMessageUseCase.execute(USER_ID, CHAPTER_ID, USER_MESSAGE, "   ");
    }


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

    private void thenAssistantMessageShouldBeSaved(List<ChatMessage> result) {
        ArgumentCaptor<ChatMessage> messageCaptor = ArgumentCaptor.forClass(ChatMessage.class);
        verify(chatConversationPort, times(2)).saveMessage(messageCaptor.capture());

        ChatMessage assistantMsg = messageCaptor.getAllValues().get(1);
        assertEquals(USER_ID, assistantMsg.getUserId());
        assertEquals(CHAPTER_ID, assistantMsg.getChapterId());
        assertEquals(AI_RESPONSE, assistantMsg.getContent());
        assertFalse(assistantMsg.isUserMessage());
        assertNotNull(assistantMsg.getTimestamp());
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(assistantMsg.getContent(), result.get(0).getContent());
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
        assertTrue(capturedContext.length() <= maxLength + 3);
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


    private ChatMessage createChatMessage(Long userId, Long chapterId, String content,
                                         boolean isUserMessage, LocalDateTime timestamp) {
        return new ChatMessage(userId, chapterId, content, isUserMessage, timestamp);
    }
}
